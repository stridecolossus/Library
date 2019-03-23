package org.sarge.lib.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import javax.lang.model.element.Modifier;

import org.junit.jupiter.api.Test;

public class ValueConverterTest {
	/**
	 * Creates an adapter with the given value.
	 * @param value Value
	 * @return Adapter
	 */
	private static ValueConverter create(String value) {
		return new ValueConverter() {
			@Override
			protected String get() {
				return value;
			}
		};
	}

	private static final ValueConverter EMPTY = create(null);

	private static final ValueConverter INVALID = create("cobblers");

	@Test
	public void toText() {
		assertEquals("string", create("string").toText());
		assertEquals("string", EMPTY.toText("string"));
		assertThrows(NumberFormatException.class, () -> EMPTY.toText());
	}

	@Test
	public void toInteger() {
		assertEquals(42, create("42").toInteger());
		assertEquals(42, EMPTY.toInteger(42));
		assertThrows(NumberFormatException.class, () -> EMPTY.toInteger());
		assertThrows(NumberFormatException.class, () -> INVALID.toInteger());
	}

	@Test
	public void toLong() {
		assertEquals(42L, create("42").toLong());
		assertEquals(42L, EMPTY.toLong(42L));
		assertThrows(NumberFormatException.class, () -> EMPTY.toLong());
		assertThrows(NumberFormatException.class, () -> INVALID.toLong());
	}

	@Test
	public void toFloat() {
		assertEquals(42f, create("42").toFloat(), 0.0001f);
		assertEquals(42f, EMPTY.toFloat(42f), 0.0001f);
		assertThrows(NumberFormatException.class, () -> EMPTY.toFloat());
		assertThrows(NumberFormatException.class, () -> INVALID.toFloat());
	}

	@Test
	public void toBoolean() {
		assertEquals(true, create("true").toBoolean());
		assertEquals(true, EMPTY.toBoolean(true));
		assertThrows(NumberFormatException.class, () -> EMPTY.toBoolean());
		assertThrows(NumberFormatException.class, () -> INVALID.toBoolean());
	}

	@Test
	public void toValue() {
		final Converter<Modifier> converter = Converter.enumeration(Modifier.class);
		assertEquals(Modifier.NATIVE, create("NATIVE").toValue(converter));
		assertEquals(Modifier.NATIVE, EMPTY.toValue(Modifier.NATIVE, converter));
		assertThrows(NumberFormatException.class, () -> EMPTY.toValue(converter));
		assertThrows(NumberFormatException.class, () -> INVALID.toValue(converter));
	}
}
