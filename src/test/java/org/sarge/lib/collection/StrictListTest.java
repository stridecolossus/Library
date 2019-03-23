package org.sarge.lib.collection;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class StrictListTest {
	private List<String> list;

	@BeforeEach
	public void before() {
		list = new StrictList<>();
	}

	@Test
	public void addNull() {
		assertThrows(IllegalArgumentException.class, () -> list.add(null));
	}

	@Test
	public void addDuplicate() {
		final String str = new String();
		list.add(str);
		assertThrows(IllegalArgumentException.class, () -> list.add(str));
	}

	@Test
	public void removeNull() {
		assertThrows(IllegalArgumentException.class, () -> list.remove(null));
	}

	@Test
	public void removeNotPresent() {
		assertThrows(IllegalArgumentException.class, () -> list.remove(new String()));
	}
}
