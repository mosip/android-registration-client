/*
package io.mosip.registration.clientmanager.context;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.neovisionaries.i18n.LanguageAlpha3Code;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.clientmanager.constant.RegistrationConstants;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.dto.AuthTokenDTO;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.exception.RegistrationExceptionConstants;

*/
/**
 * This class will load all the property files as bundles All application level
 * details will be loaded in a map
 *
 * @author Taleev Aalam
 *
 *//*

public class ApplicationContext {

    */
/**
     * Instance of {@link Logger}
     *//*

    private static final Logger LOGGER = AppConfig.getLogger(ApplicationContext.class);

    */
/** The application context. *//*

    private static ApplicationContext applicationContext;

    */
/** The application map. *//*

    private static Map<String, Object> applicationMap = new HashMap<>();

    */
/** The application languge. *//*

    private String applicationLanguge;

    private List<String> mandatoryLanguages;
    private List<String> optionalLanguages;

    public List<String> getMandatoryLanguages() {
        return mandatoryLanguages;
    }

    public void setMandatoryLanguages(List<String> mandatoryLanguages) {
        this.mandatoryLanguages = mandatoryLanguages;
    }

    public List<String> getOptionalLanguages() {
        return optionalLanguages;
    }

    public void setOptionalLanguages(List<String> optionalLanguages) {
        this.optionalLanguages = optionalLanguages;
    }

    private static Map<String, ResourceBundle> resourceBundleMap = new HashMap<>();

    */
/**
     * Checks if is primary language right to left.
     *
     * @return true, if is primary language right to left
     *//*

    public boolean isPrimaryLanguageRightToLeft() {

        return isLanguageRightToLeft(applicationLanguge);
    }

    */
/**
     * Checks if given language is right to left.
     *
     * @return true, if is language right to left
     *//*

    public boolean isLanguageRightToLeft(String langCode) {
        String rightToLeft = (String) applicationContext.getApplicationMap().get("mosip.right_to_left_orientation");
        if (null != rightToLeft && rightToLeft.contains(langCode)) {
            return true;
        }
        return false;
    }

    */
/** The auth token DTO. *//*

    private AuthTokenDTO authTokenDTO;

    */
/**
     * Instantiates a new application context.
     *//*

    private ApplicationContext() {

    }

    */
/**
     * here we will load the property files such as labels, messages and validation.
     *
     * </P>
     * <p>
     * Based on those languages these property files will be loaded.
     * </p>
     *
     * @return
     * @throws RegBaseCheckedException
     *
     *
     *//*

    public void loadResourceBundle() throws RegBaseCheckedException {
        try {
            if (applicationLanguge == null) {
                List<String> langList = Stream.concat(mandatoryLanguages.stream(), optionalLanguages.stream())
                        .collect(Collectors.toList());

                if (null != langList && !langList.isEmpty()) {
                    //choosing first language in the concatenated list as default application language
                    Optional<String> defaultAppLang = langList.stream().filter(langCode -> !langCode.isBlank()).findFirst();
                    if (defaultAppLang.isPresent()) {
                        setApplicationLanguage(defaultAppLang.get());
                    };

                    for (String langCode : langList) {
                        if (!langCode.isBlank()) {
                            String labelLangCodeKey = String.format("%s_%s", langCode, RegistrationConstants.LABELS);
                            String localeCode = (langCode != null) ? (LanguageAlpha3Code.getByCodeIgnoreCase(langCode) != null ? LanguageAlpha3Code.getByCodeIgnoreCase(langCode).getAlpha2().name() : null) : "";
                            Locale locale = new Locale(localeCode != null ? localeCode : langCode.substring(0, 2));
                            ResourceBundle labelsBundle = ResourceBundle.getBundle(RegistrationConstants.LABELS, locale);
                            if (labelsBundle.getLocale().equals(locale)) {
                                resourceBundleMap.put(labelLangCodeKey, labelsBundle);
                            } else if (langList.size() == 1) {
                                throw new RegBaseCheckedException(RegistrationExceptionConstants.INVALID_LANGUAGE_CONFIGURED.getErrorCode(),
                                        RegistrationExceptionConstants.INVALID_LANGUAGE_CONFIGURED.getErrorMessage());
                            }

                            String messageLangCodeKey = String.format("%s_%s", langCode,
                                    RegistrationConstants.MESSAGES);
                            ResourceBundle messagesBundle = ResourceBundle.getBundle(RegistrationConstants.MESSAGES, locale);
                            if (messagesBundle.getLocale().equals(locale)) {
                                resourceBundleMap.put(messageLangCodeKey, messagesBundle);
                            } else {
                                LOGGER.error("ResourceBundle not found for configured langcode ", langCode);
                            }
                        }
                    }
                }

            }
        } catch (RuntimeException exception) {
            LOGGER.error("Application Context", RegistrationConstants.APPLICATION_NAME,
                    RegistrationConstants.APPLICATION_ID, exception.getMessage());
        }
    }

    */
