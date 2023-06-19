import 'package:registration_client/pigeon/auth_response_pigeon.dart';
import 'package:registration_client/pigeon/user_pigeon.dart';

abstract class Auth {
  Future<User> validateUser(String username);
  Future<AuthResponse> login(String username, String password, bool isConnected);
}