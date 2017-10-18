package org.sarge.lib.collection;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.sarge.lib.collection.LoopIterator.Strategy;

public class LoopIteratorTest {
	@Test
	public void loop() {
		// Check constructor
		final List<Integer> list = Arrays.asList(1, 2);
		final LoopIterator<Integer> itr = new LoopIterator<>(list, Strategy.LOOP);
		assertEquals(true, itr.hasNext());
		assertEquals(null, itr.current());

		// Check first next
		assertEquals(new Integer(1), itr.next());
		assertEquals(true, itr.hasNext());
		assertEquals(new Integer(1), itr.current());

		// Next
		itr.next();
		assertEquals(true, itr.hasNext());
		assertEquals(new Integer(2), itr.current());

		// Cycle back to start
		itr.next();
		assertEquals(true, itr.hasNext());
		assertEquals(new Integer(1), itr.current());
	}

	@Test
	public void cycle() {
		final List<Integer> list = Arrays.asList(1, 2);
		final LoopIterator<Integer> itr = new LoopIterator<>(list, Strategy.CYCLE);
		assertEquals(new Integer(1), itr.next());
		assertEquals(new Integer(2), itr.next());
		assertEquals(new Integer(1), itr.next());
		assertEquals(new Integer(2), itr.next());
		assertEquals(new Integer(1), itr.next());
	}
}
