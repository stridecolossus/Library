package org.sarge.lib.util;

import static org.junit.Assert.assertEquals;

import javax.lang.model.element.Modifier;

import org.junit.Test;

public class EnumConverterTest {
	
	private static enum Mock {
		CONSTANT,
		CONSTANT_WITH_UNDERSCORES
	}
	
	@Test
	public void test() {
		final EnumConverter<Mock> converter = new EnumConverter<>( Mock.class );
		assertEquals( Mock.CONSTANT, converter.convert( "constant" ) );
		assertEquals( Mock.CONSTANT_WITH_UNDERSCORES, converter.convert( " constant-WITH-underSCORES " ) );
	}

	@Test( expected = NumberFormatException.class )
	public void convertEnumInvalidConstant() {
		final EnumConverter<Modifier> converter = new EnumConverter<>( Modifier.class );
		converter.convert( "cobblers" );
	}
}
