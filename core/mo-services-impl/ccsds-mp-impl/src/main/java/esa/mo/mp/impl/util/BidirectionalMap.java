package esa.mo.mp.impl.util;

import java.util.HashMap;

/**
 * BidirectionalMap allows one-to-one mapping with O(1) lookup
 */
public class BidirectionalMap<K, V> extends HashMap<K, V> {
    private HashMap<V, K> reverseMap = new HashMap<>();

    @Override
    public V put(K key, V value) {
        reverseMap.put(value, key);
        return super.put(key, value);
    }

    @Override
    public void clear() {
        reverseMap.clear();
        super.clear();
    }

    @Override
    public V remove(Object key) {
        reverseMap.remove(get(key));
        return super.remove(key);
    }

    /**
     * Returns the key to which the specified value is mapped, or null if this map contains no mapping for the value.
     * @param value - the value whose associated key is to be returned
     * @return the key to which the specified value is mapped, or null if this map contains no mapping for the value
     */
    public K getKey(V value) {
        return this.reverseMap.get(value);
    }
}
