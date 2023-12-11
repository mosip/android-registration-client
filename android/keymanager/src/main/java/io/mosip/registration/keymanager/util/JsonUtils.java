package io.mosip.registration.keymanager.util;

import android.util.Log;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.afterburner.AfterburnerModule;

import java.util.Map;

public class JsonUtils {

    private static ObjectMapper objectMapper;
    private static final String TAG = JsonUtils.class.getSimpleName();

    static {
        objectMapper = JsonMapper.builder().addModule(new AfterburnerModule()).build();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
    }

    /**
     * This method converts a JSON String containing multiple JSON and stores them
     * in a java Map
     *
     * @param jsonString input String containing array of JSON string(always in
     *                   double quotes) (eg."[{color=Black, type=BMW}, {color=Red,
     *                   type=FIAT}]")
     * @return java map containing JSON inputs
     */
    public static Map<String, Object> jsonStringToJavaMap(String jsonString) {
        Map<String, Object> javaMap = null;
        try {
            javaMap = objectMapper.readValue(jsonString, new TypeReference<Map<String, Object>>() {
            });
        } catch (Exception e) {
            Log.e(TAG, "Exception in converting jsonString to map");
        }
        return javaMap;
    }
}
