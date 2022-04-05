package io.mosip.registration.clientmanager.dto.registration;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DocumentDto {

    private String type;
    private String format;
    private String refNumber;
    private String path;
    private byte[] content;
}
