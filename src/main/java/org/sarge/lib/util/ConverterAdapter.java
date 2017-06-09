package org.sarge.lib.util;


/**
 * Adapter for an object that returns string-based attributes.
 * @author Sarge
 */
public abstract class ConverterAdapter {
	/**
	 * Retrieves a value.
	 * @param name Name
	 * @return Value of <tt>null</tt> if not present
	 */
	protected abstract String getValue(String name);
	
	/**
	 * Retrieves and converts an attribute.
	 * @param name			Name
	 * @param def			Optional default value
	 * @param converter		Converter
	 * @return Converted attribute
	 * @throws NumberFormatException if the attribute is invalid or is not present and no default was supplied
	 */
	public <T> T getAttribute(String name, T def, Converter<T> converter) {
		final String value = getValue(name);
		if(value == null) {
			if(def == null) {
				throw new NumberFormatException("Missing mandatory attribute: " + name);
			}
			else {
				return def;
			}
		}
		else {
			return converter.convert(value);
		}
	}

	/**
	 * Retrieves a string attribute.
	 * @param name		Name
	 * @param def		Optional default value
	 * @return String
	 */
	public String getString(String name, String def) {
		return getAttribute(name, def, Converter.STRING);
	}

	/**
	 * Retrieves an integer attribute.
	 * @param name		Name
	 * @param def		Optional default value
	 * @return Integer
	 */
	public int getInteger(String name, Integer def) {
		return getAttribute(name, def, Converter.INTEGER);
	}

	/**
	 * Retrieves a long attribute.
	 * @param name		Name
	 * @param def		Optional default value
	 * @return Long
	 */
	public long getLong(String name, Long def) {
		return getAttribute(name, def, Converter.LONG);
	}

	/**
	 * Retrieves a floating-point attribute.
	 * @param name		Name
	 * @param def		Optional default value
	 * @return Floating-point value
	 */
	public float getFloat(String name, Float def) {
		return getAttribute(name, def, Converter.FLOAT);
	}

	/**
	 * Retrieves a boolean attribute.
	 * @param name		Name
	 * @param def		Optional default value
	 * @return Boolean
	 */
	public boolean getBoolean(String name, Boolean def) {
		return getAttribute(name, def, Converter.BOOLEAN);
	}
}
