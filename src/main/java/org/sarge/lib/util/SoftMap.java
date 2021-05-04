package org.sarge.lib.util;

import static org.sarge.lib.util.Check.zeroOrMore;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Map with softly referenced values.
 * @author Sarge
 * @param <K> Key-type
 * @param <V> Value-type
 * TODO - stats? GC entries,
 */
public class SoftMap<K, V> implements Map<K, V> {
	/**
	 * Soft reference entry with reverse key lookup.
	 */
	private class SoftEntry extends SoftReference<V> {
		private final K key;

		private SoftEntry(K key, V value) {
			super(value, queue);
			this.key = key;
		}
	}

	private final Map<K, SoftEntry> map = new ConcurrentHashMap<>();
	private final Queue<V> refs = new ConcurrentLinkedQueue<>();
	private final ReentrantLock lock = new ReentrantLock();
	private final ReferenceQueue<? super V> queue = new ReferenceQueue<>();
	private final int min;

	/**
	 * Constructor with default minimum size.
	 */
	public SoftMap() {
		this(100);
	}

	/**
	 * Constructor.
	 * @param min Minimum number of hard-referenced entries to retain
	 */
	public SoftMap(int min) {
		this.min = zeroOrMore(min);
	}

	/**
	 * Removes garbage-collected entries from the underlying map.
	 */
	private void cleanup() {
		while(true) {
			@SuppressWarnings("unchecked")
			final var entry = (SoftEntry) queue.poll();
			if(entry == null) break;
			map.remove(entry.key);
		}
	}

	/**
	 * Registers a hard-referenced entry and trims the queue.
	 * @param value Value
	 */
	private void add(V value) {
		lock.lock();
		try {
			refs.add(value);
			while(refs.size() > min) {
				refs.poll();
			}
		}
		finally {
			lock.unlock();
		}
	}

	@Override
	public int size() {
		cleanup();
		return map.size();
	}

	@Override
	public boolean isEmpty() {
		cleanup();
		return map.isEmpty();
	}

	@Override
	public boolean containsKey(Object key) {
		cleanup();
		return map.containsKey(key);
	}

	@Override
	public boolean containsValue(Object value) {
		cleanup();
		return values().contains(value);
	}

	@Override
	public V get(Object key) {
		cleanup();
		final var entry = map.get(key);
		if(entry == null) {
			// No entry
			return null;
		}
		else {
			final V value = entry.get();
			if(value == null) {
				// Garbage-collected entry
				map.remove(key);
				return null;
			}
			else {
				// Note strongly referenced entry
				add(value);
				return value;
			}
		}
	}

	@Override
	public V put(K key, V value) {
		cleanup();
		final var entry = new SoftEntry(key, value);
		final var prev = map.put(key, entry);
		add(value);
		if(prev == null) {
			return null;
		}
		else {
			return prev.get();
		}
	}

	@Override
	public V remove(Object key) {
		cleanup();
		final var entry = map.remove(key);
		if(entry == null) {
			return null;
		}
		else {
			return entry.get();
		}
	}

	@Override
	public void putAll(Map<? extends K, ? extends V> m) {
		cleanup();
		for(Map.Entry<? extends K, ? extends V> entry : m.entrySet()) {
			put(entry.getKey(), entry.getValue());
		}
	}

	@Override
	public void clear() {
		lock.lock();
		try {
			refs.clear();
		}
		finally {
			lock.unlock();
		}
		cleanup();
		map.clear();
	}

	@Override
	public Set<K> keySet() {
		cleanup();
		return map.keySet();
	}

	@Override
	public Collection<V> values() {
		cleanup();
		final var result = new HashSet<V>(map.size());
		for(K key : map.keySet()) {
			final var value = get(key);
			if(value != null) {
				result.add(value);
			}
		}
		return result;
	}

	@Override
	public Set<Entry<K, V>> entrySet() {
		cleanup();
		final var result = new HashMap<K, V>(map.size());
		for(K key : map.keySet()) {
			final var value = get(key);
			if(value != null) {
				result.put(key, value);
			}
		}
		return result.entrySet();
	}
}
