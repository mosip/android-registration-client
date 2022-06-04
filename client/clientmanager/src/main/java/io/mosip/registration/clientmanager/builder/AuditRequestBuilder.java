package io.mosip.registration.clientmanager.builder;

import java.time.LocalDateTime;


import io.mosip.registration.clientmanager.spi.audit.request.AuditRequestDto;



public class AuditRequestBuilder {


	public AuditRequestBuilder() {
		auditRequest = new AuditRequestDto();
	}


	public AuditRequestBuilder setActionTimeStamp(LocalDateTime actionTimeStamp) {
		auditRequest.setActionTimeStamp(actionTimeStamp);
		return this;
	}


	public AuditRequestBuilder setApplicationId(String applicationId) {
		auditRequest.setApplicationId(applicationId);
		return this;
	}


	public AuditRequestBuilder setApplicationName(String applicationName) {
		auditRequest.setApplicationName(applicationName);
		return this;
	}


	public AuditRequestBuilder setCreatedBy(String createdBy) {
		auditRequest.setCreatedBy(createdBy);
		return this;
	}


	public AuditRequestBuilder setDescription(String description) {
		auditRequest.setDescription(description);
		return this;
	}


	public AuditRequestBuilder setEventId(String eventId) {
		auditRequest.setEventId(eventId);
		return this;
	}


	public AuditRequestBuilder setEventName(String eventName) {
		auditRequest.setEventName(eventName);
		return this;
	}


	public AuditRequestBuilder setEventType(String eventType) {
		auditRequest.setEventType(eventType);
		return this;
	}


	public AuditRequestBuilder setHostIp(String hostIp) {
		auditRequest.setHostIp(hostIp);
		return this;
	}


	public AuditRequestBuilder setHostName(String hostName) {
		auditRequest.setHostName(hostName);
		return this;
	}

	public AuditRequestBuilder setId(String id) {
		auditRequest.setId(id);
		return this;
	}


	public AuditRequestBuilder setIdType(String idType) {
		auditRequest.setIdType(idType);
		return this;
	}


	public AuditRequestBuilder setModuleId(String moduleId) {
		auditRequest.setModuleId(moduleId);
		return this;
	}


	public AuditRequestBuilder setModuleName(String moduleName) {
		auditRequest.setModuleName(moduleName);
		return this;
	}


	public AuditRequestBuilder setSessionUserId(String sessionUserId) {
		auditRequest.setSessionUserId(sessionUserId);
		return this;
	}


	public AuditRequestBuilder setSessionUserName(String sessionUserName) {
		auditRequest.setSessionUserName(sessionUserName);
		return this;
	}

	public AuditRequestDto build() {
		return auditRequest;
	}
}
