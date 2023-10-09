import 'package:flutter/material.dart';
import 'package:registration_client/platform_spi/network_service.dart';
import 'package:http/http.dart' as http;
import 'package:registration_client/utils/app_config.dart';

class NetworkServiceImpl implements NetworkService {
  @override
  Future<String> checkInternetConnection() async {

    try {
      final response = await http.get(Uri.parse(
          healthCheckApi))
          .timeout(const Duration(seconds: 2));
      return response.statusCode.toString();
    } catch (e) {
      debugPrint("Network Connection failed $e");
    }
    return "";
  }
}

NetworkService getNetworkServiceImpl() => NetworkServiceImpl();
