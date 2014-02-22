package org.sarge.lib.util;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

/**
 * Reflection-based utilities.
 * @author Sarge
 */
public class ReflectionUtils {
	private ReflectionUtils() {
		// Utility class
	}

	/**
	 * Lists the member fields of the given class and its super-classes (omitting constants and volatile fields).
	 * @param clazz Class
	 * @return List of member fields
	 */
	public static List<Field> getMemberFields( Class<?> clazz ) {
		final List<Field> fields = new ArrayList<>();
		Class<?> c = clazz;
		do {
			// Enumerate fields of current class
			for( Field f : c.getDeclaredFields() ) {
				// Skip constants
				final int mods = f.getModifiers();
				if( Modifier.isStatic( mods ) ) continue;
				if( Modifier.isVolatile( mods ) ) continue;

				// Ensure field can be accessed
				if( !f.isAccessible() ) f.setAccessible( true );

				// Add member field
				fields.add( f );
			}

			// Move to base-class
			c = c.getSuperclass();
		}
		while( c != null );

		return fields;
	}

	/**
	 * Lists the class members of the given object (omitting constants and volatile values).
	 * @param obj Object
	 * @return Class member values
	 */
	public static List<Object> getMemberValues( Object obj ) {
		Check.notNull( obj );

		final List<Object> values = new ArrayList<>();
		try {
			for( Field f : getMemberFields( obj.getClass() ) ) {
				final Object value = f.get( obj );
				values.add( value );
			}
		}
		catch( IllegalAccessException e ) {
			throw new RuntimeException( "Error accessing field", e );
		}

		return values;
	}
}
