package org.sarge.lib.util;

import java.util.function.Function;
import java.util.stream.Stream;

/**
 * General utility methods and helpers.
 * @author Sarge
 */
public final class Utility {
	private Utility() {
	}

	/**
	 * Flattens a tree of objects.
	 * @param <T> Type
	 * @param obj			Starting object
	 * @param mapper		Sub-tree mapping function
	 * @return Flattened stream
	 */
	public static <T> Stream<T> flatten(T obj, Function<T, Stream<T>> mapper) {
		return Stream.concat(Stream.of(obj), mapper.apply(obj).flatMap(e -> flatten(e, mapper)));
	}
}
