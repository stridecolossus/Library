package org.sarge.lib.util;

/**
 * A <i>percentile</i> represents a percentage numeric.
 * @see Check#isPercentile(float)
 * @author Sarge
 */
public final class Percentile extends Number implements Comparable<Percentile> {
	/**
	 * Maximum value of a percentile expressed as an integer.
	 */
	public static final int MAX = 100;

	/**
	 * Zero percentile.
	 */
	public static final Percentile ZERO = new Percentile(0f);

	/**
	 * 50% percentile.
	 */
	public static final Percentile HALF = new Percentile(0.5f);

	/**
	 * 100% percentile.
	 */
	public static final Percentile ONE = new Percentile(1f);

	private static final Percentile[] INTEGERS = new Percentile[MAX + 1];

	static {
		INTEGERS[0] = ZERO;
		for(int n = 1; n < MAX; ++n) {
			INTEGERS[n] = new Percentile(n / (float) MAX);
		}
		INTEGERS[MAX] = ONE;
	}

	/**
	 * Creates an integer percentile.
	 * @param value Percentile as a 0..100 integer (inclusive)
	 * @return Percentile
	 * @throws ArrayIndexOutOfBoundsException if the given value is not a valid percentile
	 */
	public static Percentile of(int value) {
		return INTEGERS[value];
	}

	/**
	 * Creates a percentile expressed as a range.
	 * @param value		Value
	 * @param max		Maximum value
	 * @return Ranged percentile, i.e. {@link #value} divided by {@link #max}
	 */
	public static Percentile of(float value, float max) {
		return new Percentile(value / max);
	}

	/**
	 * Parses a percentile from the given string representation.
	 * <p>
	 * A string containing a decimal point is assumed to be a 0..1 floating point value, otherwise it is treated as a 0..100 integer.
	 * <p>
	 * @param str Percentile as a string
	 * @return Percentile
	 * @throws ArrayIndexOutOfBoundsException if the given value is not a valid percentile
	 */
	public static Percentile parse(String str) {
		if(str.indexOf('.') >= 0) {
			return new Percentile(Float.parseFloat(str));
		}
		else {
			return of(Integer.parseInt(str));
		}
	}

	private final float value;

	/**
	 * Constructor.
	 * @param value Percentile as a 0..1 floating-point value
	 */
	public Percentile(float value) {
		this.value = Check.isPercentile(value);
	}

	/**
	 * @return Whether this percentile is equal to zero
	 */
	public boolean isZero() {
		return (this == ZERO) || Float.floatToIntBits(value) == 0;
	}

	@Override
	public int intValue() {
		return (int) (value * MAX);
	}

	@Override
	public long longValue() {
		return intValue();
	}

	@Override
	public float floatValue() {
		return value;
	}

	@Override
	public double doubleValue() {
		return value;
	}

	@Override
	public int compareTo(Percentile that) {
		if(this.value < that.value) {
			return -1;
		}
		else
		if(this.value > that.value) {
			return +1;
		}
		else {
			return 0;
		}
	}

	/**
	 * @param that Percentile
	 * @return Whether this percentile is less-than the given percentile
	 */
	public boolean isLessThan(Percentile that) {
		return this.value < that.value;
	}

	/**
	 * Determines the minimum of two percentiles.
	 * @param that Percentile
	 * @return Minimum percentile
	 */
	public Percentile min(Percentile that) {
		if(this.value < that.value) {
			return this;
		}
		else {
			return that;
		}
	}

	/**
	 * Determines the maximum of two percentiles.
	 * @param that Percentile
	 * @return Maximum percentile
	 */
	public Percentile max(Percentile that) {
		if(this.value > that.value) {
			return this;
		}
		else {
			return that;
		}
	}

	/**
	 * Multiplies this and the given percentile.
	 * @param p Percentile
	 * @return Multiplied percentile
	 */
	public Percentile multiply(Percentile p) {
		if(isZero() || p.isZero()) {
			return ZERO;
		}
		else {
			return new Percentile(this.value * p.value);
		}
	}

	/**
	 * Inverts this percentile.
	 * @return Inverted percentile
	 */
	public Percentile invert() {
		return new Percentile(1 - value);
	}

	@Override
	public int hashCode() {
        return Float.floatToIntBits(value);
	}

	@Override
	public boolean equals(Object obj) {
		return
				(obj == this) ||
				(obj instanceof Percentile that) &&
				(this.hashCode() == that.hashCode());
	}

	@Override
	public String toString() {
		return String.format("%d%%", intValue());
	}
}
