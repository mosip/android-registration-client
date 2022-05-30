package io.mosip.registration.clientmanager.constant;

import java.util.List;

public enum Modality {

    FINGERPRINT_SLAB_LEFT(RegistrationConstants.LEFT_SLAB_ATTR),
    FINGERPRINT_SLAB_RIGHT(RegistrationConstants.RIGHT_SLAB_ATTR),
    FINGERPRINT_SLAB_THUMBS(RegistrationConstants.THUMBS_ATTR),
    IRIS_DOUBLE(RegistrationConstants.DOUBLE_IRIS_ATTR),
    FACE(RegistrationConstants.FACE_ATTR),
    EXCEPTION_PHOTO(RegistrationConstants.EXCEPTION_PHOTO_ATTR);

    List<String> attributes;

    Modality(List<String> attributes) {
        this.attributes = attributes;
    }

}
