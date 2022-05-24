package io.mosip.registration.clientmanager.constant;

<<<<<<< HEAD
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Class contains the constants used in Registration application
 * 
 * @author Balaji Sridharan
 * @since 1.0.0
 *
 */
public class RegistrationConstants {
	/**
	 * private constructor
	 */
	private RegistrationConstants() {

	}

	/*********** UI Constants **********/
	// paths of FXML pages to be loaded

	public static final String ERROR_PAGE = "/fxml/ErrorPage.fxml";
	public static final String INITIAL_PAGE = "/fxml/RegistrationLogin.fxml";
	public static final String HOME_PAGE = "/fxml/RegistrationOfficerLayout.fxml";
	public static final String HEADER_PAGE = "/fxml/Header.fxml";
	public static final String UPDATE_PAGE = "/fxml/UpdateLayout.fxml";
	public static final String OFFICER_PACKET_PAGE = "/fxml/RegistrationOfficerPacketLayout.fxml";
	public static final String CREATE_PACKET_PAGE = "/fxml/Registration.fxml";
	public static final String ACK_RECEIPT_PATH = "/fxml/AckReceipt.fxml";
	public static final String APPROVAL_PAGE = "/fxml/RegistrationApproval.fxml";
	public static final String FTP_UPLOAD_PAGE = "/fxml/PacketUpload.fxml";
	public static final String USER_MACHINE_MAPPING = "/fxml/UserClientMachineMapping.fxml";
	public static final String SYNC_STATUS = "/fxml/RegPacketStatus.fxml";
	public static final String ONHOLD_PAGE = "/fxml/OnholdComment.fxml";
	public static final String REJECTION_PAGE = "/fxml/RejectionComment.fxml";
	public static final String USER_AUTHENTICATION = "/fxml/Authentication.fxml";
	public static final String PENDING_ACTION_PAGE = "/fxml/RegistrationPendingAction.fxml";
	public static final String PENDING_APPROVAL_PAGE = "/fxml/RegistrationPendingApproval.fxml";
	public static final String REREGISTRATION_PAGE = "/fxml/ReRegistration.fxml";
	public static final String SCAN_PAGE = "/fxml/Scan.fxml";
	public static final String UIN_UPDATE = "/fxml/UpdateUIN.fxml";
	public static final String SYNC_DATA = "/fxml/SyncDataProcess.fxml";
	public static final String USER_ONBOARD = "/fxml/Onboard.fxml";
	public static final String SEND_NOTIFICATION_PAGE = "/fxml/SendNotification.fxml";
	public static final String PACKET_UPLOAD_STATUS_UI = "/fxml/PacketUploadStatus.fxml";
	public static final String ALERT_GENERATION = "/fxml/Alert.fxml";
	public static final String DASHBOARD_PAGE = "/fxml/Dashboard.fxml";
	public static final String SELECT_LANGUAGE_PAGE = "/fxml/SelectLanguage.fxml";
	public static final String REG_PREVIEW_PAGE = "/fxml/RegistrationPreview.fxml";
	public static final String REG_AUTH_PAGE = "/fxml/OperatorAuthentication.fxml";
	public static final String SETTINGS_PAGE = "/fxml/Settings.fxml";
	public static final String FXML_PATH = "/fxml/";
	public static final String BIOMETRICS_SCAN_PAGE = "/fxml/BiometricScanPopUp.fxml";

	// FXML Id
	public static final String DEMOGRAPHIC_DETAIL = "demographicDetail";
	public static final String DOCUMENT_SCAN = "documentScan";
	public static final String DOCUMENT_PANE = "documentPane";
	public static final String EXCEPTION_PANE = "exceptionPane";
	public static final String BIOMETRIC_EXCEPTION = "biometricException";
	public static final String FINGER_PANE = "fingerPane";
	public static final String FINGER_SINGLE = "FINGERPRINT_SINGLE";
	public static final String FIN = "FIN";
	public static final String FINGER_SLAB = "FINGERPRINT_SLAB";
	public static final String FINGERPRINT_SLAB_LEFT = "FINGERPRINT_SLAB_LEFT";
	public static final String FINGERPRINT_SLAB_RIGHT = "FINGERPRINT_SLAB_RIGHT";
	public static final String FINGERPRINT_SLAB_THUMBS = "FINGERPRINT_SLAB_THUMBS";
	public static final String MDM_ENVIRONMENT = "Staging";
	public static final int MDM_TIMEOUT = 10000;
	public static final String MDM_VERSION = "1.0";

	public static final String IRIS_PANE = "irisPane";
	public static final String IRIS_IMAGE_LOCAL = "/images/leftEye.png";
	public static final String IRIS_IMAGE_LOCAL_RIGHT = "/images/rightEye.png";
	public static final String IRIS_SINGLE = "IRIS_SINGLE";
	public static final String IRIS_DOUBLE = "IRIS_DOUBLE";
	public static final String FINGERPRINT_CAPTURE = "fingerPrintCapture";
	public static final String IRIS_CAPTURE = "irisCapture";
	public static final String FACE_CAPTURE = "faceCapture";
	public static final String REGISTRATION_PREVIEW = "registrationPreview";
	public static final String OPERATOR_AUTHENTICATION = "operatorAuthenticationPane";
	public static final String ONBOARD_USER_PARENT = "onboardUser";
	public static final String ONBOARD_USER_SUCCESS = "userOnboardSuccess";
	// public static final String BIOMETRIC_EXCEPTION_FLOW =
	// "biometricExceptionFlow";
	public static final String GUARDIAN_BIOMETRIC = "biometric";

	public static final String DOC_PREVIEW_ICON = "/images/hover.png";

	// css for quality indicator
	public static final String RETRY_ATTEMPT = "#retryAttempt_";
	public static final String RETRY_ATTEMPT_ID = "retryAttempt_";
	public static final String QUALITY_LABEL_GREY = "qualityLabelGrey";
	public static final String QUALITY_LABEL_GREEN = "qualityLabelGreen";
	public static final String QUALITY_LABEL_RED = "qualityLabelRed";
	public static final String QUALITY_LABEL_BLUE_BORDER = "qualityLabelBlueBorder" ;
	public static final String PROGRESS_BAR_RED = "progress-barRed";
	public static final String PROGRESS_BAR_GREEN = "progress-barGreen";
	public static final String LABEL_RED = "labelRed";
	public static final String LABEL_GREEN = "labelGreen";
	public static final String IRIS_PANES_SELECTED = "IrisPanesSelected";
	public static final String BIO_IRIS_SELECTED = "bioIris";
	public static final String BIOMETRICS_DISPLAY = "biometricsDisplay";
	public static final String TOOLTIP_STYLE = "tooltipStyle";

	// Page Flow
	public static final String VISIBILITY = "visibility";
	// public static final String ONBOARD_LIST = "onboardPageList";
	// public static final String ONBOARD_MAP = "onboardMap";
	public static final String REGISTRATION_MAP = "registrationMap";
	public static final String ONBOARD = "onboard";

	// CSS file
	// public static final String CSS_FILE_PATH = "application.css";

	public static final String MOSIP_URL = "http://mosip.io";

	// Images path
	public static final String CLOSE_IMAGE_PATH = "/images/Close.png";
	public static final String DONE_IMAGE_PATH = "/images/done.png";
	public static final String DOC_STUB_PATH = "/images/PANStubbed.jpg";
	public static final String FP_IMG_PATH = "/images/fingerprint.jpg";
	public static final String IRIS_IMG_PATH = "/images/iris.jpg";
	public static final String FACE_IMG_PATH = "/images/Photo.png";
	public static final String LEFTPALM_IMG_PATH = "/images/leftHand.png";
	public static final String RIGHTPALM_IMG_PATH = "/images/rightHand.png";
	public static final String THUMB_IMG_PATH = "/images/thumbs.png";
	public static final String RIGHT_IRIS_IMG_PATH = "/images/Eye.png";
	public static final String LEFT_IRIS_IMG_PATH = "/images/Eye.png";
	public static final String DOUBLE_IRIS_IMG_PATH = "/images/Eyes.png";
	public static final String VIEW = "/images/View.png";
	public static final String SCAN = "/images/scan.png";
	public static final String SCAN_FOCUSED = "/images/scanFocused.png";
	public static final String BACK = "/images/arrowLeft.png";
	public static final String BACK_FOCUSED = "/images/backInWhite.png";
	public static final String WRONG_IMAGE_PATH = "/images/wrong.png";
	public static final String REJECT_IMAGE_PATH = "/images/Reject.png";
	public static final String SEND_EMAIL_IMAGE_PATH = "/images/SendEmail.png";
	public static final String SEND_EMAIL_FOCUSED_IMAGE_PATH = "/images/sendEmailFocused.png";
	public static final String LOGO = "/images/logo-final-small.png";
	public static final String NEW_REG_IMAGE = "/images/NewReg.png";
	public static final String NEW_REG_FOCUSED = "/images/NewRegistration.png";
	public static final String UPDATE_UIN_IMAGE = "/images/UINUpdate.png";
	public static final String UPDATE_UIN_FOCUSED = "/images/updateUINFocused.png";
	public static final String LOST_UIN_IMAGE = "/images/LostUIN.png";
	public static final String LOST_UIN_FOCUSED = "/images/lostUINFocused.png";
	public static final String SYNC_DATA_IMAGE = "/images/sync.png";
	public static final String SYNC_DATA_FOCUSED = "/images/syncFocused.png";
	public static final String DOWNLOAD_PREREG_IMAGE = "/images/DownloadPreRegData.png";
	public static final String DOWNLOAD_PREREG_FOCUSED = "/images/DownloadPreRegDataFocused.png";
	public static final String UPDATE_OP_BIOMETRICS_IMAGE = "/images/UpdateOperatorBiometrics.png";
	public static final String UPDATE_OP_BIOMETRICS_FOCUSED = "/images/UpdateOperatorBiometricsFocused.png";
	public static final String PENDING_APPROVAL_IMAGE = "/images/PendingApproval.png";
	public static final String PENDING_APPROVAL_FOCUSED = "/images/PendingApprovalFocused.png";
	public static final String RE_REGISTRATION_IMAGE = "/images/Re-Registration.png";
	public static final String RE_REGISTRATION_FOCUSED = "/images/ReRegistrationFocused.png";
	public static final String VIEW_REPORTS_IMAGE = "/images/ViewReports.png";
	public static final String VIEW_REPORTS_FOCUSED = "/images/ViewReportsFocused.png";
	public static final String SUCCESS_IMG_PTH = "/images/success.png";
	public static final String FAILURE_IMG_PTH = "/images/failure.png";
	public static final String GET_ONBOARDED_IMG_PATH = "/images/GetOnboarded.png";
	public static final String GET_ONBOARDED_FOCUSED = "/images/GetOnboardedFocus.png";
	public static final String ONBOARDING_IMG_PATH = "/images/OnboardYourself.png";
	public static final String ONBOARDING_FOCUSED = "/images/OnboardYourselfFocus.png";
	public static final String REGISTERING_IMG_PATH = "/images/RegisterIndividual.png";
	public static final String REGISTERING_FOCUSED = "/images/RegisterIndividualFocus.png";
	public static final String UPDATE_BIOMETRICS_IMG_PATH = "/images/activeBiometricDetail.png";
	public static final String UPDATE_BIOMETRICS_FOCUSED = "/images/UpdateBiometricsFocus.png";

	// Authentication
	public static final String SUPERVISOR_FINGERPRINT_LOGIN = "Supervisior Fingerprint Authentication";
	public static final String SINGLE = "single";
	public static final String MULTIPLE = "multiple";
	public static final String OTP_VALIDATION_SUCCESS = "success";
	public static final String SUCCESS = "Success";
	public static final String FAILURE = "Fail";
	public static final String INFO = "INFO";
	public static final String SPLITTER = "#TYPE#";
	public static final String RESTART = "Restart";

	public static final String IRIS_STUB = "leftIris";
	public static final String FACE_STUB = "face";
	public static final String SUPERVISOR_AUTH = "supervisor";
	public static final String OFFICER_AUTH = "officer";

	// Authentication Methods
	public static final String PWORD = "PWD";
	public static final String OTP = "OTP";
	public static final String FACE = "FACE";
	public static final String FACE_FULLFACE = "FACE_FULL FACE";
	public static final String IRIS = "IRIS";

	public static final String FNR = "FNR";
	public static final String IRS = "IRS";

	public static final String LOGIN_OTP_PARAM = "otp";

	public static final String LANGUAGE_SUPPORT = "mosip.supported-languages";

	// Login
	public static final String BLOCKED = "BLOCKED";
	public static final String LOGIN_INITIAL_SCREEN = "initialMode";
	public static final String LOGIN_SEQUENCE = "sequence";
	public static final String ONBOARD_USER = "isOnboardUser";
	public static final String ONBOARD_USER_UPDATE = "updateOnboard";
	public static final String ISPAGE_NAVIGATION_ALERT_REQ = "pageNavigationAlert";
	public static final String USER_MACHINE_VALIDATION_MSG = "USER_MACHINE_VALIDATION_MSG";
	public static final String BLOCKED_USER_ERROR = "BLOCKED_USER_ERROR";
	public static final String ROLES_EMPTY_ERROR = "ROLES_EMPTY_ERROR";
	public static final String ROLES_LIST = "roleList";
	public static final String USER_NAME_VALIDATION = "USER_NAME_VALIDATION";

	// Generic
	public static final String ERROR = "ERROR";
	public static final int PARAM_ONE = 1;
	public static final int PARAM_ZERO = 0;
	public static final String PREVIOUS = "PREVIOUS";
	public static final String NEXT = "NEXT";
	public static final String UNKNOWN = "UNKNOWN";

	// UI Registration Validations
	public static final String ADDRESS_KEY = "PrevAddress";
	public static final String REGISTRATION_CONTROLLER = "REGISTRATION_CONTROLLER";
	public static final String DOCUMNET_SCAN_CONTROLLER = "DOCUMNET_SCAN_CONTROLLER";
	public static final String REGISTRATION_DATA = "registrationDTOContent";
	public static final String REGISTRATION_AGE_DATA = "ageDatePickerContent";
	public static final String REGISTRATION_PANE1_DATA = "demoGraphicPane1Content";
	public static final String REGISTRATION_PANE2_DATA = "demoGraphicPane2Content";
	public static final String REGISTRATION_ISEDIT = "isEditPage";
	public static final String IS_Child = "isChild";
	public static final String ENABLE = "Y";
	public static final String DISABLE = "N";
	public static final String VALIDATION_SPLITTER = "\\s,";
	public static final String ON_TYPE = "_ontype";
	public static final String POR_BOX = "porBox";
	public static final String VALIDATION_LOGGER = "VALIDATIONS";
	public static final String REG_LGN_001 = "REG_LGN_001";
	public static final String IS_BLOCKED_WORD = "IS_BLOCKED_WORD";

	public static final String REG_DDC_004 = "REG_DDC_004";
	public static final String AGE_FIELD = "ageField";
	public static final String MOBILE_NUMBER = "mobileNo";
	public static final String POSTAL_CODE = "postalCode";
	public static final String CNI_OR_PIN = "cniOrPinNumber";
	public static final String UIN_ID = "uinId";
	public static final String EMAIL_ID = "emailId";
	public static final String ONTYPE = "ontype";
	public static final String TOOLTIP = "toolTip";
	public static final String DOC_COMBO_BOX = "documentCombobox";
	public static final String BUTTONS_LABEL = "buttonsLabel";
	public static final String DEMOGRAPHIC_FIELD_LABEL = "demoGraphicFieldLabel";
	public static final String SCAN_VBOX = "scanVBox";
	public static final String DOC_TEXT_FIELD = "documentTextField";
	public static final String DOCUMENT_CONTENT_BUTTON = "documentContentButton";

	public static final String UIN_UPDATE_ISUINUPDATE = "isUINUpdate";
	public static final String LOCAL_LANGUAGE = "LocalLanguage";
	public static final String PRIMARY_LANGUAGE = "mosip.primary-language";
	public static final String SECONDARY_LANGUAGE = "mosip.secondary-language";
	public static final String LANGUAGE_ENGLISH = "english";
	public static final String LANGUAGE_ARABIC = "arabic";

	public static final String SIMPLE_TYPE = "simpleType";
	public static final String NUMBER = "number";
	public static final String STRING = "string";
	public static final String AGE_DATE = "ageDate";
	public static final String DROPDOWN = "dropdown";
	public static final String CHECKBOX = "checkbox";
	public static final String TEXTBOX = "textbox";
	public static final String BUTTON = "button";
	public static final String RESIDENCE_STATUS = "residenceStatus";
	public static final String PRIMARY = "primary";
	public static final String SECONDARY = "secondary";
	public static final String DemoGraphicFieldMessageLabel = "demoGraphicFieldMessageLabel";
	public static final String Parent = "Parent";
	public static final String updateUinCheckBox = "updateUinCheckBox";

