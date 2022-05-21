/*
package io.mosip.registration.clientmanager.context;

import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import io.mosip.registration.clientmanager.constant.RegistrationConstants;
import io.mosip.registration.dto.*;
import org.springframework.context.ApplicationContext;

import io.mosip.kernel.biometrics.constant.BiometricType;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.LoggerConstants;
import io.mosip.registration.constants.LoginMode;
import io.mosip.registration.constants.ProcessNames;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.dto.packetmanager.BiometricsDto;
import io.mosip.registration.enums.Role;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.mdm.dto.MDMRequestDto;
import io.mosip.registration.service.bio.BioService;
import io.mosip.registration.service.login.LoginService;
import io.mosip.registration.service.security.AuthenticationService;
import io.mosip.registration.util.healthcheck.RegistrationSystemPropertiesChecker;
import io.mosip.registration.util.restclient.AuthTokenUtilService;

import javax.validation.constraints.NotNull;

*/
/**
 * This class will handle the creation of Session context, Security Context and
 * User Context. This will handle authentication of all the login methods.
 *
 *
 * @author Sravya Surampalli
 * @since 1.0.0
 *
 *//*

public class SessionContext {

    */
/**
     * Instance of {@link Logger}
     *//*

    private static final Logger LOGGER = AppConfig.getLogger(SessionContext.class);

    private static ApplicationContext applicationContext;

    public static void setApplicationContext(ApplicationContext applicationContext) {
        SessionContext.applicationContext = applicationContext;
    }

    */
/**
     * instance of sessionContext
     *//*

    private static SessionContext sessionContext;

    private static List<String> authModes = new ArrayList<>();

    private static List<String> validAuthModes = new ArrayList<>();
    private static final boolean HAVE_TO_SAVE_AUTH_TOKEN = true;

    */
/**
     * constructor of sessionContext
     *//*

    private SessionContext() {

    }

    private UUID id;
    private static UserContext userContext;
    private static SecurityContext securityContext;

    private static boolean isAutoLogout = true;
    private Date loginTime;
    private long refreshedLoginTime;
    private long timeoutInterval;
    private long idealTime;
    private Map<String, Object> mapObject;
    private AuthTokenDTO authTokenDTO;

    */
/**
     * This method will make the Session context class as singleton and returns the
     * instance of the Session context if available or else it will return null
     *
     * @return sessionContext
     *//*

    public static SessionContext getInstance() {
        if (null != authModes && null != validAuthModes && authModes.containsAll(validAuthModes)) {
            return sessionContext;
        } else {
            sessionContext = null;
            return sessionContext;
        }
    }

    */
/**
     * creating sessionContext and validating login
     * <p>
     * If Authentication Success:
     * </p>
     * <p>
     * Returns true and Creation of Session context, Security Context and User
     * Context will happen
     * </p>
     * <p>
     * If Authentication fails:
     * </p>
     * <p>
     * Returns false and Creation of Session context, Security Context and User
     * Context will not happen
     * </p>
     *
     * @param userDTO
     *            - UserInfo to create session which contains user id, user name,
     *            roles, center id
     * @param loginMethod
     *            - mode of login
     * @param isInitialSetUp
     *            - boolean variable to know if it is initial setup or not
     * @param isUserNewToMachine
     *            - is user first time accessing machine
     * @param authenticationValidatorDTO
     *            - Authentication validator should contain user id, pwd, otp
     *
     * @return boolean - Returns whether the Session context is getting created or
     *         not.
     * @throws IOException
     * @thuserDtlsrows RegBaseCheckedException
     *//*

    public static boolean create(UserDTO userDTO, String loginMethod, boolean isInitialSetUp,
                                 boolean isUserNewToMachine, AuthenticationValidatorDTO authenticationValidatorDTO)
            throws RegBaseCheckedException, IOException {
        LOGGER.debug("Entering into creating Session Context");

        Set<String> roleList = new LinkedHashSet<>();
        LoginService loginService = applicationContext.getBean(LoginService.class);
        if(userDTO != null) {
            userDTO =  loginService.getUserDetail(userDTO.getId());
            for(UserRoleDTO roleCode : userDTO.getUserRole()) {
                if (roleCode.isActive()) {
                    roleList.add(String.valueOf(roleCode.getRoleCode()));
                }
            }
        }

        if(!isInitialSetUp && userDTO == null) {
            LOGGER.error("Failed to create sessionContext as userDTO is null");
            return false;
        }

        if (isInitialSetUp) {
            authModes.add(RegistrationConstants.PWORD);
        } else {
            authModes = loginService.getModesOfLogin(ProcessNames.LOGIN.getType(), roleList);
        }

        if (null == sessionContext) {
            if (isInitialSetUp) {
                return validateInitialLogin(userDTO, loginMethod);
            } else {
                return validateAuthMethods(userDTO, loginMethod, authenticationValidatorDTO,
                        isUserNewToMachine);
            }
        } else {
            return validateAuthMethods(userDTO, loginMethod, authenticationValidatorDTO,
                    isInitialSetUp || isUserNewToMachine);
        }
    }

    */
