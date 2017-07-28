package org.sarge.lib.util;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

public class ConverterAdapterTest {
	private ConverterAdapter adapter;
	
	@Before
	public void before() {
		adapter = new ConverterAdapter(MapBuilder.build("integer", 42, "boolean", true, "float", 1.23f));
	}
	
	@Test
	public void getValue() {
	    assertEquals("42", adapter.getValue("integer", null, Converter.STRING));
	}
	
	@Test
	public void getValueDefault() {
        assertEquals("def", adapter.getValue("cobblers", "def", Converter.STRING));
	}
	
	@Test(expected = NumberFormatException.class)
	public void getValueMissing() {
        adapter.getValue("cobblers", null, Converter.STRING);
	}
	
	@Test
	public void toStringValue() {
		assertEquals("42", adapter.toString("integer", null));
	}
	
	@Test
	public void toInteger() {
		assertEquals(42, adapter.toInteger("integer", null));
	}
	
	@Test
	public void toLong() {
		assertEquals(42L, adapter.toLong("integer", null));
	}
	
	@Test
	public void toFloat() {
		assertEquals(1.23f, adapter.toFloat("float", null), 0.0001f);
	}
	
	@Test
	public void toBoolean() {
		assertEquals(true, adapter.toBoolean("boolean", null));
	}
}