	public static final String APPLICATION_LANGUAGE = "application_language";
	public static final String REGISTRATION_LOCAL_LANGUAGE = "local_language";
	//public static final String PACKET_TYPE_NEW = "New";
	public static final String REGISTRATION_DTO = "registrationDto";
	public static final String UIN_LENGTH = "mosip.kernel.uin.length";

	public static final String CHILD = "Child";
	public static final String ADULT = "Adult";
	public static final String AGE_DATEPICKER_CONTENT = "ageDatePickerContent";

	public static final String TOGGLE_BIO_METRIC_EXCEPTION = "toggleBiometricException";
	public static final String IS_LOW_QUALITY_BIOMETRICS = "Low Quality Biometrics";

	// Reasons for Exception
	public static final String MISSING_BIOMETRICS = "Missing biometrics";
	public static final String LOW_QUALITY_BIOMETRICS = "Low quality of biometrics";
	public static final String PERMANENT_EXCEPTION = "Permanent";
	public static final String TEMPORARY_EXCEPTION = "Temporary";

	// Lost UIN
	//public static final String PACKET_TYPE_LOST = "Lost";
	//public static final String LOST_UIN_CONFIG_FLAG = "mosip.registration.lost_uin_disable_flag";

	// update UIN
	public static final String UIN_LABEL = "UIN";
	public static final String FIRST_TOGGLE_LABEL = "toggleLabel1";
	public static final String SECOND_TOGGLE_LABEL = "toggleLabel2";
	//public static final String PACKET_TYPE_UPDATE = "Update";
	public static final String DOB_TOGGLE = "toggleAgeOrDob";
	public static final String UIN_UPDATE_CONFIG_FLAG = "mosip.registration.uin_update_config_flag";
	public static final String UIN_UPDATE_CONFIG_FIELDS_FROM_DB = "mosip.registration.uin.update.configured.fields";
	public static final String UIN_UPDATE_NAME = "name";
	public static final String UIN_UPDATE_AGE = "age";
	public static final String UIN_UPDATE_ADDRESS = "address";
	public static final String UIN_UPDATE_PHONE = "phone";
	public static final String UIN_UPDATE_EMAIL = "email";
	public static final String UIN_UPDATE_FOREIGNER = "foreigner";
	public static final String UIN_UPDATE_GENDER = "gender";
	public static final String UIN_UPDATE_PARENT_DETAILS = "parentOrGuardianDetails";
	public static final String UIN_UPDATE_DEMOGRAPHICDETAIL = "demographicDetail";
	public static final String UIN_UPDATE_DOCUMENTSCAN = "documentScan";
	public static final String UIN_UPDATE_FINGERPRINTCAPTURE = "fingerPrintCapture";
	public static final String UIN_UPDATE_BIOMETRICEXCEPTION = "biometricException";
	public static final String UIN_UPDATE_BIOMETRICS = "biometrics";
	public static final String UIN_UPDATE_FACECAPTURE = "faceCapture";
	public static final String UIN_UPDATE_IRISCAPTURE = "irisCapture";
	public static final String UIN_UPDATE_REGISTRATIONPREVIEW = "registrationPreview";
	public static final String UIN_UPDATE_OPERATORAUTHENTICATIONPANE = "operatorAuthenticationPane";
	public static final String UIN_UPDATE_UINUPDATENAVLBL = "uinUpdateNavLbl";
	public static final String LOSTUINLBL = "/lostuin";
	public static final String UIN_UPDATE_NAME_LBL = "Name";
	public static final String UIN_UPDATE_AGE_LBL = "Age/DOB";
	public static final String UIN_UPDATE_ADDRESS_LBL = "Address";
	public static final String UIN_UPDATE_PHONE_LBL = "Phone";
	public static final String UIN_UPDATE_EMAIL_LBL = "Email";
	public static final String UIN_UPDATE_FOREIGNER_LBL = "Foreigner/National";
	public static final String UIN_UPDATE_GENDER_LBL = "Gender";
	public static final String MALE_CODE = "MLE";
	public static final String FEMALE_CODE = "FLE";
	public static final String STREAMER_THREAD = "STREAMER_THREAD";

	public static final String UIN_UPDATE_PARENT_DETAILS_LBL = "Parent/Guardian details";
	public static final String UIN_UPDATE_CNIE_NUMBER_LBL = "CNIE/PIN/Residence Card Number";
	public static final String UIN_UPDATE_BIOMETRICS_LBL = "Biometrics";

	// onBoard User
	public static final String ONBOARD_STYLE_CLASS = "onboardAlertMsg";
	public static final String ONBOARD_IMG_PATH = "/images/tick.png";

	public static final String TICK_CIRICLE_IMG_PATH = "/images/tick-circle.png";
	public static final String EXCLAMATION_IMG_PATH = "/images/exclamation.png";
	public static final String ONBOARD_LAST_BIOMETRIC_UPDTAE_FORMAT = "EEEE dd MMM hh:mm a";
	public static final String UTC_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

	// RegistrationApproval
	public static final String PLACEHOLDER_LABEL = "PLACEHOLDER_LABEL";
	public static final String REGISTRATIONID = "registrationID";
	public static final String STATUSCODE = "statusCode";
	public static final String STATUSCOMMENT = "statusComment";
	public static final String ONHOLD_COMMENTS = "ONHOLD_COMMENTS";
	public static final String REJECTION_COMMENTS = "REJECT_COMMENTS";
	public static final String EMPTY = "";
	public static final String SPACE = " ";
	public static final String UNDER_SCORE = "_";
	public static final String PDF = "pdf";
	public static final String CONSTANTS_FILE_NAME = "/constants.properties";
	public static final String PENDING = "Pending";
	public static final String SUPERVISOR_APPROVAL_CONFIG_FLAG = "mosip.registration.supervisor_approval_config_flag";
	public static final String EOD_PROCESS_ID = "id";
	public static final String EOD_PROCESS_STATUSCOMMENT = "statusComment";
	public static final String EOD_PROCESS_ACKNOWLEDGEMENTFORMPATH = "acknowledgementFormPath";
	public static final String OPERATOR_ID = "operatorId";
	public static final String EOD_PROCESS_REGISTRATIONAPPROVALCONTROLLER = "RegistrationApprovalController";
	public static final String EXPORT_FILE_NAME = "PendingApprovalList";
	public static final String EXPORT_FILE_TYPE = ".csv";
	public static final String EOD_PROCESS_DATE_FORMAT = "dd-MM-yyyy";
	public static final String EOD_PROCESS_SLNO = "slno";
	public static final String EOD_PROCESS_DATE = "date";
	public static final String EOD_PROCESS_DATE_FORMAT_FOR_FILE = "yyyyMMddHHmmss";
	public static final String AUTH_ADVICE_FAILURE = "AUTH_ADVICE_FAILURE";

	// Packet Store Location Constants
	public static final String PACKET_STORE_LOCATION = "mosip.registration.registration_packet_store_location";
	public static final String PACKET_STORE_DATE_FORMAT = "mosip.registration.packet_store_date_format";

	// Packet Creation Constants
	public static final String ZIP_FILE_EXTENSION = ".zip";
	public static final String DEMOGRPAHIC_JSON_NAME = "ID.json";
	public static final String PACKET_META_JSON_NAME = "packet_meta_info.json";
	public static final String PACKET_DATA_HASH_FILE_NAME = "packet_data_hash.txt";
	public static final String PACKET_OSI_HASH_FILE_NAME = "packet_osi_hash.txt";
	public static final String PACKET_INTRODUCER_EXCEP_PHOTO_NAME = "_exception_photo.jpg";
	public static final String PACKET_INTRODUCER_EXCEP_PHOTO = "_exception_photo";
	public static final String AUDIT_JSON_FILE = "audit";
	public static final String JSON_FILE_EXTENSION = ".json";
	public static final String ACK_RECEIPT = "RegistrationAcknowledgement";
	public static final String APPLICANT_BIO_CBEFF_FILE_NAME = "applicant_bio_CBEFF.xml";
	public static final String OFFICER_BIO_CBEFF_FILE_NAME = "officer_bio_CBEFF.xml";
	public static final String SUPERVISOR_BIO_CBEFF_FILE_NAME = "supervisor_bio_CBEFF.xml";
	public static final String INTRODUCER_BIO_CBEFF_FILE_NAME = "introducer_bio_CBEFF.xml";
	public static final String AUTHENTICATION_BIO_CBEFF_FILE_NAME = "authentication_bio_CBEFF.xml";
	public static final String INDIVIDUAL = "INDIVIDUAL";
	public static final String INTRODUCER = "INTRODUCER";
	public static final String PARENT = "PARENT";
	public static final String CBEFF_BIR_UUIDS_MAP_NAME = "CBEFF_BIR_UUIDS";
	public static final String XML_FILE_FORMAT = ".xml";
	public static final String CBEFF_FILE_FORMAT = "cbeff";
	public static final String FACE_EXCEPTION = "ExceptionFace";
	public static final String IDENTITY_JSON_FILE_NAME = "mosip-identity-json-schema.json";
	public static final String CBEFF_SCHEMA_FILE_PATH = "/cbeff.xsd";
	public static final String CBEFF_DEFAULT_FORMAT_ORG = "Mosip";
	public static final String CBEFF_DEFAULT_FORMAT_TYPE = "257";
	public static final String CBEFF_DEFAULT_ALG_ORG = "HMAC";
	public static final String CBEFF_DEFAULT_ALG_TYPE = "SHA-256";
	public static final String CBEFF_FORMAT_ORG = "mosip.registration.cbeff_format_org";
	public static final String CBEFF_FORMAT_TYPE = "mosip.registration.cbeff_format_type";
	public static final String CBEFF_ALG_ORG = "mosip.registration.cbeff_algorithm_org";
	public static final String CBEFF_ALG_TYPE = "mosip.registration.cbeff_algorithm_type";
	public static final String IS_SUPERVISOR_AUTH_REQUIRED = "isSupervisorAuthRequired";
	public static final String IS_SUPERVISOR_BIOMETRICS_CAPTURED = "isSupervisorBiometricsCaptured";
	public static final String IS_OFFICER_BIOMETRICS_CAPTURED = "isOfficerBiometricsCaptured";

	// Validation Types
	public static final String VALIDATION_TYPE_FP = "Fingerprint";
	public static final String VALIDATION_TYPE_IRIS = "Iris";
	public static final String VALIDATION_TYPE_FACE = "Face";
	public static final String VALIDATION_TYPE_FP_SINGLE = "single";

	// Supervisor Authentication
	public static final String PWD_MATCH = "Username and Password Match";
	public static final String PWD_MISMATCH = "Username and Password Not Match";
	public static final String CREDS_NOT_FOUND = "Credentials Not Found";

	// RSA
	public static final String LOCALHOST = "localhost";

	// Constants for Registration Creation Zip

	/**
	 * Specifies the format for storing the Registration Acknowledgement
	 */
	public static final String ACKNOWLEDGEMENT_FORMAT = "html";

	public static final String IMAGE_FORMAT_PNG = "png";

	// Constants for Registration ID Generator - will be removed after Kernel
	// Integration
	public static final String DATE_FORMAT_REG = "dd-MM-yyyy";

	// Logger - Constants
	public static final String APPLICATION_ID = "REG";
	public static final String APPLICATION_NAME = "REGISTRATION";

	// Audit - Constants
	public static final String AUDIT_DEFAULT_USER = "NA";

	// OnlineConnectivity check
	public static final String URL = "http://localhost:8080/getTokenId";

	// ALert related constants
	public static final String ALERT_INFORMATION = "INFORMATION";
	public static final String ALERT_WARNING = "WARNING";
	public static final String ALERT = "ALERT";

	// api related constant values
	public static final String HTTPMETHOD = "service.httpmethod";
	public static final String SERVICE_URL = "service.url";
	public static final String HEADERS = "service.headers";
	public static final String RESPONSE_TYPE = "service.responseType";
	public static final String REQUEST_TYPE = "service.requestType";
	public static final String AUTH_HEADER = "service.authheader";
	public static final String AUTH_REQUIRED = "service.authrequired";
	public static final String SIGN_REQUIRED = "service.signrequired";
	public static final String AUTH_TYPE = "BASIC";
	public static final String REQUEST_SIGN_REQUIRED = "service.requestsignrequired";

	// OTP Related Details
	public static final String OTP_GENERATOR_SERVICE_NAME = "otp_generator";
	public static final String USERNAME_KEY = "key";
	public static final String OTP_GENERATED = "otp";
	public static final String OTP_VALIDATOR_SERVICE_NAME = "otp_validator";
	public static final String OTP_GENERATOR_RESPONSE_DTO = "otpGeneratorResponseDTO";
	public static final String OTP_VALIDATOR_RESPONSE_DTO = "otpValidatorResponseDTO";

	// Velocity Template Generator Constants
	public static final String TEMPLATE_ACKNOWLEDGEMENT = "AckReceipt";
	public static final String TEMPLATE_PREVIEW = "Preview";
	public static final String TEMPLATE_QRCODE_SOURCE = "QRCodeSource";
	public static final String TEMPLATE_UIN_UPDATE = "UINUpdate";
	public static final String TEMPLATE_HEADER_TABLE = "headerTable";
	public static final String TEMPLATE_UIN_HEADER_TABLE = "uinHeaderTable";
	public static final String TEMPLATE_UIN_LABEL = "UINLabel";
	public static final String TEMPLATE_UIN = "UIN";
	