/**
     * Validating login in case of initial setup or user new to machine
     *
     * @param userDTO
     *            - UserInfo to create session which contains user id, user name,
     *            roles, center id
     * @param loginMethod
     *            - mode of login
     *
     * @return boolean
     *//*

    private static boolean validateInitialLogin(UserDTO userDTO, String loginMethod) {
        AuthTokenUtilService authTokenUtilService = applicationContext.getBean(AuthTokenUtilService.class);
        try {
            AuthTokenDTO authTknDTO = authTokenUtilService.getAuthTokenAndRefreshToken(LoginMode.PASSWORD);
            if (null != authTknDTO) {
                createSessionContext();
                sessionContext.authTokenDTO = authTknDTO;
                validAuthModes.add(loginMethod);
                sessionContext = null;
                return true;
            } else {
                validAuthModes.remove(loginMethod);
                sessionContext = null;
                return false;
            }
        } catch (RegBaseCheckedException regBaseCheckedException) {
            return false;
        } catch (RuntimeException runtimeException) {
            sessionContext = null;
            return false;
        }
    }

    */
/**
     * Validating login wrt corresponding login method
     *
     * @param userDTO
     *            - UserInfo to create session which contains user id, user name,
     *            roles, center id
     * @param loginMethod
     *            - mode of login
     * @param authenticationValidatorDTO
     *            - Authentication validator should contain user id, pwd, otp
     * @param skipAuthModes
     *            To skip Auth modes validation
     *
     * @return boolean
     * @throws IOException
     * @throws RegBaseCheckedException
     *//*

    private static boolean validateAuthMethods(UserDTO userDTO, String loginMethod,
                                               AuthenticationValidatorDTO authenticationValidatorDTO, boolean skipAuthModes)
            throws RegBaseCheckedException, IOException {
        switch (loginMethod) {
            case RegistrationConstants.PWORD:
                return validatePword(loginMethod, userDTO, authenticationValidatorDTO, skipAuthModes);
            case RegistrationConstants.OTP:
                return validateOTP(loginMethod, userDTO, authenticationValidatorDTO);
            case RegistrationConstants.FINGERPRINT_UPPERCASE:
                return validateFingerprint(loginMethod, userDTO, authenticationValidatorDTO);
            case RegistrationConstants.IRIS:
                return validateIris(loginMethod, userDTO, authenticationValidatorDTO);
            case RegistrationConstants.FACE:
                return validateFace(loginMethod, userDTO, authenticationValidatorDTO);
            default:
        }
        return false;
    }

    */
