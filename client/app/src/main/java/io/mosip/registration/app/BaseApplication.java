package io.mosip.registration.app;

import androidx.work.Configuration;
import androidx.work.WorkManager;
import dagger.android.AndroidInjector;
import dagger.android.DaggerApplication;
import io.mosip.registration.clientmanager.config.RoomModule;
import io.mosip.registration.clientmanager.factory.ClientWorkerFactory;

import javax.inject.Inject;

public class BaseApplication extends DaggerApplication {

    @Inject
    ClientWorkerFactory clientWorkerFactory;

    @Override
    public void onCreate() {
        super.onCreate();
        // register ours custom factory to WorkerManager
        // provide custom configuration
        Configuration customConfig = new Configuration.Builder()
                .setMinimumLoggingLevel(android.util.Log.INFO)
                .setWorkerFactory(clientWorkerFactory)
                .build();
        // initialize WorkManager
        WorkManager.initialize(getApplicationContext(), customConfig);
    }

    @Override
    protected AndroidInjector<? extends DaggerApplication> applicationInjector() {
        return DaggerAppComponent.builder().application(this)
                .roomModule(new RoomModule(this))
                .appModule(new AppModule(this))
                .build();
    }
}
