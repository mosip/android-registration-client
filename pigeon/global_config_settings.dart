import 'package:pigeon/pigeon.dart';

@HostApi()
abstract class GlobalConfigSettingsApi {
  @async
  Map<String, Object> getRegistrationParams();
  @async
  Map<String, String> getLocalConfigurations();
  @async
  List<String> getPermittedConfigurationNames();
  @async
  void modifyConfigurations(Map<String, String> localPreferences);
  @async
  String getGpsEnableFlag();
}