/**
     * @param langCode   language code
     * @param bundleType messages or labels
     * @return Resource Bundle
     *//*

    public static ResourceBundle getBundle(String langCode, String bundleType) {

        return resourceBundleMap.get(String.format("%s_%s", langCode, bundleType));

    }

    public void setApplicationLanguage(String applicationLanguage) {
        this.applicationLanguge = applicationLanguage;

    }

    */
/**
     * Gets the single instance of ApplicationContext.
     *
     * @return single instance of ApplicationContext
     *//*

    public static ApplicationContext getInstance() {
        if (applicationContext == null) {
            applicationContext = new ApplicationContext();
            applicationContext.authTokenDTO = new AuthTokenDTO();
            return applicationContext;
        } else {
            return applicationContext;
        }
    }

    */
/**
     * Map.
     *
     * @return the map
     *//*

    public static Map<String, Object> map() {
        return applicationContext.getApplicationMap();
    }

    */
/**
     * Application language.
     *
     * @return the string
     *//*

    public static String applicationLanguage() {
        return applicationContext.getApplicationLanguage();
    }

    */
/**
     * Load resources.
     * @throws RegBaseCheckedException
     *//*

    public static void loadResources() throws RegBaseCheckedException {
        applicationContext.loadResourceBundle();
    }

    */
/**
     * Sets the auth token DTO.
     *
     * @param authTokenDTO the new auth token DTO
     *//*

    public static void setAuthTokenDTO(AuthTokenDTO authTokenDTO) {
        applicationContext.authTokenDTO = authTokenDTO;
    }

    */
/**
     * Auth token DTO.
     *
     * @return the auth token DTO
     *//*

    public static AuthTokenDTO authTokenDTO() {
        return applicationContext.authTokenDTO;
    }

    */
/**
     * Gets the application map.
     *
     * @return the applicationMap
     *//*

    public Map<String, Object> getApplicationMap() {
        return applicationMap;
    }

    */
/**
     * Sets the application map.
     *
     * @param applicationMap the applicationMap to set
     *//*

    public static void setApplicationMap(Map<String, Object> applicationMap) {
        ApplicationContext.applicationMap.putAll(applicationMap);
    }

    */
/**
     * Get application language.
     *
     * @return the application language
     *//*

    public String getApplicationLanguage() {
        return applicationLanguge;
    }

    */
/**
     * Sets the global config value of.
     *
     * @param code the code
     * @param val  the val
     *//*

    public static void setGlobalConfigValueOf(String code, String val) {
        applicationMap.put(code, val);
    }

    */
/**
     * Removes the global config value of.
     *
     * @param code the code
     *//*

    public static void removeGlobalConfigValueOf(String code) {
        applicationMap.remove(code);

    }

    */
/**
     * Gets the integer value.
     *
     * @param code the code
     * @return the integer value
     *//*

    public static Integer getIntValueFromApplicationMap(String code) {

        return applicationMap.containsKey(code) ? Integer.parseInt((String) applicationMap.get(code)) : null;

    }

    public static String getStringValueFromApplicationMap(String code) {

        return applicationMap.containsKey(code) ? String.valueOf(applicationMap.get(code)) : null;

    }

    public static Float getFloatValueFromApplicationMap(String code) {

        return applicationMap.containsKey(code) ? Float.parseFloat((String) applicationMap.get(code)) : null;

    }

    public static String getDateFormat() {
        return applicationMap.get("mosip.default.date.format") == null ? "yyyy/MM/dd"
                : String.valueOf(applicationMap.get("mosip.default.date.format"));
    }

    public ResourceBundle getApplicationLanguageLabelBundle() {
        return getBundle(getApplicationLanguage(), RegistrationConstants.LABELS);
    }

    public ResourceBundle getApplicationLanguageMessagesBundle() {
        return getBundle(getApplicationLanguage(), RegistrationConstants.MESSAGES);
    }

}*/
