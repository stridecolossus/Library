package org.sarge.lib.collection;

import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.sarge.lib.collection.StrictMap;

public class StrictMapTest {
	private Map<String, Object> map;

	@Before
	public void before() {
		map = new StrictMap<>();
	}

	@Test
	public void nullValue() {
		map.put( new String(), null );
	}

	@Test(expected = IllegalArgumentException.class)
	public void nullKey() {
		map.put( null, new Object() );
	}

	@Test(expected = IllegalArgumentException.class)
	public void duplicateKey() {
		final String key = new String();
		map.put( key, new Object() );
		map.put( key, new Object() );
	}

	@Test(expected = IllegalArgumentException.class)
	public void removeNull() {
		map.remove( null );
	}

	@Test(expected = IllegalArgumentException.class)
	public void removeNotPresent() {
		map.remove( new String() );
	}
}
