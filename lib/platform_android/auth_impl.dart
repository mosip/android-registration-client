import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:registration_client/pigeon/auth_response_pigeon.dart';
import 'package:registration_client/pigeon/user_pigeon.dart';

import 'package:registration_client/platform_spi/auth.dart';

class AuthImpl extends Auth {
  @override
  Future<User> validateUser(String username) async {
    late User user;
    try {
      user = await UserApi().validateUser(username);
    } on PlatformException {
      print('UserApi call failed');
    } catch (e) {
      print('User not fetched! ${e.toString()}');
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
      print('AuthResponseApi call failed');
    } catch (e) {
      debugPrint(e.toString());
    }
    return authResponse;
  }
}
