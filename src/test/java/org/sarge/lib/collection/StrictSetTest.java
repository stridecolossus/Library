package org.sarge.lib.collection;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class StrictSetTest {
	private Set<String> set;

	@BeforeEach
	public void before() {
		set = new StrictSet<>();
	}

	@Test
	public void addNull() {
		assertThrows(IllegalArgumentException.class, () -> set.add(null));
	}

	@Test
	public void addDuplicate() {
		final String str = new String();
		set.add(str);
		assertThrows(IllegalArgumentException.class, () -> set.add(str));
	}

	@Test
	public void removeNull() {
		assertThrows(IllegalArgumentException.class, () -> set.remove(null));
	}

	@Test
	public void removeNotPresent() {
		assertThrows(IllegalArgumentException.class, () -> set.remove(new String()));
	}
}
