package io.mosip.registration.clientmanager.util;

import android.content.Context;


import org.junit.Before;
import org.junit.Test;

import org.robolectric.RobolectricTestRunner;
import org.junit.runner.RunWith;

import org.robolectric.annotation.Config;
import org.robolectric.RuntimeEnvironment;

import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 28)
public class DateUtilTest {

    private DateUtil dateUtil;

    @Before
    public void setUp() {

        Context context = RuntimeEnvironment.application.getApplicationContext();
        dateUtil = new DateUtil(context);
    }

    @Test
    public void testGetDateTime_shouldReturnFormattedDateTime() {
        long millis = 1732530600000L;
        String result = dateUtil.getDateTime(millis);

        assertNotNull(result);
        assertTrue(result.matches(".*\\d{1,2}, \\d{4}.*"));
        assertTrue(result.matches(".*\\d{1,2}:\\d{2}.*"));
    }
}
