package my.hotspot.discover.test;

import static my.hotspot.discover.client.config.ClientConfigKey.COMMON_MAX_BUFFER_COUNT;

import org.junit.Test;

import my.hotspot.discover.client.utils.ClientConfigUtils;

/**
 * @author cuiyulong <cuiyulong@kuaishou.com>
 * Created on 2025-06-29
 */
public class ClientConfigUtilsTest {

    @Test
    public void testGetLong() {
        final long properties = ClientConfigUtils.getLong(COMMON_MAX_BUFFER_COUNT);
        System.out.println(properties);
    }
}
