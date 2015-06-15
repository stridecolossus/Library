package org.sarge.lib.util;

import java.util.Optional;
import java.util.function.Supplier;

/**
 * Template-class for utilities that need to convert string values.
 * @see Converter
 * @author Sarge
 */
@FunctionalInterface
public interface ConverterAdapter {
	/**
	 * Retrieves the specified string value by name.
	 * @param name Value name
	 * @return Value as a string if present
	 */
	Optional<String> getValue( String name );

	/**
	 * Retrieves a string with the given name.
	 * @param name		String name
	 * @param def		Default value
	 * @return String
	 * @throws NumberFormatException if the string is missing but mandatory
	 */
	default String getString( String name, Optional<String> def ) {
		return getValue( name, def, Converter.STRING );
	}

	/**
	 * Retrieves an integer value with the given name.
	 * @param name		Integer name
	 * @param def		Default value or empty if mandatory
	 * @return Integer value
	 * @throws NumberFormatException if the integer is invalid or missing but mandatory
	 */
	default Integer getInteger( String name, Optional<Integer> def ) {
		return getValue( name, def, Converter.INTEGER );
	}

	/**
	 * Retrieves a long value with the given name.
	 * @param name		Long name
	 * @param def		Default value or empty if mandatory
	 * @return Long value
	 * @throws NumberFormatException if the value is invalid or missing but mandatory
	 */
	default Long getLong( String name, Optional<Long> def ) {
		return getValue( name, def, Converter.LONG );
	}

	/**
	 * Retrieves a floating-point value with the given name.
	 * @param name		Float name
	 * @param def		Default value or empty if mandatory
	 * @return Floating-point value
	 * @throws NumberFormatException if the value is invalid or missing but mandatory
	 */
	default Float getFloat( String name, Optional<Float> def ) {
		return getValue( name, def, Converter.FLOAT );
	}

	/**
	 * Retrieves a boolean value with the given name.
	 * @param name		Boolean name
	 * @param def		Default value or empty if mandatory
	 * @return Boolean value
	 * @throws NumberFormatException if the value is invalid or missing but mandatory
	 */
	default Boolean getBoolean( String name, Optional<Boolean> def ) {
		return getValue( name, def, Converter.BOOLEAN );
	}

	/**
	 * Retrieves an enum value with the given name.
	 * <p>
	 * Note that this method creates a new string converter for the given class on each invocation, if a given enum
	 * is used more than once then {@link #getValue(String, Object, Converter)} should be used with a reusable converter.
	 * <p>
	 * @param <E> Enum type
	 * @param name		Enum value name
	 * @param def		Default value or empty if mandatory
	 * @param clazz		Enum class
	 * @return Enum constant
	 * @throws NumberFormatException if the value is invalid or missing but mandatory
	 */
	default <E extends Enum<E>> E getEnum( String name, Optional<E> def, Class<E> clazz ) {
		return getValue( name, def, new EnumConverter<>( clazz ) );
	}

	/**
	 * Retrieves a value by name.
	 * @param <T> Data-type
	 * @param name			Value name
	 * @param def			Default value or empty if mandatory
	 * @param converter		Value converter
	 * @return Converted value
	 * @throws NumberFormatException if the value is invalid or missing but mandatory
	 */
	default <T> T getValue( String name, Optional<T> def, Converter<T> converter ) {
		final Supplier<T> supplier = () -> def.orElseThrow( () -> new NumberFormatException( "Missing mandatory field: " + name ) );
		return getValue( name ).map( converter::convert ).orElseGet( supplier );
	}
}
