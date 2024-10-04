package io.mosip.registration.clientmanager.dto.http;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CACertificateDto {

    @SerializedName("certId")
    @Expose
    private String certId;

    @SerializedName("certData")
    @Expose
    private String certData;

    @SerializedName("partnerDomain")
    @Expose
    private String partnerDomain;

    private String createdBy;
    private LocalDateTime createdtimes;
    private String updatedBy;
    private LocalDateTime updatedtimes;
    private Boolean isDeleted;
    private LocalDateTime deletedtimes;
}
