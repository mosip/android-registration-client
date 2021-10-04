package io.mosip.registration.app.di;

import android.app.Application;

import dagger.BindsInstance;
import dagger.Component;
import dagger.android.AndroidInjector;
import dagger.android.support.AndroidSupportInjectionModule;
import io.mosip.registration.app.BaseApplication;
import io.mosip.registration.clientmanager.service.crypto.LocalClientCryptoServiceImpl;

@Component(
        modules = {
                AndroidSupportInjectionModule.class,
                ActivityBuildersModule.class,
                AppModule.class,
        }
)
public interface AppComponent extends AndroidInjector<BaseApplication> {

    @Component.Builder
    interface Builder{
        @BindsInstance
        Builder application(Application application);
        Builder appModule(AppModule appModule);
        AppComponent build();
    }

}
