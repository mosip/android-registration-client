package io.mosip.registration.packetmanager.util;

/**
 * @Author Anshul Vanawat
 */
public class ObjectStoreUtil {

    private static final String SEPARATOR = "/";

    public static String getName(String source, String process, String objectName) {
        String finalObjectName = "";
        if (!stringIsEmpty(source))
            finalObjectName = source + SEPARATOR;
        if (!stringIsEmpty(process))
            finalObjectName = finalObjectName + process + SEPARATOR;

        finalObjectName = finalObjectName + objectName;

        return finalObjectName;
    }

    public static String getName(String container, String source, String process, String objectName) {
        String finalObjectName = "";
        if (!stringIsEmpty(container))
            finalObjectName = container + SEPARATOR;
        if (!stringIsEmpty(source))
            finalObjectName = finalObjectName + source + SEPARATOR;
        if (!stringIsEmpty(process))
            finalObjectName = finalObjectName + process + SEPARATOR;

        finalObjectName = finalObjectName + objectName;

        return finalObjectName;
    }

    public static String getName(String objectName, String tagName) {
        String finalObjectName = "";
        if (!stringIsEmpty(objectName))
            finalObjectName = objectName + SEPARATOR;
        if (!stringIsEmpty(tagName))
            finalObjectName = finalObjectName + tagName;
        return finalObjectName;
    }

    private static boolean stringIsEmpty(String str) {
        if (str == null || str.trim() == "") {
            return true;
        } else
            return false;
    }
}
