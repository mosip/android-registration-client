import 'package:flutter/material.dart';

class BiometricCaptureControlProvider with ChangeNotifier {
  //Variables
  bool _isBiometricCaptureControl = false;
  



  //Getters and Setters
  bool get isBiometricCaptureControl => _isBiometricCaptureControl;

  set isBiometricCaptureControl(bool value) {
    _isBiometricCaptureControl = value;
    notifyListeners();
  }



  //Functions
}