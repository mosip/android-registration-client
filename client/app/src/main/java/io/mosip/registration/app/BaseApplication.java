package io.mosip.registration.app;

import dagger.android.AndroidInjector;
import dagger.android.DaggerApplication;
import io.mosip.registration.app.di.AppModule;
import io.mosip.registration.app.di.DaggerAppComponent;


public class BaseApplication extends DaggerApplication {
    @Override
    protected AndroidInjector<? extends DaggerApplication> applicationInjector() {
        return DaggerAppComponent.builder().application(this).appModule(new AppModule(this)).build();
    }
}
