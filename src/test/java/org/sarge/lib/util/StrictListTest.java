package org.sarge.lib.util;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.sarge.lib.util.StrictList;

public class StrictListTest {
	private List<String> list;

	@Before
	public void before() {
		list = new StrictList<>();
	}

	@Test( expected = IllegalArgumentException.class)
	public void addNull() {
		list.add( null );
	}

	@Test( expected = IllegalArgumentException.class)
	public void addDuplicate() {
		final String str = new String();
		list.add( str );
		list.add( str );
	}

	@Test( expected = IllegalArgumentException.class)
	public void removeNull() {
		list.remove( null );
	}

	@Test( expected = IllegalArgumentException.class)
	public void removeNotPresent() {
		list.remove( new String() );
	}
}
