package org.sarge.lib.util;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.sarge.lib.util.ConverterAdapter;

public class ConverterAdapterTest {
	private ConverterAdapter adapter;

	@Before
	public void before() {
		adapter = new ConverterAdapter() {
			@Override
			protected String getValue( String name ) {
				if( name.equals( "integer" ) ) {
					return "42";
				}
				else {
					return null;
				}
			}
		};
	}

	@Test
	public void getValue() {
		assertEquals( new Integer( 42 ), adapter.getInteger( "integer", null ) );
	}

	@Test
	public void getValueDefault() {
		final Integer def = new Integer( 999 );
		assertEquals( def, adapter.getInteger( "cobblers", def ) );
	}

	@Test( expected = NumberFormatException.class )
	public void getValueMandatory() {
		adapter.getInteger( "cobblers", null );
	}
}
