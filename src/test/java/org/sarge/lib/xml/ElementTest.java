package org.sarge.lib.xml;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Optional;

import javax.lang.model.element.Modifier;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.sarge.lib.util.Converter;
import org.sarge.lib.xml.Element.Attribute;
import org.sarge.lib.xml.Element.Builder;
import org.sarge.lib.xml.Element.ElementException;

public class ElementTest {
	private static final Converter<Modifier> CUSTOM = Converter.enumeration(Modifier.class);

	@Test
	@DisplayName("Construct simple element")
	public void constructor() {
		final Element element = Element.of("element");
		assertNotNull(element);
		assertEquals("element", element.name());
		assertEquals(null, element.parent());
		assertEquals(true, element.isRoot());
		assertNotNull(element.children());
		assertEquals(0, element.children().count());
	}

	@Test
	@DisplayName("Clone a child element")
	public void copyConstructor() {
		final Element child = new Element.Builder("child").add(Element.of("leaf")).build();
		new Element.Builder("element").add(child).build();
		final Element copy = new Element(child);
		assertEquals("child", copy.name());
		assertEquals(null, copy.parent());
		assertEquals(true, copy.isRoot());
		assertArrayEquals(new Element[]{Element.of("leaf")}, copy.children().toArray());
	}

	@Nested
	@DisplayName("Attribute accessor tests")
	class Attributes {
		private Element element;

		@BeforeEach
		public void before() {
			element = new Element.Builder("element")
				.attribute("name", "value")
				.attribute("integer", 1)
				.attribute("long", 2L)
				.attribute("float", 3f)
				.attribute("boolean", true)
				.attribute("enum", Modifier.NATIVE)
				.build();
		}

		@Test
		public void attribute() {
			final Attribute attr = element.attribute("name");
			assertNotNull(attr);
			assertEquals("value", attr.get());
		}

		@Test
		public void optionalAttribute() {
			assertEquals(Optional.of("value"), element.attribute("name").optional());
			assertEquals(Optional.empty(), element.attribute("cobblers").optional());
		}

		@Test
		public void optionalAttributeConverter() {
			assertEquals(Optional.of(1), element.attribute("integer").optional(Converter.INTEGER));
			assertEquals(Optional.empty(), element.attribute("cobblers").optional(Converter.INTEGER));
		}

		@Test
		public void attributeInvalidFormat() {
			assertThrows(ElementException.class, () -> element.attribute("name").toInteger());
			assertThrows(ElementException.class, () -> element.attribute("name").toLong());
			assertThrows(ElementException.class, () -> element.attribute("name").toFloat());
			assertThrows(ElementException.class, () -> element.attribute("name").toBoolean());
			assertThrows(ElementException.class, () -> element.attribute("name").toValue(CUSTOM));
		}

		@Test
		public void toText() {
			assertEquals("value", element.attribute("name").toText());
			assertEquals("def", element.attribute("cobblers").toText("def"));
		}

		@Test
		public void toInteger() {
			assertEquals(1, element.attribute("integer").toInteger());
			assertEquals(42, element.attribute("cobblers").toInteger(42));
		}

		@Test
		public void toLong() {
			assertEquals(2L, element.attribute("long").toLong());
			assertEquals(42L, element.attribute("cobblers").toLong(42L));
		}

		@Test
		public void toFloat() {
			assertEquals(3f, element.attribute("float").toFloat(), 0.0001f);
			assertEquals(42f, element.attribute("cobblers").toFloat(42f), 0.0001f);
		}

		@Test
		public void toBoolean() {
			assertEquals(true, element.attribute("boolean").toBoolean());
			assertEquals(true, element.attribute("cobblers").toBoolean(true));
		}

		@Test
		public void toValue() {
			assertEquals(Modifier.NATIVE, element.attribute("enum").toValue(CUSTOM));
			assertEquals(Modifier.NATIVE, element.attribute("cobblers").toValue(Modifier.NATIVE, CUSTOM));
		}
	}

	@Nested
	@DisplayName("XML tree tests")
	class Children {
		private Element parent;

		@BeforeEach
		public void before() {
			parent = new Element.Builder("parent")
				.child("child")
					.add("leaf")
				.end()
				.build();
		}

		@Test
		public void children() {
			final Element child = parent.child();
			assertNotNull(parent.children());
			assertArrayEquals(new Element[]{child}, parent.children().toArray());
		}

		@Test
		public void child() {
			final Element child = parent.child();
			assertEquals("child", child.name());
			assertEquals(false, child.isRoot());
			assertEquals(parent, child.parent());
		}

		@Test
		public void childMissing() {
			assertThrows(ElementException.class, () -> Element.of("parent").child());
		}

		@Test
		public void childByName() {
			assertNotNull(parent.child("child"));
		}

		@Test
		public void childByNameMissing() {
			assertThrows(ElementException.class, () -> parent.child("cobblers"));
		}

	    @Test
	    public void find() {
	        assertNotNull(parent.find().isPresent());
	        assertEquals(true, parent.find().isPresent());
	    }

	    @Test
	    public void findEmpty() {
	    	final Element root = Element.of("root");
	        assertEquals(Optional.empty(), root.find());
	    }

	    @Test
	    public void findByName() {
	        assertNotNull(parent.find("child").isPresent());
	        assertEquals(true, parent.find("child").isPresent());
	        assertEquals(Optional.empty(), parent.find("cobblers"));
	    }

	    @Test
		public void path() {
	    	final Element child = parent.child();
	    	final Element leaf = child.child();
			assertArrayEquals(new Element[]{parent}, parent.path().toArray());
			assertArrayEquals(new Element[]{parent, child}, child.path().toArray());
			assertArrayEquals(new Element[]{parent, child, leaf}, leaf.path().toArray());
		}
	}

	@Nested
	@DisplayName("Equality tests")
	class Equality {
		@Test
		public void equals() {
			final Element element = new Element.Builder("element").attribute("name", "value").build();
			assertEquals(element, element);
			assertNotEquals(element, null);
			assertNotEquals(element, Element.of("element"));
		}
	}

	@Nested
	@DisplayName("Additional builder tests")
	class Building {
		private Builder builder;

		@BeforeEach
		public void before() {
			builder = new Element.Builder("builder");
		}

		@Test
		public void childNotRootElement() {
			final Element other = new Element.Builder("other").child("child").end().build();
			final Element child = other.child();
			assertThrows(IllegalArgumentException.class, () -> builder.add(child));
		}

		@Test
		public void endRootElement() {
			assertThrows(IllegalStateException.class, () -> builder.end());
		}

		@Test
		public void buildChildElement() {
			assertThrows(IllegalStateException.class, () -> builder.child("child").build());
		}
	}

	@Nested
	@DisplayName("Element exceptions")
	class Exceptions {
		@Test
		public void exception() {
			// Create XML tree
			final Element child = Element.of("child");
			new Element.Builder("parent")
				.child("child").end()
				.add(child)
				.child("other").end()
				.child("child").end()
				.build();

			// Generate exception
			final ElementException e = child.exception("reason");
			assertNotNull(e);
			assertEquals(child, e.element());

			// Check message
			e.setFile("filename");
			assertEquals("reason at /parent/child[2] in filename", e.getMessage());
		}
	}
}
