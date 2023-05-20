package io.mosip.registration.packetmanager.cbeffutil.jaxbclasses;

import org.simpleframework.xml.transform.Transform;

public class PurposeTypeTransformer implements Transform<PurposeType> {
    @Override
    public PurposeType read(String value) throws Exception {
        return PurposeType.fromValue(value);
    }

    @Override
    public String write(PurposeType value) throws Exception {
        return value.value();
    }
}
