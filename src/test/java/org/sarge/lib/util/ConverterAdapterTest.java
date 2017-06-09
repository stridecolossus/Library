package org.sarge.lib.util;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

public class ConverterAdapterTest {
	private ConverterAdapter adapter;
	private String value;
	
	@Before
	public void before() {
		adapter = new ConverterAdapter() {
			@Override
			protected String getValue(String name) {
				return value;
			}
		};
		value = null;
	}
	
	@Test
	public void getValue() {
		value = "string";
		assertEquals(value, adapter.getString("value", null));
	}
	
	@Test
	public void getValueDefault() {
		assertEquals("def", adapter.getString("value", "def"));
	}
	
	@Test(expected = NumberFormatException.class)
	public void getValueMissing() {
		adapter.getString(value, null);
	}
	
	@Test
	public void getString() {
		value = "value";
		assertEquals(value, adapter.getString("value", null));
	}
	
	@Test
	public void getInteger() {
		value = "42";
		assertEquals(42, adapter.getInteger("value", null));
	}
	
	@Test
	public void getLong() {
		value = "42";
		assertEquals(42L, adapter.getLong("value", null));
	}
	
	@Test
	public void getFloat() {
		value = "1.23";
		assertEquals(1.23f, adapter.getFloat("value", null), 0.0001f);
	}
	
	@Test
	public void getBoolean() {
		value = "true";
		assertEquals(true, adapter.getBoolean("value", null));
	}
}
