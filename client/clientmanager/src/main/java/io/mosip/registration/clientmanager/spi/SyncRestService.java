package io.mosip.registration.clientmanager.spi;

import io.mosip.registration.clientmanager.dto.http.RequestWrapper;
import io.mosip.registration.clientmanager.dto.http.ResponseWrapper;
import io.mosip.registration.clientmanager.dto.http.SyncRIDResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

import java.util.List;

public interface SyncRestService {

    @POST("v1/syncdata/authenticate/useridpwd")
    Call<ResponseWrapper<String>> login(@Body RequestWrapper<String> authRequest);

    @POST("registrationprocessor/v1/registrationstatus/syncV2")
    Call<ResponseWrapper<List<SyncRIDResponse>>> syncRID(@Header ("timestamp") String timestamp,
                                                         @Header ("Center-Machine-RefId") String refId,
                                                         @Body RequestWrapper<String> authRequest);

    //Content-Type:multipart/form-data
    @POST("registrationprocessor/v1/packetreceiver/registrationpackets")
    Call<ResponseWrapper<List<SyncRIDResponse>>> uploadPacket(@Body RequestWrapper<String> authRequest);

}
