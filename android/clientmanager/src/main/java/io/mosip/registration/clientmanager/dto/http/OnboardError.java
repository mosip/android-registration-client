package io.mosip.registration.clientmanager.dto.http;

import com.google.gson.annotations.SerializedName;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OnboardError {

    /**
     * The error code.
     */
    @SerializedName("errorCode")
    private String errorCode;
    /**
     * The error message.
     */
    @SerializedName("errorMessage")
    private String errorMessage;

    public String toString() {
        return "errorCode : " + errorCode +
                " message : " + errorMessage;
    }

}
