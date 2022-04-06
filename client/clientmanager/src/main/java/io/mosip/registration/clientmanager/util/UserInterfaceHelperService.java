package io.mosip.registration.clientmanager.util;

import android.content.Context;
import android.util.Log;
import io.mosip.registration.clientmanager.R;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Singleton
public class UserInterfaceHelperService {

    private static final String TAG = UserInterfaceHelperService.class.getSimpleName();
    private Context context;

    @Inject
    public UserInterfaceHelperService(Context context) {
        this.context = context;
    }

    public String loadJSONFromResource() {
        String json = null;
        try(InputStream is = this.context.getResources().openRawResource(R.raw.ui_specification)) {
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            json = new String(buffer, StandardCharsets.UTF_8);
        } catch (IOException ex) {
            Log.e(TAG, "Failed to load ui spec json", ex);
        }
        return json;
    }

    public String getSchemaJsonFromResource() {
        String json = null;
        try(InputStream is = this.context.getResources().openRawResource(R.raw.identity_schema)) {
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            json = new String(buffer, StandardCharsets.UTF_8);
        } catch (IOException ex) {
            Log.e(TAG, "Failed to load ui spec json", ex);
        }
        return json;
    }
}
