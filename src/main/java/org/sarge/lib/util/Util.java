package org.sarge.lib.util;

import java.util.Arrays;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.function.Supplier;

/**
 * General utilities.
 * @author Sarge
 */
public final class Util {
	private Util() {
		// Utilities class
	}

	/**
	 * Looks up the enum constant with the specified name.
	 * @param name			Enum constant name
	 * @param clazz			Enum class
	 * @return Enum constant
	 * @param <E> Enumeration
	 * @throws NumberFormatException if the constant is invalid
	 */
	public static <E extends Enum<E>> E getEnumConstant(String name, Class<E> clazz) throws NumberFormatException {
		return getEnumConstant(name, clazz, () -> new NumberFormatException("Unknown enum constant: " + name));
	}
	
	/**
	 * Looks up the enum constant with the specified name or throw an exception of the given type.
	 * @param name			Enum constant name
	 * @param clazz			Enum class
	 * @param exception		Exception supplier
	 * @return Enum constant
	 * @param <E> Enumeration
	 * @param <X> Exception
	 */
	public static <E extends Enum<E>, X extends RuntimeException> E getEnumConstant(String name, Class<E> clazz, Supplier<? extends X> exception) throws X {
		final String str = name.trim().toUpperCase().replace("-", "_");
		return Arrays.stream(clazz.getEnumConstants()).filter(e -> e.name().equals(str)).findFirst().orElseThrow(exception);
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
		} catch(InterruptedException e) {
		    // Ignored
		}
	}
	
	/**
	 * Converts an enumeration to an iterator.
	 * @param enumeration Enumeration
	 * @return Iterator over the given enumeration
	 */
	public static <T> Iterator<T> iterator(Enumeration<T> enumeration) {
	    return new Iterator<T>() {
	        @Override
	        public boolean hasNext() {
	            return enumeration.hasMoreElements();
	        }
	        
	        @Override
	        public T next() {
	            return enumeration.nextElement();
	        }
        };
	}
}
