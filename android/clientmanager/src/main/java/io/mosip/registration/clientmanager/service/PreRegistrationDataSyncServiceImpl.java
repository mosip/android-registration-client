package io.mosip.registration.clientmanager.service;

import io.mosip.registration.clientmanager.spi.PreRegistrationDataSyncService;
import androidx.annotation.NonNull;

public class PreRegistrationDataSyncServiceImpl implements PreRegistrationDataSyncService {
    public PreRegistrationDataSyncServiceImpl(){}

    @Override
    public ResponseDTO getPreRegistration(@NonNull String preRegistrationId, boolean forceDownload) {
        return null;
    }
}