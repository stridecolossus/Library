package org.sarge.lib.object;

import org.junit.Test;

public class HashCodeBuilderTest {
	@Test
	public void test() {
		final Object obj = new MockClass();
		HashCodeBuilder.hashCode(obj);
	}
}
