package io.mosip.registration.packetmanager.cbeffutil.common;
import javax.xml.bind.annotation.adapters.XmlAdapter;

import io.mosip.registration.packetmanager.util.CryptoUtil;

/**
 * @Author Anshul Vanawat
 */
public class Base64Adapter extends XmlAdapter<String, byte[]> {

	@Override
	public byte[] unmarshal(String data) throws Exception {
		return CryptoUtil.decodeBase64(data);
	}

	@Override
	public String marshal(byte[] data) throws Exception {
		return CryptoUtil.encodeBase64String(data);
	}
}