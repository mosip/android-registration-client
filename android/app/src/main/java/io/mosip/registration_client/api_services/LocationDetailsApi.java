package io.mosip.registration_client.api_services;

import android.util.Log;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.mosip.registration.clientmanager.dao.LocationDao;
import io.mosip.registration.clientmanager.dao.LocationHierarchyDao;
import io.mosip.registration.clientmanager.dto.registration.GenericValueDto;
import io.mosip.registration_client.model.LocationResponsePigeon;

@Singleton
public class LocationDetailsApi implements LocationResponsePigeon.LocationResponseApi {


    LocationHierarchyDao locationHierarchyDao;

    LocationDao locationDao;

    @Inject
    public LocationDetailsApi(LocationHierarchyDao locationHierarchyDao,LocationDao locationDao ){
        this.locationDao = locationDao;
        this.locationHierarchyDao = locationHierarchyDao;
    }

    @Override
    public void fetchLocationList(@NonNull String langCode, @NonNull List<String> hierarchyName, @NonNull LocationResponsePigeon.Result<LocationResponsePigeon.LocationResponse> result) {




        Integer countryLevel = locationHierarchyDao.getHierarchyLevelFromName("Country");
        Integer regionLevel = locationHierarchyDao.getHierarchyLevelFromName("Region");
        Integer provinceLevel = locationHierarchyDao.getHierarchyLevelFromName("Province");
        Integer cityLevel = locationHierarchyDao.getHierarchyLevelFromName("City");
        Integer zoneLevel = locationHierarchyDao.getHierarchyLevelFromName("Zone");
        Integer postalCodeLevel = locationHierarchyDao.getHierarchyLevelFromName("Postal Code");


        List<GenericValueDto> countryList = locationDao.findAllLocationByHierarchyLevel(countryLevel, langCode);
        List<GenericValueDto> regionList = locationDao.findAllLocationByHierarchyLevel(regionLevel, langCode);
        List<GenericValueDto> provinceList = locationDao.findAllLocationByHierarchyLevel(provinceLevel, langCode);
        List<GenericValueDto> cityList = locationDao.findAllLocationByHierarchyLevel(cityLevel, langCode);
        List<GenericValueDto> zoneList = locationDao.findAllLocationByHierarchyLevel(zoneLevel, langCode);
        List<GenericValueDto> postalCodeList = locationDao.findAllLocationByHierarchyLevel(postalCodeLevel, langCode);

        List<String> countryValueList = new ArrayList<>();
        List<String> regionValueList =new ArrayList<>();
        List<String> provinceValueList = new ArrayList<>();
        List<String> cityValueList = new ArrayList<>();
        List<String> zoneValueList = new ArrayList<>();
        List<String> postalCodeValueList = new ArrayList<>();

        countryList.forEach((value)-> countryValueList.add(value.getName()));
        regionList.forEach((value)-> regionValueList.add(value.getName()));
        provinceList.forEach((value)-> provinceValueList.add(value.getName()));
        cityList.forEach((value)-> cityValueList.add(value.getName()));
        zoneList.forEach((value)-> zoneValueList.add(value.getName()));
        postalCodeList.forEach((value)-> postalCodeValueList.add(value.getName()));


        LocationResponsePigeon.LocationResponse locationResponse = new LocationResponsePigeon.LocationResponse.Builder()
                .setCountryList(countryValueList)
                .setRegionList(regionValueList)
                .setProvinceList(provinceValueList)
                .setCityList(cityValueList)
                .setZoneList(zoneValueList)
                .setPostalCodeList(postalCodeValueList)
                .build();

        result.success(locationResponse);
    }
}
