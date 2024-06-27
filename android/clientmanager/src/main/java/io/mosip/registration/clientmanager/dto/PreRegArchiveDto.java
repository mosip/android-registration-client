package io.mosip.registration.clientmanager.dto;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class PreRegArchiveDto {
    @SerializedName("pre-registration-id")
    @Expose
    private String preRegistrationId;

    @SerializedName("registration-client-id")
    @Expose
    private String registrationCenterId;

    @SerializedName("appointment-date")
    @Expose
    private String appointmentDate;

    @SerializedName("from-time-slot")
    @Expose
    private String timeSlotFrom;

    @SerializedName("to-time-slot")
    @Expose
    private String timeSlotTo;

    @SerializedName("zip-filename")
    @Expose
    private String fileName;

    @SerializedName("zip-bytes")
    @Expose
    private String zipBytes;


}
