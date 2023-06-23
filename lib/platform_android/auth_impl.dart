import 'package:flutter/services.dart';
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
    } catch(e) {
      print('User not fetched! ${e.toString()}');
    }
    return user;

  }

  @override
  Future<User> logoutUser() async {
    late User user;
    try {
      user = await UserApi().logoutUser();
    } on PlatformException {
      print('UserApi logout call failed');
    } catch(e) {
      print('User not fetched! ${e.toString()}');
    }
    return user;
  }
}