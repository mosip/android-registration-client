package io.mosip.registration.clientmanager.config;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;

import com.auth0.android.jwt.JWT;

import java.util.List;
import java.util.Map;

import io.mosip.registration.clientmanager.R;

public class SessionManager {

    private static final String TAG = SessionManager.class.getSimpleName();
    public static final String IS_OFFICER = "is_officer";
    public static final String IS_SUPERVISOR = "is_supervisor";
    public static final String IS_OPERATOR = "is_operator";
    public static final String IS_DEFAULT = "is_default";
    public static final String USER_NAME = "user_name";
    public static final String USER_ID = "user_id";
    public static final String USER_TOKEN = "user_token";
    public static final String RID = "current_rid";
    public static final String USER_EMAIL = "user_email";

    private static SessionManager manager = null;
    private static final String REALM_ACCESS = "realm_access";
    private static final String USERNAME = "name";
    public static final String PREFERRED_USERNAME = "preferred_username";
    private static final String EMAIL = "email";

    SharedPreferences sharedPreferences;

    private Context context;

    private SessionManager(Context context) {
        this.context = context;
        sharedPreferences = this.context.
                getSharedPreferences(
                        this.context.getString(R.string.app_name),
                        Context.MODE_PRIVATE);
    }

    public static SessionManager getSessionManager(Context context) {
        if(manager == null)
            manager = new SessionManager(context);
        return manager;
    }

    public List<String> saveAuthToken(@NonNull String token) throws Exception {
        final JWT jwt = new JWT(token);
        if(jwt.isExpired(15))
            throw new Exception("Expired token found : " + jwt.getExpiresAt());

        Map<String,Object> realmAccess = jwt.getClaim(REALM_ACCESS).asObject(Map.class);
        List<String> roles = (List<String>)realmAccess.get("roles");

        if(roles.isEmpty())
            throw new Exception("Unauthorized access, No roles");

        if(!roles.contains("REGISTRATION_SUPERVISOR") && !roles.contains("REGISTRATION_OFFICER"))
            throw new Exception("Unauthorized access, Required roles not found");

        SharedPreferences.Editor editor = this.context.getSharedPreferences(this.context.getString(R.string.app_name),
                Context.MODE_PRIVATE).edit();
        editor.putString(USER_TOKEN, token);
        editor.putString(USER_NAME, jwt.getClaim(USERNAME).asString());
        editor.putString(PREFERRED_USERNAME, jwt.getClaim(PREFERRED_USERNAME).asString());
        editor.putString(USER_EMAIL, jwt.getClaim(EMAIL).asString());
        editor.putBoolean(IS_SUPERVISOR, roles.contains("REGISTRATION_SUPERVISOR"));
        editor.putBoolean(IS_DEFAULT, roles.contains("Default"));
        editor.putBoolean(IS_OFFICER, roles.contains("REGISTRATION_OFFICER"));
        editor.putBoolean(IS_OPERATOR, roles.contains("REGISTRATION_OPERATOR"));
        editor.apply();
        return roles;
    }

    public String fetchAuthToken() {
        return this.context.getSharedPreferences(this.context.getString(R.string.app_name),
                Context.MODE_PRIVATE).getString(USER_TOKEN, null);
    }

    public String clearAuthToken(){
        SharedPreferences.Editor editor = this.context.getSharedPreferences(
                this.context.getString(R.string.app_name),Context.MODE_PRIVATE).edit();
        editor.clear();
        editor.apply();
        return this.context.getSharedPreferences(this.context.getString(R.string.app_name),
                Context.MODE_PRIVATE).getString(USER_TOKEN, null);
    }
}
