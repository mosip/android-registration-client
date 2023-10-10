import 'dart:convert';

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutter_config/flutter_config.dart';
import 'package:registration_client/model/actuator_info.dart';
import 'package:registration_client/pigeon/common_details_pigeon.dart';
import 'package:registration_client/platform_spi/network_service.dart';
import 'package:http/http.dart' as http;

class NetworkServiceImpl implements NetworkService {
  @override
  Future<String> checkInternetConnection() async {

    try {
      final response = await http.get(Uri.parse(
          FlutterConfig.get('BASE_URL') + FlutterConfig.get('HEALTH_CHECK_PATH')))
          .timeout(const Duration(seconds: 2));
      return response.statusCode.toString();
    } catch (e) {
      debugPrint("Network Connection failed $e");
    }
    return "";
  }
  
  @override
  Future<String> getVersionNoApp() async {
   String versionInfo = '';
    try {
      final response = await http.get(Uri.parse(
          FlutterConfig.get('BASE_URL') + FlutterConfig.get('ACTUATOR_INFO_PATH')));
          ActuatorInfo actuatorInfo = ActuatorInfo.fromJson(jsonDecode(response.body));
      versionInfo = actuatorInfo.build['version']!;
    } catch (e) {
      debugPrint("Fetch actuator info failed $e");
    }
    return versionInfo;
  }
  
  @override
  Future<String> saveVersionToGlobalParam(String id, String version) async {
    String response = "";
    try {
      response = await CommonDetailsApi().saveVersionToGlobalParam(id, version);
    } on PlatformException {
      debugPrint('SaveVersionToGlobalParam Api Call Failed');
    } catch (e) {
      debugPrint('Save version failed: $e');
    }
    return response;
  }
}

NetworkService getNetworkServiceImpl() => NetworkServiceImpl();
