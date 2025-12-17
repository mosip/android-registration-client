package io.mosip.registration.clientmanager.util;

import android.content.Context;
import android.text.format.DateFormat;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class DateUtilTest {

    @Mock
    Context mockContext;

    private MockedStatic<DateFormat> mockedDateFormat;

    @Before
    public void setUp() {
        mockedDateFormat = Mockito.mockStatic(android.text.format.DateFormat.class);
    }

    @After
    public void tearDown() {
        if (mockedDateFormat != null) {
            mockedDateFormat.close();
        }
    }

    @Test
    public void testConstructor_initializesFormats() {
        SimpleDateFormat mockDateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.US);
        SimpleDateFormat mockTimeFormat = new SimpleDateFormat("HH:mm", Locale.US);

        mockedDateFormat.when(() -> android.text.format.DateFormat.getMediumDateFormat(mockContext))
                .thenReturn(mockDateFormat);
        mockedDateFormat.when(() -> android.text.format.DateFormat.getTimeFormat(mockContext))
                .thenReturn(mockTimeFormat);

        DateUtil util = new DateUtil(mockContext);
        assertNotNull(util.dateFormat);
        assertNotNull(util.timeFormat);
    }

    @Test
    public void testGetDateTime_returnsFormattedString() {
        SimpleDateFormat mockDateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.US);
        SimpleDateFormat mockTimeFormat = new SimpleDateFormat("HH:mm", Locale.US);

        mockedDateFormat.when(() -> android.text.format.DateFormat.getMediumDateFormat(mockContext))
                .thenReturn(mockDateFormat);
        mockedDateFormat.when(() -> android.text.format.DateFormat.getTimeFormat(mockContext))
                .thenReturn(mockTimeFormat);

        DateUtil util = new DateUtil(mockContext);

        long millis = 1732530600000L; // Fixed date
        String result = util.getDateTime(millis);

        assertNotNull(result);
        // Verify result contains expected date components for fixed timestamp
        assertTrue("Result should contain a 4-digit year", result.matches(".*\\d{4}.*"));
        // Verify result contains time in HH:mm or H:mm format
        assertTrue("Result should contain time in HH:mm format", result.matches(".*\\d{1,2}:\\d{2}.*"));
    }

    @Test
    public void testGetDateTime_withEpoch() {
        SimpleDateFormat mockDateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.US);
        SimpleDateFormat mockTimeFormat = new SimpleDateFormat("HH:mm", Locale.US);

        mockedDateFormat.when(() -> android.text.format.DateFormat.getMediumDateFormat(mockContext))
                .thenReturn(mockDateFormat);
        mockedDateFormat.when(() -> android.text.format.DateFormat.getTimeFormat(mockContext))
                .thenReturn(mockTimeFormat);

        DateUtil util = new DateUtil(mockContext);

        String result = util.getDateTime(0L);
        assertNotNull(result);
        assertTrue(result.length() > 0);
    }
}
