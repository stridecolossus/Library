package org.sarge.lib.util;

import java.util.Set;
import java.util.function.Function;

/**
 * General utilities.
 * @author Sarge
 */
public final class Util {
	public static final String CARRIAGE_RETURN = System.getProperty("line.separator");
	public static final String SEPARATOR = System.getProperty("file.separator");

	/**
	 * Primitive wrapper types.
	 */
	public static final Set<Class<?>> PRIMITIVES = Set.of(
		Byte.TYPE,
		Short.TYPE,
		Integer.TYPE,
		Long.TYPE,
		Float.TYPE,
		Double.TYPE,
		Boolean.TYPE,
		Character.TYPE
	);

	private Util() {
		// Utilities class
	}

	/**
	 * Sleeps the current thread.
	 * <p>
	 * Note - silently ignores any {@link InterruptedException}s.
	 * @param time Duration
	 */
	public static void kip(long time) {
		try {
			Thread.sleep(time);
		}
		catch(InterruptedException e) {
		    // Ignored
		}
	}

	/**
	 * Clamps an integer value to the given range.
	 * @param value			Value to clamp
	 * @param min			Minimum
	 * @param max			Maximum
	 * @return Clamped value
	 */
	public static int clamp(int value, int min, int max) {
		if(value < min) {
			return min;
		}
		else
		if(value > max) {
			return max;
		}
		else {
			return value;
		}
	}

	/**
	 * Clamps a floating-point value value to the given range.
	 * @param value			Value to clamp
	 * @param min			Minimum
	 * @param max			Maximum
	 * @return Clamped value
	 */
	public static float clamp(float value, float min, float max) {
		if(value < min) {
			return min;
		}
		else
		if(value > max) {
			return max;
		}
		else {
			return value;
		}
	}

	public static boolean isEmpty(String cat) {
		return (cat == null) || cat.isEmpty();
	}

	// https://touk.pl/blog/2017/10/01/sneakily-throwing-exceptions-in-lambda-expressions-in-java/
	@SuppressWarnings("unchecked")
	public static <T extends Exception, R> R rethrow(Exception e) throws T {
		throw (T) e;
	}

	public static <T, R> Function<T, R> unchecked(Function<T, R> f) {
		return t -> {
			try {
				return f.apply(t);
			}
			catch(Exception e) {
				throw new RuntimeException("Rethrow: " + e.getMessage(), e);
			}
		};
	}
}
