import 'package:registration_client/pigeons/user_pigeon.dart';

abstract class Auth {
  Future<User> validateUser(String username);
}