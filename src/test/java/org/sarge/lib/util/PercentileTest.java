package org.sarge.lib.util;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.*;

public class PercentileTest {
	@Test
	void constructor() {
		final Percentile percentile = new Percentile(0.5f);
		assertEquals(0.5f, percentile.floatValue());
		assertEquals(0.5d, percentile.doubleValue());
		assertEquals(50, percentile.intValue());
		assertEquals(50L, percentile.longValue());
		assertEquals(Float.hashCode(0.5f), percentile.hashCode());
		assertEquals("50%", percentile.toString());
	}

	@Test
	void constructorInvalidRange() {
		assertThrows(IllegalArgumentException.class, () -> new Percentile(-1f));
		assertThrows(IllegalArgumentException.class, () -> new Percentile(2f));
	}

	@Test
	void range() {
		assertEquals(Percentile.ZERO, Percentile.of(0, 2));
		assertEquals(Percentile.HALF, Percentile.of(1, 2));
		assertEquals(Percentile.ONE, Percentile.of(2, 2));
	}

	@Test
	void constants() {
		assertEquals(100, Percentile.MAX);
		assertEquals(0.0f, Percentile.ZERO.floatValue());
		assertEquals(0.5f, Percentile.HALF.floatValue());
		assertEquals(1.0f, Percentile.ONE.floatValue());
	}

	@Test
	void isZero() {
		assertEquals(true, Percentile.ZERO.isZero());
		assertEquals(true, new Percentile(0f).isZero());
		assertEquals(true, Percentile.of(0).isZero());
		assertEquals(false, Percentile.HALF.isZero());
		assertEquals(false, Percentile.ONE.isZero());
	}

	@Test
	void integers() {
		assertEquals(Percentile.ZERO, Percentile.of(0));
		assertEquals(Percentile.HALF, Percentile.of(50));
		assertEquals(Percentile.ONE, Percentile.of(Percentile.MAX));
	}

	@Test
	void integerInvalidRange() {
		assertThrows(ArrayIndexOutOfBoundsException.class, () -> Percentile.of(-1));
		assertThrows(ArrayIndexOutOfBoundsException.class, () -> Percentile.of(101));
	}

	@Nested
	class ParseTests {
		@Test
		void zero() {
			assertEquals(Percentile.ZERO, Percentile.parse("0"));
			assertEquals(Percentile.ZERO, Percentile.parse("0.0"));
		}

		@Test
		void half() {
			assertEquals(Percentile.HALF, Percentile.parse("0.5"));
			assertEquals(Percentile.HALF, Percentile.parse("50"));
		}

		@Test
		void one() {
			assertEquals(Percentile.ONE, Percentile.parse("1.0"));
			assertEquals(Percentile.ONE, Percentile.parse("100"));
		}

		@Test
		void invalidFloatingPoint() {
			assertThrows(NumberFormatException.class, () -> Percentile.parse("NaN"));
		}

		@Test
		void invalid() {
			assertThrows(NumberFormatException.class, () -> Percentile.parse("cobblers"));
		}
	}

	@Test
	void compareTo() {
		assertEquals(+1, Percentile.HALF.compareTo(Percentile.ZERO));
		assertEquals(0,  Percentile.HALF.compareTo(Percentile.HALF));
		assertEquals(-1, Percentile.HALF.compareTo(Percentile.ONE));
	}

	@Test
	void lessThan() {
		assertEquals(true, Percentile.HALF.isLessThan(Percentile.ONE));
		assertEquals(false, Percentile.HALF.isLessThan(Percentile.HALF));
		assertEquals(false, Percentile.HALF.isLessThan(Percentile.ZERO));
	}

	@Test
	void min() {
		assertEquals(Percentile.ZERO, Percentile.HALF.min(Percentile.ZERO));
		assertEquals(Percentile.HALF, Percentile.HALF.min(Percentile.HALF));
		assertEquals(Percentile.HALF,  Percentile.HALF.min(Percentile.ONE));
	}

	@Test
	void max() {
		assertEquals(Percentile.HALF, Percentile.HALF.max(Percentile.ZERO));
		assertEquals(Percentile.HALF, Percentile.HALF.max(Percentile.HALF));
		assertEquals(Percentile.ONE,  Percentile.HALF.max(Percentile.ONE));
	}

	@Test
	void multiply() {
		assertEquals(Percentile.ONE, Percentile.ONE.multiply(Percentile.ONE));
		assertEquals(Percentile.of(25), Percentile.HALF.multiply(Percentile.HALF));
	}

	@Test
	void multiplyZero() {
		assertEquals(Percentile.ZERO, Percentile.ZERO.multiply(Percentile.ZERO));
		assertEquals(Percentile.ZERO, Percentile.ONE.multiply(Percentile.ZERO));
		assertEquals(Percentile.ZERO, Percentile.ZERO.multiply(Percentile.ONE));
	}

	@Test
	void invert() {
		assertEquals(Percentile.ONE, Percentile.ZERO.invert());
		assertEquals(Percentile.HALF, Percentile.HALF.invert());
		assertEquals(Percentile.ZERO, Percentile.ONE.invert());
	}

	@Test
	void equals() {
		assertEquals(true, Percentile.HALF.equals(Percentile.HALF));
		assertEquals(true, Percentile.HALF.equals(new Percentile(0.5f)));
		assertEquals(false, Percentile.HALF.equals(null));
		assertEquals(false, Percentile.HALF.equals(Percentile.ONE));
	}
}
