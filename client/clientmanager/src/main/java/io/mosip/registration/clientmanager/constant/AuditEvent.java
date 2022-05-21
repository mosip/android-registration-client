package io.mosip.registration.clientmanager.constant;


import static io.mosip.registration.clientmanager.constant.AuditEventType.USER_EVENT;
import static io.mosip.registration.clientmanager.constant.AuditEventType.SYSTEM_EVENT;

/**
 * Enum for Audit Events
 *
 * @author Balaji Sridharan
 * @since 1.0.0
 *
 */
public enum AuditEvent {

    // Login
    LOGIN_AUTHENTICATE_USER_ID("REG-AUTH-001", USER_EVENT.getCode(), "LOGIN_AUTHENTICATE_USER_ID",
            "Login authenticating user id: Click of Submit"),
    LOGIN_WITH_PASSWORD("REG-AUTH-002", USER_EVENT.getCode(), "LOGIN_WITH_PASSWORD",
            "Login with password: Click of Submit"),
    LOGIN_GET_OTP("REG-AUTH-003", USER_EVENT.getCode(), "LOGIN_GET_OTP", "Login with OTP: Get OTP"),
    LOGIN_SUBMIT_OTP("REG-AUTH-004", USER_EVENT.getCode(), "LOGIN_SUBMIT_OTP", "Login with OTP: Submit OTP"),
    LOGIN_RESEND_OTP("REG-AUTH-005", USER_EVENT.getCode(), "LOGIN_RESEND_OTP", "Login with OTP: Resend OTP"),
    LOGIN_WITH_FINGERPRINT("REG-AUTH-006", USER_EVENT.getCode(), "LOGIN_WITH_FINGERPRINT",
            "Login with fingerprint: Capture and submit"),
    LOGIN_WITH_IRIS("REG-AUTH-007", USER_EVENT.getCode(), "LOGIN_WITH_IRIS", "Login with iris: Capture and submit"),
    LOGIN_WITH_FACE("REG-AUTH-008", USER_EVENT.getCode(), "LOGIN_WITH_FACE", "Login with face: Capture and submit"),
    LOGOUT_USER("REG-AUTH-009", USER_EVENT.getCode(), "LOGOUT_USER", "Logout"),

    // Navigation
    NAV_LOST_UIN("REG-EVT-001", USER_EVENT.getCode(), "NAV_LOST_UIN", "Click of navigation link: Lost UIN"),
    NAV_NEW_REG("REG-EVT-002", USER_EVENT.getCode(), "NAV_NEW_REG", "Click of navigation link: New Registration"),
    NAV_UIN_UPDATE("REG-EVT-003", USER_EVENT.getCode(), "NAV_UIN_UPDATE", "Navigation link: UIN Update"),
    NAV_APPROVE_REG("REG-EVT-004", USER_EVENT.getCode(), "NAV_APPROVE_REG", "Navigation link: Approve Registration"),
    NAV_UPLOAD_PACKETS("REG-EVT-005", USER_EVENT.getCode(), "NAV_UPLOAD_PACKETS", "Navigation link: Upload Packets"),
    NAV_RE_REGISTRATION("REG-EVT-006", SYSTEM_EVENT.getCode(), "NAV_RE_REGISTRATION",
            "Navigation link: Re-Registration"),
    NAV_CORRECTION("REG-EVT-007", USER_EVENT.getCode(), "NAV_CORRECTION", "Navigation link: CORRECTION"),
    NAV_SYNC_PACKETS("REG-NAV-001", USER_EVENT.getCode(), "NAV_SYNC_PACKETS", "Navigation link: Sync Packet IDs"),
    NAV_VIRUS_SCAN("REG-NAV-002", USER_EVENT.getCode(), "NAV_VIRUS_SCAN", "Navigation link: Virus Scan"),
    NAV_SYNC_DATA("REG-NAV-003", USER_EVENT.getCode(), "NAV_SYNC_DATA", "Navigation link: Sync Data"),
    NAV_DOWNLOAD_PRE_REG_DATA("REG-NAV-004", USER_EVENT.getCode(), "NAV_DOWNLOAD_PRE_REG_DATA",
            "Navigation link: Download Pre-registration Data"),
    NAV_GEO_LOCATION("REG-NAV-005", USER_EVENT.getCode(), "NAV_GEO_LOCATION", "Navigation link: Geo-location"),
    NAV_ON_BOARD_USER("REG-NAV-006", USER_EVENT.getCode(), "NAV_ON_BOARD_USER", "Navigation link: On-board Users"),
    NAV_HOME("REG-NAV-007", SYSTEM_EVENT.getCode(), "NAV_HOME", "Navigation link: Home"),
    NAV_REDIRECT_HOME("REG-NAV-008", SYSTEM_EVENT.getCode(), "NAV_REDIRECT_HOME", "Navigation link: Redirect to Home"),
    NAV_ON_BOARD_DEVICES("REG-NAV-009", USER_EVENT.getCode(), "NAV_ON_BOARD_DEVICES",
            "Navigation link: On-board Devices"),
    NAV_DASHBOARD("REG-NAV-010", SYSTEM_EVENT.getCode(), "NAV_DASHBOARD",
            "Navigation link: Dashboard"),

