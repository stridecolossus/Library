package org.sarge.lib.util;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Map of lists.
 * @author Sarge
 * @param <K> Key type
 * @param <V> Value type
 */
public class ListMap<K, V> extends AbstractMap<K, List<V>> {
	private final Map<K, List<V>> map = new HashMap<>();

	@Override
	public Set<Entry<K, List<V>>> entrySet() {
		return map.entrySet();
	}

	/**
	 * Tests whether the given value is present in the specified list.
	 * @param key		List key
	 * @param value		Value
	 * @return Whether this list-map contains the given value
	 */
	public boolean contains( K key, V value ) {
		final List<V> list = map.get( key );
		if( list == null ) return false;
		return list.contains( value );
	}

	/**
	 * Adds a value to the specified list.
	 * @param key		List key
	 * @param value		Value to add
	 */
	public void add( K key, V value ) {
		// Lookup list
		List<V> list = map.get( key );

		// Create as required
		if( list == null ) {
			list = new ArrayList<>();
			map.put( key, list );
		}

		// Append value
		list.add( value );
	}

	/**
	 * Removes a value from the specified list.
	 * @param key		List key
	 * @param value		Value to remove
	 * @throws IllegalArgumentException if the list does not exist or the value is not present in the map
	 */
	public void removeElement( K key, V value ) {
		// Lookup list
		final List<V> list = map.get( key );
		if( list == null ) throw new IllegalArgumentException( "List not found: " + key );

		// Remove value from list
		final boolean removed = list.remove( value );
		if( !removed ) throw new IllegalArgumentException( "Value not present: key=" + key + " value=" + value );
	}
	
	@Override
	public void clear() {
		map.clear();
	}
}
