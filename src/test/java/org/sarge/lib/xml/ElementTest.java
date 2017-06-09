package org.sarge.lib.xml;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Collections;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.sarge.lib.util.Converter;
import org.sarge.lib.xml.Element.ElementException;

public class ElementTest {
	private Element parent, child;
	
	@Rule
	public ExpectedException exception = ExpectedException.none();

	@Before
	public void before() {
		parent = new Element("parent", Collections.singletonMap("key", "value"), "text", null);
		child = new Element("child", Collections.emptyMap(), "", parent);
	}
	
	@Test
	public void constructor() {
		assertEquals("parent", parent.getName());
		assertNotNull(parent.getAttributes());
		assertEquals("text", parent.getText());
	}
	
	@Test
	public void getParent() {
		assertEquals(null, parent.getParent());
		assertEquals(parent, child.getParent());
	}
	
	@Test
	public void getChildren() {
		assertNotNull(parent.getChildren());
		assertEquals(1, parent.getChildren().count());
	}
	
	@Test
	public void getChild() {
		assertEquals(child, parent.getChild());
	}

	@Test(expected = ElementException.class)
	public void getChildMissing() {
		child.getChild();
	}
	
	@Test
	public void getChildByName() {
		assertEquals(child, parent.getChild("child"));
	}

	@Test(expected = ElementException.class)
	public void getChildByNameMissing() {
		parent.getChild("cobblers");
	}
	
	@Test
	public void getAttribute() {
		assertEquals("value", parent.getAttribute("key", null, Converter.STRING));
	}
	
	@Test
	public void getAttributeMissing() {
		exception.expect(ElementException.class);
		exception.expectMessage("Missing mandatory attribute: doh at /parent");
		parent.getAttribute("doh", null, Converter.STRING);
	}
	
	@SuppressWarnings("unused")
	@Test
	public void getPath() {
		new Element("middle", Collections.emptyMap(), "", parent);
		final Element other = new Element("middle", Collections.emptyMap(), "", parent);
		final Element leaf = new Element("leaf", Collections.emptyMap(), "", other);
		assertArrayEquals(new Element[]{parent, child}, child.getPath().toArray());
		assertArrayEquals(new Element[]{parent, other, leaf}, leaf.getPath().toArray());
	}
}
