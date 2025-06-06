package io.mosip.registration.packetmanager.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import android.util.Log;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class JsonUtilsTest {

    static class Dummy {
        public String name;
        public int value;

        public Dummy() {}

        public Dummy(String name, int value) {
            this.name = name;
            this.value = value;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Dummy dummy = (Dummy) o;
            return value == dummy.value && (name == null ? dummy.name == null : name.equals(dummy.name));
        }
    }

    private Dummy dummy;

    @Before
    public void setUp() {
        dummy = new Dummy("test", 42);
    }

    @Test
    public void testJavaObjectToJsonString_Success() throws Exception {
        String json = JsonUtils.javaObjectToJsonString(dummy);
        assertTrue(json.contains("test"));
        assertTrue(json.contains("42"));
    }

    @Test
    public void testJavaObjectToJsonString_Exception() {
        try (MockedStatic<Log> logMock = Mockito.mockStatic(Log.class)) {
            // Use an object with a cyclic reference, which Jackson cannot serialize by default
            class SelfRef {
                public SelfRef ref;
            }
            SelfRef obj = new SelfRef();
            obj.ref = obj;
            try {
                JsonUtils.javaObjectToJsonString(obj);
                fail("Expected JsonProcessingException");
            } catch (JsonProcessingException e) {
                logMock.verify(() -> Log.e(Mockito.anyString(), Mockito.anyString(), Mockito.any(Throwable.class)));
            }
        }
    }

    @Test
    public void testJsonStringToJavaObject_Class_Success() throws Exception {
        String json = "{\"name\":\"test\",\"value\":42}";
        Dummy result = JsonUtils.jsonStringToJavaObject(json, Dummy.class);
        assertEquals(dummy, result);
    }

    @Test
    public void testJsonStringToJavaObject_Class_Exception() {
        try (MockedStatic<Log> logMock = Mockito.mockStatic(Log.class)) {
            String invalidJson = "{invalid json}";
            try {
                JsonUtils.jsonStringToJavaObject(invalidJson, Dummy.class);
                fail("Expected JsonProcessingException");
            } catch (JsonProcessingException e) {
                logMock.verify(() -> Log.e(Mockito.anyString(), Mockito.anyString(), Mockito.any(Throwable.class)));
            }
        }
    }

    @Test
    public void testJsonStringToJavaObject_TypeReference_Success() throws Exception {
        Map<String, Integer> map = new HashMap<>();
        map.put("a", 1);
        String json = JsonUtils.javaObjectToJsonString(map);
        TypeReference<Map<String, Integer>> typeRef = new TypeReference<Map<String, Integer>>() {};
        Map<String, Integer> result = JsonUtils.jsonStringToJavaObject(json, typeRef);
        assertEquals(map, result);
    }

    @Test
    public void testJsonStringToJavaObject_TypeReference_Exception() {
        try (MockedStatic<Log> logMock = Mockito.mockStatic(Log.class)) {
            String invalidJson = "{invalid json}";
            TypeReference<Map<String, Integer>> typeRef = new TypeReference<Map<String, Integer>>() {};
            try {
                JsonUtils.jsonStringToJavaObject(invalidJson, typeRef);
                fail("Expected JsonProcessingException");
            } catch (JsonProcessingException e) {
                logMock.verify(() -> Log.e(Mockito.anyString(), Mockito.anyString(), Mockito.any(Throwable.class)));
            }
        }
    }
}