/**
     * Validating login with pwd
     *
     * @param userDTO
     *            - UserInfo to create session which contains user id, user name,
     *            roles, center id
     * @param loginMethod
     *            - mode of login
     * @param authenticationValidatorDTO
     *            - Authentication validator should contain user id, pwd
     * @param skipAuthModes
     *            To Skip Auth Modes validation
     *
     * @return boolean
     *//*

    private static boolean validatePword(String loginMethod, UserDTO userDTO,
                                         AuthenticationValidatorDTO authenticationValidatorDTO, boolean skipAuthModes) {
        AuthenticationService authenticationService = applicationContext.getBean(AuthenticationService.class);
        try {
            if (authenticationValidatorDTO != null && authenticationService.validatePassword(authenticationValidatorDTO)) {
                createSessionContext();
                SessionContext.authTokenDTO().setLoginMode(loginMethod);
                validAuthModes.add(loginMethod);
                createSecurityContext(userDTO, skipAuthModes);
                return true;
            }
        } catch (RegBaseCheckedException e) {
            LOGGER.error("Failed validating password", e);
        }
        validAuthModes.remove(loginMethod);
        sessionContext = null;
        return false;
    }

    */
/**
     * Validating login with otp
     *
     * @param userDTO
     *            - UserInfo to create session which contains user id, user name,
     *            roles, center id
     * @param loginMethod
     *            - mode of login
     * @param authenticationValidatorDTO
     *            - Authentication validator should contain user id, otp
     *
     * @return boolean
     *//*

    private static boolean validateOTP(String loginMethod, UserDTO userDTO,
                                       AuthenticationValidatorDTO authenticationValidatorDTO) {
        AuthenticationService authenticationService = applicationContext.getBean(AuthenticationService.class);
        AuthTokenDTO authTknDTO = authenticationService.authValidator(RegistrationConstants.OTP,
                authenticationValidatorDTO.getUserId(), authenticationValidatorDTO.getOtp(), HAVE_TO_SAVE_AUTH_TOKEN);
        if (null != authTknDTO) {
            createSessionContext();
            sessionContext.authTokenDTO = authTknDTO;
            validAuthModes.add(loginMethod);
            createSecurityContext(userDTO, false);
            return true;
        } else {
            validAuthModes.remove(loginMethod);
            sessionContext = null;
            return false;
        }
    }


    */
/**
     * Validating login with Fingerprint
     *
     * @param userDTO
     *            - UserInfo to create session which contains user id, user name,
     *            roles, center id
     * @param loginMethod
     *            - mode of login
     * @param authenticationValidatorDTO
     *            - Authentication validator should contain user id
     *
     * @return boolean
     * @throws IOException
     * @throws RegBaseCheckedException
     *//*

    //TODO fetch environment for MDM request from applicationContext
    private static boolean validateFingerprint(String loginMethod, UserDTO userDTO,
                                               AuthenticationValidatorDTO authenticationValidatorDTO) throws RegBaseCheckedException, IOException {
        BioService bioService = applicationContext.getBean(BioService.class);
        AuthenticationService authService = applicationContext.getBean(AuthenticationService.class);

        String authSlab = io.mosip.registration.context.ApplicationContext.getStringValueFromApplicationMap(RegistrationConstants.AUTH_FINGERPRINT_SLAB);
        if(authSlab == null) { authSlab = RegistrationConstants.FINGERPRINT_SLAB_LEFT; }

        MDMRequestDto mdmRequestDto = new MDMRequestDto(authSlab, null,
                "Registration", io.mosip.registration.context.ApplicationContext.getStringValueFromApplicationMap(RegistrationConstants.SERVER_ACTIVE_PROFILE),
                io.mosip.registration.context.ApplicationContext
                        .getIntValueFromApplicationMap(RegistrationConstants.CAPTURE_TIME_OUT),
                1, io.mosip.registration.context.ApplicationContext
                .getIntValueFromApplicationMap(RegistrationConstants.FINGERPRINT_AUTHENTICATION_THRESHHOLD));

        List<BiometricsDto> biometrics = bioService.captureModalityForAuth(mdmRequestDto);
        if (authService.authValidator(authenticationValidatorDTO.getUserId(), BiometricType.FINGER.value(),
                biometrics)) {
            createSessionContext();
            SessionContext.authTokenDTO().setLoginMode(loginMethod);
            validAuthModes.add(loginMethod);
            createSecurityContext(userDTO, false);
            return true;
        } else {
            validAuthModes.remove(loginMethod);
            sessionContext = null;
            return false;
        }
    }

    */
