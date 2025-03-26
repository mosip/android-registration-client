package io.mosip.registration.keymanager.service;

import static io.mosip.registration.keymanager.util.CertificateManagerUtil.getPEMFormatedData;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;

import javax.crypto.KeyGenerator;

import io.mosip.registration.keymanager.dto.CryptoManagerResponseDto;
import io.mosip.registration.keymanager.entity.CACertificateStore;
import io.mosip.registration.keymanager.util.CertificateManagerUtil;

public class CryptoManagerServiceImplTest {

    @Mock
    CryptoManagerServiceImpl cryptManagerService;

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

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private CACertificateStore getCertificate(){
        X509Certificate reqX509Cert = (X509Certificate) CertificateManagerUtil.convertToCertificate(CERT_DATA);

        String certSerialNo = reqX509Cert.getSerialNumber().toString();
        String certData = getPEMFormatedData(reqX509Cert);
        CACertificateStore certStoreObj = new CACertificateStore("12345");
        certStoreObj.setCertSubject("certSubject");
        certStoreObj.setCertIssuer("certIssuer");
        certStoreObj.setIssuerId("issuerId");
        certStoreObj.setCertNotBefore(reqX509Cert.getNotBefore().getTime());
        certStoreObj.setCertNotAfter(reqX509Cert.getNotAfter().getTime());
        certStoreObj.setCertData("certData");
        certStoreObj.setCertThumbprint("certThumbprint");
        certStoreObj.setCertSerialNo("certSerialNo");
        certStoreObj.setPartnerDomain("partnerDomain");

        return certStoreObj;
    }

    @Test
    public void checkGenerateAESKeyZeroLength() throws NoSuchAlgorithmException {
        KeyGenerator keygen = cryptManagerService.generateAESKey(0);
        Assert.assertNull(keygen);
    }

    @Test
    public void checkencryptNull() throws Exception {
        CryptoManagerResponseDto dto =  cryptManagerService.encrypt(null);
        Assert.assertNull(dto);
    }

    @Test
    public void checkConcatCertThumbprintNull() throws Exception {
        CACertificateStore store = getCertificate();
        byte[] data = cryptManagerService.concatCertThumbprint(null, null);
        Assert.assertNull(data);
    }

    @Test
    public void checkConvertToCertificateNull() throws Exception {
        Certificate cert = cryptManagerService.convertToCertificate(null);
        Assert.assertNull(cert);
    }

    @Test
    public void checkCertThumbprintNull() throws Exception {
        byte[] data = cryptManagerService.getCertificateThumbprint(null);
        Assert.assertNull(data);
    }
}
