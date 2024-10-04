/*
 * Copyright (c) Modular Open Source Identity Platform
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *
*/

package io.mosip.registration_client;

import android.app.Application;

import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;
import dagger.android.AndroidInjector;
import dagger.android.support.AndroidSupportInjectionModule;
import io.flutter.embedding.android.FlutterActivity;
import io.mosip.registration.clientmanager.config.AppModule;
import io.mosip.registration.clientmanager.config.NetworkModule;
import io.mosip.registration.clientmanager.config.RoomModule;

@Singleton
@Component(
        modules = {
                AndroidSupportInjectionModule.class,
                NetworkModule.class,
                RoomModule.class,
                AppModule.class,
                HostApiModule.class,
        }
)
public interface AppComponent  extends AndroidInjector<FlutterActivity> {

    void inject(MainActivity mainActivity);

    @Component.Builder
    interface Builder{
        @BindsInstance
        Builder application(Application application);
        Builder networkModule(NetworkModule networkModule);
        Builder roomModule(RoomModule roomModule);
        Builder appModule(AppModule appModule);
        Builder hostApiModule(HostApiModule hostApiModule);
//        Builder activityBuildersModule(ActivityBuildersModule activityBuildersModule);
        AppComponent build();
    }

}
