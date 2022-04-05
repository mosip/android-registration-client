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

	public void setMetaData(Map<String, String> metaInfo) {
		metaInfo.entrySet().forEach(meta -> {
			setFields(meta.getKey(), meta.getValue(), this.metaData);
		});
	}

	public void addMetaData(String key, String value) {
		setFields(key, value, this.metaData);
	}

	public void setAudits(List<Map<String, String>> audits) {
		getAudits().addAll(audits);
	}

	public void setAudit(Map<String, String> audit) {
		getAudits().add(audit);
	}

	private void setFields(String fieldName, String value, Map finalMap) {
		try {
			if (value != null) {
				Object json = new JSONTokener(value).nextValue();
				if (json instanceof JSONObject) {
					HashMap<String, Object> hashMap = new ObjectMapper().readValue(value, HashMap.class);
					finalMap.putIfAbsent(fieldName, hashMap);
				}
				else if (json instanceof JSONArray) {
					List jsonList = new ArrayList<>();
					JSONArray jsonArray = new JSONArray(value);
					for (int i = 0; i < jsonArray.length(); i++) {
						Object obj = jsonArray.get(i);
						HashMap<String, Object> hashMap = new ObjectMapper().readValue(obj.toString(), HashMap.class);
						jsonList.add(hashMap);
					}
					finalMap.putIfAbsent(fieldName, jsonList);
				} else
					finalMap.putIfAbsent(fieldName, value);
			} else
				finalMap.putIfAbsent(fieldName, value);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
