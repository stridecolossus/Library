package org.sarge.lib.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class StringUtilTest {
	@Test
	public void wrap() {
		assertEquals("LwordR", StringUtil.wrap("word", "L", "R"));
	}
}
