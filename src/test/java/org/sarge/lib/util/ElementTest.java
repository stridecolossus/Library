package org.sarge.lib.util;

import static org.junit.jupiter.api.Assertions.*;

import java.util.*;

import org.junit.jupiter.api.*;
import org.sarge.lib.util.Element.ElementException;

public class ElementTest {
	@DisplayName("A simple element...")
	@Nested
	class Simple {
		private Element element;

		@BeforeEach
		void before() {
			element = Element.of("name");
		}

		@DisplayName("can be constructed")
		@Test
		void constructor() {
			assertNotNull(element);
			assertEquals("name", element.name());
		}

		@DisplayName("has no attributes")
		@Test
		void attributes() {
			assertEquals(Map.of(), element.attributes());
			assertEquals(Optional.empty(), element.optional("whatever"));
		}

		@DisplayName("has empty text content")
		@Test
		void text() {
			assertEquals("", element.text());
		}

		@DisplayName("has an implicit index of one")
		@Test
		void index() {
			assertEquals(1, element.index());
		}

		@DisplayName("has no parent")
		@Test
		void parent() {
			assertEquals(Optional.empty(), element.parent());
		}

		@DisplayName("has a path consisting of itself")
		@Test
		void path() {
			assertNotNull(element.path());
			assertEquals(List.of(element), element.path());
		}

		@DisplayName("has no children")
		@Test
		void children() {
			assertEquals(0, element.size());
			assertNotNull(element.children());
			assertEquals(List.of(), element.children().toList());
			assertEquals(List.of(), element.children("whatever").toList());
			assertEquals(Optional.empty(), element.child("whatever"));
		}

		@DisplayName("can be mapped by a transform function")
		@Test
		void map() {
			assertEquals("name", element.map(Element::name));
		}

		@DisplayName("can raise an exception")
		@Test
		void exception() {
			final var e = element.new ElementException("doh");
			assertEquals("doh at /name", e.getMessage());
		}
	}

	@DisplayName("An element attribute...")
	@Nested
	class Attributes {
		private Element element;

		@BeforeEach
		void before() {
			element = new Element("name", Map.of("key", "value"), "");
		}

		@DisplayName("can be queried from the element")
		@Test
		void attributes() {
			assertEquals(Map.of("key", "value"), element.attributes());
			assertEquals(Optional.of("value"), element.optional("key"));
			assertEquals("value", element.attribute("key"));
		}

		@DisplayName("can be optional")
		@Test
		void optional() {
			assertEquals(Optional.empty(), element.optional("whatever"));
		}

		@DisplayName("can be mandatory")
		@Test
		void mandatory() {
			assertThrows(ElementException.class, () -> element.attribute("whatever"));
		}
	}

	@DisplayName("An element can contain text content")
	@Test
	void text() {
		final Element element = new Element("name", Map.of(), "text");
		assertEquals("text", element.text());
	}

	@Test
	void equals() {
		final Element element = new Element("name", Map.of("key", "value"), "text");
		assertEquals(element, element);
		assertEquals(element, new Element("name", Map.of("key", "value"), "text"));
		assertNotEquals(element, null);
		assertNotEquals(element, Element.of("whatever"));
	}

	@DisplayName("A single child element...")
	@Nested
	class Child {
		private Element parent;
		private Element child;

		@BeforeEach
		void before() {
			child = Element.of("child");
			parent = new Element.Builder().name("parent").child(child).build();
		}

		@DisplayName("has a parent element")
		@Test
		void parent() {
			assertEquals(Optional.of(parent), child.parent());
		}

		@DisplayName("is a child of the parent element")
		@Test
		void children() {
			assertEquals(1, parent.size());
			assertEquals(List.of(child), parent.children().toList());
			assertEquals(Optional.of(child), parent.child("child"));
			assertEquals(child, parent.first("child"));
		}

		@DisplayName("has a path starting at the root element")
		@Test
		void path() {
			assertEquals(List.of(parent, child), child.path());
		}

		@DisplayName("can raise an exception containing the path to its parent")
		@Test
		void exception() {
			final var e = child.exception("doh");
			assertEquals("doh at /parent/child", e.getMessage());
		}

		@DisplayName("can be mandatory")
		@Test
		void first() {
			assertThrows(ElementException.class, () -> parent.first("whatever"));
		}
	}

	@DisplayName("A sibling element...")
	@Nested
	class Sibling {
		private Element parent;
		private Element one, two;
		private Element other;

		@BeforeEach
		void before() {
			one = Element.of("child");
			two = Element.of("child");
			other = Element.of("other");
			parent = new Element.Builder().name("parent").child(one).child(two).child(other).build();
		}

		@DisplayName("has an index indicating its position relative to its siblings")
		@Test
		void index() {
			assertEquals(1, one.index());
			assertEquals(2, two.index());
		}

		@DisplayName("can be selected from its parent by name")
		@Test
		void children() {
			assertEquals(List.of(one, two), parent.children("child").toList());
			assertEquals(List.of(other), parent.children("other").toList());
		}

		@DisplayName("can be retrieved by name as the first sibling")
		@Test
		void child() {
			assertEquals(Optional.of(one), parent.child("child"));
		}

		@DisplayName("can raise an exception containing its sibling index")
		@Test
		void exception() {
			final var e = two.new ElementException("doh");
			assertEquals("doh at /parent/child[2]", e.getMessage());
		}
	}

	@DisplayName("The builder for an element...")
	@Nested
	class BuilderTests {
		private Element.Builder builder;

		@BeforeEach
		void before() {
			builder = new Element.Builder();
		}

		@DisplayName("can construct a simple element")
		@Test
		void build() {
			final Element element = builder
					.name("name")
					.attribute("key", "value")
					.text("text")
					.build();

			final Element expected = new Element("name", Map.of("key", "value"), "text");
			assertEquals(expected, element);
		}

		@DisplayName("can attach an existing element as a child")
		@Test
		void child() {
			final Element child = Element.of("child");

			final Element parent = builder
					.name("parent")
					.child(child)
					.build();

			assertEquals(Optional.of(parent), child.parent());
			assertEquals(List.of(child), parent.children().toList());
		}

		@DisplayName("can attach an element with text content")
		@Test
		void text() {
			final Element parent = builder
					.name("parent")
					.child("child", "text")
					.build();

			final Element expected = new Element.Builder().name("child").text("text").build();
			assertEquals(List.of(expected), parent.children().toList());
		}

		@DisplayName("can construct child elements")
		@Test
		void children() {
			final Element parent = builder
					.name("parent")
					.child()
						.name("child")
						.end()
					.child()
						.name("child")
						.end()
					.build();

			final Element child = Element.of("child");
			assertEquals(2, parent.size());
			assertEquals(List.of(child, child), parent.children().toList());
			assertEquals(List.of(child, child), parent.children("child").toList());

			final Element actual = parent.children().iterator().next();
			assertEquals(Optional.of(parent), actual.parent());
		}

		@DisplayName("cannot complete construction if a child builder has not been ended")
		@Test
		void invalid() {
			assertThrows(IllegalStateException.class, () -> builder.child().build());
		}

		@DisplayName("cannot complete a child element that has not been started")
		@Test
		void end() {
			assertThrows(IllegalStateException.class, () -> builder.end());
		}
	}
}
