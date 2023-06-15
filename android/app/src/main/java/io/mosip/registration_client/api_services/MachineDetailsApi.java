package io.mosip.registration_client.api_services;

import android.util.Log;

import androidx.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.mosip.registration.keymanager.spi.ClientCryptoManagerService;
import io.mosip.registration_client.models.MachinePigeon;


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
                    .setError("Machine could not be initialized.")
                    .build();
        }

        JSONObject jsonObject = new JSONObject(details);

        try {
            jsonObject.put("version", "Alpha");
            details.put("version", "Alpha");
            machine = new MachinePigeon.Machine.Builder()
                    .setMap(details)
                    .build();
        } catch (JSONException e) {
            Log.e(getClass().getSimpleName(), e.getMessage(), e);
            machine = new MachinePigeon.Machine.Builder()
                    .setMap(machineDetails)
                    .setError("Machine details could not be fetched!")
                    .build();
        }

        return machine;
    }
}
