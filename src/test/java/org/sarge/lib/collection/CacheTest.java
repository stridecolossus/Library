package org.sarge.lib.collection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.sarge.lib.collection.Cache.Constraint;
import org.sarge.lib.collection.Cache.EvictionPolicy;
import org.sarge.lib.collection.Cache.Factory;

public class CacheTest {
	private static final int KEY = 42;
	private static final String VALUE = "value";

	private Cache<Integer, String> cache;
	private Factory<Integer, String> factory;

	@SuppressWarnings("unchecked")
	@BeforeEach
	public void before() {
		factory = mock(Factory.class);
		cache = new Cache.Builder<Integer, String>()
			.factory(factory)
			.constraint(Constraint.size(2))
			.policy(EvictionPolicy.LEAST_FREQUENTLY_USED)
			.build();
	}

	@Test
	public void constructor() {
		assertNotNull(cache);
		assertEquals(0, cache.size());
		assertEquals(0, cache.statistics().weight());
	}

	@Test
	public void add() {
		cache.add(KEY, VALUE);
		assertEquals(1, cache.size());
		assertEquals(true, cache.contains(KEY));
		assertEquals(VALUE, cache.get(KEY));
	}

	@Test
	public void addDuplicateKey() {
		cache.add(KEY, VALUE);
		assertThrows(IllegalArgumentException.class, () -> cache.add(KEY, VALUE));
	}

	@Test
	public void factory() {
		when(factory.get(KEY)).thenReturn(VALUE);
		assertEquals(VALUE, cache.get(KEY));
		verify(factory).get(KEY);
		assertEquals(1, cache.statistics().miss());
		assertEquals(1, cache.statistics().supplied());
	}

	@Test
	public void weight() {
		when(factory.get(KEY)).thenReturn(VALUE);
		when(factory.weight(VALUE)).thenReturn(42);
		cache.get(KEY);
		assertEquals(42, cache.statistics().weight());
	}

	@Test
	public void eviction() {
		// Add some entries
		cache.add(1, VALUE);
		cache.add(2, VALUE);
		cache.get(2);

		// Load another entry
		when(factory.get(KEY)).thenReturn(VALUE);
		cache.add(KEY);

		// Check stale entry removed
		assertEquals(1, cache.statistics().evictions());
		assertEquals(false, cache.contains(1));
		assertEquals(true, cache.contains(2));
		assertEquals(true, cache.contains(KEY));
	}

	@Test
	public void get() {
		cache.add(KEY, VALUE);
		cache.get(KEY);
		assertEquals(1, cache.statistics().hit());
	}

	@Test
	public void getMiss() {
		assertEquals(null, cache.get(KEY));
		assertEquals(1, cache.statistics().miss());
		assertEquals(1, cache.statistics().errors());
		assertEquals(0, cache.statistics().supplied());
	}

	@Test
	public void remove() {
		cache.add(KEY, VALUE);
		cache.remove(KEY);
		assertEquals(0, cache.size());
		assertEquals(null, cache.get(KEY));
	}

	@Test
	public void removeNotPresent() {
		assertThrows(IllegalArgumentException.class, () -> cache.remove(KEY));
	}

	@Test
	public void clear() {
		cache.add(KEY, VALUE);
		cache.clear();
		assertEquals(0, cache.size());
		assertEquals(0, cache.statistics().weight());
		assertEquals(null, cache.get(KEY));
	}
}
