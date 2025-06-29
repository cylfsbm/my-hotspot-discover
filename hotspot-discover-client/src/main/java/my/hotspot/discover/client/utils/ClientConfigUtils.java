package my.hotspot.discover.client.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import my.hotspot.discover.client.config.ClientConfigKey;

/**
 * @author cuiyulong <cuiyulong@kuaishou.com>
 * Created on 2025-06-29
 */
public class ClientConfigUtils {

    private static final String FILE_NAME = "client-config.properties";

    private static volatile Properties CLIENT_CONFIG_PROPERTIES = null;

    static {
        try {
            init();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private static void init() throws IOException {
        if (CLIENT_CONFIG_PROPERTIES == null) {
            synchronized (ClientConfigUtils.class) {
                if (CLIENT_CONFIG_PROPERTIES == null) {
                    Properties properties = new Properties();
                    final InputStream inputStream =
                            Thread.currentThread().getContextClassLoader().getResourceAsStream(FILE_NAME);
                    properties.load(inputStream);
                    CLIENT_CONFIG_PROPERTIES = properties;
                }
            }
        }
    }

    public static long getLong(ClientConfigKey configKey) {
        return Long.parseLong(CLIENT_CONFIG_PROPERTIES.getProperty(configKey.getPropertyKey()));
    }

    public static int getInt(ClientConfigKey configKey) {
        return Integer.parseInt(CLIENT_CONFIG_PROPERTIES.getProperty(configKey.getPropertyKey()));
    }

    public static String getString(ClientConfigKey configKey) {
        return CLIENT_CONFIG_PROPERTIES.getProperty(configKey.getPropertyKey());
    }
}
