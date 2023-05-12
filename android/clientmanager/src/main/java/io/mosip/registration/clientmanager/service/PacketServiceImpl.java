package io.mosip.registration.clientmanager.service;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.mosip.registration.clientmanager.R;
import io.mosip.registration.clientmanager.constant.PacketClientStatus;
import io.mosip.registration.clientmanager.constant.PacketServerStatus;
import io.mosip.registration.clientmanager.constant.PacketTaskStatus;
import io.mosip.registration.clientmanager.constant.RegistrationConstants;
import io.mosip.registration.clientmanager.dto.CenterMachineDto;
import io.mosip.registration.clientmanager.dto.Error;
import io.mosip.registration.clientmanager.dto.PacketIdDto;
import io.mosip.registration.clientmanager.dto.PacketStatusDto;
import io.mosip.registration.clientmanager.dto.PacketStatusRequest;
import io.mosip.registration.clientmanager.dto.PacketStatusResponse;
import io.mosip.registration.clientmanager.dto.http.PacketStatusUpdateDto;
import io.mosip.registration.clientmanager.dto.http.RegProcRequestWrapper;
import io.mosip.registration.clientmanager.dto.http.RegProcResponseWrapper;
import io.mosip.registration.clientmanager.dto.http.ServiceError;
import io.mosip.registration.clientmanager.dto.http.SyncRIDRequest;
import io.mosip.registration.clientmanager.dto.http.SyncRIDResponse;
import io.mosip.registration.clientmanager.dto.http.UploadResponse;
import io.mosip.registration.clientmanager.entity.Registration;
import io.mosip.registration.clientmanager.repository.RegistrationRepository;
import io.mosip.registration.clientmanager.repository.SyncJobDefRepository;
import io.mosip.registration.clientmanager.spi.AsyncPacketTaskCallBack;
import io.mosip.registration.clientmanager.spi.MasterDataService;
import io.mosip.registration.clientmanager.spi.PacketService;
import io.mosip.registration.clientmanager.spi.SyncRestService;
import io.mosip.registration.clientmanager.util.SyncRestUtil;
import io.mosip.registration.keymanager.util.CryptoUtil;
import io.mosip.registration.packetmanager.spi.IPacketCryptoService;
import io.mosip.registration.packetmanager.util.DateUtils;
import io.mosip.registration.packetmanager.util.HMACUtils2;
import io.mosip.registration.packetmanager.util.JsonUtils;
import lombok.NonNull;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@Singleton
public class PacketServiceImpl implements PacketService {

    private static final String TAG = PacketServiceImpl.class.getSimpleName();
    public static final String PACKET_EXTERNAL_STATUS_READER_ID = "mosip.registration.packet.external.status";
    public static final String PACKET_SYNC_ID = "mosip.registration.sync";
    public static final String PACKET_SYNC_VERSION = "1.0";
    public static final String PACKET_UPLOAD_FIELD = "file";
    public static final List<String> PACKET_UNSYNCED_STATUS = Arrays.asList(PacketClientStatus.CREATED.name(),
            PacketClientStatus.APPROVED.name(), PacketClientStatus.REJECTED.name());

    public static final List<String> PACKET_UPLOAD_STATUS = Arrays.asList(PacketServerStatus.RESEND.name(), PacketServerStatus.UPLOAD_PENDING.name());

    private Context context;
    private RegistrationRepository registrationRepository;
    private IPacketCryptoService packetCryptoService;
    private SyncRestService syncRestService;
    private MasterDataService masterDataService;

    @Inject
    public PacketServiceImpl(Context context, RegistrationRepository registrationRepository,
                             IPacketCryptoService packetCryptoService, SyncRestService syncRestService,
                             MasterDataService masterDataService) {
        this.context = context;
        this.registrationRepository = registrationRepository;
        this.packetCryptoService = packetCryptoService;
        this.syncRestService = syncRestService;
        this.masterDataService = masterDataService;
    }

    @Override
    public void syncRegistration(@NonNull String packetId) throws Exception {
        syncRegistration(packetId, new AsyncPacketTaskCallBack() {
            @Override
            public void inProgress(String RID) {
                //Do nothing
            }

            @Override
            public void onComplete(String RID, PacketTaskStatus status) {
                //Do nothing
            }
        });
    }

