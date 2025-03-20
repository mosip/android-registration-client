package io.mosip.registration.clientmanager.constant;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class AuditReferenceIdTypesTest {

    @Test
    public void testUserId() {
        AuditReferenceIdTypes refType = AuditReferenceIdTypes.USER_ID;

        assertEquals("USER_ID", refType.getReferenceTypeId());
    }

    @Test
    public void testRegistrationId() {
        AuditReferenceIdTypes refType = AuditReferenceIdTypes.REGISTRATION_ID;

        assertEquals("REGISTRATION_ID", refType.getReferenceTypeId());
    }

    @Test
    public void testPacketId() {
        AuditReferenceIdTypes refType = AuditReferenceIdTypes.PACKET_ID;

        assertEquals("PACKET_ID", refType.getReferenceTypeId());
    }

    @Test
    public void testApplicationId() {
        AuditReferenceIdTypes refType = AuditReferenceIdTypes.APPLICATION_ID;

        assertEquals("APPLICATION_ID", refType.getReferenceTypeId());
    }

    @Test
    public void testEnumValuesCount() {
        // Verify the total number of enum constants
        assertEquals(4, AuditReferenceIdTypes.values().length);
    }

    @Test
    public void testValueOf_UserId() {
        // Verify that valueOf retrieves the correct enum constant for USER_ID
        AuditReferenceIdTypes refType = AuditReferenceIdTypes.valueOf("USER_ID");
        assertEquals("USER_ID", refType.getReferenceTypeId());
    }

    @Test
    public void testValueOf_RegistrationId() {
        // Verify that valueOf retrieves the correct enum constant for REGISTRATION_ID
        AuditReferenceIdTypes refType = AuditReferenceIdTypes.valueOf("REGISTRATION_ID");
        assertEquals("REGISTRATION_ID", refType.getReferenceTypeId());
    }

    @Test
    public void testValueOf_PacketId() {
        // Verify that valueOf retrieves the correct enum constant for PACKET_ID
        AuditReferenceIdTypes refType = AuditReferenceIdTypes.valueOf("PACKET_ID");
        assertEquals("PACKET_ID", refType.getReferenceTypeId());
    }

    @Test
    public void testValueOf_ApplicationId() {
        // Verify that valueOf retrieves the correct enum constant for APPLICATION_ID
        AuditReferenceIdTypes refType = AuditReferenceIdTypes.valueOf("APPLICATION_ID");
        assertEquals("APPLICATION_ID", refType.getReferenceTypeId());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testValueOf_InvalidName() {
        // Verify that valueOf throws IllegalArgumentException for an invalid name
        AuditReferenceIdTypes.valueOf("INVALID_REF_TYPE");
    }
}