	public static final String TEMPLATE_RID_LABEL = "RIDLabel";
	public static final String TEMPLATE_RID = "RID";
	public static final String TEMPLATE_APPLICATION_ID_LABEL = "ApplicationIDLabel";
	public static final String TEMPLATE_APPLICATION_ID = "ApplicationID";
	public static final String TEMPLATE_DATE_LABEL = "DateLabel";
	public static final String TEMPLATE_DATE = "Date";
	public static final String TEMPLATE_PRE_REG_ID_LABEL = "PreRegIDLabel";
	public static final String TEMPLATE_PRE_REG_ID = "PreRegID";
	public static final String TEMPLATE_PRE_REG = "IsPreRegIDPresent";
	public static final String TEMPLATE_DEMO_INFO = "DemographicInfo";
	public static final String DEMOGRAPHIC_TEXTFIELD_FOCUSED = "demoGraphicTextFieldFocused";
	public static final String DEMOGRAPHIC_TEXTFIELD = "demoGraphicTextField";
	public static final String TEMPLATE_DOCUMENTS_LABEL = "DocumentsLabel";
	public static final String TEMPLATE_DOCUMENTS = "Documents";
	public static final String TEMPLATE_BIOMETRICS_LABEL = "BiometricsLabel";
	public static final String TEMPLATE_BIOMETRICS_CAPTURED_LABEL = "BiometricsCapturedLabel";
	public static final String TEMPLATE_BIOMETRICS_CAPTURED = "Biometrics";
	public static final String TEMPLATE_EXCEPTION_PHOTO_LABEL = "ExceptionPhotoLabel";
	public static final String TEMPLATE_LEFT_EYE_LABEL = "LeftEyeLabel";
	public static final String TEMPLATE_RIGHT_EYE_LABEL = "RightEyeLabel";
	public static final String TEMPLATE_EXCEPTION_IMAGE_SOURCE = "ExceptionImageSource";
	public static final String TEMPLATE_LEFT_EYE = "LeftEye";
	public static final String TEMPLATE_EYE_IMAGE_SOURCE = "EyeImageSource";
	public static final String TEMPLATE_RIGHT_EYE = "RightEye";
	public static final String TEMPLATE_CAPTURED_LEFT_EYE = "CapturedLeftEye";
	public static final String TEMPLATE_CAPTURED_RIGHT_EYE = "CapturedRightEye";
	public static final String TEMPLATE_LEFT_PALM_LABEL = "LeftPalmLabel";
	public static final String TEMPLATE_RIGHT_PALM_LABEL = "RightPalmLabel";
	public static final String TEMPLATE_THUMBS_LABEL = "ThumbsLabel";
	public static final String TEMPLATE_LEFT_PALM_IMAGE_SOURCE = "LeftPalmImageSource";
	public static final String TEMPLATE_RIGHT_PALM_IMAGE_SOURCE = "RightPalmImageSource";
	public static final String TEMPLATE_THUMBS_IMAGE_SOURCE = "ThumbsImageSource";
	public static final String TEMPLATE_LEFT_LITTLE_FINGER = "leftLittle";
	public static final String TEMPLATE_LEFT_RING_FINGER = "leftRing";
	public static final String TEMPLATE_LEFT_MIDDLE_FINGER = "leftMiddle";
	public static final String TEMPLATE_LEFT_INDEX_FINGER = "leftIndex";
	public static final String TEMPLATE_LEFT_THUMB_FINGER = "leftThumb";
	public static final String TEMPLATE_RIGHT_LITTLE_FINGER = "rightLittle";
	public static final String TEMPLATE_RIGHT_RING_FINGER = "rightRing";
	public static final String TEMPLATE_RIGHT_MIDDLE_FINGER = "rightMiddle";
	public static final String TEMPLATE_RIGHT_INDEX_FINGER = "rightIndex";
	public static final String TEMPLATE_RIGHT_THUMB_FINGER = "rightThumb";
	public static final String TEMPLATE_CAPTURED_LEFT_SLAP = "CapturedLeftSlap";
	public static final String TEMPLATE_CAPTURED_RIGHT_SLAP = "CapturedRightSlap";
	public static final String TEMPLATE_CAPTURED_THUMBS = "CapturedThumbs";
	public static final String TEMPLATE_MISSING_LEFT_FINGERS = "MissingLeftFingers";
	public static final String TEMPLATE_LEFT_SLAP_EXCEPTION = "LeftSlapException";
	public static final String TEMPLATE_MISSING_RIGHT_FINGERS = "MissingRightFingers";
	public static final String TEMPLATE_RIGHT_SLAP_EXCEPTION = "RightSlapException";
	public static final String TEMPLATE_MISSING_THUMBS = "MissingThumbs";
	public static final String TEMPLATE_THUMBS_EXCEPTION = "ThumbsException";
	public static final String TEMPLATE_RO_IMAGE_SOURCE = "ROImageSource";
	public static final String TEMPLATE_RO_NAME_LABEL = "RONameLabel";
	public static final String TEMPLATE_RO_NAME = "ROName";
	public static final String TEMPLATE_REG_CENTER_LABEL = "RegCenterLabel";
	public static final String TEMPLATE_REG_CENTER = "RegCenter";
	public static final String TEMPLATE_PHOTO = "Photo";
	public static final String TEMPLATE_APPLICANT_IMAGE_SOURCE = "ApplicantImageSource";
	public static final String TEMPLATE_DATE_FORMAT = "dd/MM/yyyy hh:mm a";
	public static final String TEMPLATE_JPG_IMAGE_ENCODING = "data:image/jpg;base64,";
	public static final String TEMPLATE_PNG_IMAGE_ENCODING = "data:image/png;base64,";
	public static final String TEMPLATE_CROSS_MARK = "&#10008;";
	public static final String TEMPLATE_EYE_IMAGE_PATH = "/images/Eye.png";
	public static final String TEMPLATE_LEFT_SLAP_IMAGE_PATH = "/images/leftHand.png";
	public static final String TEMPLATE_RIGHT_SLAP_IMAGE_PATH = "/images/rightHand.png";
	public static final String TEMPLATE_THUMBS_IMAGE_PATH = "/images/thumbs.png";
	public static final String TEMPLATE_STYLE_HIDE_PROPERTY = "style='display:none;'";
	public static final String TEMPLATE_RIGHT_MARK = "&#10003;";
	public static final String TEMPLATE_FINGERPRINTS_CAPTURED = "FingerprintsCaptured";
	public static final String TEMPLATE_IMPORTANT_GUIDELINES = "ImportantGuidelines";
	public static final String TEMPLATE_NAME = "Acknowledgement Template";
	public static final String TEMPLATE_RESIDENT_NAME = "ResidentName";
	public static final String TEMPLATE_RO_IMAGE = "ROImage";
	public static final String TEMPLATE_MODIFY_IMAGE_PATH = "/images/Modify.png";
	public static final String TEMPLATE_MODIFY_IMAGE_SOURCE = "ModifyImageSource";
	public static final String TEMPLATE_MODIFY = "Modify";
	public static final String TEMPLATE_ENCODING = "UTF-8";
	public static final String TEMPLATE_FACE_CAPTURE_ENABLED = "FaceCaptureEnabled";
	public static final String TEMPLATE_DOCUMENTS_ENABLED = "DocumentsEnabled";
	public static final String TEMPLATE_BIOMETRICS_ENABLED = "BiometricsEnabled";
	public static final String TEMPLATE_IRIS_ENABLED = "IrisEnabled";
	public static final String TEMPLATE_IRIS_DISABLED = "IrisDisabled";
	public static final String TEMPLATE_LEFT_EYE_CAPTURED = "leftEyeCaptured";
	public static final String TEMPLATE_RIGHT_EYE_CAPTURED = "rightEyeCaptured";
	public static final String TEMPLATE_LEFT_SLAP_CAPTURED = "leftSlapCaptured";
	public static final String TEMPLATE_RIGHT_SLAP_CAPTURED = "rightSlapCaptured";
	public static final String TEMPLATE_THUMBS_CAPTURED = "thumbsCaptured";
	public static final String TEMPLATE_CONSENT_HEADING = "Consent";
	public static final String TEMPLATE_CONSENT_DATA = "ConsentData";
	public static final String TEMPLATE_CONSENT_YES = "Yes";
	public static final String TEMPLATE_CONSENT_NO = "No";
	public static final String TEMPLATE_CONSENT_SELECTED_YES = "SelectedYes";
	public static final String TEMPLATE_CONSENT_SELECTED_NO = "SelectedNo";
	public static final String TEMPLATE_CONSENT_CHECKED = "checked='checked'";
	public static final String PARENT_PHOTO_CAPTURED = "parentPhotoCaptured";
	public static final String PARENT_PHOTO_NOT_CAPTURED = "parentPhotoNotCaptured";
	public static final String PARENT_PHOTO = "ParentPhoto";
	public static final String PARENT_IMAGE_SOURCE = "ParentImageSource";
	public static final String LIST_ITEM_OPENING_TAG = "<li><span>";
	public static final String LIST_ITEM_CLOSING_TAG = "</span></li><br/>";
	public static final String SPLIT_DELIMITOR = "\\.";
	public static final String TEMPLATE_GUIDELINES = "Guidelines";
	public static final String TEMPLATE_IS_UIN_UPDATE = "IsUINUpdate";
	public static final String TEMPLATE_APPLICANT_NAME_LABEL = "NameLabel";
	public static final String TEMPLATE_APPLICANT_NAME_VALUE = "NameValue";

	public static final String MODIFY_DEMO_INFO = "modifyDemographicInfo";
	public static final String MODIFY_DOCUMENTS = "modifyDocuments";
	public static final String MODIFY_BIOMETRICS = "modifyBiometrics";
	public static final String CLICK = "click";
	public static final String REG_CONSENT = "mosip.registration.consent_";
	public static final String REG_CONSENT_YES = "consent-yes";
	public static final String REG_CONSENT_NO = "consent-no";
	public static final String IRIS_WITH_EXCEPTION = "irisWithException";
	public static final String IRIS_WITHOUT_EXCEPTION = "irisWithoutException";
	public static final String IRIS_WITH_EXCEPTION_STYLE = "parentIris2";
	public static final String IRIS_WITHOUT_EXCEPTION_STYLE = "parentIris1";
	public static final String IRIS_STYLE = "irisStyle";
	public static final String TEMPLATE_IRIS = "iris";
	public static final String TEMPLATE_CHILD_LEFT = "childLeft";
	public static final String TEMPLATE_CHILD_RIGHT = "childRight";
	public static final String TEMPLATE_CHILD_THUMBS = "childThumbs";
	public static final String PARENT_LEFT_SLAP = "parentLeftSlap";
	public static final String PARENT_RIGHT_SLAP = "parentRightSlap";
	public static final String PARENT_LEFT_EYE = "parentLeftEye";
	public static final String PARENT_RIGHT_EYE = "parentRightEye";
	public static final String PARENT_THUMBS = "parentThumbs";
	public static final String PARENT_STYLE = "parentStyle";
	public static final String TEMPLATE_IS_CHILD = "isChild";

	// Web Camera Constants
	public static final String WEB_CAMERA_IMAGE_TYPE = "jpg";
	public static final String APPLICANT_PHOTOGRAPH_NAME = "Applicant Photograph.jpg";
	public static final String EXCEPTION_PHOTOGRAPH_NAME = "Exception Photograph.jpg";
	public static final String APPLICANT_IMAGE = "Applicant Image";
	public static final String EXCEPTION_IMAGE = "Exception Image";
	public static final String GUARDIAN_IMAGE = "Guardian Image";
	public static final String APPLICANT_PHOTO_PANE = "applicantImagePane";
	public static final String EXCEPTION_PHOTO_PANE = "exceptionImagePane";
	public static final String WEB_CAMERA_PAGE_TITLE = "Applicant Biometrics";
	public static final String WEBCAM_NAME = "mosip.registration.webcam_name";
	public static final String WEBCAM_LIBRARY_NAME = "mosip.registration.webcam_library_name";
	public static final String PHOTO_CAPTUREPANES_SELECTED = "photoCapturePanesSelected";
	public static final String DEFAULT_EXCEPTION_IMAGE_PATH = "/images/ExceptionPhoto.png";
	public static final String IMAGE_PATH = "/images/Photo.png";

	public static final String WEBCAM_WIDTH = "mosip.camera.resolution.width";
	public static final String WEBCAM_HEIGHT = "mosip.camera.resolution.height";

	// Acknowledgement Form
	public static final String ACKNOWLEDGEMENT_FORM_TITLE = "Registration Acknowledgement";
	public static final String REGISTRATION = "Registration";

	// logos for new registration
	public static final String DEMOGRAPHIC_DETAILS_LOGO = "file:src/main/resources/images/Pre-Registration.png";
	public static final String APPLICANT_BIOMETRICS_LOGO = "file:src/main/resources/images/ApplicantBiometrics.png";
	public static final String OPERATOR_AUTHENTICATION_LOGO = "file:src/main/resources/images/OperatorAuthentication.png";

	// Exception Code for Components
	public static final String PACKET_CREATION_EXP_CODE = "PCC-";
	public static final String PACKET_UPLOAD_EXP_CODE = "PAU-";
	public static final String REG_ACK_EXP_CODE = "ACK-";
	public static final String DEVICE_ONBOARD_EXP_CODE = "DVO-";
	public static final String SYNC_JOB_EXP_CODE = "SYN-";
	public static final String USER_REG_IRIS_CAPTURE_EXP_CODE = "IRC-";
	public static final String USER_REG_FINGERPRINT_CAPTURE_EXP_CODE = "FPC-";
	public static final String USER_REGISTRATION_EXP_CODE = "REG-";
	public static final String USER_REG_SCAN_EXP_CODE = "SCN-";

	// USER CLIENT MACHINE MAPPING
	public static final String MACHINE_MAPPING_LOGGER_TITLE = "REGISTRATION - USER CLIENT MACHINE MAPPING";
	public static final String SYNC_TRANSACTION_DAO_LOGGER_TITLE = "REGISTRATION-SYNC-TRANSACTION DAO";
	public static final String SYNC_JOB_CONTROL_DAO_LOGGER_TITLE = "REGISTRATION-SYNC-JOB_CONTROL DAO";

	// CENTER MACHINE DEVICE MAPPING
	public static final String DEVICE_MAPPING_SUCCESS_CODE = "REG-DVO‌-001";
	public static final String DEVICE_MAPPING_ERROR_CODE = "REG-DVO‌-002";

	// Upload Packet

	public static final String UPLOAD_STATUS = "status";
	public static final List<String> PACKET_UPLOAD_STATUS = Arrays.asList("SYNCED", "EXPORTED", "RESEND", "E");
	
	public static final List<String> PACKET_JOBS = Arrays.asList("RPS_J00006", "RSJ_J00014", "PUJ_J00017");

	public static final String PACKET_UPLOAD = "packet_upload";
	public static final String DUPLICATE = "Duplicate";
	public static final String FOUND = "Found";
	public static final String PACKET_DUPLICATE = "duplicate";
	public static final String PACKET_UPLOAD_SNO = "slno";
	public static final String PACKET_UPLOAD_DATE = "createdTime";
	public static final String PACKET_UPLOAD_FILE = "fileName";
	public static final String PACKET_CLIENT_STATUS = "packetClientStatus";
	public static final String PACKET_SERVER_STATUS = "packetServerStatus";
	public static final String PACKET_OPERATOR_ID = "userId";
	public static final String UPLOAD_FILE_NAME = "PacketUploadList";
	// opt to register constants
	public static final String OPT_TO_REG_GEO_CAP_FREQ = "GEO_CAP_FREQ";
	public static final String ICS_CODE_ONE = "REG-ICS‌-001";
	public static final String ICS_CODE_TWO = "REG-ICS‌-002";
	public static final String ICS_CODE_THREE = "REG-ICS‌-003";
	public static final String ICS_CODE_FOUR = "REG-ICS‌-004";
	public static final String OPT_TO_REG_PAK_MAX_CNT_OFFLINE_FREQ = "REG_PAK_MAX_CNT_OFFLINE_FREQ";
	public static final double OPT_TO_REG_EARTH_RADIUS = 6371000;
	public static final double OPT_TO_REG_METER_CONVERSN = 1609.00;
	public static final String OPT_TO_REG_DIST_FRM_MACHN_TO_CENTER = "DIST_FRM_MACHN_TO_CENTER";
	public static final String ICS_CODE_FIVE = "REG-ICS‌-005";
	public static final String ICS_CODE_SIX = "REG-ICS‌-006";
	public static final String ICS_CODE_SEVEN = "REG-ICS‌-007";
	public static final String PAK_APPRVL_MAX_CNT = "REG-ICS‌-008";
	public static final String PAK_APPRVL_MAX_TIME = "REG-ICS‌-009";
	public static final String REG_REC_SEVEN = "REG-REC‌-007";
	public static final String OPT_TO_REG_LAST_CAPTURED_TIME = "lastCapturedTime";
	public static final String LATITUDE = "latitude";
	public static final String OPT_TO_REG_MDS_J00001 = "MDS_J00001";
	public static final String OPT_TO_REG_LCS_J00002 = "LCS_J00002";
	public static final String OPT_TO_REG_PDS_J00003 = "PDS_J00003";
	public static final String OPT_TO_REG_RSS_J00004 = "RSS_J00004";
	public static final String OPT_TO_REG_RCS_J00005 = "RCS_J00005";
	public static final String OPT_TO_REG_RPS_J00006 = "RPS_J00006";
	public static final String OPT_TO_REG_URS_J00007 = "URS_J00007";
	public static final String OPT_TO_REG_POS_J00008 = "POS_J00008";
	public static final String OPT_TO_REG_LER_J00009 = "LER_J00009";
	public static final String OPT_TO_REG_RDJ_J00010 = "RDJ_J00010";
	public static final String OPT_TO_REG_RDJ_J00011 = "RDJ_J00011";
	public static final String OPT_TO_REG_ADJ_J00012 = "ADJ_J00012";
	public static final String OPT_TO_REG_DEL_001 = "DEL_001";
	public static final String OPT_TO_REG_UDM_J00012 = "UDM_J00012";
	public static final String GEO_CAP_FREQ = "mosip.registration.geo.capture.frequency";
	public static final String DIST_FRM_MACHN_TO_CENTER = "mosip.registration.distance.from.machine.to.center";
	public static final String REG_PAK_MAX_CNT_OFFLINE_FREQ = "mosip.registration.packet.maximum.count.offline.frequency";
	public static final String MOSIP_REGISTRATION = "mosip.registration.";
	public static final String FREQUENCY = "frequency";
	public static final String SOFTWARE_UPDATE_SUCCESS_MSG = "SoftWareUpdate Flag Updated to Y!";
	public static final String SOFTWARE_UPDATE_FAILURE_MSG = "SoftWareUpdate Flag Updated to N!";
	public static final String SOFTWARE_UPDATE_MAX_CONFIGURED_FREQ = "mosip.registration.softwareUpdateCheck_configured_frequency";
	public static final String DSC_CODE_ONE = "REG-DSC‌-001";

