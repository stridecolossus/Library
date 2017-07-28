package org.sarge.lib.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.Collections;
import java.util.stream.Stream;

import org.junit.Before;
import org.junit.Test;
import org.sarge.lib.util.Cache.Entry;
import org.sarge.lib.util.Cache.EvictionPolicy;

public class CacheTest {
    private class MockLoader implements Cache.Loader<Integer, String> {
        private int count;
        
        @Override
        public String load(Integer key) {
            ++count;
            switch(key) {
                case -1:
                    // Simulates a loading error
                    throw new RuntimeException();
                    
                case -2:
                    // Simulates an unknown key
                    return null;
                    
                case -3:
                    // Loading time
                    Util.kip(50);
                    return null;
                
                default:
                    // Mock loader
                    return String.valueOf(key);
            }
        }
    }

    private Cache<Integer, String> cache;
    private MockLoader loader;
    private Cache.EvictionPolicy policy;
    private Cache.Statistics stats;
    
    @Before
    public void before() {
        // Create an eviction policy that limits size to one entry
        policy = new EvictionPolicy() {            
            @Override
            public boolean isFull(Entry<?> entry, Cache<?, ?> cache) {
                return cache.size() > 0;
            }
            
            @Override
            public Stream<Object> getEvictionCandidates() {
                return cache.keySet().stream().map(k -> k);
            }
        };
        
        // Create cache
        loader = new MockLoader();
        cache = new Cache<>(loader, policy);
        stats = cache.statistics();
    }
    
    @Test
    public void constructor() {
        assertEquals(0, cache.size());
        assertNotNull(cache.keySet().size());
        assertEquals(0, cache.keySet().size());
    }
    
    @Test
    public void stats() {
        assertNotNull(stats);
        assertEquals(0, stats.hitCount());
        assertEquals(0, stats.missCount());
        assertEquals(0, stats.errorCount());
        assertEquals(0, stats.evictionCount());
        assertEquals(0, stats.loadingTime());
        assertEquals(0, stats.maxSize());
    }
    
    @Test
    public void getMiss() {
        assertEquals("1", cache.get(1));
        assertEquals(1, loader.count);
        assertEquals(0, stats.hitCount());
        assertEquals(1, stats.missCount());
        assertEquals(0, stats.errorCount());
        assertEquals(0, stats.evictionCount());
        assertEquals(1, stats.maxSize());
    }
    
    @Test
    public void getHit() {
        cache.add(1);
        assertEquals("1", cache.get(1));
        assertEquals(1, stats.hitCount());
        assertEquals(0, stats.missCount());
        assertEquals(0, stats.errorCount());
        assertEquals(0, stats.evictionCount());
        assertEquals(1, stats.maxSize());
    }
    
    @Test
    public void getLoadingError() {
        cache.get(-1);
        assertEquals(0, stats.hitCount());
        assertEquals(1, stats.missCount());
        assertEquals(1, stats.errorCount());
        assertEquals(0, stats.evictionCount());
        assertEquals(0, stats.maxSize());
    }
    
    @Test
    public void getUnknownValue() {
        cache.get(-2);
        assertEquals(0, stats.hitCount());
        assertEquals(1, stats.missCount());
        assertEquals(1, stats.errorCount());
        assertEquals(0, stats.evictionCount());
        assertEquals(0, stats.maxSize());
    }
    
    @Test
    public void loadingTime() {
        cache.load(-3);
        assertTrue(stats.loadingTime() > 0);
    }
    
    @Test
    public void eviction() {
        cache.get(1);
        cache.get(2);
        assertEquals(1, cache.size());
        assertEquals(1, stats.evictionCount());
        assertEquals(1, stats.maxSize());
    }
    
    @Test
    public void keySet() {
        cache.add(1);
        assertEquals(Collections.singleton(1), cache.keySet());
    }
    
    @Test
    public void add() {
        cache.add(1);
        assertEquals("1", cache.get(1));
        assertEquals(1, loader.count);
        assertEquals(1, stats.hitCount());
        assertEquals(0, stats.missCount());
        assertEquals(0, stats.errorCount());
    }

    @Test(expected = IllegalArgumentException.class)
    public void addNullKey() {
        cache.add((Integer) null);
    }
    
    @Test
    public void remove() {
        cache.add(1);
        cache.remove(1);
        assertEquals(0, cache.size());
    }
    
    @Test
    public void clear() {
        cache.add(1);
        cache.clear();
        assertEquals(0, cache.size());
    }
    
    @Test
    public void listener() {
        // Add a listener
        final Cache.Listener listener = mock(Cache.Listener.class);
        cache.add(listener);
        
        // Load an entry
        cache.get(1);
        verify(listener).entry(Cache.Listener.Type.ADDED, 1);
        
        // Load another entry that should evict the first
        cache.get(2);
        verify(listener).entry(Cache.Listener.Type.EVICTED, 1);
        verify(listener).entry(Cache.Listener.Type.ADDED, 2);
    }
}
