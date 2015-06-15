package org.sarge.lib.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Convenience builder for immutable maps.
 * @author Sarge
 */
public final class MapBuilder {
	private MapBuilder() {
	}

	/**
	 * Builds an immutable map.
	 * @param args Map entries grouped by pairs
	 * @return Immutable map
	 * @throws IllegalArgumentException if the entries are not balanced
	 * @throws ClassCastException if the entries are not valid
	 */
	@SuppressWarnings("unchecked")
	public static <K, V> Map<K, V> build( Object... args ) {
		Check.notNull( args );
		if( ( args.length % 2 ) != 0 ) throw new IllegalArgumentException( "Number of arguments is not even" );
		
		final Map<K, V> map = new HashMap<>();
		for( int n = 0; n < args.length; n += 2 ) {
			map.put( (K) args[n], (V) args[n + 1] );
		}
		
		return Collections.unmodifiableMap( map );
	}
}
