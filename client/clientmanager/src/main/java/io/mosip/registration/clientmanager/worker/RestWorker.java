package io.mosip.registration.clientmanager.worker;


import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import io.mosip.registration.clientmanager.dto.http.RequestDto;
import io.mosip.registration.clientmanager.exception.RestServiceException;
import io.mosip.registration.clientmanager.util.DateUtils;
import io.mosip.registration.clientmanager.util.RestService;
import io.mosip.registration.keymanager.dto.PublicKeyRequestDto;
import io.mosip.registration.keymanager.dto.PublicKeyResponseDto;
import io.mosip.registration.keymanager.dto.SignRequestDto;
import io.mosip.registration.keymanager.dto.SignResponseDto;
import io.mosip.registration.keymanager.spi.ClientCryptoManagerService;
import io.mosip.registration.keymanager.util.KeyManagerConstant;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Base64;
import java.util.Map;


/**
 * Performs work synchronously on a background thread provided by WorkManager.
 * In case the work is preempted for any reason, the same instance of Worker is not reused.
 * This means that doWork() is called exactly once per Worker instance.
 * A new Worker is created if a unit of work needs to be rerun.
 * A Worker is given a maximum of ten minutes to finish its execution and return a ListenableWorker.Result.
 * After this time has expired, the Worker will be signalled to stop.
 */
public class RestWorker extends Worker {

    private static final String TAG = RestWorker.class.getSimpleName();

    private ClientCryptoManagerService clientCryptoManagerService;
    private String restKey;

    public RestWorker(@NonNull Context context, @NonNull WorkerParameters workerParams,
                      @NonNull ClientCryptoManagerService clientCryptoManagerService) {
        super(context, workerParams);
        this.clientCryptoManagerService = clientCryptoManagerService;
    }

    @SuppressLint("RestrictedApi")
    @NonNull
    @Override
    public Result doWork() {
        try {
            switch (getInputData().getString("restKey")) {
                case "auth" :
                    return doAuthCall();
                default:
                    break;
            }
        } catch (Throwable t) {
            Log.e(TAG, "Failed to complete rest worker", t);
        }
        return Result.failure(new Data.Builder()
                .put("errorCode", "UNKNOWN")
                .put("errorMessage", "Failed to complete rest worker").build());
    }

    @SuppressLint("RestrictedApi")
    private Result doAuthCall() throws JSONException {
        /*String username = getInputData().getString("username");
        String password = getInputData().getString("password");

        try {
            String timestamp = DateUtils.formatToISOString(LocalDateTime.now(ZoneOffset.UTC));
            PublicKeyRequestDto publicKeyRequestDto = new PublicKeyRequestDto();
            publicKeyRequestDto.setAlias(KeyManagerConstant.SIGNV_ALIAS);
            PublicKeyResponseDto publicKeyResponseDto = this.clientCryptoManagerService.getPublicKey(publicKeyRequestDto);
            String header = String.format("{\"kid\" : \"%s\"}", publicKeyResponseDto.getPublicKey());
            String payload = String.format("{\"userId\" : \"%s\", \"password\": \"%s\", \"authType\":\"%s\", \"timestamp\" : \"%s\"}",
                    username, password, "NEW", timestamp);
            SignRequestDto signRequestDto = new SignRequestDto();
            signRequestDto.setData(payload);
            SignResponseDto signResponseDto = this.clientCryptoManagerService.sign(signRequestDto);
            String data = String.format("%s.%s.%s", Base64.getUrlEncoder().encodeToString(header.getBytes()),
                    Base64.getUrlEncoder().encodeToString(payload.getBytes()), signResponseDto.getData());

            JSONObject requestWrapper = new JSONObject();
            requestWrapper.put("id", "");
            requestWrapper.put("version", "");
            requestWrapper.put("request", data);
            requestWrapper.put("requesttime", timestamp);

            RequestDto requestDto = new RequestDto();
            requestDto.setAuthRequired(false);
            requestDto.setRequestSignRequired(false);
            requestDto.setBody(requestWrapper);
            requestDto.setUrl("https://dev.mosip.net/v1/syncdata/authenticate/useridpwd");
            Map<String, Object> response = RestService.post(requestDto);
            JSONObject jsonObject = (JSONObject) RestService.getResponseObject(response);
            return Result.success(new Data.Builder()
                    .put("token", jsonObject.getString("token"))
                    .put("refreshToken", jsonObject.getString("refreshToken")).build());

        } catch (RestServiceException e) {
            Log.e(TAG, "Failed to invoke auth API", e);
            return Result.failure(new Data.Builder()
                    .put("errorCode", e.getErrorCode())
                    .put("errorMessage", e.getMessage()).build());
        }*/
        return Result.failure();
    }
}
