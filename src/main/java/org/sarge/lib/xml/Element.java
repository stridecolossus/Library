package org.sarge.lib.xml;

import static org.sarge.lib.util.Check.notEmpty;
import static org.sarge.lib.util.Check.notNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import org.sarge.lib.collection.StrictMap;
import org.sarge.lib.util.ConverterAdapter;
import org.sarge.lib.util.StreamUtil;
import org.sarge.lib.util.StringUtil;

/**
 * XML element.
 * @author Sarge
 */
public class Element {
	private final String name;
	private final ConverterAdapter attributes;
	private final String text;
	private final Element parent;
	private final List<Element> children = new ArrayList<>();
	
	/**
	 * Constructor.
	 * @param name				Element name
	 * @param attributes		Attributes as a key-value map
	 * @param text				Element text content
	 * @param parent			Parent or <tt>null</tt> if a root element
	 */
	public Element(String name, Map<String, String> attributes, String text, Element parent) {
		this.name = notEmpty(name);
		this.attributes = new ConverterAdapter(attributes);
		this.text = notNull(text);
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
		this(name, Collections.emptyMap(), StringUtil.EMPTY_STRING, null);
	}
	
	/**
	 * @return Element name
	 */
	public String name() {
		return name;
	}
	
	/**
     * @return Textual content of this element
     */
    public String text() {
        return text;
    }

    /**
     * @return Attributes on this element
     */
    public ConverterAdapter attributes() {
        return attributes;
    }

	/**
	 * @return Parent of this element or <tt>null</tt> for the document root
	 */
	public Element parent() {
		return parent;
	}
	
	/**
	 * @return Children of this element
	 */
	public Stream<Element> children() {
		return children.stream();
	}
	
	/**
	 * Convenience accessor to retrieve children with the given name.
	 * @return Children of this element
	 */
	public Stream<Element> children(String name) {
		return children.stream().filter(e -> e.name().equals(name));
	}
	
	/**
	 * @return The <b>single</b> child of this element
	 * @throws ElementException if this element does not have exactly <b>one</b> child
	 */
	public Element child() throws ElementException {
        if(children.size() == 1) {
            return children.get(0);
        }
        else {
            throw new ElementException(this, "Expected ONE child element");
        }
	}
	
    /**
     * @param name Child name
     * @return The <b>single</b> child of this element with the given name
     * @throws ElementException if this element does not have exactly <b>one</b> child with the given name
     */
    public Element child(String name) throws ElementException {
        final Iterator<Element> itr = children(name).iterator();
        if(itr.hasNext()) {
            final Element child = itr.next();
            if(!itr.hasNext()) return child;
        }
        throw new ElementException(this, "Expected ONE child element with name " + name);
    }

    /**
     * @return Optional child element
     */
    public Optional<Element> optionalChild() {
        if(children.size() == 1) {
            return Optional.of(children.get(0));
        }
        else {
            return Optional.empty();
        }
    }

    /**
     * @param name Child name
     * @return Optional child with the given name
     */
    public Optional<Element> optionalChild(String name) {
        return StreamUtil.findOnly(children(name));
    }

    /**
     * @return Path from the root to this element
     */
    public Stream<Element> path() {
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
     * Helper - Creates an exception caused by this element.
     * @param reason Reason message
     */
    public ElementException exception(String reason) {
        return new ElementException(this, reason);
    }

    // TODO - needed
    public ElementException exception(Exception e) {
        return new ElementException(this, e);
    }

    @Override
	public String toString() {
		return name;
	}

	/**
	 * Builder for an {@link Element}.
	 */
	public static class Builder {
		private final String name;
		private final Map<String, String> attributes = new StrictMap<>();
		private String text = StringUtil.EMPTY_STRING;
		private Element parent;
		
		/**
		 * Constructor.
		 * @param name Element name
		 */
		public Builder(String name) {
			this.name = notEmpty(name);
		}

		/**
		 * Sets the textual content of this element.
		 * @param text Text
		 */
		public Builder text(String text) {
			this.text = notNull(text);
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
		 * Constructs this element.
		 * @return New element
		 */
		public Element build() {
		    return new Element(name, attributes, text, parent);
		}
	}
}
