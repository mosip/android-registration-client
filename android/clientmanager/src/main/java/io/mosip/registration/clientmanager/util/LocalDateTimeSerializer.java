package io.mosip.registration.clientmanager.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LocalDateTimeSerializer implements JsonSerializer<LocalDateTime> {

    private static final String UTC_DATETIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(UTC_DATETIME_PATTERN);

    @Override
    public JsonElement serialize(LocalDateTime localDateTime, Type srcType, JsonSerializationContext context) {
        return new JsonPrimitive(formatter.format(localDateTime));
    }
}
