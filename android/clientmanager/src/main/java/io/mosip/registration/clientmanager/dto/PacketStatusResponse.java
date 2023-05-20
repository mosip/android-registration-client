package io.mosip.registration.clientmanager.dto;

import java.util.List;
import lombok.Data;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * The DTO Class PacketStatusReaderDTO.
 *
 * @author Anshul vanawat
 * @since 1.0.0
 */

@Data
public class PacketStatusResponse {

    @SerializedName("response")
    @Expose
    private List<PacketStatusDto> response = null;
    @SerializedName("errors")
    @Expose
    private List<Error> errors = null;
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("version")
    @Expose
    private String version;
    @SerializedName("responsetime")
    @Expose
    private String responsetime;

}
