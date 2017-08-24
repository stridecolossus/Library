package org.sarge.lib.object;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class EqualsBuilderTest {
	@Test
	public void equals() {
		// Check equality
		final MockClass obj = new MockClass();
		assertEquals(true, EqualsBuilder.equals(obj, new MockClass()));
		assertEquals(true, EqualsBuilder.equals(new MockClass(), obj));

		// Check equal to self
		assertEquals(true, EqualsBuilder.equals(obj, obj));

		// Check null
		assertEquals(false, EqualsBuilder.equals(obj, null));

		// Check inequality
		final MockClass other = new MockClass();
		other.empty = "empty";
		assertEquals(false, EqualsBuilder.equals(obj, other));
		assertEquals(false, EqualsBuilder.equals(other, obj));
	}
}
