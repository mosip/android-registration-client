package io.mosip.registration.clientmanager.dto;

import com.google.gson.annotations.SerializedName;

public class PreRegistrationDataSyncRequestDto {

    @SerializedName("registrationCenterId")
    private String registrationCenterId;

    @SerializedName("fromDate")
    private String fromDate;

    @SerializedName("toDate")
    private String toDate;

    // Getters and setters
    public String getRegistrationCenterId() {
        return registrationCenterId;
    }

    public void setRegistrationCenterId(String registrationCenterId) {
        this.registrationCenterId = registrationCenterId;
    }

    public String getFromDate() {
        return fromDate;
    }

    public void setFromDate(String fromDate) {
        this.fromDate = fromDate;
    }

    public String getToDate() {
        return toDate;
    }

    public void setToDate(String toDate) {
        this.toDate = toDate;
    }
}