    @Override
    public void syncRegistration(@NonNull String packetId, AsyncPacketTaskCallBack callBack) throws Exception {
        CenterMachineDto centerMachineDto = masterDataService.getRegistrationCenterMachineDetails();

        Registration registration = registrationRepository.getRegistration(packetId);

        if (registration.getClientStatus() != null && !PACKET_UNSYNCED_STATUS.contains(registration.getClientStatus())) {
            Log.i(TAG, "Packet already synced >> " + registration.getClientStatus());
            callBack.onComplete(packetId, PacketTaskStatus.SYNC_ALREADY_COMPLETED);
            return;
        }

        RegProcRequestWrapper<List<SyncRIDRequest>> wrapper = new RegProcRequestWrapper<>();
        wrapper.setRequesttime(DateUtils.formatToISOString(LocalDateTime.now(ZoneOffset.UTC)));
        wrapper.setId(PACKET_SYNC_ID);
        wrapper.setVersion(PACKET_SYNC_VERSION);
        wrapper.setRequest(new ArrayList<SyncRIDRequest>());
        SyncRIDRequest syncRIDRequest = new SyncRIDRequest();
        syncRIDRequest.setRegistrationId(registration.getPacketId());
        syncRIDRequest.setRegistrationType(registration.getRegType().toUpperCase());
        syncRIDRequest.setPacketId(registration.getPacketId());
        syncRIDRequest.setAdditionalInfoReqId(registration.getAdditionalInfoReqId());
        syncRIDRequest.setSupervisorStatus(PacketClientStatus.APPROVED.name());

        if (registration.getAdditionalInfo() != null) {
            String additionalInfo = new String(registration.getAdditionalInfo());
            JSONObject jsonObject = new JSONObject(additionalInfo);
            syncRIDRequest.setName(jsonObject.has("name") ? jsonObject.getString("name") : null);
            syncRIDRequest.setPhone(jsonObject.has("phone") ? jsonObject.getString("phone") : null);
            syncRIDRequest.setEmail(jsonObject.has("email") ? jsonObject.getString("email") : null);
            syncRIDRequest.setLangCode(jsonObject.getString("langCode"));
        }

        try (FileInputStream fis = new FileInputStream(registration.getFilePath())) {
            byte[] byteArray = new byte[(int) fis.available()];
            fis.read(byteArray);
            syncRIDRequest.setPacketHashValue(HMACUtils2.digestAsPlainText(byteArray));
            syncRIDRequest.setPacketSize(BigInteger.valueOf(byteArray.length));
        }
        wrapper.getRequest().add(syncRIDRequest);

        byte[] cipher = this.packetCryptoService.encrypt(centerMachineDto.getMachineRefId(),
                JsonUtils.javaObjectToJsonString(wrapper).getBytes(StandardCharsets.UTF_8));

        Call<RegProcResponseWrapper<List<SyncRIDResponse>>> call = this.syncRestService.syncRID(
                DateUtils.formatToISOString(LocalDateTime.now(ZoneOffset.UTC)),
                centerMachineDto.getMachineRefId(), CryptoUtil.base64encoder.encodeToString(cipher));

        call.enqueue(new Callback<RegProcResponseWrapper<List<SyncRIDResponse>>>() {
            @Override
            public void onResponse(Call<RegProcResponseWrapper<List<SyncRIDResponse>>> call,
                                   Response<RegProcResponseWrapper<List<SyncRIDResponse>>> response) {
                if (response.isSuccessful()) {
                    ServiceError error = SyncRestUtil.getServiceError(response.body());
                    if (error == null && response.body().getResponse().get(0).getStatus().equalsIgnoreCase("SUCCESS")) {
                        registrationRepository.updateStatus(packetId, null, PacketClientStatus.SYNCED.name());
                        callBack.onComplete(packetId, PacketTaskStatus.SYNC_COMPLETED);
                        Toast.makeText(context, "Packet synced successfully", Toast.LENGTH_LONG).show();
                    } else {
                        callBack.onComplete(packetId, PacketTaskStatus.SYNC_FAILED);
                        Toast.makeText(context, "Packet sync failed : " + error, Toast.LENGTH_LONG).show();
                    }
                } else {
                    callBack.onComplete(packetId, PacketTaskStatus.SYNC_FAILED);
                    Toast.makeText(context, "Packet sync failed with Status Code : " + response.code(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<RegProcResponseWrapper<List<SyncRIDResponse>>> call, Throwable t) {
                Log.e(TAG, "Packet sync failed", t);
                callBack.onComplete(packetId, PacketTaskStatus.SYNC_FAILED);
                Toast.makeText(context, "Packet sync failed", Toast.LENGTH_LONG).show();
            }
        });
        callBack.inProgress(packetId);
    }

    @Override
    public void uploadRegistration(String packetId) {
        uploadRegistration(packetId, new AsyncPacketTaskCallBack() {
            @Override
            public void inProgress(String RID) {
                //Do nothing
            }

            @Override
            public void onComplete(String RID, PacketTaskStatus status) {
                //Do nothing
            }
        });
    }

    @Override
    public void uploadRegistration(String packetId, AsyncPacketTaskCallBack callBack) {
        Registration registration = registrationRepository.getRegistration(packetId);

        if (registration.getServerStatus() != null && !PACKET_UPLOAD_STATUS.contains(registration.getServerStatus())) {
            Log.i(TAG, "Packet already uploaded >> " + registration.getClientStatus());
            callBack.onComplete(packetId, PacketTaskStatus.UPLOAD_ALREADY_COMPLETED);
            return;
        }

        File file = new File(registration.getFilePath());
        MultipartBody.Part filePart = MultipartBody.Part.createFormData(PACKET_UPLOAD_FIELD, file.getName(),
                RequestBody.create(MediaType.parse("application/zip"), file));

        Call<RegProcResponseWrapper<UploadResponse>> call = this.syncRestService.uploadPacket(filePart);
        call.enqueue(new Callback<RegProcResponseWrapper<UploadResponse>>() {
            @Override
            public void onResponse(Call<RegProcResponseWrapper<UploadResponse>> call, Response<RegProcResponseWrapper<UploadResponse>> response) {
                if (response.isSuccessful()) {
                    ServiceError error = SyncRestUtil.getServiceError(response.body());
                    if (error == null) {
                        registrationRepository.updateStatus(packetId, response.body().getResponse().getStatus(),
                                PacketClientStatus.UPLOADED.name());
                        callBack.onComplete(packetId, PacketTaskStatus.UPLOAD_COMPLETED);
                        Toast.makeText(context, "Packet uploaded successfully", Toast.LENGTH_LONG).show();
                    } else {
                        callBack.onComplete(packetId, PacketTaskStatus.UPLOAD_FAILED);
                        Toast.makeText(context, "Packet uploaded failed : " + error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                } else {
                    callBack.onComplete(packetId, PacketTaskStatus.UPLOAD_FAILED);
                    Toast.makeText(context, "Packet uploaded failed with Status Code : " + response.code(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<RegProcResponseWrapper<UploadResponse>> call, Throwable t) {
                Log.e(TAG, "Packet uploaded failed", t);
                callBack.onComplete(packetId, PacketTaskStatus.UPLOAD_FAILED);
                Toast.makeText(context, "Packet uploaded failed", Toast.LENGTH_LONG).show();
            }
        });
        callBack.inProgress(packetId);
    }

    @Override
    public List<Registration> getAllRegistrations(int page, int pageLimit) {
        return this.registrationRepository.getAllRegistrations();
    }

    @Override
    public void syncAllPacketStatus() {
        List<Registration> registrations = this.registrationRepository.getAllRegistrations();

        if (registrations == null || registrations.size() == 0)
            return;

        PacketStatusRequest packetStatusRequest = new PacketStatusRequest();
        packetStatusRequest.setId(PACKET_EXTERNAL_STATUS_READER_ID);
        packetStatusRequest.setVersion(PACKET_SYNC_VERSION);
        packetStatusRequest.setRequesttime(DateUtils.formatToISOString(LocalDateTime.now(ZoneOffset.UTC)));

        List<PacketIdDto> packets = new ArrayList<>();

        for (Registration reg : registrations) {
            packets.add(new PacketIdDto(reg.getPacketId()));
        }

        packetStatusRequest.setRequest(packets);

        Call<PacketStatusResponse> call = this.syncRestService.getPacketStatus(packetStatusRequest);
        call.enqueue(new Callback<PacketStatusResponse>() {
            @Override
            public void onResponse(Call<PacketStatusResponse> call, Response<PacketStatusResponse> response) {
                if (response.isSuccessful()) {
                    List<PacketStatusDto> packetStatusList = response.body().getResponse();
                    int packetSyncSuccess = 0;

                    if (packetStatusList != null && packetStatusList.size() > 0) {
                        for (PacketStatusDto packetStatus : packetStatusList) {
                            PacketStatusUpdateDto updateDto = new PacketStatusUpdateDto(packetStatus.getPacketId(), packetStatus.getStatusCode());
                            registrationRepository.updateStatus(updateDto.getRegistrationId(), updateDto.getStatusCode(),
                                    PacketClientStatus.UPLOADED.name());
                            packetSyncSuccess++;
                        }
                    }

                    Toast.makeText(context, context.getString(R.string.packet_status_sync, packetSyncSuccess), Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(context, context.getString(R.string.packet_status_sync_failed_with_status_code, String.valueOf(response.code())), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<PacketStatusResponse> call, Throwable t) {
                Log.e(TAG, "Packet status sync failed", t);
                Toast.makeText(context, "Packet status sync failed", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public String getPacketStatus(String packetId) {
        Registration registration = registrationRepository.getRegistration(packetId);
        String packetStatus = registration.getServerStatus() == null ? registration.getClientStatus() : registration.getServerStatus();
        return packetStatus;
    }
}
