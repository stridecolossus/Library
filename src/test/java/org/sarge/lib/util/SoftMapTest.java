package org.sarge.lib.util;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Set;

import org.junit.jupiter.api.*;

public class SoftMapTest {
	private SoftMap<Integer, String> map;

	@BeforeEach
	public void before() {
		map = new SoftMap<>(1);
	}

	@Test
	public void constructor() {
		assertEquals(0, map.size());
		assertEquals(true, map.isEmpty());
		assertNotNull(map.keySet());
		assertNotNull(map.values());
		assertNotNull(map.entrySet());
		assertEquals(true, map.keySet().isEmpty());
		assertEquals(true, map.values().isEmpty());
		assertEquals(true, map.entrySet().isEmpty());
	}

	@Test
	public void put() {
		map.put(1, "one");
		assertEquals(1, map.size());
		assertEquals(false, map.isEmpty());
		assertEquals("one", map.get(1));
		assertEquals(true, map.containsKey(1));
		assertEquals(true, map.containsValue("one"));
		assertEquals(Set.of(1), map.keySet());
		assertEquals(Set.of("one"), map.values());
		assertEquals(Integer.valueOf(1), map.entrySet().iterator().next().getKey());
		assertEquals("one", map.entrySet().iterator().next().getValue());
	}

	@Test
	public void garbageCollection() {
		// TODO
	}
}
