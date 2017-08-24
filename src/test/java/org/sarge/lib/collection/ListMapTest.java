package org.sarge.lib.collection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;
import org.sarge.lib.collection.ListMap;

public class ListMapTest {
	private static final String key = "key";

	private ListMap<String, Integer> map;

	@Before
	public void before() {
		map = new ListMap<>();
	}

	@Test
	public void constructor() {
		assertEquals( true, map.isEmpty() );
		assertNotNull( map.entrySet() );
	}

	@Test
	public void contains() {
		assertEquals( false, map.contains( key, 42  ) );
		map.add( key, 42 );
		assertEquals( true, map.contains( key, 42  ) );
	}

	@Test
	public void add() {
		// Add a value
		map.add( key, 1 );
		assertEquals( false, map.isEmpty() );
		assertEquals( 1, map.size() );
		assertNotNull( map.get( key ) );
		assertEquals( 1, map.get( key ).size() );
		assertEquals( true, map.get( key ).contains( 1 ) );

		// Add another
		map.add( key, 2 );
		assertEquals( 2, map.get( key ).size() );
		assertEquals( true, map.get( key ).contains( 2 ) );
	}

	@Test
	public void remove() {
		map.add( key, 1 );
		map.add( key, 2 );
		map.add( key, 3 );
		map.remove( key, 2 );
		assertEquals( true, map.get( key ).contains( 1 ) );
		assertEquals( true, map.get( key ).contains( 3 ) );
	}
}
