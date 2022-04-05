package io.mosip.registration.packetmanager.dto.PacketWriter;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Data;


@Data
public class RegistrationPacket {
	
	private String registrationId;
	private double idSchemaVersion;
	private String creationDate;
	private Map<String, Object> demographics;
	private Map<String, Document> documents;
	private Map<String, BiometricRecord> biometrics;
	private Map<String, Object> metaData;
	private List<Map<String, String>> audits;
	private List<HashSequenceMetaInfo> hashSequence1;
	private List<HashSequenceMetaInfo> hashSequence2;

	public RegistrationPacket() {
		this.creationDate = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
		this.demographics = new HashMap<String, Object>();
		this.documents = new HashMap<String, Document>();
		this.biometrics = new HashMap<String, BiometricRecord>();
		this.metaData = new HashMap<String, Object>();
		this.audits = new ArrayList<Map<String, String>>();
	}

	public void setField(String fieldId, Object value) {
		this.demographics.put(fieldId, value);
	}
	
	public void setBiometricField(String fieldId, BiometricRecord value) {
		this.biometrics.put(fieldId, value);
	}
	
	public void setDocumentField(String fieldId, Document dto) {
		this.documents.put(fieldId, dto);
	}

	public void setMetaData(String key, String value) {
		this.metaData.putIfAbsent(key, value);
	}

	public void setMetaData(String key, HashMap<String, Object> value) {
		this.metaData.putIfAbsent(key, value);
	}

	public void setMetaData(String key, List<HashMap<String, Object>> value) {
		this.metaData.putIfAbsent(key, value);
	}

	public void setAudits(List<Map<String, String>> audits) {
		getAudits().addAll(audits);
	}

	public void setAudit(Map<String, String> audit) {
		getAudits().add(audit);
	}
}
