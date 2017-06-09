package org.sarge.lib.util;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Strict implementation that prevents <tt>null</tt> entries and duplicates.
 * @author Sarge
 * @param <E> Component type
 */
public class StrictList<E> extends ArrayList<E> {
	public StrictList() {
		super();
	}

	public StrictList( Collection<E> c ) {
		super( c );
	}

	@Override
	public boolean add( E e ) {
		checkAdd( e );
		return super.add( e );
	}

	@Override
	public void add( int index, E element ) {
		checkAdd( element );
		super.add( index, element );
	}

	@Override
	public boolean addAll( Collection<? extends E> c ) {
		checkAdd( c );
		return super.addAll( c );
	}

	@Override
	public boolean addAll( int index, Collection<? extends E> c ) {
		checkAdd( c );
		return super.addAll( index, c );
	}

	@Override
	public boolean remove( Object obj ) {
		Check.notNull( obj );
		if( !super.contains( obj ) ) throw new IllegalArgumentException( "Not a member: " + obj );
		return super.remove( obj );
	}

	private void checkAdd( E e ) {
		Check.notNull( e );
		if( super.contains( e ) ) throw new IllegalArgumentException( "Duplicate entry: " + e );
	}

	private void checkAdd( Collection<? extends E> c ) {
		for( E e : c ) checkAdd( e );
	}
}
