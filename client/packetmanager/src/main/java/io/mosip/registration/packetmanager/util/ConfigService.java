package io.mosip.registration.packetmanager.util;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigService {

    private final static Properties properties = new Properties();

    public static String getProperty(String key, Context context) {
        try {
            if(properties.isEmpty()) {
                AssetManager assetManager = context.getAssets();
                InputStream inputStream = assetManager.open("packetmanagerconfig.properties");
                properties.load(inputStream);
            }
        } catch (IOException e) {
            Log.e("Registration-client", "Failed to load properties file",e);
        }
        return properties.getProperty(key);
    }
}