/**
     * Validating login with Iris
     *
     * @param userDTO
     *            - UserInfo to create session which contains user id, user name,
     *            roles, center id
     * @param loginMethod
     *            - mode of login
     * @param authenticationValidatorDTO
     *            - Authentication validator should contain user id
     *
     * @return boolean
     * @throws IOException
     * @throws RegBaseCheckedException
     *//*

    //TODO fetch environment for MDM request from applicationContext
    private static boolean validateIris(String loginMethod, UserDTO userDTO,
                                        AuthenticationValidatorDTO authenticationValidatorDTO) throws RegBaseCheckedException, IOException {
        BioService bioService = applicationContext.getBean(BioService.class);
        AuthenticationService authService = applicationContext.getBean(AuthenticationService.class);

        MDMRequestDto mdmRequestDto = new MDMRequestDto(RegistrationConstants.IRIS_DOUBLE, null, "Registration",
                io.mosip.registration.context.ApplicationContext.getStringValueFromApplicationMap(RegistrationConstants.SERVER_ACTIVE_PROFILE),
                io.mosip.registration.context.ApplicationContext
                        .getIntValueFromApplicationMap(RegistrationConstants.CAPTURE_TIME_OUT),
                2, io.mosip.registration.context.ApplicationContext
                .getIntValueFromApplicationMap(RegistrationConstants.IRIS_THRESHOLD));

        List<BiometricsDto> biometrics = bioService.captureModalityForAuth(mdmRequestDto);
        if (authService.authValidator(authenticationValidatorDTO.getUserId(), BiometricType.IRIS.value(), biometrics)) {
            createSessionContext();
            SessionContext.authTokenDTO().setLoginMode(loginMethod);
            validAuthModes.add(loginMethod);
            createSecurityContext(userDTO, false);
            return true;
        } else {
            validAuthModes.remove(loginMethod);
            sessionContext = null;
            return false;
        }

    }

    */
/**
     * Validating login with Face
     *
     * @param userDTO
     *            - UserInfo to create session which contains user id, user name,
     *            roles, center id
     * @param loginMethod
     *            - mode of login
     * @param authenticationValidatorDTO
     *            - Authentication validator should contain user id
     *
     * @return boolean
     * @throws IOException
     * @throws RegBaseCheckedException
     *//*

    //TODO fetch environment for MDM request from applicationContext
    private static boolean validateFace(String loginMethod, UserDTO userDTO,
                                        AuthenticationValidatorDTO authenticationValidatorDTO) throws RegBaseCheckedException, IOException {
        BioService bioService = applicationContext.getBean(BioService.class);
        AuthenticationService authService = applicationContext.getBean(AuthenticationService.class);

        MDMRequestDto mdmRequestDto = new MDMRequestDto(RegistrationConstants.FACE_FULLFACE, null, "Registration", io.mosip.registration.context.ApplicationContext.getStringValueFromApplicationMap(RegistrationConstants.SERVER_ACTIVE_PROFILE),
                io.mosip.registration.context.ApplicationContext
                        .getIntValueFromApplicationMap(RegistrationConstants.CAPTURE_TIME_OUT), 1, io.mosip.registration.context.ApplicationContext
                .getIntValueFromApplicationMap(RegistrationConstants.FACE_THRESHOLD));

        List<BiometricsDto> biometrics = bioService.captureModalityForAuth(mdmRequestDto);
        if(authService.authValidator(authenticationValidatorDTO.getUserId(), BiometricType.FACE.value(), biometrics)) {
            createSessionContext();
            SessionContext.authTokenDTO().setLoginMode(loginMethod);
            validAuthModes.add(loginMethod);
            createSecurityContext(userDTO, false);
            return true;
        } else {
            validAuthModes.remove(loginMethod);
            sessionContext = null;
            return false;
        }

    }

    */
