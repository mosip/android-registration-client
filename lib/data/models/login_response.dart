import 'package:json_annotation/json_annotation.dart';

part 'login_response.g.dart';

@JsonSerializable()
class LoginResponse {
  final String login_response;
  final bool isLoggedIn;
  final String error_code;
  final List<dynamic> roles;

  LoginResponse({
    required this.roles,
    required this.login_response,
    required this.isLoggedIn,
    required this.error_code,
  });

  factory LoginResponse.fromJson(Map<String, dynamic> json) =>
      _$LoginResponseFromJson(json);

  Map<String, dynamic> toJson() => _$LoginResponseToJson(this);
}
