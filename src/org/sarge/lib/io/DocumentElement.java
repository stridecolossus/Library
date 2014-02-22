package org.sarge.lib.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.sarge.lib.util.Check;
import org.sarge.lib.util.ConverterAdapter;
import org.sarge.lib.util.Util;
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
public class DocumentElement extends ConverterAdapter {
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
	public static class DocumentException extends Exception {
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
			super( msg + " " + e.getPath().toString() );
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
	 * Retrieves a child element.
	 * @param name			Element name
	 * @param mandatory		Whether child element is mandatory
	 * @return Specified child element or <tt>null</tt> if not found
	 * @throws DocumentException if the element is mandatory but not present
	 */
	public DocumentElement getChild( String name, boolean mandatory ) throws DocumentException {
		for( DocumentElement e : getChildren() ) {
			if( e.getName().equals( name ) ) return e;
		}

		if( mandatory ) {
			throw new DocumentException( "Expected child element: " + name, this );
		}
		else {
			return null;
		}
	}

	public DocumentElement getChild( String name ) throws DocumentException {
		return getChild( name, true );
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
	protected String getValue( String name ) {
		return element.getAttribute( name );
	}

	@Override
	public String toString() {
		return getName();
	}
}
