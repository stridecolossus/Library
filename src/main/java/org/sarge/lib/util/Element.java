package org.sarge.lib.util;

import static java.util.stream.Collectors.*;
import static org.sarge.lib.util.Check.*;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * An <i>element</i> is a node tree used to represent compound document types such as XML and YAML.
 * <p>
 * Each element in the tree is comprised of:
 * <ul>
 * <li>the element name</li>
 * <li>optional text content</li>
 * <li>none-or-more attributes</li>
 * <li>none-or-more children</li>
 * </ul>
 * <p>
 * The {@link Builder} provides a fluid interface to construct a document:
 * <p>
 * <pre>
 * Element element = new Element.Builder()
 *     .name("parent")
 *     .attribute("attribute", "value")
 *     .text("text")
 *     .child()
 *         .name("child")
 *         .end()
 *     .build();
 * </pre>
 * <p>
 * An {@link ElementException} can be used to indicate an application error when processing an element:
 * <p>
 * <pre>
 * throw element.new ElementException(...);
 * </pre>
 * <p>
 * @author Sarge
 */
public final class Element {
	/**
	 * Creates a root element with the given name.
	 * @param name Root element name
	 * @return Root element
	 */
	public static Element of(String name) {
		return new Element.Builder().name(name).build();
	}

	private final String name;
	private final Map<String, String> attributes;
	private final String text;
	private final List<Element> children = new ArrayList<>();
	private Element parent;

	/**
	 * Constructor.
	 * @param name				Element name
	 * @param attributes		Attributes indexed by name
	 * @param text				Text content or the empty string if none
	 */
	public Element(String name, Map<String, String> attributes, String text) {
		this.name = notEmpty(name);
		this.attributes = Map.copyOf(attributes);
		this.text = text;
	}

	/**
	 * Sets the parent of this element.
	 * @param parent Parent element
	 * @throws IllegalStateException if this element already has a parent
	 */
	private void parent(Element parent) {
		if(this.parent != null) throw new IllegalStateException("Element already has a parent: " + this);
		this.parent = notNull(parent);
		parent.children.add(this);
	}

	/**
	 * @return Element name
	 */
	public String name() {
		return name;
	}

	/**
	 * @return Whether this is an empty element with no children or attributes
	 */
	public boolean isEmpty() {
		return children.isEmpty() && attributes.isEmpty();
	}

	/**
	 * @return Attributes indexed by name
	 */
	public Map<String, String> attributes() {
		return attributes;
	}

	/**
	 * A <i>content</i> instance represents an attribute or the text content of this element.
	 */
	public abstract class Content {
		private final String value;

		protected Content(String value) {
			this.value = value;
		}

		/**
		 * @return Whether this content is present
		 */
		public boolean isPresent() {
			return value != null;
		}

		/**
		 * @throws ElementException if this content is not present
		 */
		private void check() {
			if(value == null) throw exception(message());
		}

		/**
		 * @return Exception message when accessing empty content
		 */
		protected abstract String message();

		/**
		 * Transforms this content.
		 * @param <R> Return type
		 * @param transform Transformation function
		 * @return Result
		 * @throws ElementException if the transform cannot be applied to this content
		 */
		public <R> R transform(Function<String, R> transform) {
			check();
			try {
				return transform.apply(value);
			}
			catch(NumberFormatException e) {
				throw exception(e.getMessage());
			}
		}

		/**
		 * Converts this content to an integer.
		 * @return Integer
		 * @throws ElementException if this content is not a valid integer
		 */
		public int toInteger() {
			return transform(Integer::parseInt);
		}

		/**
		 * Converts this content to a floating-point number.
		 * @return Float
		 * @throws ElementException if this content is not a valid floating-point value
		 */
		public float toFloat() {
			return transform(Float::parseFloat);
		}

