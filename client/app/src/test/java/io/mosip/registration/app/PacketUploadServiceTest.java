package io.mosip.registration.app;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import android.content.Context;
import android.os.Environment;

import androidx.room.Room;
import androidx.test.platform.app.InstrumentationRegistry;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import io.mosip.registration.keymanager.service.CertificateManagerServiceImpl;
import io.mosip.registration.keymanager.spi.CertificateManagerService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.shadows.ShadowEnvironment;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import io.mosip.registration.app.util.PacketUploadService;
import io.mosip.registration.clientmanager.config.ClientDatabase;
import io.mosip.registration.clientmanager.constant.AuditEvent;
import io.mosip.registration.clientmanager.constant.Components;
import io.mosip.registration.clientmanager.constant.PacketClientStatus;
import io.mosip.registration.clientmanager.constant.PacketTaskStatus;
import io.mosip.registration.clientmanager.dto.CenterMachineDto;
import io.mosip.registration.clientmanager.dto.registration.BiometricsDto;
import io.mosip.registration.clientmanager.dto.registration.DocumentDto;
import io.mosip.registration.clientmanager.dto.registration.RegistrationDto;
import io.mosip.registration.clientmanager.interceptor.RestAuthInterceptor;
import io.mosip.registration.clientmanager.repository.AuditRepository;
import io.mosip.registration.clientmanager.repository.GlobalParamRepository;
import io.mosip.registration.clientmanager.repository.IdentitySchemaRepository;
import io.mosip.registration.clientmanager.repository.RegistrationRepository;
import io.mosip.registration.clientmanager.service.AuditManagerServiceImpl;
import io.mosip.registration.clientmanager.service.MasterDataServiceImpl;
import io.mosip.registration.clientmanager.service.PacketServiceImpl;
import io.mosip.registration.clientmanager.service.RegistrationServiceImpl;
import io.mosip.registration.clientmanager.spi.AuditManagerService;
import io.mosip.registration.clientmanager.spi.MasterDataService;
import io.mosip.registration.clientmanager.spi.PacketService;
import io.mosip.registration.clientmanager.spi.RegistrationService;
import io.mosip.registration.clientmanager.spi.SyncRestService;
import io.mosip.registration.clientmanager.util.LocalDateTimeDeserializer;
import io.mosip.registration.clientmanager.util.LocalDateTimeSerializer;
import io.mosip.registration.keymanager.dto.SignResponseDto;
import io.mosip.registration.keymanager.repository.KeyStoreRepository;
import io.mosip.registration.keymanager.service.CryptoManagerServiceImpl;
import io.mosip.registration.keymanager.service.LocalClientCryptoServiceImpl;
import io.mosip.registration.keymanager.spi.ClientCryptoManagerService;
import io.mosip.registration.keymanager.spi.CryptoManagerService;
import io.mosip.registration.packetmanager.service.PacketCryptoServiceImpl;
import io.mosip.registration.packetmanager.service.PacketWriterServiceImpl;
import io.mosip.registration.packetmanager.service.PosixAdapterServiceImpl;
import io.mosip.registration.packetmanager.spi.IPacketCryptoService;
import io.mosip.registration.packetmanager.spi.ObjectAdapterService;
import io.mosip.registration.packetmanager.spi.PacketWriterService;
import io.mosip.registration.packetmanager.util.PacketKeeper;
import io.mosip.registration.packetmanager.util.PacketManagerHelper;
import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * @author Anshul vanawat
 * @since 05/06/2022.
 */

@RunWith(RobolectricTestRunner.class)
public class PacketUploadServiceTest {

    private static final String CERT_DATA_FILE_NAME = "certificate_data.txt";
    private static final String SCHEMA_JSON = "schema_json.json";
    private static final String DUMMY_REGISTRATION_FILE_NAME = "dummy_registration_data.json";
    private static final String SYNC_REGISTRATION_200_SUCCESS = "sync_registration_200_success.json";
    private static final String SYNC_REGISTRATION_200_FAILED = "sync_registration_200_failed.json";
    private static final String UPLOAD_REGISTRATION_200_SUCCESS = "upload_registration_200_success.json";
    private static final String UPLOAD_REGISTRATION_200_FAILED = "upload_registration_200_failed.json";

