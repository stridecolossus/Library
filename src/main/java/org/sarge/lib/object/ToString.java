package org.sarge.lib.object;

import java.util.Arrays;

import org.sarge.lib.util.Check;

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
		ReflectionUtils.getMembers(obj.getClass()).forEach(field -> {
			final Object value = ReflectionUtils.getValue(field, obj);
			final String str;
			if(value == obj) {
				str = "SELF";
			}
			else
			if(value == null) {
				str = "NULL";
			}
			else {
				str = value.toString();
			}
			ts.append(field.getName(), str);
		});
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
		Check.notNull(obj);
		this.obj = obj;
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
	 * Adds commas between fields.
	 */
	private void appendDelimiter() {
		if(values.length() > 0) {
			values.append(", ");
		}
	}

	/**
	 * Adds a field value.
	 * @param value Value to add
	 */
	private void appendValue(Object value) {
		if(value == null) {
			values.append("null");
		}
		else if(value instanceof String) {
			values.append(QUOTE);
			values.append(value.toString());
			values.append(QUOTE);
		}
		else {
			values.append(value.toString());
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
