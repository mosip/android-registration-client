/*
 * Copyright (c) Modular Open Source Identity Platform
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *
*/

package io.mosip.registration_client;

import com.cronutils.model.Cron;
import com.cronutils.model.CronType;
import com.cronutils.model.definition.CronDefinitionBuilder;
import com.cronutils.model.time.ExecutionTime;
import com.cronutils.parser.CronParser;

import java.time.ZonedDateTime;
import java.util.Date;

public class CronParserUtil {

    public static long getNextExecutionTimeInMillis(String cronExpression) {
        CronParser parser = new CronParser(CronDefinitionBuilder.instanceDefinitionFor(CronType.QUARTZ));
        Cron cron = parser.parse(cronExpression);

        ZonedDateTime now = ZonedDateTime.now();
        ExecutionTime executionTime = ExecutionTime.forCron(cron);

        Date nextExecution = Date.from(executionTime.nextExecution(now).get().toInstant());

        return nextExecution.getTime();
    }
}