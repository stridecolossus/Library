package org.sarge.lib.element;

import static org.junit.jupiter.api.Assertions.*;

import java.util.*;
import java.util.function.Function;

import org.junit.jupiter.api.*;
import org.sarge.lib.element.Element.*;

public class ElementTest {
	@DisplayName("A simple element...")
	@Nested
	class Simple {
		private Element element;

		@BeforeEach
		void before() {
			element = new Element("name");
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
			final Content content = element.text();
			assertNotNull(content);
			assertEquals(false, content.isPresent());
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
			assertEquals(Optional.empty(), element.optional("whatever"));
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

	@Nested
	class ContentTests {
		private Element element;

		@BeforeEach
		void before() {
			element = new Element("name");
		}

		private Content content(String value) {
			return element.new Content(value) {
				@Override
				protected String message() {
					return "message";
				}
			};
		}

		@Test
		void isPresent() {
			final Content content = content("value");
			assertEquals(true, content.isPresent());
		}

		@Test
		void transform() {
			final Content content = content("value");
			assertEquals("value", content.transform(Function.identity()));
			assertEquals("value", content.toString());
		}

		@Test
		void toInteger() {
			final Content content = content("3");
			assertEquals(3, content.toInteger());
		}

		@Test
		void toFloat() {
			final Content content = content("0.5");
			assertEquals(0.5f, content.toFloat());
		}

		@Test
		void toBoolean() {
			final Content content = content("true");
			assertEquals(true, content.toBoolean());
		}

		@Test
		void empty() {
			final Content content = content(null);
			assertEquals(false, content.isPresent());
			assertThrows(ElementException.class, () -> content.toString());
		}
	}

	@DisplayName("An element attribute...")
	@Nested
	class Attributes {
		private Element element;

		@BeforeEach
		void before() {
			element = new Element("name", Map.of("key", "value"), null);
		}

		@DisplayName("can be queried from the element")
		@Test
		void attributes() {
			assertEquals(Map.of("key", "value"), element.attributes());
			assertNotNull(element.attribute("key"));
		}

		@DisplayName("can be optional")
		@Test
		void optional() {
			final Content attr = element.attribute("whatever");
			assertNotNull(attr);
			assertEquals(false, attr.isPresent());
		}
	}

	@Nested
	class TextContentTests {
		@DisplayName("An element can contain text content")
		@Test
		void text() {
			final Element element = new Element("name", "text");
			final Content content = element.text();
			assertNotNull(content);
			assertEquals("text", content.toString());
		}

		@DisplayName("An element with no text cannot be queried")
		@Test
		void empty() {
			final Element element = new Element("name");
			final Content content = element.text();
			assertNotNull(content);
			assertThrows(ElementException.class, () -> content.toString());
		}
	}

	@Test
	void equals() {
		final Element element = new Element("name", Map.of("key", "value"), "text");
		assertEquals(element, element);
		assertEquals(element, new Element("name", Map.of("key", "value"), "text"));
		assertNotEquals(element, null);
		assertNotEquals(element, new Element("whatever"));
	}

	@DisplayName("A single child element...")
	@Nested
	class Child {
		private Element parent;
		private Element child;

		@BeforeEach
		void before() {
			child = new Element("child");
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
			assertEquals(child, parent.child("child"));
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

		@DisplayName("is mandatory")
		@Test
		void child() {
			assertThrows(ElementException.class, () -> parent.child("whatever"));
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
			one = new Element("child");
			two = new Element("child");
			other = new Element("other");
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
			assertEquals(one, parent.child("child"));
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
			final Element child = new Element("child");

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

			final Element child = new Element("child");
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
