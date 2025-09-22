package io.mosip.registration.clientmanager.spi;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import io.mosip.registration.clientmanager.dto.ResponseDto;
import io.mosip.registration.clientmanager.entity.PreRegistrationList;

public interface PreRegistrationDataSyncService {
    Map<String, Object> getPreRegistration(String preRegistrationId, boolean forceDownload);
    void fetchPreRegistrationIds(Runnable onFinish);
    ResponseDto fetchAndDeleteRecords();
    void deletePreRegRecords(ResponseDto responseDTO, List<PreRegistrationList> preRegList);
    PreRegistrationList getPreRegistrationRecordForDeletion(String preRegistrationId);
    Timestamp getLastPreRegPacketDownloadedTime();
}