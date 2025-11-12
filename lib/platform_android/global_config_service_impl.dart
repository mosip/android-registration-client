

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:registration_client/pigeon/global_config_settings_pigeon.dart';
import 'package:registration_client/platform_spi/global_config_service.dart';

class GlobalConfigServiceImpl implements GlobalConfigService {
  @override
  Future<Map<String?, String?>> getLocalConfigurations() async {
    Map<String?, String?> localPreferences = {};
    try {
      localPreferences = await GlobalConfigSettingsApi().getLocalConfigurations();
    } on PlatformException {
      debugPrint('GlobalConfigServiceImpl call failed!');
    } catch (e) {
      debugPrint('Local Config value not fetched! ${e.toString()}');
    }
    return localPreferences;
  }

  @override
  Future<List<String?>> getPermittedConfigurationNames() async {
    List<String?> permittedConfiguration = [];
    try {
      permittedConfiguration =
          await GlobalConfigSettingsApi().getPermittedConfigurationNames();
    } on PlatformException {
      debugPrint('GlobalConfigServiceImpl call failed!');
    } catch (e) {
      debugPrint(
          'Permitted Configuration value not fetched! ${e.toString()}');
    }
    return permittedConfiguration;
  }

  @override
  Future<Map<String?, Object?>> getRegistrationParams() async {
    Map<String?, Object?> registrationParams = {};
    try {
      registrationParams = await GlobalConfigSettingsApi().getRegistrationParams();
    } on PlatformException {
      debugPrint('GlobalConfigServiceImpl call failed!');
    } catch (e) {
      debugPrint('Registration Params value not fetched! ${e.toString()}');
    }
    return registrationParams;
  }

  @override
  Future<void> modifyConfigurations(Map<String, String> localPreferences) async {
    try {
      await GlobalConfigSettingsApi().modifyConfigurations(localPreferences);
    } on PlatformException {
      debugPrint('GlobalConfigServiceImpl call failed!');
    } catch (e) {
      debugPrint('Modify Configurations failed! ${e.toString()}');
    }
  }

  @override
  Future<String> getGpsEnableFlag() async {
    String gpsEnableFlag = "";
    try {
      gpsEnableFlag = await GlobalConfigSettingsApi().getGpsEnableFlag();
    } on PlatformException {
      debugPrint("Location Api failed!");
    }  catch (e) {
      debugPrint("Location fetch error: $e");
    }
    return gpsEnableFlag;
  }

}

GlobalConfigService getGlobalConfigServiceImpl() => GlobalConfigServiceImpl();