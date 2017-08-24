package org.sarge.lib.object;

import java.lang.reflect.Array;
import java.lang.reflect.Field;

/**
 * Utilities for generating <tt>equals</tt> comparators using reflection.
 * @author Sarge
 */
public final class EqualsBuilder {
	private EqualsBuilder() {
		// Utility class
	}
	
	public static boolean isEqual(float a, float b) {
		return isEqual(a, b, 0.0001f);
	}

	public static boolean isEqual(float a, float b, float epsilon) {
		return Math.abs(a - b) < epsilon;
	}

	/**
	 * Tests equality using reflection.
	 * @param thisObject This object
	 * @param thatObject Other object
	 * @return Whether the two objects are equal
	 */
	public static boolean equals(Object thisObject, Object thatObject) {
		// Check null
		if(thatObject == null) return false;

		// Check self
		if(thisObject == thatObject) return true;

		// Check same class
		final Class<?> clazz = thisObject.getClass();
		final Class<?> other = thatObject.getClass();
		if(!clazz.isAssignableFrom(other) && !other.isAssignableFrom(clazz)) return false;

		// Compare fields
		return ReflectionUtils.getMembers(clazz).allMatch(field -> equals(field, thisObject, thatObject));
	}
	
	private static boolean equals(Field field, Object thisObject, Object thatObject) {
		final Object thisValue = ReflectionUtils.getValue(field, thisObject);
		final Object thatValue = ReflectionUtils.getValue(field, thatObject);

		if(thisValue == null) {
			return thatValue == null;
		}
		else
		if(field.getType().isArray()) {
			final int len = Array.getLength(thisValue);
			if(len != Array.getLength(thatValue)) return false;
			for(int n = 0; n < len; ++n) {
				if(!equals(Array.get(thisValue, n), Array.get(thatValue, n))) return false;
			}
			return true;
		}
		else {
			return thisValue.equals(thatValue);
		}
	}
}
