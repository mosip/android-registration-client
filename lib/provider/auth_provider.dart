/*
 * Copyright (c) Modular Open Source Identity Platform
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *
*/

import 'package:flutter/material.dart';
import 'package:registration_client/pigeon/user_pigeon.dart';

import 'package:registration_client/platform_spi/auth_service.dart';

class AuthProvider with ChangeNotifier {
  final AuthService auth = AuthService();
  bool _isLoggedIn = false;
  bool _isSyncing = false;
  bool _isOnboarded = false;
  bool _isDefault = false;
  bool _isSupervisor = false;
  bool _isOperator = false;
  bool _isOfficer = false;
  bool _isValidUser = false;
  late User _currentUser;
  String _loginError = "";
  bool _isLoggingIn = false;
  bool _isPacketAuthenticated = false;
  String _packetError = "";
  bool _isMachineActive = false;
  bool _isCenterActive = false;
  String _userId = "";
  String _username = "";
  String _userEmail = "";

  bool get isLoggedIn => _isLoggedIn;
  bool get isSyncing => _isSyncing;
  bool get isOnboarded => _isOnboarded;
  bool get isDefault => _isDefault;
  bool get isSupervisor => _isSupervisor;
  bool get isOperator => _isOperator;
  bool get isOfficer => _isOfficer;
  bool get isValidUser => _isValidUser;
  User get currentUser => _currentUser;
  String get loginError => _loginError;
  bool get isLoggingIn => _isLoggingIn;
  bool get isPacketAuthenticated => _isPacketAuthenticated;
  bool get isMachineActive => _isMachineActive;
  bool get isCenterActive => _isCenterActive;
  String get packetError => _packetError;
  String get userId => _userId;
  String get username => _username;
  String get userEmail => _userEmail;

  setIsLoggedIn(bool value) {
    _isLoggedIn = value;
    notifyListeners();
  }

  setIsSyncing(bool value){
    _isSyncing = value;
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

  setIsOperator(bool value) {
    _isOperator = value;
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

  setIsPacketAuthenticated(bool value) {
    _isPacketAuthenticated = value;
    notifyListeners();
  }

  setIsMachineActive(bool value) {
    _isMachineActive = value;
    notifyListeners();
  }

  setIsCenterActive(bool value) {
    _isCenterActive = value;
    notifyListeners();
  }

  setPacketError(String value) {
    _packetError = value;
    notifyListeners();
  }

  setUserId(String value) {
    _userId = value;
    notifyListeners();
  }

  setUsername(String value) {
    _username = value;
    notifyListeners();
  }

  setUserEmail(String value) {
    _userEmail = value;
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

  logoutUser() async {
    await auth.logout();
    clearUser();
    notifyListeners();
  }

  validateUser(String username, String langCode) async {
    final user = await auth.validateUser(username, langCode);

    if (user.errorCode != null) {
      _isValidUser = false;
    } else {
      _isValidUser = true;
      _currentUser = user;
      _isOnboarded = user.isOnboarded;
      _isMachineActive = user.machineStatus!;
      _isCenterActive = user.centerStatus!;
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
      _userId = authResponse.userId;
      _username = authResponse.username;
      _userEmail = authResponse.userEmail;
      _isDefault = authResponse.isDefault;
      _isOfficer = authResponse.isOfficer;
      _isSupervisor = authResponse.isSupervisor;
      _isOperator = authResponse.isOperator;
      _isLoggedIn = true;
    }
    setIsLoggingIn(false);
    notifyListeners();
  }
  
  authenticatePacket(String username, String password) async {
    final packetAuth = await auth.packetAuthentication(username, password);

    if(packetAuth.errorCode != null) {
      _packetError = packetAuth.errorCode!;
      _isPacketAuthenticated = false;
    } else {
      _isPacketAuthenticated = true;
    }

    notifyListeners();
  }
}