	/** Packet Status Sync Constants */
	public static final String SYNC_PROGRESS_BAR_VALUE = "syncProgressVlaue";
	public static final String PACKET_STATUS_SYNC_RESPONSE_ENTITY = "registrations";
	public static final String PACKET_STATUS_SYNC_SERVICE_NAME = "packet_status";
	public static final String PACKET_EXTERNAL_STATUS_SYNC_SERVICE_NAME = "packet_external_status";
	public static final String PACKET_STATUS_READER_URL_PARAMETER = "request";
	public static final String RESPONSE = "response";
	public static final String REGISTRATION_ID = "registrationId";
	public static final String PACKET_ID = "packetId";
	public static final String STATUS_CODE = "statusCode";
	public static final String PACKET_STATUS_CODE_PROCESSED = "PROCESSED";
	public static final String PACKET_STATUS_CODE_ACCEPTED = "ACCEPTED";
	public static final String PACKET_STATUS_CODE_PROCESSING = "PROCESSING";
	public static final String PACKET_STATUS_CODE_REREGISTER = "REREGISTER";
	public static final String RE_REGISTER_STATUS_COMEMNTS = "Re-Register";
	public static final String PACKET_STATUS_CODE_REJECTED = "REJECTED";
	public static final String PACKET_STATUS_CODE_PROCESSED_2 = "Packet has reached Packet Receiver";
	public static final List<String> PACKET_STATUS_CODES_FOR_REMAPDELETE = Arrays.asList(PACKET_STATUS_CODE_REREGISTER,
			PACKET_STATUS_CODE_PROCESSING, PACKET_STATUS_CODE_PROCESSED, PACKET_STATUS_CODE_REJECTED, PACKET_STATUS_CODE_PROCESSED_2);
	public static final String MACHINE_CENTER_REMAP_FLAG = "mosip.registration.machinecenterchanged";
	public static final String MACHINE_CENTER_REMAP_MSG = "Machine Center Remap process started";
	public static final String PACKET_STATUS_READER_ID = "mosip.registration.status";
	public static final String PACKET_EXTERNAL_STATUS_READER_ID = "mosip.registration.packet.external.status";
	public static final String PACKET_SYNC_STATUS_ID = "mosip.registration.sync";
	public static final String PACKET_SYNC_VERSION = "1.0";

	public static final String BIOMETRIC_IMAGE = "Image";
	public static final String BIOMETRIC_TYPE = "Type";
	// Packet Upload
	public static final String PACKET_TYPE = "file";
	public static final String PACKET_STATUS_PRE_SYNC = "PRE_SYNC";
	public static final String PACKET_STATUS_SYNC_TYPE = "NEW";
	public static final String ACKNOWLEDGEMENT_FILE = "_Ack";
	public static final String ACKNOWLEDGEMENT_FILE_EXTENSION = "_Ack.html";
	public static final String PACKET_SYNC_ERROR = "Error";
	public static final String RE_REGISTRATION_STATUS = "REREGISTER";
	public static final String PACKET_SYNC_REF_ID = "packetsync";
	public static final String PACKET_UPLOAD_REF_ID = "packetUpload";

	// Device On-boarding
	public static final String MAC_ADDRESS = "macaddress";
	public static final String DONGLE_SERIAL_NUMBER = "dongleSerialNumber";
	public static final String ONBOARD_DEVICES_REF_ID_TYPE = "UserID";

	// Template Name
	public static final String ACKNOWLEDGEMENT_TEMPLATE = "Ack Template";
	public static final String ACKNOWLEDGEMENT_TEMPLATE_CODE = "reg-ack-template-part";
	public static final String PREVIEW_TEMPLATE_CODE = "reg-preview-template-part";
	public static final String DASHBOARD_TEMPLATE_CODE = "reg-dashboard-template";
	public static final String DASHBOARD_TEMPLATE = "Dashboard Template";

	// Notification Service
	public static final String EMAIL_SUBJECT = "MOSIP REGISTRATION NOTIFICATION";
	public static final String EMAIL_SERVICE = "email";
	public static final String SMS_SERVICE = "sms";
	public static final String NOTIFICATION_SERVICE = "REGISTRATION - NOTIFICATION SERVICE ";
	public static final String MODE_OF_COMMUNICATION = "mosip.notificationtype";

	// Global configuration parameters
	public static final String REGISTARTION_CENTER = "REGISTARTION_CENTER";
	public static final String GLOBAL_CONFIG_TRUE_VALUE = "Y";
	public static final String REG_PAK_MAX_CNT_APPRV_LIMIT = "mosip.registration.reg_pak_max_cnt_apprv_limit";
	public static final String REG_PAK_MAX_TIME_APPRV_LIMIT = "mosip.registration.reg_pak_max_time_apprv_limit";
	public static final String DEFAULT_HOST_IP = "mosip.registration.audit_default_host_ip";
	public static final String DEFAULT_HOST_NAME = "mosip.registration.audit_default_host_name";
	public static final String APP_NAME = "mosip.registration.audit_application_name";
	public static final String APP_ID = "mosip.registration.audit_application_id";
	public static final String REVIEWER_AUTH_CONFIG = "mosip.registration.reviewer_authentication_configuration";
	public static final String QUALITY_SCORE = "mosip.registration.quality_score";
	public static final String FINGERPRINT_AUTHENTICATION_THRESHHOLD = "mosip.fingerprint_authentication.quality_score";
	public static final String IRIS_AUTHENTICATION_THRESHHOLD = "mosip.iris_authentication.quality_score";
	public static final String CAPTURE_TIME_OUT = "mosip.registration.capture_time_out";
	public static final String FINGER_PRINT_SCORE = "mosip.registration.finger_print_score";
	public static final String INVALID_LOGIN_COUNT = "mosip.registration.invalid_login_count";
	public static final String INVALID_LOGIN_TIME = "mosip.registration.invalid_login_time";
	public static final String FACE_RECAPTURE_TIME = "mosip.registration.face_recapture_time";
	public static final String MIN_AGE = "mosip.kernel.applicant.type.age.limit";
	public static final String MAX_AGE = "mosip.registration.max_age";
	public static final String DOC_SIZE = "mosip.registration.document_size";
	public static final String DOC_TYPE = "mosip.registration.document_scanner_doctype";
	public static final String ACK_INSIDE_PACKET = "mosip.registration.save_ack_inside_packet";
	public static final String PKT_STORE_LOC = "mosip.registration.registration_packet_store_location";
	public static final String PKT_STORE_DATE_FORMAT = "mosip.registration.packet_store_date_format";
	public static final String MODE_OF_COMM = "mosip.registration.mode_of_communication";
	public static final String PWORD_LENGTH = "mosip.registration.username_pwd_length";
	public static final String DOC_DISABLE_FLAG = "mosip.registration.document_enable_flag";
	public static final String GPS_SERIAL_PORT_WINDOWS = "mosip.registration.gps_serial_port_windows";
	public static final String GPS_PORT_TIMEOUT = "mosip.registration.gps_port_timeout";
	public static final String GPS_PORT_LINUX = "mosip.registration.gps_serial_port_linux";
	public static final String DOC_SCAN_DPI = "mosip.registration.document_scanner_dpi";
	public static final String DOC_SCAN_CONTRAST = "mosip.registration.document_scanner_contrast";
	public static final String DOC_SCAN_BRIGHTNESS = "mosip.registration.document_scanner_brightness";
	public static final String DOC_SCANNER_ENABLED = "mosip.registration.document_scanner_enabled";
	public static final String SCANNER_IMG_TYPE = "jpg";
	public static final String IDENTITY_CLASS_NAME = "mosip.registration.identity_class_name";
	public static final String KEY_SPLITTER = "mosip.kernel.data-key-splitter";
	public static final String ASYMMETRIC_ALG_NAME = "RSA";
	public static final String CBEFF_UNQ_TAG = "mosip.registration.cbeff_only_unique_tags";
	public static final String REG_PKT_SIZE = "mosip.registration.max_reg_packet_size";
	public static final String LOGS_PATH = "mosip.registration.logs_path";
	public static final String DB_PATH = "mosip.registration.database_path";
	public static final String CLIENT_PATH = "mosip.registration.client_path";
	public static final String PRE_REG_DAYS_LIMIT = "mosip.registration.pre_reg_no_of_days_limit";
	public static final String GPS_DEVICE_MODEL = "mosip.registration.gps_device_model";
	public static final String GPS_DEVICE_DISABLE_FLAG = "mosip.registration.gps_device_enable_flag";
	public static final String PRE_REG_PACKET_LOCATION = "mosip.registration.registration_pre_reg_packet_location";
	public static final String IS_SOFTWARE_UPDATE_AVAILABLE = "mosip.registration.is_software_update_available";
	public static final String SOFTWARE_BACKUP_FOLDER = "mosip.registration.software_backup_folder";
	public static final String PROVIDER_NAME = "mosip.registration.provider_name";
	public static final String DISK_SPACE = "mosip.registration.disk_space_size";
	public static final String LAST_SOFTWARE_UPDATE = "mosip.registration.last_software_update";
	public static final String REGCLIENT_INSTALLED_TIME = "mosip.registration.regclient_installed_time";

	// Spring Batch-Jobs
	public static final String JOB_TRIGGER_STARTED = "Trigger started";
	public static final String JOB_TRIGGER_COMPLETED = "Trigger completed";
	public static final String JOB_EXECUTION_STARTED = "Execution started";
	public static final String JOB_EXECUTION_COMPLETED = "Execution completed";
	public static final String JOB_EXECUTION_SUCCESS = "Executed with success";
	public static final String JOB_EXECUTION_FAILURE = "Executed with failure";
	public static final String JOB_EXECUTION_SUCCESS_RESTART = "Executed with success, Restart";
	public static final String JOB_TRIGGER_MIS_FIRED = "Trigger Mis-Fired";
	public static final String JOB_EXECUTION_REJECTED = "Execution Rejected";
	public static final String RETRIEVED_PRE_REG_ID = "Retrieved Pre Registration";

	public static final String offlineJobs = "mosip.registration.jobs.offline";
	public static final String unTaggedJobs = "mosip.registration.jobs.unTagged";
	public static final String restartableJobs = "mosip.registration.jobs.restart";

	// Registration batch jobs scheduler : If ‘Y’ or ‘y’ means enabled, else
	// anything as value means disabled
	public static final String IS_REGISTRATION_JOBS_SCHEDULER_ENABLED = "mosip.registration.jobs.scheduler.enable";

	// public static final String offlineJobs =
	// "DEL_J00013,RDJ_J00010,ADJ_J00012,PVS_J00015";
	// public static final String unTaggedJobs ="PDS_J00003";
	// public static final String restartableJobs ="RCS_J00005";

	public static final String JOB_TRIGGER_POINT_SYSTEM = "System";
	public static final String JOB_TRIGGER_POINT_USER = "User";
	public static final String JOB_SYNC_TO_SERVER = "Server";
	public static final String JOB_DETAIL = "jobDetail";
	public static final String APPLICATION_CONTEXT = "applicationContext";
	public static final String SYNC_TRANSACTION = "syncTransaction";

	// GPS Device
	public static final String GPS_LOGGER = "GPS-Device-Information";
	public static final String LONGITUDE = "longitude";
	public static final String GPS_DISTANCE = "distance";
	public static final String GPS_CAPTURE_ERROR_MSG = "gpsErrorMessage";
	public static final String GPS_CAPTURE_SUCCESS = "gpsCaptureSuccess";
	public static final String GPS_CAPTURE_FAILURE = "gpsCaptureFailure";
	public static final String GPS_CAPTURE_FAILURE_MSG = "GPS signal is weak please capture again";
	public static final String GPS_CAPTURE_SUCCESS_MSG = "GPS signal Captured Sucessfullty";
	public static final String GPS_CAPTURE_PORT_FAILURE_MSG = "Please insert the GPS device in the Specified Port";
	public static final String GPS_DEVICE_CONNECTION_FAILURE = "Please connect the GPS Device";
	public static final String GPS_DEVICE_CONNECTION_FAILURE_ERRO_MSG = "GPS device not found. Please connect an on-boarded GPS device.";
	public static final String GPS_REG_LGE‌_002 = "REG-LGE‌-002";
	public static final String GPS_SERIAL_PORT = "COM4";
	public static final String GPS_ERROR_CODE = "errorCode";
	public static final String GPS_CAPTURING_EXCEPTION = "GPS_EXCEPTION";
	public static final String GPS_SIGNAL = "$GP";

	// Documents
	/*public static final String POA_DOCUMENT = "POA";
	public static final String POI_DOCUMENT = "POI";
	public static final String POR_DOCUMENT = "POR";
	public static final String DOB_DOCUMENT = "POB";*/
	public static final String POE_DOCUMENT = "POE";
	public static final String SERVER_STATUS_RESEND = "RESEND";
	public static final String CLIENT_STATUS_APPROVED = "APPROVED";
	public static final List<String> CLIENT_STATUS_TO_BE_SYNCED = Arrays.asList("APPROVED", "REJECTED");
	public static final String SYNCED_STATUS = "SYNCED";

	public static final List<String> PACKET_STATUS = Arrays.asList("APPROVED", "REJECTED", "RE_REGISTER_APPROVED");

	public static final List<String> PACKET_STATUS_UPLOAD = Arrays.asList("APPROVED", "REJECTED", "SYNCED", "EXPORTED");

	public static final List<String> PACKET_EXPORT_STATUS = Arrays.asList("APPROVED", "EXPORTED", "SYNCED");
	
	public static final List<String> PACKET_PROCESSED_STATUS = Arrays.asList("PROCESSED", "ACCEPTED");
	
	public static final List<String> PACKET_REJECTED_STATUS = Arrays.asList("REREGISTER", "REJECTED");

	// Pre Registration
	public static final String PRE_REGISTRATION_ID = "pre_registration_id";
	public static final String GET_PRE_REGISTRATION_IDS = "get_pre_registration_Ids";
	public static final String GET_PRE_REGISTRATION = "get_pre_registration";
	public static final String REGISTRATION_CLIENT_ID = "10";
	public static final String PRE_REGISTRATION_DUMMY_ID = "mosip.pre-registration.datasync.fetch.ids";
	public static final String VER = "1.0";
	public static final String PRE_REG_TO_GET_ID_ERROR = "PRE_REG_TO_GET_ID_ERROR";
	public static final String PRE_REG_TO_GET_PACKET_ERROR = "PRE_REG_TO_GET_PACKET_ERROR";
	public static final String PRE_REG_PACKET_NETWORK_ERROR = "PRE_REG_PACKET_NETWORK_ERROR";
	public static final String PRE_REG_SUCCESS_MESSAGE = "PRE_REG_SUCCESS_MESSAGE";
	public static final String IS_PRE_REG_SYNC = "PreRegSync";
	public static final String PRE_REG_FILE_NAME = "fileName";
	public static final String PRE_REG_FILE_CONTENT = "fileContent";
	public static final String PRE_REG_APPOINMENT_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";

	// UI Date Format
	public static final String DATE_FORMAT = "MM/dd/yyy hh:mm:ss";
	public static final String HH_MM_SS = "HH:mm:ss";

	// Biometric Exception style
	public static final String ADD_BORDER = "addBorderStyle";
	public static final String REMOVE_BORDER = "removeBorderStyle";
	public static final String OLD_BIOMETRIC_EXCEPTION = "oldBiometric";
	public static final String NEW_BIOMETRIC_EXCEPTION = "newBiometric";

	// Iris & Fingerprint Capture for Individual Registration
	public static final String IRIS_THRESHOLD = "mosip.registration.iris_threshold";
	public static final String FACE_THRESHOLD = "mosip.registration.face_threshold";
	public static final String IMAGE_FORMAT_KEY = "imageFormat";
	public static final String IMAGE_BYTE_ARRAY_KEY = "imageBytes";
	public static final String IMAGE_BYTE_ISO = "byteIso";
	public static final String IMAGE_SCORE_KEY = "imageScore";
	public static final String LEFT = "Left";
	public static final String RIGHT = "Right";
	public static final String EYE = "Eye";
	public static final String DOT = ".";
	public static final String FINGER = "finger";
	public static final String HAND = "Hand";
	public static final String IRIS_LOWERCASE = "Iris";
	public static final String FINGERPRINT = "Fingerprint";
	public static final String FINGERPRINT_UPPERCASE = "FINGERPRINT";
	public static final String LEFTPALM = "leftSlap";
	public static final String RIGHTPALM = "rightSlap";
	public static final String THUMBS = "thumbs";
	public static final String PERCENTAGE = "%";
	public static final String ISO_FILE_NAME = "ISOTemplate";
	public static final String ISO_IMAGE_FILE_NAME = "ISOImage";
	public static final String ISO_FILE = "ISOTemplate.iso";
	public static final String DUPLICATE_FINGER = "DuplicateFinger";
	public static final String DUPLICATE_IRIS = "DuplicateIris";
	public static final String ISO_IMAGE_FILE = "ISOImage.iso";
	public static final String LEFTHAND_SLAP_FINGERPRINT_PATH = "/fingerprints/leftSlap.jpg";
	public static final String FACE_ISO = "/images/face.iso";
	public static final String LEFT_EYE_ISO = "/images/leftEye.iso";
	public static final String RIGHT_EYE_ISO = "/images/rightEye.iso";
	public static final String RIGHTHAND_SLAP_FINGERPRINT_PATH = "/fingerprints/rightSlap.jpg";
	public static final String BOTH_THUMBS_FINGERPRINT_PATH = "/fingerprints/thumbs.jpg";
	public static final String LEFTSLAP_FINGERPRINT_THRESHOLD = "mosip.registration.leftslap_fingerprint_threshold";
	public static final String RIGHTSLAP_FINGERPRINT_THRESHOLD = "mosip.registration.rightslap_fingerprint_threshold";
	public static final String THUMBS_FINGERPRINT_THRESHOLD = "mosip.registration.thumbs_fingerprint_threshold";
	public static final String FINGERPRINT_RETRIES_COUNT = "mosip.registration.num_of_fingerprint_retries";
	public static final String IRIS_RETRY_COUNT = "mosip.registration.num_of_iris_retries";
	public static final String FACE_RETRY_COUNT = "mosip.registration.num_of_face_retries";
	public static final String PHOTO_RETRY_COUNT = "mosip.registration.num_of_photo_retries";