/**
     * Creating Session Context
     *//*

    private static void createSessionContext() {
        if (null == sessionContext) {
            sessionContext = new SessionContext();
            sessionContext.setId(UUID.randomUUID());
            sessionContext.setMapObject(new HashMap<>());
            SessionContext.setAuthTokenDTO(new AuthTokenDTO());
        }
    }

    */
/**
     * Creating Security Context
     *
     * @param skipAuthModes
     *            To Skip Auth Modes validation
     *//*

    private static void createSecurityContext(UserDTO userDTO, boolean skipAuthModes) {
        LOGGER.info(LoggerConstants.LOG_REG_LOGIN, APPLICATION_NAME, APPLICATION_ID,
                "Started Creating Security Session Context with auth modes skip : " + skipAuthModes);
        if (skipAuthModes || (null != authModes && null != validAuthModes && authModes.containsAll(validAuthModes))) {
            userContext = sessionContext.new UserContext();
            LoginService loginService = applicationContext.getBean(LoginService.class);
            if (userDTO != null) {
                List<String> roleList = new ArrayList<>();
                userDTO =  loginService.getUserDetail(userDTO.getId());
                for(UserRoleDTO roleCode : userDTO.getUserRole()) {
                    if (roleCode.isActive()) {
                        roleList.add(String.valueOf(roleCode.getRoleCode()));
                    }
                }

                securityContext = sessionContext.new SecurityContext();
                securityContext.setUserId(userDTO.getId());
                securityContext.setRoles(roleList);
                securityContext.setSecurityAuthenticationMap(new HashMap<>());

                updateSessionContext(userDTO, roleList);

                getCenterMachineStatus(userDTO);

                LOGGER.info(LoggerConstants.LOG_REG_LOGIN, APPLICATION_NAME, APPLICATION_ID,
                        "Creating Session Context is completed");
            } else {
                securityContext = null;
            }
        } else {
            securityContext = null;
        }
    }

    */
/**
     * Setting values for Session context and User context and Initial info for
     * Login
     *//*

    private static void updateSessionContext(UserDTO userDTO, List<String> roleList) {

        long refreshedLoginTime = Long.parseLong(String.valueOf(io.mosip.registration.context.ApplicationContext.map()
                .get(RegistrationConstants.REFRESHED_LOGIN_TIME)));
        long idealTime = Long.parseLong(String
                .valueOf(io.mosip.registration.context.ApplicationContext.map()
                        .getOrDefault(RegistrationConstants.IDLE_TIME, "900")));

        sessionContext.setLoginTime(new Date());
        sessionContext.setRefreshedLoginTime(refreshedLoginTime);
        sessionContext.setIdealTime(idealTime);

        userContext.setUserId(userDTO.getId());
        userContext.setName(userDTO.getName());
        userContext.setRoles(roleList);

        LoginService loginService = applicationContext.getBean(LoginService.class);

        userContext.setRegistrationCenterDetailDTO(
                loginService.getRegistrationCenterDetails(userDTO.getRegCenterId(),
                        io.mosip.registration.context.ApplicationContext.applicationLanguage()));
        userContext.setAuthorizationDTO(loginService.getScreenAuthorizationDetails(roleList));
        userContext.setUserMap(new HashMap<String, Object>());
    }

    */
/**
     * validating User machine mapping status
     *//*

    private static void getCenterMachineStatus(UserDTO userDTO) {
        List<String> machineList = new ArrayList<>();
        List<String> centerList = new ArrayList<>();

        userDTO.getUserMachineMapping().forEach(machineMapping -> {
            if (machineMapping.isActive()) {
                machineList.add(machineMapping.getMachineMaster().getName().toLowerCase());
                centerList.add(machineMapping.getCentreID());
            }
        });

        */
