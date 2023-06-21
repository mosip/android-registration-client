import 'package:registration_client/pigeon/user_pigeon.dart';

abstract class Auth {
  Future<User> validateUser(String username);
  Future<User> logoutUser();
}
