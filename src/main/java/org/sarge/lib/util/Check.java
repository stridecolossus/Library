package org.sarge.lib.util;

import java.util.Collection;

/**
 * Parameter assertion methods.
 * @author Sarge
 */
public final class Check {
	private Check() {
		// Utility class
	}

	/**
	 * Tests whether the given object is null.
	 * @param obj Object to test
	 * @param msg Reason
	 * @throws IllegalArgumentException if the given object is <tt>null</tt>
	 */
	public static void notNull(Object obj, String msg) throws IllegalArgumentException {
		if(obj == null) throw new IllegalArgumentException(msg);
	}

	public static void notNull(Object obj) throws IllegalArgumentException {
		notNull(obj, "Cannot be null");
	}

	/**
	 * Tests whether the given string is empty.
	 * @param str String to test
	 * @param msg Reason
	 * @throws IllegalArgumentException if the given string is empty
	 */
	public static void notEmpty(String str, String msg) throws IllegalArgumentException {
		if((str == null) || (str.length() == 0)) {
			throw new IllegalArgumentException(msg);
		}
	}

	public static void notEmpty(String str) throws IllegalArgumentException {
		notEmpty(str, "String cannot be empty");
	}

	/**
	 * Tests whether the given collection is empty.
	 * @param c Collection to test
	 * @param msg Reason
	 * @throws IllegalArgumentException if the given collection is empty
	 */
	public static void notEmpty(Collection<?> c, String msg) throws IllegalArgumentException {
		if((c == null) || c.isEmpty()) {
			throw new IllegalArgumentException(msg);
		}
	}

	public static void notEmpty(Collection<?> c) throws IllegalArgumentException {
		notEmpty(c, "Collection cannot be empty");
	}

	/**
	 * Tests whether the given array is empty.
	 * @param array Array to test
	 * @param <T> Type
	 * @throws IllegalArgumentException if the given array is empty
	 */
	@SafeVarargs
	public static <T> void notEmpty(T... array) throws IllegalArgumentException {
		if((array == null) || (array.length == 0)) {
			throw new IllegalArgumentException("Array cannot be empty");
		}
	}

	/**
	 * Tests whether the given value is zero-or-more.
	 * @param value Value to test
	 */
	public static void zeroOrMore(float value) {
		if(value < 0) throw new IllegalArgumentException("Must be zero-or-more");
	}

	/**
	 * Tests whether the given value is one-or-more.
	 * @param value Value to test
	 */
	public static void oneOrMore(float value) {
		if(value < 1) throw new IllegalArgumentException("Must be one-or-more");
	}

	/**
	 * Tests whether the given value is within the specified range.
	 * @param value Value to test
	 * @param min Minimum
	 * @param max Maximum
	 * @throws IllegalArgumentException if the value is outside of the specified
	 *             range
	 */
	public static void range(float value, float min, float max) throws IllegalArgumentException {
		if((value < min) || (value > max)) {
			throw new IllegalArgumentException("Value of out range: " + value + "(" + min + ".." + max + ")");
		}
	}

	/**
	 * Tests whether the given floating-point value is a valid 0..1 percentile.
	 * @param f Value to test
	 * @throws IllegalArgumentException if the value is not a percentile
	 */
	public static void isPercentile(float f) {
		range(f, 0, 1);
	}
}
