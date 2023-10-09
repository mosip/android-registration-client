import 'package:flutter/material.dart';
import 'package:flutter_config/flutter_config.dart';
import 'package:registration_client/platform_spi/network_service.dart';
import 'package:http/http.dart' as http;

class NetworkServiceImpl implements NetworkService {
  @override
  Future<String> checkInternetConnection() async {

    try {
      final response = await http.get(Uri.parse(
          FlutterConfig.get('HEALTH_CHECK_URL')))
          .timeout(const Duration(seconds: 2));
      return response.statusCode.toString();
    } catch (e) {
      debugPrint("Network Connection failed $e");
    }
    return "";
  }
}

NetworkService getNetworkServiceImpl() => NetworkServiceImpl();
