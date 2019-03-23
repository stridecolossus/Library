package org.sarge.lib.collection;

import java.util.Map;
import java.util.function.BiFunction;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Pair of objects.
 * @author Sarge
 * @param <T> Type
 */
public class Pair<L, R> {
	/**
	 * Creates a pair.
	 * @param left		Left-hand object
	 * @param right		Right-hand object
	 * @return New pair
	 */
	public static <L, R> Pair<L, R> of(L left, R right) {
		return new Pair<>(left, right);
	}

	/**
	 * Pair zipper.
	 */
	public static <L, R> BiFunction<L, R, Pair<L, R>> zip() {
		return (a, b) -> new Pair<>(a, b);
	}

	/**
	 * Creates a map collector for a stream of pairs.
	 * @return Pair collector
	 */
	public static <L, R> Collector<Pair<L, R>, ?, Map<L, R>> toMap() {
		return Collectors.toMap(Pair::left, Pair::right);
	}

	private final L left;
	private final R right;

	/**
	 * Constructor.
	 * @param left		Left-hand object
	 * @param right		Right-hand object
	 */
	private Pair(L left, R right) {
		this.left = left;
		this.right = right;
	}

	/**
	 * @return Left-hand object
	 */
	public L left() {
		return left;
	}

	/**
	 * @return Right-hand object
	 */
	public R right() {
		return right;
	}

	@Override
	public boolean equals(Object that) {
		return EqualsBuilder.reflectionEquals(this, that);
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

	@Override
	public String toString() {
		return left + "/" + right;
	}
}
