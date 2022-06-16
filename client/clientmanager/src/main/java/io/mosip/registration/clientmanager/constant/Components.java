package io.mosip.registration.clientmanager.constant;

/**
 * Enum for Application Modules to be used in Audit
 *
 * @author Anshul Vanawat
 * @since 1.0.0
 */

public enum Components {

    LOGIN("REG-MOD-101", "Login"),
    HOME("REG-MOD-102", "Home"),
    REGISTRATION("REG-MOD-103", "Registration"),
    REG_PACKET_LIST("REG-MOD-104", "PacketSync"),
    JOB_SERVICE("REG-MOD-105", "DataSync");

    Components(String id, String name) {
        this.id = id;
        this.name = name;
    }

    private final String id;
    private final String name;

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }
}
