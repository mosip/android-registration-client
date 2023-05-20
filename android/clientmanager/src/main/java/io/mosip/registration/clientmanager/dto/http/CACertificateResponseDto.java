package io.mosip.registration.clientmanager.dto.http;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.Data;

import java.util.List;

@Data
public class CACertificateResponseDto {

    @SerializedName("lastSyncTime")
    @Expose
    private String lastSyncTime;

    @SerializedName("certificateDTOList")
    @Expose
    private List<CACertificateDto> certificateDTOList;
}
