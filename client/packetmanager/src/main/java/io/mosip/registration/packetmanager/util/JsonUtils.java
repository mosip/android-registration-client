package io.mosip.registration.packetmanager.util;

import android.util.Log;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class JsonUtils {
    private JsonUtils() {
    }

    public static String javaObjectToJsonString(Object className) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        String outputJson = null;

        try {
            outputJson = objectMapper.writeValueAsString(className);
            return outputJson;
        } catch (com.fasterxml.jackson.core.JsonProcessingException var4) {
            Log.i("KER-UTL-105", "json not processed successfully : " + var4.getCause());
            throw var4;
        }
    }
}
