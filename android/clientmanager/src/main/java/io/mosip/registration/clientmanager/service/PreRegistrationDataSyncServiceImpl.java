package io.mosip.registration.clientmanager.service;

import static io.mosip.registration.clientmanager.config.SessionManager.USER_NAME;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import io.mosip.registration.clientmanager.BuildConfig;
import io.mosip.registration.clientmanager.R;
import io.mosip.registration.clientmanager.constant.RegistrationConstants;
import io.mosip.registration.clientmanager.dao.PreRegistrationDataSyncDao;
import io.mosip.registration.clientmanager.dto.CenterMachineDto;
import io.mosip.registration.clientmanager.dto.PreRegArchiveDto;
import io.mosip.registration.clientmanager.dto.PreRegistrationDataSyncDto;
import io.mosip.registration.clientmanager.dto.PreRegistrationDataSyncRequestDto;
import io.mosip.registration.clientmanager.dto.PreRegistrationDto;
import io.mosip.registration.clientmanager.dto.PreRegistrationIdsDto;
import io.mosip.registration.clientmanager.dto.http.ResponseWrapper;
import io.mosip.registration.clientmanager.dto.http.ServiceError;
import io.mosip.registration.clientmanager.entity.PreRegistrationList;
import io.mosip.registration.clientmanager.exception.ClientCheckedException;
import io.mosip.registration.clientmanager.repository.GlobalParamRepository;
import io.mosip.registration.clientmanager.service.external.PreRegZipHandlingService;
import io.mosip.registration.clientmanager.spi.MasterDataService;
import io.mosip.registration.clientmanager.spi.PreRegistrationDataSyncService;
import io.mosip.registration.clientmanager.dto.registration.RegistrationDto;
import io.mosip.registration.clientmanager.spi.RegistrationService;
import io.mosip.registration.clientmanager.spi.SyncRestService;
import io.mosip.registration.clientmanager.util.SyncRestUtil;
import io.mosip.registration.packetmanager.util.DateUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import androidx.annotation.NonNull;

import org.apache.commons.io.FileUtils;
import java.time.Instant;
import java.util.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Map;
import java.util.TimeZone;
import java.util.UUID;
import java.util.WeakHashMap;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;


public class PreRegistrationDataSyncServiceImpl implements PreRegistrationDataSyncService {

    private static final String TAG = PreRegistrationDataSyncServiceImpl.class.getSimpleName();
    PreRegistrationDataSyncDao preRegistrationDao;
    PreRegZipHandlingService preRegZipHandlingService;
    MasterDataService masterDataService;
    SyncRestService syncRestService;
    SharedPreferences sharedPreferences;
    PreRegistrationList preRegistration;
    GlobalParamRepository globalParamRepository;
    RegistrationService registrationService;
    private Context context;
    private String result = "";
    ExecutorService executorServiceForPreReg = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    public static final String APPLICATION_ID_SYNC_FAILED = "application_id_sync_failed";
    public static final String ERROR_FETCH_PRE_REG_PACKET = "Failed to fetch pre-reg packet";

    public PreRegistrationDataSyncServiceImpl(Context context,PreRegistrationDataSyncDao preRegistrationDao,MasterDataService masterDataService,SyncRestService syncRestService,PreRegZipHandlingService preRegZipHandlingService,PreRegistrationList preRegistration,GlobalParamRepository globalParamRepository,RegistrationService registrationService){
        this.context = context;
        this.preRegistrationDao = preRegistrationDao;
        this.masterDataService = masterDataService;
        this.syncRestService = syncRestService;
        this.preRegZipHandlingService = preRegZipHandlingService;
        this.preRegistration = preRegistration;
        this.globalParamRepository = globalParamRepository;
        this.registrationService = registrationService;
        sharedPreferences = this.context.getSharedPreferences(
                this.context.getString(R.string.app_name),
                Context.MODE_PRIVATE);
    }

    @Override
    public void fetchPreRegistrationIds(Runnable onFinish) {
        Log.i(TAG,"Fetching Pre-Registration Id's started {}");

        CenterMachineDto centerMachineDto = this.masterDataService.getRegistrationCenterMachineDetails();
        if (centerMachineDto == null) {
            result = APPLICATION_ID_SYNC_FAILED;
            onFinish.run();
            return;
        }

        // prepare required Dto to send through API
        PreRegistrationDataSyncDto preRegistrationDataSyncDto = new PreRegistrationDataSyncDto();

        Timestamp reqTime = new Timestamp(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));

