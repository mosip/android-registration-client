package io.mosip.registration.clientmanager.config;

import android.app.Application;
import android.content.Context;

import com.google.gson.Gson;

import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;

import io.mosip.registration.clientmanager.interceptor.RestAuthInterceptor;
import io.mosip.registration.clientmanager.repository.GlobalParamRepository;
import io.mosip.registration.clientmanager.spi.SyncRestService;
import okhttp3.Cache;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class NetworkModuleTest {

    @Mock
    Application mockApplication;
    @Mock
    Context mockContext;

    private File tempCacheDir;
    private NetworkModule networkModule;
    @Mock
    GlobalParamRepository globalParamRepository;


    @Before
    public void setUp() throws IOException {
        MockitoAnnotations.initMocks(this);
        tempCacheDir = File.createTempFile("test-cache", "");
        if (tempCacheDir.exists()) tempCacheDir.delete();
        tempCacheDir.mkdir();
        when(mockApplication.getApplicationContext()).thenReturn(mockContext);
        when(mockApplication.getCacheDir()).thenReturn(tempCacheDir);
        networkModule = new NetworkModule(mockApplication);
        when(globalParamRepository.getCachedReadTimeout()).thenReturn(0L);
        when(globalParamRepository.getCachedWriteTimeout()).thenReturn(0L);
    }

    @After
    public void tearDown() {
        if (tempCacheDir != null && tempCacheDir.exists()) {
            for (File file : tempCacheDir.listFiles() != null ? tempCacheDir.listFiles() : new File[0]) {
                file.delete();
            }
            tempCacheDir.delete();
        }
    }

    @Test
    public void testProvideHttpCache() {
        Cache cache = networkModule.provideHttpCache();
        assertNotNull(cache);
        assertEquals(tempCacheDir.getAbsolutePath(), cache.directory().getAbsolutePath());
        assertEquals(10 * 1024 * 1024, cache.maxSize());
    }

    @Test
    public void testProvideGson() {
        Gson gson = networkModule.provideGson();
        assertNotNull(gson);
        String json = gson.toJson(new TestDateTimeHolder(LocalDateTime.of(2020, 1, 1, 12, 0)));
        assertTrue(json.contains("2020"));
    }

    @Test
    public void testProvideOkhttpClient() {
        Cache cache = networkModule.provideHttpCache();
        OkHttpClient client = networkModule.provideOkhttpClient(cache,globalParamRepository);
        assertNotNull(client);
        assertEquals(cache, client.cache());
        assertTrue(client.interceptors().stream().anyMatch(i -> i instanceof RestAuthInterceptor));
    }

    @Test
    public void testProvideRetrofit() {
        Gson gson = networkModule.provideGson();
        OkHttpClient client = networkModule.provideOkhttpClient(networkModule.provideHttpCache(),globalParamRepository);
        Retrofit retrofit = networkModule.provideRetrofit(gson, client);
        assertNotNull(retrofit);
        assertEquals(client, retrofit.callFactory());
        assertTrue(retrofit.converterFactories().stream().anyMatch(f -> f instanceof GsonConverterFactory));
    }

    @Test
    public void testProvideSyncRestService() {
        Gson gson = networkModule.provideGson();
        OkHttpClient client = networkModule.provideOkhttpClient(networkModule.provideHttpCache(),globalParamRepository);
        Retrofit retrofit = networkModule.provideRetrofit(gson, client);
        SyncRestService service = networkModule.provideSyncRestService(retrofit);
        assertNotNull(service);
        assertTrue(SyncRestService.class.isAssignableFrom(service.getClass()));
    }

    static class TestDateTimeHolder {
        public LocalDateTime dateTime;
        public TestDateTimeHolder(LocalDateTime dateTime) {
            this.dateTime = dateTime;
        }
    }

}
