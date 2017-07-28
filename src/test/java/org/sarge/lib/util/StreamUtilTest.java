package org.sarge.lib.util;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.junit.Test;

public class StreamUtilTest {
	@Test
	public void notPredicate() {
		final Predicate<Object> p = obj -> true;
		assertEquals(true, p.test(null));
		assertEquals(false, StreamUtil.not(p).test(null));
	}
	
	@Test
	public void iteratorToStream() {
		final Iterator<Integer> itr = Arrays.asList(1, 2).iterator();
		final Stream<Integer> str = StreamUtil.toStream(itr);
		assertNotNull(str);
		assertArrayEquals(new Integer[]{1, 2}, str.toArray());
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
		assertEquals(new Integer(42), map.get("42"));
	}
}
