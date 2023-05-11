package io.mosip.registration.clientmanager.constant;

/**
 * Enum for References Id Types to be used in Audit
 *
 * @author Balaji Sridharan
 * @since 1.0.0
 */
public enum AuditReferenceIdTypes {
    USER_ID("USER_ID"),
    REGISTRATION_ID("REGISTRATION_ID"),
    PACKET_ID("PACKET_ID"),
    APPLICATION_ID("APPLICATION_ID");

    /**
     * The constructor
     */
    AuditReferenceIdTypes(String referenceTypeId) {
        this.referenceTypeId = referenceTypeId;
    }

    private final String referenceTypeId;

    /**
     * @return the referenceTypeId
     */
    public String getReferenceTypeId() {
        return referenceTypeId;
    }
}
