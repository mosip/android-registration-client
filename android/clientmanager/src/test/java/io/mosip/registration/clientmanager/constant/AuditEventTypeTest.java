package io.mosip.registration.clientmanager.constant;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class AuditEventTypeTest {

    @Test
    public void testUserEvent() {
        AuditEventType eventType = AuditEventType.USER_EVENT;

        assertEquals("USER", eventType.getCode());
    }

    @Test
    public void testSystemEvent() {
        AuditEventType eventType = AuditEventType.SYSTEM_EVENT;

        assertEquals("SYSTEM", eventType.getCode());
    }

    @Test
    public void testEnumValuesCount() {
        // Verify the total number of enum constants
        assertEquals(2, AuditEventType.values().length);
    }

    @Test
    public void testValueOf_UserEvent() {
        // Verify that valueOf retrieves the correct enum constant for USER_EVENT
        AuditEventType eventType = AuditEventType.valueOf("USER_EVENT");
        assertEquals("USER", eventType.getCode());
    }

    @Test
    public void testValueOf_SystemEvent() {
        // Verify that valueOf retrieves the correct enum constant for SYSTEM_EVENT
        AuditEventType eventType = AuditEventType.valueOf("SYSTEM_EVENT");
        assertEquals("SYSTEM", eventType.getCode());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testValueOf_InvalidName() {
        // Verify that valueOf throws IllegalArgumentException for an invalid name
        AuditEventType.valueOf("INVALID_EVENT_TYPE");
    }
}