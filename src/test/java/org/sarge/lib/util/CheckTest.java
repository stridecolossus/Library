package org.sarge.lib.util;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.util.Collections;
import java.util.List;

import org.junit.Test;
import org.sarge.lib.util.Check;

public class CheckTest {
    @Test(expected = IllegalArgumentException.class)
    public void isNullObject() {
        Check.notNull(null);
    }

    @Test
    public void notNullObject() {
        final Object obj = new Object();
        assertEquals(obj, Check.notNull(obj));
    }

    @Test(expected = IllegalArgumentException.class)
    public void isEmptyString() {
        Check.notEmpty(new String(""));
    }

    @Test
    public void notEmptyString() {
        final String str = "string";
        assertEquals(str, Check.notEmpty(str));
    }

    @Test(expected = IllegalArgumentException.class)
    public void isEmptyCollection() {
        Check.notEmpty(Collections.emptyList());
    }

    @Test
    public void notEmptyCollection() {
        final List<Object> list = Collections.singletonList(new Object());
        assertEquals(list, Check.notEmpty(list));
    }

    @Test(expected = IllegalArgumentException.class)
    public void isEmptyArray() {
        Check.notEmpty(new Object[]{});
    }

    @Test
    public void notEmptyArray() {
        final String[] array = { "string" };
        assertArrayEquals(array, Check.notEmpty(array));
    }

    @Test
    public void zeroOrMore() {
        Check.zeroOrMore(0);
        Check.zeroOrMore(1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void zeroOrMoreInvalid() {
        Check.zeroOrMore(-1);
    }

    @Test
    public void oneOrMore() {
        Check.oneOrMore(1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void oneOrMoreInvalid() {
        Check.oneOrMore(0);
    }

    @Test
    public void range() {
        Check.range(2, 1, 3);
    }

    @Test(expected = IllegalArgumentException.class)
    public void rangeInvalid() {
        Check.range(42, 1, 3);
    }
}
