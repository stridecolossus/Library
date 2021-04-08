package org.sarge.lib.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.sarge.lib.util.Converter.EnumerationConverter;
import org.sarge.lib.util.Converter.TableConverter;

public class ConverterTest {
	@Test
	void string() {
		assertEquals("string", Converter.STRING.apply("string"));
	}

	@Test
	void integer() {
		assertEquals(Integer.valueOf(42), Converter.INTEGER.apply("42"));
	}

	@Test
	void bool() {
		assertEquals(Boolean.TRUE, Converter.BOOLEAN.apply("true"));
		assertEquals(Boolean.FALSE, Converter.BOOLEAN.apply("false"));
		assertThrows(NumberFormatException.class, () -> Converter.BOOLEAN.apply(null));
		assertThrows(NumberFormatException.class, () -> Converter.BOOLEAN.apply(""));
		assertThrows(NumberFormatException.class, () -> Converter.BOOLEAN.apply("cobblers"));
	}

	@Test
	void longInteger() {
		assertEquals(Long.valueOf(42), Converter.LONG.apply("42"));
	}

	@Test
	void floatingPoint() {
		assertEquals(1.23f, Converter.FLOAT.apply("1.23"), 0.001f);
	}

	@Test
	void enumeration() {
		enum Mock {
			CONSTANT,
			CONSTANT_UNDERSCORE
		}
		final Converter<Mock> converter = new EnumerationConverter<>(Mock.class);
		assertEquals(Mock.CONSTANT, converter.apply("constant"));
		assertEquals(Mock.CONSTANT, converter.apply("CONSTANT"));
		assertEquals(Mock.CONSTANT_UNDERSCORE, converter.apply("constant-underscore"));
		assertThrows(NumberFormatException.class, () -> converter.apply("cobblers"));
	}

	@Test
	void table() {
		final Converter<Integer> converter = new TableConverter<>(Map.of("everything", 42), Converter.INTEGER);
		assertEquals(42, converter.apply("everything"));
		assertEquals(42, converter.apply("42"));
	}
}
