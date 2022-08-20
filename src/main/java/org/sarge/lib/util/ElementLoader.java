package org.sarge.lib.util;

import java.io.*;

import javax.xml.parsers.*;

import org.sarge.lib.util.Element.Builder;
import org.w3c.dom.*;
import org.xml.sax.*;

/**
 * Element loader for an XML document.
 * @author Sarge
 */
public class ElementLoader {
	private static final DocumentBuilderFactory FACTORY = DocumentBuilderFactory.newInstance();

	/**
	 * @throws RuntimeException if the underlying XML parser cannot be instantiated
	 */
	private static DocumentBuilder parser() {
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
		final Document doc;
		try {
			doc = parser.parse(new InputSource(r));
		}
		catch(SAXException e) {
			throw new IOException("Error parsing XML document", e);
		}

		// Load tree
		final Builder root = new Builder();
		load(doc.getDocumentElement(), root);

		// Construct root element
		return root.build();
	}

	/**
	 * Recursively loads a tree of elements.
	 * @param node			Node
	 * @param builder		Builder
	 */
	private void load(Node node, Builder builder) {
		// Init element
		builder.name(node.getNodeName());

		// Load attributes
		final NamedNodeMap map = node.getAttributes();
		final int len = map.getLength();
		for(int n = 0; n < len; ++n) {
			final Node attr = map.item(n);
			builder.attribute(attr.getNodeName(), attr.getNodeValue());
		}

		// Load text content and child elements
		final NodeList nodes = node.getChildNodes();
		final int count = nodes.getLength();
		for(int n = 0; n < count; ++n) {
			final Node child = nodes.item(n);
			switch(child.getNodeType()) {
				case Node.ELEMENT_NODE -> {
					// Recurse to child elements
					final Builder sub = builder.child();
					load(child, sub);
					sub.end();
				}

				case Node.TEXT_NODE -> {
					// Load optional text content
					final String text = child.getNodeValue().trim();
					if(!text.isEmpty()) {
						builder.text(text);
					}
				}
			}
		}
	}
}
