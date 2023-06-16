/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
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
