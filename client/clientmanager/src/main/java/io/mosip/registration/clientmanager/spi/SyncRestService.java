package io.mosip.registration.clientmanager.spi;

import io.mosip.registration.clientmanager.dto.PacketStatusRequest;
import io.mosip.registration.clientmanager.dto.PacketStatusResponse;
import io.mosip.registration.clientmanager.dto.http.*;
import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.*;

import java.util.List;
import java.util.Map;

public interface SyncRestService {

    @POST("/v1/syncdata/authenticate/useridpwd")
    Call<ResponseWrapper<String>> login(@Body RequestWrapper<String> authRequest);

    @GET("/v1/syncdata/v2/clientsettings")
    Call<ResponseWrapper<ClientSettingDto>> fetchMasterDate(@QueryMap Map<String, String> params);

    @GET("/v1/syncdata/getCertificate")
    Call<ResponseWrapper<CertificateResponse>> getCertificate(@Query("applicationId") String applicationId,
                                                              @Query("referenceId") String referenceId);

    @GET("/v1/syncdata/latestidschema")
    Call<ResponseBody> getLatestIdSchema();

    @GET("/v1/syncdata/v2/userdetails")
    Call<ResponseWrapper<UserDetailResponse>> fetchCenterUserDetails(@Query("keyindex") String keyIndex);

    @POST("/registrationprocessor/v1/registrationstatus/syncV2")
    Call<RegProcResponseWrapper<List<SyncRIDResponse>>> syncRID(@Header("timestamp") String timestamp,
                                                                @Header("Center-Machine-RefId") String refId,
                                                                @Body String encryptedData);

    @Multipart
    @POST("/registrationprocessor/v1/packetreceiver/registrationpackets")
    Call<RegProcResponseWrapper<UploadResponse>> uploadPacket(@Part MultipartBody.Part filePart);


    @POST("/registrationprocessor/v1/registrationstatus/packetexternalstatus")
    Call<PacketStatusResponse> getPacketStatus(@Body PacketStatusRequest packetStatusRequestDto);


    @GET("/v1/syncdata/configs/{key_index}")
    Call<ResponseWrapper<Map<String, Object>>> getGlobalConfigs(@Path("key_index") String key_index);

    @GET("/v1/syncdata/getcacertificates")
    Call<ResponseWrapper<CACertificateResponseDto>> getCACertificates(@Query("lastupdated") String lastupdated);
}
