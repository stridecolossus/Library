package org.sarge.lib.collection;

import java.util.HashMap;
import java.util.Map;

/**
 * Bi-directional map that also supports key lookup by values.
 * @author chris
 */
public interface InverseMap<K, V> extends Map<K, V> {
    /**
     * Retrieves the key associated with the given value.
     * @param value Value
     * @return Associated key or <tt>null</tt> if not present
     */
    K key(V value);

    /**
     * @return The inverse view of this map
     */
    Map<V, K> inverse();
    
    /**
     * Hash-map-based implementation.
     * @param <K> Key-type
     * @param <V> Value-type
     */
    class InverseHashMap<K, V> extends HashMap<K, V> implements InverseMap<K, V> {
        private final Map<V, K> inverse = new HashMap<>();
        
        /**
         * Default constructor for an empty map.
         */
        public InverseHashMap() {
            super();
        }
        
        /**
         * Copy constructor.
         * @param map Map to copy
         */
        public InverseHashMap(Map<K, V> map) {
            putAll(map);
        }
        
        @Override
        public V put(K key, V value) {
            inverse.put(value, key);
            return super.put(key, value);
        }
        
        @Override
        public void putAll(Map<? extends K, ? extends V> map) {
            super.putAll(map);
            map.entrySet().forEach(entry -> inverse.put(entry.getValue(), entry.getKey()));
        }
        
        @Override
        public V remove(Object key) {
            inverse.remove(super.get(key));
            return super.remove(key);
        }
        
        @Override
        public K key(V value) {
            return inverse.get(value);
        }
        
        @Override
        public Map<V, K> inverse() {
            return inverse;
        }
    }
}