    // Registration : Demographics Details
    REG_DEMO_CAPTURE("REG-EVT-007", USER_EVENT.getCode(), "REG_DEMO_NEXT", "Started capturing demographic details"),
    REG_DEMO_PRE_REG_DATA_FETCH("REG-EVT-008", USER_EVENT.getCode(), "REG_DEMO_PRE_REG_DATA_FETCH",
            "Pre-registration: Fetch data for selected Pre-registration"),
    REG_DEMO_NEXT("REG-EVT-009", USER_EVENT.getCode(), "REG_DEMO_NEXT",
            "Click of Next after capturing demographic details"),

    // Registration : Documents
    REG_DOC_POA_SCAN("REG-EVT-010", USER_EVENT.getCode(), "REG_DOC_POA_SCAN", "PoA: Click of Scan"),
    REG_DOC_POA_VIEW("REG-EVT-011", USER_EVENT.getCode(), "REG_DOC_POA_VIEW", "PoA: View"),
    REG_DOC_POA_DELETE("REG-EVT-012", USER_EVENT.getCode(), "REG_DOC_POA_DELETE", "PoA: Delete"),
    REG_DOC_POI_SCAN("REG-EVT-013", USER_EVENT.getCode(), "REG_DOC_POI_SCAN", "PoI: Click of Scan"),
    REG_DOC_POI_VIEW("REG-EVT-014", USER_EVENT.getCode(), "REG_DOC_POI_VIEW", "PoI: View"),
    REG_DOC_POI_DELETE("REG-EVT-015", USER_EVENT.getCode(), "REG_DOC_POI_DELETE", "PoI: Delete"),
    REG_DOC_POR_SCAN("REG-EVT-016", USER_EVENT.getCode(), "REG_DOC_POR_SCAN", "PoR: Click of Scan"),
    REG_DOC_POR_VIEW("REG-EVT-017", USER_EVENT.getCode(), "REG_DOC_POR_VIEW", "PoR: View"),
    REG_DOC_POR_DELETE("REG-EVT-018", USER_EVENT.getCode(), "REG_DOC_POR_DELETE", "PoR: Delete"),
    REG_DOC_POB_SCAN("REG-EVT-019", USER_EVENT.getCode(), "REG_DOC_POB_SCAN", "PoB: Click of Scan"),
    REG_DOC_POB_VIEW("REG-EVT-020", USER_EVENT.getCode(), "REG_DOC_POB_VIEW", "PoB: View"),
    REG_DOC_POB_DELETE("REG-EVT-021", USER_EVENT.getCode(), "REG_DOC_POB_DELETE", "PoB: Delete"),
    REG_DOC_POE_SCAN("REG-EVT-022", USER_EVENT.getCode(), "REG_DOC_POB_SCAN", "PoB: Click of Scan"),
    REG_DOC_POE_VIEW("REG-EVT-023", USER_EVENT.getCode(), "REG_DOC_POB_VIEW", "PoB: View"),
    REG_DOC_POE_DELETE("REG-EVT-024", USER_EVENT.getCode(), "REG_DOC_POB_DELETE", "PoB: Delete"),
    REG_DOC_NEXT("REG-EVT-025", USER_EVENT.getCode(), "REG_DOC_NEXT", "Click of Next after uploading documents"),
    REG_DOC_BACK("REG-EVT-026", USER_EVENT.getCode(), "REG_DOC_BACK", "Click of Back to demographic details"),

