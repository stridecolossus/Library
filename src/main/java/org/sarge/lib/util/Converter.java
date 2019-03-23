package org.sarge.lib.util;

import static java.util.stream.Collectors.toMap;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;

import org.apache.commons.lang3.StringUtils;

/**
 * Converts a string to a data-type.
 * @author Sarge
 * @param <T> Data-type
 */
@FunctionalInterface
public interface Converter<T> extends Function<String, T> {
	/**
	 * Convert the given string to this data-type.
	 * @param str String to convert
	 * @return Converted data
	 * @throws NumberFormatException if the value cannot be converted
	 */
	@Override
	T apply(String str) throws NumberFormatException;

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
	Converter<Boolean> BOOLEAN = str -> {
		if(StringUtils.isEmpty(str)) {
			throw new NumberFormatException("Empty boolean");
		}
		else
		if(str.equalsIgnoreCase("true")) {
			return true;
		}
		else
		if(str.equalsIgnoreCase("false")) {
			return false;
		}
		else {
			throw new NumberFormatException("Invalid boolean: " + str);
		}
	};

	/**
	 * Creates a converter for the given enumeration.
	 * <p>
	 * Notes:
	 * <ul>
	 * <li>comparisons are case-insensitive</li>
	 * <li>enumeration constants with under-score characters are replaced by hyphens</li>
	 * </ul>
	 * <p>
	 * @param clazz Enumeration class
	 * @return Enumeration converter
	 * @throws NumberFormatException if the constant is not valid
	 * @param <E> Enumeration
	 */
	static <E extends Enum<E>> Converter<E> enumeration(Class<E> clazz) throws NumberFormatException {
		// Build constant lookup map with cleaned keys
		final Function<E, String> mapper = e -> e.name().toLowerCase();
		final Map<String, E> map = Arrays.stream(clazz.getEnumConstants()).collect(toMap(mapper, Function.identity()));

		// Create converter
		return name -> {
			final E result = map.get(name.toLowerCase().replaceAll("-", "_"));
			if(result == null) throw new NumberFormatException("Unknown enum constant: " + name);
			return result;
		};
	}
}
