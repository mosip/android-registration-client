package io.mosip.registration.clientmanager.service;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import io.mosip.registration.clientmanager.BuildConfig;
import io.mosip.registration.clientmanager.R;
import io.mosip.registration.clientmanager.constant.RegistrationConstants;
import io.mosip.registration.clientmanager.dao.PreRegistrationDataSyncDao;
import io.mosip.registration.clientmanager.dto.CenterMachineDto;
import io.mosip.registration.clientmanager.dto.MainResponseDto;
import io.mosip.registration.clientmanager.dto.PreRegArchiveDto;
import io.mosip.registration.clientmanager.dto.PreRegistrationDto;
import io.mosip.registration.clientmanager.dto.ResponseDto;
import io.mosip.registration.clientmanager.dto.http.IdSchemaResponse;
import io.mosip.registration.clientmanager.dto.http.ResponseWrapper;
import io.mosip.registration.clientmanager.dto.http.ServiceError;
import io.mosip.registration.clientmanager.entity.PreRegistrationList;
import io.mosip.registration.clientmanager.exception.ClientCheckedException;
import io.mosip.registration.clientmanager.exception.RegBaseCheckedException;
import io.mosip.registration.clientmanager.service.external.PreRegZipHandlingService;
import io.mosip.registration.clientmanager.spi.MasterDataService;
import io.mosip.registration.clientmanager.spi.PreRegistrationDataSyncService;
import io.mosip.registration.clientmanager.dto.registration.RegistrationDto;
import io.mosip.registration.clientmanager.spi.SyncRestService;
import io.mosip.registration.clientmanager.util.SyncRestUtil;
import io.mosip.registration.packetmanager.util.DateUtils;
import io.mosip.registration.packetmanager.util.JsonUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import androidx.annotation.NonNull;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.commons.io.FileUtils;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.WeakHashMap;


public class PreRegistrationDataSyncServiceImpl implements PreRegistrationDataSyncService {

    private static final String TAG = PreRegistrationDataSyncServiceImpl.class.getSimpleName();
    PreRegistrationDataSyncDao preRegistrationDao;
    PreRegZipHandlingService preRegZipHandlingService;
    MasterDataService masterDataService;
    SyncRestService syncRestService;
    private Context context;

    public PreRegistrationDataSyncServiceImpl(Context context,PreRegistrationDataSyncDao preRegistrationDao,MasterDataService masterDataService,SyncRestService syncRestService){
        this.context = context;
        this.preRegistrationDao = preRegistrationDao;
        this.masterDataService = masterDataService;
        this.syncRestService = syncRestService;
    }

    @Override
    public ResponseDto getPreRegistration(@NonNull String preRegistrationId, boolean forceDownload) {
        ResponseDto responseDTO = new ResponseDto();
        try {
            PreRegistrationList preRegistration = this.preRegistrationDao.get(preRegistrationId);
            preRegistration = fetchPreRegistration(preRegistrationId, preRegistration == null ? null :
                    forceDownload ? null : preRegistration.getLastUpdatedPreRegTimeStamp());

            if (preRegistration != null) {
                byte[] decryptedPacket = preRegZipHandlingService.decryptPreRegPacket(
                        preRegistration.getPacketSymmetricKey(),
                        FileUtils.readFileToByteArray(FileUtils.getFile(preRegistration.getPacketPath())));
                setPacketToResponse(responseDTO, decryptedPacket, preRegistrationId);
                return responseDTO;
            }
        } catch (RegBaseCheckedException regBaseCheckedException) {
            Log.e(TAG,"Failed to fetch pre-reg packet", regBaseCheckedException);
            //setErrorResponse(responseDTO, regBaseCheckedException.getErrorCode(), null);
            return responseDTO;
        } catch (Exception e) {
            Log.e(TAG,"Failed to fetch pre-reg packet", e);
        }
        //setErrorResponse(responseDTO, RegistrationConstants.PRE_REG_TO_GET_PACKET_ERROR, null);
        return responseDTO;
    }

    private PreRegistrationList fetchPreRegistration(String preRegistrationId, String lastUpdatedTimeStamp) throws ClientCheckedException {
        Log.i(TAG,"Fetching Pre-Registration started for {}"+ preRegistrationId);
        PreRegistrationList preRegistration;

        /* Check in Database whether required record already exists or not */
        preRegistration = this.preRegistrationDao.get(preRegistrationId);
        if(preRegistration == null || !FileUtils.getFile(preRegistration.getPacketPath()).exists()) {
            Log.i(TAG,"Pre-Registration ID is not present downloading {}"+ preRegistrationId);
            return downloadAndSavePacket(preRegistration, preRegistrationId, lastUpdatedTimeStamp);
        }

        if(lastUpdatedTimeStamp == null /*||
                preRegistration.getLastUpdatedPreRegTimeStamp().before(lastUpdatedTimeStamp)*/) {
            Log.i(TAG,"Pre-Registration ID is not up-to-date downloading {}"+ preRegistrationId);
            return downloadAndSavePacket(preRegistration, preRegistrationId, lastUpdatedTimeStamp);
        }
        return preRegistration;
    }

    private void setPacketToResponse(ResponseDto responseDTO, byte[] decryptedPacket, String preRegistrationId) {

        try {
            /* create attributes */
            RegistrationDto registrationDto = preRegZipHandlingService.extractPreRegZipFile(decryptedPacket);
            registrationDto.setPreRegistrationId(preRegistrationId);
            Map<String, Object> attributes = new WeakHashMap<>();
            attributes.put("registrationDto", registrationDto);
           // setSuccessResponse(responseDTO, RegistrationConstants.PRE_REG_SUCCESS_MESSAGE, attributes);
        } catch (Exception regBaseCheckedException) {
            Log.e(TAG,"REGISTRATION - PRE_REGISTRATION_DATA_SYNC - PRE_REGISTRATION_DATA_SYNC_SERVICE_IMPL",
                    regBaseCheckedException);
           // setErrorResponse(responseDTO, RegistrationConstants.PRE_REG_TO_GET_PACKET_ERROR, null);
        }

    }

