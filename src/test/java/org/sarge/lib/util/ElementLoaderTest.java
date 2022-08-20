package org.sarge.lib.util;

import static org.junit.jupiter.api.Assertions.*;

import java.io.*;
import java.util.*;

import org.junit.jupiter.api.*;

public class ElementLoaderTest {
	private ElementLoader loader;

	@BeforeEach
	void before() {
		loader = new ElementLoader();
	}

	@Test
	void load() throws IOException {
		// Build XML document
		final String xml = """
				<root attribute="value">
					<child>
						text
					</child>
					<child />
				</root>
		""";

		// Load root element
		final Element root = loader.load(new StringReader(xml));
		assertNotNull(root);
		assertEquals("root", root.name());
		assertEquals(Map.of("attribute", "value"), root.attributes());
		assertEquals(Optional.empty(), root.parent());
		assertEquals(2, root.children().count());

		// Check children
		final Element child = root.children().iterator().next();
		assertNotNull(child);
		assertEquals("child", child.name());
		assertEquals(Optional.of(root), child.parent());
		assertEquals("text", child.text());
		assertEquals(0, child.children().count());
	}
}
