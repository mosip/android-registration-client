package io.mosip.registration_client;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.mosip.registration.clientmanager.service.LoginService;
import io.mosip.registration.keymanager.spi.ClientCryptoManagerService;
import io.mosip.registration_client.api_services.MachineDetailsApi;
import io.mosip.registration_client.api_services.UserDetailsApi;

@Module
public class HostApiModule {
    @Provides
    @Singleton
    UserDetailsApi getLoginActivityApi(LoginService loginService) {
        return new UserDetailsApi(loginService);
    }

    @Provides
    @Singleton
    MachineDetailsApi getMachineDetailsApi(ClientCryptoManagerService clientCryptoManagerService) {
        return new MachineDetailsApi(clientCryptoManagerService);
    }
   

}
