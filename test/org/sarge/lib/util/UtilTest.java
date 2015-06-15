package org.sarge.lib.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import javax.lang.model.element.Modifier;

import org.junit.Test;

public class UtilTest {
	@Test
	public void isEmptyString() {
		assertEquals( true, Util.isEmpty( (String) null ) );
		assertEquals( true, Util.isEmpty( "" ) );
		assertEquals( false, Util.isEmpty( "string" ) );
	}

	@Test
	public void isEmptyCollection() {
		assertEquals( true, Util.isEmpty( (Collection<String>) null ) );
		assertEquals( true, Util.isEmpty( new ArrayList<String>() ) );
		assertEquals( false, Util.isEmpty( Collections.singleton( "item" ) ) );
	}
	
	@Test
	public void hasDuplicates() {
		assertTrue(Util.hasDuplicates(Arrays.asList(42, 42)));
		assertFalse(Util.hasDuplicates(Arrays.asList(1, 2)));
	}

	@Test
	public void appendFileSeparator() {
		final String expected = "path" + File.separator;
		assertEquals( expected, Util.appendFileSeparator( "path" ) );
		assertEquals( expected, Util.appendFileSeparator( "path" + File.separator ) );
	}
	
	@Test
	public void getEnumConstant() {
		assertEquals( Modifier.FINAL, Util.getEnumConstant( "final", Modifier.class ) );
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void getEnumConstantInvalid() {
		Util.getEnumConstant( "cobblers", Modifier.class );
	}
}
