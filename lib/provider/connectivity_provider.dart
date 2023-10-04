import 'dart:async';
import 'dart:developer';

import 'package:connectivity_plus/connectivity_plus.dart';
import 'package:flutter/material.dart';
import 'package:registration_client/platform_spi/network_service.dart';

class ConnectivityProvider extends ChangeNotifier {
  NetworkService networkService = NetworkService();
  late ConnectivityResult _connectivityResult;
  bool _vpnConnection = false;

  ConnectivityResult get connectivityResult => _connectivityResult;
  bool get vpnConnection => _vpnConnection;

  // set vpnConnection(bool value) {
  //   _vpnConnection = value;
  //   notifyListeners();
  // }

  ConnectivityProvider() {
    initialize();
  }

  Future<void> initialize() async {
    final connectivity = Connectivity();
    _connectivityResult = await connectivity.checkConnectivity();
    log("check ${_connectivityResult.toString()}");

    connectivity.onConnectivityChanged.listen((result) {
      _connectivityResult = result;
      log("on change ${result.toString()}");
      networkService.checkInternetConnection().then((value) {
        if(value == "200") {
          _vpnConnection = true;
        } else {
          _vpnConnection = false;
        }
        notifyListeners();
      });
      // notifyListeners();
    });
  }
}
