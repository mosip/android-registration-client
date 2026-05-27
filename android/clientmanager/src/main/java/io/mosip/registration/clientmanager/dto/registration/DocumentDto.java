package io.mosip.registration.clientmanager.dto.registration;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class DocumentDto {

    private String type;
    private String format;
    private String refNumber;
    private String path;
    @JsonIgnore
    private String value;
    private List<byte[]> content = new ArrayList<>();
}
