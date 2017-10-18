package org.sarge.lib.object;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Iterator;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.sarge.lib.util.StreamUtil;

/**
 * Reflection-based utilities.
 * @author Sarge
 */
public final class ReflectionUtils {
	private ReflectionUtils() {
		// Utility class
	}

	private static final Predicate<Field> MEMBER = field -> {
		final int mods = field.getModifiers();
		return !Modifier.isStatic(mods);
	};

	/**
	 * Enumerates the members fields of the given class and its super-classes.
	 * @param clazz Class
	 * @return Member fields
	 */
	public static Stream<Field> getMembers(Class<?> clazz) {
		// TODO - Java9
		final Iterator<Class<?>> itr = new Iterator<Class<?>>() {
			private Class<?> current = clazz;

			@Override
			public Class<?> next() {
				final Class<?> next = current;
				current = current.getSuperclass();
				return next;
			}

			@Override
			public boolean hasNext() {
				return current != null;
			}
		};
		return StreamUtil.toStream(itr)
		//return Stream.<Class<?>>iterate(clazz, Objects::nonNull, Class::getSuperclass)
			.map(Class::getDeclaredFields)
			.flatMap(Arrays::stream)
			.filter(MEMBER);
	}

	/**
	 * Retrieves the value specified by the given field from an object.
	 * @param field		Field
	 * @param obj		Object
	 * @return Value
	 * @throws RuntimeException if the value cannot be retrieved
	 */
	public static Object getValue(Field field, Object obj) {
		try {
			field.setAccessible(true);
			return field.get(obj);
		}
		catch(Exception e) {
			throw new RuntimeException("Error retrieving field: " + field, e);
		}
	}
}
