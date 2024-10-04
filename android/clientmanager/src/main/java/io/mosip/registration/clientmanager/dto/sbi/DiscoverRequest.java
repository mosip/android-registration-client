package io.mosip.registration.clientmanager.dto.sbi;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class DiscoverRequest {

    private String type;

    public void setType(String type) {
        this.type = type;
    }
}
