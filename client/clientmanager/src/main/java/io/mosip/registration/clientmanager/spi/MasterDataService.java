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
     * @param hierarchyLevelName
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

    /**
     *
     * @param templateName
     * @param language
     * @return
     */
    String getTemplateContent(String templateName, String language);

    /**
     *
     * @param templateName
     * @param templateTypeCode
     * @param language
     * @return
     */
    String getPreviewTemplateContent(String templateName, String templateTypeCode, String language);

}
