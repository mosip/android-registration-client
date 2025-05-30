package io.mosip.registration.packetmanager.util;

import org.junit.Test;

import static org.junit.Assert.*;

public class PacketManagerConstantTest {

    @Test
    public void testAllConstants() {
        assertEquals("ID", PacketManagerConstant.IDENTITY_FILENAME);
        assertEquals("ID.json", PacketManagerConstant.IDENTITY_FILENAME_WITH_EXT);
        assertEquals("audit", PacketManagerConstant.AUDIT_FILENAME);
        assertEquals("audit.json", PacketManagerConstant.AUDIT_FILENAME_WITH_EXT);
        assertEquals("packet_meta_info.json", PacketManagerConstant.PACKET_META_FILENAME);
        assertEquals("packet_data_hash.txt", PacketManagerConstant.PACKET_DATA_HASH_FILENAME);
        assertEquals("packet_operations_hash.txt", PacketManagerConstant.PACKET_OPER_HASH_FILENAME);
        assertEquals("cbeff", PacketManagerConstant.CBEFF_FILE_FORMAT);
        assertEquals(1.0, PacketManagerConstant.CBEFF_VERSION, 0.0);
        assertEquals("cbeff.xsd", PacketManagerConstant.CBEFF_SCHEMA_FILE_PATH);
        assertEquals("Mosip", PacketManagerConstant.CBEFF_DEFAULT_FORMAT_ORG);
        assertEquals("257", PacketManagerConstant.CBEFF_DEFAULT_FORMAT_TYPE);
        assertEquals("HMAC", PacketManagerConstant.CBEFF_DEFAULT_ALG_ORG);
        assertEquals("SHA-256", PacketManagerConstant.CBEFF_DEFAULT_ALG_TYPE);
        assertEquals("%s_bio_CBEFF", PacketManagerConstant.CBEFF_FILENAME);
        assertEquals("%s_bio_CBEFF.xml", PacketManagerConstant.CBEFF_FILENAME_WITH_EXT);
        assertEquals(".xml", PacketManagerConstant.CBEFF_EXT);

        assertEquals(9, PacketManagerConstant.FORMAT_TYPE_IRIS);
        assertEquals(8, PacketManagerConstant.FORMAT_TYPE_FACE);
        assertEquals(7, PacketManagerConstant.FORMAT_TYPE_FINGER);

        assertEquals("demographicSequence", PacketManagerConstant.DEMOGRAPHIC_SEQ);
        assertEquals("biometricSequence", PacketManagerConstant.BIOMETRIC_SEQ);
        assertEquals("otherFiles", PacketManagerConstant.OPERATIONS_SEQ);

        assertEquals("officer_bio_cbeff", PacketManagerConstant.OFFICER);
        assertEquals("supervisor_bio_cbeff", PacketManagerConstant.SUPERVISOR);

        assertEquals("properties", PacketManagerConstant.PROPERTIES);
        assertEquals("identity", PacketManagerConstant.IDENTITY);

        assertEquals("%s_%s.zip", PacketManagerConstant.SUBPACKET_ZIP_FILE_NAME);

        assertEquals("IDSchemaVersion", PacketManagerConstant.IDSCHEMA_VERSION);
        assertEquals("registrationId", PacketManagerConstant.REGISTRATIONID);

        assertEquals("id", PacketManagerConstant.SCHEMA_ID);
        assertEquals("type", PacketManagerConstant.SCHEMA_TYPE);
        assertEquals("$ref", PacketManagerConstant.SCHEMA_REF);
        assertEquals("fieldCategory", PacketManagerConstant.SCHEMA_CATEGORY);
        assertEquals("schemaVersion", PacketManagerConstant.SCHEMA_VERSION_QUERY_PARAM);

        assertEquals("#/definitions/biometricsType", PacketManagerConstant.BIOMETRICS_TYPE);
        assertEquals("#/definitions/documentType", PacketManagerConstant.DOCUMENTS_TYPE);

        assertEquals("biometricsType", PacketManagerConstant.BIOMETRICS_DATATYPE);
        assertEquals("documentType", PacketManagerConstant.DOCUMENTS_DATATYPE);

        assertEquals("FINGERPRINT_SLAB_LEFT", PacketManagerConstant.FINGERPRINT_SLAB_LEFT);
        assertEquals("FINGERPRINT_SLAB_RIGHT", PacketManagerConstant.FINGERPRINT_SLAB_RIGHT);
        assertEquals("FINGERPRINT_SLAB_THUMBS", PacketManagerConstant.FINGERPRINT_SLAB_THUMBS);
        assertEquals("IRIS_DOUBLE", PacketManagerConstant.IRIS_DOUBLE);
        assertEquals("FACE_FULL FACE", PacketManagerConstant.FACE_FULL_FACE);

        assertEquals("creationDate", PacketManagerConstant.META_CREATION_DATE);
        assertEquals("Registration Client Version Number", PacketManagerConstant.META_CLIENT_VERSION);
        assertEquals("geoLocLatitude", PacketManagerConstant.META_LATITUDE);
        assertEquals("geoLoclongitude", PacketManagerConstant.META_LONGITUDE);
        assertEquals("registrationType", PacketManagerConstant.META_REGISTRATION_TYPE);
        assertEquals("preRegistrationId", PacketManagerConstant.META_PRE_REGISTRATION_ID);
        assertEquals("machineId", PacketManagerConstant.META_MACHINE_ID);
        assertEquals("dongleId", PacketManagerConstant.META_DONGLE_ID);
        assertEquals("keyIndex", PacketManagerConstant.META_KEYINDEX);
        assertEquals("centerId", PacketManagerConstant.META_CENTER_ID);
        assertEquals("consentOfApplicant", PacketManagerConstant.META_APPLICANT_CONSENT);
        assertEquals("officerId", PacketManagerConstant.META_OFFICER_ID);
        assertEquals("officerBiometricFileName", PacketManagerConstant.META_OFFICER_BIOMETRIC_FILE);
        assertEquals("supervisorId", PacketManagerConstant.META_SUPERVISOR_ID);
        assertEquals("supervisorBiometricFileName", PacketManagerConstant.META_SUPERVISOR_BIOMETRIC_FILE);
        assertEquals("supervisorPassword", PacketManagerConstant.META_SUPERVISOR_PWD);
        assertEquals("officerPassword", PacketManagerConstant.META_OFFICER_PWD);
        assertEquals("supervisorPIN", PacketManagerConstant.META_SUPERVISOR_PIN);
        assertEquals("officerPIN", PacketManagerConstant.META_OFFICER_PIN);
        assertEquals("supervisorOTPAuthentication", PacketManagerConstant.META_SUPERVISOR_OTP);
        assertEquals("officerOTPAuthentication", PacketManagerConstant.META_OFFICER_OTP);
        assertEquals("registrationId", PacketManagerConstant.META_REGISTRATION_ID);
        assertEquals("applicationId", PacketManagerConstant.META_APPLICATION_ID);

        assertEquals("schema", PacketManagerConstant.SCHEMA);
        assertEquals("fieldCategory", PacketManagerConstant.FIELDCATEGORY);

        assertEquals("pvt", PacketManagerConstant.PVT);
        assertEquals("label", PacketManagerConstant.LABEL);
        assertEquals("value", PacketManagerConstant.VALUE);
        assertEquals("type", PacketManagerConstant.TYPE);
        assertEquals("format", PacketManagerConstant.FORMAT);

        assertEquals("IDSCHEMA", PacketManagerConstant.IDSCHEMA_URL);
        assertEquals("schemaJson", PacketManagerConstant.SCHEMA_JSON);
        assertEquals("response", PacketManagerConstant.RESPONSE);

        assertEquals("operationsData", PacketManagerConstant.META_INFO_OPERATIONS_DATA);
        assertEquals("metaInfo", PacketManagerConstant.METAINFO);
        assertEquals("audits", PacketManagerConstant.AUDITS);

        assertEquals("officerBiometricFileName", PacketManagerConstant.OFFICER_BIOMETRIC);
        assertEquals("supervisorBiometricFileName", PacketManagerConstant.SUPERVISOR_BIOMETRIC);

        assertEquals("authenticationBiometricFileName", PacketManagerConstant.META_AUTH_BIO_FILENAME);

        assertEquals("id", PacketManagerConstant.ID);
        assertEquals("packetname", PacketManagerConstant.PACKET_NAME);
        assertEquals("source", PacketManagerConstant.SOURCE);
        assertEquals("process", PacketManagerConstant.PROCESS);
        assertEquals("schemaversion", PacketManagerConstant.SCHEMA_VERSION);
        assertEquals("signature", PacketManagerConstant.SIGNATURE);
        assertEquals("encryptedhash", PacketManagerConstant.ENCRYPTED_HASH);
        assertEquals("providername", PacketManagerConstant.PROVIDER_NAME);
        assertEquals("providerversion", PacketManagerConstant.PROVIDER_VERSION);
        assertEquals("creationdate", PacketManagerConstant.CREATION_DATE);
        assertEquals("bioValue", PacketManagerConstant.BIOVALUE_KEY);
        assertEquals("\"<bioValue>\"", PacketManagerConstant.BIOVALUE_PLACEHOLDER);

        assertEquals("EXCEPTION", PacketManagerConstant.OTHER_KEY_EXCEPTION);
        assertEquals("RETRIES", PacketManagerConstant.OTHER_KEY_RETRIES);
        assertEquals("FORCE_CAPTURED", PacketManagerConstant.OTHER_KEY_FORCE_CAPTURED);
        assertEquals("SDK_SCORE", PacketManagerConstant.OTHER_KEY_SDK_SCORE);
        assertEquals("CONFIGURED", PacketManagerConstant.OTHER_KEY_CONFIGURED);
        assertEquals("PAYLOAD", PacketManagerConstant.OTHER_KEY_PAYLOAD);
        assertEquals("SPEC_VERSION", PacketManagerConstant.OTHER_KEY_SPEC_VERSION);
        assertEquals("Left Slab", PacketManagerConstant.LEFT_SLAB);
        assertEquals("Right Slab", PacketManagerConstant.RIGHT_SLAB);
        assertEquals("Thumbs", PacketManagerConstant.THUMBS);
        assertEquals("Iris", PacketManagerConstant.IRIS);
        assertEquals("Packet keeper exception occurred.", PacketManagerConstant.PACKET_KEEPER_EXCEPTION_MSG);
    }
}
