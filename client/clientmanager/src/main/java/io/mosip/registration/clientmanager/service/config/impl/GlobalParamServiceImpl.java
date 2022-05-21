//package io.mosip.registration.clientmanager.service.config.impl;
//
//import java.io.IOException;
//import java.sql.Timestamp;
//import java.util.Arrays;
//import java.util.HashMap;
//import java.util.HashSet;
//import java.util.LinkedHashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.Map.Entry;
//import java.util.Set;
//
//
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//
//import io.mosip.kernel.clientcrypto.service.impl.ClientCryptoFacade;
//import io.mosip.kernel.core.logger.spi.Logger;
//import io.mosip.kernel.core.util.CryptoUtil;
//import io.mosip.kernel.core.util.DateUtils;
//import io.mosip.registration.clientmanager.constant.RegistrationConstants;
//import io.mosip.registration.clientmanager.service.BaseService;
//import io.mosip.registration.clientmanager.service.config.GlobalParamService;
//import io.mosip.registration.config.AppConfig;
//import io.mosip.registration.constants.RegistrationConstants;
//import io.mosip.registration.context.ApplicationContext;
//import io.mosip.registration.dao.GlobalParamDAO;
//import io.mosip.registration.dto.ResponseDTO;
//import io.mosip.registration.dto.SuccessResponseDTO;
//import io.mosip.registration.entity.GlobalParam;
//import io.mosip.registration.entity.id.GlobalParamId;
//import io.mosip.registration.exception.RegBaseCheckedException;
//import io.mosip.registration.exception.RegistrationExceptionConstants;
//import io.mosip.registration.service.BaseService;
//import io.mosip.registration.service.config.GlobalParamService;
//import io.mosip.registration.util.healthcheck.RegistrationAppHealthCheckUtil;
//
///**
// * Class for implementing GlobalContextParam service
// *
// * @author Sravya Surampalli
// * @author Brahmananda Reddy
// * @since 1.0.0
// *
// */
//@Service
//public class GlobalParamServiceImpl extends BaseService implements GlobalParamService {
//
//	private static final Set<String> NON_REMOVABLE_PARAMS = new HashSet<>(
//			Arrays.asList("mosip.registration.machinecenterchanged", "mosip.registration.initial_setup",
//					"mosip.reg.db.current.version", "mosip.reg.services.version",
//					RegistrationConstants.IS_SOFTWARE_UPDATE_AVAILABLE, RegistrationConstants.SERVICES_VERSION_KEY,
//					RegistrationConstants.HTTP_API_READ_TIMEOUT, RegistrationConstants.HTTP_API_WRITE_TIMEOUT,
//					RegistrationConstants.LAST_SOFTWARE_UPDATE, RegistrationConstants.REGCLIENT_INSTALLED_TIME,
//					RegistrationConstants.AUDIT_TIMESTAMP));
//	/**
//	 * Instance of LOGGER
//	 */
//	private static final Logger LOGGER = AppConfig.getLogger(GlobalParamServiceImpl.class);
//
//	/**
//	 * Class to retrieve Global parameters of application
//	 */
//	@Autowired
//	private GlobalParamDAO globalParamDAO;
//
//	@Autowired
//	private ClientCryptoFacade clientCryptoFacade;
//
//	private static final ObjectMapper objectMapper = new ObjectMapper();
//
//	/*
//	 * (non-Javadoc)
//	 *
//	 * @see io.mosip.registration.service.GlobalParamService#getGlobalParams
//	 */
//	public Map<String, Object> getGlobalParams() {
//		LOGGER.info("Fetching list of global params");
//		return globalParamDAO.getGlobalParams();
//	}
//
//	/*
//	 * (non-Javadoc)
//	 *
//	 * @see io.mosip.registration.service.config.GlobalParamService#synchConfigData(
//	 * boolean)
//	 */
//	@Timed(extraTags = {"type", "configuration"})
//	@Override
//	public ResponseDTO synchConfigData(boolean isJob) {
//		LOGGER.info("config data sync is started");
//		ResponseDTO responseDTO = new ResponseDTO();
//
//		if (isJob && !serviceDelegateUtil.isNetworkAvailable()) {
//			LOGGER.info("NO Internet Connection So calling off global param sync");
//			return setErrorResponse(responseDTO, RegistrationConstants.NO_INTERNET, null);
//		}
//		String triggerPoint = isJob ? RegistrationConstants.JOB_TRIGGER_POINT_SYSTEM
//				: RegistrationConstants.JOB_TRIGGER_POINT_USER;
//
//		saveGlobalParams(responseDTO, triggerPoint);
//
//		if (!isJob) {
//			/* If unable to fetch from server and no data in DB create error response */
//			if (responseDTO.getSuccessResponseDTO() == null && getGlobalParams().isEmpty()) {
//				setErrorResponse(responseDTO, RegistrationConstants.POLICY_SYNC_ERROR_MESSAGE, null);
//			} else if (responseDTO.getSuccessResponseDTO() != null) {
//				setSuccessResponse(responseDTO, RegistrationConstants.POLICY_SYNC_SUCCESS_MESSAGE,
//						responseDTO.getSuccessResponseDTO().getOtherAttributes());
//			}
//		}
//
//		LOGGER.info("config data sync is completed");
//		return responseDTO;
//	}
//
//	@SuppressWarnings("unchecked")
//	private void parseToMap(HashMap<String, Object> map, HashMap<String, String> globalParamMap) {
//		if (map != null) {
//			for (Entry<String, Object> entry : map.entrySet()) {
//				String key = entry.getKey();
//
//				if (entry.getValue() instanceof HashMap) {
//					parseToMap((HashMap<String, Object>) entry.getValue(), globalParamMap);
//				} else {
//					globalParamMap.put(key, String.valueOf(entry.getValue()));
//				}
//			}
//		}
//	}
//
//	private void saveGlobalParams(ResponseDTO responseDTO, String triggerPoinnt) {
//
//		if (!serviceDelegateUtil.isNetworkAvailable()) {
//			LOGGER.error("Unable to sync config data as no internet connection!");
//			return;
//		}
//
//		try {
//				boolean isToBeRestarted = false;
//				Map<String, String> requestParamMap = new HashMap<>();
//				requestParamMap.put("key_index",
//						CryptoUtil.computeFingerPrint(clientCryptoFacade.getClientSecurity().getEncryptionPublicPart(), null));
//				LinkedHashMap<String, Object> globalParamJsonMap = (LinkedHashMap<String, Object>) serviceDelegateUtil
//						.get(RegistrationConstants.GET_GLOBAL_CONFIG, requestParamMap, true, triggerPoinnt);
//
//				if (null == globalParamJsonMap.get(RegistrationConstants.RESPONSE) ||
//						null != globalParamJsonMap.get(RegistrationConstants.ERRORS))  {
//					setErrorResponse(responseDTO, RegistrationConstants.POLICY_SYNC_ERROR_MESSAGE, null);
//					LOGGER.error("Failed in configuration sync {}", globalParamJsonMap);
//					return;
//				}
//
//				HashMap<String, Object> responseMap = (HashMap<String, Object>) globalParamJsonMap
//						.get(RegistrationConstants.RESPONSE);
//
//				HashMap<String, String> globalParamMap = new HashMap<>();
//
//				if (responseMap.get("configDetail") != null) {
//					HashMap<String, Object> configDetailJsonMap = (HashMap<String, Object>) responseMap
//							.get("configDetail");
//
//					if (configDetailJsonMap.get("globalConfiguration") != null) {
//						parseToMap(
//								(HashMap<String, Object>) getParams(
//										(String) configDetailJsonMap.get("globalConfiguration")),
//								globalParamMap);
//					}
//
//					if (configDetailJsonMap.get("registrationConfiguration") != null) {
//						parseToMap(
//								(HashMap<String, Object>) getParams(
//										(String) configDetailJsonMap.get("registrationConfiguration")),
//								globalParamMap);
//					}
//				}
//
//				List<GlobalParam> globalParamList = globalParamDAO.getAllEntries();
//				isToBeRestarted = parseGlobalParam(isToBeRestarted, globalParamMap, globalParamList);
//
//				for (Entry<String, String> key : globalParamMap.entrySet()) {
//					createNew(key.getKey(), globalParamMap.get(key.getKey()), globalParamList);
//					isToBeRestarted = isToBeRestarted ? isToBeRestarted : isPropertyRequireRestart(key.getKey());
//					/* Add in application map */
//					updateApplicationMap(key.getKey(), key.getValue());
//				}
//
//				/* Save all Global Params */
//				globalParamDAO.saveAll(globalParamList);
//
//				Map<String, Object> attributes = null;
//				if (isToBeRestarted) {
//					attributes = new HashMap<>();
//					attributes.put("Restart", RegistrationConstants.ENABLE);
//				}
//
//				setSuccessResponse(responseDTO, RegistrationConstants.POLICY_SYNC_SUCCESS_MESSAGE, attributes);
//
//		} catch (Exception exception) {
//			setErrorResponse(responseDTO,  (isAuthTokenEmptyException(exception)) ?
//					RegistrationExceptionConstants.AUTH_TOKEN_COOKIE_NOT_FOUND.getErrorCode() :
//					RegistrationConstants.POLICY_SYNC_ERROR_MESSAGE, null);
//			LOGGER.error(exception.getMessage(), exception);
//		}
//	}
//
//	private boolean parseGlobalParam(boolean isToBeRestarted, HashMap<String, String> globalParamMap,
//			List<GlobalParam> globalParamList) {
//		for (GlobalParam globalParam : globalParamList) {
//			if (!NON_REMOVABLE_PARAMS.contains(globalParam.getGlobalParamId().getCode())) {
//				/* Check in map, if exists, update it and remove from map */
//				GlobalParamId globalParamId = globalParam.getGlobalParamId();
//
//				if (globalParamMap.get(globalParamId.getCode()) != null) {
//
//					/* update (Local already exists) but val change */
//					if (!globalParamMap.get(globalParamId.getCode()).trim().equals(globalParam.getVal())
//							|| !(globalParam.getIsActive().booleanValue())) {
//						String val = globalParamMap.get(globalParamId.getCode()).trim();
//						updateVal(globalParam, val);
//
//						/* Add in application map */
//						updateApplicationMap(globalParamId.getCode(), val);
//
//						isToBeRestarted = isPropertyRequireRestart(globalParamId.getCode());
//					}
//				}
//				/* Set is deleted true as removed from server */
//				else {
//					updateIsDeleted(globalParam);
//					ApplicationContext.removeGlobalConfigValueOf(globalParamId.getCode());
//				}
//				globalParamMap.remove(globalParamId.getCode());
//			}
//		}
//		return isToBeRestarted;
//	}
//
//	private boolean isPropertyRequireRestart(String key) {
//		return (key.contains("kernel") || key.contains("mosip.primary") || key.contains("mosip.biometric.sdk") || key.contains("languages"));
//	}
//
//	private void updateVal(GlobalParam globalParam, String val) {
//		globalParam.setVal(val);
//		globalParam.setUpdBy(getUserIdFromSession());
//		globalParam.setUpdDtimes(Timestamp.valueOf(DateUtils.getUTCCurrentDateTime()));
//		globalParam.setIsActive(true);
//		globalParam.setIsDeleted(false);
//	}
//
//	private void updateIsDeleted(GlobalParam globalParam) {
//		globalParam.setIsActive(false);
//		globalParam.setIsDeleted(true);
//		globalParam.setDelDtimes(Timestamp.valueOf(DateUtils.getUTCCurrentDateTime()));
//		globalParam.setUpdBy(getUserIdFromSession());
//		globalParam.setUpdDtimes(Timestamp.valueOf(DateUtils.getUTCCurrentDateTime()));
//	}
//
//	private void createNew(String code, String value, List<GlobalParam> globalParamList) {
//		GlobalParam globalParam = new GlobalParam();
//
//		GlobalParamId globalParamId = new GlobalParamId();
//		globalParamId.setCode(code);
//		globalParamId.setLangCode(RegistrationConstants.ENGLISH_LANG_CODE);
//
//		/* TODO Need to Add Description not key (CODE) */
//		globalParam.setName(code);
//		globalParam.setTyp("CONFIGURATION");
//		globalParam.setIsActive(true);
//		globalParam.setCrBy(getUserIdFromSession());
//		globalParam.setCrDtime(Timestamp.valueOf(DateUtils.getUTCCurrentDateTime()));
//		globalParam.setVal(value);
//		globalParam.setGlobalParamId(globalParamId);
//		globalParamList.add(globalParam);
//	}
//
//	/*
//	 * (non-Javadoc)
//	 *
//	 * @see io.mosip.registration.service.config.GlobalParamService#
//	 * updateSoftwareUpdateStatus(boolean)
//	 */
//	@Override
//	public ResponseDTO updateSoftwareUpdateStatus(boolean isUpdateAvailable, Timestamp timestamp) {
//
//		LOGGER.info("Updating the SoftwareUpdate flag started.");
//
//		ResponseDTO responseDTO = new ResponseDTO();
//
//		GlobalParam globalParam = globalParamDAO.updateSoftwareUpdateStatus(isUpdateAvailable, timestamp);
//
//		SuccessResponseDTO successResponseDTO = new SuccessResponseDTO();
//		if (globalParam.getVal().equalsIgnoreCase(RegistrationConstants.ENABLE)) {
//			successResponseDTO.setMessage(RegistrationConstants.SOFTWARE_UPDATE_SUCCESS_MSG);
//		} else {
//			successResponseDTO.setMessage(RegistrationConstants.SOFTWARE_UPDATE_FAILURE_MSG);
//		}
//		responseDTO.setSuccessResponseDTO(successResponseDTO);
//
//		LOGGER.info("Updating the SoftwareUpdate flag ended.");
//		return responseDTO;
//	}
//
//	/*
//	 * (non-Javadoc)
//	 *
//	 * @see
//	 * io.mosip.registration.service.config.GlobalParamService#update(java.lang.
//	 * String, java.lang.String)
//	 */
//	@Override
//	public void update(String code, String val) {
//		LOGGER.info("Update global param started");
//
//		if(code == null || val == null) {
//			LOGGER.error("Not Update global param because of code or val is null value");
//			return;
//		}
//
//		// Primary Key
//		GlobalParamId globalParamId = new GlobalParamId();
//		globalParamId.setCode(code);
//		globalParamId.setLangCode(RegistrationConstants.ENGLISH_LANG_CODE);
//
//		// Get Current global param
//		GlobalParam globalParam = globalParamDAO.get(globalParamId);
//
//		Timestamp time = Timestamp.valueOf(DateUtils.getUTCCurrentDateTime());
//		if (globalParam == null) {
//			globalParam = new GlobalParam();
//			globalParam.setGlobalParamId(globalParamId);
//			globalParam.setCrBy(RegistrationConstants.JOB_TRIGGER_POINT_SYSTEM);
//			globalParam.setCrDtime(time);
//			globalParam.setTyp(RegistrationConstants.CONFIGURATION);
//
//		}
//		globalParam.setVal(val);
//		globalParam.setName(code);
//		globalParam.setIsActive(true);
//		globalParam.setUpdBy(RegistrationConstants.JOB_TRIGGER_POINT_SYSTEM);
//		globalParam.setUpdDtimes(time);
//
//		// Update Global Param
//		globalParamDAO.update(globalParam);
//
//		updateApplicationMap(code, val);
//
//		LOGGER.info("Update global param ended");
//	}
//
//	private void updateApplicationMap(String code, String val) {
//		ApplicationContext.setGlobalConfigValueOf(code, val);
//	}
//
//	private Map<String, Object> getParams(String encodedCipher) {
//		try {
//			byte[] data = clientCryptoFacade.decrypt(ClientCryptoUtils.decodeBase64Data(encodedCipher));
//			Map<String, Object> paramMap = objectMapper.readValue(data, HashMap.class);
//			return paramMap;
//		} catch (IOException e) {
//			LOGGER.error("Failed to decrypt and parse config response >> " ,e);
//		}
//		return null;
//	}
//}
