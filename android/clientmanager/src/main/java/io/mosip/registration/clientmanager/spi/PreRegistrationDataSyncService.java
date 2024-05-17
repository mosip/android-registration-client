package io.mosip.registration.clientmanager.spi;

import io.mosip.registration.clientmanager.dto.ResponseDto;

public interface PreRegistrationDataSyncService {
    ResponseDto getPreRegistration(String preRegistrationId, boolean forceDownload);
}