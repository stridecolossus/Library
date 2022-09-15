package org.sarge.lib.element;

import java.util.*;
import java.util.function.Function;

import org.sarge.lib.element.Element.ElementException;
import org.sarge.lib.util.Check;

/**
 * A <i>loader registry</i> maps an element to a loader by name.
 * @param <T> Loaded type
 * @author Sarge
 */
public class LoaderRegistry<T> {
	private final Map<String, Function<Element, T>> loaders = new HashMap<>();

	public LoaderRegistry() {
		init();
	}

	/**
	 * Initialises this registry.
	 * This method provides a convenience override point to register loaders in anonymous sub-class implementations.
	 */
	protected void init() {
		// Does nowt
	}

	/**
	 * Registers a loader.
	 * @param name			Loader name
	 * @param loader		Loader
	 */
	public LoaderRegistry<T> register(String name, Function<Element, T> loader) {
		Check.notEmpty(name);
		Check.notNull(loader);
		loaders.put(name, loader);
		return this;
	}

	/**
	 * Registers a loader for a literal result, i.e. maps the {@link Element#name()} to the given literal object.
	 * @param name			Loader name
	 * @param literal		Literal result
	 */
	public LoaderRegistry<T> literal(String name, T literal) {
		return register(name, __ -> literal);
	}

	// TODO - decent example

	/**
	 * Registers a loader comprising a function that applies a pre-processing transformation of the {@link Element#text()} and then delegates to a constructor.
	 * <p>
	 * Example:
	 * <pre>
	 * // Create a registry
	 * var loader = new RegistryLoader&lt;Integer&tg;();
	 *
	 * // Register a loader that transforms to the element to a point tuple
	 * ArrayConverter converter = new ArrayConverter(Point.SIZE, Point::new);
	 * loader.register("name", converter, Point::new);
	 *
	 * // Load an element
	 * Element e = new Element("name", "1, 2, 3");
	 * assertEquals(3, loader.load(e));
	 * </pre>
	 * <p>
	 * @param <R> Intermediate type
	 * @param name			Loader name
	 * @param transform		Transform function
	 * @param ctor			Constructor
	 */
	public <R> LoaderRegistry<T> register(String name, Function<String, R> transform, Function<R, T> ctor) {
		final Function<Element, T> func = e -> {
			final String text = e.text().toString();
			final R data = transform.apply(text);
			return ctor.apply(data);
		};
		return register(name, func);
	}

	/**
	 * Loads an object by delegating to the registered loader with the {@link Element#name()}.
	 * Also traps any exceptions thrown by the delegate loader.
	 * @param e Element
	 * @return Result
	 * @throws ElementException if a loader with the element name is not present or the object cannot be loaded
	 * @see #register(String, Function)
	 */
	public T load(Element e) {
		// Lookup loader
		final Function<Element, T> loader = loaders.get(e.name());
		if(loader == null) throw e.exception("Unknown loader: " + e.name());

		// Delegate
		try {
			return loader.apply(e);
		}
		catch(Exception ex) {
			throw e.new ElementException(ex);
		}
	}
}
