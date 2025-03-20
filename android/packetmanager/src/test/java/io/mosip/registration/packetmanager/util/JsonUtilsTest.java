package io.mosip.registration.packetmanager.util;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mockStatic;

public class JsonUtilsTest {

    private MockedStatic<android.util.Log> mockedLog;

    @Before
    public void setUp() {
        mockedLog = mockStatic(android.util.Log.class);
        mockedLog.when(() -> android.util.Log.e(anyString(), anyString(), Mockito.any(Throwable.class)))
                .thenAnswer(invocation -> 0);
    }

    @After
    public void tearDown() {
        mockedLog.close();
    }

    @Test
    public void testJavaObjectToJsonString_Success() throws JsonProcessingException {
        TestObject obj = new TestObject("name", 30);
        String jsonString = JsonUtils.javaObjectToJsonString(obj);
        assertNotNull(jsonString);
        assertTrue(jsonString.contains("name"));
        assertTrue(jsonString.contains("30"));
    }

    @Test
    public void testJsonStringToJavaObject_Success() throws JsonProcessingException {
        String json = "{\"name\":\"Alice\",\"age\":25}";
        TestObject obj = JsonUtils.jsonStringToJavaObject(json, TestObject.class);
        assertNotNull(obj);
        assertEquals("Alice", obj.getName());
        assertEquals(25, obj.getAge());
    }

    @Test
    public void testJsonStringToJavaObject_InvalidJson() {
        String invalidJson = "{name: Alice, age: 25}"; // Invalid JSON format
        assertThrows(JsonParseException.class, () -> JsonUtils.jsonStringToJavaObject(invalidJson, TestObject.class));
    }

    @Test
    public void testJsonStringToJavaObject_TypeReference_Success() throws JsonProcessingException {
        String json = "{\"key1\":\"value1\",\"key2\":\"value2\"}";
        Map<String, String> result = JsonUtils.jsonStringToJavaObject(json, new TypeReference<Map<String, String>>() {});
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("value1", result.get("key1"));
    }

    @Test
    public void testJsonStringToJavaObject_TypeReference_InvalidJson() {
        String invalidJson = "{key1: value1, key2: value2}";
        assertThrows(JsonParseException.class, () ->
                JsonUtils.jsonStringToJavaObject(invalidJson, new TypeReference<Map<String, String>>() {}));
    }

    static class TestObject {
        private String name;
        private int age;

        public TestObject() {}

        public TestObject(String name, int age) {
            this.name = name;
            this.age = age;
        }

        public String getName() { return name; }
        public int getAge() { return age; }
    }
}