    private static final String KEY_INDEX = "a3:1f:08:0b:2e:ff:28:f5:72:44:12:dc:f9:da:90:58:ac:b7:df:dc:d8:a9:3f:d7:c2:a2:75:ce:d7:4a:1b:5a";
    private static final String SIGNED_DATA = "fLPx2Umz3WRgbPCpDrFysQswRZfN-iEK7g2oCAqEGBPlor410YkN-xnRFxjrMy_067pzSOuC3Qg73qntbdzxBpiRnMIn8DQpClbJKvHMMx11g-uksLcJj58DMegi_9O5fknK0sFLrlGUKRPGc9jDQxEiwUYpJ_lTveTTXBVGKgDleb2rf_FrVv1yEtiu7r1ElxLdVB4ZSAOsd37jZOYlnfXqMKXdTpOIybd0KLduRUft0UGcZ3JZlY-omoP5hAvTHzmsaPNNwn1SJV8fuUlobQKMGeysyWDbIz5-A-glLIPumCX4mCeNzBZAVLIB2ee_jJsXp_0jSWSbImtnvruX9g";
    private static final Double SCHEMA_VERSION = 0.1;

    Context appContext;
    ClientDatabase clientDatabase;

    MockWebServer server;
    Retrofit retrofit;

    SyncRestService syncRestService;
    PacketUploadService packetUploadService;
    RegistrationService registrationService;
    PacketService packetService;
    AuditManagerService auditManagerService;
    CertificateManagerService certificateManagerService;
    AutoCloseable openMocks;

