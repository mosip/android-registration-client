package io.mosip.registration.clientmanager.spi;

import org.json.JSONObject;
import io.mosip.registration.clientmanager.dto.CenterMachineDto;
import io.mosip.registration.clientmanager.dto.registration.GenericDto;
import io.mosip.registration.clientmanager.dto.registration.GenericValueDto;
import io.mosip.registration.clientmanager.entity.Language;
import io.mosip.registration.clientmanager.entity.Location;

import java.util.List;
import java.nio.file.Path;

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
    /**
     * Fetches policy key
     */
    void syncCertificate(Runnable onFinish, String applicationId, String referenceId, String setApplicationId, String setReferenceId, boolean isManualSync);

    /**
     * Fetches all the master data
     * @throws Exception
     */
    void syncMasterData(Runnable onFinish, int retryNo, boolean isManualSync) throws Exception;

    /**
     * Fetches all the global params from the server and sync locally.
     * @throws Exception
     */
    void syncGlobalParamsData(Runnable onFinish, boolean isManualSync) throws Exception;

    /**
     * Fetches latest Id schema and UI specs
     * @throws Exception
     */
    void syncLatestIdSchema(Runnable onFinish, boolean isManualSync) throws Exception;

    /**
     * Fetches all the user mapped to the center mapped to this machine
     * @throws Exception
     */
    void syncUserDetails(Runnable onFinish, boolean isManualSync) throws Exception;

    void syncCACertificates(Runnable onFinish, boolean isManualSync);

    String onResponseComplete();


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
    List<GenericValueDto> findLocationByParentHierarchyCode(String parentCode, String langCode);

    /**
     * Returns the list of immediate children for the provided hierarchy Level
     * @param hierarchyLevel
     * @param langCode
     * @return
     */
    List<GenericValueDto> findLocationByHierarchyLevel(int hierarchyLevel, String langCode);

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

    List<Language> getAllLanguages();

    List<Location> findAllLocationsByLangCode(String langCode);

    void saveGlobalParam(String id, String value);

    String getGlobalParamValue(String id);

//    void downloadUrlData(Path path, JSONObject jsonObject);
}