    // Registration: Biometrics
    REG_BIO_EXCEPTION_MARKING("REG-EVT-027", USER_EVENT.getCode(), "REG_BIO_EXCEPTION_MARKING",
            "Biometric Exceptions: Marking"),
    REG_BIO_EXCEPTION_NEXT("REG-EVT-028", USER_EVENT.getCode(), "REG_BIO_EXCEPTION_NEXT",
            "Click of Next after marking exceptions"),
    REG_BIO_EXCEPTION_BACK("REG-EVT-029", USER_EVENT.getCode(), "REG_BIO_EXCEPTION_BACK",
            "Click of Back from biometric exception capture screen"),
    REG_BIO_LEFT_SLAP_SCAN("REG-EVT-030", USER_EVENT.getCode(), "REG_BIO_LEFT_SLAP_SCAN",
            "Fingerprints: Scan of left slap"),
    REG_BIO_RIGHT_SLAP_SCAN("REG-EVT-031", USER_EVENT.getCode(), "REG_BIO_RIGHT_SLAP_SCAN",
            "Fingerprints: Scan of right slap"),
    REG_BIO_THUMBS_SCAN("REG-EVT-032", USER_EVENT.getCode(), "REG_BIO_THUMBS_SCAN", "Fingerprints: Scan of thumbs"),
    REG_BIO_FINGERPRINT_BACK("REG-EVT-033", USER_EVENT.getCode(), "REG_BIO_FINGERPRINT_BACK",
            "Click of Back from fingerprint capture screen"),
    REG_BIO_IRIS_SCAN("REG-EVT-034", USER_EVENT.getCode(), "REG_BIO_IRIS_SCAN", "Iris: Scan of irises"),
    REG_BIO_LEFT_IRIS_SCAN("REG-EVT-035", USER_EVENT.getCode(), "REG_BIO_LEFT_IRIS_SCAN", "Iris: Scan of left iris"),
    REG_BIO_RIGHT_IRIS_SCAN("REG-EVT-036", USER_EVENT.getCode(), "REG_BIO_RIGHT_IRIS_SCAN", "Iris: Scan of right iris"),
    REG_BIO_IRIS_NEXT("REG-EVT-037", USER_EVENT.getCode(), "REG_BIO_IRIS_NEXT", "Click of Next after capturing irises"),
    REG_BIO_IRIS_BACK("REG-EVT-038", USER_EVENT.getCode(), "REG_BIO_IRIS_BACK",
            "Click of Back from iris capture screen"),
    REG_BIO_FACE_CAPTURE("REG-EVT-039", USER_EVENT.getCode(), "REG_BIO_FACE_CAPTURE", "Photo: Face capture"),
    REG_BIO_EXCEP_FACE_CAPTURE("REG-EVT-040", USER_EVENT.getCode(), "REG_BIO_EXCEPION_FACE_CAPTURE",
            "Photo: Exception face capture"),
    REG_BIO_CAPTURE_NEXT("REG-EVT-041", USER_EVENT.getCode(), "REG_BIO_CAPTURE_NEXT",
            "Click of Next after capturing Biometrics"),
    REG_BIO_FACE_CAPTURE_BACK("REG-EVT-042", USER_EVENT.getCode(), "REG_BIO_FACE_CAPTURE_BACK",
            "Click of Back from face photo capture screen"),

