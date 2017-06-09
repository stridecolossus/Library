package org.sarge.lib.util;

import java.util.HashMap;
import java.util.Map;

/**
 * Convenience builder for a map.
 * @author Sarge
 */
public class MapBuilder<K, V> {
	/**
	 * Creates a map from the given array of key-value pairs.
	 * @param entries Key-value pairs
	 * @return Map
	 */
	@SuppressWarnings("unchecked")
	public static <K, V> Map<K, V> build(Object... entries) {
		// Verify number of elements
		if((entries.length % 2) != 0) throw new IllegalArgumentException("Expected even number of elements");
		
		// Build map
		final Map<K, V> map = new HashMap<K, V>();
		for(int n = 0; n < entries.length; n += 2) {
			final K key = (K) entries[n];
			final V value = (V) entries[n + 1];
			map.put(key, value);
		}
		
		return map;
	}
	
	private final Map<K, V> map = new HashMap<>();
	
	/**
	 * Adds an entry.
	 * @param key		Key
	 * @param value		Value
	 * @return This builder
	 */
	public MapBuilder<K, V> add(K key, V value) {
		map.put(key, value);
		return this;
	}

	/**
	 * @return New map
	 */
	public Map<K, V> build() {
		return new HashMap<>(map);
	}
	
	@Override
	public String toString() {
		return map.toString();
	}
}
