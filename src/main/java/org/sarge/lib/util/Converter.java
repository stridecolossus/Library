package org.sarge.lib.util;

import static java.util.stream.Collectors.toMap;
import static org.sarge.lib.util.Check.notNull;

import java.util.Arrays;
import java.util.Map;
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
	 * Converts to an string (i.e. does nothing).
	 */
    Converter<String> STRING = str -> str;

	/**
	 * Converts to an integer.
	 */
	Converter<Integer> INTEGER = Integer::parseInt;

	/**
	 * Converts to a floating-point number.
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
		/**
		 * Generates the <i>name</i> of the given enumeration constant (lower-case, underscores replaced by hyphens).
		 * @param <E> Enumeration
		 * @param value Enumeration constant
		 * @return
		 */
		public static <E extends Enum<E>> String name(E value) {
			return value.name().toLowerCase().replaceAll("_", "-");
		}

		private final Map<String, E> map;

		/**
		 * Constructor.
		 * @param clazz Enumeration class
		 */
		public EnumerationConverter(Class<E> clazz) {
			map = Arrays.stream(clazz.getEnumConstants()).collect(toMap(EnumerationConverter::name, Function.identity()));
		}

		@Override
		public E apply(String str) throws NumberFormatException {
			final E result = map.get(str.toLowerCase());
			if(result == null) throw new NumberFormatException("Unknown enumeration constant: " + str);
			return result;
		}
	}

	/**
	 * A <i>table converter</i> is an adapter for a converter with a case insensitive lookup table of values.
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
