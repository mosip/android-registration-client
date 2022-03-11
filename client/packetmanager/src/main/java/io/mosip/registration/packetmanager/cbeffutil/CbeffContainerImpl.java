/**
 * 
 */
package io.mosip.registration.packetmanager.cbeffutil;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import io.mosip.registration.packetmanager.cbeffutil.jaxbclasses.BIR;
import io.mosip.registration.packetmanager.cbeffutil.jaxbclasses.BIRInfoType;
import io.mosip.registration.packetmanager.cbeffutil.jaxbclasses.BIRType;

/**
 * @Author Anshul Vanawat
 */
public class CbeffContainerImpl {

	private BIRType birType;


	public BIRType createBIRType(List<BIR> birList) {
		load();
		List<BIRType> birTypeList = new ArrayList<>();
		if (birList != null && birList.size() > 0) {
			for (BIR bir : birList) {
				birTypeList.add(bir.toBIRType(bir));
			}
		}
		birType.setBir(birTypeList);
		return birType;
	}

	private void load() {
		birType = new BIRType();
		BIRInfoType birInfo = new BIRInfoType();
		birInfo.setIntegrity(false);
		birType.setBIRInfo(birInfo);
	}

	public static byte[] createXMLBytes(BIRType bir) throws Exception {
		JAXBContext jaxbContext = JAXBContext.newInstance(BIRType.class);
		Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
		jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE); // To
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		OutputStreamWriter writer = new OutputStreamWriter(baos);
		jaxbMarshaller.marshal(bir, writer);
		byte[] savedData = baos.toByteArray();
		writer.close();
		return savedData;
	}

}
