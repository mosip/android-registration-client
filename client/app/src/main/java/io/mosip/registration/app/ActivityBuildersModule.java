package io.mosip.registration.app;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import io.mosip.registration.app.activites.*;
import io.mosip.registration.clientmanager.jobs.ConfigDataSyncJob;
import io.mosip.registration.clientmanager.jobs.PacketStatusSyncJob;

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
    abstract PacketListActivity contributeListingActivity();

    @ContributesAndroidInjector
    abstract ScreenActivity contributeScreenActivity();

    @ContributesAndroidInjector
    abstract AboutActivity contributeAboutActivity();

    @ContributesAndroidInjector
    abstract JobServiceActivity jobServiceActivity();

    @ContributesAndroidInjector
    abstract PacketStatusSyncJob providePacketStatusSyncJob();

    @ContributesAndroidInjector
    abstract ConfigDataSyncJob provideConfigDataSyncJob();

    @ContributesAndroidInjector
    abstract PreviewDocumentActivity contributePreviewDocumentActivity();

    @ContributesAndroidInjector
    abstract AcknowledgementActivity contributeAcknowledgementActivity();

    @ContributesAndroidInjector
    abstract ModalityActivity contributeModalityActivity();

}