    @Before
    public void setUp() throws Exception {
        openMocks = MockitoAnnotations.openMocks(this);
        ShadowEnvironment.setExternalStorageState(Environment.MEDIA_MOUNTED);

        appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        clientDatabase = Room.inMemoryDatabaseBuilder(appContext, ClientDatabase.class)
                .allowMainThreadQueries()
                .build();

        //-------MOCK Server Configuration Start-------
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

        //-------MOCK Server Configuration End-------

        //-------Service MOCKING Start-------
        Map<String, String> centerNames = new HashMap<>();
        centerNames.put("tam", "Center A Ben Mansour");
        centerNames.put("eng", "Center A Ben Mansour");

        CenterMachineDto centerMachineDto = new CenterMachineDto();
        centerMachineDto.setMachineId("10692");
        centerMachineDto.setMachineStatus(true);
        centerMachineDto.setMachineName("P27QNlQ5ZULr");
        centerMachineDto.setCenterId("10001");
        centerMachineDto.setCenterStatus(true);
        centerMachineDto.setCenterId("10001");
        centerMachineDto.setCenterNames(centerNames);
        centerMachineDto.setMachineRefId("10001_10692");

        MasterDataService masterDataService = mock(MasterDataServiceImpl.class);
        when(masterDataService.getRegistrationCenterMachineDetails())
                .thenReturn(centerMachineDto);

        String schemaJson = RestServiceTestHelper.getStringFromFile(appContext, SCHEMA_JSON);
        IdentitySchemaRepository identitySchemaRepository = mock(IdentitySchemaRepository.class);
        when(identitySchemaRepository.getLatestSchemaVersion())
                .thenReturn(SCHEMA_VERSION);
        when(identitySchemaRepository.getSchemaJson(any(Context.class), any(Double.class)))
                .thenReturn(schemaJson);

        KeyStoreRepository keyStoreRepository = mock(KeyStoreRepository.class);

        ClientCryptoManagerService clientCryptoManagerService = mock(LocalClientCryptoServiceImpl.class);
        when(clientCryptoManagerService.getClientKeyIndex()).thenReturn(KEY_INDEX);
        when(clientCryptoManagerService.sign(any())).thenReturn(new SignResponseDto(SIGNED_DATA));

        String certificateData = RestServiceTestHelper.getStringFromFile(appContext, CERT_DATA_FILE_NAME);
        when(keyStoreRepository.getCertificateData(anyString())).thenReturn(certificateData);

        //-------Service MOCKING End-------

        certificateManagerService = mock(CertificateManagerServiceImpl.class);
        CryptoManagerService cryptoManagerService = new CryptoManagerServiceImpl(appContext, certificateManagerService);

        IPacketCryptoService packetCryptoService = new PacketCryptoServiceImpl(appContext, clientCryptoManagerService, cryptoManagerService);

        ObjectMapper objectMapper = new ObjectMapper();
        ObjectAdapterService objectAdapterService = new PosixAdapterServiceImpl(appContext, packetCryptoService, objectMapper);
        PacketKeeper packetKeeper = new PacketKeeper(appContext, packetCryptoService, objectAdapterService);
        PacketManagerHelper packetManagerHelper = new PacketManagerHelper(appContext);
        PacketWriterService packetWriterService = new PacketWriterServiceImpl(appContext, packetManagerHelper, packetKeeper);

        RegistrationRepository registrationRepository = new RegistrationRepository(clientDatabase.registrationDao(), objectMapper);
        GlobalParamRepository globalParamRepository = new GlobalParamRepository(clientDatabase.globalParamDao());
        AuditRepository auditRepository = new AuditRepository(clientDatabase.auditDao());

        auditManagerService = new AuditManagerServiceImpl(appContext, auditRepository, globalParamRepository);

        registrationService = new RegistrationServiceImpl(appContext, packetWriterService,
                registrationRepository, masterDataService,
                identitySchemaRepository, clientCryptoManagerService, keyStoreRepository, globalParamRepository, auditManagerService);

        packetService = new PacketServiceImpl(appContext, registrationRepository,
                packetCryptoService, syncRestService, masterDataService);

        packetUploadService = new PacketUploadService(packetService);
    }

    @After
    public void tearDown() throws Exception {
        openMocks.close();
    }

