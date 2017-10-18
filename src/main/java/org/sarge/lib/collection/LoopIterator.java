package org.sarge.lib.collection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.sarge.lib.object.ToString;
import org.sarge.lib.util.Check;

/**
 * Infinite looping iterator over a collection.
 */
public class LoopIterator<T> implements Iterator<T> {
	/**
	 * Looping strategy.
	 */
	public enum Strategy {
		/**
		 * Repeatedly iterates through the list.
		 */
		LOOP,

		/**
		 * Cycles backwards-and-forwards through the list.
		 */
		CYCLE
	}

	private final List<T> list;
	private final Strategy strategy;

	private ListIterator<T> itr;
	private T current;
	private boolean forwards;

	/**
	 * Constructor.
	 * @param c			Collection
	 * @param loop		Whether to repeatedly <i>loop</i> through the collection or <i>cycle</i> back-and-forwards
	 * @throws IllegalArgumentException if the collection is empty
	 */
	public LoopIterator(Collection<T> c, Strategy strategy) {
		Check.notEmpty(c);
		this.list = new ArrayList<>(c);
		this.strategy = strategy;
		itr = list.listIterator();
		forwards = true;
	}

	/**
	 * Resets the iterator and the start/end of the list.
	 */
	private void init() {
		switch(strategy) {
		case LOOP:
			itr = list.listIterator();
			break;

		case CYCLE:
			forwards = !forwards;
			itr = list.listIterator(forwards ? 1 : list.size() - 1);
			break;

		default:
			throw new RuntimeException();
		}
	}

	@Override
	public boolean hasNext() {
		return true;
	}

	@Override
	public T next() {
		final boolean next = forwards ? itr.hasNext() : itr.hasPrevious();
		if(!next) {
			init();
		}
		current = forwards ? itr.next() : itr.previous();
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
