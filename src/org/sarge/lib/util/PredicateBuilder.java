package org.sarge.lib.util;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BinaryOperator;
import java.util.function.Predicate;

/**
 * Builder for compound predicates.
 * @author Sarge
 */
public class PredicateBuilder<T> {
	private final List<Predicate<T>> predicates = new ArrayList<>();
	
	public void add(Predicate<T> p) {
		predicates.add(p);
	}
	
	public Predicate<T> build(boolean identity, BinaryOperator<Predicate<T>> op) {
		return predicates.stream().reduce(t -> identity, op);
	}
	
	public Predicate<T> build() {
		return build(true, Predicate::and);
	}
}
