package org.sarge.lib.util;

import static org.junit.Assert.assertEquals;

import java.util.Optional;

import org.junit.Before;
import org.junit.Test;

public class ConverterAdapterTest {
	private ConverterAdapter adapter;

	@Before
	public void before() {
		adapter = new ConverterAdapter() {
			@Override
			public Optional<String> getValue( String name ) {
				if( name.equals( "integer" ) ) {
					return Optional.of( "42" );
				}
				else {
					return Optional.empty();
				}
			}
		};
	}

	@Test
	public void getValue() {
		assertEquals( new Integer( 42 ), adapter.getInteger( "integer", Optional.empty() ) );
	}

	@Test
	public void getValueDefault() {
		final Integer def = new Integer( 999 );
		assertEquals( def, adapter.getInteger( "cobblers", Optional.of( def ) ) );
	}

	@Test( expected = NumberFormatException.class )
	public void getValueMandatory() {
		adapter.getInteger( "cobblers", Optional.empty() );
	}
}
