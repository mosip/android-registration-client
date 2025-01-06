package io.mosip.registration.clientmanager.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Before;
import org.junit.Test;

public class LocalDateTimeDeserializerTest {

    private static Gson gson;

    @Before
    public void setUp() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeDeserializer());
        gson = gsonBuilder.create();
    }

    @Test
    public void deserializeValidDateFormats_Test() {
        String date1 = "\"2024-11-25T10:15:30.123Z\"";
        String date2 = "\"2024-11-25T10:15:30.123\"";
        String date3 = "\"2024-11-25T10:15:30.1234\"";
        String date4 = "\"2024-11-25T10:15:30.12345\"";
        String date5 = "\"2024-11-25T10:15:30.123456\"";

        LocalDateTime localDateTime1 = gson.fromJson(date1, LocalDateTime.class);
        LocalDateTime localDateTime2 = gson.fromJson(date2, LocalDateTime.class);
        LocalDateTime localDateTime3 = gson.fromJson(date3, LocalDateTime.class);
        LocalDateTime localDateTime4 = gson.fromJson(date4, LocalDateTime.class);
        LocalDateTime localDateTime5 = gson.fromJson(date5, LocalDateTime.class);

        assertNotNull(localDateTime1);
        assertNotNull(localDateTime2);
        assertNotNull(localDateTime3);
        assertNotNull(localDateTime4);
        assertNotNull(localDateTime5);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSS");

        assertEquals("2024-11-25T10:15:30.1230", localDateTime1.format(formatter));
        assertEquals("2024-11-25T10:15:30.1230", localDateTime2.format(formatter));
        assertEquals("2024-11-25T10:15:30.1234", localDateTime3.format(formatter));
        assertEquals("2024-11-25T10:15:30.1234", localDateTime4.format(formatter));
        assertEquals("2024-11-25T10:15:30.1234", localDateTime5.format(formatter));
    }


    @Test
    public void deserializeNullValue_Test() {
        String nullDate = "null";

        LocalDateTime result = gson.fromJson(nullDate, LocalDateTime.class);

        assertNull(result);
    }
}
