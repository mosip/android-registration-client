package io.mosip.registration.clientmanager.constant;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ComponentsTest {

    @Test
    public void testLogin() {
        Components component = Components.LOGIN;

        assertEquals("REG-MOD-101", component.getId());
        assertEquals("Login", component.getName());
    }

    @Test
    public void testHome() {
        Components component = Components.HOME;

        assertEquals("REG-MOD-102", component.getId());
        assertEquals("Home", component.getName());
    }

    @Test
    public void testRegistration() {
        Components component = Components.REGISTRATION;

        assertEquals("REG-MOD-103", component.getId());
        assertEquals("Registration", component.getName());
    }

    @Test
    public void testRegPacketList() {
        Components component = Components.REG_PACKET_LIST;

        assertEquals("REG-MOD-104", component.getId());
        assertEquals("PacketSync", component.getName());
    }

    @Test
    public void testJobService() {
        Components component = Components.JOB_SERVICE;

        assertEquals("REG-MOD-105", component.getId());
        assertEquals("DataSync", component.getName());
    }

    @Test
    public void testEnumValuesCount() {
        // Verify the total number of enum constants
        assertEquals(5, Components.values().length);
    }

    @Test
    public void testValueOf_Login() {
        // Verify that valueOf retrieves the correct enum constant for LOGIN
        Components component = Components.valueOf("LOGIN");
        assertEquals("REG-MOD-101", component.getId());
        assertEquals("Login", component.getName());
    }

    @Test
    public void testValueOf_Home() {
        // Verify that valueOf retrieves the correct enum constant for HOME
        Components component = Components.valueOf("HOME");
        assertEquals("REG-MOD-102", component.getId());
        assertEquals("Home", component.getName());
    }

    @Test
    public void testValueOf_Registration() {
        // Verify that valueOf retrieves the correct enum constant for REGISTRATION
        Components component = Components.valueOf("REGISTRATION");
        assertEquals("REG-MOD-103", component.getId());
        assertEquals("Registration", component.getName());
    }

    @Test
    public void testValueOf_RegPacketList() {
        // Verify that valueOf retrieves the correct enum constant for REG_PACKET_LIST
        Components component = Components.valueOf("REG_PACKET_LIST");
        assertEquals("REG-MOD-104", component.getId());
        assertEquals("PacketSync", component.getName());
    }

    @Test
    public void testValueOf_JobService() {
        // Verify that valueOf retrieves the correct enum constant for JOB_SERVICE
        Components component = Components.valueOf("JOB_SERVICE");
        assertEquals("REG-MOD-105", component.getId());
        assertEquals("DataSync", component.getName());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testValueOf_InvalidName() {
        // Verify that valueOf throws IllegalArgumentException for an invalid name
        Components.valueOf("INVALID_COMPONENT");
    }
}