package io.mosip.registration.clientmanager.constant;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.assertEquals;

public class RegistrationConstantsTest {

    @Test
    public void testComma() {
        assertEquals(",", RegistrationConstants.COMMA);
    }

    @Test
    public void testEmptyString() {
        assertEquals("", RegistrationConstants.EMPTY_STRING);
    }

    @Test
    public void testAgeGroup() {
        assertEquals("ageGroup", RegistrationConstants.AGE_GROUP);
    }

    @Test
    public void testAge() {
        assertEquals("age", RegistrationConstants.AGE);
    }

    @Test
    public void testDefaultAgeGroup() {
        assertEquals("adult", RegistrationConstants.DEFAULT_AGE_GROUP);
    }

    @Test
    public void testProcessKey() {
        assertEquals("_process", RegistrationConstants.PROCESS_KEY);
    }

    @Test
    public void testFlowKey() {
        assertEquals("_flow", RegistrationConstants.FLOW_KEY);
    }

    @Test
    public void testIdSchemaVersion() {
        assertEquals("IDSchemaVersion", RegistrationConstants.ID_SCHEMA_VERSION);
    }

    @Test
    public void testAuditExportedTill() {
        assertEquals("AuditExportedTill", RegistrationConstants.AUDIT_EXPORTED_TILL);
    }

    @Test
    public void testRightSlabAttr() {
        assertEquals(Arrays.asList("rightIndex", "rightMiddle", "rightRing", "rightLittle"),
                RegistrationConstants.RIGHT_SLAB_ATTR);
    }

    @Test
    public void testLeftSlabAttr() {
        assertEquals(Arrays.asList("leftIndex", "leftMiddle", "leftRing", "leftLittle"),
                RegistrationConstants.LEFT_SLAB_ATTR);
    }

    @Test
    public void testThumbsAttr() {
        assertEquals(Arrays.asList("leftThumb", "rightThumb"), RegistrationConstants.THUMBS_ATTR);
    }

    @Test
    public void testDoubleIrisAttr() {
        assertEquals(Arrays.asList("leftEye", "rightEye"), RegistrationConstants.DOUBLE_IRIS_ATTR);
    }

    @Test
    public void testFaceAttr() {
        assertEquals(Arrays.asList(""), RegistrationConstants.FACE_ATTR);
    }

    @Test
    public void testExceptionPhotoAttr() {
        assertEquals(Arrays.asList("unknown"), RegistrationConstants.EXCEPTION_PHOTO_ATTR);
    }

    @Test
    public void testDeduplicationEnableFlag() {
        assertEquals("mosip.registration.mds.deduplication.enable.flag",
                RegistrationConstants.DEDUPLICATION_ENABLE_FLAG);
    }

    @Test
    public void testEnable() {
        assertEquals("Y", RegistrationConstants.ENABLE);
    }

    @Test
    public void testDisable() {
        assertEquals("N", RegistrationConstants.DISABLE);
    }

    @Test
    public void testDiscoveryIntentAction() {
        assertEquals("io.sbi.device", RegistrationConstants.DISCOVERY_INTENT_ACTION);
    }

    @Test
    public void testDInfoIntentAction() {
        assertEquals(".Info", RegistrationConstants.D_INFO_INTENT_ACTION);
    }

    @Test
    public void testRCaptureIntentAction() {
        assertEquals(".rCapture", RegistrationConstants.R_CAPTURE_INTENT_ACTION);
    }

    @Test
    public void testSbiIntentRequestKey() {
        assertEquals("input", RegistrationConstants.SBI_INTENT_REQUEST_KEY);
    }

    @Test
    public void testSbiIntentResponseKey() {
        assertEquals("response", RegistrationConstants.SBI_INTENT_RESPONSE_KEY);
    }

    @Test
    public void testMandatoryLanguagesKey() {
        assertEquals("mosip.mandatory-languages", RegistrationConstants.MANDATORY_LANGUAGES_KEY);
    }

    @Test
    public void testOptionalLanguagesKey() {
        assertEquals("mosip.optional-languages", RegistrationConstants.OPTIONAL_LANGUAGES_KEY);
    }

    @Test
    public void testMaxLanguagesCountKey() {
        assertEquals("mosip.max-languages.count", RegistrationConstants.MAX_LANGUAGES_COUNT_KEY);
    }

    @Test
    public void testMinLanguagesCountKey() {
        assertEquals("mosip.min-languages.count", RegistrationConstants.MIN_LANGUAGES_COUNT_KEY);
    }

    @Test
    public void testLeftSlapThresholdKey() {
        assertEquals("mosip.registration.leftslap_fingerprint_threshold",
                RegistrationConstants.LEFT_SLAP_THRESHOLD_KEY);
    }

    @Test
    public void testRightSlapThresholdKey() {
        assertEquals("mosip.registration.rightslap_fingerprint_threshold",
                RegistrationConstants.RIGHT_SLAP_THRESHOLD_KEY);
    }

