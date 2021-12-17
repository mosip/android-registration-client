package io.mosip.registration.packetmanager.spi;

import java.io.InputStream;
import java.util.Map;

public interface ObjectAdapterService {

    public boolean putObject(String account, String container, String source, String process, String objectName, InputStream data);

    public Map<String, Object> addObjectMetaData(String account, String container, String source, String process, String objectName, Map<String, Object> metadata);

    public boolean removeContainer(String account, String container, String source, String process);

    //TODO encryption
    public boolean pack(String account, String container, String source, String process);

}