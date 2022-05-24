package io.mosip.registration.app;

import dagger.android.AndroidInjector;
import dagger.android.DaggerApplication;
import io.mosip.registration.clientmanager.config.RoomModule;

public class BaseApplication extends DaggerApplication {


    @Override
    protected AndroidInjector<? extends DaggerApplication> applicationInjector() {
        return DaggerAppComponent.builder().application(this)
                .roomModule(new RoomModule(this))
                .appModule(new AppModule(this))
                .build();
    }
}
