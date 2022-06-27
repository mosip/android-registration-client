package io.mosip.registration.clientmanager.util;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class LocalDateTimeDeserializer implements JsonDeserializer<LocalDateTime> {

    private static List<DateTimeFormatter> formatterList = new ArrayList<>();

    static {
        formatterList.add(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"));
        formatterList.add(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS"));
        formatterList.add(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSS"));
        formatterList.add(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSS"));
        formatterList.add(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS"));
    }

    @Override
    public LocalDateTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        LocalDateTime localDateTime = null;
        for(DateTimeFormatter formatter : formatterList) {
            try {
                localDateTime = LocalDateTime.parse(json.getAsString(), formatter.withLocale(Locale.ENGLISH));
            } catch (DateTimeParseException ex) {}
        }
        return localDateTime;
    }


}
