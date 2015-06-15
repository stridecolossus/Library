package org.sarge.lib.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Map;

import org.junit.Test;

public class MapBuilderTest {
	@Test
	public void build() {
		final Map<String, Integer> map = MapBuilder.build( "one", 1, "two", 2, "three", 3 );
		assertNotNull( map );
		assertEquals( 3, map.size() );
		assertEquals( new Integer( 1 ), map.get( "one" ) );
		assertEquals( new Integer( 2 ), map.get( "two" ) );
		assertEquals( new Integer( 3 ), map.get( "three" ) );
	}

	@Test(expected=IllegalArgumentException.class)
	public void buildNotBalanced() {
		MapBuilder.build( "one", 1, "bugger" );
	}
}
