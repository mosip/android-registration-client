package io.mosip.registration_client.ocr;

import org.junit.Test;
import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Collections;

import io.mosip.registration_client.ocr.engine.RemoteOcrProvider;

public class RemoteOcrProviderTest {

    @Test(expected = SecurityException.class)
    public void testInsecureHttpScheme_Rejected() {
        new RemoteOcrProvider(
                "http://insecure-api.mosip.io/ocr",
                10000,
                Collections.singletonList("insecure-api.mosip.io"),
                () -> "test-token"
        );
    }

    @Test(expected = SecurityException.class)
    public void testUnallowlistedHost_Rejected() {
        new RemoteOcrProvider(
                "https://malicious-server.com/ocr",
                10000,
                Collections.singletonList("api.mosip.io"),
                () -> "test-token"
        );
    }

    @Test
    public void testAllowlistedHttpsHost_Accepted() {
        RemoteOcrProvider provider = new RemoteOcrProvider(
                "https://api.mosip.io/ocr",
                10000,
                Arrays.asList("api.mosip.io", "ocr.mosip.net"),
                () -> "valid-token"
        );
        provider.validatePreconditions();
        assertTrue(provider.isHostAllowlisted("api.mosip.io"));
        assertTrue(provider.isHostAllowlisted("subdomain.api.mosip.io"));
        assertFalse(provider.isHostAllowlisted("evil.com"));
    }

    @Test(expected = SecurityException.class)
    public void testMissingAuthToken_RejectedOnPreconditions() {
        RemoteOcrProvider provider = new RemoteOcrProvider(
                "https://api.mosip.io/ocr",
                10000,
                Collections.singletonList("api.mosip.io"),
                () -> null
        );
        provider.validatePreconditions();
    }
}
