package org.sarge.lib.util;

import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.io.StringReader;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.sarge.lib.util.Element.ElementException;
import org.sarge.lib.util.Element.Handler;
import org.sarge.lib.util.Element.Handler.Index;

public class ElementTest {
	private static final String XML = "xml";
	private static final String CHILD = "child";
	private static final String TEXT = "text";
	private static final String ATTRIBUTE = "attribute";
	private static final String UNKNOWN = "cobblers";

	private Element xml;

	@BeforeEach
	void before() {
		xml = new Element.Builder()
				.attribute(ATTRIBUTE, true)
				.text(TEXT)
				.build();
	}

	@Test
	void constructor() {
		assertEquals(XML, xml.name());
		assertEquals(TEXT, xml.text());
		assertNotNull(xml.attributes());
		assertEquals(Optional.empty(), xml.parent());
		assertEquals(true, xml.isRoot());
		assertEquals(0, xml.index());
	}

	@Test
	void path() {
		assertNotNull(xml.path());
		assertArrayEquals(new Element[]{xml}, xml.path().toArray());
	}

	@Test
	void map() {
		@SuppressWarnings("unchecked")
		final Function<Element, Object> function = mock(Function.class);
		xml.map(function);
		verify(function).apply(xml);
	}

	@Test
	void equals() {
		assertEquals(true, xml.equals(xml));
		assertEquals(false, xml.equals(null));
		assertEquals(false, xml.equals(Element.of("other")));
	}

	@Nested
	class ChildrenTests {
		private Element parent, child, sibling;

		@BeforeEach
		void before() {
			child = Element.of(CHILD);
			sibling = Element.of(CHILD);
			parent = new Element.Builder().child(child).child(sibling).build();
		}

		@Test
		void constructor() {
			assertEquals(Optional.of(parent), child.parent());
		}

		@Test
		void count() {
			assertEquals(2, parent.count());
			assertEquals(0, child.count());
		}

		@Test
		void children() {
			assertArrayEquals(new Element[]{child, sibling}, parent.children().toArray());
			assertArrayEquals(new Element[]{child, sibling}, parent.children(CHILD).toArray());
			assertEquals(0, parent.children(UNKNOWN).count());
		}

		@Test
		void index() {
			assertEquals(0, child.index());
			assertEquals(1, sibling.index());
		}

		@DisplayName("Cannot retrieve an unknown child element")
		@Test
		void childNotPresent() {
			assertThrows(ElementException.class, () -> parent.child(UNKNOWN));
		}

		@DisplayName("Retrieve an optional child element")
		@Test
		void optional() {
			assertEquals(Optional.of(child), parent.optional(CHILD));
		}

		@DisplayName("Retrieve an empty optional child element")
		@Test
		void optionalNotPresent() {
			assertEquals(Optional.empty(), parent.optional(UNKNOWN));
		}

		@DisplayName("Retrieve a single child by name")
		@Test
		void child() {
			child = Element.of(CHILD);
			parent = new Element.Builder().child(child).build();
			assertEquals(child, parent.child());
			assertEquals(child, parent.child(CHILD));
		}

		@DisplayName("Cannot retrieve a single child if the parent has multiple children with the same name")
		@Test
		void childMultiple() {
			assertThrows(ElementException.class, () -> parent.child());
		}
	}

	@Nested
	class ContentTests {
		private Element parent, child;

		@BeforeEach
		void before() {
			parent = new Element.Builder().child(CHILD, TEXT).build();
			child = parent.child(CHILD);
		}

		@Test
		void text() {
			assertEquals(TEXT, parent.text(CHILD));
		}

		@DisplayName("Cannot retrieve the text of unknown child element")
		@Test
		void empty() {
			assertThrows(ElementException.class, () -> parent.text(UNKNOWN));
		}

		@DisplayName("Retrieve the text content of an element")
		@Test
		void content() {
			assertEquals(Optional.of(TEXT), child.content());
			assertEquals(Optional.of(TEXT), parent.content(CHILD));
		}

		@DisplayName("Retrieve the empty text content of an unknown element")
		@Test
		void contentEmpty() {
			assertEquals(Optional.empty(), parent.content());
			assertEquals(Optional.empty(), parent.content(UNKNOWN));
		}
	}

	@Nested
	class AttributeTests {
		@Test
		void attributes() {
			assertEquals(Map.of(ATTRIBUTE, "true"), xml.attributes());
			assertEquals(Optional.of("true"), xml.attribute(ATTRIBUTE));
		}

		@Test
		void empty() {
			assertEquals(Map.of(), Element.of("empty").attributes());
		}

		@DisplayName("Retrieve a boolean attribute")
		@Test
		void bool() {
			assertEquals(true, xml.attribute(ATTRIBUTE, false));
			assertEquals(false, xml.attribute(UNKNOWN, false));
			assertEquals(true, xml.attribute(UNKNOWN, true));
		}

		@DisplayName("Retrieve an empty attribute")
		@Test
		void attributeNotPresent() {
			assertEquals(Optional.empty(), xml.attribute(UNKNOWN));
		}
	}

	@Nested
	class HandlerTests {
		private Consumer<String> consumer;

		@SuppressWarnings("unchecked")
		@BeforeEach
		void before() {
			consumer = mock(Consumer.class);
		}

		@DisplayName("Create a handler that delegates to the children of the element")
		@Test
		void children() {
			final Element parent = new Element.Builder().child(xml).build();
			final Handler handler = spy(Handler.class);
			final Handler children = handler.children();
			assertNotNull(children);
			children.accept(parent);
			verify(handler).accept(xml);
		}

