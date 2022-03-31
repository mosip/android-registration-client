package io.mosip.registration.clientmanager.dto.http;

import lombok.Data;

@Data
public class AuthResponse {

    private String token;
    private String refreshToken;

}
