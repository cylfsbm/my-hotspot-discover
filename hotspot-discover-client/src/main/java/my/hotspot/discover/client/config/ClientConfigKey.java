package my.hotspot.discover.client.config;

/**
 * @author cuiyulong <cuiyulong@kuaishou.com>
 * Created on 2025-06-29
 */
public enum ClientConfigKey {

    COMMON_TOPIC("hotspot.discover.client.common.topic"),
    COMMON_MAX_BUFFER_INTERVAL_MS("hotspot.discover.client.common.maxBufferIntervalMs"),
    COMMON_MAX_BUFFER_COUNT("hotspot.discover.client.common.maxBufferCount"),
    COMMON_COLLECT_COUNT_THRESHOLD("hotspot.discover.client.common.collectCountThreshold"),
    COMMON_COLLECT_TOP_N("hotspot.discover.client.common.collectTopN"),
    COMMON_REDIS_SERVER_HOST("hotspot.discover.client.common.redis.server.host"),
    COMMON_REDIS_SERVER_PORT("hotspot.discover.client.common.redis.server.port"),
    ;

    private final String propertyKey;

    ClientConfigKey(String propertyKey) {
        this.propertyKey = propertyKey;
    }

    public String getPropertyKey() {
        return propertyKey;
    }
}
