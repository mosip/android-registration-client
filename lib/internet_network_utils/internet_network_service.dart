import 'dart:async';
import 'package:flutter/material.dart';
import 'package:connectivity_plus/connectivity_plus.dart';
import 'package:http/http.dart' as http;
import 'package:internet_connection_checker/internet_connection_checker.dart';

class InternetNetworkService {

  final Connectivity connectivity = Connectivity();
  final InternetConnectionChecker internetConnectionChecker;

  final StreamController<ConnectivityResult> _connectivityStreamController = StreamController.broadcast();



  Stream<ConnectivityResult> get onConnectivityChanged => _connectivityStreamController.stream;

  Future<bool> get isConnected async {
    try {
      bool isDeviceConnected = false;
      final connectivityResult = await connectivity.checkConnectivity();
      debugPrint('Connectivity Result: $connectivityResult');

      if (connectivityResult != ConnectivityResult.none) {
        isDeviceConnected = await internetConnectionChecker.hasConnection ||
            await hasInternetConnection();
      }
      debugPrint('Device Connected: $isDeviceConnected');
      return isDeviceConnected;
    } catch (e) {
      debugPrint('Error checking network connection: $e');
      return false;
    }
  }



  InternetNetworkService(this.internetConnectionChecker) {

    connectivity.onConnectivityChanged.listen((result) {

      _connectivityStreamController.sink.add(result);

    });

  }

  Future<bool> hasInternetConnection() async {
    try {
      final response = await http.get(Uri.parse('https://www.google.com')).timeout(
        const Duration(seconds: 5),
      );
      if (response.statusCode == 200) {
        return true;
      }
    } catch (e) {
      debugPrint('Error checking internet connection: $e');
    }
    return false;
  }

}

