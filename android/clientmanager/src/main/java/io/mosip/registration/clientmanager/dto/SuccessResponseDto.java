package io.mosip.registration.clientmanager.dto;

import java.util.Map;


/**
 * The Class SuccessResponseDTO.
 *
 * @author Sreekar Chukka
 * @version 1.0.0
 */
public class SuccessResponseDto {
    private String code;
    private String message;
    private Map<String, Object> otherAttributes;
    private String infoType;
    public String getCode() {
        return code;
    }
    public void setCode(String code) {
        this.code = code;
    }
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    public Map<String, Object> getOtherAttributes() {
        return otherAttributes;
    }
    public void setOtherAttributes(Map<String, Object> otherAttributes) {
        this.otherAttributes = otherAttributes;
    }
    public String getInfoType() {
        return infoType;
    }
    public void setInfoType(String infoType) {
        this.infoType = infoType;
    }
}
