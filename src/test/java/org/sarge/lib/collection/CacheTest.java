package org.sarge.lib.collection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;
import org.sarge.lib.collection.Cache.Entry;
import org.sarge.lib.collection.Cache.EvictionPolicy;
import org.sarge.lib.collection.Cache.Limit;
import org.sarge.lib.collection.Cache.Listener;
import org.sarge.lib.collection.Cache.Listener.Type;
import org.sarge.lib.collection.Cache.Loader;
import org.sarge.lib.collection.Cache.Statistics;

public class CacheTest {
	private Cache<Integer, String> cache;
	private Loader<Integer, String> loader;
	private Statistics stats;

	@SuppressWarnings("unchecked")
	@Before
	public void before() {
		loader = mock(Loader.class);
		cache = new Cache.Builder<Integer, String>()
			.cache(HashMap::new)
			.loader(loader)
			.limit(Limit.size(1))
			.weigher(value -> Integer.parseInt(value))
			.policy(EvictionPolicy.WEIGHT)
			.build();
		stats = cache.statistics();
		when(loader.load(1)).thenReturn("1");
	}

	@Test
	public void constructor() {
		assertNotNull(stats);
		assertEquals(0, cache.size());
		assertEquals(0, stats.maxSize());
		assertEquals(0, stats.hitCount());
		assertEquals(0, stats.missCount());
		assertEquals(0, stats.errorCount());
		assertEquals(0, stats.evictionCount());
		assertEquals(0L, stats.loadingTime());
		assertEquals(0, stats.weight());
	}

	@Test
	public void get() {
		assertEquals("1", cache.get(1));
		verify(loader).load(1);
		assertEquals(1, cache.size());
		assertEquals(1, stats.maxSize());
		assertEquals(0, stats.hitCount());
		assertEquals(1, stats.missCount());
		assertEquals(0, stats.errorCount());
		assertEquals(0, stats.evictionCount());
		assertEquals(1, stats.weight());
		assertTrue(stats.loadingTime() > 0);
	}

	@Test
	public void getCached() {
		assertEquals("1", cache.get(1));
		assertEquals("1", cache.get(1));
		verify(loader, times(1)).load(1);
		assertEquals(1, cache.size());
		assertEquals(1, stats.maxSize());
		assertEquals(1, stats.hitCount());
		assertEquals(1, stats.missCount());
		assertEquals(0, stats.errorCount());
		assertEquals(0, stats.evictionCount());
		assertEquals(1, stats.weight());
	}

	@Test
	public void getNullValue() {
		when(loader.load(anyInt())).thenReturn(null);
		assertEquals(null, cache.get(1));
		assertEquals(0, cache.size());
		assertEquals(0, stats.maxSize());
		assertEquals(0, stats.hitCount());
		assertEquals(1, stats.missCount());
		assertEquals(1, stats.errorCount());
		assertEquals(0, stats.evictionCount());
		assertEquals(0, stats.weight());
	}

	@Test
	public void add() {
		cache.add(2, "2");
		assertEquals("2", cache.get(2));
		assertEquals(1, cache.size());
		assertEquals(1, stats.maxSize());
		assertEquals(1, stats.hitCount());
		assertEquals(0, stats.missCount());
		assertEquals(0, stats.errorCount());
		assertEquals(0, stats.evictionCount());
		assertEquals(2, stats.weight());
	}

	@Test
	public void remove() {
		cache.add(1, "1");
		cache.remove(1);
		assertEquals(0, cache.size());
		assertEquals(1, stats.maxSize());
		assertEquals(0, stats.hitCount());
		assertEquals(0, stats.missCount());
		assertEquals(0, stats.errorCount());
		assertEquals(0, stats.evictionCount());
		assertEquals(0, stats.weight());
	}

	@Test
	public void clear() {
		cache.add(1, "1");
		cache.clear();
		assertEquals(0, cache.size());
		assertEquals(1, stats.maxSize());
		assertEquals(0, stats.hitCount());
		assertEquals(0, stats.missCount());
		assertEquals(0, stats.errorCount());
		assertEquals(0, stats.evictionCount());
		assertEquals(0, stats.weight());
	}

	@Test
	public void eviction() {
		when(loader.load(anyInt())).thenReturn("42");
		cache.get(1);
		cache.get(2);
		assertEquals(1, cache.size());
		assertEquals(1, stats.maxSize());
		assertEquals(0, stats.hitCount());
		assertEquals(2, stats.missCount());
		assertEquals(0, stats.errorCount());
		assertEquals(1, stats.evictionCount());
	}

	@Test
	public void listener() {
		@SuppressWarnings("unchecked")
		final Listener<Integer> listener = mock(Listener.class);
		cache.add(listener);
		when(loader.load(anyInt())).thenReturn("42");
		cache.get(1);
		cache.get(2);
		verify(listener).entry(Type.ADDED, 1);
		verify(listener).entry(Type.EVICTED, 1);
		verify(listener).entry(Type.ADDED, 2);
		verifyNoMoreInteractions(listener);
	}

	@Test
	public void LRU() {
		final Entry<?> entry = mock(Entry.class);
		when(entry.accessed()).thenReturn(42L);
		assertEquals(42L, EvictionPolicy.LRU.score(entry));
	}

	@Test
	public void LFU() {
		final Entry<?> entry = mock(Entry.class);
		when(entry.count()).thenReturn(42);
		assertEquals(42, EvictionPolicy.LFU.score(entry));
	}

	@Test
	public void weightPolicy() {
		final Entry<?> entry = mock(Entry.class);
		when(entry.weight()).thenReturn(42);
		assertEquals(42, EvictionPolicy.WEIGHT.score(entry));
	}

	@Test
	public void unlimited() {
		assertEquals(false, Limit.UNLIMITED.isFull(cache));
	}

	@Test
	public void sizeLimit() {
		assertEquals(false, Limit.size(1).isFull(cache));
		cache.get(1);
		assertEquals(true, Limit.size(1).isFull(cache));
	}

	@Test
	public void weightLimit() {
		assertEquals(false, Limit.weight(1).isFull(cache));
		cache.get(1);
		assertEquals(true, Limit.weight(1).isFull(cache));
	}
}
