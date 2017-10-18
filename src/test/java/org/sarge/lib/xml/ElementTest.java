package org.sarge.lib.xml;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.sarge.lib.util.AbstractTest;

public class ElementTest extends AbstractTest {
	private Element parent, child;

	@Before
	public void before() {
		parent = new Element.Builder("parent").attribute("key", "value").text("text").build();
		child = new Element.Builder("child").parent(parent).build();
	}
	
	@Test
	public void constructor() {
		assertEquals("parent", parent.name());
		assertNotNull(parent.attributes());
		assertEquals("text", parent.text());
	}
	
	@Test
	public void parent() {
		assertEquals(null, parent.parent());
		assertEquals(parent, child.parent());
	}
	
	@Test
	public void children() {
		assertNotNull(parent.children());
		assertArrayEquals(new Element[]{child}, parent.children().toArray());
	}
	
	@Test
	public void child() {
		assertEquals(child, parent.child());
	}
	
	@Test
	public void simpleChild() {
		parent = new Element.Builder("parent").child("child").build();
		parent.child("child");
	}

	@Test(expected = ElementException.class)
	public void childMissing() {
		child.child();
	}
	
	@Test
	public void childByName() {
		assertEquals(child, parent.child("child"));
	}

	@Test(expected = ElementException.class)
	public void childByNameMissing() {
		parent.child("cobblers");
	}
	
    @Test
    public void optionalChild() {
        assertEquals(Optional.of(child), parent.optionalChild());
    }

    @Test
    public void optionalChildEmpty() {
        assertEquals(Optional.empty(), child.optionalChild());
    }
    
    @Test
    public void optionalNamedChild() {
        assertEquals(Optional.of(child), parent.optionalChild("child"));
    }

    @Test
    public void optionalNamedChildEmpty() {
        assertEquals(Optional.empty(), parent.optionalChild("none"));
    }
    
	@Test
	public void attributes() {
		assertEquals("value", parent.attributes().toString("key", null));
	}
	
	@Test
	public void path() {
	    final Element leaf = new Element.Builder("leaf").parent(child).build();
		assertArrayEquals(new Element[]{parent, child}, child.path().toArray());
		assertArrayEquals(new Element[]{parent, child, leaf}, leaf.path().toArray());
	}
}
