/* ----------------------------------------------------------------------------
 * Copyright (C) 2021      European Space Agency
 *                         European Space Operations Centre
 *                         Darmstadt
 *                         Germany
 * ----------------------------------------------------------------------------
 * System                : ESA NanoSat MO Framework
 * ----------------------------------------------------------------------------
 * Licensed under European Space Agency Public License (ESA-PL) Weak Copyleft – v2.4
 * You may not use this file except in compliance with the License.
 *
 * Except as expressly set forth in this License, the Software is provided to
 * You on an "as is" basis and without warranties of any kind, including without
 * limitation merchantability, fitness for a particular purpose, absence of
 * defects or errors, accuracy or non-infringement of intellectual property rights.
 *
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ----------------------------------------------------------------------------
 */
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
