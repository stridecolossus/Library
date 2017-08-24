package org.sarge.lib.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Adapter for a map with {@link Converter} functionality for values.
 * @author Sarge
 * TODO
 * - should be called MapConverter or some-such?
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
     * Looks up an optional value.
     * @param key Key
     * @return Value
     */
    public <T> Optional<T> getOptional(Object key, Converter<T> converter) {
    	return Optional.ofNullable(values.get(key)).map(Object::toString).map(converter::convert);
    }
    
	/**
	 * Retrieves and converts a value.
	 * @param key			Key
	 * @param def			Optional default value
	 * @param converter		Converter
	 * @return Converted value
	 * @throws NumberFormatException if the value cannot be converted, or the key is not present and no default was supplied (i.e. is mandatory)
	 */
	public <T> T toValue(Object key, T def, Converter<T> converter) {
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
	public String toString(Object name, String def) {
		return toValue(name, def, Converter.STRING);
	}

	public String toString(Object name) {
		return toValue(name, null, Converter.STRING);
	}

	/**
	 * Retrieves an integer value.
	 * @param name		Name
	 * @param def		Optional default value
	 * @return Integer
	 */
	public Integer toInteger(Object name, Integer def) {
		return toValue(name, def, Converter.INTEGER);
	}

	public Integer toInteger(Object name) {
		return toValue(name, null, Converter.INTEGER);
	}

	/**
	 * Retrieves a long value.
	 * @param name		Name
	 * @param def		Optional default value
	 * @return Long
	 */
	public Long toLong(Object name, Long def) {
		return toValue(name, def, Converter.LONG);
	}

	public Long toLong(Object name) {
		return toValue(name, null, Converter.LONG);
	}

	/**
	 * Retrieves a floating-point value.
	 * @param name		Name
	 * @param def		Optional default value
	 * @return Floating-point value
	 */
	public Float toFloat(Object name, Float def) {
		return toValue(name, def, Converter.FLOAT);
	}

	public Float toFloat(Object name) {
		return toValue(name, null, Converter.FLOAT);
	}

	/**
	 * Retrieves a boolean value.
	 * @param name		Name
	 * @param def		Optional default value
	 * @return Boolean
	 */
	public Boolean toBoolean(Object name, Boolean def) {
		return toValue(name, def, Converter.BOOLEAN);
	}

	public Boolean toBoolean(Object name) {
		return toValue(name, null, Converter.BOOLEAN);
	}
	
	@Override
	public String toString() {
	    return values.toString();
	}
}
