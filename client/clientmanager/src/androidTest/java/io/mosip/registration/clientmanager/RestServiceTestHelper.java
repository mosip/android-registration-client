package io.mosip.registration.clientmanager;

import android.content.Context;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * @author Anshul vanawat
 * @since 01/06/2022.
 */
public class RestServiceTestHelper {

    public static final String GET_PACKET_STATUS_404 = "{\n" +
            "  \"error\": {\n" +
            "    \"code\": 404,\n" +
            "    \"message\": \"Not found\"\n" +
            "  }\n" +
            "}";

    public static String convertStreamToString(InputStream is) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line).append("\n");
        }
        reader.close();
        return sb.toString();
    }

    public static String getStringFromFile(Context context, String filePath) throws Exception {
        final InputStream stream = context.getResources().getAssets().open(filePath);

        String ret = convertStreamToString(stream);
        stream.close();
        return ret;
    }
}

