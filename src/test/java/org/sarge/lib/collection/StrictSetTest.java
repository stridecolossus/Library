package org.sarge.lib.collection;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.sarge.lib.collection.StrictSet;

public class StrictSetTest {
	private Set<String> set;

	@Before
	public void before() {
		set = new StrictSet<>();
	}

	@Test( expected = IllegalArgumentException.class )
	public void addNull() {
		set.add( null );
	}

	@Test( expected = IllegalArgumentException.class )
	public void addDuplicate() {
		final String str = new String();
		set.add( str );
		set.add( str );
	}

	@Test( expected = IllegalArgumentException.class )
	public void removeNull() {
		set.remove( null );
	}

	@Test( expected = IllegalArgumentException.class )
	public void removeNotPresent() {
		set.remove( new String() );
	}
}
