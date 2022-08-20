package org.sarge.lib.util;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

public class UtilityTest {
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

	@Test
	void distinct() {
		assertEquals(true, Utility.distinct(List.of()));
		assertEquals(true, Utility.distinct(List.of(1)));
		assertEquals(false, Utility.distinct(List.of(1, 1)));
	}
}
