import 'package:flutter/material.dart';
import 'package:registration_client/pigeon/user_pigeon.dart';

import 'package:registration_client/platform_spi/auth.dart';

class AuthProvider with ChangeNotifier {
  final Auth auth = Auth();
  bool _isLoggedIn = false;
  bool _isOnboarded = false;
  bool _isDefault = false;
  bool _isSupervisor = false;
  bool _isOfficer = false;
  bool _isValidUser = false;
  late User _currentUser;
  String _loginError = "";
  bool _isLoggingIn = false;

  bool get isLoggedIn => _isLoggedIn;
  bool get isOnboarded => _isOnboarded;
  bool get isDefault => _isDefault;
  bool get isSupervisor => _isSupervisor;
  bool get isOfficer => _isOfficer;
  bool get isValidUser => _isValidUser;
  User get currentUser => _currentUser;
  String get loginError => _loginError;
  bool get isLoggingIn => _isLoggingIn;

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

  setLoginError(String value) {
    _loginError = value;
    notifyListeners();
  }

  setIsLoggingIn(bool value) {
    _isLoggingIn = false;
    notifyListeners();
  }

  validateUser(String username) async {
    final user = await auth.validateUser(username);

    if (user.errorCode != null) {
      _isValidUser = false;
    } else {
      _isValidUser = true;
      _currentUser = user;
      _isOnboarded = user.isOnboarded;
    }

    notifyListeners();
  }

  authenticateUser(String username, String password, bool isConnected) async {
    final authResponse = await auth.login(username, password, isConnected);

    setIsLoggingIn(true);
    if (authResponse.errorCode != null) {
      _loginError = authResponse.errorCode!;
      _isLoggedIn = false;
    } else {
      _isDefault = authResponse.isDefault;
      _isOfficer = authResponse.isOfficer;
      _isSupervisor = authResponse.isSupervisor;
      _isLoggedIn = true;
    }
    setIsLoggingIn(false);

    notifyListeners();
  }
}
