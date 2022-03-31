package io.mosip.registration.clientmanager.factory;

import io.mosip.registration.clientmanager.dto.http.RequestWrapper;
import io.mosip.registration.clientmanager.util.DateUtils;
import io.mosip.registration.keymanager.dto.PublicKeyRequestDto;
import io.mosip.registration.keymanager.dto.PublicKeyResponseDto;
import io.mosip.registration.keymanager.dto.SignRequestDto;
import io.mosip.registration.keymanager.dto.SignResponseDto;
import io.mosip.registration.keymanager.spi.ClientCryptoManagerService;
import io.mosip.registration.keymanager.util.CryptoUtil;
import io.mosip.registration.keymanager.util.KeyManagerConstant;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Base64;


@Singleton
public class SyncRestFactory {

    private static final String TAG = SyncRestFactory.class.getSimpleName();

    private ClientCryptoManagerService clientCryptoManagerService;

    @Inject
    public SyncRestFactory(ClientCryptoManagerService clientCryptoManagerService) {
        this.clientCryptoManagerService= clientCryptoManagerService;
    }

    public RequestWrapper<String> getAuthRequest(String username, String password) {
        String timestamp = DateUtils.formatToISOString(LocalDateTime.now(ZoneOffset.UTC));

        PublicKeyRequestDto publicKeyRequestDto = new PublicKeyRequestDto();
        publicKeyRequestDto.setAlias(KeyManagerConstant.SIGNV_ALIAS);
        PublicKeyResponseDto publicKeyResponseDto = this.clientCryptoManagerService.getPublicKey(publicKeyRequestDto);
        String header = String.format("{\"kid\" : \"%s\"}", CryptoUtil.computeFingerPrint(
                CryptoUtil.base64decoder.decode(publicKeyResponseDto.getPublicKey().getBytes(StandardCharsets.UTF_8)), null));
        String payload = String.format("{\"userId\" : \"%s\", \"password\": \"%s\", \"authType\":\"%s\", \"timestamp\" : \"%s\"}",
                username, password, "NEW", timestamp);

        SignRequestDto signRequestDto = new SignRequestDto();
        signRequestDto.setData(CryptoUtil.base64encoder.encodeToString(payload.getBytes(StandardCharsets.UTF_8)));
        SignResponseDto signResponseDto = this.clientCryptoManagerService.sign(signRequestDto);
        String data = String.format("%s.%s.%s", CryptoUtil.base64encoder.encodeToString(header.getBytes()),
                CryptoUtil.base64encoder.encodeToString(payload.getBytes()), signResponseDto.getData());

        RequestWrapper<String> requestWrapper = new RequestWrapper<>();
        requestWrapper.setRequest(data);
        requestWrapper.setRequestTime(LocalDateTime.now(ZoneOffset.UTC));
        return requestWrapper;
    }
}
