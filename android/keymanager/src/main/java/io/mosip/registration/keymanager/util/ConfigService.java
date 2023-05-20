package io.mosip.registration.keymanager.util;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @Author George T Abraham
 * @Author Eric John
 */
public class ConfigService {

    private final static Properties properties = new Properties();

    public static String getProperty(String key, Context context) {
        try {
            if(properties.isEmpty()) {
                AssetManager assetManager = context.getAssets();
                InputStream inputStream = assetManager.open("config.properties");
                properties.load(inputStream);
            }
        } catch (IOException e) {
            Log.e("Registration-client", "Failed to load properties file",e);
        }
        return properties.getProperty(key);
    }
}
