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
    //late User user;
    User user = User(userId: "", errorCode: "error", isOnboarded: false);
    try {
      user = await UserApi().validateUser(username, langCode);
    }// on PlatformException {
      //   debugPrint('UserApi call failed');
      // }
    catch (e) {
      debugPrint('User not fetched! ${e.toString()}');
    }
      return user;
    }

  @override
  Future<AuthResponse> login(
      String username, String password, bool isConnected) async {
    // late AuthResponse authResponse;
    AuthResponse authResponse = AuthResponse(response: "", username: "", userId: '', isOfficer: false, userEmail: '', isDefault: false, isSupervisor: false, isOperator: false,);
    try {
      authResponse =
          await AuthResponseApi().login(username, password, isConnected);
    } //on PlatformException {
    //   debugPrint('AuthResponseApi call failed');
    // }
    catch (e) {
      debugPrint("Login failed: ${e.toString()}");
      // debugPrint(e.toString());
    }
    return authResponse;
  }

  @override
  Future<PacketAuth> packetAuthentication(
      String username, String password) async {
    PacketAuth packetAuth = PacketAuth(response: "", userId: '');
    // late PacketAuth packetAuth;
    try {
      packetAuth =
      await PacketAuthApi().authenticate(username, password);
    } //on PlatformException {
    //   debugPrint('PacketAuthenticationApi call failed!');
    // }
    catch (e) {
      debugPrint("Packet Auth failed: ${e.toString()}");
    }
    return packetAuth;
  }

  @override
  Future<String> logout() async {
    // late String logoutResponse;
    String logoutResponse = "failed"; // ✅ Safe default
    try {
      logoutResponse = await AuthResponseApi().logout();
    }//on PlatformException {
    //  debugPrint('Logout Api call failed!');
    //}
    catch (e) {
      debugPrint(e.toString());
    }
    return logoutResponse;
  }

  @override
  Future<String> stopAlarmService() async{
    // late String stopAlarmServiceResponse;
    String stopAlarmServiceResponse = "failed";
    try {
      stopAlarmServiceResponse = await AuthResponseApi().stopAlarmService();
    }//on PlatformException {
      //debugPrint('stopAlarmService Api call failed!');
   // }
    catch (e) {
      debugPrint(e.toString());
    }
    return stopAlarmServiceResponse;
  }

  @override
  Future<String> forgotPasswordUrl() async{
    String forgotPasswordResponse = "";// default safe value
    //late String forgotPasswordResponse;
    try {
      forgotPasswordResponse = await AuthResponseApi().forgotPasswordUrl();
    } //on PlatformException {
      //debugPrint('forgotPassword call failed!');
    //}
    catch (e) {
      debugPrint(e.toString());
    }
    return forgotPasswordResponse;
  }

  @override
  Future<String> getIdleTime() async {
    String idleTime = "0"; // ✅ default safe value
    try {
      idleTime = await AuthResponseApi().getIdleTime();
     }// on PlatformException {
    //   debugPrint('getIdleTime call failed!');
    // }
    catch (e) {
      debugPrint(e.toString());
    }
    return idleTime;
  }

  @override
  Future<String> getAutoLogoutPopupTimeout() async {
    String refreshLoginTime = "0";
    try {
      refreshLoginTime = await AuthResponseApi().getAutoLogoutPopupTimeout();
    } //on PlatformException {
      //debugPrint('getIdleTime call failed!');
    //}
    catch (e) {
      debugPrint(e.toString());
    }
    return refreshLoginTime;
  }

  @override
  Future<List<String?>> getRolesByUserId(String userId) async {
    List<String?> rolesList = [];
    try {
      rolesList = await AuthResponseApi().getRolesByUserId(userId);
    } //on PlatformException {
    //  debugPrint('getRolesByUserId call failed!');
    //}
    catch (e) {
      debugPrint(e.toString());
    }
    return rolesList;
  }

  // @override
//   // Future<String> getPasswordLength() async {
//   //   late String passwordLength;
//   //   try {
//   //     passwordLength = await AuthResponseApi().getPasswordLength();
//   //   } on PlatformException {
//   //     debugPrint('getPasswordLength call failed!');
//   //   } catch (e) {
//   //     debugPrint(e.toString());
//   //   }
//   //   return passwordLength;
//   // }
  @override
  Future<String> getPasswordLength() async {
    try {
      // This is currently failing because it's hitting the TUSD server
      var response = await AuthResponseApi().getPasswordLength();
      return response.toString();
    } catch (e) {
      print("DEBUG: Failed to get password length, using default: $e");
      return "8"; // Provide a default value so the app doesn't crash
    }
  }
}

AuthService getAuthServiceImpl() => AuthServiceImpl();
