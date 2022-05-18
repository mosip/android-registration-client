package io.mosip.registration.keymanager.spi;

import io.mosip.registration.keymanager.dto.CACertificateRequestDto;
import io.mosip.registration.keymanager.dto.CACertificateResponseDto;

public interface CACertificateManagerService {

    CACertificateResponseDto uploadCACertificate(CACertificateRequestDto caCertificateRequestDto);
}