    // Registration Preview
    REG_PREVIEW_DEMO_EDIT("REG-EVT-043", USER_EVENT.getCode(), "REG_PREVIEW_DEMO_EDIT", "Click of Edit demographics"),
    REG_PREVIEW_DOC_EDIT("REG-EVT-044", USER_EVENT.getCode(), "REG_PREVIEW_DOC_EDIT", "Click of Edit documents"),
    REG_PREVIEW_BIO_EDIT("REG-EVT-045", USER_EVENT.getCode(), "REG_PREVIEW_BIO_EDIT", "Click of Biometrics Edit"),
    REG_PREVIEW_SUBMIT("REG-EVT-046", USER_EVENT.getCode(), "REG_PREVIEW_SUBMIT",
            "Click of Next after Registration Preview"),
    REG_PREVIEW_BACK("REG-EVT-047", USER_EVENT.getCode(), "REG_PREVIEW_BACK",
            "Click of Back from registration preview screen"),

    // Registration: Operator/Supervisor Authentication
    REG_OPERATOR_AUTH_PASSWORD("REG-EVT-048", USER_EVENT.getCode(), "REG_OPERATOR_AUTH_PASSWORD",
            "Operator authentication with password: Click of Submit"),
    REG_OPERATOR_AUTH_GET_OTP("REG-EVT-049", USER_EVENT.getCode(), "REG_OPERATOR_AUTH_GET_OTP",
            "Operator authentication with OTP: Get OTP"),
    REG_OPERATOR_AUTH_SUBMIT_OTP("REG-EVT-050", USER_EVENT.getCode(), "REG_OPERATOR_AUTH_SUBMIT_OTP",
            "Operator authentication with OTP: Submit OTP"),
    REG_OPERATOR_AUTH_RESEND_OTP("REG-EVT-051", USER_EVENT.getCode(), "REG_OPERATOR_AUTH_RESEND_OTP",
            "Operator authentication with OTP: Resend OTP"),
    REG_OPERATOR_AUTH_FINGERPRINT("REG-EVT-052", USER_EVENT.getCode(), "REG_OPERATOR_AUTH_FINGERPRINT",
            "Operator authentication with fingerprint: Capture and submit"),
    REG_OPERATOR_AUTH_IRIS("REG-EVT-053", USER_EVENT.getCode(), "REG_OPERATOR_AUTH_IRIS",
            "Operator authentication with iris: Capture and submit"),
    REG_OPERATOR_AUTH_FACE("REG-EVT-054", USER_EVENT.getCode(), "REG_OPERATOR_AUTH_FACE",
            "Operator authentication with face: Capture and submit"),
    REG_OPERATOR_AUTH_PREVIEW("REG-EVT-055", USER_EVENT.getCode(), "REG_OPERATOR_AUTH_PREVIEW", "Back to Preview"),
    REG_ACK_PRINT("REG-EVT-056", USER_EVENT.getCode(), "REG_ACK_PRINT", "Print receipt"),
    REG_REVIEWER_AUTH_PASSWORD("REG-EVT-057", USER_EVENT.getCode(), "REG_REVIEWER_AUTH_PASSWORD",
            "Reviewer authentication with password: Click of Submit"),
    REG_REVIEWER_AUTH_GET_OTP("REG-EVT-058", USER_EVENT.getCode(), "REG_REVIEWER_AUTH_GET_OTP",
            "Reviewer authentication with OTP: Get OTP"),
    REG_REVIEWER_AUTH_SUBMIT_OTP("REG-EVT-059", USER_EVENT.getCode(), "REG_REVIEWER_AUTH_SUBMIT_OTP",
            "Reviewer authentication with OTP: Submit OTP"),
    REG_REVIEWER_AUTH_RESEND_OTP("REG-EVT-060", USER_EVENT.getCode(), "REG_REVIEWER_AUTH_RESEND_OTP",
            "Reviewer authentication with OTP: Resend OTP"),
    REG_REVIEWER_AUTH_FINGERPRINT("REG-EVT-061", USER_EVENT.getCode(), "REG_REVIEWER_AUTH_FINGERPRINT",
            "Reviewer authentication with fingerprint: Capture and submit"),
    REG_REVIEWER_AUTH_IRIS("REG-EVT-062", USER_EVENT.getCode(), "REG_REVIEWER_AUTH_IRIS",
            "Reviewer authentication with iris: Capture and submit"),
    REG_REVIEWER_AUTH_FACE("REG-EVT-063", USER_EVENT.getCode(), "REG_REVIEWER_AUTH_FACE",
            "Reviewer authentication with face: Capture and submit"),
    REG_REVIEWER_AUTH_PREVIEW("REG-EVT-064", USER_EVENT.getCode(), "REG_REVIEWER_AUTH_PREVIEW", "Back to Preview"),

