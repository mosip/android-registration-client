package io.mosip.registration.clientmanager.repository;

import io.mosip.registration.clientmanager.dao.LocationDao;
import io.mosip.registration.clientmanager.dao.LocationHierarchyDao;
import io.mosip.registration.clientmanager.dto.registration.GenericValueDto;
import io.mosip.registration.clientmanager.entity.Location;
import io.mosip.registration.clientmanager.entity.LocationHierarchy;
import org.json.JSONException;
import org.json.JSONObject;

import javax.inject.Inject;
import java.util.List;

public class LocationRepository {

    private LocationDao locationDao;
    private LocationHierarchyDao locationHierarchyDao;

    @Inject
    public LocationRepository(LocationDao locationDao,
                              LocationHierarchyDao locationHierarchyDao) {
        this.locationDao = locationDao;
        this.locationHierarchyDao = locationHierarchyDao;
    }

    public Integer getHierarchyLevel(String levelName) {
        return this.locationHierarchyDao.getHierarchyLevelFromName(levelName);
    }

    public List<String> getLocations(String parentLocCode, String langCode) {
        if(parentLocCode == null) {
            return this.locationDao.findParentLocation(langCode);
        }
        return this.locationDao.findAllLocationByParentLocCode(parentLocCode, langCode);
    }

    public List<GenericValueDto> getLocationsBasedOnHierarchyLevel(int level, String langCode) {
        return this.locationDao.findAllLocationByHierarchyLevel(level, langCode);
    }


    public List<GenericValueDto> getLocationsByCode(String code) {
        return this.locationDao.findAllLocationByCode(code);
    }

    public void saveLocationData(JSONObject locationJson) throws JSONException {
        Location location = new Location(locationJson.getString("code"),
                locationJson.getString("langCode"));
        location.setName(locationJson.getString("name"));
        location.setHierarchyLevel(locationJson.getInt("hierarchyLevel"));
        location.setHierarchyName(locationJson.getString("hierarchyName"));
        location.setIsActive(locationJson.getBoolean("isActive"));
        location.setIsDeleted(locationJson.getBoolean("isDeleted"));
        this.locationDao.insert(location);
    }

    public void saveLocationHierarchyData(JSONObject locationJson) throws JSONException {
        LocationHierarchy locationHierarchy = new LocationHierarchy(locationJson.getInt("hierarchyLevel"),
                locationJson.getString("hierarchyLevelName"),
                locationJson.getString("langCode"));
        this.locationHierarchyDao.insert(locationHierarchy);
    }
}
