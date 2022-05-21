//package io.mosip.registration.clientmanager.util.restclient;
//
//
//import com.auth0.jwt.JWT;
//import com.auth0.jwt.impl.JWTParser;
//import com.auth0.jwt.interfaces.Claim;
//import com.auth0.jwt.interfaces.DecodedJWT;
//import io.micrometer.core.annotation.Counted;
//import io.micrometer.core.annotation.Timed;
//import io.mosip.kernel.clientcrypto.service.impl.ClientCryptoFacade;
//import io.mosip.kernel.clientcrypto.util.ClientCryptoUtils;
//import io.mosip.kernel.core.logger.spi.Logger;
//import io.mosip.kernel.core.util.CryptoUtil;
//import io.mosip.kernel.core.util.DateUtils;
//import io.mosip.registration.config.AppConfig;
//import io.mosip.registration.constants.LoginMode;
//import io.mosip.registration.constants.RegistrationConstants;
//import io.mosip.registration.context.ApplicationContext;
//import io.mosip.registration.context.SessionContext;
//import io.mosip.registration.dao.UserDetailDAO;
//import io.mosip.registration.dto.AuthTokenDTO;
//import io.mosip.registration.dto.LoginUserDTO;
//import io.mosip.registration.entity.UserToken;
//import io.mosip.registration.exception.ConnectionException;
//import io.mosip.registration.exception.RegBaseCheckedException;
//import io.mosip.registration.exception.RegistrationExceptionConstants;
//import io.mosip.registration.repositories.UserTokenRepository;
//import lombok.SneakyThrows;
//import org.json.JSONArray;
//import org.json.JSONObject;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.core.env.Environment;
//import org.springframework.dao.DataIntegrityViolationException;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.HttpMethod;
//import org.springframework.http.MediaType;
//import org.springframework.http.client.SimpleClientHttpRequestFactory;
//import org.springframework.retry.RetryCallback;
//import org.springframework.retry.RetryContext;
//import org.springframework.retry.backoff.FixedBackOffPolicy;
//import org.springframework.retry.policy.SimpleRetryPolicy;
//import org.springframework.retry.support.RetryTemplate;
//import org.springframework.retry.support.RetryTemplateBuilder;
//import org.springframework.stereotype.Service;
//import org.springframework.web.client.RestClientException;
//import org.springframework.web.util.UriComponentsBuilder;
//
//import javax.annotation.PostConstruct;
//import javax.validation.constraints.NotNull;
//import java.net.URI;
//import java.time.LocalDateTime;
//import java.time.ZoneOffset;
//import java.util.*;
//
//
///**
// * @author Anusha Sunkada
// * @since 1.1.3
// */
//@Service
//public class AuthTokenUtilService {
//
//    private static final Logger LOGGER = AppConfig.getLogger(AuthTokenUtilService.class);
//    private static final String REALM_ACCESS = "realm_access";
//    private static final String ROLES = "roles";
//    private static final String USERNAME = "name";
//
//    @Autowired
//    private ClientCryptoFacade clientCryptoFacade;
//
//    @Autowired
//    private RestClientUtil restClientUtil;
//
//    @Autowired
//    private Environment environment;
//
//    @Autowired
//    private UserDetailDAO userDetailDAO;
//
//    @Autowired
//    private UserTokenRepository userTokenRepository;
//
//    @Autowired
//    private ServiceDelegateUtil serviceDelegateUtil;
//
//    private RetryTemplate retryTemplate;
//
//    @PostConstruct
//    public void init() {
//        FixedBackOffPolicy backOffPolicy = new FixedBackOffPolicy();
//        backOffPolicy.setBackOffPeriod((Long) ApplicationContext.map().getOrDefault("mosip.registration.retry.delay.auth", 1000l));
//
//        SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy();
//        retryPolicy.setMaxAttempts((Integer) ApplicationContext.map().getOrDefault("mosip.registration.retry.maxattempts.auth", 2));
//
//        retryTemplate = new RetryTemplateBuilder()
//                .retryOn(ConnectionException.class)
//                .customPolicy(retryPolicy)
//                .customBackoff(backOffPolicy)
//                .build();
//    }
//
//    public boolean hasAnyValidToken() {
//        UserToken userToken = userTokenRepository.findTopByTokenExpiryGreaterThanAndUserDetailIsActiveTrueOrderByTokenExpiryDesc(System.currentTimeMillis()/1000);
//        if(userToken != null) {
//            return true;
//        }
//        userToken = userTokenRepository.findTopByRtokenExpiryGreaterThanAndUserDetailIsActiveTrueOrderByRtokenExpiryDesc(System.currentTimeMillis()/1000);
//        if(userToken != null) {
//            return true;
//        }
//
//        LOGGER.error("No valid auth token found! Needs new token to be fetched");
//        return false;
//    }
//
//    public AuthTokenDTO fetchAuthToken(String triggerPoint) throws RegBaseCheckedException {
//        LOGGER.info("fetchAuthToken invoked for triggerPoint >>>>> {}", triggerPoint);
//
//        if(SessionContext.isSessionContextAvailable()) {
//            UserToken userToken = userTokenRepository.findByUsrIdAndUserDetailIsActiveTrue(SessionContext.userId());
//            if(userToken != null && userToken.getTokenExpiry() > (System.currentTimeMillis()/1000)) {
//                AuthTokenDTO authTokenDTO = new AuthTokenDTO();
//                authTokenDTO.setCookie(String.format("Authorization=%s", userToken.getToken()));
//                return authTokenDTO;
//            }
//
//            if(userToken != null && userToken.getRtokenExpiry() > (System.currentTimeMillis()/1000)) {
//                AuthTokenDTO authTokenDTO = new AuthTokenDTO();
//                authTokenDTO.setCookie(String.format("Authorization=%s",
//                        refreshAuthToken(userToken.getUsrId(), userToken.getRefreshToken())));
//                return authTokenDTO;
//            }
//        }
//        else {
//            UserToken userToken = userTokenRepository.findTopByTokenExpiryGreaterThanAndUserDetailIsActiveTrueOrderByTokenExpiryDesc(System.currentTimeMillis()/1000);
//            if(userToken != null) {
//                AuthTokenDTO authTokenDTO = new AuthTokenDTO();
//                authTokenDTO.setCookie(String.format("Authorization=%s", userToken.getToken()));
//                return authTokenDTO;
//            }
//
//            userToken = userTokenRepository.findTopByRtokenExpiryGreaterThanAndUserDetailIsActiveTrueOrderByRtokenExpiryDesc(System.currentTimeMillis()/1000);
//            if(userToken != null) {
//                AuthTokenDTO authTokenDTO = new AuthTokenDTO();
//                authTokenDTO.setCookie(String.format("Authorization=%s",
//                        refreshAuthToken(userToken.getUsrId(), userToken.getRefreshToken())));
//                return authTokenDTO;
//            }
//        }
//
//        LoginUserDTO loginUserDTO = (LoginUserDTO) ApplicationContext.map().get(RegistrationConstants.USER_DTO);
//        if(loginUserDTO != null && loginUserDTO.getPassword() != null) {
//            return getAuthTokenAndRefreshToken(LoginMode.PASSWORD);
//        }
//
//        throw new RegBaseCheckedException(
//                RegistrationExceptionConstants.AUTH_TOKEN_COOKIE_NOT_FOUND.getErrorCode(),
//                RegistrationExceptionConstants.AUTH_TOKEN_COOKIE_NOT_FOUND.getErrorMessage());
//    }
//
//
//
//    private String refreshAuthToken(String userId, String refreshToken) throws RegBaseCheckedException {
//        LOGGER.debug("refreshAuthToken invoked for userId >>>>> {}", userId);
//        try {
//            String timestamp = DateUtils.formatToISOString(LocalDateTime.now(ZoneOffset.UTC));
//            String header = String.format("{\"kid\" : \"%s\"}", CryptoUtil.computeFingerPrint(clientCryptoFacade.getClientSecurity().getSigningPublicPart(), null));
//            String payload = String.format("{\"refreshToken\" : \"%s\", \"authType\":\"%s\", \"timestamp\" : \"%s\"}",
//                    refreshToken, "REFRESH", timestamp);
//            byte[] signature = clientCryptoFacade.getClientSecurity().signData(payload.getBytes());
//            String data = String.format("%s.%s.%s", CryptoUtil.encodeToURLSafeBase64(header.getBytes()),
//                    CryptoUtil.encodeToURLSafeBase64(payload.getBytes()), CryptoUtil.encodeToURLSafeBase64(signature));
//
//            RequestHTTPDTO requestHTTPDTO = getRequestHTTPDTO(data, timestamp);
//            setTimeout(requestHTTPDTO);
//            setURI(requestHTTPDTO, new HashMap<>(), getEnvironmentProperty("auth_by_password", RegistrationConstants.SERVICE_URL));
//            Map<String, Object> responseMap = restClientUtil.invokeForToken(requestHTTPDTO);
//
//            long currentTimeInSeconds = System.currentTimeMillis()/1000;
//            JSONObject jsonObject = getAuthTokenResponse(responseMap);
//            userDetailDAO.updateAuthTokens(userId, jsonObject.getString("token"),
//                    jsonObject.getString("refreshToken"),
//                    currentTimeInSeconds + jsonObject.getLong("expiryTime"),
//                    currentTimeInSeconds + jsonObject.getLong("refreshExpiryTime"));
//            return jsonObject.getString("token");
//        } catch (DataIntegrityViolationException e) {
//            LOGGER.error(e.getMessage(), e);
//            throw new RegBaseCheckedException(RegistrationExceptionConstants.AUTH_TOKEN_SAVE_FAILED.getErrorCode(),
//                    RegistrationExceptionConstants.AUTH_TOKEN_SAVE_FAILED.getErrorMessage());
//        } catch (Exception exception) {
//            LOGGER.error(exception.getMessage(), exception);
//            throw new RegBaseCheckedException(RegistrationExceptionConstants.AUTH_TOKEN_COOKIE_NOT_FOUND.getErrorCode(),
//                    exception.getMessage(), exception);
//        }
//    }
//
//    public AuthTokenDTO getAuthTokenAndRefreshToken(LoginMode loginMode) throws RegBaseCheckedException {
//        LoginUserDTO loginUserDTO = (LoginUserDTO) ApplicationContext.map().get(RegistrationConstants.USER_DTO);
//        return getAuthTokenAndRefreshToken(loginMode, loginUserDTO);
//    }
//
//
//    public AuthTokenDTO getAuthTokenAndRefreshToken(LoginMode loginMode, LoginUserDTO loginUserDTO) throws RegBaseCheckedException {
//        LOGGER.info("Fetching Auth Token and refresh token based on Login Mode >>> {}",loginMode);
//        try {
//            String timestamp = DateUtils.formatToISOString(LocalDateTime.now(ZoneOffset.UTC));
//            String header = String.format("{\"kid\" : \"%s\"}", CryptoUtil.computeFingerPrint(clientCryptoFacade.getClientSecurity().getSigningPublicPart(), null));
//
//            String payload = "";
//            switch (loginMode) {
//                case PASSWORD:
//                    payload = String.format("{\"userId\" : \"%s\", \"password\": \"%s\", \"authType\":\"%s\", \"timestamp\" : \"%s\"}",
//                        loginUserDTO.getUserId(), loginUserDTO.getPassword(), "NEW", timestamp);
//                    break;
//                case OTP:
//                    payload = String.format("{\"userId\" : \"%s\", \"otp\": \"%s\", \"authType\":\"%s\", \"timestamp\" : \"%s\"}",
//                            loginUserDTO.getUserId(), loginUserDTO.getOtp(), "OTP", timestamp);
//                    break;
//            }
//
//            byte[] signature = clientCryptoFacade.getClientSecurity().signData(payload.getBytes());
//            String data = String.format("%s.%s.%s", CryptoUtil.encodeToURLSafeBase64(header.getBytes()),
//                    CryptoUtil.encodeToURLSafeBase64(payload.getBytes()), CryptoUtil.encodeToURLSafeBase64(signature));
//
//            RequestHTTPDTO requestHTTPDTO = getRequestHTTPDTO(data, timestamp);
//            setTimeout(requestHTTPDTO);
//            setURI(requestHTTPDTO, new HashMap<>(), getEnvironmentProperty("auth_by_password", RegistrationConstants.SERVICE_URL));
//            Map<String, Object> responseMap = restClientUtil.invokeForToken(requestHTTPDTO);
//
//            JSONObject jsonObject = getAuthTokenResponse(responseMap);
//            AuthTokenDTO authTokenDTO = new AuthTokenDTO();
//            authTokenDTO.setCookie(String.format("Authorization=%s", jsonObject.getString("token")));
//            authTokenDTO.setLoginMode(loginMode.getCode());
//
//            updateUserDetails(loginUserDTO.getUserId(), loginUserDTO.getPassword(), jsonObject.getString("token"),
//                    jsonObject.getString("refreshToken"));
//
//            ApplicationContext.setAuthTokenDTO(authTokenDTO);
//            if(SessionContext.isSessionContextAvailable())
//                SessionContext.setAuthTokenDTO(authTokenDTO);
//
//            return authTokenDTO;
//
//        } catch (DataIntegrityViolationException e) {
//            LOGGER.error(e.getMessage(), e);
//            throw new RegBaseCheckedException(RegistrationExceptionConstants.AUTH_TOKEN_SAVE_FAILED.getErrorCode(),
//                    RegistrationExceptionConstants.AUTH_TOKEN_SAVE_FAILED.getErrorMessage());
//        } catch (Exception exception) {
//            LOGGER.error(exception.getMessage(), exception);
//            throw new RegBaseCheckedException(RegistrationExceptionConstants.AUTH_TOKEN_COOKIE_NOT_FOUND.getErrorCode(),
//                    exception.getMessage(), exception);
//        }
//    }
//
//    public String sendOtpWithRetryWrapper(String userId) throws ConnectionException {
//        RetryCallback<String, ConnectionException> retryCallback = new RetryCallback<String, ConnectionException>() {
//            @SneakyThrows
//            @Override
//            public String doWithRetry(RetryContext retryContext) throws ConnectionException {
//                LOGGER.info("Currently in Retry wrapper. Current counter : {}", retryContext.getRetryCount());
//                return sendOTP(userId);
//            }
//        };
//        return retryTemplate.execute(retryCallback);
//    }
//
//    public String sendOTP(String userId) throws ConnectionException {
//        LOGGER.info("Request to send OTP");
//        try {
//            String timestamp = DateUtils.formatToISOString(LocalDateTime.now(ZoneOffset.UTC));
//            String header = String.format("{\"kid\" : \"%s\"}", CryptoUtil.computeFingerPrint(clientCryptoFacade.getClientSecurity().getSigningPublicPart(), null));
//
//            JSONObject jsonObject = new JSONObject();
//            jsonObject.put("userId", userId);
//            jsonObject.put("appId", ApplicationContext.map().get(RegistrationConstants.REGISTRATION_CLIENT));
//            jsonObject.put("useridtype", RegistrationConstants.USER_ID_CODE);
//            jsonObject.put("context", RegistrationConstants.REGISTRATION_CONTEXT);
//            jsonObject.put("otpChannel", new JSONArray(ApplicationContext.map().get(RegistrationConstants.OTP_CHANNELS).toString().toLowerCase().split(",")));
//            jsonObject.put("timestamp", timestamp);
//            String payload = jsonObject.toString();
//
//            byte[] signature = clientCryptoFacade.getClientSecurity().signData(payload.getBytes());
//            String data = String.format("%s.%s.%s", CryptoUtil.encodeToURLSafeBase64(header.getBytes()),
//                    CryptoUtil.encodeToURLSafeBase64(payload.getBytes()), CryptoUtil.encodeToURLSafeBase64(signature));
//
//            RequestHTTPDTO requestHTTPDTO = getRequestHTTPDTO(data, timestamp);
//            setTimeout(requestHTTPDTO);
//            setURI(requestHTTPDTO, new HashMap<>(), getEnvironmentProperty("auth_by_otp", RegistrationConstants.SERVICE_URL));
//            Map<String, Object> responseMap = restClientUtil.invokeForToken(requestHTTPDTO);
//            responseMap = (Map<String, Object>) responseMap.get(RegistrationConstants.REST_RESPONSE_BODY);
//
//            if (responseMap.get(RegistrationConstants.RESPONSE) != null) {
//                LinkedHashMap<String, String> otpMessage = (LinkedHashMap<String, String>) responseMap
//                        .get("response");
//                return RegistrationConstants.OTP_GENERATION_SUCCESS_MESSAGE + otpMessage.get("message");
//            }
//
//            if (responseMap.get(RegistrationConstants.ERRORS) != null) {
//                String errMsg = ((List<LinkedHashMap<String, String>>) responseMap
//                        .get(RegistrationConstants.ERRORS)).get(0).get(RegistrationConstants.ERROR_MSG);
//                LOGGER.error(errMsg);
//            }
//        } catch (RestClientException ex) {
//            throw new ConnectionException("REG_SEND_OTP", ex.getMessage(), ex);
//        }
//        return null;
//    }
//
//    @Counted(recordFailuresOnly = true)
//    @Timed
//    private JSONObject getAuthTokenResponse(Map<String, Object> responseMap) throws RegBaseCheckedException {
//        if(responseMap.get(RegistrationConstants.REST_RESPONSE_BODY) != null) {
//            Map<String, Object> respBody = (Map<String, Object>) responseMap.get(RegistrationConstants.REST_RESPONSE_BODY);
//            if (respBody.get("response") != null) {
//                byte[] decryptedData = clientCryptoFacade.decrypt(CryptoUtil.decodeURLSafeBase64((String)respBody.get("response")));
//                return new JSONObject(new String(decryptedData));
//            }
//
//            if(respBody.get("errors") != null) {
//                List<LinkedHashMap<String, Object>> errorMap = (List<LinkedHashMap<String, Object>>) respBody
//                        .get(RegistrationConstants.ERRORS);
//                if(!errorMap.isEmpty()) {
//                    throw new RegBaseCheckedException((String)errorMap.get(0).get("errorCode"),
//                            (String)errorMap.get(0).get("message"));
//                }
//            }
//        }
//        throw new RegBaseCheckedException(
//                RegistrationExceptionConstants.AUTH_TOKEN_COOKIE_NOT_FOUND.getErrorCode(),
//                RegistrationExceptionConstants.AUTH_TOKEN_COOKIE_NOT_FOUND.getErrorMessage());
//    }
//
//    private RequestHTTPDTO getRequestHTTPDTO(String data, String timestamp) {
//        Map<String, String> requestBody = new HashMap<>();
//        requestBody.put("id", "");
//        requestBody.put("version", "");
//        requestBody.put("request", data);
//        requestBody.put("requesttime", timestamp);
//
//        RequestHTTPDTO requestHTTPDTO = new RequestHTTPDTO();
//        requestHTTPDTO.setClazz(Object.class);
//        requestHTTPDTO.setRequestBody(requestBody);
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_JSON);
//        requestHTTPDTO.setHttpHeaders(headers);
//        requestHTTPDTO.setIsSignRequired(false);
//        requestHTTPDTO.setRequestSignRequired(false);
//        requestHTTPDTO.setHttpMethod(HttpMethod.POST);
//        return requestHTTPDTO;
//    }
//
//    private String getEnvironmentProperty(String serviceName, String serviceComponent) {
//        return environment.getProperty(serviceName.concat(RegistrationConstants.DOT).concat(serviceComponent));
//    }
//
//    private void setURI(RequestHTTPDTO requestHTTPDTO, Map<String, String> requestParams, String url) {
//        url = serviceDelegateUtil.prepareURLByHostName(url);
//        LOGGER.info("Preparing URI for web-service >>>>>  {} ", url);
//        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromUriString(url);
//        if (requestParams != null) {
//            Set<String> set = requestParams.keySet();
//            for (String queryParamName : set) {
//                uriComponentsBuilder.queryParam(queryParamName, requestParams.get(queryParamName));
//            }
//        }
//        URI uri = uriComponentsBuilder.build().toUri();
//        requestHTTPDTO.setUri(uri);
//        LOGGER.info("Completed preparing URI for web-service >>>>>>> {} ", uri);
//    }
//
//    private void setTimeout(RequestHTTPDTO requestHTTPDTO) {
//        // Timeout in milli second
//        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
//        requestFactory.setReadTimeout(
//                Integer.parseInt((String) ApplicationContext.map().get(RegistrationConstants.HTTP_API_READ_TIMEOUT)));
//        requestFactory.setConnectTimeout(
//                Integer.parseInt((String) ApplicationContext.map().get(RegistrationConstants.HTTP_API_WRITE_TIMEOUT)));
//        requestHTTPDTO.setSimpleClientHttpRequestFactory(requestFactory);
//    }
//
//
//    private void updateUserDetails(@NotNull String userId, String password, String token, String refreshToken) throws Exception {
//        if(password != null)
//            userDetailDAO.updateUserPwd(userId, password);
//
//        Date now = Calendar.getInstance().getTime();
//        DecodedJWT decodedJWT = JWT.decode(token);
//        if(decodedJWT.getExpiresAt() == null || decodedJWT.getExpiresAt().before(now)) {
//            throw new RegBaseCheckedException(RegistrationExceptionConstants.AUTH_TOKEN_COOKIE_NOT_FOUND.getErrorCode(),
//                   "Auth token received is expired : " + decodedJWT.getExpiresAt());
//        }
//
//        userDetailDAO.updateUserRolesAndUsername(userId, getUsername(decodedJWT), getRoles(decodedJWT));
//
//        DecodedJWT decodedRefreshJWT = JWT.decode(refreshToken);
//        userDetailDAO.updateAuthTokens(userId, token, refreshToken, decodedJWT.getExpiresAt().getTime(),
//                decodedRefreshJWT.getExpiresAt().getTime());
//    }
//
//    private List<String> getRoles(@NotNull DecodedJWT decodedJWT) {
//        Claim realmAccess = decodedJWT.getClaim(REALM_ACCESS);
//        if (!realmAccess.isNull()) {
//            return (List<String>) realmAccess.asMap().get("roles");
//        }
//        return new ArrayList<>();
//    }
//
//    private String getUsername(@NotNull DecodedJWT decodedJWT) {
//        return decodedJWT.getClaim(USERNAME) == null ? null : decodedJWT.getClaim(USERNAME).asString().trim();
//    }
//}
