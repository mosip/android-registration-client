package io.mosip.registration.clientmanager.dto.http;

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
    private String errorCode;
    /**
     * The error message.
     */
    private String errorMessage;

    public String toString() {
        return "errorCode : " + errorCode +
                " message : " + errorMessage;
    }

}
