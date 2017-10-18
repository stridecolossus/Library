package org.sarge.lib.collection;

import static org.sarge.lib.util.Check.notNull;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.function.Supplier;

import org.sarge.lib.object.ToString;

/**
 * In-memory cache.
 * <p>
 * Example usage for a cache of data loaded from the file-system:
 * <pre><blockquote>{@code
 *	// Create loader
 *	final Loader<String, String> loader = filename -> ... // Load data from file
 *
 *	// Create file cache
 *	final Cache cache = new Cache.Builder()
 * 		.loader(loader)
 * 		.limit(Limit.size(10))
 * 		.policy(EvictionPolicy.LRU)
 * 		.build();
 *
 *	// Pre-populate some data
 *	cache.add(...);
 *
 *	...
 *
 *	// Lookup or load file from cache
 *	final String file = cache.get(...);
 * }</blockquote></pre>
 * <p>
 * Notes:
 * <ul>
 * <li>Methods that are invoked directly on the cache ({@link #add(Object, Object)}, {@link #remove(Object)} and {@link #clear()}) do not notify listeners.</li>
 * <li>The cache makes no assumptions about the values returned by the configured {@link Weigher}, i.e. it is possible to set a {@link Limit#weight(int)} limit with the default weigher (which always returns zero).</li>
 * </ul>
 * @author Sarge
 * @param <K> Key-type
 * @param <V> Value-type
 * TODO
 * - cache refresher, re-loads all existing entries via loader
 */
public class Cache<K, V> {
    /**
     * Loads value that are not present in the cache.
	 * @param <K> Key-type
	 * @param <V> Value-type
     */
    @FunctionalInterface
    public interface Loader<K, V> {
        /**
         * Loads a new value for the cache.
         * @param key Key
         * @return Value
         */
        V load(K key);
    }

    /**
     * Weighs new cache entries for eviction purposes.
     * @param <V> Value type
     * @see Entry#weight()
     */
    @FunctionalInterface
    public interface Weigher<V> {
        /**
         * Calculates the <i>weight</i> of a cached value.
         * @param value Value
         * @return Weight
         */
		int weigh(V value);
    }

    /**
     * Cache limit.
	 * @param <K> Key-type
	 * @param <V> Value-type
     */
    @FunctionalInterface
    public interface Limit {
    	/**
    	 * Tests for a <i>full</i> cache.
    	 * @param cache Cache
    	 * @return Whether the given cache if full
    	 */
    	boolean isFull(Cache<?, ?> cache);

    	/**
    	 * Unlimited cache.
    	 */
    	Limit UNLIMITED = cache -> false;

    	/**
    	 * Creates a size-based cache limit.
    	 * @param max Maximum size
    	 * @return Size limit
    	 */
    	static Limit size(int max) {
    		return cache -> cache.cache.size() >= max;
    	}

    	/**
    	 * Creates a weight-based cache limit.
    	 * @param max Maximum total weight of all cache entries
    	 * @return Weight limit
    	 * @see Statistics#weight()
    	 */
    	static Limit weight(int max) {
    		return cache -> cache.stats.weight >= max;
    	}
    }

    /**
     * Eviction policy for a full cache.
     * @see Cache.Limit
     */
    @FunctionalInterface
    public interface EvictionPolicy {
    	/**
    	 * Scores the given cache entry.
    	 * @param entry Cache entry
    	 * @return Entry score
    	 */
    	long score(Entry<?> entry);

    	/**
    	 * Least-recently-used policy.
    	 * @see Entry#lastUpdated()
    	 */
    	EvictionPolicy LRU = entry -> entry.accessed();

    	/**
    	 * Least-frequently-used policy.
    	 * @see Entry#count()
    	 */
    	EvictionPolicy LFU = entry -> entry.count();

    	/**
    	 * Lowest weight policy.
    	 * @see Entry#weight()
    	 */
    	EvictionPolicy WEIGHT = entry -> entry.weight();
    }

    /**
     * Listener on cache entry changes.
	 * @param <K> Key-type
     */
    @FunctionalInterface
    public interface Listener<K> {
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
        void entry(Type type, K key);
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
        public long accessed() {
            return accessed;
        }

        /**
         * @return Most-recent create/update time of this entry
         */
        public long updated() {
            return updated;
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
            return ToString.toString(this);
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
        private int weight;

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

        /**
         * @return Total weight of this cache
         */
        public int weight() {
        	return weight;
        }

        @Override
        public String toString() {
        	return ToString.toString(this);
        }
    }

    // Cache
    private final Map<K, Entry<V>> cache;
    private final Collection<Listener<K>> listeners = new StrictList<>();
    private final Statistics stats = new Statistics();

    // Config
    private final Loader<K, V> loader;
    private final Weigher<V> weigher;
    private final Limit limit;
    private final EvictionPolicy policy;

    /**
     * Constructor.
     * @param cache			Underlying cache data-structure
     * @param loader		Loader for new cache values
     * @param weigher		Weigher for new cache entries
     * @param limit			Cache limit
     * @param policy		Eviction policy
     */
    private Cache(Map<K, Entry<V>> cache, Loader<K, V> loader, Weigher<V> weigher, Limit limit, EvictionPolicy policy) {
    	this.cache = notNull(cache);
		this.loader = notNull(loader);
		this.weigher = notNull(weigher);
		this.limit = notNull(limit);
		this.policy = notNull(policy);
	}

