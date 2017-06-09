package org.sarge.lib.xml;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.sarge.lib.util.Check;
import org.sarge.lib.util.Converter;
import org.sarge.lib.util.Util;

/**
 * XML element.
 * @author Sarge
 */
public class Element {
	private final String name;
	private final Map<String, String> attributes;
	private final String text;
	private final Element parent;
	private final List<Element> children = new ArrayList<>();
	
	/**
	 * Constructor.
	 * @param name				Element name
	 * @param attributes		Attributes as a key-value map
	 * @param text				Optional text
	 * @param parent			Parent or <tt>null</tt> if a root element
	 */
	public Element(String name, Map<String, String> attributes, String text, Element parent) {
		Check.notEmpty(name);
		Check.notNull(text);
		this.name = name;
		this.attributes = new HashMap<>(attributes);
		this.text = text;
		this.parent = parent;
		if(parent != null) {
			parent.children.add(this);
		}
	}

	/**
	 * Convenience constructor for a simple root element.
	 * @param name Element name
	 */
	public Element(String name) {
		this(name, Collections.emptyMap(), "", null);
	}
	
	/**
	 * @return Element name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * @return Parent of this element
	 */
	public Element getParent() {
		return parent;
	}
	
	/**
	 * @return Children of this element
	 */
	public Stream<Element> getChildren() {
		return children.stream();
	}
	
	/**
	 * Convenience accessor to retrieve children with the given name.
	 * @return Children of this element
	 */
	public Stream<Element> getChildren(String name) {
		return children.stream().filter(e -> e.getName().equals(name));
	}

	/**
	 * @return Optional single child element
	 */
	public Optional<Element> getOptionalChild() {
		if(children.size() == 1) {
			return Optional.of(children.get(0));
		}
		else {
			return Optional.empty();
		}
	}

	/**
	 * @return The single child of this element
	 */
	public Element getChild() {
		return getOptionalChild().orElseThrow(() -> new ElementException(this, "Expected single child element"));
	}

	/**
	 * @return Optional single child of this element with the given name
	 */
	public Optional<Element> getOptionalChild(String name) {
		return Util.findOnly(getChildren(name));
	}

	/**
	 * @return The single child of this element with the given name
	 */
	public Element getChild(String name) {
		return getOptionalChild(name).orElseThrow(() -> new ElementException(this, "Expected child element: " + name));
	}

	/**
	 * @return Path from the root to this element
	 */
	public Stream<Element> getPath() {
		final List<Element> path = new ArrayList<>();
		Element e = this;
		while(true) {
			path.add(e);
			e = e.parent;
			if(e == null) break;
		}
		Collections.reverse(path);
		return path.stream();
	}

	/**
	 * Maps elements to XML name.
	 * @return Element name/index
	 */
	public static final Function<Element, String> ELEMENT_NAME = element -> {
		final String name = element.getName();
		final Element parent = element.getParent();
		if((parent == null) || (parent.children.size() == 1)) {
			return name;
		}
		else {
			final List<Element> children = parent.getChildren(name).collect(Collectors.toList());
			if(children.size() == 1) {
				return name;
			}
			else {
				final int index = children.indexOf(element);
				return String.format("%s[%d]", name, index + 1);
			}
		}
	};

	/**
	 * @return Textual content of this element
	 */
	public String getText() {
		return text;
	}
	
	/**
	 * @return Attributes ordered by name
	 */
	public Map<String, String> getAttributes() {
		return attributes;
	}
	
	/**
	 * Retrieves an attribute.
	 * @param name			Attribute name
	 * @param def			Optional default value if the specified attribute is not present
	 * @param converter		Converter
	 * @return Attribute value
	 * @throws ElementException if the attribute is missing but mandatory (no default supplied) or cannot be converted
	 */
	public <T> T getAttribute(String name, T def, Converter<T> converter) throws ElementException {
		final String value = attributes.get(name);
		if(value == null) {
			if(def == null) {
				throw new ElementException(this, "Missing mandatory attribute: " + name);
			}
			else {
				return def;
			}
		}
		else {
			try {
				return converter.convert(value);
			}
			catch(NumberFormatException e) {
				throw new ElementException(this, e.getMessage());
			}
		}
	}
	
	public <T> Optional<T> getOptionalAttribute(String name, T def, Converter<T> converter) {
		final String value = attributes.get(name);
		if(value == null) {
			return Optional.ofNullable(def);
		}
		else {
			return Optional.of(converter.convert(value));
		}
	}

	/**
	 * Retrieves a string attribute.
	 * @param name		Attribute name
	 * @param def		Optional default value
	 * @return String attribute
	 */
	public String getString(String name, String def) {
		return getAttribute(name, def, Converter.STRING);
	}
	
