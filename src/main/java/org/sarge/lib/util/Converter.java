package org.sarge.lib.util;

import java.time.Duration;
import java.time.LocalTime;
import java.util.function.Supplier;

/**
 * Converts a string to a data-type.
 * @author Sarge
 * @param <T> Data-type
 */
@FunctionalInterface
public interface Converter<T> {
	/**
	 * Convert the given string to this data-type.
	 * @param str String to convert
	 * @return Converted data
	 * @throws NumberFormatException if the value is not valid
	 */
	T convert(String str);
	
	/**
	 * Converts to an string (i.e. does nothing).
	 */
	Converter<String> STRING = str -> str;

	/**
	 * Converts to an integer.
	 */
	Converter<Integer> INTEGER = Integer::parseInt;

	/**
	 * Converts to floating-point.
	 */
	Converter<Float> FLOAT = Float::parseFloat;

	/**
	 * Converts to a long.
	 */
	Converter<Long> LONG = Long::parseLong;

	/**
	 * Converts to a boolean.
	 */
	Converter<Boolean> BOOLEAN = Boolean::parseBoolean;

	/**
	 * Creates a duration converter.
	 * @return Duration converter
	 * @see LocalTime#parse(CharSequence)
	 */
	Converter<Duration> DURATION = str -> {
		final LocalTime time = LocalTime.parse(str);
		return Duration.between(LocalTime.MIN, time);
	};
	
	/**
	 * Creates a converter for the given enumeration.
	 * @param clazz Enumeration class
	 * @return Enumeration converter
	 * @throws NumberFormatException if the constant is not valid
	 * @param <E> Enumeration
	 * @see #enumeration(Class, Supplier)
	 */
	static <E extends Enum<E>> Converter<E> enumeration(Class<E> clazz) throws NumberFormatException {
		return name -> Util.getEnumConstant(name, clazz);
	}
}
