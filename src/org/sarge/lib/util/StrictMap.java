package org.sarge.lib.util;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Strict implementation that prevents <tt>null</tt> or duplicate keys.
 * @author Sarge
 * @param <K> Key type
 * @param <V> Value type
 */
public class StrictMap<K, V> extends AbstractMap<K, V> {
	private final Map<K, V> map;

	public StrictMap() {
		this.map = new HashMap<>();
	}

	public StrictMap( Map<K, V> map ) {
		this.map = map;
	}

	@Override
	public Set<java.util.Map.Entry<K, V>> entrySet() {
		return map.entrySet();
	}

	@Override
	public V put( K key, V value ) {
		Check.notNull( key );
		if( map.containsKey( key ) ) throw new IllegalArgumentException( "Duplicate key: " + key );
		return map.put( key, value );
	}

	@Override
	public V remove( Object key ) {
		Check.notNull( key );
		if( !map.containsKey( key ) ) throw new IllegalArgumentException( "Unknown key: " + key );
		return super.remove( key );
	}
}
