package org.sarge.lib.util;

import static java.util.stream.Collectors.toMap;

import java.util.*;
import java.util.function.Function;

/**
 * A <i>converter</i>
 * @param <T> Converted data-type
 * @author Sarge
 */
@FunctionalInterface
public interface Converter<T> extends Function<String, T> {
	/**
	 * Converts the given string.
	 * @param str String to convert
	 * @return Converted value
	 * @throws NumberFormatException if the value cannot be converted
	 */
	@Override
	T apply(String str) throws NumberFormatException;

	/**
	 * Identity converter.
	 */
	Converter<String> IDENTITY = str -> str;

	/**
	 * Converts to a boolean.
	 */
	Converter<Boolean> BOOLEAN = str -> {
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
	 * @param <E> Enumeration
	 * @param clazz Enumeration class
	 * @return Enumeration converter
	 */
	static <E extends Enum<E>> Converter<E> of(Class<E> clazz) {
		final var map = Arrays.stream(clazz.getEnumConstants()).collect(toMap(Converter::constant, Function.identity()));

		return str -> {
			final E result = map.get(str.toLowerCase());
			if(result == null) throw new NumberFormatException("Unknown enumeration constant: " + str);
			return result;
		};
	}

	/**
	 * Generates the <i>standardised</i> name of the given enumeration constant.
	 * The result is lower-case with underscores replaced by hyphens.
	 * @param e Enumeration constant
	 * @return Enumeration constant name
	 */
	private static String constant(Enum<?> e) {
		return e.name().toLowerCase().replaceAll("_", "-");
	}

	/**
	 * Creates an adapter for a converter that first attempts to lookup a value from the given table.
	 * @param <T> Converted type
	 * @param table			Lookup table
	 * @param converter		Converter
	 * @return Table converter
	 */
	static <T> Converter<T> of(Map<String, T> table, Converter<T> converter) {
		final var copy = Map.copyOf(table);

		return str -> {
			final T value = copy.get(str);
			if(value == null) {
				return converter.apply(str);
			}
			else {
				return value;
			}
		};
	}
}
