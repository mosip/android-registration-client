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

/**
 * Custom {@link Application} used as the root for dependency injection.
 *
 * - Builds the Dagger {@link AppComponent} once per process.
 * - Exposes that component to both UI ({@link MainActivity}) and background
 *   work ({@link SyncWorker}) via {@link #getAppComponent()}.
 *
 * Using a real Application class (referenced from AndroidManifest.xml) is
 * what allows WorkManager jobs to resolve the same graph even when the UI
 * process is recreated solely to run background work.
 */
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
