package io.mosip.registration.clientmanager.config;

import android.app.Application;
import android.content.Context;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.io.File;
import java.time.LocalDateTime;

import io.mosip.registration.clientmanager.interceptor.RestAuthInterceptor;
import io.mosip.registration.clientmanager.spi.SyncRestService;
import io.mosip.registration.clientmanager.util.LocalDateTimeDeserializer;
import io.mosip.registration.clientmanager.util.LocalDateTimeSerializer;
import okhttp3.Cache;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static io.mosip.registration.clientmanager.BuildConfig.BASE_URL;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 33)
public class NetworkModuleTest {

    @Mock
    private Application mockApplication;

    @Mock
    private Context mockContext;

    @Mock
    private Cache mockCache;

    @Mock
    private Gson mockGson;

    @Mock
    private OkHttpClient mockOkHttpClient;

    @Mock
    private Retrofit mockRetrofit;

    @Mock
    private SyncRestService mockSyncRestService;

    private NetworkModule networkModule;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        when(mockApplication.getApplicationContext()).thenReturn(mockContext);
        when(mockApplication.getCacheDir()).thenReturn(new File("cache"));
        networkModule = new NetworkModule(mockApplication);
    }

    @Test
    public void testConstructor_InitializesFields() {
        assertEquals(mockApplication, networkModule.application);
        assertEquals(mockContext, networkModule.appContext);
    }

    @Test
    public void testProvideHttpCache_ReturnsCache() {
        Cache cache = networkModule.provideHttpCache();
        assertNotNull(cache);
        assertEquals(10 * 1024 * 1024, cache.maxSize());
        assertEquals(new File("cache"), cache.directory());
    }

    @Test
    public void testProvideGson_ReturnsConfiguredGson() {
        Gson gson = networkModule.provideGson();
        assertNotNull(gson);
        GsonBuilder builder = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeSerializer())
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeDeserializer());
        Gson expectedGson = builder.create();
        LocalDateTime now = LocalDateTime.now();
        String jsonFromActual = gson.toJson(now);
        String jsonFromExpected = expectedGson.toJson(now);
        assertEquals(jsonFromExpected, jsonFromActual);
    }

    @Test
    public void testProvideOkhttpClient_ReturnsConfiguredClient() {
        OkHttpClient client = networkModule.provideOkhttpClient(mockCache);
        assertNotNull(client);
        assertEquals(mockCache, client.cache());
        assertTrue(client.interceptors().stream().anyMatch(i -> i instanceof RestAuthInterceptor));
    }

    @Test
    public void testProvideRetrofit_ReturnsConfiguredRetrofit() {
        // Act
        Retrofit retrofit = networkModule.provideRetrofit(mockGson, mockOkHttpClient);

        // Assert
        assertNotNull(retrofit);
        // Update expected value to include trailing slash
        assertEquals(BASE_URL+"/", retrofit.baseUrl().toString());
        assertEquals(mockOkHttpClient, retrofit.callFactory());
        boolean hasGsonConverter = false;
        for (Converter.Factory factory : retrofit.converterFactories()) {
            if (factory instanceof GsonConverterFactory) {
                hasGsonConverter = true;
                break;
            }
        }
        assertTrue("Retrofit should have a GsonConverterFactory", hasGsonConverter);
    }

    @Test
    public void testProvideSyncRestService_ReturnsService() {
        when(mockRetrofit.create(SyncRestService.class)).thenReturn(mockSyncRestService);
        SyncRestService service = networkModule.provideSyncRestService(mockRetrofit);
        assertNotNull(service);
        verify(mockRetrofit).create(SyncRestService.class);
        assertEquals(mockSyncRestService, service);
    }
}