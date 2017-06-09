package org.sarge.lib.xml;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.sarge.lib.xml.Element.Builder;

public class ElementBuilderTest {
	private Builder builder;
	
	@Before
	public void before() {
		builder = new Builder("name");
	}
	
	@Test
	public void build() {
		final Element e = builder.build();
		assertNotNull(e);
		assertEquals("name", e.getName());
		assertEquals("", e.getText());
		assertNotNull(e.getAttributes());
		assertTrue(e.getAttributes().isEmpty());
		assertNotNull(e.getChildren());
		assertEquals(0, e.getChildren().count());
	}
	
	@Test
	public void attribute() {
		final Element e = builder.attribute("key", "value").build();
		assertEquals("value", e.getAttributes().get("key"));
	}
	
	@Test
	public void text() {
		final Element e = builder.text("text").build();
		assertEquals("text", e.getText());
	}
	
	@Test
	public void parent() {
		final Element parent = new Element("parent");
		final Element e = builder.parent(parent).build();
		assertEquals(parent, e.getParent());
		assertEquals(1, parent.getChildren().count());
		assertEquals(e, parent.getChildren().iterator().next());
	}

	@Test
	public void child() {
		// Start a child
		final Builder child = builder.child("child");
		assertNotEquals(child, builder);
		
		// Finish child and check back to parent
		final Builder b = child.pop();
		assertEquals(builder, b);
		
		// Check resultant tree
		final Element e = builder.build();
		assertEquals(1, e.getChildren().count());
		assertNotNull(e.getChild());
		assertEquals("child", e.getChild().getName());
	}
	
	@Test(expected = IllegalStateException.class)
	public void buildPendingChild() {
		builder.child("child").build();
	}
	
	@Test(expected = IllegalStateException.class)
	public void popNoChild() {
		builder.pop();
	}
	
	@Test
	public void children() {
		builder.children("one", "two");
		final Element e = builder.build();
		assertEquals(2, e.getChildren().count());
		assertArrayEquals(new String[]{"one", "two"}, e.getChildren().map(Element::getName).toArray());
	}
}