    REG_BIO_EXCEPTION_REMOVING("REG-EVT-065", USER_EVENT.getCode(), "REG_BIO_EXCEPTION_REMOVING",
            "Biometric Exceptions: Removing"),

    // Approve Registration
    APPR_VIEW_REG("REG-EVT-066", USER_EVENT.getCode(), "APPR_VIEW_REG", "View registration detail"),
    APPR_REG("REG-EVT-067", USER_EVENT.getCode(), "APPR_REG", "Approve registration"),
    REJECT_REG("REG-EVT-068", USER_EVENT.getCode(), "REJECT_REG", "Reject registration"),

    // Sync Packet Ids
    SYNC_PACKET_IDS("REG-SYNC-PKTS-001", SYSTEM_EVENT.getCode(), "SYNC_PACKET_IDS", "Send Packet IDs to server"),

    // Upload Packets
    UPLOAD_PACKET("REG-UPL-PKT-001", SYSTEM_EVENT.getCode(), "UPLOAD_PACKET", "Upload packets"),

    // Virus Scan
    VIRUS_SCAN_REG_CLIENT("REG-VIRUS-SCN-001", USER_EVENT.getCode(), "VIRUS_SCAN_REG_CLIENT",
            "Scan registration client"),
    VIRUS_SCAN_REG_PACKETS("REG-VIRUS-SCN-002", USER_EVENT.getCode(), "VIRUS_SCAN_REG_PACKETS",
            "Scan registration packets"),

    // Geo-Location
    GEO_LOCATION_CAPTURE("REG-GEO-LOC-001", SYSTEM_EVENT.getCode(), "GEO_LOCATION_CAPTURE", "Capture geo-location"),

    // On-Board Users
    ON_BOARD_USER("REG-USR-ONBOARD-001", USER_EVENT.getCode(), "ON_BOARD_USER", "On-board user"),
    DE_BOARD_MAPPED_USER("REG-USR-ONBOARD-002", USER_EVENT.getCode(), "DE_BOARD_MAPPED_USER",
            "Deactivate user mapping"),
    ACTIVATE_MAPPED_USER("REG-USR-ONBOARD-003", USER_EVENT.getCode(), "ACTIVATE_MAPPED_USER", "Activate user mapping"),
    DELETE_ON_BOARDER_USER("REG-USR-ONBOARD-004", USER_EVENT.getCode(), "DELETE_ON_BOARDER_USER",
            "Delete user mapping"),

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

    // Client To Server Sync
    SYNC_USER_MAPPING("REG-SYNC-008", USER_EVENT.getCode(), "SYNC_USER_MAPPING", "Sync user mapping"),
    SYNC_DEVICE_MAPPING("REG-SYNC-009", USER_EVENT.getCode(), "SYNC_DEVICE_MAPPING", "Sync device mapping"),
    SYNC_CLIENT_STATE("REG-SYNC-010", USER_EVENT.getCode(), "SYNC_CLIENT_STATE", "Sync client state"),

