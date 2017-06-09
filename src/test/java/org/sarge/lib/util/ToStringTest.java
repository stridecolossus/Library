package org.sarge.lib.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ToStringTest {
	@SuppressWarnings("unused")
	private static class Data {
		public static final int ignored = 42;
		int num = 42;
		String str = "string";
		String empty = null;
	}

	@Test
	public void reflection() {
		final String result = ToString.toString(new Data());
		assertEquals("Data[num=42,str=\"string\",empty=null]", result);
	}

	@Test
	public void commaDelimited() {
		final String result = ToString.toString(new float[]{ 1, 2, 3 });
		assertEquals("[1.0, 2.0, 3.0]", result);
	}
}
