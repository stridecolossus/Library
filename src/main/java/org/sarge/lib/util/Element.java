package org.sarge.lib.util;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static org.sarge.lib.util.Check.notEmpty;
import static org.sarge.lib.util.Check.notNull;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * An <i>element</i> is a node in an XML document.
 * @author Sarge
 */
public final class Element {
	/**
	 * Creates a root element with the given name.
	 * @param name Root name
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
	 * @param name 				Element name
	 * @param attributes		Attributes
	 * @param text				Optional text content
	 */
	private Element(String name, Map<String, String> attributes, String text) {
		this.name = notEmpty(name);
		this.attributes = Map.copyOf(attributes);
		this.text = text;
	}

	/**
	 * TODO
	 * @param parent
	 */
	private void link(Element parent) {
		assert this.parent == null;
		this.parent = notNull(parent);
		parent.children.add(this);
	}

	/**
	 * @return Name of this element
	 */
	public String name() {
		return name;
	}

	/**
	 * @return Parent of this element
	 */
	public Optional<Element> parent() {
		return Optional.ofNullable(parent);
	}

	/**
	 * @return Whether this is a root element
	 */
	public boolean isRoot() {
		return parent == null;
	}

	/**
	 * @return Sibling index or zero if this element has no siblings
	 */
	public int index() {
		// Check for root or single element
		if((parent == null) || (parent.children.size() == 1)) {
			return 0;
		}

		// Check for single sibling
		final List<Element> siblings = parent.children(name).collect(toList());
		if(siblings.size() == 1) {
			return 0;
		}

		// Otherwise determine sibling index (by reference not equality)
		for(int n = 0; n < siblings.size(); ++n) {
			if(siblings.get(n) == this) {
				return n;
			}
		}
		throw new RuntimeException();
	}

	/**
	 * @return Path from this element to the document root
	 */
	public Stream<Element> path() {
		return Stream.iterate(this, Objects::nonNull, e -> e.parent);
	}

	/**
	 * @return Number of children of this element
	 */
	public int count() {
		return children.size();
	}

	/**
	 * @return Child elements
	 */
	public Stream<Element> children() {
		return children.stream();
	}

	/**
	 * Helper - Retrieves the children of this element with the given name (case insensitive).
	 * @param name Name
	 * @return Children
	 */
	public Stream<Element> children(String name) {
		return children.stream().filter(e -> e.name.equalsIgnoreCase(name));
	}

	/**
	 * Retrieves a child element.
	 * @param name Element name (case insensitive)
	 * @return Child element
	 * @throws ElementException if the child does not exist
	 */
	public Element child(String name) {
		return optional(name).orElseThrow(() -> exception(String.format("Child element [%s] not present", name)));
	}

	/**
	 * Retrieves an optional child element.
	 * @param name child element name (case insensitive)
	 * @return Child element
	 */
	public Optional<Element> optional(String name) {
		return children.stream().filter(e -> e.name.equalsIgnoreCase(name)).findAny();
	}

	/**
	 * Retrieves the <b>single</b> child of this element.
	 * @return Child element
	 * @throws ElementException if this element does not have exactly one child
	 */
	public Element child() {
		if(children.size() != 1) throw exception("Expected exactly one child element");
		return children.get(0);
	}

	/**
	 * @return Element attributes ordered by name
	 */
	public Map<String, String> attributes() {
		return attributes;
	}

	/**
	 * Retrieves an attribute.
	 * @param name Attribute name
	 * @return Attribute
	 */
	public Optional<String> attribute(String name) {
		return Optional.ofNullable(attributes.get(name));
	}

	/**
	 * Helper - Retrieves an optional boolean attribute.
	 * @param name		Boolean attribute name
	 * @param def		Default value
	 * @return Boolean attribute
	 */
	public boolean attribute(String name, boolean def) {
		return attribute(name).map(Converter.BOOLEAN).orElse(def);
	}

	/**
	 * @return Text content of this element or {@code null} if none
	 */
	public String text() {
		return text;
	}

	/**
	 * @param name Child element name
	 * @return Text content of the given child element or {@code null} if none
	 * @throws ElementException if the child does not exist
	 */
	public String text(String name) {
		return child(name).text();
	}

	/**
	 * @return Optional text content of this element
	 */
	public Optional<String> content() {
		return Optional.ofNullable(text);
	}

	/**
	 * @param name Child element name
	 * @return Optional text content of the given child (if present)
	 */
	public Optional<String> content(String name) {
		return optional(name).flatMap(Element::content);
	}

	/**
	 * Helper - Maps this element.
	 * @param <R> Resultant type
	 * @param mapper Mapper
	 * @return Result
	 */
	public <R> R map(Function<Element, R> mapper) {
		return mapper.apply(this);
	}