    // Export Packets
    EXPORT_REG_PACKETS("REG-EXPT-PKT-001", USER_EVENT.getCode(), "EXPORT_REGISTRATION_PACKETS",
            "Export Packets: To external device"),

    // Registration Packet Creation
    PACKET_CREATION_SUCCESS("REG-EVT-066", USER_EVENT.getCode(), "PACKET_CREATION_SUCCESS",
            "Packet Succesfully Created"),
    PACKET_ENCRYPTED("REG-EVT-067", USER_EVENT.getCode(), "PACKET_ENCRYPTED", "Packet Encrypted Sucessfully"),
    PACKET_UPLOADED("REG-EVT-068", USER_EVENT.getCode(), "PACKET_UPLOADED", "Packet Uploaded Successfully"),
    PACKET_SYNCED_TO_SERVER("REG-EVT-069", USER_EVENT.getCode(), "PACKET_SYNCED_TO_SERVER",
            "Packet Synced to Server Sucesfully"),
    PACKET_DELETED("REG-EVT-070", USER_EVENT.getCode(), "PACKET_DELETED", "Packet Deleted Successfully"),
    PACKET_APPROVED("REG-EVT-071", USER_EVENT.getCode(), "PACKET_APPROVED", "Packet Approved Successfully"),
    PACKET_REJECTED("REG-EVT-072", USER_EVENT.getCode(), "PACKET_REJECTED", "Packet Rejected Successfully"),
    PACKET_HOLDED("REG-EVT-073", USER_EVENT.getCode(), "PACKET_HOLDED", "Packet Holded for particular reason"),
    PACKET_INTERNAL_ERROR("REG-EVT-074", USER_EVENT.getCode(), "PACKET_INTERNAL_ERROR", "Packet Creation Error"),
    PACKET_INTERNAL_ZIP("REG-EVT-075", USER_EVENT.getCode(), "PACKET_INTERNAL_ZIP",
            "Packet internally zipped successfully"),
    PACKET_DEMO_JSON_CREATED("REG-EVT-076", USER_EVENT.getCode(), "PACKET_DEMO_JSON_CREATED",
            "Packet Demographic JSON created successfully"),
    PACKET_HMAC_FILE_CREATED("REG-EVT-077", USER_EVENT.getCode(), "PACKET_HMAC_FILE_CREATED",
            "Packet HMAC File created successfully"),
    PACKET_META_JSON_CREATED("REG-EVT-080", USER_EVENT.getCode(), "PACKET_META_JSON_CREATED",
            "Packet Meta-Data JSON created successfully"),
    PACKET_AUDIT_JSON_CREATED("REG-EVT-081", USER_EVENT.getCode(), "PACKET_AUDIT_JSON_CREATED",
            "Packet Audit JSON created successfully"),
    PACKET_AES_ENCRYPTED("REG-EVT-082", USER_EVENT.getCode(), "PACKET_AES_ENCRYPTED", "Packet Encrypted Sucessfully"),

    // Scheduler
    UI_SCHEDULER_STARTED("REG-SCH-001", USER_EVENT.getCode(), "UI_SCHEDULER_STARTED", "Scheduler started Sucessfully"),

