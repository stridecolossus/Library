package org.sarge.lib.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.sarge.lib.util.Converter.EnumerationConverter;
import org.sarge.lib.util.Converter.TableConverter;

class ConverterTest {
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

	// TODO - this was local to enumeration() but Eclipse kept removed the first character!
	enum MockEnum {
		CONSTANT,
		CONSTANT_UNDERSCORE
	}

	@Test
	void enumeration() {
		final Converter<MockEnum> converter = new EnumerationConverter<>(MockEnum.class);
		assertEquals(MockEnum.CONSTANT, converter.apply("constant"));
		assertEquals(MockEnum.CONSTANT, converter.apply("CONSTANT"));
		assertEquals(MockEnum.CONSTANT_UNDERSCORE, converter.apply("constant-underscore"));
		assertThrows(NumberFormatException.class, () -> converter.apply("cobblers"));
	}

	@Test
	void table() {
		final Converter<Integer> converter = new TableConverter<>(Map.of("everything", 42), Converter.INTEGER);
		assertEquals(42, converter.apply("everything"));
		assertEquals(42, converter.apply("42"));
	}
}
