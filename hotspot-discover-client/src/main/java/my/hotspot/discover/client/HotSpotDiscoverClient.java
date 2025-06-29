package my.hotspot.discover.client;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

/**
 * @author cuiyulong <cuiyulong@kuaishou.com>
 * Created on 2025-06-29
 */
public interface HotSpotDiscoverClient {

    void collect(String bizName, String key, int count);

    default void collect(String bizName, String key) {
        collect(bizName, key, 1);
    }

    Map<String, Boolean> isHotSpot(String bizName, Collection<String> keys);

    default boolean isHotSpot(String bizName, String key) {
        final Map<String, Boolean> resultMap = isHotSpot(bizName, Collections.singleton(key));
        if (resultMap == null || !resultMap.containsKey(key)) {
            return false;
        }
        return resultMap.get(key);
    }
}
