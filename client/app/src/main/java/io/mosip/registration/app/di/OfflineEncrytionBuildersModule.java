package io.mosip.registration.app.di;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import io.mosip.registration.app.ObjectStoreDemo;
import io.mosip.registration.clientmanager.util.OfflineEncryptionUtil;

@Module
public abstract class OfflineEncrytionBuildersModule {

    @ContributesAndroidInjector
    abstract OfflineEncryptionUtil contributeOfflineEncryptionUtil();
}
