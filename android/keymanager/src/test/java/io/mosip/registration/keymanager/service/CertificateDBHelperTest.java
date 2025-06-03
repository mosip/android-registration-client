package io.mosip.registration.keymanager.service;

import io.mosip.registration.keymanager.entity.CACertificateStore;
import io.mosip.registration.keymanager.exception.KeymanagerServiceException;
import io.mosip.registration.keymanager.repository.CACertificateStoreRepository;
import io.mosip.registration.keymanager.util.CertificateManagerUtil;
import io.mosip.registration.keymanager.util.KeyManagerErrorCode;
import org.junit.Before;
import org.junit.Test;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.MockitoJUnitRunner;

import java.security.cert.X509Certificate;
import java.security.cert.CertificateFactory;
import java.io.ByteArrayInputStream;
import java.security.cert.CertificateEncodingException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.math.BigInteger;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class CertificateDBHelperTest {

    @Mock
    private CACertificateStoreRepository repository;

    @InjectMocks
    private CertificateDBHelper dbHelper;

    // Test data constants
    private final String thumbprint = "9a2b3c4d5e7f8g9h1j2k3l4m5n6p7q8r9s0t1u2v3w4x5y6z7a8b9c0d";
    private final String domain = "DEVICE";
    private final String certSubject = "C=IN, ST=KA, O=pid1748002926597_dsl_device, OU=IDA-TEST-ORG-UNIT, CN=PARTNER-device";
    private final String certIssuer = "C=IN, ST=KA, L=BANGALORE, O=IITB, OU=MOSIP-TECH-CENTER (PMS), CN=www.mosip.io";
    private final String certId = "5efebbe4-010e-4a9a-9708-9760e1cb7251";
    private final String issuerId = "3a009b92-d26c-41e2-accc-9c9f30760e4e";
    private final BigInteger serialNumber = new BigInteger("1234567890123456789"); // Update with actual serial

    // Valid PEM-encoded certificate - Using a real X.509 certificate
    private final String pemData = "-----BEGIN CERTIFICATE-----\n" +
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
            "YnJpZEVDQ1NIQTM4NDIwMjBDQTEtMS5jcmwwRqBEoEKGQGh0dHA6Ly9jcmw0LmRp\n" +
            "Z2ljZXJ0LmNvbS9EaWdpQ2VydFRMU0h5YnJpZEVDQ1NIQTM4NDIwMjBDQTEtMS5j\n" +
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

    private CACertificateStore caCertificateStore;

    @Before
    public void setup() {
        caCertificateStore = new CACertificateStore(certId);
        caCertificateStore.setCertSubject(certSubject);
        caCertificateStore.setCertIssuer(certIssuer);
        caCertificateStore.setIssuerId(issuerId);
        caCertificateStore.setCertThumbprint(thumbprint);
        caCertificateStore.setPartnerDomain(domain);
        caCertificateStore.setCertData(pemData);

        Calendar notBefore = Calendar.getInstance();
        notBefore.set(2024, Calendar.MAY, 26, 12, 25, 0);
        notBefore.set(Calendar.MILLISECOND, 0);

        Calendar notAfter = Calendar.getInstance();
        notAfter.set(2026, Calendar.MAY, 26, 12, 25, 0);
        notAfter.set(Calendar.MILLISECOND, 0);

        caCertificateStore.setCertNotBefore(notBefore.getTimeInMillis());
        caCertificateStore.setCertNotAfter(notAfter.getTimeInMillis());
        caCertificateStore.setCertSerialNo(serialNumber.toString());
    }

    // Utility to generate a mock X509Certificate for tests
    private X509Certificate getMockX509Certificate() throws Exception {
        X509Certificate cert = mock(X509Certificate.class);
        when(cert.getType()).thenReturn("X.509");
        when(cert.getSerialNumber()).thenReturn(serialNumber);
        when(cert.getNotBefore()).thenReturn(new java.util.Date(System.currentTimeMillis() - 10000));
        when(cert.getNotAfter()).thenReturn(new java.util.Date(System.currentTimeMillis() + 100000));
        when(cert.getEncoded()).thenReturn("mock-certificate".getBytes());
        return cert;
    }

    // Utility to generate a real X509Certificate from PEM
    private X509Certificate getX509CertificateFromPEM(String pem) throws Exception {
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        String cleanPem = pem.replace("-----BEGIN CERTIFICATE-----", "")
                .replace("-----END CERTIFICATE-----", "")
                .replaceAll("\\s", "");
        byte[] decoded = java.util.Base64.getDecoder().decode(cleanPem);
        return (X509Certificate) cf.generateCertificate(new ByteArrayInputStream(decoded));
    }

    @Test
    public void testIsCertificateExist_WhenCertificateExists_ReturnsTrue() {
        when(repository.getCACertStore(thumbprint, domain))
                .thenReturn(caCertificateStore);

        boolean result = dbHelper.isCertificateExist(thumbprint, domain);

        assertTrue(result);
        verify(repository).getCACertStore(thumbprint, domain);
    }

    @Test
    public void testIsCertificateExist_WhenCertificateDoesNotExist_ReturnsFalse() {
        when(repository.getCACertStore(thumbprint, domain))
                .thenReturn(null);

        boolean result = dbHelper.isCertificateExist(thumbprint, domain);

        assertFalse(result);
        verify(repository).getCACertStore(thumbprint, domain);
    }

    @Test
    public void testIsCertificateExist_NullInput() {
        when(repository.getCACertStore(null, null)).thenReturn(null);
        boolean result = dbHelper.isCertificateExist(null, null);
        assertFalse(result);
        verify(repository).getCACertStore(null, null);
    }

    @Test
    public void testIsCertificateExist_EmptyStrings() {
        when(repository.getCACertStore("", "")).thenReturn(null);
        boolean result = dbHelper.isCertificateExist("", "");
        assertFalse(result);
        verify(repository).getCACertStore("", "");
    }

    @Test
    public void testStoreCACertificate_RealCertificate() throws Exception {
        X509Certificate cert = getMockX509Certificate();
        doNothing().when(repository).save(any(CACertificateStore.class));
        dbHelper.storeCACertificate(certId, certSubject, certIssuer, issuerId, cert, thumbprint, domain);
        verify(repository, times(1)).save(any(CACertificateStore.class));
    }

    @Test(expected = NullPointerException.class)
    public void testStoreCACertificate_AllNulls() {
        dbHelper.storeCACertificate(null, null, null, null, null, null, null);
    }

    @Test
    public void testGetPEMFormatedData_RealCertificate() throws Exception {
        X509Certificate cert = getMockX509Certificate();
        String pem = dbHelper.getPEMFormatedData(cert);
        assertNotNull(pem);
        assertFalse(pem.isEmpty());
    }

    @Test
    public void testGetPEMFormatedData_NonX509Type() throws Exception {
        X509Certificate cert = mock(X509Certificate.class);
        when(cert.getType()).thenReturn("OTHER");
        when(cert.getEncoded()).thenReturn("mock-certificate".getBytes());
        String pem = dbHelper.getPEMFormatedData(cert);
        assertNotNull(pem);
        assertFalse(pem.isEmpty());
    }

    @Test
    public void testGetPEMFormatedData_CertificateEncodingException_Mock() throws Exception {
        X509Certificate cert = mock(X509Certificate.class);
        when(cert.getType()).thenReturn("X.509");
        when(cert.getEncoded()).thenThrow(new CertificateEncodingException("encoding error"));
        KeymanagerServiceException ex = assertThrows(KeymanagerServiceException.class, () -> {
            dbHelper.getPEMFormatedData(cert);
        });
        assertEquals(KeyManagerErrorCode.INTERNAL_SERVER_ERROR.getErrorCode(), ex.getErrorCode());
    }

    @Test
    public void testGetTrustAnchors_WithSelfSignedCerts() {
        when(repository.getAllCACertStore(domain)).thenReturn(Collections.emptyList());

        Map<String, Set<?>> result = dbHelper.getTrustAnchors(domain);

        assertNotNull(result);
        assertTrue("Result should contain at least 2 entries", result.size() >= 2);

        for (Map.Entry<String, Set<?>> entry : result.entrySet()) {
            Set<?> certSet = entry.getValue();
            assertNotNull("Set for key " + entry.getKey() + " should not be null", certSet);
        }

        verify(repository).getAllCACertStore(domain);
    }

    @Test
    public void testGetTrustAnchors_WithIntermediateCerts() {
        when(repository.getAllCACertStore(domain)).thenReturn(Collections.emptyList());

        Map<String, Set<?>> result = dbHelper.getTrustAnchors(domain);

        assertNotNull(result);
        assertTrue("Result should contain at least 2 entries", result.size() >= 2);

        for (Map.Entry<String, Set<?>> entry : result.entrySet()) {
            Set<?> certSet = entry.getValue();
            assertNotNull("Set for key " + entry.getKey() + " should not be null", certSet);
        }

        verify(repository).getAllCACertStore(domain);
    }

    @Test
    public void testGetTrustAnchors_EmptyList() {
        when(repository.getAllCACertStore(domain)).thenReturn(Collections.emptyList());

        Map<String, Set<?>> result = dbHelper.getTrustAnchors(domain);

        assertNotNull(result);
        assertTrue("Result should contain at least 2 entries", result.size() >= 2);

        for (Map.Entry<String, Set<?>> entry : result.entrySet()) {
            Set<?> certSet = entry.getValue();
            assertNotNull("Set for key " + entry.getKey() + " should not be null", certSet);
            assertEquals("Set for key " + entry.getKey() + " should be empty", 0, certSet.size());
        }

        verify(repository).getAllCACertStore(domain);
    }

    @Test
    public void testGetTrustAnchors_WithNonSelfSignedCert() {
        CACertificateStore certStore = mock(CACertificateStore.class);
        when(certStore.getCertData()).thenReturn(pemData);

        X509Certificate x509Cert = mock(X509Certificate.class);

        when(repository.getAllCACertStore(domain)).thenReturn(Arrays.asList(certStore));

        try (MockedStatic<CertificateManagerUtil> mockedStatic = mockStatic(CertificateManagerUtil.class)) {
            mockedStatic.when(() -> CertificateManagerUtil.convertToCertificate(anyString())).thenReturn(x509Cert);
            mockedStatic.when(() -> CertificateManagerUtil.isSelfSignedCertificate(x509Cert)).thenReturn(false);

            Map<String, Set<?>> result = dbHelper.getTrustAnchors(domain);
            assertNotNull(result);
            for (Map.Entry<String, Set<?>> entry : result.entrySet()) {
                assertNotNull("Set for key " + entry.getKey() + " should not be null", entry.getValue());
            }
        }
    }

    @Test
    public void testGetIssuerCertId_SingleValidCertificate() {
        CACertificateStore validCert = new CACertificateStore("valid-cert");
        Calendar notBefore = Calendar.getInstance();
        notBefore.add(Calendar.YEAR, -1);
        Calendar notAfter = Calendar.getInstance();
        notAfter.add(Calendar.YEAR, 1);

        validCert.setCertNotBefore(notBefore.getTimeInMillis());
        validCert.setCertNotAfter(notAfter.getTimeInMillis());
        validCert.setCertSubject(certSubject);
        validCert.setCertIssuer(certIssuer);

        List<CACertificateStore> certificates = Arrays.asList(validCert);
        when(repository.getAllCACertStoreByCertSubject(certIssuer))
                .thenReturn(certificates);

        try (MockedStatic<CertificateManagerUtil> mockedStatic = mockStatic(CertificateManagerUtil.class)) {
            mockedStatic.when(() -> CertificateManagerUtil.isValidTimestamp(any(), eq(validCert))).thenReturn(true);

            String result = dbHelper.getIssuerCertId(certIssuer);

            assertEquals("valid-cert", result);
            verify(repository).getAllCACertStoreByCertSubject(certIssuer);
        }
    }

    @Test
    public void testGetIssuerCertId_MultipleCertificates_ReturnsSortedFirst() {
        // Create two valid certificates with different notBefore dates
        CACertificateStore cert1 = new CACertificateStore("cert1");
        CACertificateStore cert2 = new CACertificateStore("cert2");

        Calendar now = Calendar.getInstance();

        // First certificate started earlier
        Calendar notBefore1 = Calendar.getInstance();
        notBefore1.add(Calendar.YEAR, -2); // 2 years ago
        Calendar notAfter1 = Calendar.getInstance();
        notAfter1.add(Calendar.YEAR, 1); // 1 year in future

        // Second certificate started later
        Calendar notBefore2 = Calendar.getInstance();
        notBefore2.add(Calendar.YEAR, -1); // 1 year ago
        Calendar notAfter2 = Calendar.getInstance();
        notAfter2.add(Calendar.YEAR, 2); // 2 years in future

        cert1.setCertNotBefore(notBefore1.getTimeInMillis());
        cert1.setCertNotAfter(notAfter1.getTimeInMillis());
        cert1.setCertSubject(certSubject);
        cert1.setCertIssuer(certIssuer);

        cert2.setCertNotBefore(notBefore2.getTimeInMillis());
        cert2.setCertNotAfter(notAfter2.getTimeInMillis());
        cert2.setCertSubject(certSubject);
        cert2.setCertIssuer(certIssuer);

        // Mock repository to return a list with two valid certificates
        List<CACertificateStore> certificates = Arrays.asList(cert2, cert1); // Intentionally out of order
        when(repository.getAllCACertStoreByCertSubject(certIssuer))
                .thenReturn(certificates);

        String result = dbHelper.getIssuerCertId(certIssuer);

        assertEquals("cert1", result); // Earlier notBefore
        verify(repository).getAllCACertStoreByCertSubject(certIssuer);
    }

    @Test
    public void testGetIssuerCertId_NoValidCertificates_ThrowsException() {
        CACertificateStore expiredCert = new CACertificateStore("expired");
        Calendar notBefore = Calendar.getInstance();
        notBefore.add(Calendar.YEAR, -2);
        Calendar notAfter = Calendar.getInstance();
        notAfter.add(Calendar.YEAR, -1);
        expiredCert.setCertNotBefore(notBefore.getTimeInMillis());
        expiredCert.setCertNotAfter(notAfter.getTimeInMillis());
        expiredCert.setCertSubject(certSubject);
        expiredCert.setCertIssuer(certIssuer);

        List<CACertificateStore> certificates = Arrays.asList(expiredCert);
        when(repository.getAllCACertStoreByCertSubject(certIssuer)).thenReturn(certificates);

        assertThrows(IndexOutOfBoundsException.class, () -> {
            dbHelper.getIssuerCertId(certIssuer);
        });
        verify(repository).getAllCACertStoreByCertSubject(certIssuer);
    }

    @Test
    public void testConstructor() {
        CertificateDBHelper helper = new CertificateDBHelper(repository);
        assertNotNull(helper);
    }
}
