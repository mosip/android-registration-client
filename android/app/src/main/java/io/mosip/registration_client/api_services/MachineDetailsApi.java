package io.mosip.registration_client.api_services;

import android.util.Log;

import androidx.annotation.NonNull;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.mosip.registration.keymanager.spi.ClientCryptoManagerService;
import io.mosip.registration_client.model.MachinePigeon;


@Singleton
public class MachineDetailsApi  implements MachinePigeon.MachineApi {
    Map<String, String> machineDetails = new HashMap<>();
    MachinePigeon.Machine machine;
    ClientCryptoManagerService clientCryptoManagerService;

    @Inject
    public MachineDetailsApi(ClientCryptoManagerService clientCryptoManagerService) {
        this.clientCryptoManagerService = clientCryptoManagerService;
    }

    @NonNull
    @Override
    public MachinePigeon.Machine getMachineDetails() {
        Map<String, String> details =
                clientCryptoManagerService.getMachineDetails();

        if(details.get("name") == null) {
            machine = new MachinePigeon.Machine.Builder()
                    .setMap(machineDetails)
                    .setErrorCode("REG_MACHINE_NOT_INITIALIZED")
                    .build();
            return machine;
        }

        try {
            details.put("version", "Alpha");
            machine = new MachinePigeon.Machine.Builder()
                    .setMap(details)
                    .build();
        } catch (Exception e) {
            Log.e(getClass().getSimpleName(), e.getMessage(), e);
            machine = new MachinePigeon.Machine.Builder()
                    .setMap(machineDetails)
                    .setErrorCode("REG_MACHINE_NOT_FETCHED")
                    .build();
        }

        return machine;
    }
}
