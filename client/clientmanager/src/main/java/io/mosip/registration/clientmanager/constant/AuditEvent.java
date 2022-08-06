package io.mosip.registration.clientmanager.constant;


import static io.mosip.registration.clientmanager.constant.AuditEventType.SYSTEM_EVENT;
import static io.mosip.registration.clientmanager.constant.AuditEventType.USER_EVENT;

/**
 * Enum for Audit Events
 *
 * @author Anshul Vanawat
 * @since 1.0.0
 */

public enum AuditEvent {

    //Loaded
    LOADED_LOGIN("REG-LOAD-001", USER_EVENT.getCode(), "LOADED_LOGIN", "Login activity loaded"),
    LOADED_ABOUT("REG-LOAD-002", USER_EVENT.getCode(), "LOADED_ABOUT", "About activity loaded"),
    LOADED_HOME("REG-LOAD-003", USER_EVENT.getCode(), "LOADED_HOME", "Home activity loaded"),
    LOADED_REG_LISTING("REG-LOAD-004", USER_EVENT.getCode(), "LOADED_REG_LISTING", "Registration List activity loaded"),
    LOADED_JOB_SERVICE("REG-LOAD-005", USER_EVENT.getCode(), "LOADED_JOB_SERVICE", "Job service list activity loaded"),
    LOADED_DATA_ENTRY_LANG("REG-LOAD-006", USER_EVENT.getCode(), "LOADED_DATA_ENTRY_LANG", "DataEntry Language selection activity loaded"),

    //Login
    LOGIN_WITH_PASSWORD("REG-AUTH-001", USER_EVENT.getCode(), "LOGIN_WITH_PASSWORD",
            "Login with password: Click of Submit"),
    ABOUT_CLIENT("REG-AUTH-002", USER_EVENT.getCode(), "ABOUT_CLIENT", "Press and hold on logo to navigate: About activity"),
    LOGOUT_USER("REG-AUTH-003", USER_EVENT.getCode(), "LOGOUT_USER", "Logout"),

    //Home
    MASTER_DATA_SYNC("REG-HOME-001", USER_EVENT.getCode(), "MASTER_DATA_SYNC", "Master data sync clicked"),
    NEW_REGISTRATION("REG-HOME-002", USER_EVENT.getCode(), "NEW_REGISTRATION", "New registration clicked"),
    LIST_REGISTRATION("REG-HOME-003", USER_EVENT.getCode(), "LIST_REGISTRATION", "List registration clicked"),
    LIST_JOB_SERVICE("REG-HOME-004", USER_EVENT.getCode(), "LIST_JOB_SERVICE", "List job service clicked"),

    //REG_PACKET_LIST
    SYNC_PACKET("REG-PKT-001", USER_EVENT.getCode(), "SYNC_PACKET", "Packet sync clicked"),
    UPLOAD_PACKET("REG-PKT-002", USER_EVENT.getCode(), "UPLOAD_PACKET", "Upload packet clicked"),
    SYNC_AND_UPLOAD_PACKET("REG-PKT-003", USER_EVENT.getCode(), "SYNC_AND_UPLOAD_PACKET", "sync and upload started"),

    //JOB_SERVICE
    TRIGGER_JOB("REG-JOB-001", USER_EVENT.getCode(), "TRIGGER_JOB", "Trigger job service clicked"),
    SCHEDULE_JOB("REG-JOB-002", USER_EVENT.getCode(), "SCHEDULE_JOB", "Schedule job service clicked"),
    CANCEL_JOB("REG-JOB-003", USER_EVENT.getCode(), "CANCEL_JOB", "Cancel scheduled job service clicked"),

    //REGISTRATION USER EVENT
    REGISTRATION_START("REG-EVT-001", USER_EVENT.getCode(), "REGISTRATION_START", "Registration start event initiated"),
    LOADED_REGISTRATION_SCREEN("REG-EVT-002", USER_EVENT.getCode(), "LOADED_REGISTRATION_SCREEN", "Registration screen activity loaded"),
    NEXT_BUTTON_CLICKED("REG-EVT-003", USER_EVENT.getCode(), "NEXT_BUTTON_CLICKED", "Next button clicked"),
    DOCUMENT_SCAN("REG-EVT-004", USER_EVENT.getCode(), "DOCUMENT_SCAN", "Scan document button clicked"),
    DOCUMENT_SCAN_FAILED("REG-EVT-005", USER_EVENT.getCode(), "DOCUMENT_SCAN_FAILED", "Document scan failed"),
    DOCUMENT_PREVIEW("REG-EVT-006", USER_EVENT.getCode(), "DOCUMENT_PREVIEW", "Document preview button clicked"),
    BIOMETRIC_CAPTURE("REG-EVT-007", USER_EVENT.getCode(), "BIOMETRIC_CAPTURE_INITIATED", "Biometric capture initiated"),
    LOADED_REGISTRATION_PREVIEW("REG-EVT-008", USER_EVENT.getCode(), "LOADED_REGISTRATION_PREVIEW", "Registration preview loaded"),
    CREATE_PACKET_AUTH("REG-EVT-009", USER_EVENT.getCode(), "CREATE_PACKET_AUTH", "Packet create authentication clicked"),
    CREATE_PACKET_AUTH_FAILED("REG-EVT-010", USER_EVENT.getCode(), "CREATE_PACKET_AUTH_FAILED", "Packet create authentication failed"),
    LOADED_ACKNOWLEDGEMENT_SCREEN("REG-EVT-011", USER_EVENT.getCode(), "LOADED_ACKNOWLEDGEMENT_SCREEN", "Acknowledgement Activity loaded"),
    PRINT_ACKNOWLEDGEMENT("REG-EVT-0012", USER_EVENT.getCode(), "PRINT_ACKNOWLEDGEMENT", "Print acknowledgement"),

    //REGISTRATION SYSTEM EVENT
    DISCOVER_SBI_FAILED("REG-EVT-013", SYSTEM_EVENT.getCode(), "DISCOVER_SBI_FAILED", "SBI discovery failed"),
    DEVICE_INFO_FAILED("REG-EVT-014", SYSTEM_EVENT.getCode(), "DEVICE_INFO_FAILED", "Device info failed"),
    R_CAPTURE_FAILED("REG-EVT-015", SYSTEM_EVENT.getCode(), "R_CAPTURE_FAILED", "R_capture failed"),
    DISCOVER_SBI_PARSE_FAILED("REG-EVT-016", SYSTEM_EVENT.getCode(), "DISCOVER_SBI_PARSE_FAILED", "SBI discovery response parsing failed"),
    DEVICE_INFO_PARSE_FAILED("REG-EVT-017", SYSTEM_EVENT.getCode(), "DEVICE_INFO_PARSE_FAILED", "Device info response parsing failed"),
    R_CAPTURE_PARSE_FAILED("REG-EVT-018", SYSTEM_EVENT.getCode(), "R_CAPTURE_PARSE_FAILED", "R_capture response parsing failed"),
    CREATE_PACKET_FAILED("REG-EVT-019", USER_EVENT.getCode(), "CREATE_PACKET_FAILED", "Packet creation failed");

    AuditEvent(String id, String type, String name, String description) {
        this.id = id;
        this.type = type;
        this.name = name;
        this.description = description;
    }

    private final String id;
    private final String type;
    private final String name;
    private final String description;

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

}