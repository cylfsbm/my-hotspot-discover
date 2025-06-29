package my.hotspot.discover.client.impl;

import static my.hotspot.discover.client.config.ClientConfigKey.COMMON_COLLECT_COUNT_THRESHOLD;
import static my.hotspot.discover.client.config.ClientConfigKey.COMMON_COLLECT_TOP_N;
import static my.hotspot.discover.client.config.ClientConfigKey.COMMON_MAX_BUFFER_COUNT;
import static my.hotspot.discover.client.config.ClientConfigKey.COMMON_MAX_BUFFER_INTERVAL_MS;
import static my.hotspot.discover.client.config.ClientConfigKey.COMMON_REDIS_SERVER_HOST;
import static my.hotspot.discover.client.config.ClientConfigKey.COMMON_REDIS_SERVER_PORT;
import static my.hotspot.discover.client.config.ClientConfigKey.COMMON_TOPIC;
import static my.hotspot.discover.client.utils.ClientConfigUtils.getInt;
import static my.hotspot.discover.client.utils.ClientConfigUtils.getLong;
import static my.hotspot.discover.client.utils.ClientConfigUtils.getString;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.LongAdder;
import java.util.stream.Collectors;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

import com.github.phantomthief.collection.BufferTrigger;

import my.hotspot.discover.base.cache.RedisClient;
import my.hotspot.discover.base.model.KafkaData;
import my.hotspot.discover.base.utils.ObjectMapperUtils;
import my.hotspot.discover.client.HotSpotDiscoverClient;
import my.hotspot.discover.client.model.CollectorData;
import redis.clients.jedis.JedisPoolConfig;

/**
 * @author cuiyulong <cuiyulong@kuaishou.com>
 * Created on 2025-06-29
 */
public class HotSpotDiscoverCommonClient implements HotSpotDiscoverClient {

    private final Map<String, HotSpotDiscoverCollector> collectorContainer;
    private final Producer<String, String> kafkaProducer;
    private final RedisClient redisClient;

    public HotSpotDiscoverCommonClient() throws IOException {
        this.collectorContainer = new ConcurrentHashMap<>();
        this.kafkaProducer = new KafkaProducer<>(new Properties());
        final JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        redisClient =
                new RedisClient(jedisPoolConfig, getString(COMMON_REDIS_SERVER_HOST), getInt(COMMON_REDIS_SERVER_PORT));
    }

    @Override
    public void collect(String bizName, String key, int count) {
        final HotSpotDiscoverCollector collector =
                collectorContainer.computeIfAbsent(bizName, HotSpotDiscoverCollector::new);
        collector.collect(key, count);
    }

    @Override
    public Map<String, Boolean> isHotSpot(String bizName, Collection<String> keys) {
        return Map.of();
    }

    private class HotSpotDiscoverCollector {

        private final String bizName;
        private final BufferTrigger<CollectorData> bufferTrigger;

        private HotSpotDiscoverCollector(String bizName) {
            this.bizName = bizName;
            this.bufferTrigger = BufferTrigger.<CollectorData, Map<String, LongAdder>> simple()
                    .maxBufferCount(getLong(COMMON_MAX_BUFFER_COUNT))
                    .interval(getLong(COMMON_MAX_BUFFER_INTERVAL_MS), TimeUnit.MILLISECONDS)
                    .name("hotSpotDiscoverCollector-" + bizName)
                    .consumer(this::doConsume)
                    .setContainer(ConcurrentHashMap::new, (container, data) -> {
                        container.computeIfAbsent(data.getKey(), (key) -> new LongAdder())
                                .add(data.getCount());
                        return true;
                    }).build();
        }

        private void doConsume(Map<String, LongAdder> container) {
            if (container == null || container.isEmpty()) {
                return;
            }
            final Map<String, Long> collectData = container.entrySet()
                    .stream()
                    .filter(entry -> entry.getValue().longValue() >= getCollectCountThreshold())
                    .sorted(Collections.reverseOrder(Comparator.comparingLong((entry) -> entry.getValue().longValue())))
                    .limit(getCollectTopN())
                    .collect(Collectors.toMap(Entry::getKey, entry -> entry.getValue().longValue()));
            if (collectData.isEmpty()) {
                return;
            }
            final KafkaData kafkaData = new KafkaData(bizName, collectData);
            kafkaProducer.send(new ProducerRecord<>(getKafkaTopic(), ObjectMapperUtils.toJSON(kafkaData)));
        }

        public long getCollectCountThreshold() {
            return getLong(COMMON_COLLECT_COUNT_THRESHOLD);
        }

        public long getCollectTopN() {
            return getLong(COMMON_COLLECT_TOP_N);
        }

        public String getKafkaTopic() {
            return getString(COMMON_TOPIC);
        }

        public void collect(String key, int count) {
            bufferTrigger.enqueue(new CollectorData(key, count));
        }
    }
}
