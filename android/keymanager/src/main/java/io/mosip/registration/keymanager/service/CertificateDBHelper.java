package io.mosip.registration.keymanager.service;

import io.mosip.registration.keymanager.entity.CACertificateStore;
import io.mosip.registration.keymanager.exception.KeymanagerServiceException;
import io.mosip.registration.keymanager.repository.CACertificateStoreRepository;
import io.mosip.registration.keymanager.util.CertificateManagerUtil;
import io.mosip.registration.keymanager.util.DateUtils;
import io.mosip.registration.keymanager.util.KeyManagerConstant;
import io.mosip.registration.keymanager.util.KeyManagerErrorCode;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemObjectGenerator;
import org.bouncycastle.util.io.pem.PemWriter;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;
import java.io.StringWriter;
import java.security.cert.CertificateEncodingException;
import java.security.cert.TrustAnchor;
import java.security.cert.X509Certificate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * DB Helper class for Keymanager
 *
 * @author Mahammed Taheer
 * @since 1.1.2
 *
 */

@Singleton
public class CertificateDBHelper {

    private static final String TAG = CertificateDBHelper.class.getSimpleName();

    private CACertificateStoreRepository caCertificateStoreRepository;

    @Inject
    public CertificateDBHelper(CACertificateStoreRepository caCertificateStoreRepository) {
        this.caCertificateStoreRepository = caCertificateStoreRepository;
    }

    public boolean isCertificateExist(String certThumbprint, String partnerDomain){
        CACertificateStore caCertificate = caCertificateStoreRepository.getCACertStore(certThumbprint, partnerDomain);
        if (Objects.nonNull(caCertificate)) {
            return true;
        }
        return false;
    }

    public void storeCACertificate(String certId, String certSubject, String certIssuer, String issuerId,
                                   X509Certificate reqX509Cert, String certThumbprint, String partnerDomain) {

        String certSerialNo = reqX509Cert.getSerialNumber().toString();
        String certData = getPEMFormatedData(reqX509Cert);
        CACertificateStore certStoreObj = new CACertificateStore(certId);
        certStoreObj.setCertSubject(certSubject);
        certStoreObj.setCertIssuer(certIssuer);
        certStoreObj.setIssuerId(issuerId);
        certStoreObj.setCertNotBefore(reqX509Cert.getNotBefore().getTime());
        certStoreObj.setCertNotAfter(reqX509Cert.getNotAfter().getTime());
        certStoreObj.setCertData(certData);
        certStoreObj.setCertThumbprint(certThumbprint);
        certStoreObj.setCertSerialNo(certSerialNo);
        certStoreObj.setPartnerDomain(partnerDomain);
        caCertificateStoreRepository.save(certStoreObj);
    }

    public String getPEMFormatedData(X509Certificate reqX509Cert) {
        StringWriter stringWriter = new StringWriter();
        try (PemWriter pemWriter = new PemWriter(stringWriter)) {
            pemWriter.writeObject(new PemObject(reqX509Cert.getType(), reqX509Cert.getEncoded()));
            pemWriter.flush();
            return stringWriter.toString();
        } catch (IOException | CertificateEncodingException e) {
            throw new KeymanagerServiceException(KeyManagerErrorCode.INTERNAL_SERVER_ERROR.getErrorCode(),
                    KeyManagerErrorCode.INTERNAL_SERVER_ERROR.getErrorMessage(), e);
        }
    }

    public Map<String, Set<?>> getTrustAnchors(String partnerDomain) {
        Set<TrustAnchor> rootTrust = new HashSet<>();
        Set<X509Certificate> intermediateCerts = new HashSet<>();
        caCertificateStoreRepository.getAllCACertStore(partnerDomain).stream().forEach(
                trustCert -> {
                    String certificateData = trustCert.getCertData();
                    X509Certificate x509Cert = (X509Certificate) CertificateManagerUtil.convertToCertificate(certificateData);
                    if (CertificateManagerUtil.isSelfSignedCertificate(x509Cert)) {
                        rootTrust.add(new TrustAnchor(x509Cert, null));
                    } else{
                        intermediateCerts.add(x509Cert);
                    }
                }
        );
        Map<String, Set<?>> hashMap = new HashMap<>();
        hashMap.put(KeyManagerConstant.TRUST_ROOT, rootTrust);
        hashMap.put(KeyManagerConstant.TRUST_INTER, intermediateCerts);
        return hashMap;
    }


    public String getIssuerCertId(String certIssuerDn) {
        LocalDateTime currentDateTime = DateUtils.getUTCCurrentDateTime();
        List<CACertificateStore> certificates = caCertificateStoreRepository.getAllCACertStoreByCertSubject(certIssuerDn)
                .stream()
                .filter(cert -> CertificateManagerUtil.isValidTimestamp(currentDateTime, cert))
                .collect(Collectors.toList());

        if (certificates.size() == 1) {
            return certificates.get(0).getCertId();
        }
        List<CACertificateStore> sortedCerts = certificates.stream()
                .sorted((cert1, cert2) -> cert1.getCertNotBefore().compareTo(cert2.getCertNotBefore()))
                .collect(Collectors.toList());
        return sortedCerts.get(0).getCertId();
    }
}