    @Test
    public void queueSyncAndUploadPacket_Success() throws InterruptedException {
        String packetId = createDummyRegistration();
        if (packetId.isEmpty()) {
            fail();
        }

        final Dispatcher dispatcher = new Dispatcher() {
            @Override
            public MockResponse dispatch(RecordedRequest request) {
                if (request.getPath().contains("sync")) {
                    //Sync registration
                    try {
                        String syncResponse = RestServiceTestHelper.getStringFromFile(appContext, SYNC_REGISTRATION_200_SUCCESS);
                        return new MockResponse()
                                .setResponseCode(200)
                                .setBody(syncResponse);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (request.getPath().contains("registrationpackets")) {
                    //Upload registration
                    try {
                        String uploadResponse = RestServiceTestHelper.getStringFromFile(appContext, UPLOAD_REGISTRATION_200_SUCCESS);
                        return new MockResponse()
                                .setResponseCode(200)
                                .setBody(uploadResponse);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                return new MockResponse().setResponseCode(404);
            }
        };

        server.setDispatcher(dispatcher);

        AtomicReference<PacketTaskStatus> finalProgressStatus = new AtomicReference<>();
        packetUploadService.syncAndUploadPacket(packetId, (RID, progress) -> {
            finalProgressStatus.set(progress);
        });

        //waiting for sync and upload to completed
        Thread.sleep(1000);
        assertSame(finalProgressStatus.get(), PacketTaskStatus.UPLOAD_COMPLETED);
    }


    @Test
    public void queueSyncAndUploadPacket_SyncFailed1() throws InterruptedException {
        String packetId = createDummyRegistration();
        if (packetId.isEmpty()) {
            fail();
        }

        final Dispatcher dispatcher = new Dispatcher() {
            @Override
            public MockResponse dispatch(RecordedRequest request) {
                if (request.getPath().contains("sync")) {
                    //Sync registration
                    try {
                        String syncResponse = RestServiceTestHelper.getStringFromFile(appContext, SYNC_REGISTRATION_200_FAILED);
                        return new MockResponse()
                                .setResponseCode(200)
                                .setBody(syncResponse);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                return new MockResponse().setResponseCode(404);
            }
        };

        server.setDispatcher(dispatcher);

        AtomicReference<PacketTaskStatus> finalProgressStatus = new AtomicReference<>();
        packetUploadService.syncAndUploadPacket(packetId, (RID, progress) -> {
            finalProgressStatus.set(progress);
        });

        //waiting for sync and upload to completed
        Thread.sleep(1000);
        assertSame(finalProgressStatus.get(), PacketTaskStatus.SYNC_FAILED);
    }


    @Test
    public void queueSyncAndUploadPacket_SyncFailed2() throws InterruptedException {
        String packetId = createDummyRegistration();
        if (packetId.isEmpty()) {
            fail();
        }

        final Dispatcher dispatcher = new Dispatcher() {
            @Override
            public MockResponse dispatch(RecordedRequest request) {
                if (request.getPath().contains("sync")) {
                    //Sync registration
                    try {
                        String syncResponse = RestServiceTestHelper.getStringFromFile(appContext, SYNC_REGISTRATION_200_FAILED);
                        return new MockResponse()
                                .setResponseCode(404)
                                .setBody(syncResponse);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                return new MockResponse().setResponseCode(404);
            }
        };

        server.setDispatcher(dispatcher);

        AtomicReference<PacketTaskStatus> finalProgressStatus = new AtomicReference<>();
        packetUploadService.syncAndUploadPacket(packetId, (RID, progress) -> {
            finalProgressStatus.set(progress);
        });

        //waiting for sync and upload to completed
        Thread.sleep(1000);
        assertSame(finalProgressStatus.get(), PacketTaskStatus.SYNC_FAILED);
    }

    @Test
    public void queueSyncAndUploadPacket_UploadFailed1() throws InterruptedException {
        String packetId = createDummyRegistration();
        if (packetId.isEmpty()) {
            fail();
        }

        final Dispatcher dispatcher = new Dispatcher() {
            @Override
            public MockResponse dispatch(RecordedRequest request) {
                if (request.getPath().contains("sync")) {
                    //Sync registration
                    try {
                        String syncResponse = RestServiceTestHelper.getStringFromFile(appContext, SYNC_REGISTRATION_200_SUCCESS);
                        return new MockResponse()
                                .setResponseCode(200)
                                .setBody(syncResponse);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (request.getPath().contains("registrationpackets")) {
                    //Upload registration
                    try {
                        String uploadResponse = RestServiceTestHelper.getStringFromFile(appContext, UPLOAD_REGISTRATION_200_FAILED);
                        return new MockResponse()
                                .setResponseCode(200)
                                .setBody(uploadResponse);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                return new MockResponse().setResponseCode(404);
            }
        };

        server.setDispatcher(dispatcher);

        AtomicReference<PacketTaskStatus> finalProgressStatus = new AtomicReference<>();
        packetUploadService.syncAndUploadPacket(packetId, (RID, progress) -> {
            finalProgressStatus.set(progress);
        });

        //waiting for sync and upload to completed
        Thread.sleep(1000);
        assertSame(finalProgressStatus.get(), PacketTaskStatus.UPLOAD_FAILED);
    }

    @Test
    public void queueSyncAndUploadPacket_UploadFailed2() throws InterruptedException {
        String packetId = createDummyRegistration();
        if (packetId.isEmpty()) {
            fail();
        }

        final Dispatcher dispatcher = new Dispatcher() {
            @Override
            public MockResponse dispatch(RecordedRequest request) {
                if (request.getPath().contains("sync")) {
                    //Sync registration
                    try {
                        String syncResponse = RestServiceTestHelper.getStringFromFile(appContext, SYNC_REGISTRATION_200_SUCCESS);
                        return new MockResponse()
                                .setResponseCode(200)
                                .setBody(syncResponse);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (request.getPath().contains("registrationpackets")) {
                    //Upload registration
                    try {
                        String uploadResponse = RestServiceTestHelper.getStringFromFile(appContext, UPLOAD_REGISTRATION_200_FAILED);
                        return new MockResponse()
                                .setResponseCode(404)
                                .setBody(uploadResponse);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                return new MockResponse().setResponseCode(404);
            }
        };

        server.setDispatcher(dispatcher);

        AtomicReference<PacketTaskStatus> finalProgressStatus = new AtomicReference<>();
        packetUploadService.syncAndUploadPacket(packetId, (RID, progress) -> {
            finalProgressStatus.set(progress);
        });

        //waiting for sync and upload to completed
        Thread.sleep(1000);
        assertSame(finalProgressStatus.get(), PacketTaskStatus.UPLOAD_FAILED);
    }


    @Test
    public void queueSyncAndUploadPacket_AlreadySynced() throws InterruptedException {
        String packetId = createDummyRegistration();
        if (packetId.isEmpty()) {
            fail();
        }

        //Sync will be completed, but upload will fail, leaving packet with status synced
        final Dispatcher dispatcher = new Dispatcher() {
            @Override
            public MockResponse dispatch(RecordedRequest request) {
                if (request.getPath().contains("sync")) {
                    //Sync registration success
                    try {
                        String syncResponse = RestServiceTestHelper.getStringFromFile(appContext, SYNC_REGISTRATION_200_SUCCESS);
                        return new MockResponse()
                                .setResponseCode(200)
                                .setBody(syncResponse);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (request.getPath().contains("registrationpackets")) {
                    //Upload registration fail
                    try {
                        String uploadResponse = RestServiceTestHelper.getStringFromFile(appContext, UPLOAD_REGISTRATION_200_FAILED);
                        return new MockResponse()
                                .setResponseCode(404)
                                .setBody(uploadResponse);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                return new MockResponse().setResponseCode(404);
            }
        };

        server.setDispatcher(dispatcher);

        //Running sync again
        AtomicReference<PacketTaskStatus> finalProgressStatus1 = new AtomicReference<>();
        packetUploadService.syncAndUploadPacket(packetId, (RID, progress) -> {
            finalProgressStatus1.set(progress);
        });

        Thread.sleep(1000);
        //checking packet status
        assertEquals(PacketClientStatus.SYNCED.name(), packetService.getPacketStatus(packetId));
        assertSame(finalProgressStatus1.get(), PacketTaskStatus.UPLOAD_FAILED);


        //Sync already done, packet will be successful
        final Dispatcher dispatcher2 = new Dispatcher() {
            @Override
            public MockResponse dispatch(RecordedRequest request) {
                if (request.getPath().contains("registrationpackets")) {
                    //Upload registration fail
                    try {
                        String uploadResponse = RestServiceTestHelper.getStringFromFile(appContext, UPLOAD_REGISTRATION_200_SUCCESS);
                        return new MockResponse()
                                .setResponseCode(200)
                                .setBody(uploadResponse);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                return new MockResponse().setResponseCode(404);
            }
        };

        server.setDispatcher(dispatcher2);

        //Running sync again
        AtomicReference<PacketTaskStatus> finalProgressStatus2 = new AtomicReference<>();
        packetUploadService.syncAndUploadPacket(packetId, (RID, progress) -> {
            finalProgressStatus2.set(progress);
        });

        //waiting for sync and upload to completed
        Thread.sleep(1000);
        assertSame(finalProgressStatus2.get(), PacketTaskStatus.UPLOAD_COMPLETED);
    }


    @Test
    public void queueSyncAndUploadPacket_AlreadyUploaded() throws InterruptedException {
        String packetId = createDummyRegistration();
        if (packetId.isEmpty()) {
            fail();
        }

        //Sync and upload will be completed
        final Dispatcher dispatcher = new Dispatcher() {
            @Override
            public MockResponse dispatch(RecordedRequest request) {
                if (request.getPath().contains("sync")) {
                    //Sync registration success
                    try {
                        String syncResponse = RestServiceTestHelper.getStringFromFile(appContext, SYNC_REGISTRATION_200_SUCCESS);
                        return new MockResponse()
                                .setResponseCode(200)
                                .setBody(syncResponse);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (request.getPath().contains("registrationpackets")) {
                    //Upload registration fail
                    try {
                        String uploadResponse = RestServiceTestHelper.getStringFromFile(appContext, UPLOAD_REGISTRATION_200_SUCCESS);
                        return new MockResponse()
                                .setResponseCode(200)
                                .setBody(uploadResponse);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                return new MockResponse().setResponseCode(404);
            }
        };

        server.setDispatcher(dispatcher);

        AtomicReference<PacketTaskStatus> finalProgressStatus1 = new AtomicReference<>();
        packetUploadService.syncAndUploadPacket(packetId, (RID, progress) -> {
            finalProgressStatus1.set(progress);
        });

        //waiting for sync and upload to completed
        Thread.sleep(1000);
        assertSame(finalProgressStatus1.get(), PacketTaskStatus.UPLOAD_COMPLETED);


        //Running sync and upload again.
        AtomicReference<PacketTaskStatus> finalProgressStatus2 = new AtomicReference<>();
        packetUploadService.syncAndUploadPacket(packetId, (RID, progress) -> {
            finalProgressStatus2.set(progress);
        });

        //waiting for sync and upload to completed
        Thread.sleep(1000);
        assertSame(finalProgressStatus2.get(), PacketTaskStatus.UPLOAD_ALREADY_COMPLETED);
    }

    private String createDummyRegistration() {
        List<String> lang = new ArrayList<>();
        lang.add("eng");
        RegistrationDto registrationDto;

        try {
            auditManagerService.audit(AuditEvent.LOADED_ABOUT, Components.LOGIN);
            auditManagerService.audit(AuditEvent.LOADED_HOME, Components.LOGIN);
            auditManagerService.audit(AuditEvent.LOADED_LOGIN, Components.LOGIN);

            ObjectMapper mapper = new ObjectMapper();
            String jsonString = RestServiceTestHelper.getStringFromFile(appContext, DUMMY_REGISTRATION_FILE_NAME);
            RegistrationDto dummyReg = mapper.readValue(jsonString, RegistrationDto.class);

            registrationService.startRegistration(dummyReg.getSelectedLanguages());
            registrationDto = registrationService.getRegistrationDto();

            String packetId = registrationDto.getRId();
            registrationDto.setConsent("dummy consent text");

            for (Map.Entry<String, BiometricsDto> entry : dummyReg.getBiometrics().entrySet()) {
                String key = entry.getKey();
                String[] strVal = key.split("_");
                String fieldId = strVal.length > 0 ? strVal[0] : "fieldId";
                String attribute = strVal.length > 1 ? strVal[0] : "attribute";
                registrationDto.addBiometric(fieldId, attribute, 1, entry.getValue());
            }

            for (Map.Entry<String, Object> entry : dummyReg.getDemographics().entrySet()) {
                registrationDto.addDemographicField(entry.getKey(), entry.getValue());
            }

            for (Map.Entry<String, DocumentDto> entry : dummyReg.getDocuments().entrySet()) {
                registrationDto.addDocument(entry.getKey(), entry.getValue().getType(), entry.getValue().getRefNumber(), entry.getValue().getContent().get(0));
            }
            registrationService.submitRegistrationDto("Dummy");
            return packetId;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}
