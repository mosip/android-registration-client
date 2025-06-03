package io.mosip.registration.keymanager.util;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.Test;

public class KeyManagerConstantTest {

    @Test
    public void testConstants() {
        assertEquals(" ", KeyManagerConstant.WHITESPACE);
        assertEquals("should not be null or empty", KeyManagerConstant.INVALID_REQUEST);
        assertEquals("should not be empty", KeyManagerConstant.EMPTY_ATTRIBUTE);
        assertEquals(".+\\S.*", KeyManagerConstant.EMPTY_REGEX);

        assertEquals("ENDEC", KeyManagerConstant.ENCDEC_ALIAS);
        assertEquals("SIGN", KeyManagerConstant.SIGNV_ALIAS);

        assertEquals(".mosipkeys", KeyManagerConstant.APP_CONF_DIR);
        assertEquals("client.conf", KeyManagerConstant.APP_CONF);
        assertEquals("machineName", KeyManagerConstant.APP_CONF_KEY_MACHINE_NAME);
        assertEquals("X.509", KeyManagerConstant.CERTIFICATE_TYPE);

        assertEquals("", KeyManagerConstant.EMPTY);

        assertEquals("=", KeyManagerConstant.EQUALS_SIGN);
        assertEquals(",", KeyManagerConstant.COMMA);

        assertEquals("TrustRoot", KeyManagerConstant.TRUST_ROOT);
        assertEquals("TrustInter", KeyManagerConstant.TRUST_INTER);
        assertEquals("Upload Success.", KeyManagerConstant.SUCCESS_UPLOAD);
        assertEquals("Partial Upload Success.", KeyManagerConstant.PARTIAL_SUCCESS_UPLOAD);
        assertEquals("Upload Failed.", KeyManagerConstant.UPLOAD_FAILED);

        assertEquals("keyAlias", KeyManagerConstant.KEYALIAS);
        assertEquals("currentKeyAlias", KeyManagerConstant.CURRENTKEYALIAS);

        assertFalse(KeyManagerConstant.DEFAULT_INCLUDES);

        assertEquals("TRUST_NOT_VERIFIED", KeyManagerConstant.TRUST_NOT_VERIFIED);
        assertEquals("TRUST_NOT_VERIFIED_NO_DOMAIN", KeyManagerConstant.TRUST_NOT_VERIFIED_NO_DOMAIN);
        assertEquals("TRUST_CERT_PATH_NOT_VALID", KeyManagerConstant.TRUST_NOT_VALID);
        assertEquals("TRUST_CERT_PATH_VALID", KeyManagerConstant.TRUST_VALID);

        assertEquals("\\.", KeyManagerConstant.PERIOD);

        assertEquals("x5c", KeyManagerConstant.JWT_HEADER_CERT_KEY);

        assertEquals("Validation Successful", KeyManagerConstant.VALIDATION_SUCCESSFUL);
        assertEquals("Validation Failed", KeyManagerConstant.VALIDATION_FAILED);
    }
}
