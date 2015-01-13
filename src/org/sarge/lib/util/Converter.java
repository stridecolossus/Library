package org.sarge.lib.util;

/**
 * Defines a method of converting a string to a data-type.
 * @author Sarge
 * @param <T> Data-type
 */
@FunctionalInterface
public interface Converter<T> {
	/**
	 * Converts the given string to this data-type.
	 * @param str String representation
	 * @return Converted value
	 * @throws NumberFormatException if the value cannot be converted
	 */
	T convert( String str ) throws NumberFormatException;

	/**
	 * String-to-string conversion, i.e. does nothing.
	 */
	Converter<String> STRING = str -> str;

	/**
	 * Converts to an integer.
	 */
	Converter<Integer> INTEGER = Integer::parseInt;

	/**
	 * Converts to a long.
	 */
	Converter<Long> LONG = Long::parseLong;

	/**
	 * Converts to a floating-point value.
	 */
	Converter<Float> FLOAT = Float::parseFloat;

	/**
	 * Converts to a double.
	 */
	Converter<Double> DOUBLE = Double::parseDouble;

	/**
	 * Converts to a boolean value.
	 */
	Converter<Boolean> BOOLEAN = Boolean::parseBoolean;
}
