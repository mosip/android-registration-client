package io.mosip.registration.clientmanager.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import android.content.Context;

import androidx.room.Room;
import androidx.test.platform.app.InstrumentationRegistry;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import io.mosip.registration.clientmanager.config.ClientDatabase;
import io.mosip.registration.clientmanager.constant.PacketClientStatus;
import io.mosip.registration.clientmanager.dao.RegistrationDao;
import io.mosip.registration.clientmanager.dao.SyncJobDefDao;
import io.mosip.registration.clientmanager.entity.Registration;
import io.mosip.registration.clientmanager.interceptor.RestAuthInterceptor;
import io.mosip.registration.clientmanager.repository.RegistrationRepository;
import io.mosip.registration.clientmanager.repository.SyncJobDefRepository;
import io.mosip.registration.clientmanager.service.PacketServiceImpl;
import io.mosip.registration.clientmanager.spi.SyncRestService;
import io.mosip.registration.clientmanager.util.LocalDateTimeDeserializer;
import io.mosip.registration.clientmanager.util.LocalDateTimeSerializer;
import io.mosip.registration.clientmanager.util.RestServiceTestHelper;
import okhttp3.Cache;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * @author Anshul vanawat
 * @since 25/07/2022.
 */

@RunWith(RobolectricTestRunner.class)
public class PacketServiceImplTest {

    private static final String PACKET_ID = "10001103911003120220530051317";
    private static final String PACKET_STATUS_CODE_CREATED = PacketClientStatus.CREATED.name();
    private static final String PACKET_STATUS_CODE_ACCEPTED = "ACCEPTED";
    private static final String CONTAINER_PATH = "/storage/emulated/0/Documents/PACKET_MANAGER_ACCOUNT/10001103911003120220530051317.zip";
    private static final String REGISTRATION_TYPE = "NEW";
    private static final String CENTER_ID = "10001";
    private static final String ADDITIONAL_INFO = "{\"langCode\":\"eng\"}";
    private static final String GET_PACKET_STATUS_200 = "getPacketStatus_200.json";
    private static final String GET_PACKET_STATUS_404 = "status_404.json";

    Context appContext;
    ClientDatabase clientDatabase;
    ObjectMapper objectMapper = new ObjectMapper();
    MockWebServer server;
    Retrofit retrofit;

    RegistrationRepository registrationRepository;
    SyncRestService syncRestService;
    SyncJobDefRepository syncJobDefRepository;
    RegistrationDao registrationDao;