    // Login and User related
    LOGIN_MODES_FETCH("REG-AUTH-010", USER_EVENT.getCode(), "LOGIN_MODES_FETCH", "Fetching Login Modes"),
    USER_STATUS_FETCH("REG-AUTH-011", USER_EVENT.getCode(), "USER_STATUS_FETCH", "Fetching User Status"),
    VALIDATE_USER_CRED("REG-AUTH-012", USER_EVENT.getCode(), "VALIDATE_USER_CREDENTIALS",
            "Validating User credentials"),
    FETCH_USR_DET("REG-AUTH-013", USER_EVENT.getCode(), "FETCH_USER_DETAILS", "Fetching User Details"),
    FETCH_CNTR_NAME("REG-AUTH-014", USER_EVENT.getCode(), "FETCH_CENTER_NAME", "Fetching Center Name"),
    FETCH_CNTR_DET("REG-AUTH-015", USER_EVENT.getCode(), "FETCH_CENTER_DETAILS", "Fetching Center Details"),
    FETCH_USR_ROLE("REG-AUTH-016", USER_EVENT.getCode(), "FETCH_USER_ROLE", "Fetching User Roles"),
    FETCH_SCR_AUTH("REG-AUTH-017", USER_EVENT.getCode(), "FETCH_SCREEN_AUTH", "Fetching screens to be authorized"),
    SYNCJOB_INFO_FETCH("REG-SYNC-011", USER_EVENT.getCode(), "SYNC_JOB_INFO_FETCH",
            "SyncJobInfo containing the sync control list and yet to export packet count fetched successfully"),
    SYNC_INFO_VALIDATE("REG-SYNC-012", USER_EVENT.getCode(), "SYNC_INFO_VALIDATION",
            "Validating the sync status ended successfully"),
    SYNC_PKT_COUNT_VALIDATE("REG-SYNC-013", USER_EVENT.getCode(), "SYNC_PKT_COUNT_VALIDATION",
            "Validating yet to export packets frequency with the configured limit count"),
    PACKET_RETRIVE("REG-PKT-001", USER_EVENT.getCode(), "PACKET_RETRIVE",
            "Packets which are in created state for approval are retrived"),
    PACKET_UPDATE("REG-PKT-002", USER_EVENT.getCode(), "PACKET_UPDATE",
            "Packets which are in created state are updated"),
    SYNC_GEO_VALIDATE("REG-SYNC-014", USER_EVENT.getCode(), "SYNC_GEO_VALIDATE",
            "Validating the geo information ended successfully"),
    PENDING_PKT_CNT_VALIDATE("REG-PKT-003", USER_EVENT.getCode(), "PENDING_PKT_COUNT_VALIDATION",
            "Validating the Pending packets count"),
    PENDING_PKT_DUR_VALIDATE("REG-PKT-004", USER_EVENT.getCode(), "PENDING_PKT_DURATION_VALIDATION",
            "Validating the Pending packets Duration"),
    DEVICE_MAPPING_SUCCESS("REG-AUTH-018", USER_EVENT.getCode(), "DEVICE_MAPPING_SUCCESS",
            "device mapped successfully"),
    DEVICE_UN_MAPPING("REG-AUTH-019", USER_EVENT.getCode(), "DEVICE_UN_MAPPING", "Device is unmapped "),

    // Sync Packets
    SYNC_SERVER("REG-SYNC-014", USER_EVENT.getCode(), "SYNC_SERVER", "Synchronize the packet status to the server"),

    // Packet Upload
    PACKET_UPLOAD("REG-EVT-083", USER_EVENT.getCode(), "PACKET_UPLOAD", "Upload the local packets to the server"),

    // Notification Service
    NOTIFICATION_STATUS("REG-NOT-001", USER_EVENT.getCode(), "NOTIFICATION_SERVICE", "Notification request status"),

    // Registration Audits
    SAVE_DETAIL_TO_DTO("REG-EVT-084", USER_EVENT.getCode(), "SAVE_DETAIL_TO_DTO", "Saving the details to DTO"),
    MACHINE_REMAPPED("REG-AUTH-020", USER_EVENT.getCode(), "CENTER_MACHINE_REMAP_SERVICE",
            "machine is remapped to another center"),

    // Scheduler Util
    SCHEDULER_REFRESHED_TIMEOUT("REG-SCH-002", SYSTEM_EVENT.getCode(), "REFRESHED_TIMEOUT",
            "The time task remainder alert"),
    SCHEDULER_SESSION_TIMEOUT("REG-SCH-003", SYSTEM_EVENT.getCode(), "SESSION_TIMEOUT",
            "The time task session expires"),

