package io.mosip.registration.clientmanager.constant;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ClientManagerConstantTest {

    @Test
    public void testDefaultUploadCron() {
        assertEquals("0 0 12 * * ?", ClientManagerConstant.DEFAULT_UPLOAD_CRON);
    }

    @Test
    public void testDefaultBatchSize() {
        assertEquals(Integer.valueOf(4), ClientManagerConstant.DEFAULT_BATCH_SIZE);
    }
}