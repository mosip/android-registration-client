import 'package:registration_client/pigeon/auth_response_pigeon.dart';

import 'package:registration_client/pigeon/user_pigeon.dart';
import 'package:registration_client/platform_android/auth_impl.dart';

abstract class Auth {
  Future<User> validateUser(String username);

  Future<AuthResponse> login(
      String username, String password, bool isConnected);

  factory Auth() => getAuthImpl();
}
