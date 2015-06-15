package org.sarge.lib.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.sarge.lib.util.Check;
import org.sarge.lib.util.ConverterAdapter;
import org.sarge.lib.util.Util;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/**
 * DOM element.
 * <p>
 * Wrapper class around the hideous W3C implementation.
 * <p>
 * @author Sarge
 */
public class DocumentElement implements ConverterAdapter {
	private static final DocumentBuilder builder;

	static {
		try {
			builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		}
		catch( Exception e ) {
			throw new RuntimeException( "Unable to create document builder", e );
		}
	}

	/**
	 * XML exception.
	 */
	public static class DocumentException extends RuntimeException {
		/**
		 * Constructor.
		 * @param msg	Reason
		 * @param e		Cause
		 */
		private DocumentException( String msg, Exception e ) {
			super( msg, e );
		}

		/**
		 * Constructor.
		 * @param msg	Reason
		 * @param e		Element
		 */
		public DocumentException( String msg, DocumentElement e ) {
			super( msg + " at " + toPath( e ) );
		}

		/**
		 * Constructor.
		 * @param msg	Reason
		 * @param e		Element
		 */
		public DocumentException( Exception cause, DocumentElement e ) {
			super( cause.getMessage() + " at " + toPath( e ), cause );
		}
		
		private static String toPath( DocumentElement e ) {
			return e.getPath().stream().map( DocumentElement::getName ).collect( Collectors.joining( "/" ) );
		}
	}

	/**
	 * Loads an XML document.
	 * @param is XML
	 * @return Root element
	 * @throws DocumentException if the XML cannot be parsed
	 */
	public static DocumentElement load( InputStream is ) throws DocumentException {
		// Open file
		Check.notNull( is );
		final Document doc;
		try( final LineNumberReader r = new LineNumberReader( new InputStreamReader( is ) ) ) {
			// Parse DOM
			try {
				doc = builder.parse( new InputSource( r ) );
			}
			catch( Exception e ) {
				throw new DocumentException( "Error loading XML at line " + r.getLineNumber(), e );
			}
		}
		catch( IOException e ) {
			throw new RuntimeException( e );
		}

		// Convert to wrapper
		return new DocumentElement( doc.getDocumentElement(), null );
	}

	private final org.w3c.dom.Element element;
	private final DocumentElement parent;

	private List<DocumentElement> children;

	private DocumentElement( org.w3c.dom.Element element, DocumentElement parent ) {
		this.element = element;
		this.parent = parent;
	}

	/**
	 * @return Element name
	 */
	public String getName() {
		return element.getNodeName();
	}
	
	/**
	 * @return Element text content
	 */
	public String getText() {
		return element.getTextContent();
	}

	/**
	 * @return Parent element or <tt>null</tt> if root element
	 */
	public DocumentElement getParent() {
		return parent;
	}
	
	/**
	 * Convenience method to lookup an enum constant matching the name of this element.
	 * @param clazz Enum class
	 * @return Enum constant
	 */
	public <E extends Enum<E>> E getEnum( Class<E> clazz ) {
		return Util.getEnumConstant( getName(), clazz );
	}

	/**
	 * @return Path from the document root to this element
	 */
	public List<DocumentElement> getPath() {
		final List<DocumentElement> path = new ArrayList<>();
		DocumentElement e = this;
		do {
			path.add( e );
			e = e.parent;
		} while( e != null );
		Collections.reverse( path );
		return path;
	}

	/**
	 * Retrieves an optional child element.
	 * @param name			Element name
	 * @param mandatory		Whether child element is mandatory
	 * @return Specified child element
	 * @throws DocumentException if the element is mandatory but not present
	 */
	public Optional<DocumentElement> getChild( String name, boolean mandatory ) throws DocumentException {
		for( DocumentElement e : getChildren() ) {
			if( e.getName().equals( name ) ) return Optional.of( e );
		}

		if( mandatory ) {
			throw new DocumentException( "Expected child element: " + name, this );
		}
		else {
			return Optional.empty();
		}
	}

	public DocumentElement getChild( String name ) throws DocumentException {
		return getChild( name, true ).get();
	}

	/**
	 * @return Child of this element
	 * @throws DocumentException if this element has none or multiple children
	 */
	public DocumentElement getChild() throws DocumentException {
		getChildren();
		switch( children.size() ) {
		case 1:
			return children.get( 0 );

		case 0:
			throw new DocumentException( "No child elements", this );

		default:
			throw new DocumentException( "More than one child element", this );
		}
	}

	/**
	 * @return All child elements
	 */
	public List<DocumentElement> getChildren() {
		// Lazy-load child elements
		if( children == null ) {
			children = new ArrayList<>();
			final NodeList nodes = element.getChildNodes();
			final int len = nodes.getLength();
			for( int n = 0; n < len; ++n ) {
				// Skip other nodes
				final Node node = nodes.item( n );
				if( node.getNodeType() != Node.ELEMENT_NODE ) continue;

				// Convert to wrapper
				final DocumentElement e = new DocumentElement( (org.w3c.dom.Element) node, this );
				children.add( e );
			}
		}

		return children;
	}

	/**
	 * @param name Element name
	 * @return Specified child elements
	 */
	public List<DocumentElement> getChildren( String name ) {
		final List<DocumentElement> list = new ArrayList<>();
		for( DocumentElement e : getChildren() ) {
			if( e.getName().equals( name ) ) {
				list.add( e );
			}
		}
		return list;
	}

	/**
	 * @return Text content of all child elements
	 */
	public List<String> getChildrenText() {
		final List<String> list = new ArrayList<>();
		for( DocumentElement e : getChildren() ) {
			list.add( e.getText() );
		}
		return list;
	}

	/**
	 * @return Text content of the specified child elements
	 */
	public List<String> getChildrenText( String name ) {
		final List<String> list = new ArrayList<>();
		for( DocumentElement e : getChildren() ) {
			if( e.getName().equals( name ) ) {
				list.add( e.getText() );
			}
		}
		return list;
	}

	/**
	 * @param name Attribute name
	 * @return Whether this element has an attribute with the given name
	 */
	public boolean hasAttribute( String name ) {
		return !Util.isEmpty( element.getAttribute( name ) );
	}

	@Override
	public Optional<String> getValue( String name ) {
		final Attr attr = element.getAttributeNode( name );
		if( attr == null ) {
			return Optional.empty();
		}
		else {
			return Optional.of( attr.getValue() );
		}
	}
	
	@Override
	public String toString() {
		return getName();
	}
}
