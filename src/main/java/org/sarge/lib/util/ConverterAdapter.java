package org.sarge.lib.util;

import java.util.HashMap;
import java.util.Map;

/**
 * Adapter for a map with {@link Converter} functionality for values.
 * @author Sarge
 * TODO
 * - should be called MapConverter or some-such?
 * - need implements Map? or expose underlying table? probably not
 * - return types should be wrappers?
 * - over-loads for mandatory getters?
 */
public class ConverterAdapter {
    private final Map<?, ?> values;

    /**
     * Constructor.
     * @param values Table of name-value pairs
     */
    public ConverterAdapter(Map<?, ?> values) {
        this.values = new HashMap<>(values);
    }
    
	/**
	 * Retrieves and converts a value.
	 * @param key			Key
	 * @param def			Optional default value
	 * @param converter		Converter
	 * @return Converted value
	 * @throws NumberFormatException if the value cannot be converted, or the key is not present and no default was supplied (i.e. is mandatory)
	 */
	public <T> T getValue(String key, T def, Converter<T> converter) {
		final Object value = values.get(key);
		if(value == null) {
			if(def == null) {
				throw new NumberFormatException("Missing mandatory entry: " + key);
			}
			else {
				return def;
			}
		}
		else {
			return converter.convert(value.toString());
		}
	}

	/**
	 * Retrieves a string value.
	 * @param name		Name
	 * @param def		Optional default value
	 * @return String
	 */
	public String toString(String name, String def) {
		return getValue(name, def, Converter.STRING);
	}

	/**
	 * Retrieves an integer value.
	 * @param name		Name
	 * @param def		Optional default value
	 * @return Integer
	 */
	public int toInteger(String name, Integer def) {
		return getValue(name, def, Converter.INTEGER);
	}

	/**
	 * Retrieves a long value.
	 * @param name		Name
	 * @param def		Optional default value
	 * @return Long
	 */
	public long toLong(String name, Long def) {
		return getValue(name, def, Converter.LONG);
	}

	/**
	 * Retrieves a floating-point value.
	 * @param name		Name
	 * @param def		Optional default value
	 * @return Floating-point value
	 */
	public float toFloat(String name, Float def) {
		return getValue(name, def, Converter.FLOAT);
	}

	/**
	 * Retrieves a boolean value.
	 * @param name		Name
	 * @param def		Optional default value
	 * @return Boolean
	 */
	public boolean toBoolean(String name, Boolean def) {
		return getValue(name, def, Converter.BOOLEAN);
	}
	
	@Override
	public String toString() {
	    return values.toString();
	}
}
