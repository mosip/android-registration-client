package io.mosip.registration.clientmanager.config;

import android.app.Application;
import android.content.Context;
import android.util.Log;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dagger.Module;
import dagger.Provides;
import io.mosip.registration.clientmanager.BuildConfig;
import io.mosip.registration.clientmanager.interceptor.RestAuthInterceptor;
import io.mosip.registration.clientmanager.repository.GlobalParamRepository;
import io.mosip.registration.clientmanager.spi.SyncRestService;
import io.mosip.registration.clientmanager.util.LocalDateTimeDeserializer;
import io.mosip.registration.clientmanager.util.LocalDateTimeSerializer;
import okhttp3.Cache;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import javax.inject.Singleton;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

@Module
public class NetworkModule {

    Application application;
    Context appContext;

    public NetworkModule(Application application) {
        this.application = application;
        this.appContext = application.getApplicationContext();
    }

    @Provides
    @Singleton
    Cache provideHttpCache() {
        int cacheSize = 10 * 1024 * 1024;
        return new Cache(application.getCacheDir(), cacheSize);
    }

    @Provides
    @Singleton
    Gson provideGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES);
        gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeSerializer());
        gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeDeserializer());
        return gsonBuilder.create();
    }

    @Provides
    @Singleton
    OkHttpClient provideOkhttpClient(Cache cache, GlobalParamRepository globalParamRepository) {
        OkHttpClient.Builder client = new OkHttpClient.Builder();
        client.cache(cache);
        client.addInterceptor(new RestAuthInterceptor(appContext));

        String readTimeoutStr = globalParamRepository.getCachedStringReadTimeOut();
        String writeTimeoutStr = globalParamRepository.getCachedStringWriteTimeOut();

        long readTimeout = BuildConfig.HTTP_READ_TIMEOUT;  // Default from BuildConfig
        long writeTimeout = BuildConfig.HTTP_WRITE_TIMEOUT; // Default from BuildConfig

        // Try to get from GlobalParamRepository
        readTimeout = parseTimeout(readTimeoutStr, readTimeout, "readTimeout");
        writeTimeout = parseTimeout(writeTimeoutStr, writeTimeout, "writeTimeout");
        client.readTimeout(readTimeout, TimeUnit.MILLISECONDS);
        client.writeTimeout(writeTimeout, TimeUnit.MILLISECONDS);
        return client.build();
    }

    @Provides
    @Singleton
    Retrofit provideRetrofit(Gson gson, OkHttpClient okHttpClient) {
        return new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create(gson))
                .baseUrl(BuildConfig.BASE_URL)
                .client(okHttpClient)
                .build();
    }

    @Provides
    @Singleton
    SyncRestService provideSyncRestService(Retrofit retrofit) {
        return retrofit.create(SyncRestService.class);
    }

    private long parseTimeout(String rawValue, long fallback, String label) {
        if (rawValue == null || rawValue.trim().isEmpty()) {
            return fallback;
        }
        try {
            return Long.parseLong(rawValue.trim());
        } catch (NumberFormatException ex) {
            Log.w("NetworkModule", "Invalid " + label + " in GlobalParamRepository: " + rawValue + ", using fallback: " + fallback, ex);
            return fallback;
        }
    }
}