/* user onboard skipped for the user with role Default *//*

        if ((machineList.contains(RegistrationSystemPropertiesChecker.getMachineId().toLowerCase())
                && centerList.contains(userDTO.getRegCenterId()))
                || Role.isDefaultUser(SessionContext.userContext().getRoles())) {
            sessionContext.mapObject.put(RegistrationConstants.ONBOARD_USER, false);
            sessionContext.mapObject.put(RegistrationConstants.ONBOARD_USER_UPDATE, false);
            sessionContext.mapObject.put(RegistrationConstants.ISPAGE_NAVIGATION_ALERT_REQ,
                    RegistrationConstants.DISABLE);
        } else {
            sessionContext.mapObject.put(RegistrationConstants.ONBOARD_USER, true);
            sessionContext.mapObject.put(RegistrationConstants.ONBOARD_USER_UPDATE, false);
            sessionContext.mapObject.put(RegistrationConstants.ISPAGE_NAVIGATION_ALERT_REQ,
                    RegistrationConstants.ENABLE);
        }
    }

    */
/**
     * Reading map from sessionContext
     *
     * @return map
     *//*

    public static Map<String, Object> map() {
        if(sessionContext==null) {
            return null;
        }
        return sessionContext.getMapObject();
    }

    */
/**
     * Return the Type casted object based on the input
     *
     * @param map
     *            - map that contains key and values to be typecasted
     * @param key
     *            - input to typecast
     * @param returnType
     *            - the type to which the object has to be typecasted
     * @param <T>
     *            - Generic type
     * @return T - typecasted object
     *//*

    public static <T> T getValue(Map<String, Object> map, String key, Class<T> returnType) {
        return returnType.cast(map.get(key));
    }

    */
/**
     * Reading userContext from sessionContext
     *
     * @return userContext
     *//*

    public static UserContext userContext() {
        return sessionContext.getUserContext();
    }

    */
/**
     * Reading SecurityContext from sessioncontext
     *
     * @return securityContext
     *//*

    public static SecurityContext securityContext() {
        return sessionContext.getSecurityContext();
    }

    */
/**
     * Reading userMap from sessionContext
     *
     * @return userMap
     *//*

    public static Map<String, Object> userMap() {
        return sessionContext.getUserContext().getUserMap();
    }

    */
/**
     * Reading refreshedLoginTime from sessionContext
     *
     * @return refreshedLoginTime
     *//*

    public static long refreshedLoginTime() {
        return sessionContext.getRefreshedLoginTime();
    }

    */
/**
     * Reading loginTime from sessioncontext
     *
     * @return loginTime
     *//*

    public static Date loginTime() {
        return sessionContext.getLoginTime();
    }

    */
/**
     * Reading timeoutInterval from sessionContext
     *
     * @return timeoutInterval
     *//*

    public static long timeoutInterval() {
        return sessionContext.getTimeoutInterval();
    }

    */
/**
     * Reading idealTime from sessionContext
     *
     * @return idealTime
     *//*

    public static long idealTime() {
        return sessionContext.getIdealTime();
    }

    */
/**
     * Setting authTokenDTO to sessionContext
     *
     * @param authTokenDTO
     *            DTO for auth token
     *//*

    public static void setAuthTokenDTO(AuthTokenDTO authTokenDTO) {
        sessionContext.authTokenDTO = authTokenDTO;
    }

    */
/**
     * Reading authTokenDTO from sessionContext
     *
     * @return authTokenDTO
     *//*

    public static AuthTokenDTO authTokenDTO() {
        return sessionContext.authTokenDTO;
    }

    */
/**
     * Reading userId from sessionContext
     *
     * @return userId
     *//*

    public static String userId() {
        if (sessionContext == null || sessionContext.getUserContext() == null
                || sessionContext.getUserContext().getUserId() == null) {
            return RegistrationConstants.AUDIT_DEFAULT_USER;
        } else {
            return sessionContext.getUserContext().getUserId();
        }
    }

    */
