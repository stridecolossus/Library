package org.sarge.lib.util;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import java.util.stream.Stream;

import javax.lang.model.element.Modifier;

import org.junit.Test;

public class UtilTest {
	@Test
	public void isEmptyString() {
		assertEquals(true, Util.isEmpty((String) null));
		assertEquals(true, Util.isEmpty(""));
		assertEquals(false, Util.isEmpty("string"));
	}

	@Test
	public void isEmptyCollection() {
		assertEquals(true, Util.isEmpty((Collection<String>) null));
		assertEquals(true, Util.isEmpty(new ArrayList<String>()));
		assertEquals(false, Util.isEmpty(Collections.singleton("item")));
	}

	@Test
	public void hasDuplicates() {
		assertTrue(Util.hasDuplicates(Arrays.asList(42, 42)));
		assertFalse(Util.hasDuplicates(Arrays.asList(1, 2)));
	}

	@Test
	public void appendFileSeparator() {
		final String expected = "path" + File.separator;
		assertEquals(expected, Util.appendFileSeparator("path"));
		assertEquals(expected, Util.appendFileSeparator("path" + File.separator));
	}
	
	@Test
	public void wrap() {
		assertEquals("LwordR", Util.wrap("word", "L", "R"));
	}

	@Test
	public void getEnumConstant() {
		assertEquals(Modifier.FINAL, Util.getEnumConstant("final", Modifier.class));
	}
	
	@Test(expected=NumberFormatException.class)
	public void getEnumConstantInvalid() {
		Util.getEnumConstant("cobblers", Modifier.class);
	}
	
	@Test
	public void notPredicate() {
		final Predicate<Object> p = obj -> true;
		assertEquals(true, p.test(null));
		assertEquals(false, Util.not(p).test(null));
	}
	
	@Test
	public void iteratorToStream() {
		final Iterator<Integer> itr = Arrays.asList(1, 2).iterator();
		final Stream<Integer> str = Util.toStream(itr);
		assertNotNull(str);
		assertArrayEquals(new Integer[]{1, 2}, str.toArray());
	}
	
	@Test
	public void concat() {
		final List<Integer> first = Arrays.asList(1, 2, 3);
		final List<Integer> second = Arrays.asList(4, 5);
		assertEquals(Arrays.asList(1, 2, 3, 4, 5), Util.concat(first, second));
	}
	
	@Test
	public void findOnly() {
		assertEquals(Optional.of(42), Util.findOnly(Stream.of(42)));
	}

	@Test
	public void findOnlyEmptyStream() {
		assertEquals(Optional.empty(), Util.findOnly(Stream.of()));
	}

	@Test
	public void findOnlyMultipleElements() {
		assertEquals(Optional.empty(), Util.findOnly(Stream.of(1, 2, 3)));
	}

	@Test
	public void compoundPredicate() {
		final Predicate<String> equals = str -> str.equals("one");
		final Predicate<String> contains = str -> str.contains("one");
		final Predicate<String> compound = Util.compoundPredicate(Arrays.asList(equals, contains), Predicate::and, true);
		assertEquals(true, compound.test("one"));
		assertEquals(false, compound.test("cobblers"));
	}
	
	@Test
	public void zip() {
		final Stream<Integer> left = Stream.of(1, 2);
		final Stream<String> right = Stream.of("one", "two");
		final BiFunction<Integer, String, String> zipper = (a, b) -> a + "," + b;
		final Stream<String> stream = Util.zip(left, right, zipper);
		assertNotNull(stream);
		assertArrayEquals(new String[]{"1,one", "2,two"}, stream.toArray());
	}
	
	@Test
	public void toMap() {
		final Collection<Integer> c = Collections.singleton(42);
		final Map<String, Integer> map = Util.toMap(c, i -> i.toString());
		assertNotNull(map);
		assertEquals(1, map.size());
		assertEquals(new Integer(42), map.get("42"));
	}
}
