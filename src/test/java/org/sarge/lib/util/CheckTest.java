package org.sarge.lib.util;

import java.util.ArrayList;
import java.util.Collections;

import org.junit.Test;
import org.sarge.lib.util.Check;

public class CheckTest {
	@Test( expected = IllegalArgumentException.class )
	public void isNullObject() {
		Check.notNull( null );
	}

	@Test
	public void notNullObject() {
		Check.notNull( new Object() );
	}

	@Test( expected = IllegalArgumentException.class )
	public void isEmptyString() {
		Check.notEmpty( new String( "" ) );
	}

	@Test
	public void notEmptyString() {
		Check.notEmpty( new String( "string" ) );
	}

	@Test( expected = IllegalArgumentException.class )
	public void isEmptyCollection() {
		Check.notEmpty( new ArrayList<String>() );
	}

	@Test
	public void notEmptyCollection() {
		Check.notEmpty( Collections.singleton( new Object() ) );
	}

	@Test( expected = IllegalArgumentException.class )
	public void isEmptyArray() {
		Check.notEmpty( new Object[]{} );
	}

	@Test
	public void notEmptyArray() {
		Check.notEmpty( new String[]{ "string" } );
	}

	@Test
	public void zeroOrMore() {
		Check.zeroOrMore( 0 );
		Check.zeroOrMore( 1 );
	}

	@Test( expected = IllegalArgumentException.class )
	public void zeroOrMoreInvalid() {
		Check.zeroOrMore( -1 );
	}

	@Test
	public void oneOrMore() {
		Check.oneOrMore( 1 );
	}

	@Test( expected = IllegalArgumentException.class )
	public void oneOrMoreInvalid() {
		Check.oneOrMore( 0 );
	}

	@Test
	public void range() {
		Check.range( 2, 1, 3 );
	}

	@Test( expected = IllegalArgumentException.class )
	public void rangeInvalid() {
		Check.range( 42, 1, 3 );
	}
}
