package org.sarge.lib.util;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Stream utilities.
 * @author Sarge
 */
public final class StreamUtil {
	private StreamUtil() {
		// Utilities class
	}

    /**
     * Negates the given predicate (handy for inverting a method reference).
     * @param p Predicate
     * @return Negated predicate
     * @see Predicate#negate()
     */
    public static <T> Predicate<T> not(Predicate<T> p) {
        return p.negate();
    }

    /**
     * Generates a stream from the given iteration.
     * @param seed			Start value
     * @param hasNext		Determines when the stream terminates
     * @param next			Produces the next element
     * @return A new stream
     * @throws IllegalArgumentException if any argument is <tt>null</tt>
     * TODO - replaces with Java 9 version
     */
    public static <T> Stream<T> iterate(T seed, Predicate<? super T> hasNext, UnaryOperator<T> next) {
    	Check.notNull(seed);
    	Check.notNull(hasNext);
    	Check.notNull(next);
    	final Iterator<T> itr = new Iterator<T>() {
    		private T current = seed;
    		
    		@Override
    		public boolean hasNext() {
    			if(hasNext.test(current)) {
    				return true;
    			}
    			else {
    				current = null;
    				return false;
    			}
    		}
    		
    		@Override
    		public T next() {
    			if(current == null) throw new NoSuchElementException();
    			final T result = current;
    			current = next.apply(current);
    			return result;
    		}
		};
		return toStream(itr);
    }

    /**
	 * Creates a finite stream from the given iterator.
	 * @param itr Iterator
	 * @return Stream
	 */
	public static <T> Stream<T> toStream(Iterator<T> itr) {
		Check.notNull(itr);
		final Iterable<T> iterable = () -> itr;
		return StreamSupport.stream(iterable.spliterator(), false);
	}

	/**
	 * Finds a <b>single</b> element from the given stream.
	 * @param stream Stream
	 * @return Single element if present
	 */
	public static <T> Optional<T> findOnly(Stream<T> stream) {
		final Iterator<T> itr = stream.iterator();
		if(itr.hasNext()) {
			final T result = itr.next();
			if(itr.hasNext()) {
				// Multiple elements
				return Optional.empty();
			}
			else {
				// Single element
				return Optional.of(result);
			}
		}
		else {
			// Empty stream
			return Optional.empty();
		}
	}

	/**
	 * Zips two streams.
	 * @param left		Left stream
	 * @param right		Right stream
	 * @param zipper	Function to combine stream elements
	 * @return Zipped stream
	 */
	public static <L, R, T> Stream<T> zip(Stream<L> left, Stream<R> right, BiFunction<L, R, T> zipper) {
		final Iterator<L> a = left.iterator();
		final Iterator<R> b = right.iterator();
		final Iterator<T> itr = new Iterator<T>() {
			@Override
			public boolean hasNext() {
				return a.hasNext() && b.hasNext();
			}

			@Override
			public T next() {
				return zipper.apply(a.next(), b.next());
			}
		};
		final boolean parallel = left.isParallel() && right.isParallel();
		final Iterable<T> iterable = () -> itr;
		return StreamSupport.stream(iterable.spliterator(), parallel);
	}
	
	/**
	 * Creates a compound predicate from the given list using {@link Predicate#and} with a <tt>true</tt> default result.
	 * @param predicates Predicates
	 * @return Compound predicate
	 */
	public static <T> Predicate<T> compoundPredicate(Collection<Predicate<T>> predicates) {
		return compoundPredicate(predicates, Predicate::and, true);
	}
	
	/**
	 * Creates a compound predicate from the given list.
	 * @param predicates	Predicates
	 * @param op			Compound operator
	 * @param def			Default response
	 * @return Compound predicate
	 */
	public static <T> Predicate<T> compoundPredicate(Collection<Predicate<T>> predicates, BinaryOperator<Predicate<T>> op, boolean def) {
		return predicates.stream().reduce(op).orElse(whatever -> def);
	}

	/**
	 * Converts to the given collection to a map.
	 * @param c			Collection
	 * @param mapper	Key-mapper
	 * @return Map of the given values indexed by the specified key-mapper
	 */
	public static <K, V> Map<K, V> toMap(Collection<V> c, Function<V, K> mapper) {
		return c.stream().collect(Collectors.toMap(mapper, Function.identity()));
	}
}
