package io.mosip.registration.keymanager.util;

import android.util.Log;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Map;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class JsonUtilsTest {

    @Test
    public void testJsonStringToJavaMap_validJson() {
        String json = "{\"color\":\"Black\",\"type\":\"BMW\"}";
        Map<String, Object> result = JsonUtils.jsonStringToJavaMap(json);
        assertNotNull(result);
        assertEquals("Black", result.get("color"));
        assertEquals("BMW", result.get("type"));
    }

    @Test
    public void testJsonStringToJavaMap_invalidJson_logsError() {
        String invalidJson = "{color=Black, type=BMW}"; // invalid JSON
        try (MockedStatic<Log> logMock = Mockito.mockStatic(Log.class)) {
            Map<String, Object> result = JsonUtils.jsonStringToJavaMap(invalidJson);
            assertNull(result);
            logMock.verify(() -> Log.e(Mockito.eq("JsonUtils"), Mockito.anyString()));
        }
    }

    @Test
    public void testJsonStringToJavaMap_emptyJson() {
        String emptyJson = "";
        try (MockedStatic<Log> logMock = Mockito.mockStatic(Log.class)) {
            Map<String, Object> result = JsonUtils.jsonStringToJavaMap(emptyJson);
            assertNull(result);
            logMock.verify(() -> Log.e(Mockito.eq("JsonUtils"), Mockito.anyString()));
        }
    }
}
