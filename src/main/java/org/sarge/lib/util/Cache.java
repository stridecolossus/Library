package org.sarge.lib.util;

import static org.sarge.lib.util.Check.notNull;

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

/**
 * In-memory cache.
 * @author Sarge
 * @param <K> Key-type
 * @param <V> Value-type
 */
public class Cache<K, V> {
    /**
     * Loads value that are not present in the cache.
     * @param <K>
     * @param <V>
     */
    @FunctionalInterface
    public interface Loader<K, V> {
        /**
         * Loads a new value for the cache.
         * @param key Key
         * @return Value
         */
        V load(K key);

        /**
         * Calculates the <i>weight</i> of a cached value.
         * @param value Value
         * @return Weight (default is zero)
         */
        default int calculateWeight(V value) {
            return 0;
        }
    }

    /**
     * Caching policy.
     */
    public interface EvictionPolicy {
        /**
         * Tests whether the given entry will exceed the limit of this caching policy.
         * @param entry     Entry to be added
         * @param cache     Cache
         * @return Whether this cache is full
         */
        boolean isFull(Entry<?> entry, Cache<?, ?> cache);

        /**
         * @return Key(s) of the entries to be evicted if this cache is full
         */
        Stream<Object> getEvictionCandidates();

        /**
         * Policy for an unlimited cache (never evicts).
         */
        EvictionPolicy NONE = new EvictionPolicy() {
            @Override
            public boolean isFull(Entry<?> entry, Cache<?, ?> cache) {
                return false;
            }

            @Override
            public Stream<Object> getEvictionCandidates() {
                throw new UnsupportedOperationException();
            }
        };
    }

    /**
     * Listener on cache entry changes.
     */
    public interface Listener {
        /**
         * Event type.
         */
        public enum Type {
            ADDED,
            EVICTED
        }

        /**
         * Notifies a cache entry change.
         * @param type      Type of change
         * @param key       Key
         */
        void entry(Type type, Object key);
    }

    /**
     * Cache entry.
     * @param <V>
     */
    public static class Entry<V> {
        // Cached value
        private V value;
        private final int weight;

        // Entry stats
        private long accessed;
        private long updated;
        private int count;

        /**
         * Constructor.
         * @param value     Cached value
         * @param weight    Weight
         */
        private Entry(V value, int weight) {
            final long now = System.currentTimeMillis();
            this.value = value;
            this.weight = weight;
            this.accessed = now;
            this.updated = now;
        }

        /**
         * @return Number of times this entry has been accessed
         */
        public int count() {
            return count;
        }

        /**
         * @return Weight of this entry
         */
        public int weight() {
            return weight;
        }

        /**
         * @return Most-recent access time of this entry
         */
        public long lastAccessed() {
            return accessed;
        }

        /**
         * @return Most-recent create/update time of this entry
         */
        public long lastUpdated() {
            return accessed;
        }

        /**
         * Updates the last-loaded time of this entry.
         * @param value New cached value
         */
        private void update(V value) {
            this.value = value;
            this.updated = System.currentTimeMillis();
            this.count = 0;
        }

        /**
         * Updates the last-accessed time of this entry.
         */
        private void update() {
            this.accessed = System.currentTimeMillis();
            ++count;
        }

        @Override
        public String toString() {
            // TODO
            return super.toString();
        }
    }

    /**
     * Cache statistics.
     */
    public static final class Statistics {
        private int hits;
        private int misses;
        private int errors;
        private long loadingTime;
        private int evictionCount;
        private int maxSize;

        /**
         * @return Total number of cache hits
         */
        public int hitCount() {
            return hits;
        }

        /**
         * @return Total number of cache misses
         */
        public int missCount() {
            return misses;
        }

        /**
         * @return Total number of loading failures
         * @see Cache#load(Object)
         */
        public int errorCount() {
            return errors;
        }

        /**
         * @return Total number of cache evictions
         */
        public int evictionCount() {
            return evictionCount;
        }

        /**
         * @return Total loading time (ms)
         */
        public long loadingTime() {
            return loadingTime;
        }

        /**
         * @return Maximum cache size
         */
        public int maxSize() {
            return maxSize;
        }

        // TODO - getters, current/max size

        @Override
        public String toString() {
            // TODO
            return "hits=" + hits + " misses=" + misses;
        }
    }

