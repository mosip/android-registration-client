import 'package:flutter/material.dart';
import 'package:registration_client/platform_spi/network_service.dart';
import 'package:http/http.dart' as http;

class NetworkServiceImpl implements NetworkService {
  @override
  Future<String> checkInternetConnection() async {

    try {
      final response = await http.get(Uri.parse(
          'https://api-internal.dev2.mosip.net/v1/syncdata/actuator/health'))
          .timeout(const Duration(seconds: 2));
      return response.statusCode.toString();
    } catch (e) {
      debugPrint("Network Connection failed $e");
    }
    return "";
  }
}

NetworkService getNetworkServiceImpl() => NetworkServiceImpl();
