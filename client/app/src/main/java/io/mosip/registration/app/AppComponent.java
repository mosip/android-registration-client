package io.mosip.registration.app;

import android.app.Application;

import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;
import dagger.android.AndroidInjector;
import dagger.android.support.AndroidSupportInjectionModule;
import io.mosip.registration.clientmanager.config.RoomModule;

@Singleton
@Component(
        modules = {
                AndroidSupportInjectionModule.class,
                ActivityBuildersModule.class,
                RoomModule.class,
                AppModule.class
        }
)
public interface AppComponent extends AndroidInjector<BaseApplication> {

    @Component.Builder
    interface Builder{
        @BindsInstance
        Builder application(Application application);
        Builder roomModule(RoomModule roomModule);
        Builder appModule(AppModule appModule);
        AppComponent build();
    }
}
