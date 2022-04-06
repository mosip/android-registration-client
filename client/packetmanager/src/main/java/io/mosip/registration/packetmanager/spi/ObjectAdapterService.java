package io.mosip.registration.packetmanager.spi;

import java.io.InputStream;
import java.util.Map;

/**
 * @Author Anshul Vanawat
 */
public interface ObjectAdapterService {

    boolean putObject(String account, String container, String source, String process, String objectName, InputStream data);

    Map<String, Object> addObjectMetaData(String account, String container, String source, String process, String objectName, Map<String, Object> metadata);

    boolean removeContainer(String account, String container, String source, String process);

    String pack(String account, String container, String source, String process, String refId);

}