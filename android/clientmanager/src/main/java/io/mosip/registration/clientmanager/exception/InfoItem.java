package io.mosip.registration.clientmanager.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InfoItem implements Serializable {

    public String errorCode = null;
    public String errorText = null;
}
