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
import io.mosip.registration.app.viewmodel.ListingViewModel;
import io.mosip.registration.clientmanager.dao.RegistrationDao;
import io.mosip.registration.clientmanager.factory.ClientWorkerFactory;
import io.mosip.registration.clientmanager.factory.SyncRestFactory;
import io.mosip.registration.clientmanager.interceptor.RestAuthInterceptor;
import io.mosip.registration.clientmanager.repository.RegistrationRepository;
import io.mosip.registration.clientmanager.service.LoginService;
import io.mosip.registration.clientmanager.service.MasterDataServiceImpl;
import io.mosip.registration.clientmanager.service.RegistrationService;
import io.mosip.registration.clientmanager.spi.MasterDataService;
import io.mosip.registration.clientmanager.spi.SyncRestService;
import io.mosip.registration.clientmanager.util.UserInterfaceHelperService;
import io.mosip.registration.keymanager.service.LocalClientCryptoServiceImpl;
import io.mosip.registration.keymanager.spi.ClientCryptoManagerService;
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
    public IPacketCryptoService provideIPacketCryptoService(ClientCryptoManagerService clientCryptoManagerService) {
        return new PacketCryptoServiceImpl(appContext, clientCryptoManagerService);
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
    public MasterDataService provideMasterDataService() {
        return new MasterDataServiceImpl(appContext);
    }

    @Singleton
    @Provides
    public ClientWorkerFactory provideClientWorkerFactory(ClientCryptoManagerService clientCryptoManagerService) {
        return new ClientWorkerFactory(clientCryptoManagerService);
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
                .baseUrl("https://dev.mosip.net/")
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
    SyncRestFactory provideSyncRestFactory(ClientCryptoManagerService clientCryptoManagerService) {
        return new SyncRestFactory(clientCryptoManagerService);
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
                                                   RegistrationRepository registrationRepository) {
        return new RegistrationService(appContext, packetWriterService, userInterfaceHelperService,
                registrationRepository);
    }

    @Provides
    @Singleton
    UserInterfaceHelperService provideUserInterfaceHelperService() {
        return new UserInterfaceHelperService(appContext);
    }

    @Provides
    @Singleton
    RegistrationRepository provideRegistrationRepository(RegistrationDao registrationDao) {
        return new RegistrationRepository(registrationDao);
    }
}