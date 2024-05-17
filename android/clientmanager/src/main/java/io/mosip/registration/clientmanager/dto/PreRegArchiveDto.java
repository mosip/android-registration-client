package io.mosip.registration.clientmanager.dto;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.sql.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * The DTO Class PreRegArchiveDTO.
 *
 * @author Sreekar Chukka
 * @since 1.0.0
 */
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
    private Date appointmentDate;

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
