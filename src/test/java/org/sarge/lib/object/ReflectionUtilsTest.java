package org.sarge.lib.object;

import static java.util.stream.Collectors.toList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Field;
import java.util.Collection;

import org.junit.Test;

public class ReflectionUtilsTest {
	@Test
	public void getMembers() {
		// Get class-members
		final Collection<String> result = ReflectionUtils.getMembers(MockClass.class).map(Field::getName).collect(toList());
		assertNotNull(result);
		assertEquals(6, result.size());
		
		// Check class-members
		assertTrue(result.contains("num"));
		assertTrue(result.contains("str"));
		assertTrue(result.contains("empty"));

		// Check super-class members
		assertTrue(result.contains("elementData"));
		assertTrue(result.contains("size"));
		assertTrue(result.contains("modCount"));
		
	}
	
	@Test
	public void getValue() throws Exception {
		final Field field = String.class.getDeclaredField("hash");
		final String str = "string";
		assertEquals(str.hashCode(), ReflectionUtils.getValue(field, str));
	}
}
