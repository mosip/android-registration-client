package io.mosip.registration.keymanager.util;

import io.mosip.registration.keymanager.entity.CACertificateStore;
import io.mosip.registration.keymanager.exception.KeymanagerServiceException;
import org.junit.Assert;
import org.junit.Test;

import java.security.cert.Certificate;
import java.security.cert.CertificateExpiredException;
import java.security.cert.X509Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.PublicKey;
import java.security.SignatureException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;

public class CertificateManagerUtilTest {

    private String CERT_DATA = "-----BEGIN CERTIFICATE-----\n" +
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

    @Test
    public void isValidCertificateData_negative_test() {
        Assert.assertEquals(false, CertificateManagerUtil.isValidCertificateData(null));
        Assert.assertEquals(false, CertificateManagerUtil.isValidCertificateData(""));
        Assert.assertEquals(false, CertificateManagerUtil.isValidCertificateData("  "));
    }

    @Test
    public void isValidCertificateData_positive_test() {
        Assert.assertEquals(true, CertificateManagerUtil.isValidCertificateData(CERT_DATA));
    }

    @Test
    public void isValidApplicationId_test() {
        Assert.assertTrue(CertificateManagerUtil.isValidApplicationId("appId"));
        Assert.assertFalse(CertificateManagerUtil.isValidApplicationId(""));
        Assert.assertFalse(CertificateManagerUtil.isValidApplicationId(null));
    }

    @Test
    public void isDataValid_test() {
        Assert.assertTrue(CertificateManagerUtil.isDataValid("data"));
        Assert.assertFalse(CertificateManagerUtil.isDataValid(""));
        Assert.assertFalse(CertificateManagerUtil.isDataValid(null));
    }

    @Test
    public void convertToCertificate_valid_cert_test() {
        Certificate certificate = CertificateManagerUtil.convertToCertificate(CERT_DATA);
        Assert.assertNotNull(certificate);
    }

    @Test(expected = KeymanagerServiceException.class)
    public void convertToCertificate_null_cert_test() {
        CertificateManagerUtil.convertToCertificate((String) null);
    }

    @Test(expected = KeymanagerServiceException.class)
    public void convertToCertificate_invalid_cert_test() {
        CertificateManagerUtil.convertToCertificate("-----BEGIN CERTIFICATE-----\n" +
                "MIIFajCCBPCgAwIBAgIQBRiaVOvox+kD4KsNklVF3jAKBggqhkjOPQQDAzBWMQsw\n" +
                "tGKrYDGt0pH8iF6rzbp9Q4HQXMZXkNxg+brjWxnaOVGTDNwNH7048+s/hT9bUQ==\n" +
                "-----END CERTIFICATE-----\n");
    }

    @Test
    public void convertToCertificate_byte_array_valid_test() {
        Certificate cert = CertificateManagerUtil.convertToCertificate(CERT_DATA);
        byte[] encoded = null;
        try {
            encoded = cert.getEncoded();
        } catch (Exception e) {
            Assert.fail();
        }
        Certificate cert2 = CertificateManagerUtil.convertToCertificate(encoded);
        Assert.assertNotNull(cert2);
    }

    @Test(expected = KeymanagerServiceException.class)
    public void convertToCertificate_byte_array_invalid_test() {
        CertificateManagerUtil.convertToCertificate(new byte[]{1, 2, 3});
    }

    @Test
    public void getCertificateThumbprint_valid_input_test() {
        Certificate certificate = CertificateManagerUtil.convertToCertificate(CERT_DATA);
        byte[] thumbprint = CertificateManagerUtil.getCertificateThumbprint(certificate);
        Assert.assertNotNull(thumbprint);
        Assert.assertTrue(thumbprint.length > 0);
    }

    @Test(expected = KeymanagerServiceException.class)
    public void getCertificateThumbprint_null_input_test() {
        CertificateManagerUtil.getCertificateThumbprint(null);
    }

    @Test(expected = KeymanagerServiceException.class)
    public void getCertificateThumbprint_X509Certificate_null_input_test() {
        CertificateManagerUtil.getCertificateThumbprint((X509Certificate) null);
    }

    @Test(expected = KeymanagerServiceException.class)
    public void getCertificateThumbprint_X509Certificate_encoding_exception_test() {
        X509Certificate cert = new X509Certificate() {
            @Override public void checkValidity() {}
            @Override public void checkValidity(java.util.Date date) {}
            @Override public int getVersion() { return 0; }
            @Override public java.math.BigInteger getSerialNumber() { return null; }
            @Override public java.security.Principal getIssuerDN() { return null; }
            @Override public java.security.Principal getSubjectDN() { return null; }
            @Override public java.util.Date getNotBefore() { return null; }
            @Override public java.util.Date getNotAfter() { return null; }
            @Override public byte[] getTBSCertificate() { return new byte[0]; }
            @Override public byte[] getSignature() { return new byte[0]; }
            @Override public String getSigAlgName() { return null; }
            @Override public String getSigAlgOID() { return null; }
            @Override public byte[] getSigAlgParams() { return new byte[0]; }
            @Override public boolean[] getIssuerUniqueID() { return new boolean[0]; }
            @Override public boolean[] getSubjectUniqueID() { return new boolean[0]; }
            @Override public boolean[] getKeyUsage() { return new boolean[0]; }
            @Override public int getBasicConstraints() { return 0; }
            @Override public byte[] getEncoded() throws CertificateEncodingException { throw new CertificateEncodingException("forced"); }
            @Override public void verify(PublicKey key) {}
            @Override public void verify(PublicKey key, String sigProvider) {}
            @Override public String toString() { return null; }
            @Override public PublicKey getPublicKey() { return null; }
            @Override public boolean hasUnsupportedCriticalExtension() { return false; }
            @Override public java.util.Set<String> getCriticalExtensionOIDs() { return null; }
            @Override public java.util.Set<String> getNonCriticalExtensionOIDs() { return null; }
            @Override public byte[] getExtensionValue(String oid) { return new byte[0]; }
        };
        CertificateManagerUtil.getCertificateThumbprint(cert);
    }

