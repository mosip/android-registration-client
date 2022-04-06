package io.mosip.registration.app;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import io.mosip.registration.app.activites.*;
import io.mosip.registration.clientmanager.service.MasterDataServiceImpl;
import io.mosip.registration.clientmanager.spi.MasterDataService;

@Module
public abstract class ActivityBuildersModule {

    @ContributesAndroidInjector
    abstract LoginActivity contributeLoginActivity();

    @ContributesAndroidInjector
    abstract MainActivity contributeMainActivity();

    @ContributesAndroidInjector
    abstract RegistrationActivity contributeRegistrationActivity();

    @ContributesAndroidInjector
    abstract BiometricsActivity contributeBiometricsActivity();

    @ContributesAndroidInjector
    abstract DocumentsActivity contributeDocumentsActivity();

    @ContributesAndroidInjector
    abstract DemographicsActivity contributeDemographicsActivity();

    @ContributesAndroidInjector
    abstract PreviewActivity contributePreviewActivity();

    @ContributesAndroidInjector
    abstract ListingActivity contributeListingActivity();

}
