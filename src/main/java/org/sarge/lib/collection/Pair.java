package org.sarge.lib.collection;

import java.util.Map;
import java.util.function.BiFunction;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.sarge.lib.object.EqualsBuilder;
import org.sarge.lib.object.HashCodeBuilder;

/**
 * Pair of objects.
 * @author Sarge
 * @param <T> Type
 */
public class Pair<L, R> {
	private final L left;
	private final R right;

	public Pair(L left, R right) {
		this.left = left;
		this.right = right;
	}

	public L getLeft() {
		return left;
	}

	public R getRight() {
		return right;
	}

	@Override
	public boolean equals(Object obj) {
		return EqualsBuilder.equals(this, obj);
	}
	
	@Override
	public int hashCode() {
		return HashCodeBuilder.hashCode(this);
	}

	@Override
	public String toString() {
		return left + "/" + right;
	}

	/**
	 * Pair zipper.
	 */
	public static <L, R> BiFunction<L, R, Pair<L, R>> zip() {
		return (a, b) -> new Pair<>(a, b);
	}

	/**
	 * Creates a collector for a stream of pairs.
	 * @return Pair collector
	 */
	public static <L, R> Collector<Pair<L, R>, ?, Map<L, R>> toMap() {
		return Collectors.toMap(Pair::getLeft, Pair::getRight);
	}
}
