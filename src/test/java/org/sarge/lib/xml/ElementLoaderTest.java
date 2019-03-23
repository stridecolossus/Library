package org.sarge.lib.xml;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.io.StringReader;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ElementLoaderTest {
	private ElementLoader loader;

	@BeforeEach
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
		assertEquals("parent", parent.name());
		assertEquals(null, parent.parent());
		assertEquals(true, parent.isRoot());
		assertEquals(StringUtils.EMPTY, parent.text());

		// Check attributes
		assertNotNull(parent.attribute("name"));
		assertEquals("value", parent.attribute("name").toText());

		// Check children
		assertNotNull(parent.children());
		assertEquals(1, parent.children().count());

		// Check child
		final Element child = parent.children().iterator().next();
		assertEquals("child", child.name());
		assertEquals(parent, child.parent());
		assertEquals(false, child.isRoot());
		assertEquals("text", child.text());
		assertNotNull(child.children());
		assertEquals(0, child.children().count());
	}
}
