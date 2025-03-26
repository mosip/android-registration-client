package io.mosip.registration.keymanager.service;

import android.content.Context;
import android.content.res.AssetManager;
import io.mosip.registration.keymanager.dto.*;
import io.mosip.registration.keymanager.exception.KeymanagerServiceException;
import io.mosip.registration.keymanager.repository.KeyStoreRepository;
import io.mosip.registration.keymanager.util.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class CertificateManagerServiceImplTest {

    @Mock
    private Context context;

    @Mock
    private AssetManager assetManager;

    @Mock
    private CertificateDBHelper certificateDBHelper;

    @Mock
    private KeyStoreRepository keyStoreRepository;

    private CertificateManagerServiceImpl certificateManagerService;

    private X509Certificate x509Certificate;

    private static final String VALID_CERT_DATA = "-----BEGIN CERTIFICATE-----\n" +
            "MIIFajCCBPCgAwIBAgIQBRiaVOvox+kD4KsNklVF3jAKBggqhkjOPQQDAzBWMQsw\n" +
            "CQYDVQQGEwJVUzEVMBMGA1UEChMMRGlnaUNlcnQgSW5jMTAwLgYDVQQDEydEaWdp\n" +
            "Q2VydCBUTFMgSHlicmlkIEVDQyBTSEEzODQgMjAyMCBDQTEwHhcNMjIwMzE1MDAw\n" +
            "MDAwWhcNMjMwMzE1MjM1OTU5WjBmMQswCQYDVQQGEwJVUzETMBEGA1UECBMKQ2Fs\n" +
            "aWZvcm5pYTEWMBQGA1UEBxMNU2FuIEZyYW5jaXNjbzEVMBMGA1UEChMMR2l0SHVi\n" +
            "LCBJbmMuMRMwEQYDVQQDEwpnaXRodWIuY29tMFkwEwYHKoZIzj0CAQYIKoZIzj0D\n" +
            "AQcDQgAESrCTcYUh7GI/y3TARsjnANwnSjJLitVRgwgRI1JlxZ1kdZQQn5ltP3v7\n" +
            "KTtYuDdUeEu3PRx3fpDdu2cjMlyA0aOCA44wggOKMB8GA1UdIwQYMBaAFAq8CCkX\n" +
            "jKU5bXoOzjPHLrPt+8N6MB0GA1UdDgQWBBR4qnLGcWloFLVZsZ6LbitAh0I7HjAl\n" +
            "BgNVHREEHjAcggpnaXRodWIuY29tgg53d3cuZ2l0aHViLmNvbTAOBgNVHQ8BAf8E\n" +
            "BAMCB4AwHQYDVR0lBBYwFAYIKwYBBQUHAwEGCCsGAQUFBwMCMIGbBgNVHR8EgZMw\n" +
            "gZAwRqBEoEKGQGh0dHA6Ly9jcmwzLmRpZ2ljZXJ0LmNvbS9EaWdpQ2VydFRMU0h5\n" +
            "bnJpZEVDQ1NIQTM4NDIwMjBDQTEtMS5jcmwwRqBEoEKGQGh0dHA6Ly9jcmw0LmRp\n" +
            "Z2ljZXJ0LmNvbS9EaWdpQ2VydFRMU0h5YnJpZEVDQ1NIQTM8NDIwMjBDQTEtMS5j\n" +
            "cmwwPgYDVR0gBDcwNTAzBgZngQwBAgIwKTAnBggrBgEFBQcCARYbaHR0cDovL3d3\n" +
            "dy5kaWdpY2VydC5jb20vQ1BTMIGFBggrBgEFBQcBAQR5MHcwJAYIKwYBBQUHMAGG\n" +
            "GGh0dHA6Ly9vY3NwLmRpZ2ljZXJ0LmNvbTBPBggrBgEFBQcwAoZDaHR0cDovL2Nh\n" +
            "Y2VydHMuZGlnaWNlcnQuY29tL0RpZ2lDZXJ0VExTSHlicmlkRUNDU0hBMzg0MjAy\n" +
            "MENBMS0xLmNydDAJBgNVHRMEAjAAMIIBfwYKKwYBBAHWeQIEAgSCAW8EggFrAWkA\n" +
            "dgCt9776fP8QyIudPZwePhhqtGcpXc+xDCTKhYY069yCigAAAX+Oi8SRAAAEAwBH\n" +
            "MEUCIAR9cNnvYkZeKs9JElpeXwztYB2yLhtc8bB0rY2ke98nAiEAjiML8HZ7aeVE\n" +
            "P/DkUltwIS4c73VVrG9JguoRrII7gWMAdwA1zxkbv7FsV78PrUxtQsu7ticgJlHq\n" +
            "P+Eq76gDwzvWTAAAAX+Oi8R7AAAEAwBIMEYCIQDNckqvBhup7GpANMf0WPueytL8\n" +
            "u/PBaIAObzNZeNMpOgIhAMjfEtE6AJ2fTjYCFh/BNVKk1mkTwBTavJlGmWomQyaB\n" +
            "AHYAs3N3B+GEUPhjhtYFqdwRCUp5LbFnDAuH3PADDnk2pZoAAAF/jovErAAABAMA\n" +
            "RzBFAiEA9Uj5Ed/XjQpj/MxQRQjzG0UFQLmgWlc73nnt3CJ7vskCICqHfBKlDz7R\n" +
            "EHdV5Vk8bLMBW1Q6S7Ga2SbFuoVXs6zFMAoGCCqGSM49BAMDA2gAMGUCMCiVhqft\n" +
            "7L/stBmv1XqSRNfE/jG/AqKIbmjGTocNbuQ7kt1Cs7kRg+b3b3C9Ipu5FQIxAM7c\n" +
            "tGKrYDGt0pH8iF6rzbp9Q4HQXMZXkNxg+brjWxnaOVGTDNwNH7048+s/hT9bUQ==\n" +
            "-----END CERTIFICATE-----\n";

    private static final String INVALID_CERT_DATA = "-----BEGIN CERTIFICATE-----\n" +
            "MIIFajCCBPCgAwIBAgIQBRiaVOvox+kD4KsNklVF3jAKBggqhkjOPQQDAzBWMQsw\n" +
            "tGKrYDGt0pH8iF6rzbp9Q4HQXMZXkNxg+brjWxnaOVGTDNwNH7048+s/hT9bUQ==\n" +
            "-----END CERTIFICATE-----\n";

    @Before
    public void setUp() throws Exception {
        when(context.getAssets()).thenReturn(assetManager);
        String allowedDomains = "example.com,mosip.net";
        InputStream inputStream = new ByteArrayInputStream(allowedDomains.getBytes());
        when(assetManager.open(anyString())).thenReturn(inputStream);

        certificateManagerService = new CertificateManagerServiceImpl(context, certificateDBHelper, keyStoreRepository);
        x509Certificate = (X509Certificate) CertificateManagerUtil.convertToCertificate(VALID_CERT_DATA);
    }

    @Test(expected = KeymanagerServiceException.class)
    public void testVerifyCertificateTrust_InvalidCertData() {
        CertificateTrustRequestDto requestDto = new CertificateTrustRequestDto();
        requestDto.setCertificateData(INVALID_CERT_DATA);
        requestDto.setPartnerDomain("DEVICE");

        certificateManagerService.verifyCertificateTrust(requestDto);
    }

    @Test(expected = KeymanagerServiceException.class)
    public void testUploadOtherDomainCertificate_InvalidData() {
        CertificateRequestDto requestDto = new CertificateRequestDto();
        requestDto.setCertificateData(INVALID_CERT_DATA);
        requestDto.setApplicationId("APP123");
        requestDto.setReferenceId("REF123");

        certificateManagerService.uploadOtherDomainCertificate(requestDto);
    }

    @Test(expected = KeymanagerServiceException.class)
    public void testUploadOtherDomainCertificate_InvalidDates() {
        CertificateRequestDto requestDto = new CertificateRequestDto();
        requestDto.setCertificateData(VALID_CERT_DATA);
        requestDto.setApplicationId("APP123");
        requestDto.setReferenceId("REF123");

        certificateManagerService.uploadOtherDomainCertificate(requestDto);
    }

    @Test
    public void testGetCertificate_Success() {
        String appId = "APP123";
        String refId = "REF123";
        when(keyStoreRepository.getCertificateData(refId)).thenReturn(VALID_CERT_DATA);

        String result = certificateManagerService.getCertificate(appId, refId);

        assertEquals(VALID_CERT_DATA, result);
        verify(keyStoreRepository, times(1)).getCertificateData(refId);
    }

    @Test
    public void testIsValidReferenceId_Valid() {
        assertTrue(certificateManagerService.isValidReferenceId("REF123"));
    }

    @Test
    public void testIsValidReferenceId_Invalid() {
        assertFalse(certificateManagerService.isValidReferenceId(null));
        assertFalse(certificateManagerService.isValidReferenceId(""));
        assertFalse(certificateManagerService.isValidReferenceId("   "));
    }

    @Test
    public void testIsValidApplicationId_Valid() {
        assertTrue(certificateManagerService.isValidApplicationId("APP123"));
    }

    @Test
    public void testIsValidApplicationId_Invalid() {
        assertFalse(certificateManagerService.isValidApplicationId(null));
        assertFalse(certificateManagerService.isValidApplicationId(""));
        assertFalse(certificateManagerService.isValidApplicationId("   "));
    }
}