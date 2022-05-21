package io.mosip.registration.clientmanager.spi.audit;


import android.util.Log;

//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//
//import io.mosip.kernel.core.auditmanager.spi.AuditHandler;
// import io.mosip.kernel.core.util.DateUtils;
import androidx.core.database.DatabaseUtilsCompat;

import io.mosip.registration.clientmanager.constant.AuditEvent;
import io.mosip.registration.clientmanager.constant.Components;
import io.mosip.registration.clientmanager.constant.LoggerConstants;
import io.mosip.registration.clientmanager.constant.RegistrationConstants;
//import io.mosip.registration.clientmanager.context.ApplicationContext;
//import io.mosip.registration.clientmanager.context.SessionContext;
import io.mosip.registration.clientmanager.dao.AuditDao;
import io.mosip.registration.clientmanager.dto.ResponseDTO;
import io.mosip.registration.clientmanager.entity.Audit;
import io.mosip.registration.clientmanager.spi.audit.builder.AuditRequestBuilder;
import io.mosip.registration.clientmanager.spi.audit.request.AuditRequestDto;
import io.mosip.registration.clientmanager.util.DateUtils;
//import io.mosip.registration.config.AppConfig;
//import io.mosip.registration.constants.AuditEvent;
//import io.mosip.registration.constants.AuditReferenceIdTypes;
//import io.mosip.registration.constants.Components;
//import io.mosip.registration.constants.LoggerConstants;
//import io.mosip.registration.constants.RegistrationConstants;

// import io.mosip.registration.dao.AuditDAO;
//
//import io.mosip.registration.service.BaseService;
//import io.mosip.registration.util.healthcheck.RegistrationSystemPropertiesChecker;

/**
 * Class to Audit the events of Registration Client.
 * <p>
 * This class creates a wrapper around {@link AuditRequestBuilder} class. This
 * class creates a {@link AuditRequestBuilder} object for each audit event and
 * persists the same using {@link// AuditHandler} .
 *
 * @author Balaji Sridharan
 * @since 1.0.0
 *
 */
//@Service
public class AuditManagerSerivceImpl /*extends BaseService*/ implements AuditManagerService {

	//private static final Logger LOGGER = AppConfig.getLogger(AuditManagerSerivceImpl.class);
	private String TAG = AuditManagerSerivceImpl.class.getSimpleName();

	//@Autowired
	private AuditHandler<AuditRequestDto> auditHandler;

	//@Autowired
	private AuditDao auditDAO;

