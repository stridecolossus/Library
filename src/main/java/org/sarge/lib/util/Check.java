package org.sarge.lib.util;

import java.util.*;

/**
 * Utility class providing various parameter validation methods.
 * @author Sarge
 */
public final class Check {
	private Check() {
		// Utility class
	}

	/**
	 * Checks that the given object is not {@code null}.
	 * @param obj Non-null object
	 * @param msg Reason
	 * @throws IllegalArgumentException if the given object is {@code null}
	 */
	public static <T> T notNull(T obj, String msg) {
		if(obj == null) {
            throw new IllegalArgumentException(msg);
        }
		return obj;
	}

	public static <T> T notNull(T obj) {
		return notNull(obj, "Cannot be null");
	}

	/**
	 * Checks that the given string is not {@code null} or empty.
	 * @param str String
	 * @param msg Reason
	 * @throws IllegalArgumentException if the given string is empty
	 */
	public static String notEmpty(String str, String msg) {
		if((str == null) || str.isEmpty()) throw new IllegalArgumentException(msg);
		return str;
	}

	public static String notEmpty(String str) {
		return notEmpty(str, "String cannot be empty");
	}

	/**
	 * Checks that the given collection is not {@code null} or empty.
	 * @param c Collection
	 * @param msg Reason
	 * @throws IllegalArgumentException if the given collection is empty
	 */
	public static <T> Collection<T> notEmpty(Collection<T> c, String msg) {
		if((c == null) || c.isEmpty()) throw new IllegalArgumentException(msg);
		return c;
	}

	public static <T> Collection<T> notEmpty(Collection<T> c) {
		return notEmpty(c, "Collection cannot be empty");
	}

	/**
	 * Checks that the given map is not {@code null} or empty.
	 * @param map Map
	 * @param msg Reason
	 * @return Map
	 * @throws IllegalArgumentException if the given map is empty
	 */
	public static <K, V> Map<K, V> notEmpty(Map<K, V> map, String msg) {
		if((map == null) || map.isEmpty()) throw new IllegalArgumentException(msg);
		return map;
	}

	public static <K, V> Map<K, V> notEmpty(Map<K, V> map) {
		return notEmpty(map, "Map cannot be empty");
	}

	/**
	 * Checks that the given array is not {@code null} or empty.
	 * @param array Array
	 * @param <T> Array component type
	 * @throws IllegalArgumentException if the given array is empty
	 */
	public static <T> T[] notEmpty(T[] array) {
		if((array == null) || (array.length == 0)) {
			throw new IllegalArgumentException("Array cannot be empty");
		}
		return array;
	}

	/**
     * Checks that the given number is zero-or-more.
     * @param <T> Number type
     * @param num Number
     */
    public static <T extends Number> T zeroOrMore(T num) {
        if(num.floatValue() < 0) {
            throw new IllegalArgumentException("Must be zero-or-more");
        }
        return num;
    }

    /**
     * Checks that the given number is one-or-more.
     * @param <T> Number type
     * @param num Value to test
     */
    public static <T extends Number> T oneOrMore(T num) {
        if(num.floatValue() < 1) {
            throw new IllegalArgumentException("Must be one-or-more");
        }
        return num;
    }

    /**
     * Checks that the given number is greater than zero.
     * @param <T> Number type
     * @param num Number
     */
    public static <T extends Number> T positive(T num) {
    	if(num.floatValue() <= 0) {
    		throw new IllegalArgumentException("Must be positive and greater-than zero");
    	}
    	return num;
    }

    /**
     * Checks that the given number is within the specified range.
     * Note that the {@link max} parameter is <b>inclusive</b>, i.e. whether the given number is less than <b>or equal to</b> the specified maximum.
     * @param <T> Number type
     * @param num Number
     * @param min Minimum
     * @param max Maximum (inclusive)
     * @throws IllegalArgumentException if the value is not within the specified range
     */
    public static <T extends Number> T range(T num, T min, T max) {
    	final float f = num.floatValue();
        if((f < min.floatValue()) || (f > max.floatValue())) {
            throw new IllegalArgumentException(String.format("Value of out range: actual=%s expected=(%s...%s)", num, min, max));
        }
        return num;
    }

	/**
	 * Checks that the given floating-point value is a valid 0..1 percentile.
	 * @param p Percentile
	 * @throws IllegalArgumentException if the value is not a percentile
	 */
	public static float isPercentile(float p) {
		return range(p, 0f, 1f);
	}
}
