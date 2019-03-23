package org.sarge.lib.util;

/**
 * A <i>value converter</i> provides methods to convert a string-based value to primitives.
 * @author Sarge
 * @see Converter
 */
public abstract class ValueConverter {
	/**
	 * @return Value or <tt>null</tt> if not present
	 */
	protected abstract String get();

	/**
	 * @return Value as a string
	 */
	public String toText() {
		return toValue(null, Converter.STRING);
	}

	/**
	 * @param def Default value
	 * @return Value as a string
	 */
	public String toText(String def) {
		return toValue(def, Converter.STRING);
	}

	/**
	 * @return Value as an integer
	 */
	public int toInteger() {
		return toValue(null, Converter.INTEGER);
	}

	/**
	 * @param def Default value
	 * @return Value as an integer
	 */
	public int toInteger(int def) {
		return toValue(def, Converter.INTEGER);
	}

	/**
	 * @return Value as a long
	 */
	public long toLong() {
		return toValue(null, Converter.LONG);
	}

	/**
	 * @param def Default value
	 * @return Value as a long
	 */
	public long toLong(long def) {
		return toValue(def, Converter.LONG);
	}

	/**
	 * @return Value as a floating-point number
	 */
	public float toFloat() {
		return toValue(null, Converter.FLOAT);
	}

	/**
	 * @param def Default value
	 * @return Value as a floating-point number
	 */
	public float toFloat(float def) {
		return toValue(def, Converter.FLOAT);
	}

	/**
	 * @return Value as a boolean
	 */
	public boolean toBoolean() {
		return toValue(null, Converter.BOOLEAN);
	}

	/**
	 * @param def Default value
	 * @return Value as a boolean
	 */
	public boolean toBoolean(boolean def) {
		return toValue(def, Converter.BOOLEAN);
	}

	/**
	 * Applies the given converter to this value.
	 * @param converter Converter
	 * @return Converted value
	 */
	public <T> T toValue(Converter<T> converter) {
		return toValue(null, converter);
	}

	/**
	 * Applies the given converter to this value.
	 * @param def			Default value or <tt>null</tt> if mandatory
	 * @param converter		Converter
	 * @return Converted value
	 * @throws NumberFormatException if the value cannot be converted, or it is mandatory and not present
	 */
	public <T> T toValue(T def, Converter<T> converter) {
		final String value = get();
		if(value == null) {
			if(def == null) {
				throw new NumberFormatException("Expected mandatory entry: " + this);
			}
			else {
				return def;
			}
		}
		else {
			return converter.apply(value);
		}
	}
}
