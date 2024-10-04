/*
 * Copyright (c) Modular Open Source Identity Platform
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *
*/

import 'package:connectivity_plus/connectivity_plus.dart';
import 'package:flutter/material.dart';
import 'package:registration_client/platform_spi/network_service.dart';

class ConnectivityProvider extends ChangeNotifier {
  NetworkService networkService = NetworkService();
  late ConnectivityResult _connectivityResult;
  bool _isConnected = false;

  ConnectivityResult get connectivityResult => _connectivityResult;
  bool get isConnected => _isConnected;

  checkNetworkConnection() async {
    String response = await networkService.checkInternetConnection();
    if(response == "200") {
      _isConnected = true;
    } else {
      _isConnected = false;
    }
    notifyListeners();
  }
}