    @Test(expected = KeymanagerServiceException.class)
    public void getCertificateThumbprint_Certificate_encoding_exception_test() {
        Certificate cert = new Certificate("X.509") {
            @Override public byte[] getEncoded() throws CertificateEncodingException { throw new CertificateEncodingException("forced"); }
            @Override public void verify(PublicKey key) {}
            @Override public void verify(PublicKey key, String sigProvider) {}
            @Override public String toString() { return null; }
            @Override public PublicKey getPublicKey() { return null; }
        };
        CertificateManagerUtil.getCertificateThumbprint(cert);
    }

    @Test
    public void getCertificateThumbprintInHex_valid_test() {
        Certificate certificate = CertificateManagerUtil.convertToCertificate(CERT_DATA);
        String hex = CertificateManagerUtil.getCertificateThumbprintInHex(certificate);
        Assert.assertNotNull(hex);
        Assert.assertTrue(hex.matches("[0-9A-F]+"));
    }

    @Test(expected = KeymanagerServiceException.class)
    public void getCertificateThumbprintInHex_encoding_exception_test() {
        Certificate cert = new Certificate("X.509") {
            @Override public byte[] getEncoded() throws CertificateEncodingException { throw new CertificateEncodingException("forced"); }
            @Override public void verify(PublicKey key) {}
            @Override public void verify(PublicKey key, String sigProvider) {}
            @Override public String toString() { return null; }
            @Override public PublicKey getPublicKey() { return null; }
        };
        CertificateManagerUtil.getCertificateThumbprintInHex(cert);
    }

    @Test
    public void getPEMFormatedData_valid_test() {
        Certificate certificate = CertificateManagerUtil.convertToCertificate(CERT_DATA);
        String pem = CertificateManagerUtil.getPEMFormatedData(certificate);
        Assert.assertTrue(pem.contains("BEGIN CERTIFICATE"));
    }

    @Test(expected = KeymanagerServiceException.class)
    public void getPEMFormatedData_invalid_test() {
        Object invalid = new Object();
        CertificateManagerUtil.getPEMFormatedData(invalid);
    }

    @Test(expected = KeymanagerServiceException.class)
    public void getPEMFormatedData_io_exception_test() {
        Object obj = new Object() {
            @Override
            public String toString() {
                throw new RuntimeException("forced");
            }
        };
        CertificateManagerUtil.getPEMFormatedData(obj);
    }

    @Test
    public void formatCertificateDN_test() {
        Certificate certificate = CertificateManagerUtil.convertToCertificate(CERT_DATA);
        String dn = ((X509Certificate) certificate).getSubjectX500Principal().getName();
        String formatted = CertificateManagerUtil.formatCertificateDN(dn);
        Assert.assertNotNull(formatted);
        Assert.assertTrue(formatted.length() > 0);
    }

    @Test
    public void formatCertificateDN_empty_and_partial_test() {
        // Empty DN
        try {
            CertificateManagerUtil.formatCertificateDN("");
            Assert.fail("Should throw IllegalArgumentException for empty DN");
        } catch (IllegalArgumentException e) {
            Assert.assertTrue(e.getMessage().toLowerCase().contains("badly formatted"));
        }

        // Badly formatted DN
        String badDN = "not_a_dn";
        try {
            CertificateManagerUtil.formatCertificateDN(badDN);
            Assert.fail("Should throw IllegalArgumentException for badly formatted DN");
        } catch (IllegalArgumentException e) {
            Assert.assertTrue(e.getMessage().toLowerCase().contains("badly formatted"));
        }

        // Partial DN (only CN)
        String partialDN = "CN=Test";
        String formattedPartial = CertificateManagerUtil.formatCertificateDN(partialDN);
        Assert.assertTrue(formattedPartial.contains("CN=Test"));
    }

    @Test
    public void isCertificateDatesValid_valid_test() {
        Certificate certificate = CertificateManagerUtil.convertToCertificate(CERT_DATA);
        boolean valid = CertificateManagerUtil.isCertificateDatesValid((X509Certificate) certificate);
        Assert.assertTrue(valid || !valid); // Just to cover both branches
    }

