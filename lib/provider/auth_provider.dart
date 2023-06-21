import 'dart:convert';

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:registration_client/pigeon/user_pigeon.dart';
import 'package:registration_client/platform_android/auth_impl.dart';

class AuthProvider with ChangeNotifier {
  bool _isLoggedIn = false;
  bool _isOnboarded = false;
  bool _isDefault = false;
  bool _isSupervisor = false;
  bool _isOfficer = false;
  bool _isValidUser = false;
  late User _currentUser;

  bool get isLoggedIn => _isLoggedIn;
  bool get isOnboarded => _isOnboarded;
  bool get isDefault => _isDefault;
  bool get isSupervisor => _isSupervisor;
  bool get isOfficer => _isOfficer;
  bool get isValidUser => _isValidUser;
  User get currentUser => _currentUser;

  setIsLoggedIn(bool value) {
    _isLoggedIn = value;
    notifyListeners();
  }

  setIsOnboarded(bool value) {
    _isOnboarded = value;
    notifyListeners();
  }

  setIsDefault(bool value) {
    _isDefault = value;
    notifyListeners();
  }

  setIsSupervisor(bool value) {
    _isSupervisor = value;
    notifyListeners();
  }

  setIsOfficer(bool value) {
    _isOfficer = value;
    notifyListeners();
  }

  setIsValidUser(bool value) {
    _isValidUser = value;
    notifyListeners();
  }

  setCurrentUser(User user) {
    _currentUser = user;
    notifyListeners();
  }

  clearUser() {
    _isLoggedIn = false;
    _isValidUser = false;
    _isDefault = false;
    _isOfficer = false;
    _isOnboarded = false;
    _isSupervisor = false;
    notifyListeners();
  }

  validateUser(username) async {
    final user = await AuthImpl().validateUser(username);

    if (user.errorCode != null) {
      _isValidUser = false;
    } else {
      _isOnboarded = false;
      _isValidUser = true;
      _currentUser = user;
    }

    notifyListeners();
  }

  logoutUser() async {
    User response;
    User response_validate_user;
    Map<String, dynamic> mp;
    try {
      //response = await OnboardingPage2View.platform.invokeMethod("logout");
      response = await AuthImpl().logoutUser();

      // response_validate_user = await OnboardingPage2View.platform
      //     .invokeMethod("validateUsername", {'username': ""});
      response_validate_user = await AuthImpl().validateUser("");
      //mp = jsonDecode(response_validate_user);
      print("usermap in auth_provider------------: $response_validate_user");
    } on PlatformException catch (e) {
      //response = "Some Error Occurred";
      print("Some Error Occurred ${e}");
    }
    notifyListeners();
  }
}
