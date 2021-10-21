package io.mosip.registration.app.di;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.mosip.registration.clientmanager.service.crypto.LocalClientCryptoServiceImpl;
import io.mosip.registration.clientmanager.service.packet.PosixAdapter;
import io.mosip.registration.clientmanager.util.OfflineEncryptionUtil;

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

    @Provides
    public LocalClientCryptoServiceImpl provideLocalClientCryptoServiceImpl(){
        return new LocalClientCryptoServiceImpl(appContext);
    }

    @Provides
    public PosixAdapter posixAdapter(){
        return new PosixAdapter(appContext);
    }

    @Provides
    public OfflineEncryptionUtil offlineEncryptionUtil(){
        return new OfflineEncryptionUtil(appContext);
    }
}