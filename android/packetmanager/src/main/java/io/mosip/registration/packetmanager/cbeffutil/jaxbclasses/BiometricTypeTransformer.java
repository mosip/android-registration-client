package io.mosip.registration.packetmanager.cbeffutil.jaxbclasses;

import io.mosip.registration.packetmanager.dto.PacketWriter.BiometricType;
import org.simpleframework.xml.transform.Transform;

public class BiometricTypeTransformer implements Transform<BiometricType> {

    @Override
    public BiometricType read(String value) {
        return BiometricType.fromValue(value);
    }

    @Override
    public String write(BiometricType value) {
        return value.value();
    }
}