    private PreRegistrationList downloadAndSavePacket(PreRegistrationList preRegistration, @NonNull String preRegistrationId,
                                                      String lastUpdatedTimeStamp) throws ClientCheckedException {

        //final PreRegistrationList getPreRegistrationList = preRegistration;

        CenterMachineDto centerMachineDto = this.masterDataService.getRegistrationCenterMachineDetails();
        Log.i(TAG,"Pre-Registration get center Id"+ centerMachineDto.getCenterId() + centerMachineDto.getMachineId());
        if (centerMachineDto == null)
            throw new ClientCheckedException(context, R.string.err_001);


        Call<ResponseWrapper<PreRegArchiveDto>> call = this.syncRestService.getPreRegistrationData(preRegistrationId, centerMachineDto.getMachineId(), BuildConfig.CLIENT_VERSION);


        try {
            Response<ResponseWrapper<PreRegArchiveDto>> response = call.execute();

            // Handle response
           // handlePreRegistrationResponse(response, preRegistration, preRegistrationId, lastUpdatedTimeStamp);

            if (response.isSuccessful()) {

                ResponseWrapper<PreRegArchiveDto> responseWrapper = response.body();
                Log.i(TAG,"Pre-Registration all data "+ response.body());

                if (responseWrapper != null && responseWrapper.getResponse() != null) {

                    Log.i(TAG,"Pre-Registration all main dto "+ responseWrapper.getResponse().getZipBytes());
                    if(responseWrapper.getResponse() != null && responseWrapper.getResponse().getZipBytes() != null) {
                        PreRegistrationDto preRegistrationDto = preRegZipHandlingService
                                .encryptAndSavePreRegPacket(preRegistrationId, responseWrapper.getResponse().getZipBytes());

                        // Transaction
//                            SyncTransaction syncTransaction = syncManager.createSyncTransaction(
//                                    RegistrationConstants.RETRIEVED_PRE_REG_ID, RegistrationConstants.RETRIEVED_PRE_REG_ID,
//                                    RegistrationConstants.JOB_TRIGGER_POINT_SYSTEM, "PDS_J00003");

                        // save in Pre-Reg List
                        PreRegistrationList preRegistrationList = preparePreRegistration(preRegistrationDto);

                        if(responseWrapper.getResponse().getAppointmentDate() != null) {
                            preRegistrationList.setAppointmentDate(DateUtils.parseUTCToLocalDateTime(responseWrapper.getResponse().getAppointmentDate(),
                                    "yyyy-MM-dd"));
                        }

                        preRegistrationList.setLastUpdatedPreRegTimeStamp(lastUpdatedTimeStamp == null ?
                                String.valueOf(Timestamp.valueOf(String.valueOf(DateUtils.getUTCCurrentDateTime()))) : lastUpdatedTimeStamp);
                        if (preRegistration == null) {
                            long id = this.preRegistrationDao.save(preRegistrationList);
                            preRegistration = this.preRegistrationDao.getById(id);
                        } else {
                            preRegistrationList.setId(getPreRegistrationList.getId());
                            preRegistrationList.setUpdBy(getUserIdFromSession());
                            preRegistrationList.setUpdDtimes(new Timestamp(System.currentTimeMillis()));
//                            long id = this.preRegistrationDao.update(preRegistrationList);
//                            preRegistration = this.preRegistrationDao.getById(id);
                        }
                    } else if (responseWrapper.getErrors() != null && !responseWrapper.getErrors().isEmpty()) {
//                            PreRegistrationExceptionJSONInfoDTO errorResponse = mainResponseDTO.getErrors().get(0);
//                            Log.i(TAG,"Pre-reg-id {} errors from response {}", preRegistrationId,
//                                    errorResponse.getErrorCode(), errorResponse.getMessage());
//                            throw new RegBaseCheckedException(errorResponse.getErrorCode(), errorResponse.getMessage());
                    }

                }

            } else {
                Log.e(TAG, "Response not successful: " + response.message());
            }
        } catch (IOException e) {
            Log.e(TAG, "Failed to execute call: " + e.getMessage(), e);
        }

        return preRegistration;
    }

    private PreRegistrationList preparePreRegistration(
            PreRegistrationDto preRegistrationDto) {

        PreRegistrationList preRegistrationList = new PreRegistrationList();

        preRegistrationList.setId(UUID.randomUUID().toString());
        preRegistrationList.setPreRegId(preRegistrationDto.getPreRegId());
        // preRegistrationList.setAppointmentDate(preRegistrationDto.getAppointmentDate());
        preRegistrationList.setPacketSymmetricKey(preRegistrationDto.getSymmetricKey());
//        preRegistrationList.setStatusCode(syncTransaction.getStatusCode());
//        preRegistrationList.setStatusComment(syncTransaction.getStatusComment());
        preRegistrationList.setPacketPath(preRegistrationDto.getPacketPath());
//        preRegistrationList.setsJobId(syncTransaction.getSyncJobId());
//        preRegistrationList.setSynctrnId(syncTransaction.getId());
//        preRegistrationList.setLangCode(syncTransaction.getLangCode());
        preRegistrationList.setIsActive(true);
        preRegistrationList.setIsDeleted(false);
        //preRegistrationList.setCrBy(syncTransaction.getCrBy());
        preRegistrationList.setCrDtime(new Timestamp(System.currentTimeMillis()));
        return preRegistrationList;
    }
}