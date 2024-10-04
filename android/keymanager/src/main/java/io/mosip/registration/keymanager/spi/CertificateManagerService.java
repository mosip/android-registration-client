package io.mosip.registration.keymanager.spi;

import io.mosip.registration.keymanager.dto.CACertificateRequestDto;
import io.mosip.registration.keymanager.dto.CACertificateResponseDto;
import io.mosip.registration.keymanager.dto.CertificateRequestDto;
import io.mosip.registration.keymanager.dto.CertificateTrustRequestDto;
import io.mosip.registration.keymanager.dto.CertificateTrustResponseDto;

public interface CertificateManagerService {

    CACertificateResponseDto uploadCACertificate(CACertificateRequestDto caCertificateRequestDto);
    void uploadOtherDomainCertificate(CertificateRequestDto certificateRequestDto);
    String getCertificate(String applicationId, String referenceId);
    CertificateTrustResponseDto verifyCertificateTrust(CertificateTrustRequestDto certificateTrustRequestDto);
}
