package io.mosip.registration.clientmanager.util;

import android.util.Log;

import com.cronutils.model.Cron;
import com.cronutils.model.CronType;
import com.cronutils.model.definition.CronDefinitionBuilder;
import com.cronutils.model.time.ExecutionTime;
import com.cronutils.parser.CronParser;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Optional;

/**
 * Simple utility for cron expression parsing and next sync time calculation
 */
public class CronExpressionParser {

    private static final CronParser cronParser = new CronParser(
            CronDefinitionBuilder.instanceDefinitionFor(CronType.QUARTZ)
    );

    /**
     * Calculates next execution time from cron expression
     * @param cronExpression e.g., "0 0 11 * * ?" (Every day at 11:00 AM)
     * @return Next execution time or null if invalid
     */
    public static Instant getNextExecutionTime(String cronExpression) {
        try {
            if (cronExpression == null || cronExpression.trim().isEmpty()) {
                return null;
            }

            Cron cron = cronParser.parse(cronExpression.trim());
            ExecutionTime executionTime = ExecutionTime.forCron(cron);
            ZonedDateTime now = ZonedDateTime.now(ZoneId.systemDefault());

            Optional<ZonedDateTime> next = executionTime.nextExecution(now);
            return next.map(ZonedDateTime::toInstant).orElse(null);
        } catch (Exception e) {
            Log.e("CronExpressionParser", "Error parsing cron expression", e);
        }
        return null;
    }

    /**
     * Validates cron expression
     * @param cronExpression The cron expression to validate
     * @return true if valid
     */
    public static boolean isValidCronExpression(String cronExpression) {
        try {
            if (cronExpression == null || cronExpression.trim().isEmpty()) {
                return false;
            }
            cronParser.parse(cronExpression.trim());
            return true;
        } catch (Exception e) {
            Log.e("CronExpressionParser", "Invalid cron expression", e);
        }
        return false;
    }
}