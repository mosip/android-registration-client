package io.mosip.registration.clientmanager.repository;

import androidx.annotation.NonNull;
import io.mosip.registration.clientmanager.dao.MachineMasterDao;
import io.mosip.registration.clientmanager.entity.MachineMaster;
import io.mosip.registration.keymanager.spi.ClientCryptoManagerService;
import org.json.JSONException;
import org.json.JSONObject;

import javax.inject.Inject;

public class MachineRepository {

    private MachineMasterDao machineMasterDao;

    @Inject
    public MachineRepository(MachineMasterDao machineMasterDao) {
        this.machineMasterDao = machineMasterDao;;
    }

    public MachineMaster getMachine(@NonNull String machineName) {
        return this.machineMasterDao.findMachineByName(machineName);
    }

    public void saveMachineMaster(JSONObject machineJson) throws JSONException {
        MachineMaster machineMaster = new MachineMaster(machineJson.getString("id"));
        machineMaster.setIsActive(machineJson.getBoolean("isActive"));
        machineMaster.setName(machineJson.getString("name"));
        machineMaster.setRegCenterId(machineJson.getString("regCenterId"));
        machineMaster.setValidityDateTime(null); //TODO
        machineMasterDao.insert(machineMaster);
    }
}