	/**
	 * Retrieves an integer attribute.
	 * @param name		Attribute name
	 * @param def		Optional default value
	 * @return Integer attribute
	 */
	public Integer getInteger(String name, Integer def) {
		return getAttribute(name, def, Converter.INTEGER);
	}
	
	/**
	 * Retrieves a long integer attribute.
	 * @param name		Attribute name
	 * @param def		Optional default value
	 * @return Long integer attribute
	 */
	public Long getLong(String name, Long def) {
		return getAttribute(name, def, Converter.LONG);
	}
	
	/**
	 * Retrieves a boolean attribute.
	 * @param name		Attribute name
	 * @param def		Optional default value
	 * @return Boolean attribute
	 */
	public Boolean getBoolean(String name, Boolean def) {
		return getAttribute(name, def, Converter.BOOLEAN);
	}

	/**
	 * Retrieves a floating-point attribute.
	 * @param name		Attribute name
	 * @param def		Optional default value
	 * @return Floating-point attribute
	 */
	public Float getFloat(String name, Float def) {
		return getAttribute(name, def, Converter.FLOAT);
	}
	
	@Override
	public String toString() {
		return name;
	}

	/**
	 * Exception relating to an {@link Element}.
	 */
	public static class ElementException extends RuntimeException {
		private final Element element;
		
		/**
		 * constructor.
		 * @param element		Related element
		 * @param reason		Reason text
		 */
		public ElementException(Element element, String reason) {
			this(element, reason, ELEMENT_NAME);
		}

		protected ElementException(Element element, String reason, Function<Element, String> mapper) {
			super(build(element, reason, mapper));
			this.element = element;
		}
		
		protected ElementException(Element element, Exception e, Function<Element, String> mapper) {
			super(build(element, e.getMessage(), mapper), e);
			this.element = element;
		}

		/**
		 * @return Element that caused this exception
		 */
		public Element getElement() {
			return element;
		}
		
		private static String build(Element element, String reason, Function<Element, String> mapper) {
			final String path = element.getPath().map(mapper).collect(Collectors.joining("/"));
			return reason + " at /" + path;
		}
	}

	/**
	 * Builder for an {@link Element}.
	 */
	public static class Builder {
		// Element attributes
		private final String name;
		private final Map<String, String> attributes = new HashMap<>();
		private String text = Util.EMPTY_STRING;
		private Element parent;
		
		// Tree
		private final List<Builder> children = new ArrayList<>();
		private Builder prev;
		
		/**
		 * Constructor.
		 * @param name Element name
		 */
		public Builder(String name) {
			Check.notEmpty(name);
			this.name = name;
		}

		/**
		 * Sets the textual content of this element.
		 * @param text Text
		 */
		public Builder text(String text) {
			Check.notNull(text);
			this.text = text;
			return this;
		}

		/**
		 * Adds an attribute to this element.
		 * @param name		Attribute name
		 * @param value		Value
		 */
		public Builder attribute(String name, Object value) {
			attributes.put(name, value.toString());
			return this;
		}
		
		/**
		 * Sets the parent of this element.
		 * @param parent Parent element
		 */
		public Builder parent(Element parent) {
			this.parent = parent;
			return this;
		}

		/**
		 * Starts a new child of the current element.
		 * @param name Child element name
		 * @return Builder for a new child element
		 * @see #pop()
		 */
		public Builder child(String name) {
			final Builder child = new Builder(name);
			child.prev = this;
			children.add(child);
			return child;
		}

		/**
		 * Completes a child element.
		 * @return Parent builder
		 * @see #child(String)
		 * @throws IllegalStateException if there is no active child element to pop
		 */
		public Builder pop() {
			if(prev == null) throw new IllegalStateException("No child element started");
			return prev;
		}
		
		/**
		 * Convenience method to add one-or-more simple child elements.
		 * @param names Element names
		 * @see #child(String)
		 */
		public Builder children(String... names) {
			Stream.of(names).forEach(this::child);
			return this;
		}

		/**
		 * @return New element
		 * @throws IllegalStateException if there is are pending child elements
		 */
		public Element build() {
			if(prev != null) throw new IllegalStateException("Child elements not finished");
			return build(parent);
		}

		/**
		 * Recursively constructs XML from the builder tree.
		 * @param parent Parent element
		 * @return Element
		 */
		private Element build(Element parent) {
			// Construct this element
			final Element e = new Element(name, attributes, text, parent);

			// Recurse to child builders and attach to this element
			for(Builder b : children) {
				b.build(e);
			}
			
			return e;
		}
	}
}
