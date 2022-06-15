package io.mosip.registration.clientmanager.constant;

/**
 * Enum for Audit Events
 *
 * @author Balaji Sridharan
 * @since 1.0.0
 */

public enum AuditEventType {
    USER_EVENT("USER"),
    SYSTEM_EVENT("SYSTEM");

    /**
     * @param code
     */
    AuditEventType(String code) {
        this.code = code;
    }

    private final String code;

    /**
     * @return the code
     */
    public String getCode() {
        return code;
    }
}
