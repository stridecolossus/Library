package org.sarge.lib.util;

import java.util.List;

/**
 * Generates hash-code functions using reflection.
 * @author Sarge
 */
public class HashCodeBuilder {
	private HashCodeBuilder() {
		// Utility class
	}

	/**
	 * Generates a hash-code for the given object using reflection.
	 * @param obj Object
	 * @return Hash-code
	 */
	public static int hashCode( Object obj ) {
		// Get class member values
		final List<Object> values = ReflectionUtils.getMemberValues( obj );

		// Calculate hash-code
		int total = 17;
		for( Object value : values ) {
			if( value == null ) continue;
			if( value == obj ) continue;
			total = total * 37 + value.hashCode();
		}

		return total;
	}
}
