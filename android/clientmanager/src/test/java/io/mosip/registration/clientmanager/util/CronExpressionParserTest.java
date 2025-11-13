package io.mosip.registration.clientmanager.util;

import android.util.Log;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.time.Instant;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class CronExpressionParserTest {

    private MockedStatic<Log> logMock;

    @Before
    public void setUp() {
        logMock = Mockito.mockStatic(Log.class);
        logMock.when(() -> Log.e(Mockito.anyString(), Mockito.anyString())).thenReturn(0);
        logMock.when(() -> Log.e(Mockito.anyString(), Mockito.anyString(), Mockito.any(Throwable.class))).thenReturn(0);
    }

    @After
    public void tearDown() {
        if (logMock != null) {
            logMock.close();
        }
    }

    @Test
    public void testGetNextExecutionTime_withValidExpression_returnsFutureInstant() {
        Instant before = Instant.now();
        Instant nextExecution = CronExpressionParser.getNextExecutionTime("0 0 12 * * ?");

        assertNotNull("Expected next execution time for valid cron", nextExecution);
        assertTrue("Next execution should be in the future", nextExecution.isAfter(before));
    }

    @Test
    public void testGetNextExecutionTime_withNullOrBlankExpression_returnsNull() {
        assertNull(CronExpressionParser.getNextExecutionTime(null));
        assertNull(CronExpressionParser.getNextExecutionTime("   "));
    }

    @Test
    public void testGetNextExecutionTime_withInvalidExpression_returnsNull() {
        assertNull(CronExpressionParser.getNextExecutionTime("invalid cron expression"));
    }

    @Test
    public void testIsValidCronExpression_withValidExpression_returnsTrue() {
        assertTrue(CronExpressionParser.isValidCronExpression(" 0 15 10 * * ? "));
    }

    @Test
    public void testIsValidCronExpression_withNullOrBlankExpression_returnsFalse() {
        assertFalse(CronExpressionParser.isValidCronExpression(null));
        assertFalse(CronExpressionParser.isValidCronExpression("   "));
    }

    @Test
    public void testIsValidCronExpression_withInvalidExpression_returnsFalse() {
        assertFalse(CronExpressionParser.isValidCronExpression("* *"));
    }
}