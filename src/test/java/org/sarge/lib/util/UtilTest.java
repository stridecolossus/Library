package org.sarge.lib.util;

import static org.junit.Assert.assertEquals;

import javax.lang.model.element.Modifier;

import org.junit.Test;

public class UtilTest {
	@Test
	public void getEnumConstant() {
		assertEquals(Modifier.FINAL, Util.getEnumConstant("final", Modifier.class));
	}
	
	@Test(expected=NumberFormatException.class)
	public void getEnumConstantInvalid() {
		Util.getEnumConstant("cobblers", Modifier.class);
	}
}
