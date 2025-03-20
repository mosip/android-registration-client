package io.mosip.registration.keymanager.util;

import static org.junit.Assert.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Map;

@RunWith(MockitoJUnitRunner.class)
public class JsonUtilsTest {

    @Test
    public void testJsonStringToJavaMap_ValidJson() throws JsonProcessingException {
        String jsonString = "{\"name\":\"Alice\",\"age\":25}";
        Map<String, Object> result = JsonUtils.jsonStringToJavaMap(jsonString);

        assertNotNull(result);
        assertEquals("Alice", result.get("name"));
        assertEquals(25, result.get("age"));  // Ensure age is correctly parsed as an integer
    }

    @Test
    public void testJsonStringToJavaMap_InvalidJson() {
        String invalidJson = "{\"color\":\"Black\",\"type\":\"BMW\""; // Missing closing brace
        Map<String, Object> result = JsonUtils.jsonStringToJavaMap(invalidJson);

        assertNull(result); // Should return null on parsing failure
    }

    @Test
    public void testJsonStringToJavaMap_EmptyJson() {
        String emptyJson = "{}";
        Map<String, Object> result = JsonUtils.jsonStringToJavaMap(emptyJson);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}
