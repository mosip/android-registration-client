package io.mosip.registration.app;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.mosip.registration.keymanager.service.LocalClientCryptoServiceImpl;
import io.mosip.registration.keymanager.spi.ClientCryptoManagerService;
import io.mosip.registration.packetmanager.service.PacketWriterServiceImpl;
import io.mosip.registration.packetmanager.service.PosixAdapterServiceImpl;
import io.mosip.registration.packetmanager.spi.ObjectAdapterService;
import io.mosip.registration.packetmanager.spi.PacketWriterService;

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
    public ClientCryptoManagerService provideClientCryptoManagerService(){
        return new LocalClientCryptoServiceImpl(appContext);
    }

    @Singleton
    @Provides
    public PacketWriterService providePacketWriterService(){
        return new PacketWriterServiceImpl(appContext);
    }

    @Singleton
    @Provides
    public ObjectAdapterService provideObjectAdapterService() {
        return new PosixAdapterServiceImpl(appContext);
    }
}