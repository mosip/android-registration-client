//package io.mosip.registration.clientmanager.util.restclient;
//
//import java.net.HttpURLConnection;
//import java.net.URI;
//import java.nio.file.Path;
//import java.time.LocalDateTime;
//import java.util.*;
//
//import io.micrometer.core.annotation.Timed;
//import io.mosip.registration.exception.ConnectionException;
//import lombok.NonNull;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.core.env.Environment;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.HttpMethod;
//import org.springframework.stereotype.Component;
//import org.springframework.util.Assert;
//import org.springframework.web.client.RestClientException;
//import org.springframework.web.util.UriComponentsBuilder;
//
//import io.mosip.kernel.core.logger.spi.Logger;
//import io.mosip.kernel.core.util.DateUtils;
//import io.mosip.registration.config.AppConfig;
//import io.mosip.registration.constants.RegistrationConstants;
//import io.mosip.registration.context.ApplicationContext;
//import io.mosip.registration.exception.RegBaseCheckedException;
//import io.mosip.registration.exception.RegistrationExceptionConstants;
//
///**
// * This is a helper class .it invokes with different classes to get the response
// *
// * @author Yaswanth S
// * @since 1.0.0
// *
// */
//@Component("serviceDelegateUtil")
//public class ServiceDelegateUtil {
//
//	private static final Logger LOGGER = AppConfig.getLogger(ServiceDelegateUtil.class);
//	public static final String MOSIP_HOSTNAME_PLACEHOLDER = "${mosip.hostname}";
//
//	@Autowired
//	private RestClientUtil restClientUtil;
//
//	@Autowired
//	private Environment environment;
//
//
//	public String getHostName() {
//		return io.mosip.registration.context.ApplicationContext.getStringValueFromApplicationMap(RegistrationConstants.MOSIP_HOSTNAME);
//	}
//
//	public String prepareURLByHostName(String url) {
//		String mosipHostNameVal = getHostName();
//		Assert.notNull(mosipHostNameVal, "mosip.hostname is missing");
//		return (url != null) ? url.replace(MOSIP_HOSTNAME_PLACEHOLDER, mosipHostNameVal)
//				: url;
//	}
//
//	/**
//	 * This method checks the Internet connectivity across the application.
//	 *
//	 * <p>
//	 * Creates a {@link HttpURLConnection} and opens a communications link to the
//	 * resource referenced by this URL. If the connection is established
//	 * successfully, this method will return true which indicates Internet Access
//	 * available, otherwise, it will return false, indicating Internet Access not
//	 * available.
//	 * </p>
//	 *
//	 * @return true, if is network available and false, if it is not available.
//	 */
//	@Timed
//	public boolean isNetworkAvailable() {
//		try {
//			String healthCheckUrl = ApplicationContext.getStringValueFromApplicationMap(RegistrationConstants.HEALTH_CHECK_URL);
//			Assert.notNull(healthCheckUrl, "Property mosip.reg.healthcheck.url missing");
//			String serviceUrl = prepareURLByHostName(healthCheckUrl);
//			LOGGER.info("Registration Network Checker had been called --> {}", serviceUrl);
//			return restClientUtil.isConnectedToSyncServer(serviceUrl);
//		} catch (Exception exception) {
//			LOGGER.error("No Internet Access" , exception);
//		}
//		return false;
//	}
//
//
//	/**
//	 * Prepare and trigger GET request.
//	 *
//	 * @param serviceName
//	 *            service to be invoked
//	 * @param requestParams
//	 *            parameters along with url
//	 * @param hasPathParams
//	 *            the has path params
//	 * @param triggerPoint
//	 *            system or user driven invocation
//	 * @return Object requiredType of object response Body
//	 * @throws RegBaseCheckedException
//	 *             generalised exception with errorCode and errorMessage
//	 * @throws ConnectionException
//	 *             when client error exception from server / server exception
//	 */
//	public Object get(String serviceName, Map<String, String> requestParams, boolean hasPathParams, String triggerPoint)
//			throws RegBaseCheckedException, ConnectionException {
//
//		LOGGER.debug("Get method has been called - {}", serviceName);
//
//		Map<String, Object> responseMap;
//		Object responseBody = null;
//
//		RequestHTTPDTO requestHTTPDTO = new RequestHTTPDTO();
//
//		try {
//			requestHTTPDTO = prepareGETRequest(requestHTTPDTO, serviceName, requestParams);
//			requestHTTPDTO.setAuthRequired(
//					Boolean.valueOf(getEnvironmentProperty(serviceName, RegistrationConstants.AUTH_REQUIRED)));
//			requestHTTPDTO.setAuthZHeader(getEnvironmentProperty(serviceName, RegistrationConstants.AUTH_HEADER));
//			requestHTTPDTO.setIsSignRequired(
//					Boolean.valueOf(getEnvironmentProperty(serviceName, RegistrationConstants.SIGN_REQUIRED)));
//			requestHTTPDTO.setTriggerPoint(triggerPoint);
//			requestHTTPDTO.setRequestSignRequired(
//					Boolean.valueOf(getEnvironmentProperty(serviceName, RegistrationConstants.REQUEST_SIGN_REQUIRED)));
//
//			// URI creation
//			String url = getEnvironmentProperty(serviceName, RegistrationConstants.SERVICE_URL);
//			url = prepareURLByHostName(url);
//			Map<String, String> queryParams = new HashMap<>();
//			for (String key : requestParams.keySet()) {
//				if (!url.contains("{" + key + "}")) {
//					queryParams.put(key, requestParams.get(key));
//				}
//			}
//
//			if (hasPathParams) {
//				requestHTTPDTO.setUri(UriComponentsBuilder.fromUriString(url).build(requestParams));
//				url = requestHTTPDTO.getUri().toString();
//			}
//
//			/** Set URI */
//			setURI(requestHTTPDTO, queryParams, url);
//
//			responseMap = restClientUtil.invokeURL(requestHTTPDTO);
//
//		}  catch (RestClientException e) {
//			LOGGER.error(e.getMessage(), e);
//			throw new ConnectionException(RegistrationExceptionConstants.ACCESS_ERROR.getErrorCode(),
//					RegistrationExceptionConstants.ACCESS_ERROR.getErrorMessage(), e);
//		}
//
//		if (isResponseValid(responseMap, RegistrationConstants.REST_RESPONSE_BODY)) {
//			responseBody = responseMap.get(RegistrationConstants.REST_RESPONSE_BODY);
//		}
//		LOGGER.debug("Get method has been ended - {}", serviceName);
//
//		return responseBody;
//	}
//
//
//	public void download(String url, Map<String, String> requestParams, String headers, boolean authRequired,
//					  String authHeader, String triggerPoint, @NonNull Path path, boolean isEncrypted)
//			throws RegBaseCheckedException, ConnectionException {
//		LOGGER.debug("Get method has been called - {}", url);
//		RequestHTTPDTO requestHTTPDTO = new RequestHTTPDTO();
//		try {
//			requestHTTPDTO.setHttpMethod(HttpMethod.GET);
//			requestHTTPDTO.setHttpHeaders(new HttpHeaders());
//			setHeaders(requestHTTPDTO.getHttpHeaders(), headers);// Headers
//			requestHTTPDTO.setAuthRequired(authRequired);
//			requestHTTPDTO.setAuthZHeader(authHeader);
//			requestHTTPDTO.setTriggerPoint(triggerPoint);
//			requestHTTPDTO.setIsSignRequired(true);
//			requestHTTPDTO.setFilePath(path);
//			requestHTTPDTO.setFileEncrypted(isEncrypted);
//
//			url = prepareURLByHostName(url);
//			Map<String, String> queryParams = new HashMap<>();
//			for (String key : requestParams.keySet()) {
//				if (!url.contains("{" + key + "}")) {
//					queryParams.put(key, requestParams.get(key));
//				}
//			}
//			setURI(requestHTTPDTO, queryParams, url);
//			restClientUtil.downloadFile(requestHTTPDTO);
//		}  catch (RestClientException e) {
//			LOGGER.error(e.getMessage(), e);
//			throw new ConnectionException(RegistrationExceptionConstants.ACCESS_ERROR.getErrorCode(),
//					RegistrationExceptionConstants.ACCESS_ERROR.getErrorMessage(), e);
//		} catch (Exception e) {
//			LOGGER.error(e.getMessage(), e);
//			throw new RegBaseCheckedException("REG-FILE-ERR", e.getMessage());
//		}
//	}
//
//
//	/**
//	 * prepare and trigger POST request.
//	 *
//	 * @param serviceName
//	 *            service to be invoked
//	 * @param object
//	 *            request type
//	 * @param triggerPoint
//	 *            system or user driven invocation
//	 * @return Object requiredType of object response Body
//	 * @throws RegBaseCheckedException
//	 *             generalised exception with errorCode and errorMessage
//	 * @throws ConnectionException
//	 *             when client error, server error, access error
//	 */
//	public Object post(String serviceName, Object object, String triggerPoint) throws RegBaseCheckedException, ConnectionException {
//		LOGGER.debug("Post method called - {} ", serviceName);
//
//		RequestHTTPDTO requestDto;
//		Object responseBody = null;
//		Map<String, Object> responseMap = null;
//
//		try {
//			requestDto = preparePOSTRequest(serviceName, object);
//			requestDto.setAuthRequired(
//					Boolean.valueOf(getEnvironmentProperty(serviceName, RegistrationConstants.AUTH_REQUIRED)));
//			requestDto.setAuthZHeader(getEnvironmentProperty(serviceName, RegistrationConstants.AUTH_HEADER));
//			requestDto.setIsSignRequired(
//					Boolean.valueOf(getEnvironmentProperty(serviceName, RegistrationConstants.SIGN_REQUIRED)));
//			requestDto.setTriggerPoint(triggerPoint);
//			requestDto.setRequestSignRequired(
//					Boolean.valueOf(getEnvironmentProperty(serviceName, RegistrationConstants.REQUEST_SIGN_REQUIRED)));
//			responseMap = restClientUtil.invokeURL(requestDto);
//		} catch (RestClientException e) {
//			LOGGER.error(e.getMessage(), e);
//			throw new ConnectionException(RegistrationExceptionConstants.ACCESS_ERROR.getErrorCode(),
//					RegistrationExceptionConstants.ACCESS_ERROR.getErrorMessage(), e);
//		}
//
//		if (isResponseValid(responseMap, RegistrationConstants.REST_RESPONSE_BODY)) {
//			responseBody = responseMap.get(RegistrationConstants.REST_RESPONSE_BODY);
//		}
//		LOGGER.debug("Post method ended - {} ", serviceName);
//
//		return responseBody;
//	}
//
//
//	/**
//	 * Prepare GET request.
//	 *
//	 * @param requestHTTPDTO
//	 *            the request HTTPDTO
//	 * @param serviceName
//	 *            service to be invoked
//	 * @param requestParams
//	 *            params need to add along with url
//	 * @return RequestHTTPDTO requestHTTPDTO with required data
//	 * @throws RegBaseCheckedException
//	 *             the reg base checked exception
//	 */
//	private RequestHTTPDTO prepareGETRequest(RequestHTTPDTO requestHTTPDTO, final String serviceName,
//			final Map<String, String> requestParams) throws RegBaseCheckedException {
//		LOGGER.debug("Prepare Get request method called");
//
//		// prepare httpDTO except rquest type and uri build
//		prepareRequest(requestHTTPDTO, serviceName, null);
//
//		// ResponseType
//		Class<?> responseClass = null;
//		try {
//			responseClass = Class.forName(getEnvironmentProperty(serviceName, RegistrationConstants.RESPONSE_TYPE));
//		} catch (ClassNotFoundException classNotFoundException) {
//			throw new RegBaseCheckedException(
//					RegistrationExceptionConstants.REG_CLASS_NOT_FOUND_ERROR_CODE.getErrorCode(),
//					RegistrationExceptionConstants.REG_CLASS_NOT_FOUND_ERROR_CODE.getErrorMessage(),
//					classNotFoundException);
//		}
//
//		requestHTTPDTO.setClazz(responseClass);
//
//		LOGGER.debug("Prepare Get request method ended");
//
//		return requestHTTPDTO;
//	}
//
//	/**
//	 * Prepare POST request.
//	 *
//	 * @param serviceName
//	 *            service to be invoked
//	 * @param object
//	 *            request type
//	 * @return RequestHTTPDTO requestHTTPDTO with required data
//	 */
//	private RequestHTTPDTO preparePOSTRequest(final String serviceName, final Object object) {
//		LOGGER.debug("Preparing post request for web-service");
//
//		// DTO need to to be prepared
//		RequestHTTPDTO requestHTTPDTO = new RequestHTTPDTO();
//
//		// prepare httpDTO except rquest type and uri build
//		prepareRequest(requestHTTPDTO, serviceName, object);
//
//		// URI creation
//		setURI(requestHTTPDTO, null, getEnvironmentProperty(serviceName, RegistrationConstants.SERVICE_URL));
//
//		// RequestType
//		requestHTTPDTO.setClazz(Object.class);
//
//		LOGGER.debug("Completed preparing post request for web-service");
//
//		return requestHTTPDTO;
//
//	}
//
//	/**
//	 * Sets the URI.
//	 *
//	 * @param requestHTTPDTO
//	 *            the request HTTPDTO
//	 * @param requestParams
//	 *            the request params
//	 * @param url
//	 *            the url
//	 */
//	private void setURI(RequestHTTPDTO requestHTTPDTO, Map<String, String> requestParams, String url) {
//		LOGGER.debug("Preparing URI for web-service");
//
//		// BuildURIComponent
//		UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromUriString(url);
//
//		if (requestParams != null) {
//			Set<String> set = requestParams.keySet();
//			for (String queryParamName : set) {
//				uriComponentsBuilder.queryParam(queryParamName, requestParams.get(queryParamName));
//
//			}
//		}
//		URI uri = uriComponentsBuilder.build().toUri();
//
//		requestHTTPDTO.setUri(uri);
//
//		LOGGER.debug("Completed preparing URI for web-service");
//	}
//
//	/**
//	 * Setup of headers
//	 *
//	 * @param httpHeaders
//	 *            http headers
//	 * @param headers
//	 *            headers
//	 */
//	private void setHeaders(HttpHeaders httpHeaders, String headers) {
//		LOGGER.debug("Preparing Header for web-service request");
//		if(headers == null || headers.trim().isEmpty())
//			return;
//
//		String[] header = headers.split(",");
//		String[] headerValues = null;
//		//if (header != null) {
//			for (String subheader : header) {
//				if(subheader.trim().isEmpty())
//					continue;
//
//				//if (subheader != null) {
//					headerValues = subheader.split(":");
//					if (headerValues[0].equalsIgnoreCase("timestamp")) {
//						headerValues[1] = DateUtils.formatToISOString(LocalDateTime.now());
//					} else if (headerValues[0].equalsIgnoreCase("Center-Machine-RefId")) {
//						headerValues[1] = String
//								.valueOf(ApplicationContext.map().get(RegistrationConstants.USER_CENTER_ID))
//								.concat(RegistrationConstants.UNDER_SCORE).concat(String
//										.valueOf(ApplicationContext.map().get(RegistrationConstants.USER_STATION_ID)));
//					} else if (headerValues[0].equalsIgnoreCase("authorization")) {
//						headerValues[1] = "auth";
//					} else if (headerValues[0].equalsIgnoreCase("signature")) {
//						headerValues[1] = "sign";
//					}
//					httpHeaders.add(headerValues[0], headerValues[1]);
//				//}
//			}
//			httpHeaders.add("Cache-Control", "no-cache,max-age=0");
//		//}
//
//		LOGGER.debug("Completed preparing Header for web-service request");
//	}
//
//	/**
//	 * @param requestHTTPDTO
//	 *            create requestedHTTPDTO
//	 * @param serviceName
//	 *            service name to be called
//	 * @param requestBody
//	 *            object to be included in HTTP entities
//	 */
//	private void prepareRequest(RequestHTTPDTO requestHTTPDTO, String serviceName, Object requestBody) {
//		LOGGER.debug("Preparing RequestHTTPDTO object for web-service");
//
//		requestHTTPDTO.setHttpMethod(
//				HttpMethod.valueOf(getEnvironmentProperty(serviceName, RegistrationConstants.HTTPMETHOD)));
//		requestHTTPDTO.setHttpHeaders(new HttpHeaders());
//		requestHTTPDTO.setRequestBody(requestBody);
//		// Headers
//		setHeaders(requestHTTPDTO.getHttpHeaders(), getEnvironmentProperty(serviceName, RegistrationConstants.HEADERS));
//
//		LOGGER.debug("Completed preparing RequestHTTPDTO object for web-service");
//	}
//
//
//	private String getEnvironmentProperty(String serviceName, String serviceComponent) {
//		return environment.getProperty(serviceName.concat(RegistrationConstants.DOT).concat(serviceComponent));
//	}
//
//
//
//	private boolean isResponseValid(Map<String, Object> responseMap, String key) {
//		return !(null == responseMap || responseMap.isEmpty() || !responseMap.containsKey(key));
//	}
//
//}