/**
     * Reading userName from sessionContext
     *
     * @return userName
     *//*

    public static String userName() {
        if (sessionContext == null || sessionContext.getUserContext() == null
                || sessionContext.getUserContext().getName() == null) {
            return RegistrationConstants.AUDIT_DEFAULT_USER;
        } else {
            return sessionContext.getUserContext().getName();
        }
    }

    */
/**
     * Reading isSessionContextAvailable from sessionContext
     *
     * @return boolean
     *//*

    public static boolean isSessionContextAvailable() {
        return sessionContext != null;
    }

    */
/**
     * Getter for id
     *
     * @return id
     *//*

    public UUID getId() {
        return id;
    }

    */
/**
     * Setter for id
     *
     * @param id
     *            session id
     *//*

    public void setId(UUID id) {
        this.id = id;
    }

    */
/**
     * Getter for userContext
     *
     * @return userContext
     *//*

    public UserContext getUserContext() {
        return userContext;
    }

    */
/**
     * Getter for securityContext
     *
     * @return securityContext
     *//*

    public SecurityContext getSecurityContext() {
        return securityContext;
    }

    */
/**
     * Getter for loginTime
     *
     * @return loginTime
     *//*

    public Date getLoginTime() {
        return loginTime;
    }

    */
/**
     * Setter for loginTime
     *
     * @param loginTime
     *            time of login
     *//*

    public void setLoginTime(Date loginTime) {
        this.loginTime = loginTime;
    }

    */
/**
     * Getter for refreshedLoginTime
     *
     * @return refreshedLoginTime
     *//*

    public long getRefreshedLoginTime() {
        return refreshedLoginTime;
    }

    */
/**
     * Setter for refreshedLoginTime
     *
     * @param refreshedLoginTime
     *            time of login when to refresh
     *//*

    public void setRefreshedLoginTime(long refreshedLoginTime) {
        this.refreshedLoginTime = refreshedLoginTime;
    }

    */
/**
     * Getter for timeoutInterval
     *
     * @return timeoutInterval
     *//*

    public long getTimeoutInterval() {
        return timeoutInterval;
    }

    */
/**
     * Setter for timeoutInterval
     *
     * @param timeoutInterval
     *            time for logout
     *//*

    public void setTimeoutInterval(long timeoutInterval) {
        this.timeoutInterval = timeoutInterval;
    }

    */
/**
     * Getter for idealTime
     *
     * @return idealTime
     *//*

    public long getIdealTime() {
        return idealTime;
    }

    */
/**
     * Setter for idealTime
     *
     * @param idealTime
     *            ideal time
     *//*

    public void setIdealTime(long idealTime) {
        this.idealTime = idealTime;
    }

    */
/**
     * Getter for mapObject
     *
     * @return mapObject
     *//*

    public Map<String, Object> getMapObject() {
        return mapObject;
    }

    */
/**
     * Setter for mapObject
     *
     * @param mapObject
     *            session map
     *//*

    public void setMapObject(Map<String, Object> mapObject) {
        this.mapObject = mapObject;
    }

    */
/**
     * destroys the session
     *//*

    public static void destroySession() {
        sessionContext = null;
        authModes.clear();
        validAuthModes.clear();

        LOGGER.info(LoggerConstants.LOG_REG_LOGIN, APPLICATION_NAME, APPLICATION_ID, "Leaving Session Context");
    }

    */
/**
     * class for User context
     *
     *//*

    public class UserContext {
        private String userId;
        private String name;
        private RegistrationCenterDetailDTO registrationCenterDetailDTO;
        private List<String> roles;
        private AuthorizationDTO authorizationDTO;
        private Map<String, Object> userMap;

        */
/**
         * Constructor for User context
         *//*

        private UserContext() {

        }

        */
/**
         * Getter for userId
         *
         * @return userId
         *//*

        public String getUserId() {
            if (userId == null) {
                return RegistrationConstants.JOB_TRIGGER_POINT_SYSTEM;
            }
            return userId;
        }

        */
