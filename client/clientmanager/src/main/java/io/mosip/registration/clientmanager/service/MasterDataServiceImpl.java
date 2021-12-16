package io.mosip.registration.clientmanager.service;

import io.mosip.registration.clientmanager.dto.GenericDto;
import io.mosip.registration.clientmanager.spi.MasterDataService;

import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;


@Singleton
public class   MasterDataServiceImpl implements MasterDataService {


    /**
     * TODO - Currently stubbed to support dependent tasks
     */
    @Override
    public List<GenericDto> getAllLocationHierarchyLevels(String langCode) {
        List<GenericDto> list = new ArrayList<>();
        list.add(new GenericDto("1", "Country", langCode));
        list.add(new GenericDto("2", "Region", langCode));
        list.add(new GenericDto("3", "Province", langCode));
        list.add(new GenericDto("4", "City", langCode));
        list.add(new GenericDto("5", "Postal Code", langCode));
        return list;
    }

    /**
     * TODO - Currently stubbed to support dependent tasks
     */
    @Override
    public List<GenericDto> getFieldValues(String fieldName, String langCode) {
        List<GenericDto> list = new ArrayList<>();
        list.add(new GenericDto("code-a", fieldName+"-a", langCode));
        list.add(new GenericDto("code-b", fieldName+"-b", langCode));
        list.add(new GenericDto("code-c", fieldName+"-c", langCode));
        return list;
    }

    /**
     * TODO - Currently stubbed to support dependent tasks
     */
    @Override
    public List<GenericDto> findLocationByParentHierarchyCode(String parentCode, String langCode) {
        List<GenericDto> list = new ArrayList<>();
        switch (parentCode) {
            case "MC":
                list.add(new GenericDto("RG1", "Region 1", langCode));
                list.add(new GenericDto("RG2", "Region 2", langCode));
                break;
            case "RG1":
                list.add(new GenericDto("PR1", "RG1 Province 1", langCode));
                break;
            case "RG2":
                list.add(new GenericDto("PR2", "RG2 Province 1", langCode));
                break;
            case "PR1":
                list.add(new GenericDto("CT1", "PR1 City 1", langCode));
                break;
            case "PR2":
                list.add(new GenericDto("CT2", "PR2 City 1", langCode));
                break;
            case "CT1":
                list.add(new GenericDto("PC1", "CT1 Postal 000", langCode));
                list.add(new GenericDto("PC2", "CT1 Postal 001", langCode));
                list.add(new GenericDto("PC3", "CT1 Postal 002", langCode));
                break;
            case "CT2":
                list.add(new GenericDto("PC21", "CT2 Postal 111", langCode));
                list.add(new GenericDto("PC22", "CT2 Postal 112", langCode));
                list.add(new GenericDto("PC23", "CT2 Postal 113", langCode));
                break;
            default:
                list.add(new GenericDto("MC", "My Country", langCode));
                break;
        }
        return list;
    }
}
