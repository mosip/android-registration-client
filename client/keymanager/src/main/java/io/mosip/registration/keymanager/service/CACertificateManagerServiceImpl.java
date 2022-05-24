package io.mosip.registration.keymanager.service;

import android.content.Context;
import android.util.Log;
import io.mosip.registration.keymanager.dto.CACertificateRequestDto;
import io.mosip.registration.keymanager.dto.CACertificateResponseDto;
import io.mosip.registration.keymanager.exception.KeymanagerServiceException;
import io.mosip.registration.keymanager.spi.CACertificateManagerService;
import io.mosip.registration.keymanager.util.CertificateManagerUtil;
import io.mosip.registration.keymanager.util.ConfigService;
import io.mosip.registration.keymanager.util.KeyManagerConstant;
import io.mosip.registration.keymanager.util.KeyManagerErrorCode;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.security.*;
import java.security.cert.*;
import java.util.*;
import java.util.stream.Stream;

@Singleton
public class CACertificateManagerServiceImpl implements CACertificateManagerService {

    private static final String TAG = CACertificateManagerServiceImpl.class.getSimpleName();
    private Context context;
    private String partnerAllowedDomains;

    private CertificateDBHelper certificateDBHelper;

    @Inject
    public CACertificateManagerServiceImpl(Context context, CertificateDBHelper certificateDBHelper) {
        this.context = context;
        this.certificateDBHelper = certificateDBHelper;
        partnerAllowedDomains = ConfigService.getProperty("mosip.kernel.partner.allowed.domains",context);
    }

    @Override
    public CACertificateResponseDto uploadCACertificate(CACertificateRequestDto caCertificateRequestDto) {
        Log.i(TAG, "Uploading CA/Sub-CA Certificate.");

        String certificateData = caCertificateRequestDto.getCertificateData();
        if (!CertificateManagerUtil.isValidCertificateData(certificateData)) {
            Log.e(TAG, "Invalid Certificate Data provided to upload the ca/sub-ca certificate.");
            throw new KeymanagerServiceException(KeyManagerErrorCode.INVALID_CERTIFICATE.getErrorCode(),
                    KeyManagerErrorCode.INVALID_CERTIFICATE.getErrorMessage());
        }
        X509Certificate reqX509Cert = (X509Certificate) CertificateManagerUtil.convertToCertificate(certificateData);
        String certThumbprint = CertificateManagerUtil.getCertificateThumbprint(reqX509Cert);
        String partnerDomain = validateAllowedDomains(caCertificateRequestDto.getPartnerDomain());

        validateBasicCACertParams(reqX509Cert, certThumbprint, partnerDomain);

        String certSubject = CertificateManagerUtil.formatCertificateDN(reqX509Cert.getSubjectX500Principal().getName());
        String certIssuer = CertificateManagerUtil.formatCertificateDN(reqX509Cert.getIssuerX500Principal().getName());
        boolean selfSigned = CertificateManagerUtil.isSelfSignedCertificate(reqX509Cert);

        if (selfSigned) {
            Log.i(TAG, "Adding Self-signed Certificate in store.");
            String certId = UUID.randomUUID().toString();
            certificateDBHelper.storeCACertificate(certId, certSubject, certIssuer, certId, reqX509Cert, certThumbprint,
                    partnerDomain);
        } else {
            Log.i(TAG, "Adding Intermediate Certificates in store.");

            boolean certValid = validateCertificatePath(reqX509Cert, partnerDomain);
            if (!certValid) {
                Log.e(TAG, "Sub-CA Certificate not allowed to upload as root CA is not available.");
                throw new KeymanagerServiceException(KeyManagerErrorCode.ROOT_CA_NOT_FOUND.getErrorCode(),
                        KeyManagerErrorCode.ROOT_CA_NOT_FOUND.getErrorMessage());
            }
            String issuerId = certificateDBHelper.getIssuerCertId(certIssuer);
            String certId = UUID.randomUUID().toString();
            certificateDBHelper.storeCACertificate(certId, certSubject, certIssuer, issuerId, reqX509Cert, certThumbprint,
                    partnerDomain);
        }

        CACertificateResponseDto responseDto = new CACertificateResponseDto();
        responseDto.setStatus(KeyManagerConstant.SUCCESS_UPLOAD);
        return responseDto;
    }

    private boolean validateCertificatePath(X509Certificate reqX509Cert, String partnerDomain) {

        try {
            Map<String, Set<?>> trustStoreMap = certificateDBHelper.getTrustAnchors(partnerDomain);
            Set<TrustAnchor> rootTrustAnchors = (Set<TrustAnchor>) trustStoreMap
                    .get(KeyManagerConstant.TRUST_ROOT);
            Set<X509Certificate> interCerts = (Set<X509Certificate>) trustStoreMap
                    .get(KeyManagerConstant.TRUST_INTER);

            X509CertSelector certToVerify = new X509CertSelector();
            certToVerify.setCertificate(reqX509Cert);

            PKIXBuilderParameters pkixBuilderParams = new PKIXBuilderParameters(rootTrustAnchors, certToVerify);
            pkixBuilderParams.setRevocationEnabled(false);

            CertStore interCertStore = CertStore.getInstance("Collection",
                    new CollectionCertStoreParameters(interCerts));
            pkixBuilderParams.addCertStore(interCertStore);

            // Building the cert path and verifying the certification chain
            CertPathBuilder certPathBuilder = CertPathBuilder.getInstance("PKIX");
            certPathBuilder.build(pkixBuilderParams);
            /* PKIXCertPathBuilderResult result = (PKIXCertPathBuilderResult) */
            /*
             * List<? extends Certificate> certList =
             * result.getCertPath().getCertificates();
             */
            return true;
        } catch (CertPathBuilderException | InvalidAlgorithmParameterException | NoSuchAlgorithmException exp) {
            Log.e(TAG, "Ignore this exception, the exception thrown when trust validation failed.");
        }
        return false;
    }

    private String validateAllowedDomains(String partnerDomain) {
        String validPartnerDomain = Stream.of(partnerAllowedDomains.split(",")).map(String::trim)
                .filter(allowedDomain -> allowedDomain.equalsIgnoreCase(partnerDomain)).findFirst()
                .orElseThrow(() -> new KeymanagerServiceException(
                        KeyManagerErrorCode.INVALID_PARTNER_DOMAIN.getErrorCode(),
                        KeyManagerErrorCode.INVALID_PARTNER_DOMAIN.getErrorMessage()));
        return validPartnerDomain.toUpperCase();
    }

    private void validateBasicCACertParams(X509Certificate reqX509Cert, String certThumbprint, String partnerDomain) {
        boolean certExist = certificateDBHelper.isCertificateExist(certThumbprint, partnerDomain);
        if (certExist) {
            Log.e(TAG,"CA/sub-CA certificate already exists in Store.");
            throw new KeymanagerServiceException(
                    KeyManagerErrorCode.CERTIFICATE_EXIST_ERROR.getErrorCode(),
                    KeyManagerErrorCode.CERTIFICATE_EXIST_ERROR.getErrorMessage());
        }

        boolean validDates = CertificateManagerUtil.isCertificateDatesValid(reqX509Cert);
        if (!validDates) {
            Log.e(TAG,"Certificate Dates are not valid.");
            throw new KeymanagerServiceException(
                    KeyManagerErrorCode.CERTIFICATE_DATES_NOT_VALID.getErrorCode(),
                    KeyManagerErrorCode.CERTIFICATE_DATES_NOT_VALID.getErrorMessage());
        }
    }


}