    @Before
    public void setUp() throws Exception {
        appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        clientDatabase = Room.inMemoryDatabaseBuilder(appContext, ClientDatabase.class)
                .allowMainThreadQueries()
                .build();

        registrationDao = clientDatabase.registrationDao();
        registrationRepository = new RegistrationRepository(registrationDao, objectMapper);

        SyncJobDefDao syncJobDefDao = clientDatabase.syncJobDefDao();
        syncJobDefRepository = new SyncJobDefRepository(syncJobDefDao);
        server = new MockWebServer();
        server.start();

        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES);
        gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeSerializer());
        gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeDeserializer());
        Gson gson = gsonBuilder.create();

        int cacheSize = 10 * 1024 * 1024;
        Cache cache = new Cache(appContext.getCacheDir(), cacheSize);

        OkHttpClient.Builder client = new OkHttpClient.Builder();
        client.cache(cache);
        client.addInterceptor(new RestAuthInterceptor(appContext));
        OkHttpClient okHttpClient = client.build();

        retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create(gson))
                .baseUrl(server.url("/").toString())
                .client(okHttpClient)
                .build();

        syncRestService = retrofit.create(SyncRestService.class);
    }

    @After
    public void tearDown() {
        clientDatabase.close();
    }

    @Test
    public void syncAllPacketStatusSuccessResponse() throws Exception {
        //Dummy registration data
        Registration registrationDummyData = new Registration(PACKET_ID);
        registrationDummyData.setFilePath(CONTAINER_PATH);
        registrationDummyData.setRegType(REGISTRATION_TYPE);
        registrationDummyData.setCenterId(CENTER_ID);
        registrationDummyData.setClientStatus(PacketClientStatus.UPLOADED.name());
        registrationDummyData.setServerStatus(PACKET_STATUS_CODE_CREATED);
        registrationDummyData.setCrDtime(System.currentTimeMillis());
        registrationDummyData.setCrBy("110006");
        registrationDummyData.setAdditionalInfo(ADDITIONAL_INFO.getBytes(StandardCharsets.UTF_8));
        registrationDao.insert(registrationDummyData);

        //Preparing mock response
        server.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody(RestServiceTestHelper.getStringFromFile(appContext, GET_PACKET_STATUS_200)));

        PacketServiceImpl packetServiceImpl = new PacketServiceImpl(appContext, registrationRepository, null, syncRestService, null);
        packetServiceImpl.syncAllPacketStatus();

        //waiting for sync to completed
        Thread.sleep(1000);

        Registration registrationAfterSync = registrationRepository.getRegistration(PACKET_ID);
        assertEquals(PACKET_STATUS_CODE_ACCEPTED, registrationAfterSync.getServerStatus());
    }

    @Test
    public void syncAllPacketStatusNoRegistrations() throws Exception {
        //Preparing mock response
        server.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody(RestServiceTestHelper.getStringFromFile(appContext, GET_PACKET_STATUS_200)));

        PacketServiceImpl packetServiceImpl = new PacketServiceImpl(appContext, registrationRepository, null, syncRestService, null);
        packetServiceImpl.syncAllPacketStatus();

        //waiting for sync to completed
        Thread.sleep(1000);

        Registration registrationAfterSync = registrationRepository.getRegistration(PACKET_ID);
        assertNull(registrationAfterSync);
    }

    @Test
    public void syncAllPacketStatusNotFoundResponse() throws Exception {
        //Dummy registration data
        Registration registrationDummyData = new Registration(PACKET_ID);
        registrationDummyData.setFilePath(CONTAINER_PATH);
        registrationDummyData.setRegType(REGISTRATION_TYPE);
        registrationDummyData.setCenterId(CENTER_ID);
        registrationDummyData.setClientStatus(PacketClientStatus.UPLOADED.name());
        registrationDummyData.setServerStatus(PACKET_STATUS_CODE_CREATED);
        registrationDummyData.setCrDtime(System.currentTimeMillis());
        registrationDummyData.setCrBy("110006");
        registrationDummyData.setAdditionalInfo(ADDITIONAL_INFO.getBytes(StandardCharsets.UTF_8));
        registrationDao.insert(registrationDummyData);

        //Preparing mock response
        server.enqueue(new MockResponse()
                .setResponseCode(404)
                .setBody(RestServiceTestHelper.getStringFromFile(appContext, GET_PACKET_STATUS_404)));

        PacketServiceImpl packetServiceImpl = new PacketServiceImpl(appContext, registrationRepository, null, syncRestService, null);
        packetServiceImpl.syncAllPacketStatus();

        //waiting for packetServiceSync to complete
        Thread.sleep(1000);

        Registration registrationAfterSync = registrationRepository.getRegistration(PACKET_ID);
        assertEquals(PACKET_STATUS_CODE_CREATED, registrationAfterSync.getServerStatus());
    }

    @Test
    public void defaultMockResponse() {
        MockResponse response = new MockResponse();
        assertTrue(headersToList(response).contains("Content-Length: 0"));
        assertEquals("HTTP/1.1 200 OK", response.getStatus());
    }

    private List<String> headersToList(MockResponse response) {
        Headers headers = response.getHeaders();
        int size = headers.size();
        List<String> headerList = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            headerList.add(headers.name(i) + ": " + headers.value(i));
        }
        return headerList;
    }
}