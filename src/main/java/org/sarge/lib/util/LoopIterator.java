package org.sarge.lib.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Infinite looping iterator over a collection.
 */
public class LoopIterator<T> implements Iterator<T> {
	private final List<T> list;
	
	private Iterator<T> itr;
	private T current;

	/**
	 * Constructor.
	 * @param c Collection
	 * @throws IllegalArgumentException if the collection is empty
	 */
	public LoopIterator(Collection<T> c) {
		Check.notEmpty(c);
		this.list = new ArrayList<>(c);
		itr = list.iterator();
		current = list.get(0);
	}
	
	@Override
	public boolean hasNext() {
		return true;
	}
	
	@Override
	public T next() {
		if(!itr.hasNext()) {
			itr = list.iterator();
		}
		current = itr.next();
		return current;
	}

	/**
	 * @return Current element
	 */
	public T current() {
		return current;
	}
	
	@Override
	public String toString() {
		final ToString ts = new ToString(this);
		ts.append("index", list.indexOf(current));
		ts.append("size", list.size());
		return ts.toString();
	}
}
