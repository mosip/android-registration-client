package io.mosip.registration.packetmanager.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Document {

    private String value;
    private String type;
    private String format;
    private String refNumber;
}
