package org.sarge.lib.util;

import static org.sarge.lib.util.Check.notNull;

import java.util.function.Supplier;

/**
 * A <i>lazy supplier</i> is a thread-safe <i>lazy initialisation</i> implementation.
 * @param <T> Supplied type
 * @author Sarge
 */
public class LazySupplier<T> implements Supplier<T> {
	private final Supplier<T> supplier;

	private volatile T value;

	/**
	 * Constructor.
	 * @param supplier Delegate supplier
	 */
	public LazySupplier(Supplier<T> supplier) {
		this.supplier = notNull(supplier);
	}

	@Override
	public T get() {
		final T result = value;		// Perform read

		if(result == null) {
			synchronized(this) {
				if(value == null) {
					value = notNull(supplier.get());
				}
				return value;
			}
		}
		else {
			return result;
		}
	}
}
