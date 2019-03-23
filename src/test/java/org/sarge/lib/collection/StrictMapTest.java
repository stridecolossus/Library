package org.sarge.lib.collection;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class StrictMapTest {
	private Map<String, Object> map;

	@BeforeEach
	public void before() {
		map = new StrictMap<>();
	}

	@Test
	public void nullValue() {
		map.put(new String(), null);
	}

	@Test
	public void nullKey() {
		assertThrows(IllegalArgumentException.class, () -> map.put(null, new Object()));
	}

	@Test
	public void duplicateKey() {
		final String key = new String();
		map.put(key, new Object());
		assertThrows(IllegalArgumentException.class, () -> map.put(key, new Object()));
	}

	@Test
	public void removeNull() {
		assertThrows(IllegalArgumentException.class, () -> map.remove(null));
	}

	@Test
	public void removeNotPresent() {
		assertThrows(IllegalArgumentException.class, () -> map.remove(new String()));
	}
}
