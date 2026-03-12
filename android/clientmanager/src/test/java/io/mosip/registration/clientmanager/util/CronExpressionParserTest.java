package io.mosip.registration.clientmanager.util;

import org.junit.Test;

import java.time.Instant;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class CronExpressionParserTest {

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