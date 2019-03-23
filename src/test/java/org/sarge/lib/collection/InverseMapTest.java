package org.sarge.lib.collection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.sarge.lib.collection.InverseMap.InverseHashMap;

public class InverseMapTest {
    private InverseHashMap<Integer, String> map;

    @BeforeEach
    public void before() {
        map = new InverseHashMap<>();
    }

    @Test
    public void constructor() {
        assertEquals(true, map.isEmpty());
        assertEquals(0, map.size());
    }

    @Test
    public void inverse() {
        final Map<String, Integer> inverse = map.inverse();
        assertNotNull(inverse);
        assertEquals(true, inverse.isEmpty());
        assertEquals(0, inverse.size());
    }

    @Test
    public void put() {
        map.put(1, "one");
        compare();
    }

    @Test
    public void putAll() {
        map.putAll(Collections.singletonMap(1, "one"));
        compare();
    }

    @Test
    public void remove() {
        map.put(1, "one");
        assertEquals("one", map.remove(1));
        assertEquals(true, map.isEmpty());
        assertEquals(true, map.inverse().isEmpty());
    }

    @Test
    public void removeValue() {
        map.put(1, "one");
        assertEquals(Integer.valueOf(1), map.removeValue("one"));
        assertEquals(true, map.isEmpty());
        assertEquals(true, map.inverse().isEmpty());
    }

    private void compare() {
        assertEquals(1, map.size());
        compare(Collections.singleton(1), map.keySet());
        compare(Collections.singleton("one"), map.values());
        final Map<String, Integer> inverse = map.inverse();
        assertEquals(1, inverse.size());
        compare(Collections.singleton("one"), inverse.keySet());
        compare(Collections.singleton(1), inverse.values());
    }

    private static void compare(Collection<?> expected, Collection<?> actual) {
        assertNotNull(expected);
        assertEquals(expected.size(), actual.size());
        for(Object obj : expected) {
            assertTrue("Missing element " + obj, actual.contains(obj));
        }
    }
}