	/*public static final String[] LEFTHAND_SEGMNTD_FILE_PATHS = new String[] { "/fingerprints/lefthand/leftIndex/",
			"/fingerprints/lefthand/leftLittle/", "/fingerprints/lefthand/leftMiddle/",
			"/fingerprints/lefthand/leftRing/" };
	public static final String[] RIGHTHAND_SEGMNTD_DUPLICATE_FILE_PATHS = new String[] {
			"/fingerprints/righthand/rightIndex/", "/fingerprints/righthand/rightLittle/",
			"/fingerprints/righthand/rightMiddle/", "/fingerprints/righthand/rightRing/" };
	public static final String[] RIGHTHAND_SEGMNTD_FILE_PATHS = new String[] { "/fingerprints/Srighthand/rightIndex/",
			"/fingerprints/Srighthand/rightLittle/", "/fingerprints/Srighthand/rightMiddle/",
			"/fingerprints/Srighthand/rightRing/" };
	public static final String[] THUMBS_SEGMNTD_FILE_PATHS = new String[] { "/fingerprints/thumb/leftThumb/",
			"/fingerprints/thumb/rightThumb/" };*/
	public static final String THUMB = "Thumb";
	public static final String LEFT_HAND = "Left Hand ";
	public static final String RIGHT_HAND = "Right Hand ";
	public static final String RIGHT_IRIS = "Right Iris ";
	public static final String LEFT_IRIS = "Left Iris ";
	/*public static final String[] LEFTHAND_SEGMNTD_FILE_PATHS_USERONBOARD = new String[] {
			"/UserOnboard/leftHand/leftIndex/", "/UserOnboard/leftHand/leftLittle/",
			"/UserOnboard/leftHand/leftMiddle/", "/UserOnboard/leftHand/leftRing/" };
	public static final String[] RIGHTHAND_SEGMNTD_FILE_PATHS_USERONBOARD = new String[] {
			"/UserOnboard/rightHand/rightIndex/", "/UserOnboard/rightHand/rightLittle/",
			"/UserOnboard/rightHand/rightMiddle/", "/UserOnboard/rightHand/rightRing/" };
	public static final String[] THUMBS_SEGMNTD_FILE_PATHS_USERONBOARD = new String[] { "/UserOnboard/thumb/leftThumb/",
			"/UserOnboard/thumb/rightThumb/" };*/
	public static final String COMMA = ",";
	public static final String HYPHEN = "-";
	public static final String FINGERPRINT_PANES_SELECTED = "fingerPrintPanesSelected";
	public static final String BIOMETRIC_PANES_SELECTED = "biometricPaneSelected";
	public static final Set<String> BIO_TYPE = new HashSet<>(
			Arrays.asList(RegistrationConstants.HAND, RegistrationConstants.THUMB));
	public static final String LEFTSLAPCOUNT = "leftSlapCount";
	public static final String RIGHTSLAPCOUNT = "rightSlapCount";
	public static final String THUMBCOUNT = "thumbCount";
	public static final String EXCEPTIONCOUNT = "exceptionCount";

	/** Exception codes **/
	private static final String REG_SERVICE_CODE = "REG-SER-";

	public static final String REG_FRAMEWORK_PACKET_HANDLING_EXCEPTION = PACKET_CREATION_EXP_CODE + "PHS-002";
	public static final String PACKET_ZIP_CREATION = REG_SERVICE_CODE + "ZCM-203";
	public static final String ENCRYPTED_PACKET_STORAGE = REG_SERVICE_CODE + "STM-211";
	public static final String LOGIN_SERVICE = REG_SERVICE_CODE + "IPD-214";
	public static final String SERVICE_DELEGATE_UTIL = REG_SERVICE_CODE + "IPD-215";
	public static final String SERVICE_DATA_PROVIDER_UTIL = REG_SERVICE_CODE + "DPU-216";
	public static final String UPDATE_SYNC_AUDIT = REG_SERVICE_CODE + "ADI-220";
	public static final String READ_PROPERTY_FILE_ERROR = REG_SERVICE_CODE + "PFR-222";
	public static final String PACKET_UPDATE_STATUS = REG_SERVICE_CODE + "UPS-217";
	public static final String PACKET_RETRIVE_STATUS = REG_SERVICE_CODE + "RPS-218";
	public static final String MACHINE_MAPPING_RUN_TIME_EXCEPTION = REG_SERVICE_CODE + "RDI-219";
	public static final String MACHINE_MAPPING_STATIONID_RUN_TIME_EXCEPTION = REG_SERVICE_CODE + "UMM-220";
	public static final String SYNC_STATUS_VALIDATE = REG_SERVICE_CODE + "SSV-223";
	public static final String MACHINE_MASTER_RECORD_NOT_FOUND = REG_SERVICE_CODE + "MMD-224";

	// #Exceptions SyncJobs
	public static final String SYNC_TRANSACTION_RUNTIME_EXCEPTION = REG_SERVICE_CODE + "RPS-BTM-226";
	public static final String SYNC_JOB_RUN_TIME_EXCEPTION = REG_SERVICE_CODE + "RPS-JTD-227";
	public static final String PACKET_SYNC__STATUS_READER_NULL_POINTER_EXCEPTION = REG_SERVICE_CODE + "RPS-PSJ-228";
	public static final String BASE_JOB_NO_SUCH_BEAN_DEFINITION_EXCEPTION = REG_SERVICE_CODE + "RPS-BJ-229";
	public static final String BASE_JOB_NULL_POINTER_EXCEPTION = REG_SERVICE_CODE + "RPS-BJ-229";

	// Device Onboarding Service
	private static final String DEVICE_ONBOARDING_SERVICE = REG_SERVICE_CODE + "DVO-";
	public static final String UPDATE_DEVICE_MAPPING_EXCEPTION = DEVICE_ONBOARDING_SERVICE + "MMS-232";

	public static final String PACKET_RETRIVE_STATUS_EXCEPTION = REG_SERVICE_CODE + "PRS - 233";
	public static final String PACKET_UPDATE_STATUS_EXCEPTION = REG_SERVICE_CODE + "PUS - 234";

	// Exceptions
	private static final String REG_UI_CODE = "REG-UI";

	public static final String REG_UI_LOGIN_LOADER_EXCEPTION = REG_UI_CODE + "RAI-001";
	public static final String REG_UI_LOGIN_SCREEN_LOADER_EXCEPTION = REG_UI_CODE + "LC-002";
	public static final String REG_UI_HOMEPAGE_LOADER_EXCEPTION = REG_UI_CODE + "ROC-003";
	public static final String REG_UI_BASE_CNTRLR_IO_EXCEPTION = REG_UI_CODE + "BAS-004";
	public static final String REG_UI_VIEW_ACK_FORM_IO_EXCEPTION = REG_UI_CODE + "VAF-005";

	// Exceptions for User Registration - Iris & FingerPrint Capture
	public static final String USER_REG_IRIS_CAPTURE_PAGE_LOAD_EXP = USER_REG_IRIS_CAPTURE_EXP_CODE + "ICC-001";
	public static final String USER_REG_IRIS_CAPTURE_NEXT_SECTION_LOAD_EXP = USER_REG_IRIS_CAPTURE_EXP_CODE + "ICC-002";
	public static final String USER_REG_IRIS_CAPTURE_PREV_SECTION_LOAD_EXP = USER_REG_IRIS_CAPTURE_EXP_CODE + "ICC-003";
	public static final String USER_REG_IRIS_CAPTURE_POPUP_LOAD_EXP = USER_REG_IRIS_CAPTURE_EXP_CODE + "ICC-004";
	public static final String USER_REG_IRIS_VALIDATION_EXP = USER_REG_IRIS_CAPTURE_EXP_CODE + "ICC-005";
	public static final String USER_REG_IRIS_SCORE_VALIDATION_EXP = USER_REG_IRIS_CAPTURE_EXP_CODE + "ICC-006";
	public static final String USER_REG_IRIS_SCAN_EXP = USER_REG_IRIS_CAPTURE_EXP_CODE + "IFC-001";
	public static final String USER_REG_FINGERPRINT_SCAN_EXP = USER_REG_FINGERPRINT_CAPTURE_EXP_CODE + "FSC-003";
	public static final String USER_REG_FINGERPRINT_PAGE_LOAD_EXP = USER_REG_FINGERPRINT_CAPTURE_EXP_CODE + "FCC-001";
	public static final String USER_REG_FINGERPRINT_CAPTURE_NEXT_SECTION_LOAD_EXP = USER_REG_FINGERPRINT_CAPTURE_EXP_CODE
			+ "FCC-002";
	public static final String USER_REG_FINGERPRINT_CAPTURE_PREV_SECTION_LOAD_EXP = USER_REG_FINGERPRINT_CAPTURE_EXP_CODE
			+ "FCC-003";
	public static final String USER_REG_FINGERPRINT_CAPTURE_POPUP_LOAD_EXP = USER_REG_FINGERPRINT_CAPTURE_EXP_CODE
			+ "FCC-004";
	public static final String USER_REG_FINGERPRINT_VALIDATION_EXP = USER_REG_FINGERPRINT_CAPTURE_EXP_CODE + "FCC-005";
	public static final String USER_REG_FINGERPRINT_SCORE_VALIDATION_EXP = USER_REG_FINGERPRINT_CAPTURE_EXP_CODE
			+ "FCC-006";
	public static final String USER_REG_IRIS_SAVE_EXP = USER_REG_IRIS_CAPTURE_EXP_CODE + "ICC-008";
	public static final String USER_REG_GET_IRIS_QUALITY_SCORE_EXP = USER_REG_IRIS_CAPTURE_EXP_CODE + "ICC-009";
	public static final String USER_REG_IRIS_STUB_IMAGE_EXP = USER_REG_IRIS_CAPTURE_EXP_CODE + "IFC-002";

	// Exception for Registration - Document Scan and Upload
	public static final String USER_REG_DOC_SCAN_UPLOAD_EXP = USER_REGISTRATION_EXP_CODE + "SCN-001";

	// Scan
	public static final String USER_REG_SCAN_EXP = USER_REG_SCAN_EXP_CODE + "DOC-001";

	// Regex Constants
	//public static final String FULL_NAME_REGEX = "([A-z]+\\s?\\.?)+";
	//public static final int FULL_NAME_LENGTH = 50;
	//public static final String ADDRESS_LINE1_REGEX = "^.{1,50}$";
	public static final String NUMBER_REGEX = "\\d+";
	//public static final String NUMBER_OR_NOTHING_REGEX = "^\\d*$";
	public static final String FOUR_NUMBER_REGEX = "\\d{4}";
	public static final String NUMBER_REGEX_ZERO_TO_THREE = "\\d{0,3}";
	//public static final int MOBILE_NUMBER_LENGTH = 9;
	//public static final String EMAIL_ID_REGEX = "^([\\w\\-\\.]+)@((\\[([0-9]{1,3}\\.){3}[0-9]{1,3}\\])|(([\\w\\-]+\\.)+)([a-zA-Z]{2,4}))$";
	/*public static final String EMAIL_ID_REGEX_INITIAL = "([a-zA-Z]+\\.?\\-?\\@?(\\d+)?)+";
	public static final String CNI_OR_PIN_NUMBER_REGEX = "\\d{0,30}";
	public static final String AGE_REGEX = "\\d{1,2}";
	public static final String UIN_REGEX = "\\d{1,30}";
	public static final String POSTAL_CODE_REGEX = "\\d{5}";
	public static final String POSTAL_CODE_REGEX_INITIAL = "\\d{1,5}";
	*/
	public static final String REGEX_ANY = ".*";
	public static final String ONE = "1";
	public static final String BIOMETRIC_SEPERATOR = "(?<=\\.)(.*)(?=\\.)";

	// master sync
	public static final String MASTER_SYNC_SUCESS_MSG_CODE = "REG-MDS‌-001";
	public static final String MASTER_SYNC_OFFLINE_FAILURE_MSG_CODE = "REG-MDS‌-002";
	public static final String MASTER_SYNC_FAILURE_MSG_CODE = "REG-MDS‌-003";
	public static final String MASTER_SYNC_FAILURE_MSG_INFO = "Error in sync";
	public static final String MASTER_SYNC_FAILURE_MSG = "SYNC_FAILURE";
	public static final String MASTER_SYNC_OFFLINE_FAILURE_MSG_INFO = "Client not online";
	public static final String MASTER_SYNC_OFFLINE_FAILURE_MSG = "PRE_REG_PACKET_NETWORK_ERROR";
	public static final String MASTER_SYNC_EXCEPTION = "MASTER_SYNC_EXCEPTION";
	public static final String MASTER_SYNC_JOD_DETAILS = "MASTER_SYNC_JOB_DETAILS";
	public static final String MASTER_SYNC_SUCCESS = "Sync successful";
	public static final String MASTER_SYNC = "MASTER_SYNC";
	public static final String NO_INTERNET = "NO_INTERNET";
	public static final String MASTER_VALIDATOR_SERVICE_NAME = "master_sync";
	public static final String MASTER_SYNC_SUCCESS_MESSAGE = "MASTER_SYNC_SUCCESS";
	public static final String MASTER_SYNC_ERROR_MESSAGE = "MASTER_SYNC_ERROR";
	// public static final String MASTER_CENTER_REMAP_SERVICE_NAME =
	// "center_remap_sync";
	public static final String MASTER_CENTER_PARAM = "regcenterId";
	public static final String MASTER_DATA_LASTUPDTAE = "lastUpdated";
	public static final String MASTER_SYNC_LOGGER_INFO = "Entering into Master Sync Dao Impl...";
	// POLICY SYNC
	public static final String REG_APP_ID = "REGISTRATION";
	public static final String KERNEL_APP_ID = "KERNEL";
	// public static final String KERNEL_REF_ID = "SIGNATUREKEY";
	public static final String POLICY_SYNC_SUCCESS_CODE = "REG-MDS‌-001 ";
	public static final String POLICY_SYNC_SUCCESS_MESSAGE = "SYNC_SUCCESS";
	public static final String POLICY_SYNC_ERROR_CODE = "REG-MDS‌-003 ";
	public static final String POLICY_SYNC_ERROR_MESSAGE = "SYNC_FAILURE";
	public static final String POLICY_SYNC_CLIENT_NOT_ONLINE_ERROR_CODE = "REG-MDS‌-002";
	public static final String POLICY_SYNC_CLIENT_NOT_ONLINE_ERROR_MESSAGE = "POLICY_SYNC_CLIENT_NOT_ONLINE_ERROR_MESSAGE";

	public static final String SYNCH_CONFIG_DATA_JOB_TITLE = "synch config data job";
	public static final String REG_USER_MAPPING_SYNC_JOB_TITLE = "registration user mapping sync job";

	// PRE-REG DELETE JOB
	public static final String PRE_REG_DELETE_SUCCESS = "PRE_REG_DELETE_SUCCESS";
	public static final String PRE_REG_DELETE_FAILURE = "PRE_REG_DELETE_FAILURE";

	// Connection Error
	public static final String CONNECTION_ERROR = "CONNECTION_ERROR";

	// Exceptions - Template Generator
	public static final String TEMPLATE_GENERATOR_ACK_RECEIPT_EXCEPTION = PACKET_CREATION_EXP_CODE + "TGE-002";
	public static final String TEMPLATE_GENERATOR_SMS_EXCEPTION = PACKET_CREATION_EXP_CODE + "TGE-002";

