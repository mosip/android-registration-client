package io.mosip.registration.app;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;

import javax.inject.Singleton;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dagger.Module;
import dagger.Provides;
import io.mosip.registration.clientmanager.config.LocalDateTimeDeserializer;
import io.mosip.registration.clientmanager.config.LocalDateTimeSerializer;
import io.mosip.registration.clientmanager.service.AuditManagerServiceImpl;
import io.mosip.registration.clientmanager.service.RegistrationServiceImpl;
import io.mosip.registration.clientmanager.spi.AuditManagerService;
import io.mosip.registration.clientmanager.spi.RegistrationService;
import io.mosip.registration.clientmanager.util.SyncRestUtil;
import io.mosip.registration.clientmanager.interceptor.RestAuthInterceptor;
import io.mosip.registration.clientmanager.repository.*;
import io.mosip.registration.clientmanager.service.LoginService;
import io.mosip.registration.clientmanager.service.MasterDataServiceImpl;
import io.mosip.registration.clientmanager.service.PacketServiceImpl;
import io.mosip.registration.clientmanager.spi.MasterDataService;
import io.mosip.registration.clientmanager.spi.PacketService;
import io.mosip.registration.clientmanager.spi.SyncRestService;
import io.mosip.registration.clientmanager.util.UserInterfaceHelperService;
import io.mosip.registration.keymanager.repository.CACertificateStoreRepository;
import io.mosip.registration.keymanager.repository.KeyStoreRepository;
import io.mosip.registration.keymanager.service.CACertificateManagerServiceImpl;
import io.mosip.registration.keymanager.service.CertificateDBHelper;
import io.mosip.registration.keymanager.service.CryptoManagerServiceImpl;
import io.mosip.registration.keymanager.service.LocalClientCryptoServiceImpl;
import io.mosip.registration.keymanager.spi.CACertificateManagerService;
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
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.time.LocalDateTime;

@Module
public class AppModule {

    Application application;
    Context appContext;

    public AppModule(Application application) {
        this.application = application;
        this.appContext = application.getApplicationContext();
    }

    @Provides
    Application providesApplication() {
        return application;
    }

    @Provides
    @NonNull
    public Context provideApplicationContext() {
        return appContext;
    }

    @Singleton
    @Provides
    public ClientCryptoManagerService provideClientCryptoManagerService() {
        return new LocalClientCryptoServiceImpl(appContext);
    }

    @Singleton
    @Provides
    public CryptoManagerService provideCryptoManagerService(KeyStoreRepository keyStoreRepository) {
        return new CryptoManagerServiceImpl(appContext,keyStoreRepository);
    }

    @Singleton
    @Provides
    public IPacketCryptoService provideIPacketCryptoService(ClientCryptoManagerService clientCryptoManagerService,
                                                            CryptoManagerService cryptoManagerService) {
        return new PacketCryptoServiceImpl(appContext, clientCryptoManagerService, cryptoManagerService);
    }

    @Singleton
    @Provides
    public ObjectAdapterService provideObjectAdapterService(IPacketCryptoService iPacketCryptoService) {
        return new PosixAdapterServiceImpl(appContext, iPacketCryptoService, new ObjectMapper());
    }

    @Singleton
    @Provides
    public PacketKeeper providePacketKeeper(IPacketCryptoService iPacketCryptoService,
                                            ObjectAdapterService objectAdapterService) {
        return new PacketKeeper(appContext, iPacketCryptoService, objectAdapterService);
    }

    @Singleton
    @Provides
    public PacketManagerHelper providePacketManagerHelper() {
        return new PacketManagerHelper(appContext);
    }

    @Singleton
    @Provides
    public PacketWriterService providePacketWriterService(PacketManagerHelper packetManagerHelper,
                                                          PacketKeeper packetKeeper){
        return new PacketWriterServiceImpl(appContext, packetManagerHelper, packetKeeper);
    }

