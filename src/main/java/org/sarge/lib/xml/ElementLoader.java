package org.sarge.lib.xml;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.sarge.lib.xml.Element.Builder;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/**
 * Loads XML from an input stream.
 * @author Sarge
 */
public class ElementLoader {
	private static final DocumentBuilderFactory FACTORY = DocumentBuilderFactory.newInstance();
	
	private final DocumentBuilder builder;
	
	/**
	 * Constructor.
	 * @throws RuntimeException if the underlying SAX parser cannot be instantiated
	 */
	public ElementLoader() {
		try {
			builder = FACTORY.newDocumentBuilder();
		}
		catch(ParserConfigurationException e) {
			throw new RuntimeException("Error creating document builder", e);
		}
	}

	/**
	 * Loads an XML element tree.
	 * @param in XML
	 * @return Root element
	 * @throws IOException if the XML cannot be loaded
	 */
	public Element load(Reader in) throws IOException {
		// Load XML
		// TODO - auto-close?
		final Document doc;
		try {
			doc = builder.parse(new InputSource(new BufferedReader(in)));
		}
		catch(Exception e) {
			throw new IOException("Error parsing XML", e);
		}
		
		// Convert to element tree
		return parse(doc.getDocumentElement(), null);
	}
	
	/**
	 * Recursively parses a W3C element.
	 * @param xml W3C element
	 * @return Element
	 */
	private static Element parse(org.w3c.dom.Element xml, Element parent) {
		// Start new element
		assert xml.getNodeType() == Node.ELEMENT_NODE;
		final Builder builder = new Builder(xml.getNodeName());
		
		// Parse attributes
		final NamedNodeMap attrs = xml.getAttributes();
		for(int n = 0; n < attrs.getLength(); ++n) {
			final Node node = attrs.item(n);
			builder.attribute(node.getNodeName(), node.getNodeValue());
		}
		
		// Load child nodes and element text
		final List<Node> children = new ArrayList<>();
		final NodeList nodes = xml.getChildNodes();
		for(int n = 0; n < nodes.getLength(); ++n) {
			final Node node = nodes.item(n);
			switch(node.getNodeType()) {
			case Node.ELEMENT_NODE:
				// Note child nodes for parsing later
				children.add(node);
				break;

			case Node.TEXT_NODE:
				// Load text content
				builder.text(node.getNodeValue());
				break;
				
			default:
				// Ignore others
				break;
			}
		}

		// Construct element
		builder.parent(parent);
		final Element element = builder.build();
		
		// Parse children
		for(Node node : children) {
			parse((org.w3c.dom.Element) node, element);
		}

		return element;
	}
}