    private final ConcurrentHashMap<K, Entry<V>> cache = new ConcurrentHashMap<>();
    private final Loader<K, V> loader;
    private final EvictionPolicy policy;
    private final Collection<Listener> listeners = new StrictList<>();
    private final Statistics stats = new Statistics();

    /**
     * TODO
     * - refresher - default is none
     * - separate Weigher (default is zero)
     * - builder
     */

    /**
     * Constructor.
     * @param loader Value loader
     * @param policy Eviction policy
     */
    public Cache(Loader<K, V> loader, EvictionPolicy policy) {
        this.loader = notNull(loader);
        this.policy = notNull(policy);
    }

    /**
     * @return Current size of this cache
     */
    public int size() {
        return cache.size();
    }

    /**
     * @return Key-set view
     * TODO - immutable?
     */
    public Set<K> keySet() {
        return cache.keySet();
    }

    /**
     * @return Cache statistics
     */
    public Statistics statistics() {
        return stats;
    }

    /**
     * Broadcasts a cache entry update to all listeners.
     * @param type      Type of change
     * @param entry     Entry
     */
    private void broadcast(Listener.Type type, Object key) {
        for(Listener listener : listeners) {
            listener.entry(type, key);
        }
    }

    /**
     * Retrieves a value from the cache, loading missing values as required.
     * @param key Key
     * @return Value or <tt>null</tt> if not present
     */
    public V get(K key) {
        // Lookup from cache
        if(key == null) throw new IllegalArgumentException("Cache not does support null keys");
        final Entry<V> entry = cache.get(key);

        if(entry == null) {
            // Load if not present
            ++stats.misses;
            final V value = load(key);
            if(value == null) {
                ++stats.errors;
            }
            return value;
        }
        else {
            // Cache hit
            ++stats.hits;
            entry.update();
            return entry.value;
        }
    }

    /**
     * Loads a new value and updates the cache.
     * @param key Key
     * @return Value
     */
    protected V load(K key) {
        // Load value
        final long start = System.currentTimeMillis();
        final V value;
        try {
            value = loader.load(key);
        }
        catch(final Exception e) {
            // TODO - log
            return null;
        }
        finally {
            stats.loadingTime += System.currentTimeMillis() - start;
        }

        // Check for unknown cache keys
        if(value == null) {
            // TODO - log
            return null;
        }

        // Create new entry
        final int weight = loader.calculateWeight(value);
        final Entry<V> entry = new Entry<>(value, weight);
        cleanup(entry);
        cache.put(key, entry);
        broadcast(Listener.Type.ADDED, key);

        // Track maximum cache size
        final int size = cache.size();
        if(size > stats.maxSize) {
            stats.maxSize = size;
        }

        return value;
    }

    /**
     * Evicts cache entries until the cache can accommodate the given entry.
     * @param entry New cache entry
     * TODO
     * - this should 'score' all the entries and stream through to find lowest and evict until policy is satisfied, i.e. Policy::score(Entry<?>)
     * - separate Weigher from Loader
     * - 
     */
    private void cleanup(Entry<V> entry) {
        while(!cache.isEmpty() && policy.isFull(entry, this)) {
            // Get next entry to be evicted
            final Stream<Object> keys = policy.getEvictionCandidates();
            if(keys == null) {
                // TODO - log
                break;
            }

            // Evict from cache
            keys.forEach(this::evict);
        }
    }

    /**
     * Evicts an entry from the cache.
     * @param key Key
     */
    private void evict(Object key) {
        cache.remove(key);
        broadcast(Listener.Type.EVICTED, key);
        ++stats.evictionCount;
    }

    /**
     * Adds a listener for cache entry changes.
     * @param listener Listener
     */
    public void add(Listener listener) {
        listeners.add(listener);
    }

    /**
     * Populates the given cache entry.
     * @param key Key
     */
    public void add(K key) {
        if(key == null) throw new IllegalArgumentException("Cannot add a NULL key");
        load(key);
    }

    /**
     * Populates the given cache entries.
     * @param keys Keys
     */
    public void add(Set<K> keys) {
        for(final K key : keys) {
            add(key);
        }
    }

    /**
     * Removes an entry from the cache.
     * @param key Key
     */
    public void remove(Object key) {
        cache.remove(key);
    }

    /**
     * Removes <b>all</b> entries from the cache.
     */
    public void clear() {
        cache.clear();
    }

    @Override
    public String toString() {
        // TODO
        return stats.toString();
    }
}

