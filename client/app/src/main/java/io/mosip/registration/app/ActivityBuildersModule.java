package io.mosip.registration.app;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import io.mosip.registration.app.activites.*;

@Module
public abstract class ActivityBuildersModule {

    @ContributesAndroidInjector
    abstract MainActivity contributeMainActivity();

    @ContributesAndroidInjector
    abstract PacketWriterActivity contributePacketWriterActivity();

    @ContributesAndroidInjector
    abstract PosixAdapterActivity contributePosixAdapterActivity();

    @ContributesAndroidInjector
    abstract LoginActivity contributeLoginActivity();

    @ContributesAndroidInjector
    abstract RegistrationController contributeDemographicRegistrationController();

    @ContributesAndroidInjector
    abstract BiometricsActivity contributeBiometricsActivity();

    @ContributesAndroidInjector
    abstract DocumentsActivity contributeDocumentsActivity();

    @ContributesAndroidInjector
    abstract DemographicsActivity contributeDemographicsActivity();

    @ContributesAndroidInjector
    abstract PreviewActivity contributePreviewActivity();

}
