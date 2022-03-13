package io.mosip.registration.app;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class ActivityBuildersModule {

    @ContributesAndroidInjector
    abstract MainActivity contributeMainActivity();

    @ContributesAndroidInjector
    abstract PacketWriterActivity contributePacketWriterActivity();

    @ContributesAndroidInjector
    abstract PosixAdapterActivity contributePosixAdapterActivity();

}
