package org.sarge.lib.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Field;
import java.util.List;

import org.junit.Test;

public class ReflectionUtilsTest {
	@Test
	public void getMemberFields() {
		final List<Field> fields = ReflectionUtils.getMemberFields( MockClass.class );
		assertNotNull( fields );
		assertEquals( 3, fields.size() );

		final Class<?>[] classes = new Class<?>[]{ Integer.TYPE, String.class, String.class };
		final String[] names = new String[]{ "num", "str", "empty" };
		for( int n = 0; n < 3; ++n ) {
			final Field f = fields.get( n );
			assertEquals( classes[ n ], f.getType() );
			assertEquals( names[ n ], f.getName() );
			assertEquals( true, f.isAccessible() );
		}
	}

	@Test
	public void getMemberValues() {
		final MockClass obj = new MockClass();
		final List<Object> values = ReflectionUtils.getMemberValues( obj );
		assertNotNull( values );
		assertEquals( 3, values.size() );
		assertEquals( 42, values.get( 0 ) );
		assertEquals( "string", values.get( 1 ) );
		assertEquals( null, values.get( 2 ) );
	}

	@Test
	public void inheritedClass() {
		final Object obj = new MockClass() {
			@SuppressWarnings("unused")
			int inherited = 37;
		};
		final List<Object> values = ReflectionUtils.getMemberValues( obj );
		assertTrue( values.contains( 37 ) );
	}
}
