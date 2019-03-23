package org.sarge.lib.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

public class UtilTest {
	@Disabled
	@Test
	public void rethrow() {
		Util.rethrow(new IOException());
	}

	@Test
	public void clamp() {
		assertEquals(0, Util.clamp(-1, 0, 2));
		assertEquals(1, Util.clamp(1, 0, 2));
		assertEquals(2, Util.clamp(3, 0, 2));
	}
}
