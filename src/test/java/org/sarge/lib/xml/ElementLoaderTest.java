package org.sarge.lib.xml;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.io.StringReader;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;

public class ElementLoaderTest {
	private ElementLoader loader;
	
	@Before
	public void before() {
		loader = new ElementLoader();
	}
	
	@Test
	public void load() throws IOException {
		// Create an XML document
		final String xml =
			"<parent name=\"value\">" +
				"<child>" +
					"text" +
				"</child>" +
			"</parent>";
		
		// Load XML
		final Element parent = loader.load(new StringReader(xml));
		assertNotNull(parent);
		
		// Check parent
		assertEquals("parent", parent.getName());
		assertEquals(null, parent.getParent());
		assertEquals(Optional.empty(), parent.getText());
		
		// Check attributes
		assertNotNull(parent.getAttributes());
		assertEquals(1, parent.getAttributes().size());
		assertEquals("value", parent.getAttributes().get("name"));

		// Check children
		assertNotNull(parent.getChildren());
		assertEquals(1, parent.getChildren().count());

		// Check child
		final Element child = parent.getChildren().iterator().next();
		assertEquals("child", child.getName());
		assertEquals(parent, child.getParent());
		assertEquals(Optional.of("text"), child.getText());
		assertNotNull(child.getChildren());
		assertEquals(0, child.getChildren().count());
	}
}
