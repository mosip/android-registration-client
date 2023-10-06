import 'package:connectivity_plus/connectivity_plus.dart';
import 'package:flutter/material.dart';
import 'package:registration_client/platform_spi/network_service.dart';

class ConnectivityProvider extends ChangeNotifier {
  NetworkService networkService = NetworkService();
  late ConnectivityResult _connectivityResult;
  bool _isConnected = false;

  ConnectivityResult get connectivityResult => _connectivityResult;
  bool get isConnected => _isConnected;

  // ConnectivityProvider() {
  //   initialize();
  //   _connectivity.onConnectivityChanged.listen(updateConnection);
  // }

  // Future<void> initialize() async {
  //   late ConnectivityResult result;
  //   try {
  //     result = await _connectivity.checkConnectivity();
  //   } on PlatformException catch (e) {
  //     debugPrint('Couldn\'t check connectivity status $e');
  //     return;
  //   }

  //   updateConnection(result);
  // }

  // void updateConnection(ConnectivityResult result) async {
  //   _connectivityResult = result;
  //   String response = await networkService.checkInternetConnection();
  //   if(response == "200") {
  //     log("Back online");
  //     _vpnConnection = true;
  //   } else {
  //     log("offline");
  //     _vpnConnection = false;
  //   }
  //   log(_vpnConnection.toString());
  //   notifyListeners();
  // }

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