    // MDM
    MDM_CAPTURE_FAILED("REG-EVT-085", USER_EVENT.getCode(), "CAPTURE_FAILED", "Biometric capture failed"),
    MDM_CAPTURE_SUCCESS("REG-EVT-086", USER_EVENT.getCode(), "CAPTURE_SUCESS", "Biometric capture completed"),
    MDM_NO_DEVICE_AVAILABLE("REG-EVT-087", USER_EVENT.getCode(), "DEVICE_NOT_FOUND", "No device is available"),
    MDM_DEVICE_FOUND("REG-EVT-088", USER_EVENT.getCode(), "MDM_DEVICE_FOUND", "Device is found"),

    REG_DOC_SCAN("REG-EVT-089", USER_EVENT.getCode(), "REG_DOC_SCAN", "Doc: Click of Scan"),
    REG_DOC_VIEW("REG-EVT-090", USER_EVENT.getCode(), "REG_DOC_VIEW", "Doc: View"),
    REG_DOC_DELETE("REG-EVT-091", USER_EVENT.getCode(), "REG_DOC_DELETE", "Doc: Delete"),

    //FXControls
    REG_HTML_FX_CONTROL("REG-EVT-092", USER_EVENT.getCode(), "REG_HTML_FX_CONTROL",
            "Entered into Applicant Consent Page"),
    REG_CHECKBOX_FX_CONTROL("REG-EVT-093", USER_EVENT.getCode(), "REG_CHECKBOX_FX_CONTROL",
            "Checkbox has been selected"),
    REG_NAVIGATION("REG-EVT-094", USER_EVENT.getCode(), "REG_NAVIGATION",
            "Navigating to next screen"),

    //EOD Authentication
    REG_SUPERVISOR_AUTH_PASSWORD("REG-EVT-095", USER_EVENT.getCode(), "REG_SUPERVISOR_AUTH_PASSWORD",
            "Supervisor authentication with password: Click of Submit"),
    REG_SUPERVISOR_AUTH_GET_OTP("REG-EVT-096", USER_EVENT.getCode(), "REG_SUPERVISOR_AUTH_GET_OTP",
            "Supervisor authentication with OTP: Get OTP"),
    REG_SUPERVISOR_AUTH_SUBMIT_OTP("REG-EVT-097", USER_EVENT.getCode(), "REG_SUPERVISOR_AUTH_SUBMIT_OTP",
            "Supervisor authentication with OTP: Submit OTP"),
    REG_SUPERVISOR_AUTH_RESEND_OTP("REG-EVT-098", USER_EVENT.getCode(), "REG_SUPERVISOR_AUTH_RESEND_OTP",
            "Supervisor authentication with OTP: Resend OTP"),
    REG_SUPERVISOR_AUTH_FINGERPRINT("REG-EVT-099", USER_EVENT.getCode(), "REG_SUPERVISOR_AUTH_FINGERPRINT",
            "Supervisor authentication with fingerprint: Capture and submit"),
    REG_SUPERVISOR_AUTH_IRIS("REG-EVT-100", USER_EVENT.getCode(), "REG_SUPERVISOR_AUTH_IRIS",
            "Supervisor authentication with iris: Capture and submit"),
    REG_SUPERVISOR_AUTH_FACE("REG-EVT-101", USER_EVENT.getCode(), "REG_SUPERVISOR_AUTH_FACE",
            "Supervisor authentication with face: Capture and submit"),

    REG_BLOCKLISTED_WORD_ACCEPTED("REG-EVT-102", USER_EVENT.getCode(), "REG_BLOCKLISTED_WORD_ACCEPTED",
            "Demographic Details : A blocklisted word has been accepted in Registration"),
    REG_BLOCKLISTED_WORD_REJECTED("REG-EVT-103", USER_EVENT.getCode(), "REG_BLOCKLISTED_WORD_REJECTED",
            "Demographic Details : A blocklisted word has been rejected in Registration");



    /**
     * The constructor
     */
    private AuditEvent(String id, String type, String name, String description) {
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