	/**
	 * An <i>element handler</i> is a consumer for an element.
	 */
	public interface Handler extends Consumer<Element> {
		/**
		 * Delegates this handler to the children of the element.
		 * @return Children handler
		 */
		default Handler children() {
			return xml -> xml.children.forEach(this);
		}

		/**
		 * Creates an element handler that first applies the given converter to the text of the element.
		 * @param <T> Transformed type
		 * @param converter		Element text converter
		 * @param consumer		Consumer
		 * @return Conversion handler
		 */
		static <T> Handler of(Converter<T> converter, Consumer<T> consumer) {
			return xml -> consumer.accept(converter.apply(xml.text));
		}

		/**
		 * Creates an element handler for the given transform.
		 * @param <T> Transformed type
		 * @param transformer	Element transformer
		 * @param consumer		Consumer
		 * @return Transform handler
		 */
		static <T> Handler of(Function<Element, T> transformer, Consumer<T> consumer) {
			return xml -> consumer.accept(transformer.apply(xml));
		}

		/**
		 * Creates an element handler that accepts the text of the element.
		 * @param consumer Element text consumer
		 * @return Element text handler
		 */
		static <T> Handler of(Consumer<String> consumer) {
			return xml -> consumer.accept(xml.text);
		}

		/**
		 * An <i>index handler</i> delegates an element to a handler by name.
		 */
		class Index implements Handler {
			private final Function<String, Handler> mapper;
			private final Handler def;

			/**
			 * Constructor.
			 * @param mapper		Maps the element to a handler
			 * @param def			Optional default handler
			 */
			public Index(Function<String, Handler> mapper, Handler def) {
				this.mapper = notNull(mapper);
				this.def = def;
			}

			@Override
			public void accept(Element xml) {
				final Handler delegate = mapper.apply(xml.name);
				if(delegate == null) {
					if(def == null) throw xml.exception("No handler for element");
					def.accept(xml);
				}
				else {
					delegate.accept(xml);
				}
			}
		}
	}

	/**
	 * An <i>element exception</i> indicates an XML processing exception thrown by the application.
	 * <p>
	 * The exception message is decorated with an XPath-like string representing the location of this element within the document.
	 * <p>
	 * @see Element#path()
	 * @see Element#index()
	 */
	public class ElementException extends RuntimeException {
		/**
		 * Constructor.
		 * @param message		Message
		 * @param cause			Root cause
		 */
		public ElementException(String message, Throwable cause) {
			super(message, cause);
		}

		/**
		 * Constructor.
		 * @param cause Root cause
		 */
		public ElementException(Throwable cause) {
			super(cause);
		}

		/**
		 * Constructor.
		 * @param message Message
		 */
		public ElementException(String message) {
			super(message);
		}

		/**
		 * @return Element on which the exception was raised
		 */
		public Element element() {
			return Element.this;
		}

		@Override
		public String getMessage() {
			// Build path
			final var path = path().collect(toList());
			Collections.reverse(path);

			// Convert to indexed path string
			final String str = path.stream().map(this::name).collect(joining("/"));

			// Build message
			return new StringBuilder()
					.append(super.getMessage())
					.append(" at /")
					.append(str)
					.toString();
		}

		/**
		 * @return Name of the given element within the path
		 */
		private String name(Element e) {
			final int index = e.index();
			if(index == 0) {
				return e.name;
			}
			else {
				return String.format("%s[%d]", e.name, index + 1);
			}
		}
	}

	/**
	 * Helper - Creates an XML exception at this element.
	 * @param message Message
	 * @return New XML exception
	 */
	public ElementException exception(String message) {
		return new ElementException(message);
	}

	/**
	 * Helper - Wraps an exception at this element.
	 * @param e Exception
	 * @return New XML exception
	 */
	public ElementException exception(Exception e) {
		return new ElementException(e);
	}

	@Override
	public int hashCode() {
		return Objects.hash(name, parent, text, attributes);
	}

	@Override
	public boolean equals(Object obj) {
		if(obj == this) {
			return true;
		}

		return
				(obj instanceof Element that) &&
				(this.parent == that.parent) &&
				this.name.equals(that.name) &&
				Objects.equals(this.text, that.text) &&
				this.attributes.equals(that.attributes);
	}

	@Override
	public String toString() {
		return name;
	}

	/**
	 * Builder for an XML element.
	 */
	public static class Builder {
		private String name = "xml";
		private String text;
		private final Map<String, String> attributes = new HashMap<>();
		private final List<Element> children = new ArrayList<>();
		private final Builder parent;

		/**
		 * Default constructor.
		 */
		public Builder() {
			this(null);
		}

