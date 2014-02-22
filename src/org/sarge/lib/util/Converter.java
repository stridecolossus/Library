package org.sarge.lib.util;

/**
 * Defines a method of converting a string to a data-type.
 * @author Sarge
 * @param <T> Data-type
 */
public interface Converter<T> {
	/**
	 * Converts the given string to this data-type.
	 * @param str String representation
	 * @return Converted value
	 * @throws NumberFormatException if the value cannot be converted
	 */
	T convert( String str ) throws NumberFormatException;

	/**
	 * String-to-string conversion, i.e. does nothing.
	 */
	Converter<String> STRING = new Converter<String>() {
		@Override
		public String convert( String str ) {
			return str;
		}
	};

	/**
	 * Converts to an integer.
	 */
	Converter<Integer> INTEGER = new Converter<Integer>() {
		@Override
		public Integer convert( String str ) throws NumberFormatException {
			return Integer.parseInt( str );
		}
	};

	/**
	 * Converts to a long.
	 */
	Converter<Long> LONG = new Converter<Long>() {
		@Override
		public Long convert( String str ) throws NumberFormatException {
			return Long.parseLong( str );
		}
	};


	/**
	 * Converts to a floating-point value.
	 */
	Converter<Float> FLOAT = new Converter<Float>() {
		@Override
		public Float convert( String str ) throws NumberFormatException {
			return Float.parseFloat( str );
		}
	};

	/**
	 * Converts to a boolean value.
	 */
	Converter<Boolean> BOOLEAN = new Converter<Boolean>() {
		@Override
		public Boolean convert( String str ) throws NumberFormatException {
			return Boolean.parseBoolean( str );
		}
	};
}
