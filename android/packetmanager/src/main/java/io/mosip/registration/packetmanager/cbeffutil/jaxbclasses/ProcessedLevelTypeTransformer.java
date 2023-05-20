package io.mosip.registration.packetmanager.cbeffutil.jaxbclasses;

import org.simpleframework.xml.transform.Transform;

public class ProcessedLevelTypeTransformer implements Transform<ProcessedLevelType> {
    @Override
    public ProcessedLevelType read(String value) throws Exception {
        return ProcessedLevelType.fromValue(value);
    }

    @Override
    public String write(ProcessedLevelType value) throws Exception {
        return value.value();
    }
}
