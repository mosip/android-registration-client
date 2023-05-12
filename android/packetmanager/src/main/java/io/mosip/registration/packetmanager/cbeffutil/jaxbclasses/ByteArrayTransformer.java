package io.mosip.registration.packetmanager.cbeffutil.jaxbclasses;

import io.mosip.registration.keymanager.util.CryptoUtil;
import org.simpleframework.xml.transform.Transform;

public class ByteArrayTransformer implements Transform<byte[]> {
    @Override
    public byte[] read(String value) throws Exception {
        return CryptoUtil.base64decoder.decode(value);
    }

    @Override
    public String write(byte[] value) throws Exception {
        if(value == null) { return ""; }
        return CryptoUtil.base64encoder.encodeToString(value);
    }
}
