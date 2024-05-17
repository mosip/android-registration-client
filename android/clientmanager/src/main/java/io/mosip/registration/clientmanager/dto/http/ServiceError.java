package io.mosip.registration.clientmanager.dto.http;

import com.google.gson.annotations.SerializedName;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServiceError {

    /**
     * The error code.
     */
    @SerializedName("errorCode")
    private String errorCode;
    /**
     * The error message.
     */
    @SerializedName("message")
    private String message;

    public String toString() {
        return "errorCode : " + errorCode +
                " message : " + message;
    }
}