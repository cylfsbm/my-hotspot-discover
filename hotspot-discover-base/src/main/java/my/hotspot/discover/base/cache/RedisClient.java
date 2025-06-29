package my.hotspot.discover.base.cache;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * @author cuiyulong <cuiyulong@kuaishou.com>
 * Created on 2025-06-29
 */
public class RedisClient {

    private final JedisPool JEDIS_POOL;

    public RedisClient(JedisPoolConfig jedisPoolConfig, String host, int port) {
        JEDIS_POOL =
                new JedisPool(jedisPoolConfig, host, port);
    }

    private Jedis getJedisClient() {
        return JEDIS_POOL.getResource();
    }

    public void zincrby(String key, double score, String member) {
        getJedisClient().zincrby(key, score, member);
    }
}
