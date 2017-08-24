package org.sarge.lib.collection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Map;

import org.junit.Test;
import org.sarge.lib.collection.MapBuilder;

public class MapBuilderTest {
	@Test
	public void build() {
		final Map<String, Integer> map = new MapBuilder<String, Integer>().add("one", 1).add("two", 2).build();
		assertEquals(2, map.size());
		assertEquals(new Integer(1), map.get("one"));
		assertEquals(new Integer(2), map.get("two"));
	}
	
	@Test
	public void buildArray() {
		final Map<String, Integer> map = MapBuilder.build("one", 1, "two", 2);
		assertNotNull(map);
		assertEquals(2, map.size());
		assertEquals(new Integer(1), map.get("one"));
		assertEquals(new Integer(2), map.get("two"));
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void buildArrayInvalid() {
		MapBuilder.build("one", 1, "two");
	}
}