		@DisplayName("Create a handler for a given element transformer")
		@Test
		void transformer() {
			final Handler handler = Handler.of((Element e) -> e.text(), consumer);
			handler.accept(xml);
			verify(consumer).accept(TEXT);
		}

		@DisplayName("Create a handler that first applies a converter to the element text")
		@Test
		void converter() {
			final Handler handler = Handler.of(Converter.STRING, consumer);
			handler.accept(xml);
			verify(consumer).accept(TEXT);
		}

		@DisplayName("Create a handler for the element text")
		@Test
		void text() {
			final Handler handler = Handler.of(consumer);
			handler.accept(xml);
			verify(consumer).accept(TEXT);
		}

		@Nested
		class IndexHandlerTests {
			private Handler handler;

			@BeforeEach
			void before() {
				handler = mock(Handler.class);
			}

			@DisplayName("Delegate an element to a handler by name")
			@Test
			void index() {
				final Index index = new Index(XML -> handler, null);
				index.accept(xml);
				verify(handler).accept(xml);
			}

			@DisplayName("Delegate an element to the defalt handler if no explicit mapping is configured")
			@Test
			void def() {
				final Index index = new Index(whatever -> null, handler);
				index.accept(xml);
				verify(handler).accept(xml);
			}

			@DisplayName("Fail for an element with no mapping")
			@Test
			void fail() {
				final Index index = new Index(whatever -> null, null);
				assertThrows(ElementException.class, () -> index.accept(xml));
			}
		}
	}

	@Nested
	class ExceptionTests {
		private static final String MESSAGE = "message";

		@Test
		void constructor() {
			final var cause = new IllegalArgumentException();
			final ElementException e = xml.new ElementException(MESSAGE, cause);
			assertEquals(xml, e.element());
			assertEquals("message at /xml", e.getMessage());
			assertEquals(cause, e.getCause());
		}

		@DisplayName("An element can throw an exception with itself as the cause")
		@Test
		void helper() {
			assertEquals(xml.new ElementException(MESSAGE).getMessage(), xml.exception(MESSAGE).getMessage());
		}

		@DisplayName("An element exception should contain the path to the offending element")
		@Test
		void child() {
			final Element child = Element.of(CHILD);
			new Element.Builder().name("parent").child(child).build();
			assertEquals("message at /parent/child", child.exception(MESSAGE).getMessage());
		}
	}

	@Nested
	class BuilderTests {
		private Element child;

		@BeforeEach
		void before() {
			child = Element.of(CHILD);
		}

		@DisplayName("Build a simple element")
		@Test
		void build() {
			final Element result = new Element.Builder().build();
			assertNotNull(result);
			assertEquals(XML, result.name());
			assertEquals(null, result.text());
			assertEquals(Map.of(), result.attributes());
			assertEquals(result, Element.of(XML));
		}

		@DisplayName("Build an element with an existing child")
		@Test
		void childExisting() {
			final Element parent = new Element.Builder().child(child).build();
			assertEquals(Optional.of(parent), child.parent());
			assertArrayEquals(new Element[]{child}, parent.children().toArray());
			assertEquals(1, parent.count());
			assertEquals(0, child.count());
		}

		@DisplayName("Build a child element")
		@Test
		void child() {
			final Element parent = new Element.Builder()
					.child()
						.name(CHILD)
						.end()
					.build();
			child = parent.child(CHILD);
			assertEquals(Optional.of(parent), child.parent());
			assertArrayEquals(new Element[]{child}, parent.children().toArray());
			assertEquals(1, parent.count());
			assertEquals(0, child.count());
		}

		@DisplayName("Cannot build an element with an incomplete child")
		@Test
		void buildChildNotCompleted() {
			assertThrows(IllegalStateException.class, () -> new Element.Builder().child().build());
		}

		@DisplayName("Cannot end a builder that is not constructing a child element")
		@Test
		void endChildNotStarted() {
			assertThrows(IllegalStateException.class, () -> new Element.Builder().end());
		}

		@DisplayName("Cannot add an existing child that already has a parent")
		@Test
		void childAlreadyParent() {
			new Element.Builder().child(child).build();
			assertThrows(IllegalStateException.class, () -> new Element.Builder().child(child));
		}
	}

	@Nested
	class LoaderTests {
		private Element.Loader loader;

		@BeforeEach
		void before() {
			loader = new Element.Loader();
		}

		@Test
		void load() throws IOException {
			// Create XML
			final String xml =
					"""
					<parent one="1" two="2">
						<child>
							text
						</child>

						<sibling>
							<other />
						</sibling>
					</parent>
					""";

			// Load XML
			final Element root = loader.load(new StringReader(xml));
			assertNotNull(root);

			// Check parent element
			assertEquals("parent", root.name());
			assertEquals(2, root.count());
			assertEquals(Map.of("one", "1", "two", "2"), root.attributes());

			// Check children
			final var children = root.children().collect(toList());
			assertEquals(2, children.size());
			assertEquals(1, root.children("child").count());
			assertEquals(1, root.children("sibling").count());

			// Check child
			final Element child = children.get(0);
			assertEquals(CHILD, child.name());
			assertEquals(TEXT, child.text().toString());
			assertEquals(0, child.children().count());

			// Check nested child
			assertEquals(1, children.get(1).children().count());
		}
	}
}
