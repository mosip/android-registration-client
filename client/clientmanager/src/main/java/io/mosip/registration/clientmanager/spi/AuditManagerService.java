package io.mosip.registration.clientmanager.spi;


import io.mosip.registration.clientmanager.constant.AuditEvent;
import io.mosip.registration.clientmanager.constant.Components;
import io.mosip.registration.clientmanager.dto.ResponseDTO;

public interface AuditManagerService {
	void audit(AuditEvent auditEventEnum, Components appModuleEnum, String refId,
			   String refIdType);

	ResponseDTO deleteAuditLogs();
}