    /**
     * Looks up or loads a cache entry.
     * @param key Key
     * @return Cached value or <tt>null</tt> if not present
     * @see Loader#load(Object)
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
	 * @return Current size of this cache
	 */
	public int size() {
		return cache.size();
	}

	/**
	 * Adds a new entry to the cache.
	 * @param key		Key
	 * @param value		Value
	 */
    public void add(K key, V value) {
    	newEntry(key, value);
    }

    /**
     * Removes an entry from the cache.
     * @param key Key
     * @return Current cache value or <tt>null</tt> if none
     */
    public V remove(K key) {
   		return removeEntry(key);
    }

    /**
     * Clears <b>all</b> cache entries.
     */
    public void clear() {
    	cache.clear();
    	stats.weight = 0;
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
        catch(Exception e) {
            return null;
        }
        finally {
            stats.loadingTime += System.currentTimeMillis() - start;
        }

        // Check for unknown cache keys
        if(value == null) {
            return null;
        }

        // Add new cache entry
        newEntry(key, value);

        return value;
    }

    /**
     * Adds a new cache entry.
     * @param key		Key
     * @param value		Value
     */
    private void newEntry(K key, V value) {
        // Create new entry
        final int weight = weigher.weigh(value);
        final Entry<V> entry = new Entry<>(value, weight);

        // Add to cache
        cleanup(entry);
        cache.put(key, entry);

        // Update statistics
        stats.weight += entry.weight;

        // Track maximum cache size
        final int size = cache.size();
        if(size > stats.maxSize) {
            stats.maxSize = size;
        }

        // Notify listeners
        broadcast(Listener.Type.ADDED, key);
    }

    /**
     * Evicts cache entries until this cache can accommodate the given entry.
     * @param entry Entry being added
     */
    private void cleanup(Entry<V> entry) {
    	// Skip if nothing to evict
    	if(cache.isEmpty()) return;

    	// Score all cache entries (lowest are evicted soonest)
    	final LinkedList<K> scored = cache.entrySet().stream()
    		.filter(e -> e.getValue() != entry)
    		.sorted(Comparator.comparing(e -> policy.score(e.getValue())))
    		.map(Map.Entry::getKey)
    		.collect(LinkedList::new, LinkedList::add, LinkedList::addAll);

    	// Evict entries until no longer full
    	while(!cache.isEmpty() && limit.isFull(this)) {
    		final K key = scored.pop();
            ++stats.evictionCount;
            removeEntry(key);
            broadcast(Listener.Type.EVICTED, key);
    	}
    }

    /**
     * Removes a cache entry and updates statistics.
     * @param key Entry to remove
     * @return Current value or <tt>null</tt> if not present
     */
    private V removeEntry(K key) {
		final Entry<V> entry = cache.remove(key);
		if(entry == null) {
			return null;
		}
		else {
			stats.weight -= entry.weight;
			return entry.value;
		}
    }

    /**
     * @return Cache statistics
     */
    public Statistics statistics() {
        return stats;
    }

    /**
     * Adds a listener for entry eviction events to this cache.
     * @param listener Cache listener
     */
    public void add(Listener<K> listener) {
    	listeners.add(listener);
    }

    /**
     * Removes a listener from this cache.
     * @param listener Listener to remove
     */
    public void remove(Listener<K> listener) {
    	listeners.remove(listener);
    }

    /**
     * Broadcasts a cache entry update to all listeners.
     * @param type      Type of change
     * @param entry     Entry
     */
    private void broadcast(Listener.Type type, K key) {
        for(Listener<K> listener : listeners) {
            listener.entry(type, key);
        }
    }

    @Override
    public String toString() {
    	return stats.toString();
    }

    /**
     * Builder for a cache.
	 * @param <K> Key-type
	 * @param <V> Value-type
     */
    public static class Builder<K, V> {
    	private Map<K, Entry<V>> cache = new HashMap<>();
        private Loader<K, V> loader = key -> null;
        private Weigher<V> weigher = value -> 0;
        private Limit limit = Limit.UNLIMITED;
        private EvictionPolicy policy = EvictionPolicy.LRU;

        /**
         * Sets the type of the underlying cache implementation (default is a basic {@link HashMap}).
         * @param supplier Creates a new cache implementation
         */
        public Builder<K, V> cache(Supplier<Map<K, Entry<V>>> supplier) {
        	this.cache = supplier.get();
        	return this;
        }

        /**
         * Sets the loader for new cache entries (default returns <tt>null</tt>).
         * @param loader Loader
         */
        public Builder<K, V> loader(Loader<K, V> loader) {
        	this.loader = notNull(loader);
        	return this;
        }

        /**
         * Sets the weigher for new cache entries (default returns zero).
         * @param weigher Weigher
         */
        public Builder<K, V> weigher(Weigher<V> weigher) {
        	this.weigher = notNull(weigher);
        	return this;
        }

        /**
         * Sets the limit for this cache (default is {@link Limit#UNLIMITED}).
         * @param limit Cache limit
         */
        public Builder<K, V> limit(Limit limit) {
        	this.limit = notNull(limit);
        	return this;
        }

        /**
         * Sets the eviction policy for scoring stale cache entries (default is {@link EvictionPolicy#LRU}).
         * @param policy Eviction policy
         */
        public Builder<K, V> policy(EvictionPolicy policy) {
        	this.policy = notNull(policy);
        	return this;
        }

        /**
         * Builds a new cache.
         * @return New cache
         */
        public Cache<K, V> build() {
        	return new Cache<>(cache, loader, weigher, limit, policy);
        }
    }
}
