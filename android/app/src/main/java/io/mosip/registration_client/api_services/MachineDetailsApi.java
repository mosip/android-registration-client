package io.mosip.registration_client.api_services;

import android.util.Log;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.mosip.registration.clientmanager.entity.RegistrationCenter;
import io.mosip.registration.clientmanager.repository.RegistrationCenterRepository;
import io.mosip.registration.keymanager.spi.ClientCryptoManagerService;
import io.mosip.registration_client.model.MachinePigeon;


@Singleton
public class MachineDetailsApi  implements MachinePigeon.MachineApi {
    Map<String, String> machineDetails = new HashMap<>();
    MachinePigeon.Machine machine;
    ClientCryptoManagerService clientCryptoManagerService;
    RegistrationCenterRepository registrationCenterRepository;

    @Inject
    public MachineDetailsApi(ClientCryptoManagerService clientCryptoManagerService,
                             RegistrationCenterRepository registrationCenterRepository) {
        this.clientCryptoManagerService = clientCryptoManagerService;
        this.registrationCenterRepository = registrationCenterRepository;
    }

    @Override
    public void getMachineDetails(@NonNull MachinePigeon.Result<MachinePigeon.Machine> result) {
        Map<String, String> details =
                clientCryptoManagerService.getMachineDetails();

        if(details.get("name") == null) {
            machine = new MachinePigeon.Machine.Builder()
                    .setMap(machineDetails)
                    .setErrorCode("REG_MACHINE_NOT_INITIALIZED")
                    .build();
            result.success(machine);
        }

        try {
            details.put("version", "Alpha");
            machine = new MachinePigeon.Machine.Builder()
                    .setMap(details)
                    .build();
            result.success(machine);
        } catch (Exception e) {
            Log.e(getClass().getSimpleName(), e.getMessage(), e);
            machine = new MachinePigeon.Machine.Builder()
                    .setMap(machineDetails)
                    .setErrorCode("REG_MACHINE_NOT_FETCHED")
                    .build();
            result.success(machine);
        }
    }

    @Override
    public void getCenterName(@NonNull String regCenterId, @NonNull MachinePigeon.Result<String> result) {
        List<RegistrationCenter> registrationCenterList = new ArrayList<>();
        String regCenter = "";
        try {
            registrationCenterList =
                    registrationCenterRepository.getRegistrationCenter(regCenterId);
        } catch (Exception e) {
            Log.e(getClass().getSimpleName(), "Error in getCenterName", e);
        }

        if(registrationCenterList != null && !registrationCenterList.isEmpty()) {
            regCenter = registrationCenterList.get(0).getName();
        }
        result.success(regCenter);
    }
}
