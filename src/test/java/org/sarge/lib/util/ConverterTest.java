package org.sarge.lib.util;

import static org.junit.jupiter.api.Assertions.*;

import java.time.Duration;
import java.util.Map;

import org.junit.jupiter.api.Test;

class ConverterTest {
	@Test
	void bool() {
		assertEquals(Boolean.TRUE, Converter.BOOLEAN.apply("true"));
		assertEquals(Boolean.FALSE, Converter.BOOLEAN.apply("false"));
		assertThrows(NumberFormatException.class, () -> Converter.BOOLEAN.apply(""));
		assertThrows(NumberFormatException.class, () -> Converter.BOOLEAN.apply("cobblers"));
	}

	@Test
	void enumeration() {
		enum MockEnum {
			CONSTANT,
			CONSTANT_UNDERSCORE
		}
		final var converter = Converter.of(MockEnum.class);
		assertEquals(MockEnum.CONSTANT, converter.apply("constant"));
		assertEquals(MockEnum.CONSTANT, converter.apply("CONSTANT"));
		assertEquals(MockEnum.CONSTANT_UNDERSCORE, converter.apply("constant-underscore"));
		assertThrows(NumberFormatException.class, () -> converter.apply("cobblers"));
	}

	@Test
	void table() {
		final var converter = Converter.of(Map.of("everything", 42), Integer::parseInt);
		assertEquals(42, converter.apply("everything"));
		assertEquals(42, converter.apply("42"));
	}

	@Test
	void duration() {
		assertEquals(Duration.ofSeconds(1), Converter.DURATION.apply("PT1S"));
		assertEquals(Duration.ofMillis(2), Converter.DURATION.apply("2ms"));
		assertEquals(Duration.ofSeconds(3), Converter.DURATION.apply("3s"));
		assertEquals(Duration.ofMinutes(4), Converter.DURATION.apply("4m"));
		assertEquals(Duration.ofHours(5), Converter.DURATION.apply("5h"));
		assertEquals(Duration.ofDays(6), Converter.DURATION.apply("6d"));
	}
}
