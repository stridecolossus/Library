package org.sarge.lib.collection;

import java.util.Map;
import java.util.function.BiFunction;
import java.util.stream.Collector;
import java.util.stream.Collectors;

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
		if(obj == null) return false;
		if(obj == this) return true;
		if(obj instanceof Pair) {
			// TODO - how to ensure has same type?
			@SuppressWarnings("unchecked")
			final Pair<L, R> that = (Pair<L, R>) obj;
			return this.left.equals(that.left) && this.right.equals(that.right);
		}
		else {
			return false;
		}
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
