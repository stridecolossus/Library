package org.sarge.lib.util;

import static org.junit.Assert.assertEquals;

import javax.lang.model.element.Modifier;

import org.junit.Test;

public class ConverterTest {
	@Test
	public void convert() {
		assertEquals( new Integer( 42 ), Converter.INTEGER.convert( "42"  ) );
		assertEquals( new Long( 1234567890 ), Converter.LONG.convert( "1234567890"  ) );
		assertEquals( new Float( 1.2345 ), Converter.FLOAT.convert( "1.2345"  ) );
		assertEquals( Boolean.TRUE, Converter.BOOLEAN.convert( "true"  ) );
	}

	@Test( expected = NumberFormatException.class )
	public void invalidInteger() {
		Converter.INTEGER.convert( "cobblers" );
	}

	@Test( expected = NumberFormatException.class )
	public void invalidLong() {
		Converter.LONG.convert( "cobblers" );
	}

	@Test( expected = NumberFormatException.class )
	public void invalidFloat() {
		Converter.FLOAT.convert( "cobblers" );
	}

	@Test
	public void convertEnum() {
		final EnumConverter<Modifier> converter = new EnumConverter<>( Modifier.class );
		assertEquals( Modifier.PUBLIC, converter.convert( "PUBLIC" ) );
	}
}
