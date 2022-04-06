package io.mosip.registration.clientmanager.spi;

import io.mosip.registration.clientmanager.dto.registration.GenericDto;

import java.util.List;

public interface MasterDataService {

    void initialSync() throws Exception;
    void syncCertificate();
    void syncMasterData() throws Exception;

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
    List<String> findLocationByParentHierarchyCode(String parentCode, String langCode);

    /**
     * Returns the list of immediate children for the provided hierarchy Level
     * @param hierarchyLevel
     * @param langCode
     * @return
     */
    List<String> findLocationByHierarchyLevel(int hierarchyLevel, String langCode);

}
