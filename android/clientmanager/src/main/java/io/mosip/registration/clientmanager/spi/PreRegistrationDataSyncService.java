package io.mosip.registration.clientmanager.spi;

public interface PreRegistrationDataSyncService {
    public ResponseDTO getPreRegistration(String preRegistrationId, boolean forceDownload);
}