		/**
		 * Converts this content to a boolean value.
		 * @return Boolean
		 * @throws ElementException if this content is not a valid boolean
		 * @see Converter#BOOLEAN
		 */
		public boolean toBoolean() {
			return transform(Converter.BOOLEAN::apply);
		}

		@Override
		public String toString() {
			check();
			return value;
		}
	}

	/**
	 * Retrieves an attribute.
	 * @param name Attribute name
	 * @return Attribute
	 */
	public Content attribute(String name) {
		return new Content(attributes.get(name)) {
			@Override
			protected String message() {
				return String.format("Attribute %s not present", name);
			}
		};
	}

	/**
	 * @return Text content
	 */
	public Content text() {
		return new Content(text) {
			@Override
			protected String message() {
				return "Text not present";
			}
		};
	}

	/**
	 * @return Parent of this element
	 */
	public Optional<Element> parent() {
		return Optional.ofNullable(parent);
	}

	/**
	 * @return Number of children
	 */
	public int size() {
		return children.size();
	}

	/**
	 * @return Children of this element
	 */
	public Stream<Element> children() {
		return children.stream();
	}

	/**
	 * Convenience accessor for the children of this element with the given name.
	 * @param name Child name
	 * @return Children with the given name
	 */
	public Stream<Element> children(String name) {
		return children.stream().filter(e -> e.name.equals(name));
	}

	/**
	 * Retrieves the <i>first</i> child of this element with the given name.
	 * @param name Child element name
	 * @return Child element
	 * @throws ElementException if the element does not exist
	 */
	public Element child(String name) {
		return optional(name).orElseThrow(() -> exception("Expected child element: " + name));
	}

	/**
	 * Retrieves the <i>first</i> child of this element.
	 * @return Child element
	 * @throws ElementException if this element does not have a child
	 */
	public Element child() {
		if(children.isEmpty()) throw exception("Expected child element");
		return children.get(0);
	}

	/**
	 * Optionally retrieves the <i>first</i> child of this element with the given name.
	 * @param name Child element name
	 * @return Child element
	 */
	public Optional<Element> optional(String name) {
		return children(name).findAny();
	}

	/**
	 * The <i>index</i> of an element is the <i>position</i> of this element with respect to its siblings (starting at one), where a <i>sibling</i> is an element with the same name.
	 * A root element or a single child element therefore has an index of one.
	 * @return Index of this element
	 */
	public int index() {
		// Skip if no siblings
		if(parent == null) {
			return 1;
		}

		// Determine sibling index
		int index = 1;
		for(final Element sibling : parent.children) {
			if(sibling == this) {
				return index;
			}
			if(sibling.name.equals(this.name)) {
				++index;
			}
		}
		throw new RuntimeException();
	}

	/**
	 * @return Path from this element to the root
	 */
	public List<Element> path() {
		final List<Element> path = Stream
				.iterate(this, Objects::nonNull, e -> e.parent)
				.collect(toCollection(LinkedList::new));

		Collections.reverse(path);

		return path;
	}

	/**
	 * Applies the given mapping function to this element.
	 * @param <T> Mapped type
	 * @param mapper Mapping function
	 * @return Mapped value
	 */
	public <T> T map(Function<Element, T> mapper) {
		return mapper.apply(this);
	}

	/**
	 * Convenience method to create an exception on this element.
	 * @param message Exception message
	 * @return New element exception
	 */
	public ElementException exception(String message) {
		return new ElementException(message);
	}

	@Override
	public int hashCode() {
		return Objects.hash(name, attributes, text, children, parent);
	}

	@Override
	public boolean equals(Object obj) {
		return
				(obj == this) ||
				(obj instanceof Element that) &&
				this.name.equals(that.name) &&
//				(this.parent == that.parent) &&
				Objects.equals(this.text, that.text) &&
				this.attributes.equals(that.attributes) &&
				this.children.equals(that.children);
	}

	@Override
	public String toString() {
		return name;
	}

