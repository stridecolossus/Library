package org.sarge.lib.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import javax.lang.model.element.Modifier;

import org.junit.jupiter.api.Test;

public class ConverterTest {
	@Test
	public void convertString() {
		assertEquals("string", Converter.STRING.apply("string"));
	}

	@Test
	public void convertInteger() {
		assertEquals(Integer.valueOf(42), Converter.INTEGER.apply("42"));
	}

	@Test
	public void convertBoolean() {
		assertEquals(Boolean.TRUE, Converter.BOOLEAN.apply("true"));
		assertEquals(Boolean.FALSE, Converter.BOOLEAN.apply("false"));
		assertThrows(NumberFormatException.class, () -> Converter.BOOLEAN.apply(null));
		assertThrows(NumberFormatException.class, () -> Converter.BOOLEAN.apply(""));
		assertThrows(NumberFormatException.class, () -> Converter.BOOLEAN.apply("cobblers"));
	}

	@Test
	public void convertLong() {
		assertEquals(Long.valueOf(42), Converter.LONG.apply("42"));
	}

	@Test
	public void convertFloat() {
		assertEquals(1.23f, Converter.FLOAT.apply("1.23"), 0.001f);
	}

	@Test
	public void convertEnumeration() {
		assertEquals(Modifier.NATIVE, Converter.enumeration(Modifier.class).apply("native"));
		assertThrows(NumberFormatException.class, () -> Converter.enumeration(Modifier.class).apply("cobblers"));
	}
}