	// Jobs
	public static final String BATCH_JOB_START_SUCCESS_MESSAGE = "BATCH_JOB_START_SUCCESS_MESSAGE";
	public static final String START_SCHEDULER_ERROR_MESSAGE = "START_SCHEDULER_ERROR_MESSAGE";
	public static final String BATCH_JOB_STOP_SUCCESS_MESSAGE = "BATCH_JOB_STOP_SUCCESS_MESSAGE";
	public static final String STOP_SCHEDULER_ERROR_MESSAGE = "STOP_SCHEDULER_ERROR_MESSAGE";
	public static final String CURRENT_JOB_DETAILS_ERROR_MESSAGE = "CURRENT_JOB_DETAILS_ERROR_MESSAGE";
	public static final String EXECUTE_JOB_ERROR_MESSAGE = "EXECUTE_JOB_ERROR_MESSAGE";
	public static final String SYNC_DATA_PROCESS_ALREADY_STARTED = "SYNC_DATA_PROCESS_ALREADY_STARTED";
	public static final String SYNC_DATA_PROCESS_ALREADY_STOPPED = "SYNC_DATA_PROCESS_ALREADY_STOPPED";
	public static final String SYNC_DATA_DTO = "SYNC-DATA DTO";
	public static final String JOB_RUNNING = "RUNNING";
	public static final String JOB_COMPLETED = "COMPLETED";
	public static final String NO_JOB_COMPLETED = "NO_JOB_COMPLETED";
	public static final String NO_JOBS_TRANSACTION = "NO_JOBS_TRANSACTION";
	public static final String NO_JOBS_RUNNING = "NO_JOBS_RUNNING";
	public static final String JOB_UNKNOWN = "UNKNOWN";

	// PACKET
	public static final String PACKET_STATUS_SYNC_ERROR_RESPONSE = "PACKET_STATUS_SYNC_ERROR_RESPONSE";
	public static final String PACKET_STATUS_SYNC_SUCCESS_MESSAGE = "PACKET_STATUS_SYNC_SUCCESS_MESSAGE";
	public static final String PACKET_CREATION_DISK_SPACE_CHECK = "PACKET_CREATION_DISK_SPACE_CHECK";

	// OTP
	public static final String OTP_GENERATION_SUCCESS_MESSAGE = "OTP_GENERATION_SUCCESS_MESSAGE";
	public static final String OTP_GENERATION_ERROR_MESSAGE = "OTP_GENERATION_ERROR_MESSAGE";
	public static final String OTP_VALIDATION_ERROR_MESSAGE = "OTP_VALIDATION_ERROR_MESSAGE";

	// Packet Export
	public static final String FILE_EXPLORER_NAME = "File Explorer";

	// Sync Status
	public static final String REG_PKT_APPRVL_CNT_EXCEED = "REG_PKT_APPRVL_CNT_EXCEED";
	public static final String REG_PKT_APPRVL_TIME_EXCEED = "REG_PKT_APPRVL_TIME_EXCEED";
	public static final String OPT_TO_REG_TIME_EXPORT_EXCEED = "OPT_TO_REG_TIME_EXPORT_EXCEED";
	public static final String OPT_TO_REG_TIME_SYNC_EXCEED = "OPT_TO_REG_TIME_SYNC_EXCEED";
	public static final String OPT_TO_REG_REACH_MAX_LIMIT = "OPT_TO_REG_REACH_MAX_LIMIT";
	public static final String OPT_TO_REG_OUTSIDE_LOCATION = "OPT_TO_REG_OUTSIDE_LOCATION";
	public static final String OPT_TO_REG_WEAK_GPS = "OPT_TO_REG_WEAK_GPS";
	public static final String OPT_TO_REG_INSERT_GPS = "OPT_TO_REG_INSERT_GPS";
	public static final String OPT_TO_REG_GPS_PORT_MISMATCH = "OPT_TO_REG_GPS_PORT_MISMATCH";
	public static final String OPT_TO_REG_LAST_SOFTWAREUPDATE_CHECK = "OPT_TO_REG_LAST_SOFTWAREUPDATE_CHECK";

	public static final String POLICY_SYNC_SERVICE = "policysync";
	public static final String KEY_NAME = "mosip.registration.key_policy_sync_threshold_value";
	public static final String OPT_TO_REG_LAST_EXPORT_REG_PKTS_TIME = "mosip.registration.last_export_registration_config_time";

	// Reg Deletion
	public static final String REGISTRATION_DELETION_BATCH_JOBS_SUCCESS = "REGISTRATION_DELETION_BATCH_JOBS_SUCCESS";
	public static final String REGISTRATION_DELETION_BATCH_JOBS_FAILURE = "REGISTRATION_DELETION_BATCH_JOBS_FAILURE";

	// Application Language
	public static final String APPLICATION_LANUAGE = "eng";

	// Global-Config Constants
	public static final String GET_GLOBAL_CONFIG = "get_registration_center_config";
	public static final String REGISTRATION_CENTER_ID = "registrationcenterid";
	public static final String GLOBAL_CONFIG_ERROR_MSG = "please synch the data before starting the application";

	// user on boarding
	public static final String USER_ON_BOARDING_ERROR_RESPONSE = "USER_ONBOARD_ERROR";
	public static final String USER_ON_BOARDING_EXCEPTION = "USER_ON_BOARDING_EXCEPTION";
	public static final String USER_ON_BOARDING_EXCEPTION_MSG_CODE = "REG-URE‌-000";
	public static final String USER_ON_BOARDING_SUCCESS_CODE = "REG-URE‌-002";
	public static final String USER_ON_BOARDING_SUCCESS_MSG = "USER_ONBOARD_SUCCESS";
	public static final String USER_ON_BOARDING_THRESHOLD_NOT_MET_MSG = "USER_ON_BOARDING_THRESHOLD_NOT_MET_MSG";
	public static final String USER_STATION_ID = "stationId";
	public static final String USER_CENTER_ID = "centerId";
	public static final String USER_ONBOARD_DATA = "UserOnboardBiometricData";
	public static final String USER_ON_BOARD_THRESHOLD_LIMIT = "mosip.registration.user_on_board_threshold_limit";

	// Configuration Constants
	public static final String AUDIT_LOG_DELETION_CONFIGURED_DAYS = "mosip.registration.audit_log_deletion_configured_days";
	public static final String SYNC_TRANSACTION_NO_OF_DAYS_LIMIT = "mosip.registration.sync_transaction_no_of_days_limit";
	public static final String REG_DELETION_CONFIGURED_DAYS = "mosip.registration.reg_deletion_configured_days";
	public static final String PRE_REG_DELETION_CONFIGURED_DAYS = "mosip.registration.pre_reg_deletion_configured_days";

	// Audit Constants
	public static final String AUDIT_LOGS_DELETION_SUCESS_MSG = "AUDIT_LOGS_DELETION_SUCESS_MSG";
	public static final String AUDIT_LOGS_DELETION_FLR_MSG = "Audit Logs Deleted Failed";
	public static final String AUDIT_LOGS_DELETION_EMPTY_MSG = "AUDIT_LOGS_DELETION_EMPTY_MSG";

	// Rest Authentication Constants
	public static final String USER_DTO = "userDTO";
	public static final String REST_OAUTH = "oauth";
	public static final String REST_OAUTH_USER_NAME = "userName";
	public static final String REST_OAUTH_USER_PSWD = "password";
	public static final String REST_OAUTH_ERROR_CODE = "REST-OAUTH-001";
	public static final String REST_OAUTH_ERROR_MSG = "Internal Server Error";
	public static final String REST_AUTHORIZATION = "authorization";
	public static final String REST_RESPONSE_BODY = "responseBody";
	public static final String REST_RESPONSE_HEADERS = "responseHeader";
	public static final String AUTH_SET_COOKIE = "Set-Cookie";
	public static final String AUTH_AUTHORIZATION = "Authorization";
	public static final String AUTH_EXPIRES = "Expires";
	public static final String AUTH_MAX_AGE = "Max-Age";
	public static final String REGISTRATION_CLIENT = "mosip.registration.app.id";
	public static final String REGISTRATION_CONTEXT = "auth-otp";
	public static final String COOKIE = "Cookie";
	public static final String ENGLISH_LANG_CODE = "eng";
	public static final String USER_ID_CODE = "USERID";
	public static final String OTP_CHANNELS = "mosip.registration.otp_channels";
	public static final String AUTH_TOKEN_NOT_RECEIVED_ERROR = "Auth failed, Invalid/expired auth token";

	// flag for quality check with SDK
	public static final String QUALITY_CHECK_WITH_SDK = "mosip.registration.quality_check_with_sdk";
	public static final String UPDATE_SDK_QUALITY_SCORE = "mosip.registration.replace_sdk_quality_score";

	// Packet Sync
	public static final String PACKET_SYNC = "packet_sync";
	public static final String PACKET_SYNC_V2 = "packet_sync_v2";

	// Validations to ignore

	public static List<String> fieldsToExclude() {
		List<String> fieldToExclude = new ArrayList<>();
		fieldToExclude.add("preRegistrationId");
		fieldToExclude.add("virtualKeyboard");
		fieldToExclude.add("docPageNumber");
		fieldToExclude.add("residence");
		fieldToExclude.add("NFR");
		fieldToExclude.add("FR");
		fieldToExclude.add("residenceLocalLanguage");
		fieldToExclude.add("genderValue");
		fieldToExclude.add("genderValueLocalLanguage");
		fieldToExclude.add("updateUinId");

		return fieldToExclude;

	}

	// ID JSON Business Validation
	/*private static final String ID_JSON_BIZ_VALIDATION_PREFIX = "mosip.id.validation.identity";
	public static final String LENGTH = "length";
	public static final String EMAIL_VALIDATION_REGEX = ID_JSON_BIZ_VALIDATION_PREFIX.concat(DOT).concat("email");
	public static final String EMAIL_VALIDATION_LENGTH = EMAIL_VALIDATION_REGEX.concat(DOT).concat(LENGTH);
	public static final String PHONE_VALIDATION_REGEX = ID_JSON_BIZ_VALIDATION_PREFIX.concat(DOT).concat("phone");
	public static final String PHONE_VALIDATION_LENGTH = PHONE_VALIDATION_REGEX.concat(DOT).concat(LENGTH);
	public static final String REFERENCE_ID_NO_VALIDATION_REGEX = ID_JSON_BIZ_VALIDATION_PREFIX.concat(DOT)
			.concat("referenceIdentityNumber");
	public static final String POSTAL_CODE_VALIDATION_REGEX = ID_JSON_BIZ_VALIDATION_PREFIX.concat(DOT)
			.concat("postalCode");
	public static final String POSTAL_CODE_VALIDATION_LENGTH = POSTAL_CODE_VALIDATION_REGEX.concat(DOT).concat(LENGTH);
	public static final String DOB_VALIDATION_REGEX = ID_JSON_BIZ_VALIDATION_PREFIX.concat(DOT).concat("dateOfBirth");
	public static final String ID_FULL_NAME_REGEX = ID_JSON_BIZ_VALIDATION_PREFIX.concat(DOT)
			.concat("fullName.[*].value");
	public static final String ADDRESS_LINE_1_REGEX = ID_JSON_BIZ_VALIDATION_PREFIX.concat(DOT)
			.concat("addressLine1.[*].value");
	public static final String ADDRESS_LINE_2_REGEX = ID_JSON_BIZ_VALIDATION_PREFIX.concat(DOT)
			.concat("addressLine2.[*].value");
	public static final String ADDRESS_LINE_3_REGEX = ID_JSON_BIZ_VALIDATION_PREFIX.concat(DOT)
			.concat("addressLine3.[*].value");
	public static final String AGE_VALIDATION_REGEX = ID_JSON_BIZ_VALIDATION_PREFIX.concat(DOT).concat("age");
*/	public static final String TRUE = String.valueOf(true);
	public static final String FALSE = String.valueOf(false);
	public static String CNI_MANDATORY = String.valueOf(false);

	public static final String REGEX = "regex";
	public static final String IS_MANDATORY = "isMandatory";
	public static final String IS_FIXED = "isFixed";

	// Virus Scan
	public static final String VIRUS_SCAN_PACKET_NOT_FOUND = "FILE_NOT_PRESENT_FOR_SCAN";
	public static final String VIRUS_SCAN_INFECTED_FILES = "Infected Files";
	public static final String ANTIVIRUS_SERVICE_NOT_ACCESSIBLE = "ANTIVIRUS_SERVICE_NOT_ACCESSIBLE";

	// concent of applicant
	public static final String YES = "Yes";
	public static final String NO = "No";

	// User Details
	public static final String USER_DETAILS_SERVICE_NAME = "user_details";
	public static final String MAPPER_UTILL = "MAPPER_UTILL";
	public static final String REG_ID = "regid";

	public static final String CONTENT_TYPE_EMAIL = "EMAIL";
	public static final String CONTENT_TYPE_MOBILE = "MOBILE";

	// Key-Policy Key validation
	public static final String VALID_KEY = "VALID KEY";
	public static final String INVALID_KEY = "INVALID_KEY";

	public static final String JOB_ID = "JOB_ID";

	public static final String SYNC_DATA_FREQ = "mosip.registration.sync_jobs_restart_freq";

	public static final String LABEL = "Label";
	public static final String LABEL_SMALL_CASE = "label";

	public static final Object UI_SYNC_DATA = "mosip.registration.ui_sync_data";
	//public static final String MDM_ENABLED = "mosip.mdm.enabled";

	public static final String MESSAGE = "Message";
	public static final String HASH = "#";
	public static final String QOUTE = "\"";
	public static final String DOB_MESSAGE = "dobMessage";
	public static final String DD = "dd";
	public static final String MM = "mm";
	public static final String YYYY = "yyyy";
	public static final String DOB = "dob";
	public static final String ERRORS = "errors";
	public static final String ERROR_MSG = "message";
	public static final String OK_MSG = "ok";
	public static final String NEW_LINE = "\n";

	public static final String ATTR_INDIVIDUAL_TYPE = "individualTypeCode";
	public static final String ATTR_DATE_OF_BIRTH = "dateofbirth";
	public static final String ATTR_GENDER_TYPE = "genderCode";
	public static final String ATTR_NON_FORINGER = "NFR";
	public static final String ATTR_FORINGER = "FR";
	public static final String ATTR_FORINGER_DOB_PARSING = "yyyy/MM/dd";
	public static final String ATTR_FORINGER_DOB_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

	public static final String SYNC_FAILURE = "SYNC_FAILURE";

	// Scheduler
	public static final String IDLE_TIME = "mosip.registration.idle_time";
	public static final String REFRESHED_LOGIN_TIME = "mosip.registration.refreshed_login_time";
	public static final String SCHEDULER_TITLE_STYLE = "schedulerTitleMsg";
	public static final String SCHEDULER_CONTENT_STYLE = "schedulerMsg";
	public static final String SCHEDULER_TITLE_BORDER = "schedulerTitle";
	public static final String SCHEDULER_TIMER_STYLE = "schedulerTimer";
	public static final String SCHEDULER_BTN_STYLE = "schedulerContinueBtn";
	public static final String SCHEDULER_BORDER = "schedulerStage";

	public static final String USER_DETAILS = "userDetails";

	public static final String OTP_EXPIRY_TIME = "mosip.kernel.otp.expiry-time";

	// TODO Need to discuss with Sravya about code
	public static String INITIAL_SETUP = "mosip.registration.initial_setup";

	public static final String SIGNED_KEY = "signed-key";
	public static final String TIME_STAMP = "timeStamp";
	public static final String REF_ID = "referenceId";
	public static final String PUBLIC_KEY_ISSUES_DATE = "issuedAt";
	public static final String PUBLIC_KEY_EXPIRE_DATE = "expiryAt";
	public static final String PUBLIC_KEY = "publicKey";
	public static final String PUBLIC_KEY_REST = "public_key";
	public static final String GET_CERTIFICATE = "getcert_sync";
	public static final String GET_CERT_APP_ID = "applicationId";
	public static final String KER = "SIGN";
	public static final String CERTIFICATE = "certificate";
	public static final String DTAE_MONTH_YEAR_REGEX = "dd|mm|yyyy|ddLocalLanguage|mmLocalLanguage|yyyyLocalLanguage|ageField|dob";

	public static final String UIN_UPDATE_PARENTGUARDIAN_DETAILS = "parentOrGuardianDetails";

	public static final String PAGE_NAVIGATION_CONFIRM = "PAGE_NAVIGATION_CONFIRM";
	public static final String PAGE_NAVIGATION_CANCEL = "PAGE_NAVIGATION_CANCEL";
	public static final String UPDATE_NOW_LABEL = "UPDATE_NOW_LABEL";
	public static final String UPDATE_LATER_LABEL = "UPDATE_LATER_LABEL";
	public static String CANCEL_MSG = "CANCEL_LABEL";
	public static final String PUBLIC_KEY_REF_ID = "packet-encryption-key";
	public static final String USER_DETAIL_SALT_SERVICE_NAME = "user_salt_details";
	public static final String SERVICES_VERSION_KEY = "mosip.reg.services.current.version";

