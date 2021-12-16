package io.mosip.registration.app.di;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import io.mosip.registration.app.MainActivity;
import io.mosip.registration.app.ObjectStoreDemo;

@Module
public abstract class ActivityBuildersModule {

    @ContributesAndroidInjector
    abstract MainActivity contributeMainActivity();
}