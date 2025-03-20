package io.mosip.registration.clientmanager.constant;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class AuditEventTest {

    @Test
    public void testLoadedLogin() {
        AuditEvent event = AuditEvent.LOADED_LOGIN;

        assertEquals("REG-LOAD-001", event.getId());
        assertEquals(AuditEventType.USER_EVENT.getCode(), event.getType());
        assertEquals("LOADED_LOGIN", event.getName());
        assertEquals("Login activity loaded", event.getDescription());
    }

    @Test
    public void testLoginWithPassword() {
        AuditEvent event = AuditEvent.LOGIN_WITH_PASSWORD;

        assertEquals("REG-AUTH-001", event.getId());
        assertEquals(AuditEventType.USER_EVENT.getCode(), event.getType());
        assertEquals("LOGIN_WITH_PASSWORD", event.getName());
        assertEquals("Login with password: Click of Submit", event.getDescription());
    }

    @Test
    public void testMasterDataSync() {
        AuditEvent event = AuditEvent.MASTER_DATA_SYNC;

        assertEquals("REG-HOME-001", event.getId());
        assertEquals(AuditEventType.USER_EVENT.getCode(), event.getType());
        assertEquals("MASTER_DATA_SYNC", event.getName());
        assertEquals("Master data sync clicked", event.getDescription());
    }

    @Test
    public void testSyncPacket() {
        AuditEvent event = AuditEvent.SYNC_PACKET;

        assertEquals("REG-PKT-001", event.getId());
        assertEquals(AuditEventType.USER_EVENT.getCode(), event.getType());
        assertEquals("SYNC_PACKET", event.getName());
        assertEquals("Packet sync clicked", event.getDescription());
    }

    @Test
    public void testTriggerJob() {
        AuditEvent event = AuditEvent.TRIGGER_JOB;

        assertEquals("REG-JOB-001", event.getId());
        assertEquals(AuditEventType.USER_EVENT.getCode(), event.getType());
        assertEquals("TRIGGER_JOB", event.getName());
        assertEquals("Trigger job service clicked", event.getDescription());
    }

    @Test
    public void testRegistrationStart() {
        AuditEvent event = AuditEvent.REGISTRATION_START;

        assertEquals("REG-EVT-001", event.getId());
        assertEquals(AuditEventType.USER_EVENT.getCode(), event.getType());
        assertEquals("REGISTRATION_START", event.getName());
        assertEquals("Registration start event initiated", event.getDescription());
    }

    @Test
    public void testDiscoverSbiFailed_SystemEvent() {
        AuditEvent event = AuditEvent.DISCOVER_SBI_FAILED;

        assertEquals("REG-EVT-013", event.getId());
        assertEquals(AuditEventType.SYSTEM_EVENT.getCode(), event.getType());
        assertEquals("DISCOVER_SBI_FAILED", event.getName());
        assertEquals("SBI discovery failed", event.getDescription());
    }

    @Test
    public void testCreatePacketFailed() {
        AuditEvent event = AuditEvent.CREATE_PACKET_FAILED;

        assertEquals("REG-EVT-019", event.getId());
        assertEquals(AuditEventType.USER_EVENT.getCode(), event.getType());
        assertEquals("CREATE_PACKET_FAILED", event.getName());
        assertEquals("Packet creation failed", event.getDescription());
    }

    @Test
    public void testEnumValuesCount() {
        // Verify the total number of enum constants
        assertEquals(38, AuditEvent.values().length);
    }

    @Test
    public void testValueOf() {
        // Verify that valueOf retrieves the correct enum constant
        AuditEvent event = AuditEvent.valueOf("LOADED_HOME");
        assertEquals("REG-LOAD-003", event.getId());
        assertEquals(AuditEventType.USER_EVENT.getCode(), event.getType());
        assertEquals("LOADED_HOME", event.getName());
        assertEquals("Home activity loaded", event.getDescription());
    }
}