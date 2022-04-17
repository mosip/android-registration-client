package io.mosip.registration.clientmanager.spi;

import io.mosip.registration.clientmanager.dto.CenterMachineDto;
import io.mosip.registration.clientmanager.dto.registration.GenericDto;
import java.util.List;

public interface MasterDataService {

    /**
     *
     * @return CenterMachineDto
     */
    CenterMachineDto getRegistrationCenterMachineDetails();

    /**
     * Triggers syncCertificate and syncMasterData
     * @throws Exception
     */
    void manualSync() throws Exception;

    /**
     * Fetches policy key
     */
    void syncCertificate();

    /**
     * Fetches all the master data
     * @throws Exception
     */
    void syncMasterData() throws Exception;


    void syncLatestIdSchema() throws Exception;


    /**
     *
     * @param hierarchyLevelName
     * @return
     */
    Integer getHierarchyLevel(String hierarchyLevelName);

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
    List<String> findLocationByHierarchyLevel(String hierarchyLevelName, String langCode);


    /**
     *
     * @param categoryCode
     * @param applicantType
     * @param langCode
     * @return
     */
    List<String> getDocumentTypes(String categoryCode, String applicantType, String langCode);

}
