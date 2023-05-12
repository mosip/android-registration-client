package io.mosip.registration.clientmanager.dto.http;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class UserDetailResponse {

    @SerializedName("lastSyncTime")
    @Expose
    private String lastSyncTime;

    @SerializedName("userDetails")
    @Expose
    private String userDetails;
}