	/*
	 * (non-Javadoc)
	 *
	 * @see io.mosip.registration.audit.AuditFactory#audit(io.mosip.registration.
	 * constants.AuditEvent, io.mosip.registration.constants.Components,
	 * java.lang.String, java.lang.String)
	 */
	@Override
	public void audit(AuditEvent auditEventEnum, Components appModuleEnum, String refId, String refIdType) {



		// Getting Host IP Address and Name
		String hostIP = "localhost";
		//String hostName = RegistrationSystemPropertiesChecker.getMachineId();
		String hostName = RegistrationConstants.DEFAULT_HOST_NAME;
//		hostName = hostName != null ? hostName : String.valueOf(ApplicationContext.map().get(RegistrationConstants.DEFAULT_HOST_NAME));

		if (auditEventEnum.getId().contains(RegistrationConstants.REGISTRATION_EVENTS)
				&& getRegistrationDTOFromSession() != null
				&& getRegistrationDTOFromSession().getRegistrationId() != null) {
			refId = getRegistrationDTOFromSession().getRegistrationId();
			refIdType = AuditReferenceIdTypes.REGISTRATION_ID.getReferenceTypeId();
		} else if (SessionContext.userId() != null && !SessionContext.userId().equals("NA")) {
			refId = SessionContext.userId();
			refIdType = AuditReferenceIdTypes.USER_ID.getReferenceTypeId();
		}

		AuditRequestBuilder auditRequestBuilder = new AuditRequestBuilder();
		auditRequestBuilder.setActionTimeStamp(DateUtils.getLocalDateTime())
				//.setApplicationId(String.valueOf(ApplicationContext.map().get(RegistrationConstants.APP_ID)))
				//.setApplicationName(String.valueOf(ApplicationContext.map().get(RegistrationConstants.APP_NAME)))
				.setApplicationId(RegistrationConstants.APPLICATION_ID)
				.setApplicationName(RegistrationConstants.APPLICATION_NAME)
				.setCreatedBy(RegistrationConstants.AUDIT_DEFAULT_USER).setDescription(auditEventEnum.getDescription())
				.setEventId(auditEventEnum.getId()).setEventName(auditEventEnum.getName())
				.setEventType(auditEventEnum.getType()).setHostIp(hostIP).setHostName(hostName).setId(refId)
				.setIdType(refIdType).setModuleId(appModuleEnum.getId()).setModuleName(appModuleEnum.getName())
				.setSessionUserId(RegistrationConstants.AUDIT_DEFAULT_USER).setSessionUserName(RegistrationConstants.AUDIT_DEFAULT_USER);

		auditHandler.addAudit(auditRequestBuilder.build());

		//Audit audit = new Audit();

		auditDAO.insert(new Audit("",
				DateUtils.getLocalDateTime(),
				auditEventEnum.getId(),
				auditEventEnum.getName(),
				auditEventEnum.getType(),
				DateUtils.getLocalDateTime(),
				hostName,
				hostIP,
				RegistrationConstants.APPLICATION_ID,
				RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.AUDIT_DEFAULT_USER,
				RegistrationConstants.AUDIT_DEFAULT_USER,
				refId,
				refIdType,"",
				appModuleEnum.getName(),
				appModuleEnum.getId(), ""));

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see io.mosip.registration.service.audit.AuditService#deleteAuditLogs()
	 */
	@Override
	public synchronized ResponseDTO deleteAuditLogs() {

//		LOGGER.info(LoggerConstants.AUDIT_SERVICE_LOGGER_TITLE, RegistrationConstants.APPLICATION_NAME,
//				RegistrationConstants.APPLICATION_ID, "Deletion of Audit Logs Started");

		Log.i(TAG, "Deletion of Audit Logs Started"+ LoggerConstants.AUDIT_SERVICE_LOGGER_TITLE+"," +
						RegistrationConstants.APPLICATION_NAME+","+ RegistrationConstants.APPLICATION_ID );

		ResponseDTO responseDTO = new ResponseDTO();

		//String val = getGlobalConfigValueOf(RegistrationConstants.AUDIT_TIMESTAMP);
		String val = RegistrationConstants.AUDIT_TIMESTAMP;

		if (val != null) {
			try {
				/* Delete Audits before given Time */
				//auditDAO.deleteAudits(DateUtils.parseToLocalDateTime(val));
				auditDAO.deleteAll();
				//setSuccessResponse(responseDTO, RegistrationConstants.AUDIT_LOGS_DELETION_SUCESS_MSG, null);
				Log.e(TAG, "deleteAuditLogs: "+RegistrationConstants.AUDIT_LOGS_DELETION_SUCESS_MSG );

			} catch (RuntimeException runtimeException) {
				//LOGGER.error(runtimeException.getMessage(), runtimeException);
				Log.e(TAG, "deleteAuditLogs: "+runtimeException.getMessage()+","+runtimeException);
				Log.e(TAG, "deleteAuditLogs: "+RegistrationConstants.AUDIT_LOGS_DELETION_SUCESS_MSG );

				//setErrorResponse(responseDTO, RegistrationConstants.AUDIT_LOGS_DELETION_FLR_MSG, null);
			}
		} else {
			//setSuccessResponse(responseDTO, RegistrationConstants.AUDIT_LOGS_DELETION_EMPTY_MSG, null);
		}
		//LOGGER.info("Deletion of Audit Logs Completed for datetime before : {}", val);
		Log.i(TAG, "Deletion of Audit Logs Completed for datetime before : {}"+val);

		return responseDTO;
	}
}
