package io.mosip.registration.clientmanager.config;

import android.content.Context;
import android.content.SharedPreferences;
import io.mosip.registration.clientmanager.R;

public class SessionManager {

    private SessionManager manager = null;
    private static final String USER_TOKEN = "user_token";
    private Context context;
    private SharedPreferences sharedPreferences;

    private SessionManager(Context context) {
        if(manager == null) {
            synchronized (this) {
                this.context = context;
                this.sharedPreferences = this.context.getSharedPreferences(this.context.getString(R.string.app_name),
                        Context.MODE_PRIVATE);
            }
        }
    }

    public static SessionManager getSessionManager(Context context) {
        return new SessionManager(context);
    }

    public void saveAuthToken(String token) {
        SharedPreferences.Editor editor = this.sharedPreferences.edit();
        editor.putString(USER_TOKEN, token);
        editor.apply();
    }

    public String fetchAuthToken() {
        return this.sharedPreferences.getString(USER_TOKEN, null);
    }
}
