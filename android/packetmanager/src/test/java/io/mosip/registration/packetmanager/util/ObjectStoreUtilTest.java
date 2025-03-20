package io.mosip.registration.packetmanager.util;

import org.junit.Test;
import static org.junit.Assert.*;

public class ObjectStoreUtilTest {

    @Test
    public void testGetName_WithSourceProcessObject() {
        String result = ObjectStoreUtil.getName("source", "process", "object");
        assertEquals("source/process/object", result);
    }

    @Test
    public void testGetName_WithSourceOnly() {
        String result = ObjectStoreUtil.getName("source", "", "object");
        assertEquals("source/object", result);
    }

    @Test
    public void testGetName_WithProcessOnly() {
        String result = ObjectStoreUtil.getName("", "process", "object");
        assertEquals("process/object", result);
    }

    @Test
    public void testGetName_WithObjectOnly() {
        String result = ObjectStoreUtil.getName("", "", "object");
        assertEquals("object", result);
    }

    @Test
    public void testGetName_WithContainerSourceProcessObject() {
        String result = ObjectStoreUtil.getName("container", "source", "process", "object");
        assertEquals("container/source/process/object", result);
    }

    @Test
    public void testGetName_WithContainerOnly() {
        String result = ObjectStoreUtil.getName("container", "", "", "object");
        assertEquals("container/object", result);
    }

    @Test
    public void testGetName_WithContainerAndSource() {
        String result = ObjectStoreUtil.getName("container", "source", "", "object");
        assertEquals("container/source/object", result);
    }

    @Test
    public void testGetName_WithObjectNameAndTag() {
        String result = ObjectStoreUtil.getName("object", "tag");
        assertEquals("object/tag", result);
    }

    @Test
    public void testGetName_WithEmptyObjectName() {
        String result = ObjectStoreUtil.getName("", "tag");
        assertEquals("tag", result);
    }

    @Test
    public void testGetName_WithEmptyTag() {
        String result = ObjectStoreUtil.getName("object", "");
        assertEquals("object/", result);
    }

    @Test
    public void testGetName_WithEmptyObjectAndTag() {
        String result = ObjectStoreUtil.getName("", "");
        assertEquals("", result);
    }

    @Test
    public void testGetName_WithNullValues() {
        String result = ObjectStoreUtil.getName(null, null, "object");
        assertEquals("object", result);
    }

    @Test
    public void testGetName_WithContainerAndNullValues() {
        String result = ObjectStoreUtil.getName("container", null, null, "object");
        assertEquals("container/object", result);
    }

    @Test
    public void testGetName_WithObjectNameAndNullTag() {
        String result = ObjectStoreUtil.getName("object", null);
        assertEquals("object/", result);
    }

    @Test
    public void testGetName_WithNullObjectNameAndTag() {
        String result = ObjectStoreUtil.getName(null, "tag");
        assertEquals("tag", result);
    }

    @Test
    public void testGetName_WithAllNulls() {
        String result = ObjectStoreUtil.getName(null, null);
        assertEquals("", result);
    }

    @Test
    public void testStringIsEmpty_WithNull() throws Exception {
        // Using reflection to test private method
        java.lang.reflect.Method method = ObjectStoreUtil.class.getDeclaredMethod("stringIsEmpty", String.class);
        method.setAccessible(true);

        boolean result = (boolean) method.invoke(null, (Object) null);
        assertTrue(result);
    }

    @Test
    public void testStringIsEmpty_WithEmptyString() throws Exception {
        java.lang.reflect.Method method = ObjectStoreUtil.class.getDeclaredMethod("stringIsEmpty", String.class);
        method.setAccessible(true);

        boolean result = (boolean) method.invoke(null, "");
        assertTrue(result);
    }

    @Test
    public void testStringIsEmpty_WithWhitespaceString() throws Exception {
        java.lang.reflect.Method method = ObjectStoreUtil.class.getDeclaredMethod("stringIsEmpty", String.class);
        method.setAccessible(true);

        boolean result = (boolean) method.invoke(null, "   ");
        assertTrue(result);
    }

    @Test
    public void testStringIsEmpty_WithValidString() throws Exception {
        java.lang.reflect.Method method = ObjectStoreUtil.class.getDeclaredMethod("stringIsEmpty", String.class);
        method.setAccessible(true);

        boolean result = (boolean) method.invoke(null, "valid");
        assertFalse(result);
    }
}