        preRegistrationDataSyncDto.setId(RegistrationConstants.PRE_REGISTRATION_DUMMY_ID);
        preRegistrationDataSyncDto.setRequestTime(DateUtils.formatToISOString(LocalDateTime.now(ZoneOffset.UTC)));
        preRegistrationDataSyncDto.setVersion(RegistrationConstants.VER);

        PreRegistrationDataSyncRequestDto preRegistrationDataSyncRequestDto = new PreRegistrationDataSyncRequestDto();
        preRegistrationDataSyncRequestDto.setRegistrationCenterId(centerMachineDto.getCenterId());
        preRegistrationDataSyncRequestDto.setFromDate(getFromDate(reqTime));
        preRegistrationDataSyncRequestDto.setToDate(getToDate(reqTime));

        preRegistrationDataSyncDto.setRequest(preRegistrationDataSyncRequestDto);

        //REST call to get Pre Registration Id's
        Call<ResponseWrapper<PreRegistrationIdsDto>> call = this.syncRestService.getPreRegistrationIds(preRegistrationDataSyncDto);

        Log.i(TAG,"REST API Url "+call);
        call.enqueue(new Callback<ResponseWrapper<PreRegistrationIdsDto>>() {
            @Override
            public void onResponse(Call<ResponseWrapper<PreRegistrationIdsDto>> call, Response<ResponseWrapper<PreRegistrationIdsDto>> response) {
                if (response.isSuccessful()) {
                    ServiceError error = SyncRestUtil.getServiceError(response.body());
                    if(error == null) {
                        try {
                            ResponseWrapper<PreRegistrationIdsDto> responseWrapper = response.body();

                            //pre-rids received
                            if (responseWrapper != null && responseWrapper.getResponse() != null) {
                                Map<String, String> preRegIds = responseWrapper.getResponse().getPreRegistrationIds();
                                getPreRegistrationPackets(preRegIds);
                                Log.i(TAG,"Fetching Application data ended successfully");
                            }
                           Toast.makeText(context, "Application Id Sync Completed", Toast.LENGTH_LONG).show();
                           result = "";
                           onFinish.run();
                        } catch (Exception e) {
                            result = APPLICATION_ID_SYNC_FAILED;
                            Log.e(TAG, APPLICATION_ID_SYNC_FAILED, e);
                            Toast.makeText(context, "Application Id Sync failed " + error.getMessage(), Toast.LENGTH_LONG).show();
                            onFinish.run();
                        }

                    } else {
                        result = APPLICATION_ID_SYNC_FAILED;
                        Toast.makeText(context, "Application Id Sync failed " + error.getMessage(), Toast.LENGTH_LONG).show();
                        onFinish.run();
                    }
                } else {
                    result = APPLICATION_ID_SYNC_FAILED;
                    Toast.makeText(context, "Application Id Sync failed with status code : " + response.code(), Toast.LENGTH_LONG).show();
                    onFinish.run();
                }
            }
            @Override
            public void onFailure(Call<ResponseWrapper<PreRegistrationIdsDto>> call, Throwable t) {
                Log.e(TAG,"Application Data Sync "+ t);
                result = APPLICATION_ID_SYNC_FAILED;
                Toast.makeText(context, "Application Id Sync failed", Toast.LENGTH_LONG).show();
                onFinish.run();
            }
        });
    }

    private void getPreRegistrationPackets(Map<String, String> preRegIds) {
        Log.i(TAG,"Fetching Pre-Registration ID's in parallel mode started");
        /* Get Packets Using pre registration ID's */
        for (Map.Entry<String, String> preRegDetail : preRegIds.entrySet()) {
            try {
                executorServiceForPreReg.execute(
                        new Runnable() {
                            public void run() {
                                //TODO - Need to inform pre-reg team to correct date format
                                preRegDetail.setValue(preRegDetail.getValue().endsWith("Z") ? preRegDetail.getValue() : preRegDetail.getValue() + "Z");
                                try {
                                    fetchPreRegistration(preRegDetail.getKey(), String.valueOf(Timestamp.from(Instant.parse(preRegDetail.getValue()))));
                                } catch (Exception e) {
                                    Log.e(TAG,ERROR_FETCH_PRE_REG_PACKET, e);
                                }
                            }
                        }
                );
            } catch (Exception ex) {
                Log.e(TAG,ERROR_FETCH_PRE_REG_PACKET, ex);
            }
        }
        Log.e(TAG,"Added Pre-Registration packet fetch task in parallel mode completed");
    }

    @Override
    public Map<String, Object> getPreRegistration(@NonNull String preRegistrationId, boolean forceDownload) {
        Log.i(getClass().getSimpleName(),"enter pre reg.....");
        Map<String, Object> attributeData = new WeakHashMap<>();
        try {
            preRegistration = this.preRegistrationDao.get(preRegistrationId);
            preRegistration = fetchPreRegistration(preRegistrationId, preRegistration == null ? null :
                    forceDownload ? null : preRegistration.getLastUpdatedPreRegTimeStamp());

            if (preRegistration != null && preRegistration.getPacketPath()!=null) {
                byte[] decryptedPacket = preRegZipHandlingService.decryptPreRegPacket(
                        preRegistration.getPacketSymmetricKey(),
                        FileUtils.readFileToByteArray(FileUtils.getFile(preRegistration.getPacketPath())));
                attributeData = setPacketToResponse(decryptedPacket, preRegistrationId);
            }
        } catch (Exception e) {
            Log.e(TAG,ERROR_FETCH_PRE_REG_PACKET, e);
        }
        return attributeData;
    }

    private PreRegistrationList fetchPreRegistration(String preRegistrationId, String lastUpdatedTimeStamp) throws Exception {
        Log.i(TAG,"Fetching Pre-Registration started for {}"+ preRegistrationId);
       // PreRegistrationList preRegistration;

        /* Check in Database whether required record already exists or not */
        preRegistration = this.preRegistrationDao.get(preRegistrationId);
        if(preRegistration == null || preRegistration.getPacketPath() == null ||
                !FileUtils.getFile(preRegistration.getPacketPath()).exists()) {
            Log.i(TAG,"Pre-Registration ID is not present downloading {}"+ preRegistrationId);
            try {
                preRegistration = downloadAndSavePacket(preRegistrationId, lastUpdatedTimeStamp);
            } catch (ExecutionException | InterruptedException e) {
                this.registrationService.getRegistrationDto().getDocuments().clear();
                this.registrationService.getRegistrationDto().getDemographics().clear();
                throw new RuntimeException(e);
            }
            return preRegistration;
        }

        Timestamp updatedPreRegTimeStamp = Timestamp.valueOf(preRegistration.getLastUpdatedPreRegTimeStamp());
        Timestamp lastUpdatedTime = Timestamp.valueOf(lastUpdatedTimeStamp);
        if(lastUpdatedTimeStamp == null ||
                updatedPreRegTimeStamp.before(lastUpdatedTime)) {
            Log.i(TAG,"Pre-Registration ID is not up-to-date downloading {}"+ preRegistrationId);
            try {
                preRegistration = downloadAndSavePacket(preRegistrationId, lastUpdatedTimeStamp);
            } catch (ExecutionException | InterruptedException e) {
                this.registrationService.getRegistrationDto().getDocuments().clear();
                this.registrationService.getRegistrationDto().getDemographics().clear();
                throw new RuntimeException(e);
            }
        }
        return preRegistration;
    }

    private Map<String, Object> setPacketToResponse(byte[] decryptedPacket, String preRegistrationId) {
        Map<String, Object> attributes = new WeakHashMap<>();
        try {
            // create attributes
            RegistrationDto registrationDto = preRegZipHandlingService.extractPreRegZipFile(decryptedPacket);
            registrationDto.setPreRegistrationId(preRegistrationId);
            attributes.put("registrationDto", registrationDto);
            Log.i(TAG,"get registrationDto"+attributes);
        } catch (Exception regBaseCheckedException) {
            Log.e(TAG,"REGISTRATION - PRE_REGISTRATION_DATA_SYNC - PRE_REGISTRATION_DATA_SYNC_SERVICE_IMPL",
                    regBaseCheckedException);
        }
        return attributes;
    }

    private PreRegistrationList downloadAndSavePacket(@NonNull String preRegistrationId,
                                                     String lastUpdatedTimeStamp) throws ClientCheckedException, ExecutionException, InterruptedException {

        CenterMachineDto centerMachineDto = this.masterDataService.getRegistrationCenterMachineDetails();
        if (centerMachineDto == null) {
            throw new ClientCheckedException(context, R.string.err_001);
        }

        Callable<PreRegistrationList> callable = () -> {
            Call<ResponseWrapper<PreRegArchiveDto>> call = this.syncRestService.getPreRegistrationData(preRegistrationId, centerMachineDto.getMachineId(), BuildConfig.CLIENT_VERSION);
            Response<ResponseWrapper<PreRegArchiveDto>> response = call.execute();// Synchronous call

            if (response.isSuccessful() && response.body() != null) {
                ServiceError error = SyncRestUtil.getServiceError(response.body());
                if (error == null) {
                    ResponseWrapper<PreRegArchiveDto> responseWrapper = response.body();
                    PreRegArchiveDto preRegArchiveDto = responseWrapper.getResponse();
                    if (preRegArchiveDto != null && preRegArchiveDto.getZipBytes() != null) {
                        String stringZipBytes = preRegArchiveDto.getZipBytes();
                        String byteString = stringZipBytes.replaceAll("\\+", "-").replaceAll("/", "_");
                        PreRegistrationDto preRegistrationDto = preRegZipHandlingService.encryptAndSavePreRegPacket(preRegistrationId, byteString, centerMachineDto);

                        // save in Pre-Reg List
                        PreRegistrationList preRegistrationList = preparePreRegistration(centerMachineDto, preRegistrationDto, preRegArchiveDto.getAppointmentDate(), lastUpdatedTimeStamp);
                        preRegistration = preRegistrationList;
                        return preRegistration;
                    } else {
                        throw new NullPointerException("PreRegArchiveDto or ZipBytes is null");
                    }
                } else {
                    throw new Exception("Service Error: " + error.getMessage());
                }
            } else {
                throw new Exception("Unsuccessful response or empty body");
            }
        };

        FutureTask<PreRegistrationList> futureTask = new FutureTask<>(callable);
        new Thread(futureTask).start();

        // This will block until the computation is done
        return futureTask.get();
    }

    private PreRegistrationList preparePreRegistration(CenterMachineDto centerMachineDto,
            PreRegistrationDto preRegistrationDto, String appointmentDate,String lastUpdatedTimeStamp) {

        LocalDateTime currentUTCTime = LocalDateTime.now(ZoneOffset.UTC);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedCurrentUTCTime = currentUTCTime.format(formatter);

        PreRegistrationList preRegistrationList = new PreRegistrationList();
        String id = UUID.randomUUID().toString();
        preRegistrationList.setId(id);
        preRegistrationList.setPreRegId(preRegistrationDto.getPreRegId());
        if(appointmentDate!=null){
           preRegistrationList.setAppointmentDate(appointmentDate);
        }
        preRegistrationList.setLastUpdatedPreRegTimeStamp(lastUpdatedTimeStamp == null ?
                String.valueOf(Timestamp.valueOf(formattedCurrentUTCTime)) : lastUpdatedTimeStamp);
        preRegistrationList.setPacketSymmetricKey(preRegistrationDto.getSymmetricKey());
        preRegistrationList.setStatusCode("Executed with success");
        preRegistrationList.setStatusComment("Executed with success");
        preRegistrationList.setPacketPath(preRegistrationDto.getPacketPath());
//        preRegistrationList.setsJobId(syncTransaction.getSyncJobId());
        preRegistrationList.setSynctrnId(centerMachineDto.getCenterId());
        preRegistrationList.setLangCode("eng");
        preRegistrationList.setIsActive(true);
        preRegistrationList.setIsDeleted(false);
        preRegistrationList.setCrBy(sharedPreferences.getString(USER_NAME, ""));
        preRegistrationList.setCrDtime(String.valueOf(System.currentTimeMillis()));
        preRegistrationDao.save(preRegistrationList);
        return preRegistrationList;
    }


    private String getToDate(Timestamp reqTime) {

        Calendar cal = Calendar.getInstance();
        cal.setTime(reqTime);
        if(this.globalParamRepository.getCachedStringGlobalParam(RegistrationConstants.PRE_REG_DAYS_LIMIT)!=null) {
            cal.add(Calendar.DATE,
                    Integer.parseInt(String.valueOf(this.globalParamRepository.getCachedStringGlobalParam(RegistrationConstants.PRE_REG_DAYS_LIMIT))));
        }

        return formatDate(cal);

    }
    private String formatDate(Calendar cal) {
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");// dd/MM/yyyy
        Date toDate = cal.getTime();

        return sdfDate.format(toDate);
    }

    private String getFromDate(Timestamp reqTime) {

        Calendar cal = Calendar.getInstance();
        cal.setTime(reqTime);

        return formatDate(cal);
    }
}