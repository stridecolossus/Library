package org.sarge.lib.util;

import static org.junit.Assert.assertEquals;

import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.sarge.lib.collection.MapBuilder;

public class ConverterAdapterTest {
	private ConverterAdapter adapter;
	
	@Before
	public void before() {
		adapter = new ConverterAdapter(MapBuilder.build("integer", 42, "boolean", true, "float", 1.23f));
	}
	
	@Test
	public void toOptional() {
	    assertEquals(Optional.of(42), adapter.getOptional("integer", Converter.INTEGER));
	    assertEquals(Optional.empty(), adapter.getOptional("cobblers", Converter.INTEGER));
	}
	
	@Test
	public void toValue() {
	    assertEquals("42", adapter.toValue("integer", null, Converter.STRING));
	}
	
	@Test
	public void toValueDefault() {
        assertEquals("def", adapter.toValue("cobblers", "def", Converter.STRING));
	}
	
	@Test(expected = NumberFormatException.class)
	public void toValueMissing() {
        adapter.toValue("cobblers", null, Converter.STRING);
	}
	
	@Test
	public void toStringValue() {
		assertEquals("42", adapter.toString("integer"));
	}
	
	@Test
	public void toInteger() {
		assertEquals(new Integer(42), adapter.toInteger("integer"));
	}
	
	@Test
	public void toLong() {
		assertEquals(new Long(42L), adapter.toLong("integer"));
	}
	
	@Test
	public void toFloat() {
		assertEquals(1.23f, adapter.toFloat("float"), 0.0001f);
	}
	
	@Test
	public void toBoolean() {
		assertEquals(true, adapter.toBoolean("boolean"));
	}
}