    @Test
    public void testThumbsThresholdKey() {
        assertEquals("mosip.registration.thumbs_fingerprint_threshold",
                RegistrationConstants.THUMBS_THRESHOLD_KEY);
    }

    @Test
    public void testIrisThresholdKey() {
        assertEquals("mosip.registration.iris_threshold", RegistrationConstants.IRIS_THRESHOLD_KEY);
    }

    @Test
    public void testFaceThresholdKey() {
        assertEquals("mosip.registration.face_threshold", RegistrationConstants.FACE_THRESHOLD_KEY);
    }

    @Test
    public void testLeftSlapAttemptsKey() {
        assertEquals("mosip.registration.num_of_fingerprint_retries",
                RegistrationConstants.LEFT_SLAP_ATTEMPTS_KEY);
    }

    @Test
    public void testRightSlapAttemptsKey() {
        assertEquals("mosip.registration.num_of_fingerprint_retries",
                RegistrationConstants.RIGHT_SLAP_ATTEMPTS_KEY);
    }

    @Test
    public void testThumbsAttemptsKey() {
        assertEquals("mosip.registration.num_of_fingerprint_retries",
                RegistrationConstants.THUMBS_ATTEMPTS_KEY);
    }

    @Test
    public void testIrisAttemptsKey() {
        assertEquals("mosip.registration.num_of_iris_retries", RegistrationConstants.IRIS_ATTEMPTS_KEY);
    }

    @Test
    public void testFaceAttemptsKey() {
        assertEquals("mosip.registration.num_of_face_retries", RegistrationConstants.FACE_ATTEMPTS_KEY);
    }

    @Test
    public void testServerVersion() {
        assertEquals("mosip.registration.server_version", RegistrationConstants.SERVER_VERSION);
    }

    @Test
    public void testPrimaryLanguage() {
        assertEquals("mosip.primary-language", RegistrationConstants.PRIMARY_LANGUAGE);
    }

    @Test
    public void testSecondaryLanguage() {
        assertEquals("mosip.secondary-language", RegistrationConstants.SECONDARY_LANGUAGE);
    }

    @Test
    public void testConsentScreenTemplateName() {
        assertEquals("mosip.registration.consent-screen-template-name",
                RegistrationConstants.CONSENT_SCREEN_TEMPLATE_NAME);
    }

    @Test
    public void testIndividualBiometricsId() {
        assertEquals("mosip.registration.individual-biometrics-id",
                RegistrationConstants.INDIVIDUAL_BIOMETRICS_ID);
    }

    @Test
    public void testIntroducerBiometricsId() {
        assertEquals("mosip.registration.introducer-biometrics-id",
                RegistrationConstants.INTRODUCER_BIOMETRICS_ID);
    }

    @Test
    public void testInfantAgeGroupName() {
        assertEquals("mosip.registration.infant-agegroup-name",
                RegistrationConstants.INFANT_AGEGROUP_NAME);
    }

    @Test
    public void testAgeGroupConfig() {
        assertEquals("mosip.registration.agegroup-config", RegistrationConstants.AGEGROUP_CONFIG);
    }

    @Test
    public void testAllowedBioAttributes() {
        assertEquals("mosip.registration.allowed-bioattributes",
                RegistrationConstants.ALLOWED_BIO_ATTRIBUTES);
    }

    @Test
    public void testDefaultAppTypeCode() {
        assertEquals("mosip.registration.default-app-type-code",
                RegistrationConstants.DEFAULT_APP_TYPE_CODE);
    }

    @Test
    public void testResponse() {
        assertEquals("response", RegistrationConstants.RESPONSE);
    }

    @Test
    public void testErrors() {
        assertEquals("errors", RegistrationConstants.ERRORS);
    }

    @Test
    public void testOnBoardAuthStatus() {
        assertEquals("authStatus", RegistrationConstants.ON_BOARD_AUTH_STATUS);
    }

    @Test
    public void testUserOnBoardingThresholdNotMetMsg() {
        assertEquals("USER_ON_BOARDING_THRESHOLD_NOT_MET_MSG",
                RegistrationConstants.USER_ON_BOARDING_THRESHOLD_NOT_MET_MSG);
    }

    @Test
    public void testSuccess() {
        assertEquals("Success", RegistrationConstants.SUCCESS);
    }

    @Test
    public void testFinger() {
        assertEquals("FINGER", RegistrationConstants.FINGER);
    }

    @Test
    public void testFace() {
        assertEquals("FACE", RegistrationConstants.FACE);
    }

    @Test
    public void testIris() {
        assertEquals("IRIS", RegistrationConstants.IRIS);
    }

    @Test
    public void testUserOnBoardingSuccessMsg() {
        assertEquals("USER_ONBOARD_SUCCESS", RegistrationConstants.USER_ON_BOARDING_SUCCESS_MSG);
    }