	// TPM
	public static final byte[] NULL_VECTOR = new byte[0];
	public static final String MOSIP_REGISTRATION_DB_KEY = "mosip.reg.db.key";

	// SQL Execution
	public static final String SQL_EXECUTION_SUCCESS = "SQL EXECUTION SUCCESS";
	public static final String ROLL_BACK_SQL_EXECUTION_SUCCESS = "ROLL BACK SQL EXECUTION SUCCESS";
	public static final String ROLL_BACK_SQL_EXECUTION_FAILURE = "ROLL BACK SQL EXECUTION FAILURE";
	public static final String SQL_EXECUTION_FAILURE = "SQL EXECUTION FAILURE";
	public static final String BACKUP_PREVIOUS_SUCCESS = "Backup Success";
	public static final String BACKUP_PREVIOUS_FAILURE = "Backup Failed";

	public static final String PUBLICKEY = "publicKey";
	public static final String ISSUED_AT = "issuedAt";
	public static final String EXPIRY_AT = "expiryAt";
	public static final String SERVICE_NAME = "policysync";

	public static final String IDA_REFERENCE_ID = "INTERNAL";
	public static final String PUBLIC_KEY_IDA_REST = "ida_key";
	public static final String ON_BOARD_IDA_VALIDATION = "ida_auth";
	public static final String ID = "id";
	public static final String IDENTITY = "mosip.identity.auth.internal";
	public static final String VERSION = "version";
	public static final String ENV = "env";
	public static final String DOMAIN_URI = "domainUri";
	public static final String TRANSACTION_Id = "transactionId";
	public static final String PURPOSE = "purpose";
	public static final String PURPOSE_AUTH = "Auth";
	public static final String REQUEST_TIME = "requestTime";
	public static final String TRANSACTION_ID = "transactionID";
	public static final String TRANSACTION_ID_VALUE = "1234567890";
	public static final String BIO = "bio";
	public static final String REQUEST_AUTH = "requestedAuth";
	public static final String CONSENT_OBTAINED = "consentObtained";
	public static final String INDIVIDUAL_ID = "individualId";
	public static final String INDIVIDUAL_ID_TYPE = "individualIdType";
	public static final String KEY_INDEX = "keyIndex";
	public static final String ON_BOARD_TIME_STAMP = "timestamp";
	public static final String DEVICE_PROVIDER_ID = "deviceProviderID";
	public static final String ON_BOARD_BIO_TYPE = "bioType";
	public static final String ON_BOARD_BIO_SUB_TYPE = "bioSubType";
	public static final String ON_BOARD_BIO_VALUE = "bioValue";
	public static final String ON_BOARD_BIO_DATA = "data";
	public static final String ON_BOARD_BIOMETRICS = "biometrics";
	public static final String ON_BOARD_REQUEST = "request";
	public static final String ON_BOARD_REQUEST_HMAC = "requestHMAC";
	public static final String ON_BOARD_REQUEST_SESSION_KEY = "requestSessionKey";
	public static final String ON_BOARD_PUBLIC_KEY_ERROR = "Public key is either null or invalid public key";
	public static final String ON_BOARD_AUTH_STATUS = "authStatus";
	public static final String ON_BOARD_FACE_ID = "FID";
	public static final String ON_BOARD_IRIS_ID = "IIR";
	public static final String ON_BOARD_FINGER_ID = "FIR";
	public static final String ON_BOARD_COGENT = "cogent";
	public static final String AGE_IDENTITY = "identity";
	public static final String DATE_OF_BIRTH = "dateOfBirth";

	public static final String STUB_FACE = "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";

	// TPM Public Key Sync
	public static final String ERROR_CODE = "errorCode";
	public static final String MESSAGE_CODE = "message";
	public static final String TPM_PUBLIC_KEY_SYNC_SERVICE_NAME = "tpm_public_key";
	public static final String SERIAL_NUMBER = "serialnumber";

	public static final Map<String, String> userOnBoardMap = new HashMap<String, String>() {

		{
			put("leftIndex", "LF_INDEX");
			put("leftLittle", "LF_LITTLE");
			put("leftMiddle", "LF_MIDDLE");
			put("leftRing", "LF_RING");
			put("leftThumb", "LF_THUMB");
			put("rightIndex", "RF_INDEX");
			put("rightLittle", "RF_LITTLE");
			put("rightMiddle", "RF_MIDDLE");
			put("rightRing", "RF_RING");
			put("rightThumb", "RF_THUMB");
			put("LeftEye", "L_IRIS");
			put("RightEye", "R_IRIS");
			put("LeftEye.png", "LEFT");
			put("RightEye.png", "RIGHT");
		}

	};

	public static final Map<String, String> uIToMDSExceptionMap = new HashMap<String, String>() {

		{
			put("leftIndex", "LF_INDEX");
			put("leftLittle", "LF_LITTLE");
			put("leftMiddle", "LF_MIDDLE");
			put("leftRing", "LF_RING");
			put("leftThumb", "LF_THUMB");
			put("rightIndex", "RF_INDEX");
			put("rightLittle", "RF_LITTLE");
			put("rightMiddle", "RF_MIDDLE");
			put("rightRing", "RF_RING");
			put("rightThumb", "RF_THUMB");
			put("leftEye", "L_IRIS");
			put("rightEye", "R_IRIS");
		}

	};

	public static final Map<String, String> userOnBoardBioFlag = new HashMap<String, String>() {

		{
			put("Left Index", "Left IndexFinger");
			put("Left Little", "Left LittleFinger");
			put("Left Middle", "Left MiddleFinger");
			put("Left Ring", "Left RingFinger");
			put("Left Thumb", "Left Thumb");
			put("Right Index", "Right IndexFinger");
			put("Right Little", "Right LittleFinger");
			put("Right Middle", "Right MiddleFinger");
			put("Right Ring", "Right RingFinger");
			put("Right Thumb", "Right Thumb");
			put("Left Iris", "Left");
			put("Right Iris", "Right");

		}

	};

	public static final String RESPONSE_SIGNATURE = "response-signature";
	public static final String ON_BOARD_FACE = "FACE";
	public static final String AUTH_SERVICE_URL = "authmanager/authenticate";

	public static final int DAYS = 31;
	public static final int MONTH = 12;
	public static final int YEAR = 3;
	public static final String EYETOOLTIP = "View Details";
	public static final String DOCUMENT_VIEW_ICON = "DocumentViewIcon";

	public static final String LANG_CODE_MANDATORY = "language code is mandatory...";
	public static final String CODE_AND_LANG_CODE_MANDATORY = "code and language code is mandatory...";
	public static final String TRIGGER_POINT_MSG = "trigger point is mandatory...";

	public static final String LeftIndex = "Left Index";
	public static final String LeftMiddle = "Left Middle";
	public static final String LeftRing = "Left Ring";
	public static final String LeftLittle = "Left Little";
	public static final String RightIndex = "Right Index";
	public static final String RightMiddle = "Right Middle";
	public static final String RightRing = "Right Ring";
	public static final String RightLittle = "Right Little";
	public static final String LeftThumb = "Left Thumb";
	public static final String RightThumb = "Right Thumb";

	// bio-devices bio-types
	public static final List<String> LEFT_SLAP = Arrays.asList(LeftIndex, LeftMiddle, LeftRing, LeftLittle);

	public static final List<String> RIGHT_SLAP = Arrays.asList(RightIndex, RightMiddle, RightRing, RightLittle);

	public static final List<String> TWO_THUMBS = Arrays.asList(LeftThumb, RightThumb);

	public static final String LEFT_EYE = "Left Iris";
	public static final String RIGHT_EYE = "Right Iris";

	public static final List<String> TWO_IRIS = Arrays.asList(LEFT_EYE, RIGHT_EYE);

	// public static final String SESSION_KEY_URL = "session_key";
	public static final String AUTH_HASH = "hash";
	public static final String SESSION_KEY = "sessionKey";
	public static final String SIGNATURE = "signature";
	public static final String ADD = "aad";
	public static final String SALT = "salt";
	public static final String REQ_TIME = "requesttime";
	public static final String AP_ID = "applicationId";
	public static final String AP_IDA = "IDA";
	public static final int MAX_BIO_QUALITY_SCORE = 100;

	public static String DEDUPLICATION_ENABLE_FLAG = "mosip.registration.mds.deduplication.enable.flag";

	public static final String SERVER_ACTIVE_PROFILE = "mosip.registration.server_profile";
	public static final String AUTH_FINGERPRINT_SLAB = "mosip.registration.auth_fingerprint_slab";
	public static final String ID_AUTH_DOMAIN_URI = "mosip.registration.ida.domainuri";
	public static final String MDS_RESP_ALLOWED_LAG_MINS = "mosip.registration.mds.allowed.lag.mins";

	public static final String applicantBiometricDTO = "applicantBiometricDTO";
	public static final String introducerBiometricDTO = "introducerBiometricDTO";
	public static final String supervisorBiometricDTO = "supervisorBiometricDTO";
	public static final String operatorBiometricDTO = "operatorBiometricDTO";

	public static final String leftIndexUiAttribute = "leftIndex";
	public static final String leftLittleUiAttribute = "leftLittle";
	public static final String leftMiddleUiAttribute = "leftMiddle";
	public static final String leftRingUiAttribute = "leftRing";
	public static final String leftThumbUiAttribute = "leftThumb";

	public static final String rightIndexUiAttribute = "rightIndex";
	public static final String rightLittleUiAttribute = "rightLittle";
	public static final String rightMiddleUiAttribute = "rightMiddle";
	public static final String rightThumbUiAttribute = "rightThumb";
	public static final String rightRingUiAttribute = "rightRing";

	public static final String leftEyeUiAttribute = "leftEye";
	public static final String rightEyeUiAttribute = "rightEye";

	public static final List<String> rightHandUiAttributes = Arrays.asList(rightIndexUiAttribute,
			rightMiddleUiAttribute, rightRingUiAttribute, rightLittleUiAttribute);
	public static final List<String> leftHandUiAttributes = Arrays.asList(leftLittleUiAttribute,
			leftRingUiAttribute, leftMiddleUiAttribute, leftIndexUiAttribute);
	public static final List<String> twoThumbsUiAttributes = Arrays.asList(leftThumbUiAttribute, rightThumbUiAttribute);
	public static final List<String> eyesUiAttributes = Arrays.asList(leftEyeUiAttribute, rightEyeUiAttribute);
	public static final List<String> faceUiAttributes = Arrays.asList("face");

	public static final String ID_SCHEMA_SYNC_SERVICE = "idschema_key";

	/*public static final Map<String, String> regBioMap = new HashMap<String, String>() {

		{
			put("leftIndex", LeftIndex);
			put("leftLittle", LeftLittle);
			put("leftMiddle", LeftMiddle);
			put("leftRing", LeftRing);
			put("leftThumb", LeftThumb);
			put("rightIndex", RightIndex);
			put("rightLittle", RightLittle);
			put("rightMiddle", RightMiddle);
			put("rightRing", RightRing);
			put("rightThumb", RightThumb);
			put("leftEye", LEFT_EYE);
			put("rightEye", RIGHT_EYE);

		}

	};*/

	/*public static final Map<String, String> mdsToRegBioMap = new HashMap<String, String>() {

		{
			put("LF_INDEX", LeftIndex);
			put("LF_LITTLE", LeftLittle);
			put("LF_MIDDLE", LeftMiddle);
			put("LF_RING", LeftRing);
			put("LF_THUMB", LeftThumb);
			put("RF_INDEX", RightIndex);
			put("RF_LITTLE", RightLittle);
			put("RF_MIDDLE", RightMiddle);
			put("RF_RING", RightRing);
			put("RF_THUMB", RightThumb);
			put("L_IRIS", LEFT_EYE);
			put("R_IRIS", RIGHT_EYE);
		}

	};*/

	public static final String BIOMETRICS_TYPE = "biometricsType";
	public static final String APPLICANT = "applicant";

	//public static final String MOSIP_CLEINT_ID = "mosip.registration.client.id";
	//public static final String MOSIP_SECRET_KEY = "mosip.registration.secret.Key";

	/** BELOW WERE EXTERNALISING Spring.properties to external configs */

	// Timeout Configuartion
	public static final String HTTP_API_READ_TIMEOUT = "mosip.registration.HTTP_API_READ_TIMEOUT";
	public static final String HTTP_API_WRITE_TIMEOUT = "mosip.registration.HTTP_API_WRITE_TIMEOUT";

	// Biometric Device Already present in configs
	// public static final String PROVIDER_NAME =
	// "mosip.registration.PROVIDER_NAME";
	// public static final String WEBCAM_LIBRARY_NAME =
	// "mosip.registration.WEBCAM_LIBRARY_NAME";

	// document scanner properties
	public static final String DOCUMENT_SCANNER_DEPTH = "mosip.registration.DOCUMENT_SCANNER_DEPTH";
	public static final String DOCUMENT_SCANNER_HOST = "mosip.registration.DOCUMENT_SCANNER_HOST";
	public static final String DOCUMENT_SCANNER_PORT = "mosip.registration.DOCUMENT_SCANNER_PORT";
	public static final String DOCUMENT_SCANNER_TIMEOUT = "mosip.registration.DOCUMENT_SCANNER_TIMEOUT";

	// #MDM
	// #host has to be clarified whether nedd to be in config or hardcoded
	// #portRangeFrom and portRangeTo are mandate,portRangeTo can be same are
	// greater than then portRangeFrom
	public static final String MDM_HOST = "mosip.registration.mdm.host";
	public static final String MDM_START_PORT_RANGE = "mosip.registration.mdm.portRangeFrom";
	public static final String MDM_END_PORT_RANGE = "mosip.registration.mdm.portRangeTo";
	public static final String MDM_CONTEXT_PATH = "mosip.registration.mdm.contextPath";
	public static final String MDM_HOST_PROTOCOL = "mosip.registration.mdm.hostProtocol";
	public static final String MDM_CONNECTION_TIMEOUT = "mosip.registration.mdm.connection.timeout";
	public static final String METHOD_BASED_MDM_CONNECTION_TIMEOUT = "mosip.registration.mdm.%s.connection.timeout";

	// #Proof Of Exception Documents
	public static final String POE_DOCUMENT_VALUE = "mosip.registration.doc_value";

	public static final String LOST_REGISTRATION_BIO_MVEL_OPERATOR = "mosip.registration.lostuin.biometrics.mvel.operator";
	public static final String UPDATE_REGISTRATION_BIO_MVEL_OPERATOR = "mosip.registration.updateuin.biometrics.mvel.operator";
	public static final String LOST_REGISTRATION_BIO_MVEL_EXPR = "mosip.registration.lostuin.biometrics.mvel.expr";
	public static final String UPDATE_REGISTRATION_BIO_MVEL_EXPR = "mosip.registration.updateuin.biometrics.mvel.expr";

	public static final String BIOMETRICS_GROUP = "Biometrics";

	public static final String SPEC_VERSION_095 = "0.9.5";
	public static final String SPEC_VERSION_092 = "0.9.2";
	public static final String SPEC_VERSION_1_0 = "1.0";

	public static final String TEMPLATE_FACE_IMAGE_SOURCE = "FaceImageSource";

	public static final String REGISTRATION_EVENTS = "REG-EVT";

	public static final String EXCEPTION_PHOTO = "Exception_Photo";

	public static final String EXCEPTIONAL_SCANNER_DEVICE_TYPES = "mosip.registration.scanner.device.types";

	// Exception Images Path
	public static final String LEFTMIDDLE_IMG_PATH = "/images/exceptionmarks/leftMiddle.png";
	public static final String LEFTINDEX_IMG_PATH = "/images/exceptionmarks/leftIndex.png";
	public static final String LEFTRING_IMG_PATH = "/images/exceptionmarks/leftRing.png";
	public static final String LEFTLITTLE_IMG_PATH = "/images/exceptionmarks/leftLittle.png";
	public static final String LEFTTHUMB_IMG_PATH = "/images/exceptionmarks/leftThumb.png";
	public static final String RIGHTEYE_IMG_PATH = "/images/exceptionmarks/lightBlueCrossMark.png";
	public static final String LEFTEYE_IMG_PATH = "/images/exceptionmarks/lightBlueCrossMark.png";

	// UI Schema field group name
	public static final String UI_SCHEMA_GROUP_FULL_NAME = "FullName";
	public static final String UI_SCHEMA_SUBTYPE_FULL_NAME = "name";
	public static final String UI_SCHEMA_SUBTYPE_EMAIL = "Email";
	public static final String UI_SCHEMA_SUBTYPE_PHONE = "Phone";
	public static final String MVEL_TYPE = "MVEL";
	public static final String MACHINE_VERIFICATION_SUCCESS = "Machine Verification Success";
	public static final String UTC_APPENDER = " (UTC)";

