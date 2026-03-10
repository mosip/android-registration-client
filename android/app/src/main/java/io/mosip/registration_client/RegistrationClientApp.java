/*
 * Copyright (c) Modular Open Source Identity Platform
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *
*/

package io.mosip.registration_client;

import android.app.Application;

import io.mosip.registration.clientmanager.config.AppModule;
import io.mosip.registration.clientmanager.config.NetworkModule;
import io.mosip.registration.clientmanager.config.RoomModule;

public class RegistrationClientApp extends Application {

    private volatile AppComponent appComponent;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public synchronized AppComponent getAppComponent() {
        if (appComponent == null) {
            appComponent = DaggerAppComponent.builder()
                    .application(this)
                    .networkModule(new NetworkModule(this))
                    .roomModule(new RoomModule(this, getApplicationInfo()))
                    .appModule(new AppModule(this))
                    .hostApiModule(new HostApiModule(this))
                    .build();
        }
        return appComponent;
    }

    public synchronized void setAppComponent(AppComponent component) {
        this.appComponent = component;
    }
}
