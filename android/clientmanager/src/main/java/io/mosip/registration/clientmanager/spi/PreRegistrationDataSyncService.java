package io.mosip.registration.clientmanager.spi;

import java.util.Map;

import io.mosip.registration.clientmanager.dto.ResponseDto;
import io.mosip.registration.clientmanager.exception.ClientCheckedException;

public interface PreRegistrationDataSyncService {
    public Map<String, Object> getPreRegistration(String preRegistrationId, boolean forceDownload);
    void fetchPreRegistrationIds(Runnable onFinish);
}