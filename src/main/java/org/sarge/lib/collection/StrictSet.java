package org.sarge.lib.collection;

import java.util.AbstractSet;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.sarge.lib.util.Check;

/**
 * Strict set implementation that prevents <tt>null</tt> entries and duplicates.
 * @author Sarge
 * @param <E> Data-type
 */
public class StrictSet<E> extends AbstractSet<E> {
	private final Set<E> set;

	public StrictSet() {
		this(new HashSet<E>());
	}

	public StrictSet(Collection<E> set) {
		this.set = new HashSet<>(set);
	}

	@Override
	public Iterator<E> iterator() {
		return set.iterator();
	}

	@Override
	public int size() {
		return set.size();
	}

	@Override
	public boolean add(E e) {
		Check.notNull(e);
		if(set.contains(e)) throw new IllegalArgumentException("Duplicate entry: " + e);
		set.add(e);
		return true;
	}

	@Override
	public boolean remove(Object obj) {
		Check.notNull(obj);
		if(!set.contains(obj)) throw new IllegalArgumentException("Not a member: " + obj);
		set.remove(obj);
		return true;
	}
}
