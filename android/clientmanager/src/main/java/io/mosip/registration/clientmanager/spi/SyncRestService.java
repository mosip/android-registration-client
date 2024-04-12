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
    Call<ResponseWrapper<ClientSettingDto>> fetchMasterData(@QueryMap Map<String, String> params);

    @GET("/v1/syncdata/clientsettings")
    Call<ResponseWrapper<ClientSettingDto>> fetchV1MasterData(@QueryMap Map<String, String> params);

    @GET("/v1/syncdata/getCertificate")
    Call<ResponseWrapper<CertificateResponse>> getPolicyKey(@Query("applicationId") String applicationId,
                                                            @Query("referenceId") String referenceId,
                                                            @Query("version") String version);

    @GET("/v1/syncdata/latestidschema")
    Call<ResponseBody> getLatestIdSchema(@Query("version") String version,
                                         @Query("domain") String domain);

    @GET("/v1/syncdata/v2/userdetails")
    Call<ResponseWrapper<UserDetailResponse>> fetchCenterUserDetails(@Query("keyindex") String keyIndex,
                                                                     @Query("version") String version);

    @GET("/v1/syncdata/userdetails")
    Call<ResponseWrapper<UserDetailResponse>> fetchV1CenterUserDetails(@Query("keyindex") String keyIndex,
                                                                     @Query("version") String version);

    @POST("/registrationprocessor/v1/registrationstatus/syncV2")
    Call<RegProcResponseWrapper<List<SyncRIDResponse>>> syncRID(@Header("timestamp") String timestamp,
                                                                @Header("Center-Machine-RefId") String refId,
                                                                @Body String encryptedData);

    @POST("/registrationprocessor/v1/registrationstatus/sync")
    Call<RegProcResponseWrapper<List<SyncRIDResponse>>> v1syncRID(@Header("timestamp") String timestamp,
                                                                @Header("Center-Machine-RefId") String refId,
                                                                @Body String encryptedData);

    @Multipart
    @POST("/registrationprocessor/v1/packetreceiver/registrationpackets")
    Call<RegProcResponseWrapper<UploadResponse>> uploadPacket(@Part MultipartBody.Part filePart);

    @POST("/registrationprocessor/v1/registrationstatus/packetexternalstatus")
    Call<PacketStatusResponse> getPacketStatus(@Body PacketStatusRequest packetStatusRequestDto);

    @POST("/registrationprocessor/v1/registrationstatus/search")
    Call<PacketStatusResponse> getV1PacketStatus(@Body PacketStatusRequest packetStatusRequestDto);

    @GET("/v1/syncdata/configs/{key_index}")
    Call<ResponseWrapper<Map<String, Object>>> getGlobalConfigs(@Path("key_index") String key_index,
                                                                @Query("version") String version);
    @GET("/v1/syncdata/configs/{machine_name}")
    Call<ResponseWrapper<Map<String, Object>>> getV1GlobalConfigs(@Path("machine_name") String machine_name,
                                                                @Query("version") String version);

    @GET("/v1/syncdata/getcacertificates")
    Call<ResponseWrapper<CACertificateResponseDto>> getCACertificates(@Query("lastupdated") String lastupdated,
                                                                      @Query("version") String version);

    @GET("/registrationprocessor/v1/registrationstatus/getCertificate?applicationId=IDA&referenceId=INTERNAL")
    Call<ResponseWrapper<Map<String, Object>>> getIDACertificate();

    @POST("/registrationprocessor/v1/registrationstatus/auth")
    Call<OnboardResponseWrapper<Map<String, Object>>> doOperatorAuth(@Header("Authorization") String authToken,
                                                              @Header("Signature") String signature,
                                                              @Header("Cookie") String cookie,
                                                              @Body Map<String, Object> requestMap);
    @GET
    Call<ResponseBody> downloadScript(@Url String url,
                                      @HeaderMap Map<String, String> headers,
                                      @Query("keyindex") String keyindex);
}
