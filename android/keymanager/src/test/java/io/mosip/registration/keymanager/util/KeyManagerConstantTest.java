package io.mosip.registration.keymanager.util;

import static org.junit.Assert.*;

import org.junit.Test;

public class KeyManagerConstantTest {

    @Test
    public void testConstants_AreNotNullOrEmpty() {
        assertNotNull(KeyManagerConstant.WHITESPACE);
        assertNotNull(KeyManagerConstant.INVALID_REQUEST);
        assertNotNull(KeyManagerConstant.EMPTY_ATTRIBUTE);
        assertNotNull(KeyManagerConstant.EMPTY_REGEX);
        assertNotNull(KeyManagerConstant.ENCDEC_ALIAS);
        assertNotNull(KeyManagerConstant.SIGNV_ALIAS);
        assertNotNull(KeyManagerConstant.APP_CONF_DIR);
        assertNotNull(KeyManagerConstant.APP_CONF);
        assertNotNull(KeyManagerConstant.APP_CONF_KEY_MACHINE_NAME);
        assertNotNull(KeyManagerConstant.CERTIFICATE_TYPE);
        assertNotNull(KeyManagerConstant.EQUALS_SIGN);
        assertNotNull(KeyManagerConstant.COMMA);
        assertNotNull(KeyManagerConstant.TRUST_ROOT);
        assertNotNull(KeyManagerConstant.TRUST_INTER);
        assertNotNull(KeyManagerConstant.SUCCESS_UPLOAD);
        assertNotNull(KeyManagerConstant.PARTIAL_SUCCESS_UPLOAD);
        assertNotNull(KeyManagerConstant.UPLOAD_FAILED);
        assertNotNull(KeyManagerConstant.KEYALIAS);
        assertNotNull(KeyManagerConstant.CURRENTKEYALIAS);
        assertNotNull(KeyManagerConstant.TRUST_NOT_VERIFIED);
        assertNotNull(KeyManagerConstant.TRUST_NOT_VERIFIED_NO_DOMAIN);
        assertNotNull(KeyManagerConstant.TRUST_NOT_VALID);
        assertNotNull(KeyManagerConstant.TRUST_VALID);
        assertNotNull(KeyManagerConstant.PERIOD);
        assertNotNull(KeyManagerConstant.JWT_HEADER_CERT_KEY);
        assertNotNull(KeyManagerConstant.VALIDATION_SUCCESSFUL);
        assertNotNull(KeyManagerConstant.VALIDATION_FAILED);
    }

    @Test
    public void testBooleanConstants() {
        assertFalse(KeyManagerConstant.DEFAULT_INCLUDES);
    }
}
