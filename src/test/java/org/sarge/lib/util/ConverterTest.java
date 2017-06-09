package org.sarge.lib.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.time.Duration;

import javax.lang.model.element.Modifier;

import org.junit.Test;

public class ConverterTest {
	@Test
	public void convertString() {
		assertEquals("string", Converter.STRING.convert("string"));
	}

	@Test
	public void convertInteger() {
		assertEquals(new Integer(42), Converter.INTEGER.convert("42"));
	}

	@Test
	public void convertBoolean() {
		assertEquals(Boolean.TRUE, Converter.BOOLEAN.convert("true"));
	}

	@Test
	public void convertLong() {
		assertEquals(new Long(42), Converter.LONG.convert("42"));
	}

	@Test
	public void convertFloat() {
		assertEquals(new Float(1.23), Converter.FLOAT.convert("1.23"), 0.001f);
	}

	@Test
	public void convertEnumeration() {
		assertEquals(Modifier.NATIVE, Converter.enumeration(Modifier.class).convert("NATIVE"));
	}

	@Test(expected=NumberFormatException.class)
	public void convertEnumerationUnknownCustom() {
		Converter.enumeration(Modifier.class).convert("COBBLERS");
	}
	
	@Test
	public void duration() {
		final Duration duration = Converter.DURATION.convert("01:02:03");
		assertNotNull(duration);
		assertEquals(1, duration.toHours());
		assertEquals(60 + 2, duration.toMinutes());
		assertEquals(((60 * 60) + (2 * 60) + 3) * 1000, duration.toMillis());
	}
}
