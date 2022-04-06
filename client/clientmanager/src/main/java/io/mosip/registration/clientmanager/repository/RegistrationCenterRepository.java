package io.mosip.registration.clientmanager.repository;

import io.mosip.registration.clientmanager.dao.RegistrationCenterDao;
import io.mosip.registration.clientmanager.entity.RegistrationCenter;
import org.json.JSONException;
import org.json.JSONObject;

import javax.inject.Inject;

public class RegistrationCenterRepository {

    private RegistrationCenterDao registrationCenterDao;

    @Inject
    public RegistrationCenterRepository(RegistrationCenterDao registrationCenterDao) {
        this.registrationCenterDao = registrationCenterDao;
    }

    public void saveRegistrationCenter(JSONObject centerJson) throws JSONException {
        RegistrationCenter registrationCenter = new RegistrationCenter(centerJson.getString("id"),
                centerJson.getString("langCode"));
        registrationCenter.setLatitude(centerJson.getString("latitude"));
        registrationCenter.setLongitude(centerJson.getString("longitude"));
        registrationCenter.setLocationCode(centerJson.getString("locationCode"));
        registrationCenter.setName(centerJson.getString("name"));
        registrationCenter.setIsActive(centerJson.getBoolean("isActive"));
        //TODO
        registrationCenterDao.insert(registrationCenter);
    }
}
