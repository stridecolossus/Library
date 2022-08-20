package org.sarge.lib.util;

import static java.util.stream.Collectors.toMap;

import java.util.*;
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
	 * Converts a collection to a map with values indexed by the given key extractor.
	 * @param <K> Key type
	 * @param <V> Value type
	 * @param values		Values
	 * @param key			Key extractor
	 * @return Collection as a map
	 * @throws IllegalStateException for any duplicates
	 */
	public static <K, V> Map<K, V> map(Collection<V> values, Function<V, K> key) {
		return values.stream().collect(toMap(key, Function.identity()));
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

	/**
	 * Tests whether the given collection contains <i>distinct</i> elements, i.e. no duplicates.
	 * @param collection Collection
	 * @return Whether the collection elements are distinct
	 */
	public static boolean distinct(Collection<?> collection) {
		final long count = collection.stream().distinct().count();
		return count == collection.size();
	}
}
