package io.mosip.registration.clientmanager.dto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class PreRegistrationIdsDto {

    @SerializedName("transactionId")
    @Expose
    private String transactionId;

    @SerializedName("countOfPreRegIds")
    @Expose
    private String countOfPreRegIds;

    @SerializedName("preRegistrationIds")
    @Expose
    private Map<String, String> preRegistrationIds;
}
