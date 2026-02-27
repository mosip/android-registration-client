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
    LOGIN_AUTHENTICATE_USER_ID("REG-AUTH-001", USER_EVENT.getCode(), "LOGIN_AUTHENTICATE_USER_ID",
            "Login authenticating user id: Click of Submit"),
    LOGIN_WITH_PASSWORD("REG-AUTH-002", USER_EVENT.getCode(), "LOGIN_WITH_PASSWORD",
            "Login with password: Click of Submit"),
    ABOUT_CLIENT("REG-AUTH-003", USER_EVENT.getCode(), "ABOUT_CLIENT", "Press and hold on logo to navigate: About activity"),
    LOGOUT_USER("REG-AUTH-009", USER_EVENT.getCode(), "LOGOUT_USER", "Logout"),
    USER_STATUS_FETCH("REG-AUTH-011", USER_EVENT.getCode(), "USER_STATUS_FETCH", "Fetching User Status"),
    VALIDATE_USER_ID("REG-AUTH-012", USER_EVENT.getCode(), "VALIDATE_USER_ID", "Validating User identifier"),
    FETCH_USR_DET("REG-AUTH-013", USER_EVENT.getCode(), "FETCH_USER_DETAILS", "Fetching User Details"),
    FETCH_CNTR_NAME("REG-AUTH-014", USER_EVENT.getCode(), "FETCH_CENTER_NAME", "Fetching Center Name"),
    FETCH_CNTR_DET("REG-AUTH-015", USER_EVENT.getCode(), "FETCH_CENTER_DETAILS", "Fetching Center Details"),
    FETCH_USR_ROLE("REG-AUTH-016", USER_EVENT.getCode(), "FETCH_USER_ROLE", "Fetching User Roles"),

    // Navigation
    NAV_LOST_UIN("REG-EVT-001", USER_EVENT.getCode(), "NAV_LOST_UIN", "Click of navigation link: Lost UIN"),
    NAV_NEW_REG("REG-EVT-002", USER_EVENT.getCode(), "NAV_NEW_REG", "Click of navigation link: New Registration"),
    NAV_UIN_UPDATE("REG-EVT-003", USER_EVENT.getCode(), "NAV_UIN_UPDATE", "Navigation link: UIN Update"),
    NAV_APPROVE_REG("REG-EVT-004", USER_EVENT.getCode(), "NAV_APPROVE_REG", "Navigation link: Approve Registration"),
    NAV_UPLOAD_PACKETS("REG-EVT-005", USER_EVENT.getCode(), "NAV_UPLOAD_PACKETS", "Navigation link: Upload Packets"),
    NAV_CORRECTION("REG-EVT-007", USER_EVENT.getCode(), "NAV_CORRECTION", "Navigation link: CORRECTION"),
    NAV_SYNC_PACKETS("REG-NAV-001", USER_EVENT.getCode(), "NAV_SYNC_PACKETS", "Navigation link: Sync Packet IDs"),
    NAV_SYNC_DATA("REG-NAV-003", USER_EVENT.getCode(), "NAV_SYNC_DATA", "Navigation link: Sync Data"),
    NAV_DOWNLOAD_PRE_REG_DATA("REG-NAV-004", USER_EVENT.getCode(), "NAV_DOWNLOAD_PRE_REG_DATA",
            "Navigation link: Download Pre-registration Data"),
    NAV_GEO_LOCATION("REG-NAV-005", USER_EVENT.getCode(), "NAV_GEO_LOCATION", "Navigation link: Geo-location"),
    NAV_ON_BOARD_USER("REG-NAV-006", USER_EVENT.getCode(), "NAV_ON_BOARD_USER", "Navigation link: On-board Users"),
    NAV_HOME("REG-NAV-007", SYSTEM_EVENT.getCode(), "NAV_HOME", "Navigation link: Home"),
    NAV_REDIRECT_HOME("REG-NAV-008", SYSTEM_EVENT.getCode(), "NAV_REDIRECT_HOME", "Navigation link: Redirect to Home"),
    NAV_ON_BOARD_DEVICES("REG-NAV-009", USER_EVENT.getCode(), "NAV_ON_BOARD_DEVICES",
            "Navigation link: On-board Devices"),
    NAV_DASHBOARD("REG-NAV-010", SYSTEM_EVENT.getCode(), "NAV_DASHBOARD","Navigation link: Dashboard"),
    NAV_OPERATIONAL_TASKS("REG-NAV-012", SYSTEM_EVENT.getCode(), "NAV_OPERATIONAL_TASKS", "Navigation link: Operational Tasks"),

    //Home
    MASTER_DATA_SYNC("REG-HOME-001", USER_EVENT.getCode(), "MASTER_DATA_SYNC", "Master data sync clicked"),
    NEW_REGISTRATION("REG-HOME-002", USER_EVENT.getCode(), "NEW_REGISTRATION", "New registration clicked"),
    LIST_REGISTRATION("REG-HOME-003", USER_EVENT.getCode(), "LIST_REGISTRATION", "List registration clicked"),
    LIST_JOB_SERVICE("REG-HOME-004", USER_EVENT.getCode(), "LIST_JOB_SERVICE", "List job service clicked"),

    //REG_PACKET_LIST
    SYNC_PACKET("REG-PKT-001", USER_EVENT.getCode(), "SYNC_PACKET", "Packet sync clicked"),
    PACKET_UPDATE("REG-PKT-002", USER_EVENT.getCode(), "PACKET_UPDATE", "Packets which are in created state are updated"),
    UPLOAD_PACKET("REG-UPL-PKT-001", USER_EVENT.getCode(), "UPLOAD_PACKET", "Upload packet clicked"),
    SYNC_AND_UPLOAD_PACKET("REG-PKT-003", USER_EVENT.getCode(), "SYNC_AND_UPLOAD_PACKET", "sync and upload started"),
    PACKET_RETRIEVE("REG-PKT-004", USER_EVENT.getCode(), "PACKET_RETRIEVE", "Packets which are in created state for approval are retrieved"),

    //JOB_SERVICE
    TRIGGER_JOB("REG-JOB-001", USER_EVENT.getCode(), "TRIGGER_JOB", "Trigger job service clicked"),
    SCHEDULE_JOB("REG-JOB-002", USER_EVENT.getCode(), "SCHEDULE_JOB", "Schedule job service clicked"),
    CANCEL_JOB("REG-JOB-003", USER_EVENT.getCode(), "CANCEL_JOB", "Cancel scheduled job service clicked"),

    //REGISTRATION USER EVENT
    REGISTRATION_START("REG-EVT-104", USER_EVENT.getCode(), "REGISTRATION_START", "Registration start event initiated"),
    LOADED_REGISTRATION_SCREEN("REG-EVT-111", USER_EVENT.getCode(), "LOADED_REGISTRATION_SCREEN", "Registration screen activity loaded"),
    NEXT_BUTTON_CLICKED("REG-EVT-069", USER_EVENT.getCode(), "NEXT_BUTTON_CLICKED", "Next button clicked to %s"),
    DOCUMENT_SCAN("REG-EVT-106", USER_EVENT.getCode(), "DOCUMENT_SCAN", "Scan document button clicked"),
    DOCUMENT_SCAN_FAILED("REG-EVT-050", USER_EVENT.getCode(), "DOCUMENT_SCAN_FAILED", "Document scan failed"),
    DOCUMENT_PREVIEW("REG-EVT-006", USER_EVENT.getCode(), "DOCUMENT_PREVIEW", "Document preview button clicked"),
    BIOMETRIC_CAPTURE("REG-EVT-107", USER_EVENT.getCode(), "BIOMETRIC_CAPTURE_INITIATED", "Biometric capture initiated"),
    LOADED_REGISTRATION_PREVIEW("REG-EVT-108", USER_EVENT.getCode(), "LOADED_REGISTRATION_PREVIEW", "Registration preview loaded"),
    CREATE_PACKET_AUTH("REG-EVT-109", USER_EVENT.getCode(), "CREATE_PACKET_AUTH", "Packet create authentication clicked"),
    CREATE_PACKET_AUTH_FAILED("REG-EVT-043", USER_EVENT.getCode(), "CREATE_PACKET_AUTH_FAILED", "Packet create authentication failed"),
    LOADED_ACKNOWLEDGEMENT_SCREEN("REG-EVT-044", USER_EVENT.getCode(), "LOADED_ACKNOWLEDGEMENT_SCREEN", "Acknowledgement Activity loaded"),
    PRINT_ACKNOWLEDGEMENT("REG-EVT-045", USER_EVENT.getCode(), "PRINT_ACKNOWLEDGEMENT", "Print acknowledgement"),

    // Registration : Demographics Details
    REG_DEMO_CAPTURE("REG-EVT-110", USER_EVENT.getCode(), "REG_DEMO_NEXT", "FvStarted capturing demographic details"),
    REG_DEMO_PRE_REG_DATA_FETCH("REG-EVT-008", USER_EVENT.getCode(), "REG_DEMO_PRE_REG_DATA_FETCH", "Pre-registration: Fetch data for selected Pre-registration"),
    REG_DEMO_NEXT("REG-EVT-105", USER_EVENT.getCode(), "REG_DEMO_NEXT", "Click of Next after capturing demographic details"),

    // Registration Preview
    REG_PREVIEW_DEMO_EDIT("REG-EVT-114", USER_EVENT.getCode(), "REG_PREVIEW_DEMO_EDIT", "Click of Edit demographics"),
    REG_PREVIEW_DOC_EDIT("REG-EVT-112", USER_EVENT.getCode(), "REG_PREVIEW_DOC_EDIT", "Click of Edit documents"),
    REG_PREVIEW_BIO_EDIT("REG-EVT-113", USER_EVENT.getCode(), "REG_PREVIEW_BIO_EDIT", "Click of Biometrics Edit"),
    REG_PREVIEW_SUBMIT("REG-EVT-046", USER_EVENT.getCode(), "REG_PREVIEW_SUBMIT",
            "Click of Next after Registration Preview"),
    REG_PREVIEW_BACK("REG-EVT-047", USER_EVENT.getCode(), "REG_PREVIEW_BACK","Click of Back from registration preview screen"),

    //REGISTRATION SYSTEM EVENT
    DISCOVER_SBI_FAILED("REG-EVT-070", SYSTEM_EVENT.getCode(), "DISCOVER_SBI_FAILED", "SBI discovery failed"),
    DEVICE_INFO_FAILED("REG-EVT-049", SYSTEM_EVENT.getCode(), "DEVICE_INFO_FAILED", "Device info failed"),
    R_CAPTURE_FAILED("REG-EVT-077", SYSTEM_EVENT.getCode(), "R_CAPTURE_FAILED", "R_capture failed"),
    DISCOVER_SBI_PARSE_FAILED("REG-EVT-051", SYSTEM_EVENT.getCode(), "DISCOVER_SBI_PARSE_FAILED", "SBI discovery response parsing failed"),
    DEVICE_INFO_PARSE_FAILED("REG-EVT-052", SYSTEM_EVENT.getCode(), "DEVICE_INFO_PARSE_FAILED", "Device info response parsing failed"),
    R_CAPTURE_PARSE_FAILED("REG-EVT-053", SYSTEM_EVENT.getCode(), "R_CAPTURE_PARSE_FAILED", "R_capture response parsing failed"),
    CREATE_PACKET_FAILED("REG-EVT-054", USER_EVENT.getCode(), "CREATE_PACKET_FAILED", "Packet creation failed"),

    // Packet Upload
    PACKET_UPLOAD("REG-EVT-083", USER_EVENT.getCode(), "PACKET_UPLOAD", "Upload the local packets to the server"),

    // Registration : Documents
    REG_DOC_NEXT("REG-EVT-025", USER_EVENT.getCode(), "REG_DOC_NEXT", "Click of Next after uploading documents"),
    REG_DOC_BACK("REG-EVT-026", USER_EVENT.getCode(), "REG_DOC_BACK", "Click of Back to demographic details"),

    // MDM
    REG_DOC_SCAN("REG-EVT-089", USER_EVENT.getCode(), "REG_DOC_SCAN", "Doc: Click of Scan for %s"),
    REG_DOC_VIEW("REG-EVT-090", USER_EVENT.getCode(), "REG_DOC_VIEW", "Doc: View %s document"),
    REG_DOC_DELETE("REG-EVT-091", USER_EVENT.getCode(), "REG_DOC_DELETE", "Doc: Delete %s document"),

    // Approve Registration
    APPR_VIEW_REG("REG-EVT-066", USER_EVENT.getCode(), "APPR_VIEW_REG", "View registration detail"),
    APPR_REG("REG-EVT-067", USER_EVENT.getCode(), "APPR_REG", "Approve registration"),
    REJECT_REG("REG-EVT-068", USER_EVENT.getCode(), "REJECT_REG", "Reject registration"),

    // Geo-Location
    GEO_LOCATION_CAPTURE("REG-GEO-LOC-001", SYSTEM_EVENT.getCode(), "GEO_LOCATION_CAPTURE", "Capture geo-location"),
    GEO_LOCATION_DENIED("REG-GEO-LOC-002", SYSTEM_EVENT.getCode(), "GEO_LOCATION_DENIED", "Denied geo-location"),

    // Client To Server Sync
    SYNC_USER_MAPPING("REG-SYNC-008", USER_EVENT.getCode(), "SYNC_USER_MAPPING", "Sync user mapping"),
    SYNC_DEVICE_MAPPING("REG-SYNC-009", USER_EVENT.getCode(), "SYNC_DEVICE_MAPPING", "Sync device mapping"),
    SYNC_CLIENT_STATE("REG-SYNC-010", USER_EVENT.getCode(), "SYNC_CLIENT_STATE", "Sync client state"),
    SYNCJOB_INFO_FETCH("REG-SYNC-011", USER_EVENT.getCode(), "SYNC_JOB_INFO_FETCH", "SyncJobInfo containing the sync control list and yet to export packet count fetched successfully"),
    SYNC_INFO_VALIDATE("REG-SYNC-012", USER_EVENT.getCode(), "SYNC_INFO_VALIDATION", "Validating the sync status ended successfully"),
    SYNC_PKT_COUNT_VALIDATE("REG-SYNC-013", USER_EVENT.getCode(), "SYNC_PKT_COUNT_VALIDATION", "Validating yet to export packets frequency with the configured limit count"),
    SYNC_GEO_VALIDATE("REG-SYNC-015", USER_EVENT.getCode(), "SYNC_GEO_VALIDATE", "Validating the geo information ended successfully"),

    // Export Packets
    EXPORT_REG_PACKETS("REG-EXPT-PKT-001", USER_EVENT.getCode(), "EXPORT_REGISTRATION_PACKETS",
            "Export Packets: To external device"),

    // Sync Packet Ids
    SYNC_PACKET_IDS("REG-SYNC-PKTS-001", SYSTEM_EVENT.getCode(), "SYNC_PACKET_IDS", "Send Packet IDs to server"),

    // Sync Packets
    SYNC_SERVER("REG-SYNC-014", USER_EVENT.getCode(), "SYNC_SERVER", "Synchronize the packet status to the server"),

    // Server To Client Sync
    SYNC_MASTER_DATA("REG-SYNC-001", USER_EVENT.getCode(), "SYNC_MASTER_DATA", "Sync master data"),
    SYNC_REGISTRATION_CENTER_DETAILS("REG-SYNC-002", USER_EVENT.getCode(), "SYNC_REGISTRATION_CENTER_DETAILS",
            "Sync registration centre details"),
    SYNC_MACHINE_DETAILS("REG-SYNC-003", USER_EVENT.getCode(), "SYNC_MACHINE_DETAILS", "Sync machine details"),
    SYNC_DEVICE_DETAILS("REG-SYNC-004", USER_EVENT.getCode(), "SYNC_DEVICE_DETAILS", "Sync device details"),
    SYNC_USER_DETAILS("REG-SYNC-005", USER_EVENT.getCode(), "SYNC_USER_DETAILS", "Sync user details"),
    SYNC_REGISTRATION_PACKET_STATUS("REG-SYNC-006", USER_EVENT.getCode(), "SYNC_REGISTRATION_PACKET_STATUS",
            "Sync registration packet status"),
    SYNC_PRE_REGISTRATION_PACKET("REG-SYNC-007", USER_EVENT.getCode(), "SYNC_PRE_REGISTRATION_PACKET",
            "Sync pre-registration data"),

    // Scheduler Util
    SCHEDULER_REFRESHED_TIMEOUT("REG-SCH-002", SYSTEM_EVENT.getCode(), "REFRESHED_TIMEOUT",
            "The time task remainder alert"),
    SCHEDULER_SESSION_TIMEOUT("REG-SCH-003", SYSTEM_EVENT.getCode(), "SESSION_TIMEOUT",
            "The time task session expires"),

    // Registration: Operator/Supervisor Authentication
    REG_OPERATOR_AUTH_PASSWORD("REG-EVT-048", USER_EVENT.getCode(), "REG_OPERATOR_AUTH_PASSWORD","Operator authentication with password: Click of Submit"),
    REG_OPERATOR_AUTH_PREVIEW("REG-EVT-055", USER_EVENT.getCode(), "REG_OPERATOR_AUTH_PREVIEW", "Back to Preview"),
    REG_ACK_PRINT("REG-EVT-056", USER_EVENT.getCode(), "REG_ACK_PRINT", "Print receipt"),

    // Registration Packet Creation
    PACKET_CREATION_SUCCESS("REG-EVT-078", USER_EVENT.getCode(), "PACKET_CREATION_SUCCESS","Packet Successfully Created"),
    PACKET_ENCRYPTED_AND_INTERNAL_ZIP("REG-EVT-085", USER_EVENT.getCode(), "PACKET_ENCRYPTED_AND_INTERNAL_ZIP", "Packet encrypted and zipped successfully"),
    PACKET_UPLOADED("REG-EVT-080", USER_EVENT.getCode(), "PACKET_UPLOADED", "Packet Uploaded Successfully"),
    PACKET_SYNCED_TO_SERVER("REG-EVT-081", USER_EVENT.getCode(), "PACKET_SYNCED_TO_SERVER", "Packet Synced to Server Successfully"),
    PACKET_STATUS_UPDATE("REG-EVT-071", USER_EVENT.getCode(), "PACKET_APPROVED", "Packet %s Successfully"),
    PACKET_INTERNAL_ERROR("REG-EVT-074", USER_EVENT.getCode(), "PACKET_INTERNAL_ERROR", "Packet Creation Error"),
    PACKET_DEMO_JSON_CREATED("REG-EVT-076", USER_EVENT.getCode(), "PACKET_DEMO_JSON_CREATED", "Packet Demographic JSON created successfully"),
    SAVE_DETAIL_TO_DTO("REG-EVT-084", USER_EVENT.getCode(), "SAVE_DETAIL_TO_DTO", "Saving the details to DTO"),

    // Biometrics
    REG_BIO_EXCEPTION_MARKING("REG-EVT-027", USER_EVENT.getCode(), "REG_BIO_EXCEPTION_MARKING", "Biometric Exceptions: Marking"),
    REG_BIO_NEXT("REG-EVT-037", USER_EVENT.getCode(), "REG_BIO_NEXT", "Click Next to capture the next biometric"),
    REG_BIO_EXCEPTION_REMOVING("REG-EVT-065", USER_EVENT.getCode(), "REG_BIO_EXCEPTION_REMOVING", "Biometric Exceptions: Removing"),
    REG_BIO_SCAN("REG-EVT-092", USER_EVENT.getCode(), "REG_BIO_SCAN", 	"Scan of %s"),
    REG_BIO_CAPTURE_NEXT("REG-EVT-041", USER_EVENT.getCode(), "REG_BIO_CAPTURE_NEXT",	"Click of Next after capturing Biometrics");

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