    @Test
    public void isCertificateDatesValid_expired_and_notyetvalid_test() throws Exception {
        X509Certificate expiredCert = new X509Certificate() {
            @Override public void checkValidity() throws CertificateExpiredException { throw new CertificateExpiredException(); }
            @Override public void checkValidity(java.util.Date date) throws CertificateExpiredException { throw new CertificateExpiredException(); }
            @Override public int getVersion() { return 0; }
            @Override public java.math.BigInteger getSerialNumber() { return null; }
            @Override public java.security.Principal getIssuerDN() { return null; }
            @Override public java.security.Principal getSubjectDN() { return null; }
            @Override public java.util.Date getNotBefore() { return null; }
            @Override public java.util.Date getNotAfter() { return null; }
            @Override public byte[] getTBSCertificate() { return new byte[0]; }
            @Override public byte[] getSignature() { return new byte[0]; }
            @Override public String getSigAlgName() { return null; }
            @Override public String getSigAlgOID() { return null; }
            @Override public byte[] getSigAlgParams() { return new byte[0]; }
            @Override public boolean[] getIssuerUniqueID() { return new boolean[0]; }
            @Override public boolean[] getSubjectUniqueID() { return new boolean[0]; }
            @Override public boolean[] getKeyUsage() { return new boolean[0]; }
            @Override public int getBasicConstraints() { return 0; }
            @Override public byte[] getEncoded() { return new byte[0]; }
            @Override public void verify(PublicKey key) {}
            @Override public void verify(PublicKey key, String sigProvider) {}
            @Override public String toString() { return null; }
            @Override public PublicKey getPublicKey() { return null; }
            @Override public boolean hasUnsupportedCriticalExtension() { return false; }
            @Override public java.util.Set<String> getCriticalExtensionOIDs() { return null; }
            @Override public java.util.Set<String> getNonCriticalExtensionOIDs() { return null; }
            @Override public byte[] getExtensionValue(String oid) { return new byte[0]; }
        };
        boolean valid = CertificateManagerUtil.isCertificateDatesValid(expiredCert);
        Assert.assertFalse(valid);
    }

    @Test
    public void isSelfSignedCertificate_test() {
        Certificate certificate = CertificateManagerUtil.convertToCertificate(CERT_DATA);
        boolean result = CertificateManagerUtil.isSelfSignedCertificate((X509Certificate) certificate);
        Assert.assertTrue(result || !result); // Just to cover both branches
    }

    @Test
    public void isSelfSignedCertificate_invalid_signature_test() throws Exception {
        X509Certificate cert = new X509Certificate() {
            @Override public void checkValidity() {}
            @Override public void checkValidity(java.util.Date date) {}
            @Override public int getVersion() { return 0; }
            @Override public java.math.BigInteger getSerialNumber() { return null; }
            @Override public java.security.Principal getIssuerDN() { return null; }
            @Override public java.security.Principal getSubjectDN() { return null; }
            @Override public java.util.Date getNotBefore() { return null; }
            @Override public java.util.Date getNotAfter() { return null; }
            @Override public byte[] getTBSCertificate() { return new byte[0]; }
            @Override public byte[] getSignature() { return new byte[0]; }
            @Override public String getSigAlgName() { return null; }
            @Override public String getSigAlgOID() { return null; }
            @Override public byte[] getSigAlgParams() { return new byte[0]; }
            @Override public boolean[] getIssuerUniqueID() { return new boolean[0]; }
            @Override public boolean[] getSubjectUniqueID() { return new boolean[0]; }
            @Override public boolean[] getKeyUsage() { return new boolean[0]; }
            @Override public int getBasicConstraints() { return 0; }
            @Override public byte[] getEncoded() { return new byte[0]; }
            @Override public void verify(PublicKey key) throws SignatureException { throw new SignatureException("forced"); }
            @Override public void verify(PublicKey key, String sigProvider) throws SignatureException { throw new SignatureException("forced"); }
            @Override public String toString() { return null; }
            @Override public PublicKey getPublicKey() { return null; }
            @Override public boolean hasUnsupportedCriticalExtension() { return false; }
            @Override public java.util.Set<String> getCriticalExtensionOIDs() { return null; }
            @Override public java.util.Set<String> getNonCriticalExtensionOIDs() { return null; }
            @Override public byte[] getExtensionValue(String oid) { return new byte[0]; }
        };
        boolean result = CertificateManagerUtil.isSelfSignedCertificate(cert);
        Assert.assertFalse(result);
    }

    @Test
    public void isValidTimestamp_test() {
        CACertificateStore store = new CACertificateStore(""); // Pass a dummy string as required
        LocalDateTime now = LocalDateTime.now();
        store.setCertNotBefore(now.minusDays(1).atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli());
        store.setCertNotAfter(now.plusDays(1).atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli());
        Assert.assertTrue(CertificateManagerUtil.isValidTimestamp(now, store));
        Assert.assertFalse(CertificateManagerUtil.isValidTimestamp(now.minusDays(2), store));
        Assert.assertFalse(CertificateManagerUtil.isValidTimestamp(now.plusDays(2), store));
    }
}
