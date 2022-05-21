///*
//package io.mosip.registration.clientmanager.service.config.impl;
//
//import java.util.List;
//import java.util.Map;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import io.mosip.registration.dao.LocalConfigDAO;
//import io.mosip.registration.entity.PermittedLocalConfig;
//import io.mosip.registration.service.BaseService;
//import io.mosip.registration.service.config.LocalConfigService;
//
//@Service
//public class LocalConfigServiceImpl extends BaseService implements LocalConfigService {
//
//	@Autowired
//	private LocalConfigDAO localConfigDAO;
//
//	@Override
//	public List<PermittedLocalConfig> getAllPermittedLocalConfigs() {
//		return localConfigDAO.getAllPermittedLocalConfigs();
//	}
//
//	@Override
//	public List<String> getPermittedJobs(String configType) {
//		return localConfigDAO.getPermittedJobs(configType);
//	}
//
//	@Override
//	public List<String> getPermittedConfigurations(String configType) {
//		return localConfigDAO.getPermittedConfigurations(configType);
//	}
//
//	@Override
//	public void modifyJob(String name, String value) {
//		localConfigDAO.modifyJob(name, value);
//	}
//
//	@Override
//	public void modifyConfigurations(Map<String, String> localPreferences) {
//		localConfigDAO.modifyConfigurations(localPreferences);
//	}
//
//	@Override
//	public Map<String, String> getLocalConfigurations() {
//		return localConfigDAO.getLocalConfigurations();
//	}
//
//	@Override
//	public String getValue(String name) {
//		return localConfigDAO.getValue(name);
//	}
//
//	@Override
//	public void updateShortcutPreference(String name, String value) {
//		localConfigDAO.updateShortcutPreference(name, value);
//	}
//}
//*/
