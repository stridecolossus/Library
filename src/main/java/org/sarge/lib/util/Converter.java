package org.sarge.lib.util;

import static java.util.stream.Collectors.toMap;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

/**
 * A <i>converter</i> is a convenience adapter for a parsing function.
 * @param <T> Result type
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
	 * Parses a case insensitive boolean value.
	 * @param str Boolean string
	 * @return Parsed boolean
	 */
	Converter<Boolean> BOOLEAN = str -> {
		if(str.equalsIgnoreCase("true")) {
			return Boolean.TRUE;
		}
		else
		if(str.equalsIgnoreCase("false")) {
			return Boolean.FALSE;
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
	// TODO - this is a bit specific? add as a optional argument to converter?

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

	/**
	 * Parses a duration.
	 * <p>
	 * This implementation supports the standard ISO {@link Duration#parse(CharSequence)} or a custom format specified by a duration and a time unit, e.g. {@code 10s} for 10 seconds.
	 * <p>
	 * The following time units are supported:
	 * <ul>
	 * <li>ms - Milliseconds</li>
	 * <li>s - Seconds</li>
	 * <li>m - Minutes</li>
	 * <li>h - Hours</li>
	 * <li>d - days</li>
	 * </ul>
	 */
	Converter<Duration> DURATION = str -> {
		if(str.startsWith("P")) {
			// Delegate to ISO format
			return Duration.parse(str);
		}
		else {
			if(str.endsWith("ms")) {
				// Parse milliseconds
				final String ms = str.substring(0, str.length() - 2);
				return Duration.ofMillis(Long.parseLong(ms));
			}
			else {
				// Parse custom duration format
				final int end = str.length() - 1;
				final int num = Integer.parseInt(str.substring(0, end));
				final TimeUnit unit = switch(str.charAt(end)) {
					case 's' -> TimeUnit.SECONDS;
					case 'm' -> TimeUnit.MINUTES;
					case 'h' -> TimeUnit.HOURS;
					case 'd' -> TimeUnit.DAYS;
					default -> throw new NumberFormatException("Unsupported duration unit: " + str);
				};
				return Duration.of(num, unit.toChronoUnit());
			}
		}
	};
}
