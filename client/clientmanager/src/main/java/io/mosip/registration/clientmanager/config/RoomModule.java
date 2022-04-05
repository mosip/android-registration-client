package io.mosip.registration.clientmanager.config;

import android.app.Application;
import androidx.room.Room;
import dagger.Module;
import dagger.Provides;
import io.mosip.registration.clientmanager.dao.RegistrationDao;
import io.mosip.registration.clientmanager.dao.UserTokenDao;

import javax.inject.Singleton;


@Module
public class RoomModule {

    private static final String DATABASE_NAME = "reg-client";

    private ClientDatabase clientDatabase;

    public RoomModule(Application application) {
        clientDatabase = Room.databaseBuilder(application, ClientDatabase.class, DATABASE_NAME)
                .allowMainThreadQueries()
                .build();
    }

    @Singleton
    @Provides
    ClientDatabase providesRoomDatabase() {
        return clientDatabase;
    }

    @Singleton
    @Provides
    RegistrationDao providesRegistrationDao(ClientDatabase clientDatabase) {
        return clientDatabase.registrationDao();
    }

    @Singleton
    @Provides
    UserTokenDao providesUserTokenDao(ClientDatabase clientDatabase) {
        return clientDatabase.userTokenDao();
    }

}
