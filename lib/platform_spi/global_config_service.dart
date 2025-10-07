import 'package:registration_client/platform_android/global_config_service_impl.dart';

abstract class GlobalConfigService {
  Future<Map<String?, Object?>> getRegistrationParams();

  Future<Map<String?, String?>> getLocalConfigurations();

  Future<List<String?>> getPermittedConfigurationNames();

  Future<void> modifyConfigurations(Map<String, String> localPreferences);

  factory GlobalConfigService() => getGlobalConfigServiceImpl();
}