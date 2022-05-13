package io.mosip.registration.app;

import android.app.job.JobService;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import io.mosip.registration.app.activites.*;
import io.mosip.registration.clientmanager.jobservice.PacketSyncStatusJob;

@Module
public abstract class ActivityBuildersModule {

    @ContributesAndroidInjector
    abstract LoginActivity contributeLoginActivity();

    @ContributesAndroidInjector
    abstract MainActivity contributeMainActivity();

    @ContributesAndroidInjector
    abstract RegistrationActivity contributeRegistrationActivity();

    @ContributesAndroidInjector
    abstract PreviewActivity contributePreviewActivity();

    @ContributesAndroidInjector
    abstract ListingActivity contributeListingActivity();

    @ContributesAndroidInjector
    abstract ScreenActivity contributeScreenActivity();

    @ContributesAndroidInjector
    abstract AboutActivity contributeAboutActivity();

    @ContributesAndroidInjector
    abstract JobServiceActivity jobServiceActivity();

    @ContributesAndroidInjector
    abstract PacketSyncStatusJob providePacketSyncStatusJob();

    @ContributesAndroidInjector
    abstract PreviewDocumentActivity contributePreviewDocumentActivity();

}
