package io.mosip.registration.clientmanager.dto.objectstore;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ObjectDto {

    private String source;
    private String process;
    private String objectName;
    private Date lastModified;
}
