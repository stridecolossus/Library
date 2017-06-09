package org.sarge.lib.util;

import java.io.File;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * General utilities.
 * @author Sarge
 */
public final class Util {
	public static final String EMPTY_STRING = "";
	
	private static final Logger log = Logger.getLogger(Util.class.getName());

	private static Map<Class<?>, Class<?>> primitives = new HashMap<>();
	private static Map<Class<?>, Class<?>> wrappers = new HashMap<>();

	private static void add(Class<?> clazz, Class<?> wrapper) {
		wrappers.put(clazz, wrapper);
		primitives.put(wrapper, clazz);
	}

	static {
		add(float.class, Float.class);
		add(int.class, Integer.class);
		add(long.class, Long.class);
		add(boolean.class, Boolean.class);
	}

	private Util() {
		// Utilities class
	}

	/**
	 * @param str String to test
	 * @return Whether the given string is <tt>null</tt> or empty
	 */
	public static boolean isEmpty(String str) {
		return (str == null) || (str.length() == 0);
	}

	/**
	 * @param c Collection to test
	 * @return Whether the given collection is <tt>null</tt> or empty
	 */
	public static boolean isEmpty(Collection<?> c) {
		return (c == null) || c.isEmpty();
	}

	/**
	 * @param c Collection to test
	 * @return Whether the given collection contains any duplicate elements
	 */
	public static boolean hasDuplicates(Collection<?> c) {
		final Set<?> set = new HashSet<>(c);
		return set.size() != c.size();
	}

	/**
	 * Appends a file separator to the given string.
	 * @param path Path
	 * @return Path with appended file separator
	 */
	public static String appendFileSeparator(String path) {
		if(path.endsWith(File.separator)) {
			return path;
		}
		else {
			return path + File.separator;
		}
	}

	/**
	 * Wraps a string.
	 * @param str		String
	 * @param left		Left-hand token
	 * @param right		Right-hand token
	 * @return Wrapped string
	 */
	public static String wrap(String str, String left, String right) {
		final StringBuilder sb = new StringBuilder();
		sb.append(left);
		sb.append(str);
		sb.append(right);
		return sb.toString();
	}

	/**
	 * Wraps a string.
	 * @param str		String
	 * @param wrap		Wrapping token
	 * @return Wrapped string
	 */
	public static String wrap(String str, String wrap) {
		return wrap(str, wrap, wrap);
	}
	
	/**
	 * Looks up the enum constant with the specified name.
	 * @param name			Enum constant name
	 * @param clazz			Enum class
	 * @return Enum constant
	 * @param <E> Enumeration
	 * @throws NumberFormatException if the constant is invalid
	 */
	public static <E extends Enum<E>> E getEnumConstant(String name, Class<E> clazz) throws NumberFormatException {
		return getEnumConstant(name, clazz, () -> new NumberFormatException("Unknown enum constant: " + name));
	}
	
	/**
	 * Looks up the enum constant with the specified name or throw an exception of the given type.
	 * @param name			Enum constant name
	 * @param clazz			Enum class
	 * @param exception		Exception supplier
	 * @return Enum constant
	 * @param <E> Enumeration
	 * @param <X> Exception
	 */
	public static <E extends Enum<E>, X extends RuntimeException> E getEnumConstant(String name, Class<E> clazz, Supplier<? extends X> exception) throws X {
		final String str = name.trim().toUpperCase().replace("-", "_");
		return Arrays.stream(clazz.getEnumConstants()).filter(e -> e.name().equals(str)).findFirst().orElseThrow(exception);
	}

	/**
	 * Concatenates a list of lists.
	 * @param lists Lists to concatenate
	 * @return Concatenated list
	 */
	@SafeVarargs
	public static <T> List<T> concat(List<T>... lists) {
		final List<T> result = new ArrayList<T>();
		for(List<T> list : lists) {
			result.addAll(list);
		}
		return result;
	}

	/**
	 * Sleeps the current thread.
	 * @param time Duration
	 */
	public static void kip(long time) {
		try {
			Thread.sleep(time);
		} catch(InterruptedException e) {
			log.log(Level.WARNING, "Sleep interrupted", e);
		}
	}

	/**
	 * Maps primitive types to the corresponding wrapper class.
	 * @param type Primitive type
	 * @return Wrapper type
	 */
	public static Class<?> toWrapper(Class<?> type) {
		return wrappers.get(type);
	}

	/**
	 * Maps wrapper types to primitives.
	 * @param wrapper Wrapper type
	 * @return Primitive type
	 */
	public static Class<?> toPrimitive(Class<?> wrapper) {
		return wrappers.get(wrapper);
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
	 * Creates a finite stream from the given iterator.
	 * @param itr Iterator
	 * @return Stream
	 */
	public static <T> Stream<T> toStream(Iterator<T> itr) {
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
