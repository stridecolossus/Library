package org.sarge.lib.util;

/**
 * Template-class for utilities that need to convert string values.
 * @see Converter
 * @author Sarge
 */
public abstract class ConverterAdapter {
	/**
	 * Retrieves a string with the given name.
	 * @param name		Attribute name
	 * @param def		Default value or <tt>null</tt> if mandatory
	 * @return String
	 * @throws NumberFormatException if the string missing but mandatory
	 */
	public String getString( String name, String def ) {
		return getValue( name, def, Converter.STRING );
	}

	/**
	 * Retrieves an integer value with the given name.
	 * @param name		Attribute name
	 * @param def		Default value or <tt>null</tt> if mandatory
	 * @return Integer value
	 * @throws NumberFormatException if the integer is invalid or missing but mandatory
	 */
	public Integer getInteger( String name, Integer def ) {
		return getValue( name, def, Converter.INTEGER );
	}

	/**
	 * Retrieves a long value with the given name.
	 * @param name		Attribute name
	 * @param def		Default value or <tt>null</tt> if mandatory
	 * @return Long value
	 * @throws NumberFormatException if the value is invalid or missing but mandatory
	 */
	public Long getLong( String name, Long def ) {
		return getValue( name, def, Converter.LONG );
	}

	/**
	 * Retrieves a floating-point value with the given name.
	 * @param name		Attribute name
	 * @param def		Default value or <tt>null</tt> if mandatory
	 * @return Floating-point value
	 * @throws NumberFormatException if the value is invalid or missing but mandatory
	 */
	public Float getFloat( String name, Float def ) {
		return getValue( name, def, Converter.FLOAT );
	}

	/**
	 * Retrieves a boolean value with the given name.
	 * @param name		Attribute name
	 * @param def		Default value or <tt>null</tt> if mandatory
	 * @return Boolean value
	 * @throws NumberFormatException if the value is invalid or missing but mandatory
	 */
	public Boolean getBoolean( String name, Boolean def ) {
		return getValue( name, def, Converter.BOOLEAN );
	}

	/**
	 * Retrieves an enum value with the given name.
	 * <p>
	 * Note that this method creates a new string converter for the given class on each invocation, if a given enum
	 * is used more than once then {@link #getValue(String, Object, Converter)} should be used with a reusable converter.
	 * <p>
	 * @param <E> Enum type
	 * @param name		Attribute name
	 * @param def		Default value or <tt>null</tt> if mandatory
	 * @param clazz		Enum class
	 * @return Enum constant
	 * @throws NumberFormatException if the value is invalid or missing but mandatory
	 */
	public <E extends Enum<E>> E getEnum( String name, E def, Class<E> clazz ) {
		return getValue( name, def, new EnumConverter<>( clazz ) );
	}

	/**
	 * Retrieves a value by name.
	 * @param <T> Data-type
	 * @param name			Attribute name
	 * @param def			Default value or <tt>null</tt> if mandatory
	 * @param converter		Value converter
	 * @return converted value
	 * @throws NumberFormatException if the value is invalid or missing but mandatory
	 */
	protected <T> T getValue( String name, T def, Converter<T> converter ) {
		// Load value from this source
		final String value = getValue( name );

		// Convert value or use default if missing and not mandatory
		if( Util.isEmpty( value ) ) {
			if( def == null ) {
				throw new NumberFormatException( "Missing mandatory value: " + name );
			}
			else {
				return def;
			}
		}
		else {
			return converter.convert( value );
		}
	}

	/**
	 * Retrieves the specified attribute value by name.
	 * @param name Attribute name
	 * @return Attribute value or <tt>null</tt> if not present
	 */
	protected abstract String getValue( String name );
}
