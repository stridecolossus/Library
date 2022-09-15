package org.sarge.lib.element;

import static org.junit.jupiter.api.Assertions.*;

import java.util.function.Function;

import org.junit.jupiter.api.*;
import org.sarge.lib.element.Element.ElementException;

public class LoaderRegistryTest {
	private LoaderRegistry<String> registry;

	@BeforeEach
	void before() {
		registry = new LoaderRegistry<>() {
			@Override
			protected void init() {
				register("default", Element::name);
			}
		};
	}

	@DisplayName("A loader can be registered by name")
	@Test
	void register() {
		registry.register("name", Element::name);
		assertEquals("name", registry.load(new Element("name")));
	}

	@DisplayName("A loader can be registered by name")
	@Test
	void literal() {
		registry.literal("name", "literal");
		assertEquals("literal", registry.load(new Element("name")));
	}

	@DisplayName("A loader can be registered as a compound transform and constructor")
	@Test
	void compound() {
		registry.register("name", Integer::parseInt, String::valueOf);
		assertEquals("3", registry.load(new Element("name", "3")));
	}

	@DisplayName("A loader can be registered at initialisation time")
	@Test
	void init() {
		assertEquals("default", registry.load(new Element("default")));
	}

	@DisplayName("An object cannot be loaded if a matching loader has not been registered")
	@Test
	void unknown() {
		assertThrows(ElementException.class, () -> registry.load(new Element("cobblers")));
	}

	@DisplayName("An exception thrown by a loader is captured by the registry")
	@Test
	void error() {
		final Function<Element, String> doh = __ -> {
			throw new RuntimeException("doh");
		};
		registry.register("doh", doh);
		assertThrows(ElementException.class, () -> registry.load(new Element("doh")));
	}
}
