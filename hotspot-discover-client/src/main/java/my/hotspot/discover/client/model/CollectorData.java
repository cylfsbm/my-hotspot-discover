package my.hotspot.discover.client.model;

/**
 * @author cuiyulong <cuiyulong@kuaishou.com>
 * Created on 2025-06-29
 */
public class CollectorData {

    private final String key;
    private final int count;

    public CollectorData(String key, int count) {
        this.key = key;
        this.count = count;
    }

    public int getCount() {
        return count;
    }

    public String getKey() {
        return key;
    }
}
