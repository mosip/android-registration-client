package io.mosip.registration.clientmanager.interceptor;

import android.content.Context;
import io.mosip.registration.clientmanager.config.SessionManager;
import io.mosip.registration.clientmanager.dao.UserTokenDao;
import io.mosip.registration.clientmanager.entity.UserToken;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

import com.auth0.android.jwt.JWT;

import java.io.IOException;
import java.util.Date;

public class RestAuthInterceptor implements Interceptor {

    private static final String COOKIE = "Cookie";
    private static final String TOKEN_TEMPLATE = "Authorization=%s";
    // Leeway in seconds for JWT expiry checks (clock skew only). Keep this small and
    // consistent with SessionManager.
    private static final int TOKEN_EXPIRY_CLOCK_SKEW_LEEWAY_SECONDS = 90;
    // Extra safety buffer (in seconds) applied at decision points so we avoid sending
    // requests with tokens that are about to expire.
    private static final int TOKEN_EXPIRY_SAFETY_BUFFER_SECONDS = 120;
    private final Object restoreLock = new Object();
    private SessionManager sessionManager;
    private final UserTokenDao userTokenDao;
    private final Context appContext;

    public RestAuthInterceptor(Context context, UserTokenDao userTokenDao) {
        this.appContext = context.getApplicationContext();
        this.sessionManager = SessionManager.getSessionManager(this.appContext);
        this.userTokenDao = userTokenDao;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request.Builder requestBuilder = chain.request().newBuilder();
        String token = this.sessionManager.fetchAuthToken();
        token = ensureValidSessionToken(token);
        if (token != null) {
            requestBuilder.addHeader(COOKIE, String.format(TOKEN_TEMPLATE, token));
        }
        return chain.proceed(requestBuilder.build());
    }

    private String ensureValidSessionToken(String token) {
        // If there's no token (or it is expired), try to restore it from DB so background jobs don't fail
        // just because SharedPreferences were cleared.
        if (isTokenValid(token)) {
            return token;
        }

        try {
            synchronized (restoreLock) {
                // Another thread may have already restored a valid token.
                String currentToken = this.sessionManager.fetchAuthToken();
                if (isTokenValid(currentToken)) {
                    return currentToken;
                }

                String userId = appContext
                        .getSharedPreferences(appContext.getString(io.mosip.registration.clientmanager.R.string.app_name), Context.MODE_PRIVATE)
                        .getString(SessionManager.PREFERRED_USERNAME, null);
                if (userId == null || userId.isEmpty()) {
                    return null;
                }

                UserToken userToken = userTokenDao.findByUsername(userId);
                if (userToken == null) {
                    return null;
                }

                String dbToken = userToken.getToken();
                if (!isTokenValid(dbToken)) {
                    return null;
                }

                // Repopulate session prefs so subsequent calls use the same token.
                sessionManager.saveAuthTokenSync(dbToken);
                return dbToken;
            }
        } catch (Exception ignored) {
            return null;
        }
    }

    private boolean isTokenValid(String token) {
        if (token == null || token.isEmpty()) {
            return false;
        }
        try {
            JWT jwt = new JWT(token);
            if (jwt.isExpired(TOKEN_EXPIRY_CLOCK_SKEW_LEEWAY_SECONDS)) {
                return false;
            }
            Date expiresAt = jwt.getExpiresAt();
            if (expiresAt != null) {
                long msRemaining = expiresAt.getTime() - System.currentTimeMillis();
                if (msRemaining <= TOKEN_EXPIRY_SAFETY_BUFFER_SECONDS * 1000L) {
                    return false;
                }
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
