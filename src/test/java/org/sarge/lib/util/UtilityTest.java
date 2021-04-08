package org.sarge.lib.util;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

public class UtilityTest {
	@Test
	void map() {
		final var map = Utility.map(new String[]{"1", "2"}, Integer::parseInt);
		assertNotNull(map);
		assertEquals(2, map.size());
		assertEquals("1", map.get(1));
		assertEquals("2", map.get(2));
	}

	@Test
	void flatten() {
		interface Node {
			Stream<Node> children();
		}
		final Node root = mock(Node.class);
		final Node child = mock(Node.class);
		when(root.children()).thenReturn(Stream.of(child));
		assertArrayEquals(new Node[]{root, child}, Utility.flatten(root, Node::children).toArray());
	}
}
