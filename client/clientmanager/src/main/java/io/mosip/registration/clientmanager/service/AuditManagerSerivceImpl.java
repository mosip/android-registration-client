package io.mosip.registration.clientmanager.service;


import android.util.Log;

import io.mosip.registration.clientmanager.constant.AuditEvent;
import io.mosip.registration.clientmanager.constant.Components;
import io.mosip.registration.clientmanager.constant.LoggerConstants;
import io.mosip.registration.clientmanager.constant.RegistrationConstants;
import io.mosip.registration.clientmanager.dao.AuditDao;
import io.mosip.registration.clientmanager.dto.ResponseDTO;
import io.mosip.registration.clientmanager.entity.Audit;
import io.mosip.registration.clientmanager.spi.AuditManagerService;
import io.mosip.registration.clientmanager.builder.AuditRequestBuilder;
 import io.mosip.registration.packetmanager.util.DateUtils;

public class AuditManagerSerivceImpl implements AuditManagerService {

 	private String TAG = AuditManagerSerivceImpl.class.getSimpleName();

 	private AuditHandler<AuditRequestDto> auditHandler;

 	private AuditDao auditDAO;

	@Override
	public void audit(AuditEvent auditEventEnum, Components appModuleEnum, String refId, String refIdType) {



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


	@Override
	public synchronized ResponseDTO deleteAuditLogs() {


		Log.i(TAG, "Deletion of Audit Logs Started"+ LoggerConstants.AUDIT_SERVICE_LOGGER_TITLE+"," +
						RegistrationConstants.APPLICATION_NAME+","+ RegistrationConstants.APPLICATION_ID );

		ResponseDTO responseDTO = new ResponseDTO();

 		String val = RegistrationConstants.AUDIT_TIMESTAMP;

		if (val != null) {
			try {

				auditDAO.deleteAll();


			} catch (RuntimeException runtimeException) {
				Log.e(TAG, "deleteAuditLogs: "+RegistrationConstants.AUDIT_LOGS_DELETION_SUCESS_MSG );

 			}
		} else {
 		}

		Log.i(TAG, "Deletion of Audit Logs Completed for datetime before : {}"+val);

		return responseDTO;
	}
}
