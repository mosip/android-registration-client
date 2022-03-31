package io.mosip.registration.clientmanager.spi;

import io.mosip.registration.clientmanager.dto.GenericDto;

import java.util.List;

public interface MasterDataService {

    /**
     * Returns the list of supported location hierarchy levels
     * @param langCode
     * @return
     */
    List<GenericDto> getAllLocationHierarchyLevels(String langCode);

    /**
     * Fetch possible list of values for the provided fieldName
     * fieldName is usually the subType in UI-Spec
     * @param fieldName
     * @param langCode
     * @return
     */
    List<String> getFieldValues(String fieldName, String langCode);

    /**
     * Returns the list of immediate children for the provided parent location code
     * @param parentCode
     * @param langCode
     * @return
     */
    List<GenericDto> findLocationByParentHierarchyCode(String parentCode, String langCode);

}
