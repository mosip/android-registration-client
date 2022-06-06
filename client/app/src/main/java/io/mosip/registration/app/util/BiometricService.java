package io.mosip.registration.app.util;

import io.mosip.registration.clientmanager.constant.Modality;

public interface BiometricService {

    void startBiometricCapture(Modality modality);
}
