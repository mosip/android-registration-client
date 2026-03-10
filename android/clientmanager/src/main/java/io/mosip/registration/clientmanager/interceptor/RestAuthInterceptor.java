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

public class RestAuthInterceptor implements Interceptor {

    private static final String COOKIE = "Cookie";
    private static final String TOKEN_TEMPLATE = "Authorization=%s";
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
            sessionManager.saveAuthToken(dbToken);
            return dbToken;
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
            return !jwt.isExpired(15);
        } catch (Exception e) {
            return false;
        }
    }
}
