import 'dart:async';

import 'package:connectivity_plus/connectivity_plus.dart';
import 'package:flutter/material.dart';

class ConnectivityProvider extends ChangeNotifier {
  late ConnectivityResult _connectivityResult;

  ConnectivityResult get connectivityResult => _connectivityResult;

  ConnectivityProvider() {
    initialize();
  }

  Future<void> initialize() async {
    final connectivity = Connectivity();
    _connectivityResult = await connectivity.checkConnectivity();

    connectivity.onConnectivityChanged.listen((result) {
      _connectivityResult = result;
      notifyListeners();
    });
  }

  bool get isConnected => _connectivityResult != ConnectivityResult.none;
}