	// Mandatory Astrik
	public static final String ASTRIK = " * ";

	public static String MACHINE_REMAP_CODE = "KER-SNC-149";

	public static String OPERATOR_ONBOARDING_BIO_ATTRIBUTES = "mosip.registration.operator.onboarding.bioattributes";
	public static String RIGHT_TO_LEFT_ORIENTATION_LANGUAGES = "mosip.language.righttoleft.orientation";

	public static String CONFIGURATION = "CONFIGURATION";
	public static String ONBOARD_CERT_THUMBPRINT = "thumbprint";

	public static final String OPT_TO_REG_CCS_J00017 = "CCS_J00017";
	public static final String API_CALL_FAILED = "API call failed with unknown error";

	public static final String FORGOT_USERNAME_URL = "mosip.registration.forgot_username_url";
	public static final String FORGOT_PWORD_URL = "mosip.registration.forgot_password_url";
	public static final String RESET_PWORD_URL = "mosip.registration.reset_password_url";
	public static final String EMAIL_PLACEHOLDER = "{EMAIL}";
	public static final String ONBOARD_YOURSELF_URL = "mosip.registration.onboard_yourself_url";
	public static final String REGISTERING_INDIVIDUAL_URL = "mosip.registration.registering_individual_url";
	public static final String SYNC_DATA_URL = "mosip.registration.sync_data_url";
	public static final String MAPPING_DEVICES_URL = "mosip.registration.mapping_devices_url";
	public static final String UPLOADING_DATA_URL = "mosip.registration.uploading_data_url";
	public static final String UPDATING_BIOMETRICS_URL = "mosip.registration.updating_biometrics_url";

	// Dashboard Constants
	public static final String DASHBOARD_TITLE = "dashBoard";
	public static final String DASHBOARD_USERS = "Users";
	public static final String DASHBOARD_FORMAT = "mosip.registration.dashboard_date_format";
	public static final String TOTAL_PACKETS_LABEL = "totalPacketsLabel";
	public static final String PENDING_EOD_LABEL = "pendingEODLabel";
	public static final String PENDING_UPLOAD_LABEL = "pendingUploadLabel";
	public static final String TOTAL_PACKETS_COUNT = "totalPacketsCount";
	public static final String PENDING_EOD_COUNT = "pendingEODCount";
	public static final String PENDING_UPLOAD_COUNT = "pendingUploadCount";
	public static final String USER_DETAILS_MAP = "userDetails";
	public static final String ACTIVITIES_MAP = "activities";
	public static final String DASHBOARD_USER_ID = "userId";
	public static final String DASHBOARD_USER_NAME = "userName";
	public static final String DASHBOARD_USER_ROLE = "userRole";
	public static final String DASHBOARD_USER_STATUS = "userStatus";
	public static final String DASHBOARD_ACTIVITY_NAME = "activityName";
	public static final String DASHBOARD_ACTIVITY_VALUE = "activityValue";
	public static final String BOLD_TAG = "<b>";
	public static final String BOLD_END_TAG = "</b>";
	public static final String DASHBOARD_UPDATES = "Updates";

	public static String LOCATION = "Location";
	public static String IMAGE_VIEW = "imageView";
	public static final String DEMOGRAPHIC_COMBOBOX = "demographicCombobox";
	public static String TEXT_FIELD = "TextField";
	public static String VBOX = "VBOX";
	public static String HBOX = "HBOX";
	public static String VALUE = "VALUE";
	public static final String GENERIC_DETAIL = "genericScreen";
	public static final String GENERIC_LAYOUT = "/fxml/GenericRegistrationLayout.fxml";
	public static final String BIOMETRIC_FXML = "/fxml/GenericBiometricFXML.fxml";
	public static final String DEVICE_STATUS_READY = "Ready";

//	Code CleanUp 
	public static final String LABELS = "labels";
	public static final String MESSAGES = "messages";
	public static final String Resident_Information = "Resident_Information";
	public static final String previewHeader = "previewHeader";
	public static final String authentication = "authentication";
	public static final String SLASH = " / ";

	public static final String NEW_REGISTRATION_FLOW = "New Registration";
	public static final String UIN_UPDATE_FLOW = "UIN Update";
	public static final String LOST_UIN_FLOW = "Lost UIN";
	public static final String SCAN_BUTTON = "scan";
	public static final String REF_NUMBER = "REF_NUMBER";
	public static final String INVALID_AGE = "INVALID_AGE";
	public static final String INVALID_DATE = "INVALID_DATE";
	public static final String INVALID_DATE_LIMIT = "INVALID_DATE_LIMIT";
	public static final String DOB_REQUIRED = "DOB_REQUIRED";

	public static final String ICONS_HBOX = "iconsHBox";
	public static final String KEYBOARD_PANE = "keyboardPane";
	public static final String QUALITY_BOX_LABEL = "qualityBoxLabel";
	public static final String MODALITY_BUTTONS = "modalityButtons";
	public static final int LANGCODE_LENGTH = 3;

	// Images Constants
	public static final String IMAGES_PATH = "mosip.registration.images.path";
	public static final String IMAGES_THEME = "mosip.registration.theme";
	public static final String NEW_REGISTRATION_IMG = "NewRegistration.png";
	public static final String PRINTER_IMG = "Printer.png";
	public static final String PHOTO_IMG = "Photo.png";
	public static final String CLOSE_IMG = "DocumentClose.png";
	public static final String RIGHT_HAND_IMG = "rightHand.png";
	public static final String SCAN_IMG = "scan.png";
	public static final String EYE_IMG = "Eye.png";
	public static final String ARROW_LEFT_IMG = "arrowLeft.png";
	public static final String ARROW_RIGHT_IMG = "arrowRight.png";
	public static final String MOSIP_LOGO_SMALL_IMG = "logo-final-small.png";
	public static final String USER_IMG = "User.png";
	public static final String REG_CENTER_LOCATION_IMG = "RegCentreLocation.png";
	public static final String SYSTEM_IMG = "system.png";;
	public static final String ONLINE_IMG = "SystemOnline.png";
	public static final String HAMBURGER_IMG = "hamburger.png";
	public static final String HOME_IMG = "Home.png";
	public static final String EXPORT_ICON_IMG = "export-icon.jpg";
	public static final String ACTIVE_DEMO_DETAILS_IMG = "activeDemographicDetails.png";
	public static final String DEACTIVE_AUTH_IMG = "deactiveAuthentication.png";
	public static final String DEACTIVE_BIOMETRIC_IMG = "deactiveBiometriDetails.png";
	public static final String DEACTIVEUPLOAD_DOCUMENT_IMG = "deactiveUploadDocument.png";
	public static final String MOSIP_LOGON_FINAL_IMG = "logo-final.png";
	public static final String FINGERPRINT_SCAN_IMG = "FingerprintScan.png";
	public static final String EYE_SCAN_IMG = "Eyescan.png";
	public static final String FACE_SCAN_IMG = "FaceScan.png";
	public static final String SYNC_IMG = "sync.png";
	public static final String DWLD_PRE_REG_DATA_IMG = "DownloadPreRegData.png";
	public static final String UPDATE_OPERATOR_BIOMETRICS_IMG = "UpdateOperatorBiometrics.png";
	public static final String NEW_REG_IMG = "NewReg.png";
	public static final String UIN_UPDATE_IMG = "UINUpdate.png";
	public static final String LOST_UIN_IMG = "LostUIN.png";
	public static final String PENDING_APPROVAL_IMG = "PendingApproval.png";
	public static final String RE_REGISTRATION_IMG = "Re-Registration.png";
	public static final String VIEW_REPORTS_IMG = "ViewReports.png";
	public static final String TICK_IMG = "tick.png";
	public static final String AUTHENTICATE_IMG = "Authenticate.png";
	public static final String REJECT_IMG = "Reject.png";
	public static final String APPROVE_IMG = "Approve.png";
	public static final String INFORMED_IMG = "informed.png";
	public static final String CANT_INFORM_IMG = "cantInform.png";
	public static final String STREAM_IMG = "stream.png";
	public static final String CROP_IMG = "crop.png";
//    public static final String REJECT_IMG ="reject.png";
	public static final String HOVER_IMG = "hover.png";
	public static final String EMAIL_IMG = "Email.png";
	public static final String MOBILE_IMG = "EnterMobile.png";
	public static final String GET_ONBOARD_IMG = "GetOnboarded.png";
	public static final String ONBOARD_YOURSELF_IMG = "OnboardYourself.png";
	public static final String REGISTER_INDVIDUAL_IMG = "RegisterIndividual.png";
	public static final String ACTIVE_BIOMETRIC_DETAILS_IMG = "activeBiometricDetail.png";
	public static final String SEND_EMAIL_IMG = "SendEmail.png";

	public static final String UPDATE_UIN_FOCUSED_IMG = "updateUINFocused.png";
	public static final String SYNC_DATA_FOCUSED_IMG  = "syncFocused.png";
	public static final String DOWNLOAD_PREREG_FOCUSED_IMG  = "DownloadPreRegDataFocused.png";
	public static final String UPDATE_OP_BIOMETRICS_FOCUSED_IMG  = "UpdateOperatorBiometricsFocused.png";
	public static final String PENDING_APPROVAL_FOCUSED_IMG  = "PendingApprovalFocused.png";
	public static final String RE_REGISTRATION_FOCUSED_IMG  = "ReRegistrationFocused.png";
	public static final String VIEW_REPORTS_FOCUSED_IMG  = "ViewReportsFocused.png";
	public static final String GET_ONBOARDED_FOCUSED_IMG  = "GetOnboardedFocus.png";
	public static final String ONBOARDING_FOCUSED_IMG  = "OnboardYourselfFocus.png";
	public static final String REGISTERING_FOCUSED_IMG  = "RegisterIndividualFocus.png";
	public static final String UPDATE_BIOMETRICS_FOCUSED_IMG  = "UpdateBiometricsFocus.png";
	public static final String LOST_UIN_FOCUSED_IMG = "lostUINFocused.png";
	public static final String BACK_FOCUSED_IMG = "backInWhite.png";
	public static final String WRONG_IMG = "wrong.png";
	public static final String DOUBLE_IRIS_IMG = "Eyes.png";
	public static final String DOUBLE_IRIS_WITH_INDICATORS_IMG = "EyesWithIndicators.png";
	public static final String FACE_IMG = "Photo.png";
	public static final String LEFTPALM_IMG= "leftHand.png";
	public static final String RIGHTPALM_IMG = "rightHand.png";
	public static final String THUMB_IMG = "thumbs.png";
	public static final String TICK_CIRICLE_IMG = "tick-circle.png";
	public static final String EXCLAMATION_IMG = "exclamation.png";
	public static final String CROSS_IMG = "cross-mark.png";
	// Exception Images Path
	public static final String LEFTMIDDLE_IMG = "exceptionmarks/leftMiddle.png";
	public static final String LEFTINDEX_IMG = "exceptionmarks/leftIndex.png";
	public static final String LEFTRING_IMG = "exceptionmarks/leftRing.png";
	public static final String LEFTLITTLE_IMG = "exceptionmarks/leftLittle.png";
	public static final String LEFTTHUMB_IMG = "exceptionmarks/leftThumb.png";
	public static final String RIGHTEYE_IMG = "exceptionmarks/lightBlueCrossMark.png";
	public static final String LEFTEYE_IMG = "exceptionmarks/lightBlueCrossMark.png";
	public static final String DEFAULT_EXCEPTION_IMG = "ExceptionPhoto.png";
	public static final String IMAGES = "images";
	
	public static final String MOSIP_HOSTNAME = "mosip.hostname";
	public static final String MOSIP_UPGRADE_SERVER_URL = "mosip.client.upgrade.server.url";
	public static final String HEALTH_CHECK_URL = "mosip.reg.healthcheck.url";
	
	/** Moved To Here from UiConstants file */
	public static final String REGEX_TYPE = "REGEX";

	/** BIR Meta info constants */
	public static final String RETRIES = "RETRIES";
	public static final String FORCE_CAPTURED  = "FORCE_CAPTURED";
	public static final String SDK_SCORE  = "SDK_SCORE";
	public static final String EXCEPTION   = "EXCEPTION";
	public static final String CONFIGURED    = "CONFIGURED";
	public static final String PAYLOAD = "PAYLOAD";
	public static final String SPEC_VERSION = "SPEC_VERSION";

	
  public static final String PACKET_APPLICATION_ID = "applicationId";
  
	/** Settings Page Labels & CSS styles */
	public static final String SYNC_JOB_STYLE = "syncJobStyle";
	public static final String RUN_NOW_LABEL = "runNow";
	public static final String OPERATIONAL_TITLE = "operationalTitle";
	public static final String NEXT_RUN_LABEL = "nextRun";
	public static final String OPERATIONAL_DETAILS = "operationalDetails";
	public static final String LAST_RUN_LABEL = "lastRun";
	public static final String CRON_EXPRESSION_LABEL = "cronExpression";
	public static final String SYNC_JOB_LABEL_STYLE = "syncJobLabel";
	public static final String SYNC_JOB_TEXTFIELD_STYLE = "syncJobTextField";
	public static final String SUBMIT_LABEL = "submit";
	public static final String SYNC_JOB_BUTTON_STYLE = "syncJobButton";
	public static final String UPDATE_LABEL = "update";
	public static final String FINGERPRINT_DEVICE_IMG = "FingerprintDevice.PNG";
	public static final String IRIS_DEVICE_IMG = "IrisDevice.PNG";
	public static final String FACE_DEVICE_IMG = "FaceDevice.PNG";
	public static final String DOC_SCANNER_DEVICE = "DocScannerDevice.PNG";
	public static final String PORT_RANGE_REGEX = "^([0-9]{1,4}|[1-5][0-9]{4}|6[0-4][0-9]{3}|65[0-4][0-9]{2}|655[0-2][0-9]|6553[0-5])$";
	public static final String FINGERPRINT_DEVICE_KEY = "fingerprint_slab";
	public static final String IRIS_DEVICE_KEY = "iris_double";
	public static final String FACE_DEVICE_KEY = "face_face";
	public static final String FP_DEVICE_CONNECTED_IMG = "FpDeviceConnected.png";
	public static final String IRIS_DEVICE_CONNECTED_IMG = "IrisDeviceConnected.png";
	public static final String FACE_DEVICE_CONNECTED_IMG = "FaceDeviceConnected.png";
	public static final String FP_DEVICE_DISCONNECTED_IMG = "FpDeviceDisconnected.png";
	public static final String IRIS_DEVICE_DISCONNECTED_IMG = "IrisDeviceDisconnected.png";
	public static final String FACE_DEVICE_DISCONNECTED_IMG = "FaceDeviceDisconnected.png";
	public static final String REFRESH_ICON = "refresh.png";
	public static final String SHORTCUT_ICON = "shortcut.png";
	public static final String DEVICE_SETTINGS_NAME = "devices";
	public static final String DEVICES_SHORTCUT_PREFERENCE_NAME = "devices_shortcut";
	
	public static final String PERMITTED_JOB_TYPE = "JOB";
	public static final String PERMITTED_CONFIG_TYPE = "CONFIGURATION";
	public static final String PERMITTED_SHORTCUT = "SHORTCUT";
	
	public static final String QUIT_NOW = "QUIT_NOW";
	public static final String QUIT_LATER = "QUIT_LATER";

	public static final String RESPONSE_SIGNATURE_PUBLIC_KEY_APP_ID = "SERVER-RESPONSE";
	public static final String RESPONSE_SIGNATURE_PUBLIC_KEY_REF_ID = "SIGN-VERIFY";
	
	public static final String  BIOVALUE_KEY  = "bioValue";
	public static final String BIOVALUE_PLACEHOLDER = "\"<bioValue>\"";
	
	public static final String AUDIT_TIMESTAMP = "mosip.registration.audit_timestamp";
	public static final String AGE_GROUP_CONFIG = "mosip.regproc.packet.classifier.tagging.agegroup.ranges";
	
	public static final String LOGOUT = "Logout";
	public static final String ROLES_MODIFIED = "Roles Modified";

	public static final String JPG_COMPRESSION_QUALITY = "mosip.registration.doc.jpg.compression";
	public static final String APPLICANT_TYPE_MVEL_SCRIPT = "mosip.kernel.applicantType.mvel.file";
	public static final String FIELDS_TO_RETAIN_ON_PRID_FETCH = "mosip.registration.fields.to.retain.post.prid.fetch";
=======
public class RegistrationConstants {
    public static final String PACKET_EXTERNAL_STATUS_READER_ID = "mosip.registration.packet.external.status";
    public static final String PACKET_SYNC_VERSION = "1.0";
>>>>>>> 0215c59c15997774d37ddb562670565fae6ea61d
}
