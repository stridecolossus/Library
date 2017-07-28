package org.sarge.lib.util;

/**
 * String utilities.
 * @author Sarge
 */
public final class StringUtil {
	public static final String EMPTY_STRING = "";
	
	private StringUtil() {
		// Utilities class
	}
	
	public static boolean isEmpty(String str) {
	    return (str == null) || str.isEmpty();
	}

	/**
	 * Wraps a string.
	 * @param str		String
	 * @param left		Left-hand token
	 * @param right		Right-hand token
	 * @return Wrapped string
	 */
	public static String wrap(String str, String left, String right) {
		final StringBuilder sb = new StringBuilder();
		sb.append(left);
		sb.append(str);
		sb.append(right);
		return sb.toString();
	}

	/**
	 * Wraps a string.
	 * @param str		String
	 * @param wrap		Wrapping token
	 * @return Wrapped string
	 */
	public static String wrap(String str, String wrap) {
		return wrap(str, wrap, wrap);
	}
}
