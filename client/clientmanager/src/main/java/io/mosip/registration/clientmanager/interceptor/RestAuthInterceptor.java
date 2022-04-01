package io.mosip.registration.clientmanager.interceptor;

import android.content.Context;
import io.mosip.registration.clientmanager.config.SessionManager;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

public class RestAuthInterceptor implements Interceptor {

    private static final String COOKIE = "Cookie";
    private static final String TOKEN_TEMPLATE = "Authorization=%s";
    private SessionManager sessionManager;

    public RestAuthInterceptor(Context context) {
        this.sessionManager = SessionManager.getSessionManager(context);
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request.Builder requestBuilder = chain.request().newBuilder();
        if(this.sessionManager.fetchAuthToken() != null) {
            requestBuilder.addHeader(COOKIE, String.format(TOKEN_TEMPLATE, this.sessionManager.fetchAuthToken()));
        }
        return chain.proceed(requestBuilder.build());
    }
}