		/**
		 * Constructor for a child.
		 * @param parent Parent builder
		 */
		private Builder(Builder parent) {
			this.parent = parent;
		}

		/**
		 * Sets the name of this element.
		 * @param name Element name
		 */
		public Builder name(String name) {
			this.name = notEmpty(name);
			return this;
		}

		/**
		 * Adds an attribute.
		 * @param name		Attribute name
		 * @param value		Value
		 */
		public Builder attribute(String name, Object value) {
			Check.notEmpty(name);
			Check.notNull(value);
			attributes.put(name, String.valueOf(value));
			return this;
		}

		/**
		 * Sets the text content of this element.
		 * @param text Text content
		 */
		public Builder text(String text) {
			this.text = notNull(text);
			return this;
		}

		/**
		 * Helper - Adds a child element with the given text.
		 * @param name Child element name
		 * @param text Child Text content
		 */
		public Builder child(String name, Object text) {
			final Element child = new Element.Builder().name(name).text(String.valueOf(text)).build();
			children.add(child);
			return this;
		}

		/**
		 * Starts a child element.
		 * @return New builder
		 * @see #end()
		 */
		public Builder child() {
			return new Builder(this);
		}

		/**
		 * Adds an existing element as a child.
		 * @param child Child element
		 * @throws IllegalStateException if the given child already has a parent
		 */
		public Builder child(Element child) {
			if(child.parent != null) throw new IllegalStateException("Child already has a parent: " + child);
			children.add(child);
			return this;
		}

		/**
		 * Completes this child element.
		 * @return Parent builder
		 * @throws IllegalStateException if this is not a child
		 */
		public Builder end() {
			// Check this is a child element
			if(parent == null) throw new IllegalStateException("Cannot end a root element");

			// Attach child
			final Element child = buildLocal();
			parent.children.add(child);

			// Return to parent builder
			return parent;
		}

		/**
		 * Constructs this element.
		 * @return New element
		 * @throws IllegalStateException if this is a child element
		 */
		public Element build() {
			if(parent != null) throw new IllegalStateException("Cannot build a child element");
			return buildLocal();
		}

		/**
		 * Constructs this element and links its children.
		 */
		private Element buildLocal() {
			final Element element = new Element(name, attributes, text);
			children.forEach(e -> e.link(element));
			return element;
		}
	}

	/**
	 * Loader for an XML document.
	 */
	public static class Loader {
		private static final DocumentBuilderFactory FACTORY = DocumentBuilderFactory.newInstance();

		/**
		 * @throws RuntimeException if the underlying XML parser cannot be instantiated
		 */
		public static DocumentBuilder parser() {
			try {
				return FACTORY.newDocumentBuilder();
			}
			catch(Exception e) {
				throw new RuntimeException("Error creating XML document parser", e);
			}
		}

		private final DocumentBuilder parser = parser();

		/**
		 * Loads an XML document.
		 * @param r XML reader
		 * @return Root element
		 * @throws IOException if the XML cannot be loaded
		 */
		public Element load(Reader r) throws IOException {
			// Load document
			Document doc;
			try {
				doc = parser.parse(new InputSource(r));
			}
			catch(SAXException e) {
				throw new IOException("Error parsing XML document", e);
			}

			// Load XML tree
			final Builder root = new Builder();
			final var tree = load(doc.getDocumentElement(), root);
			tree.forEach(Builder::end);

			// Extract root element
			return root.children.get(0);
		}

		/**
		 * Recursively loads a document.
		 * @param node 			Node
		 * @param builder		Builder
		 * @return Element builder
		 */
		private Stream<Builder> load(Node node, Builder parent) {
			// Init element
			final Builder builder = new Builder(parent);
			builder.name(node.getNodeName());

			// Load attributes
			final NamedNodeMap map = node.getAttributes();
			final int len = map.getLength();
			for(int n = 0; n < len; ++n) {
				final Node attr = map.item(n);
				builder.attribute(attr.getNodeName(), attr.getNodeValue());
			}

			// Process text and children nodes
			final NodeList nodes = node.getChildNodes();
			final int count = nodes.getLength();
			final List<Node> children = new ArrayList<>(count / 2);		// Children should be roughly half of the nodes
			for(int n = 0; n < count; ++n) {
				final Node child = nodes.item(n);
				switch(child.getNodeType()) {
					case Node.ELEMENT_NODE -> {
						// Add child element
						children.add(child);
					}

					case Node.TEXT_NODE -> {
						// Load text content
						final String text = child.getNodeValue().trim();
						if(!text.isEmpty()) {
							builder.text(text);
						}
					}
				}
			}

			// Recurse depth-first to children
			return Stream.concat(children.stream().flatMap(e -> load(e, builder)), Stream.of(builder));
		}
	}
}
