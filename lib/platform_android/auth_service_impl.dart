/*
 * Copyright (c) Modular Open Source Identity Platform
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *
*/

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:registration_client/pigeon/auth_response_pigeon.dart';
import 'package:registration_client/pigeon/packet_auth_pigeon.dart';
import 'package:registration_client/pigeon/user_pigeon.dart';

import 'package:registration_client/platform_spi/auth_service.dart';

class AuthServiceImpl implements AuthService {
  @override
  Future<User> validateUser(String username, String langCode) async {
    late User user;
    try {
      user = await UserApi().validateUser(username, langCode);
    } on PlatformException {
      debugPrint('UserApi call failed');
    } catch (e) {
      debugPrint('User not fetched! ${e.toString()}');
    }
    return user;
  }

  @override
  Future<AuthResponse> login(
      String username, String password, bool isConnected) async {
    late AuthResponse authResponse;
    try {
      authResponse =
          await AuthResponseApi().login(username, password, isConnected);
    } on PlatformException {
      debugPrint('AuthResponseApi call failed');
    } catch (e) {
      debugPrint(e.toString());
    }
    return authResponse;
  }

  @override
  Future<PacketAuth> packetAuthentication(
      String username, String password) async {
    late PacketAuth packetAuth;
    try {
      packetAuth =
          await PacketAuthApi().authenticate(username, password);
    } on PlatformException {
      debugPrint('PacketAuthenticationApi call failed!');
    } catch (e) {
      debugPrint(e.toString());
    }

    return packetAuth;
  }

  @override
  Future<String> logout() async {
    late String logoutResponse;
    try {
      logoutResponse = await AuthResponseApi().logout();
    }on PlatformException {
      debugPrint('Logout Api call failed!');
    }catch (e) {
      debugPrint(e.toString());
    }

    return logoutResponse;
  }

  @override
  Future<String> stopAlarmService() async{
    late String stopAlarmServiceResponse;
    try {
      stopAlarmServiceResponse = await AuthResponseApi().stopAlarmService();
    }on PlatformException {
      debugPrint('stopAlarmService Api call failed!');
    }catch (e) {
      debugPrint(e.toString());
    }
    return stopAlarmServiceResponse;
  }

  @override
  Future<String> forgotPasswordUrl() async{
    late String forgotPasswordResponse;
    try {
      forgotPasswordResponse = await AuthResponseApi().forgotPasswordUrl();
    } on PlatformException {
      debugPrint('forgotPassword call failed!');
    } catch (e) {
      debugPrint(e.toString());
    }
    return forgotPasswordResponse;
  }

  @override
  Future<String> getIdleTime() async {
    late String idleTime;
    try {
      idleTime = await AuthResponseApi().getIdleTime();
    } on PlatformException {
      debugPrint('getIdleTime call failed!');
    } catch (e) {
      debugPrint(e.toString());
    }
    return idleTime;
  }

  @override
  Future<String> getAutoLogoutPopupTimeout() async {
    late String refreshLoginTime;
    try {
      refreshLoginTime = await AuthResponseApi().getAutoLogoutPopupTimeout();
    } on PlatformException {
      debugPrint('getIdleTime call failed!');
    } catch (e) {
      debugPrint(e.toString());
    }
    return refreshLoginTime;
  }

  @override
  Future<List<String?>> getRolesByUserId(String userId) async {
    List<String?> rolesList = [];
    try {
      rolesList = await AuthResponseApi().getRolesByUserId(userId);
    } on PlatformException {
      debugPrint('getRolesByUserId call failed!');
    } catch (e) {
      debugPrint(e.toString());
    }
    return rolesList;
  }

  @override
  Future<String> getPasswordLength() async {
    late String passwordLength;
    try {
      passwordLength = await AuthResponseApi().getPasswordLength();
    } on PlatformException {
      debugPrint('getPasswordLength call failed!');
    } catch (e) {
      debugPrint(e.toString());
    }
    return passwordLength;
  }

}

AuthService getAuthServiceImpl() => AuthServiceImpl();
