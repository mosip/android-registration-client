package io.mosip.registration.packetmanager.util;

import android.util.Log;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;

public class JsonUtils {

    private static final String TAG = JsonUtils.class.getSimpleName();
    private static ObjectMapper objectMapper;

    static {
        //TODO - uncomment below
        //objectMapper = JsonMapper.builder().addModule(new AfterburnerModule()).build();
        //objectMapper.registerModule(new JavaTimeModule());
        objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
    }

    public static String javaObjectToJsonString(Object object) throws JsonProcessingException {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException ex) {
            Log.e(TAG, "Failed to serialize object", ex);
            throw ex;
        }
    }

    public static <T> T jsonStringToJavaObject(String jsonString, Class<T> clazz) throws JsonProcessingException {
        try {
            return objectMapper.readValue(jsonString, clazz);
        } catch (JsonProcessingException ex) {
            Log.e(TAG, "Failed to deserialize object", ex);
            throw ex;
        }
    }

    public static <T> T jsonStringToJavaObject(String jsonString, TypeReference<T> typeReference) throws JsonProcessingException {
        try {
            return objectMapper.readValue(jsonString, typeReference);
        } catch (JsonProcessingException ex) {
            Log.e(TAG, "Failed to deserialize object", ex);
            throw ex;
        }
    }
}
