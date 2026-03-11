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
    // Leeway (in seconds) for JWT expiry checks to tolerate clock skew between
    // client and server. This should be small and consistent across the app.
    private static final int TOKEN_EXPIRY_CLOCK_SKEW_LEEWAY_SECONDS = 90;

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
        List<String> roles = validateAndExtractRoles(token);

        SharedPreferences.Editor editor = this.context.getSharedPreferences(this.context.getString(R.string.app_name),
                Context.MODE_PRIVATE).edit();
        editor.putString(USER_TOKEN, token);
        editor.putString(USER_NAME, new JWT(token).getClaim(USERNAME).asString());
        editor.putString(PREFERRED_USERNAME, new JWT(token).getClaim(PREFERRED_USERNAME).asString());
        editor.putString(USER_EMAIL, new JWT(token).getClaim(EMAIL).asString());
        editor.putBoolean(IS_SUPERVISOR, roles.contains("REGISTRATION_SUPERVISOR"));
        editor.putBoolean(IS_DEFAULT, roles.contains("Default"));
        editor.putBoolean(IS_OFFICER, roles.contains("REGISTRATION_OFFICER"));
        editor.putBoolean(IS_OPERATOR, roles.contains("REGISTRATION_OPERATOR"));
        editor.apply();
        return roles;
    }

    /**
     * Synchronous variant of {@link #saveAuthToken(String)} that uses commit()
     * instead of apply() so callers can rely on the token being visible to
     * other threads immediately after this call returns.
     */
    public List<String> saveAuthTokenSync(@NonNull String token) throws Exception {
        List<String> roles = validateAndExtractRoles(token);

        final JWT jwt = new JWT(token);
        if(jwt.isExpired(TOKEN_EXPIRY_CLOCK_SKEW_LEEWAY_SECONDS))
            throw new Exception("Expired token found : " + jwt.getExpiresAt());

        Map<String,Object> realmAccess = jwt.getClaim(REALM_ACCESS).asObject(Map.class);
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
        if (!editor.commit()) {
            throw new Exception("Failed to persist auth token");
        }
        return roles;
    }

    /**
     * Common validation and role extraction logic used by both async and sync
     * token save methods.
     */
    private List<String> validateAndExtractRoles(@NonNull String token) throws Exception {
        final JWT jwt = new JWT(token);
        if(jwt.isExpired(TOKEN_EXPIRY_CLOCK_SKEW_LEEWAY_SECONDS))
            throw new Exception("Expired token found : " + jwt.getExpiresAt());

        Map<String,Object> realmAccess = jwt.getClaim(REALM_ACCESS).asObject(Map.class);
        Object rolesClaim = realmAccess != null ? realmAccess.get("roles") : null;
        if (!(rolesClaim instanceof List)) {
            throw new Exception("Unauthorized access, No roles");
        }
        List<String> roles = (List<String>) rolesClaim;

        if(roles.isEmpty())
            throw new Exception("Unauthorized access, No roles");

        if(!roles.contains("REGISTRATION_SUPERVISOR") && !roles.contains("REGISTRATION_OFFICER") && !roles.contains("REGISTRATION_OPERATOR"))
            throw new Exception("Unauthorized access, Required roles not found");

        return roles;
    }

    public String fetchAuthToken() {
        return this.context.getSharedPreferences(this.context.getString(R.string.app_name),
                Context.MODE_PRIVATE).getString(USER_TOKEN, null);
    }

    public String clearAuthToken(){
        SharedPreferences.Editor editor = this.context.getSharedPreferences(
                this.context.getString(R.string.app_name),Context.MODE_PRIVATE).edit();
//        editor.clear();
        editor.remove(USER_TOKEN);
        editor.remove(USER_NAME);
        editor.remove(PREFERRED_USERNAME);
        editor.remove(USER_EMAIL);
        editor.remove(IS_SUPERVISOR);
        editor.remove(IS_DEFAULT);
        editor.remove(IS_OFFICER);
        editor.remove(IS_OPERATOR);
        editor.apply();
        return this.context.getSharedPreferences(this.context.getString(R.string.app_name),
                Context.MODE_PRIVATE).getString(USER_TOKEN, null);
    }
}
