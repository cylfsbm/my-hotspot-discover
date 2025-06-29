package my.hotspot.discover.base.model;

import java.util.Map;

/**
 * @author cuiyulong <cuiyulong@kuaishou.com>
 * Created on 2025-06-29
 */
public class KafkaData {

    private final String bizName;
    private final Map<String, Long> countMap;

    public KafkaData(String bizName, Map<String, Long> countMap) {
        this.bizName = bizName;
        this.countMap = countMap;
    }
}
