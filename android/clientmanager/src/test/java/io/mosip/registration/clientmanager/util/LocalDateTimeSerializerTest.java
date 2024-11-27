package io.mosip.registration.clientmanager.util;

import static org.junit.Assert.assertEquals;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

import java.time.LocalDateTime;
import java.time.Month;

import org.junit.Test;

public class LocalDateTimeSerializerTest {

    private final LocalDateTimeSerializer serializer = new LocalDateTimeSerializer();

    @Test
    public void serializeBasicLocalDateTime_Test() {

        LocalDateTime dateTime = LocalDateTime.of(2024, Month.NOVEMBER, 25, 14, 30, 45, 123000000);

        String expectedJson = "2024-11-25T14:30:45.123Z";

        JsonElement jsonElement = serializer.serialize(dateTime, LocalDateTime.class, null);

        assertEquals(new JsonPrimitive(expectedJson), jsonElement);
    }

    @Test
    public void serializeLocalDateTimeWithSeconds_Test() {

        LocalDateTime dateTime = LocalDateTime.of(2024, Month.NOVEMBER, 25, 14, 30, 45, 0);

        String expectedJson = "2024-11-25T14:30:45.000Z";

        JsonElement jsonElement = serializer.serialize(dateTime, LocalDateTime.class, null);

        assertEquals(new JsonPrimitive(expectedJson), jsonElement);
    }

    @Test
    public void serializeLocalDateTimeWithLeapYear_Test() {

        LocalDateTime dateTime = LocalDateTime.of(2024, Month.FEBRUARY, 29, 10, 0, 0, 0);

        String expectedJson = "2024-02-29T10:00:00.000Z";

        JsonElement jsonElement = serializer.serialize(dateTime, LocalDateTime.class, null);

        assertEquals(new JsonPrimitive(expectedJson), jsonElement);
    }

    @Test
    public void serializeEdgeCaseLocalDateTime_Test() {

        LocalDateTime dateTime = LocalDateTime.of(2024, Month.JANUARY, 1, 0, 0, 0, 0);

        String expectedJson = "2024-01-01T00:00:00.000Z";

        JsonElement jsonElement = serializer.serialize(dateTime, LocalDateTime.class, null);

        assertEquals(new JsonPrimitive(expectedJson), jsonElement);
    }

}
