package org.sarge.lib.object;

import static org.sarge.lib.util.Check.notNull;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.function.Consumer;

/**
 * To-string builder.
 * @author Sarge
 */
public class ToString {
	private static final char QUOTE = '\"';

	/**
	 * Builds a string representation of the given object using reflection.
	 * @param obj Object to be converted
	 * @return To-string
	 */
	public static String toString(Object obj) {
		final ToString ts = new ToString(obj);
		final Consumer<Field> append = field -> {
			final Object value = ReflectionUtils.getValue(field, obj);
			ts.append(field.getName(), value);
		};
		ReflectionUtils.getMembers(obj.getClass()).forEach(append);
		return ts.toString();
	}

	/**
	 * Builds a simple comma-delimited string representation of the given floating-point array.
	 * @param array Array of floating-point values
	 * @return Comma-delimited string
	 */
	public static String toString(float... array) {
		return Arrays.toString(array);
	}

	private final Object obj;
	private final StringBuilder values = new StringBuilder();

	/**
	 * Constructor.
	 * @param obj Object to be converted
	 */
	public ToString(Object obj) {
		this.obj = notNull(obj);
	}

	/**
	 * Adds commas between fields.
	 */
	private void appendDelimiter() {
		if(values.length() > 0) {
			values.append(", ");
		}
	}

	/**
	 * Adds a field name and value
	 * @param name		Field name
	 * @param value		Value
	 */
	public void append(String name, Object value) {
		appendDelimiter();
		values.append(name);
		values.append('=');
		appendValue(value);
	}

	/**
	 * Adds a field value
	 * @param value Value
	 */
	public void append(Object value) {
		appendDelimiter();
		appendValue(value);
	}

	/**
	 * Adds a field value.
	 * @param value Value to add
	 */
	private void appendValue(Object value) {
		final Object converted = convert(value);
		values.append(converted);
	}

	/**
	 * Converts the given field value to its string representation.
	 * @param value Field value
	 * @return Converted object
	 */
	private Object convert(Object value) {
		if(value == null) {
			return "NULL";
		}
		else
		if(value == obj) {
			return "SELF";
		}
		else
		if(value instanceof String) {
			final StringBuilder sb = new StringBuilder();
			sb.append(QUOTE);
			sb.append(value);
			sb.append(QUOTE);
			return sb.toString();
		}
		else {
			return value;
		}
	}

	@Override
	public String toString() {
		// Start with class-name
		final StringBuilder sb = new StringBuilder();
		final Class<?> clazz = obj.getClass();
		sb.append(clazz.getSimpleName());

		// Add field-values
		sb.append('[');
		sb.append(values.toString());
		sb.append(']');

		// Convert to string
		return sb.toString();
	}
}
