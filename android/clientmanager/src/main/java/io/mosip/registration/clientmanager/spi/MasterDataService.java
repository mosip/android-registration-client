package io.mosip.registration.clientmanager.spi;

import io.mosip.registration.clientmanager.dto.CenterMachineDto;
import io.mosip.registration.clientmanager.dto.registration.GenericDto;
import io.mosip.registration.clientmanager.dto.registration.GenericValueDto;

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

    /**
     * Fetches all the global params from the server and sync locally.
     * @throws Exception
     */
    void syncGlobalParamsData() throws Exception;

    /**
     * Fetches latest Id schema and UI specs
     * @throws Exception
     */
    void syncLatestIdSchema() throws Exception;

    /**
     * Fetches all the user mapped to the center mapped to this machine
     * @throws Exception
     */
    void syncUserDetails() throws Exception;

    void syncCACertificates();


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
    List<GenericValueDto> getFieldValues(String fieldName, String langCode);

    List<GenericValueDto> getFieldValuesByCode(String fieldName, String code);

    /**
     * Returns the list of immediate children for the provided parent location code
     * @param parentCode
     * @param langCode
     * @return
     */
    List<String> findLocationByParentHierarchyCode(String parentCode, String langCode);

    /**
     * Returns the list of immediate children for the provided hierarchy Level
     * @param hierarchyLevelName
     * @param langCode
     * @return
     */
    List<GenericValueDto> findLocationByHierarchyLevel(String hierarchyLevelName, String langCode);

    /**
     * Returns the element for the provided code and language code
     * @param code
     * @return
     */
    List<GenericValueDto> findLocationByCode(String code);


    /**
     *
     * @param categoryCode
     * @param applicantType
     * @param langCode
     * @return
     */
    List<String> getDocumentTypes(String categoryCode, String applicantType, String langCode);

    /**
     *
     * @param templateName
     * @param language
     * @return
     */
    String getTemplateContent(String templateName, String language);

    /**
     *
     * @param templateTypeCode
     * @param language
     * @return
     */
    String getPreviewTemplateContent(String templateTypeCode, String language);

}