/**
         * Setter for userId
         *
         * @param userId
         *            id of the user
         *//*

        public void setUserId(String userId) {
            this.userId = userId;
        }

        */
/**
         * Getter for name
         *
         * @return name
         *//*

        public String getName() {
            return name;
        }

        */
/**
         * Setter for name
         *
         * @param name
         *            user name
         *//*

        public void setName(String name) {
            this.name = name;
        }

        */
/**
         * Getter for registrationCenterDetailDTO
         *
         * @return registrationCenterDetailDTO
         *//*

        public RegistrationCenterDetailDTO getRegistrationCenterDetailDTO() {
            return registrationCenterDetailDTO;
        }

        */
/**
         * Setter for registrationCenterDetailDTO
         *
         * @param registrationCenterDetailDTO
         *            registration center details
         *//*

        public void setRegistrationCenterDetailDTO(RegistrationCenterDetailDTO registrationCenterDetailDTO) {
            this.registrationCenterDetailDTO = registrationCenterDetailDTO;
        }

        */
/**
         * Getter for roles
         *
         * @return list of roles
         *//*

        public List<String> getRoles() {
            return roles;
        }

        */
/**
         * Setter for roles
         *
         * @param roles
         *            user roles
         *//*

        public void setRoles(List<String> roles) {
            this.roles = roles;
        }

        */
/**
         * Getter for authorizationDTO
         *
         * @return authorizationDTO
         *//*

        public AuthorizationDTO getAuthorizationDTO() {
            return authorizationDTO;
        }

        */
/**
         * Setter for authorizationDTO
         *
         * @param authorizationDTO
         *            DTO for authorization details
         *//*

        public void setAuthorizationDTO(AuthorizationDTO authorizationDTO) {
            this.authorizationDTO = authorizationDTO;
        }

        */
/**
         * Getter for userMap
         *
         * @return userMap
         *//*

        public Map<String, Object> getUserMap() {
            return userMap;
        }

        */
/**
         * Setter for userMap
         *
         * @param userMap
         *            user map
         *//*

        public void setUserMap(Map<String, Object> userMap) {
            this.userMap = userMap;
        }

    }

    */
/**
     * class for User context
     *
     *//*

    public class SecurityContext {
        private String userId;
        private List<String> roles;
        private Map<String, Object> securityAuthenticationMap;

        */
/**
         * Constructor for User context
         *//*

        private SecurityContext() {

        }

        */
/**
         * Getter for userId
         *
         * @return userId
         *//*

        public String getUserId() {
            if (userId == null) {
                return RegistrationConstants.JOB_TRIGGER_POINT_SYSTEM;
            }
            return userId;
        }

        */
/**
         * Setter for userId
         *
         * @param userId
         *            id of the user
         *//*

        public void setUserId(String userId) {
            this.userId = userId;
        }

        */
/**
         * Getter for roles
         *
         * @return list of roles
         *//*

        public List<String> getRoles() {
            return roles;
        }

        */
/**
         * Setter for roles
         *
         * @param roles
         *            user roles
         *//*

        public void setRoles(List<String> roles) {
            this.roles = roles;
        }

        */
/**
         * Getter for userMap
         *
         * @return userMap
         *//*

        public Map<String, Object> getSecurityAuthenticationMap() {
            return securityAuthenticationMap;
        }

        */
/**
         * Setter for securityAuthenticationMap
         *
         * @param securityAuthenticationMap
         *            - Security Authentication Map
         *//*

        public void setSecurityAuthenticationMap(Map<String, Object> securityAuthenticationMap) {
            this.securityAuthenticationMap = securityAuthenticationMap;
        }

    }

    public static boolean isAutoLogoutAllowed() {
        return SessionContext.isAutoLogout;
    }

    public static void setAutoLogout(boolean isAutoLogout) {
        SessionContext.isAutoLogout = isAutoLogout;
    }

}*/
