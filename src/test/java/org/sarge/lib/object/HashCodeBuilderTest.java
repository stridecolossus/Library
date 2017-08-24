package org.sarge.lib.object;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.sarge.lib.object.HashCodeBuilder;

public class HashCodeBuilderTest {
	@Test
	public void test() {
		final Object obj = new MockClass();
		assertEquals( -891961076, HashCodeBuilder.hashCode( obj ) );
	}
}
