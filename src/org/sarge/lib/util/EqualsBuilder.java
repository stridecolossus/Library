package org.sarge.lib.util;

import java.lang.reflect.Field;
import java.util.List;

/**
 * Utilities for generating <tt>equals</tt> comparators using reflection.
 * @author Sarge
 */
public class EqualsBuilder {
	/**
	 * Tests equality using reflection.
	 * @param thisObject This object
	 * @param thatObject Other object
	 * @return Whether the two objects are equal
	 */
	public static boolean equals( Object thisObject, Object thatObject ) {
		// Check null
		if( thatObject == null ) return false;

		// Check self
		if( thisObject == thatObject ) return true;

		// Check same class
		final Class<?> clazz = thisObject.getClass();
		final Class<?> other = thatObject.getClass();
		if( !clazz.isAssignableFrom( other ) && !other.isAssignableFrom( clazz ) ) return false;

		// Compare fields
		final List<Field> fields = ReflectionUtils.getMemberFields( clazz );
		try {
			for( Field f : fields ) {
				final Object thisValue = f.get( thisObject );
				final Object thatValue = f.get( thatObject );
				if( thisValue == null ) {
					if( thatValue != null ) return false;
				}
				else {
					if( !thisValue.equals( thatValue ) ) return false;
				}
			}
		}
		catch( IllegalAccessException e ) {
			throw new RuntimeException( "Error acessing field", e );
		}

		// Objects are equal
		return true;
	}
}
