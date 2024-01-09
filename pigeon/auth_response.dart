import 'package:pigeon/pigeon.dart';

class AuthResponse {
  final String response;
  final String username;
  final bool isOfficer;
  final bool isDefault;
  final bool isSupervisor;
  final bool isOperator;
  final String? errorCode;

  AuthResponse({
    required this.response,
    required this.username,
    required this.isOfficer,
    required this.isDefault,
    required this.isSupervisor,
    required this.isOperator,
    this.errorCode, 
  });
}

@HostApi()
abstract class AuthResponseApi {
  @async
  AuthResponse login(String username, String password, bool isConnected);
}
