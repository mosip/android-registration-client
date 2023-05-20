
package io.mosip.registration.clientmanager.dto;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * The DTO Class PacketStatusReaderDTO.
 *
 * @author Anshul vanawat
 * @since 1.0.0
 */


@Data
public class PacketStatusRequest {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("request")
    @Expose
    private List<PacketIdDto> request = null;
    @SerializedName("requesttime")
    @Expose
    private String requesttime;
    @SerializedName("version")
    @Expose
    private String version;

}