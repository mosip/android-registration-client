package io.mosip.registration.clientmanager.dto;

import com.google.gson.annotations.SerializedName;


public class PreRegistrationDataSyncDto {

    @SerializedName("version")
    private String version;

    @SerializedName("id")
    private String id;

    @SerializedName("requesttime")
    private String requesttime;

    @SerializedName("request")
    private PreRegistrationDataSyncRequestDto request;

    // Getters and setters
    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRequestTime() {
        return requesttime;
    }

    public void setRequestTime(String requestTime) {
        this.requesttime = requestTime;
    }

    public PreRegistrationDataSyncRequestDto getRequest() {
        return request;
    }

    public void setRequest(PreRegistrationDataSyncRequestDto request) {
        this.request = request;
    }
}