    @Test
    public void testUserOnBoardingErrorResponse() {
        assertEquals("USER_ONBOARD_ERROR", RegistrationConstants.USER_ON_BOARDING_ERROR_RESPONSE);
    }

    @Test
    public void testRegistrationScreen() {
        assertEquals("Registration: %s", RegistrationConstants.REGISTRATION_SCREEN);
    }

    @Test
    public void testAgeGroupConfigAudit() {
        assertEquals("mosip.regproc.packet.classifier.tagging.agegroup.ranges",
                RegistrationConstants.AGE_GROUP_CONFIG);
    }

    @Test
    public void testApplicantTypeMvelScript() {
        assertEquals("mosip.kernel.applicantType.mvel.file",
                RegistrationConstants.APPLICANT_TYPE_MVEL_SCRIPT);
    }

    @Test
    public void testResponseSignaturePublicKeyAppId() {
        assertEquals("SERVER-RESPONSE", RegistrationConstants.RESPONSE_SIGNATURE_PUBLIC_KEY_APP_ID);
    }

    @Test
    public void testResponseSignaturePublicKeyRefId() {
        assertEquals("SIGN-VERIFY", RegistrationConstants.RESPONSE_SIGNATURE_PUBLIC_KEY_REF_ID);
    }

    @Test
    public void testPreRegistrationDummyId() {
        assertEquals("mosip.pre-registration.datasync.fetch.ids",
                RegistrationConstants.PRE_REGISTRATION_DUMMY_ID);
    }

    @Test
    public void testVer() {
        assertEquals("1.0", RegistrationConstants.VER);
    }

    @Test
    public void testPreRegDaysLimit() {
        assertEquals("mosip.registration.pre_reg_no_of_days_limit",
                RegistrationConstants.PRE_REG_DAYS_LIMIT);
    }

    @Test
    public void testUiSchemaSubtypeFullName() {
        assertEquals("name", RegistrationConstants.UI_SCHEMA_SUBTYPE_FULL_NAME);
    }

    @Test
    public void testUiSchemaSubtypeEmail() {
        assertEquals("Email", RegistrationConstants.UI_SCHEMA_SUBTYPE_EMAIL);
    }

    @Test
    public void testUiSchemaSubtypePhone() {
        assertEquals("Phone", RegistrationConstants.UI_SCHEMA_SUBTYPE_PHONE);
    }

    @Test
    public void testSelectedHandles() {
        assertEquals("mosip.registration.default-selected-handle-fields",
                RegistrationConstants.SELECTED_HANDLES);
    }

    @Test
    public void testTemplateImportantGuidelines() {
        assertEquals("mosip.registration.important_guidelines",
                RegistrationConstants.TEMPLATE_IMPORTANT_GUIDELINES);
    }

    @Test
    public void testPreRegPacketLocation() {
        assertEquals("mosip.registration.registration_pre_reg_packet_location",
                RegistrationConstants.PRE_REG_PACKET_LOCATION);
    }

    @Test
    public void testForgotPasswordUrl() {
        assertEquals("mosip.registration.reset_password_url",
                RegistrationConstants.FORGOT_PASSWORD_URL);
    }

    @Test
    public void testLeftLittleFinger() {
        assertEquals("Left LittleFinger", RegistrationConstants.LEFT_LITTLE_FINGER);
    }

    @Test
    public void testLeftRingFinger() {
        assertEquals("Left RingFinger", RegistrationConstants.LEFT_RING_FINGER);
    }

    @Test
    public void testLeftMiddleFinger() {
        assertEquals("Left MiddleFinger", RegistrationConstants.LEFT_MIDDLE_FINGER);
    }

    @Test
    public void testLeftIndexFinger() {
        assertEquals("Left IndexFinger", RegistrationConstants.LEFT_INDEX_FINGER);
    }

    @Test
    public void testRightLittleFinger() {
        assertEquals("Right LittleFinger", RegistrationConstants.RIGHT_LITTLE_FINGER);
    }

    @Test
    public void testRightRingFinger() {
        assertEquals("Right RingFinger", RegistrationConstants.RIGHT_RING_FINGER);
    }

    @Test
    public void testRightMiddleFinger() {
        assertEquals("Right MiddleFinger", RegistrationConstants.RIGHT_MIDDLE_FINGER);
    }

    @Test
    public void testRightIndexFinger() {
        assertEquals("Right IndexFinger", RegistrationConstants.RIGHT_INDEX_FINGER);
    }

    @Test
    public void testLeftThumb() {
        assertEquals("Left Thumb", RegistrationConstants.LEFT_THUMB);
    }

    @Test
    public void testRightThumb() {
        assertEquals("Right Thumb", RegistrationConstants.RIGHT_THUMB);
    }

    @Test
    public void testRight() {
        assertEquals("Right", RegistrationConstants.RIGHT);
    }

    @Test
    public void testLeft() {
        assertEquals("Left", RegistrationConstants.LEFT);
    }
}