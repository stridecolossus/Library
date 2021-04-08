package org.sarge.lib.util;

import static java.util.stream.Collectors.toMap;
import static org.sarge.lib.util.Check.notNull;

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
	 * An <i>enumeration converter</i> maps a string value to an enumeration.
	 * <p>
	 * Notes:
	 * <ul>
	 * <li>comparisons are case-insensitive</i>
	 * <li>enumeration constants have hyphens replacing any underscores</li>
	 * </ul>
	 * @param <E> Enumeration
	 */
	class EnumerationConverter<E extends Enum<E>> implements Converter<E> {
		private final Map<String, E> map;

		/**
		 * Constructor.
		 * @param clazz Enumeration class
		 */
		public EnumerationConverter(Class<E> clazz) {
			map = Arrays.stream(clazz.getEnumConstants()).collect(toMap(this::name, Function.identity()));
		}

		private String name(E key) {
			return key.name().toLowerCase().replaceAll("_", "-");
		}

		@Override
		public E apply(String str) throws NumberFormatException {
			final E result = map.get(str.toLowerCase());
			if(result == null) throw new NumberFormatException("Unknown enumeration constant: " + str);
			return result;
		}
	}

	/**
	 * A <i>table converter</i> is an adapter for a converter with a lookup table of values (case insensitive).
	 * @param <T> Conversion type
	 */
	class TableConverter<T> implements Converter<T> {
		private final Map<String, T> table;
		private final Converter<T> converter;

		/**
		 * Constructor.
		 * @param table				Table
		 * @param converter			Delegate converter
		 */
		public TableConverter(Map<String, T> table, Converter<T> converter) {
			this.table = Map.copyOf(table);
			this.converter = notNull(converter);
		}

		@Override
		public T apply(String str) throws NumberFormatException {
			final T value = table.get(str.toLowerCase());
			if(value == null) {
				return converter.apply(str);
			}
			else {
				return value;
			}
		}
	}
}
