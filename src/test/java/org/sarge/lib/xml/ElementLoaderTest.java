package org.sarge.lib.xml;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.io.StringReader;

import org.junit.Before;
import org.junit.Test;
import org.sarge.lib.util.StringUtil;

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
		assertEquals("parent", parent.name());
		assertEquals(null, parent.parent());
		assertEquals(StringUtil.EMPTY_STRING, parent.text());

		// Check attributes
		assertEquals("value", parent.attributes().toString("name", null));

		// Check children
		assertNotNull(parent.children());
		assertEquals(1, parent.children().count());

		// Check child
		final Element child = parent.children().iterator().next();
		assertEquals("child", child.name());
		assertEquals(parent, child.parent());
		assertEquals("text", child.text());
		assertNotNull(child.children());
		assertEquals(0, child.children().count());
	}
}
