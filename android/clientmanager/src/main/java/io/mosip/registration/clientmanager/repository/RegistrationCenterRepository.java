package io.mosip.registration.clientmanager.repository;

import io.mosip.registration.clientmanager.dao.RegistrationCenterDao;
import io.mosip.registration.clientmanager.entity.RegistrationCenter;
import org.json.JSONException;
import org.json.JSONObject;

import javax.inject.Inject;
import java.util.List;

public class RegistrationCenterRepository {

    private RegistrationCenterDao registrationCenterDao;

    @Inject
    public RegistrationCenterRepository(RegistrationCenterDao registrationCenterDao) {
        this.registrationCenterDao = registrationCenterDao;
    }

    public List<RegistrationCenter> getRegistrationCenter(String centerId) {
        return this.registrationCenterDao.getAllRegistrationCentersById(centerId);
    }

    public RegistrationCenter getRegistrationCenterByCenterIdAndLangCode(String centerId, String langCode) {
        return this.registrationCenterDao.getRegistrationCenterByCenterIdAndLangCode(centerId, langCode);
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
