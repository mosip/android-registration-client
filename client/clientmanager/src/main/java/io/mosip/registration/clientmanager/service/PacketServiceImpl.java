package io.mosip.registration.clientmanager.service;

import android.content.Context;
import android.widget.Toast;
import io.mosip.registration.clientmanager.constant.PacketClientStatus;
import io.mosip.registration.clientmanager.dto.http.*;
import io.mosip.registration.clientmanager.entity.Registration;
import io.mosip.registration.clientmanager.repository.RegistrationRepository;
import io.mosip.registration.clientmanager.spi.PacketService;
import io.mosip.registration.clientmanager.spi.SyncRestService;
import io.mosip.registration.clientmanager.util.SyncRestUtil;
import io.mosip.registration.keymanager.dto.CryptoManagerRequestDto;
import io.mosip.registration.keymanager.spi.CryptoManagerService;
import io.mosip.registration.keymanager.util.CryptoUtil;
import io.mosip.registration.packetmanager.spi.IPacketCryptoService;
import io.mosip.registration.packetmanager.util.DateUtils;
import io.mosip.registration.packetmanager.util.HMACUtils2;
import io.mosip.registration.packetmanager.util.JsonUtils;
import lombok.NonNull;
import org.json.JSONObject;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.FileInputStream;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

@Singleton
public class PacketServiceImpl implements PacketService {

    private static final String TAG = PacketServiceImpl.class.getSimpleName();
    public static final String PACKET_SYNC_ID = "mosip.registration.sync";
    public static final String PACKET_SYNC_VERSION = "1.0";

    private Context context;
    private RegistrationRepository registrationRepository;
    private IPacketCryptoService packetCryptoService;
    private SyncRestService syncRestService;

    @Inject
    public PacketServiceImpl(Context context, RegistrationRepository registrationRepository,
                             IPacketCryptoService packetCryptoService, SyncRestService syncRestService) {
        this.context = context;
        this.registrationRepository = registrationRepository;
        this.packetCryptoService = packetCryptoService;
        this.syncRestService = syncRestService;
    }

    @Override
    public List<Registration> getUnSyncedRegistrations() {
        return null;
    }

    @Override
    public List<Registration> getSyncedRegistrations() {
        return null;
    }

    @Override
    public void syncRegistration(@NonNull String packetId) throws Exception {
        String refId = "10001_10008";
        Registration registration = registrationRepository.getRegistration(packetId);

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
            syncRIDRequest.setName(jsonObject.getString("name"));
            syncRIDRequest.setPhone(jsonObject.getString("phone"));
            syncRIDRequest.setEmail(jsonObject.getString("email"));
            syncRIDRequest.setLangCode(jsonObject.getString("langCode"));
        }

        try (FileInputStream fis = new FileInputStream(registration.getFilePath())) {
            byte[] byteArray = new byte[(int) fis.available()];
            fis.read(byteArray);
            syncRIDRequest.setPacketHashValue(HMACUtils2.digestAsPlainText(byteArray));
            syncRIDRequest.setPacketSize(BigInteger.valueOf(byteArray.length));
        }
        wrapper.getRequest().add(syncRIDRequest);

        byte[] cipher = this.packetCryptoService.encrypt(refId,
                JsonUtils.javaObjectToJsonString(wrapper).getBytes(StandardCharsets.UTF_8));

        Call<RegProcResponseWrapper<List<SyncRIDResponse>>> call = this.syncRestService.syncRID(
                DateUtils.formatToISOString(LocalDateTime.now(ZoneOffset.UTC)),
                refId, CryptoUtil.base64encoder.encodeToString(cipher));
        call.enqueue(new Callback<RegProcResponseWrapper<List<SyncRIDResponse>>>() {
            @Override
            public void onResponse(Call<RegProcResponseWrapper<List<SyncRIDResponse>>> call,
                                   Response<RegProcResponseWrapper<List<SyncRIDResponse>>> response) {
                if(response.isSuccessful()) {
                    ServiceError error = SyncRestUtil.getServiceError(response.body());
                    if(error == null) {
                        registrationRepository.updateServerStatus(packetId, response.body().getResponse().get(0).getStatus());
                        Toast.makeText(context, "Packet synced successfully", Toast.LENGTH_LONG).show();
                    }
                    else
                        Toast.makeText(context, "Packet sync failed : " + error.getMessage(), Toast.LENGTH_LONG).show();
                }
                else
                    Toast.makeText(context, "Packet sync failed with Status Code : " + response.code(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Call<RegProcResponseWrapper<List<SyncRIDResponse>>> call, Throwable t) {
                Toast.makeText(context, "Packet sync failed", Toast.LENGTH_LONG).show();
            }
        });

    }

    @Override
    public void uploadRegistration(String packetId) throws Exception {

    }

    @Override
    public List<Registration> getAllRegistrations(int page, int pageLimit) {
        return null;
    }
}
