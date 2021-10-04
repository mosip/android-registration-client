package io.mosip.registration.clientmanager.util;

//import io.mosip.kernel.core.util.StringUtils;

public class ObjectStoreUtil {

    private static final String SEPARATOR = "/";

    public static String getName(String source, String process, String objectName) {
        String finalObjectName = "";
        //if (StringUtils.isNotEmpty(source))
        if (!stringIsEmpty(source))
            finalObjectName = source + SEPARATOR;
        //if (StringUtils.isNotEmpty(process))
        if (!stringIsEmpty(process))
            finalObjectName = finalObjectName + process + SEPARATOR;

        finalObjectName = finalObjectName + objectName;

        return finalObjectName;
    }

    public static String getName(String container, String source, String process, String objectName) {
        String finalObjectName = "";
        //if (StringUtils.isNotEmpty(container))
        if (!stringIsEmpty(container))
            finalObjectName = container + SEPARATOR;
        //if (StringUtils.isNotEmpty(source))
        if (!stringIsEmpty(source))
            finalObjectName = finalObjectName + source + SEPARATOR;
        //if (StringUtils.isNotEmpty(process))
        if (!stringIsEmpty(process))
            finalObjectName = finalObjectName + process + SEPARATOR;

        finalObjectName = finalObjectName + objectName;

        return finalObjectName;
    }

    public static String getName(String objectName, String tagName) {
        String finalObjectName = "";
        //if (StringUtils.isNotEmpty(objectName))
        if (!stringIsEmpty(objectName))
            finalObjectName = objectName + SEPARATOR;
        //if (StringUtils.isNotEmpty(tagName))
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
