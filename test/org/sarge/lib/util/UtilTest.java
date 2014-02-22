package org.sarge.lib.util;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.junit.Test;
import org.sarge.lib.util.Util;

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
	public void appendFileSeparator() {
		final String expected = "path" + File.separator;
		assertEquals( expected, Util.appendFileSeparator( "path" ) );
		assertEquals( expected, Util.appendFileSeparator( "path" + File.separator ) );
	}
}
