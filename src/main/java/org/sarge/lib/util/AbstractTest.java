package org.sarge.lib.util;

import org.junit.Rule;
import org.junit.rules.ExpectedException;

/**
 * Test-helper base-class.
 * @author Sarge
 */
public abstract class AbstractTest {
	/**
	 * Expected exception.
	 */
	@Rule
	public static final ExpectedException EXPECTED = ExpectedException.none();

	/**
	 * Registers an expected exception with the given message.
	 * @param type		Exception class
	 * @param message	Expected message or <tt>null</tt> to ignore the exception text
	 */
	protected static void expect(Class<? extends Throwable> type, String message) {
		EXPECTED.expect(type);
		if(!Util.isEmpty(message)) {
			EXPECTED.expectMessage(message);
		}
	}

	/**
	 * Registers an expected exception with the given message.
	 * @param type		Exception class
	 * @param message	Expected message
	 */
	protected static void expect(Class<? extends Throwable> type) {
		expect(type, null);
	}
}
