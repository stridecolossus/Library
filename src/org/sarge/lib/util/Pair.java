package org.sarge.lib.util;

/**
 * Pair of objects.
 * @author Sarge
 * @param <T> Type
 */
public class Pair<T> {
	private final T left, right;
	
	public Pair( T left, T right ) {
		this.left = left;
		this.right = right;
	}
	
	public T getLeft() {
		return left;
	}
	
	public T getRight() {
		return right;
	}
	
	@Override
	public boolean equals( Object obj ) {
		if( obj == null ) return false;
		if( obj == this ) return true;
		if( obj instanceof Pair ) {
			// TODO - how to ensure has same type?
			@SuppressWarnings("unchecked")
			final Pair<T> that = (Pair<T>) obj;
			return this.left.equals( that.left ) && this.right.equals( that.right );
		}
		else {
			return false;
		}
	}
	
	@Override
	public String toString() {
		return left + "/" + right;
	}
}