    @Singleton
    @Provides
    public MasterDataService provideMasterDataService(SyncRestService syncRestService, ClientCryptoManagerService clientCryptoManagerService,
                                                      MachineRepository machineRepository,
                                                      RegistrationCenterRepository registrationCenterRepository,
                                                      DocumentTypeRepository documentTypeRepository,
                                                      ApplicantValidDocRepository applicantValidDocRepository,
                                                      TemplateRepository templateRepository,
                                                      DynamicFieldRepository dynamicFieldRepository,
                                                      KeyStoreRepository keyStoreRepository,
                                                      LocationRepository locationRepository,
                                                      GlobalParamRepository globalParamRepository,
                                                      IdentitySchemaRepository identitySchemaRepository,
                                                      BlocklistedWordRepository blocklistedWordRepository,
                                                      SyncJobDefRepository syncJobDefRepository,
                                                      UserDetailRepository userDetailRepository,
                                                      CACertificateManagerService caCertificateManagerService,
                                                      LanguageRepository languageRepository) {
        return new MasterDataServiceImpl(appContext, syncRestService, clientCryptoManagerService,
                machineRepository, registrationCenterRepository, documentTypeRepository, applicantValidDocRepository,
                templateRepository, dynamicFieldRepository, keyStoreRepository, locationRepository,
                globalParamRepository, identitySchemaRepository, blocklistedWordRepository, syncJobDefRepository, userDetailRepository,
                caCertificateManagerService, languageRepository);
    }

    @Provides
    @Singleton
    Cache provideHttpCache() {
        int cacheSize = 10 * 1024 * 1024;
        Cache cache = new Cache(application.getCacheDir(), cacheSize);
        return cache;
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
    OkHttpClient provideOkhttpClient(Cache cache) {
        OkHttpClient.Builder client = new OkHttpClient.Builder();
        client.cache(cache);
        client.addInterceptor(new RestAuthInterceptor(appContext));
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

    @Provides
    @Singleton
    SyncRestUtil provideSyncRestFactory(ClientCryptoManagerService clientCryptoManagerService) {
        return new SyncRestUtil(clientCryptoManagerService);
    }

    @Provides
    @Singleton
    LoginService provideLoginService(ClientCryptoManagerService clientCryptoManagerService) {
        return new LoginService(appContext, clientCryptoManagerService);
    }

    @Provides
    @Singleton
    RegistrationService provideRegistrationService(PacketWriterService packetWriterService,
                                                   UserInterfaceHelperService userInterfaceHelperService,
                                                   RegistrationRepository registrationRepository,
                                                   MasterDataService masterDataService,
                                                   IdentitySchemaRepository identitySchemaRepository,
                                                   ClientCryptoManagerService clientCryptoManagerService) {
        return new RegistrationServiceImpl(appContext, packetWriterService, userInterfaceHelperService,
                registrationRepository, masterDataService, identitySchemaRepository, clientCryptoManagerService);
    }

    @Provides
    @Singleton
    UserInterfaceHelperService provideUserInterfaceHelperService() {
        return new UserInterfaceHelperService(appContext);
    }

    @Provides
    @Singleton
    PacketService providePacketService(RegistrationRepository registrationRepository, SyncJobDefRepository syncJobDefRepository,
                                       IPacketCryptoService packetCryptoService, SyncRestService syncRestService,
                                       MasterDataService masterDataService) {
        return new PacketServiceImpl(appContext, registrationRepository, syncJobDefRepository, packetCryptoService, syncRestService,
                masterDataService);
    }

    @Provides
    @Singleton
    CACertificateManagerService provideCACertificateManagerService(CertificateDBHelper certificateDBHelper) {
        return new CACertificateManagerServiceImpl(appContext, certificateDBHelper);
    }

    @Provides
    @Singleton
    CertificateDBHelper provideCertificateDBHelper(CACertificateStoreRepository caCertificateStoreRepository) {
        return new CertificateDBHelper(caCertificateStoreRepository);
    }

    @Provides
    @Singleton
    AuditManagerService provideAuditManagerService(AuditRepository auditRepository, GlobalParamRepository globalParamRepository) {
        return new AuditManagerServiceImpl(appContext, auditRepository, globalParamRepository);
    }
}