package org.sarge.lib.util;

import static java.util.stream.Collectors.toMap;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
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
	 * Creates a map from a collection ordered by the given key extractor.
	 * @param <K> Key type
	 * @param <V> Value type
	 * @param values 	Values
	 * @param mapper 	Key extractor
	 * @return New map
	 * @throws IllegalStateException for a duplicate key
	 */
	public static <K, V> Map<K, V> map(Collection<V> values, Function<V, K> mapper) {
		return values.stream().collect(toMap(mapper, Function.identity()));
	}

	/**
	 * Creates a map from an array ordered by the given key extractor.
	 * @param <K> Key type
	 * @param <V> Value type
	 * @param array		Array
	 * @param mapper 	Key extractor
	 * @return New map
	 */
	public static <K, V> Map<K, V> map(V[] array, Function<V, K> mapper) {
		return map(Arrays.asList(array), mapper);
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