	/**
	 * An <i>element exception</i> indicates a processing exception thrown by the application.
	 * <p>
	 * The exception message is decorated with an XPath-like string representing the location of this element within the document.
	 * <p>
	 * @see Element#index()
	 */
	public class ElementException extends RuntimeException {
		/**
		 * Constructor.
		 * @param message 		Exception message
		 * @param cause			Optional cause
		 */
		public ElementException(String message, Throwable cause) {
			super(message, cause);
		}

		/**
		 * Constructor.
		 * @param message Exception message
		 */
		public ElementException(String message) {
			super(message);
		}

		@Override
		public String getMessage() {
			final String path = path()
					.stream()
					.map(this::name)
					.collect(joining("/"));

			return String.format("%s at /%s", super.getMessage(), path);
		}

		/**
		 * @return Name of the given element decorated with the sibling index
		 */
		private String name(Element e) {
			final int index = e.index();
			if(index == 1) {
				return e.name;
			}
			else {
				return String.format("%s[%d]", e.name, index);
			}
		}
	}

	/**
	 * Builder for an element.
	 */
	public static class Builder {
		// Element properties
		private String name;
		private final Map<String, String> attributes = new HashMap<>();
		private String text;

		// Tree
		private final List<Element> children = new ArrayList<>();
		private Builder parent;

		/**
		 * Sets the name of this element.
		 * @param name Element name
		 */
		public Builder name(String name) {
			this.name = notEmpty(name);
			return this;
		}

		/**
		 * Adds an attribute to this element.
		 * @param name 		Attribute name
		 * @param value		Value
		 */
		public Builder attribute(String name, Object value) {
			Check.notEmpty(name);
			attributes.put(name, value.toString());
			return this;
		}

		/**
		 * Sets the text content of this element.
		 * @param text Text content
		 */
		public Builder text(String text) {
			this.text = notEmpty(text);
			return this;
		}

		/**
		 * Starts a new builder for a child of this element.
		 * @return New child element builder
		 */
		public Builder child() {
			final Builder child = new Builder();
			child.parent = this;
			return child;
		}

		/**
		 * Attaches an existing element as a child of this element.
		 * @param child Child element to attach
		 */
		public Builder child(Element child) {
			children.add(notNull(child));
			return this;
		}

		/**
		 * Helper - Attaches a child element with the given text content.
		 * @param name		Child element name
		 * @param text		Text content
		 */
		public Builder child(String name, String text) {
			final Element child = new Builder().name(name).text(text).build();
			return child(child);
		}

		/**
		 * Constructs this child element and returns control to the parent builder.
		 * @return Parent builder
		 * @throws IllegalStateException if this is not a builder for a child element
		 */
		public Builder end() {
			if(parent == null) throw new IllegalStateException("Not a child element builder");

			// Construct this child element and attach to parent
			final Element child = create();
			parent.children.add(child);

			// Return control to parent builder
			try {
				return parent;
			}
			finally {
				parent = null;
			}
		}

		/**
		 * Constructs this element.
		 * @return New element
		 * @throws IllegalStateException if this is a builder for a child element
		 * @see #end()
		 */
		public Element build() {
			if(parent != null) throw new IllegalStateException("Cannot build a child element");

			if(name == null) {
				return empty(children);
			}
			else {
				return create();
			}
		}

		/**
		 * Invoked if the name of this element has not been populated.
		 * <p>
		 * This method can be overridden to handle use-cases where the root element may be optional.
		 * <p>
		 * @param children Children elements
		 * @return New element
		 */
		protected Element empty(List<Element> children) {
			name("root");
			return create();
		}

		/**
		 * Constructs this element and links its children.
		 * @return New element
		 */
		protected Element create() {
			// Construct element
			final Element element = new Element(name, attributes, text);

			// Attach children
			for(final Element e : children) {
				e.parent(element);
			}

			return element;
		}
	}
}
