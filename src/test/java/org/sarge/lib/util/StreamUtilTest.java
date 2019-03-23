package org.sarge.lib.util;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

public class StreamUtilTest {
	@Test
	public void notPredicate() {
		final Predicate<Object> p = obj -> true;
		assertEquals(true, p.test(null));
		assertEquals(false, StreamUtil.not(p).test(null));
	}

	@Test
	public void iteratorToStream() {
		final var itr = Arrays.asList(1, 2).iterator();
		final var str = StreamUtil.toStream(itr);
		assertNotNull(str);
		assertArrayEquals(new Integer[]{1, 2}, str.toArray());
	}

	@Test
	public void select() {
		final var c = Arrays.asList(Integer.valueOf(1), Float.valueOf(2f));
		assertEquals(1, StreamUtil.select(Integer.class, c.stream()).count());
		assertEquals(1, StreamUtil.select(Float.class, c.stream()).count());
		assertEquals(2, StreamUtil.select(Number.class, c.stream()).count());
		assertEquals(0, StreamUtil.select(Long.class, c.stream()).count());
		assertEquals(Integer.valueOf(1), StreamUtil.select(Integer.class, c.stream()).iterator().next());
	}

	@Test
	public void findOnly() {
		assertEquals(Optional.of(42), StreamUtil.findOnly(Stream.of(42)));
	}

	@Test
	public void findOnlyEmptyStream() {
		assertEquals(Optional.empty(), StreamUtil.findOnly(Stream.of()));
	}

	@Test
	public void findOnlyMultipleElements() {
		assertEquals(Optional.empty(), StreamUtil.findOnly(Stream.of(1, 2, 3)));
	}

	@Test
	public void compoundPredicate() {
		final Predicate<String> equals = str -> str.equals("one");
		final Predicate<String> contains = str -> str.contains("one");
		final Predicate<String> compound = StreamUtil.compoundPredicate(Arrays.asList(equals, contains), Predicate::and, true);
		assertEquals(true, compound.test("one"));
		assertEquals(false, compound.test("cobblers"));
	}

	@Test
	public void zip() {
		final Stream<Integer> left = Stream.of(1, 2);
		final Stream<String> right = Stream.of("one", "two");
		final BiFunction<Integer, String, String> zipper = (a, b) -> a + "," + b;
		final Stream<String> stream = StreamUtil.zip(left, right, zipper);
		assertNotNull(stream);
		assertArrayEquals(new String[]{"1,one", "2,two"}, stream.toArray());
	}

	@Test
	public void toMap() {
		final Collection<Integer> c = Collections.singleton(42);
		final Map<String, Integer> map = StreamUtil.toMap(c, i -> i.toString());
		assertNotNull(map);
		assertEquals(1, map.size());
		assertEquals(Integer.valueOf(42), map.get("42"));
	}
}
