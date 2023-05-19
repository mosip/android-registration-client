// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'login_response.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

LoginResponse _$LoginResponseFromJson(Map<String, dynamic> json) =>
    LoginResponse(
      login_response: json['login_response'] as String,
      isLoggedIn: json['isLoggedIn'] as bool,
      error_code: json['error_code'] as String,
    );

Map<String, dynamic> _$LoginResponseToJson(LoginResponse instance) =>
    <String, dynamic>{
      'login_response': instance.login_response,
      'isLoggedIn': instance.isLoggedIn,
      'error_code': instance.error_code,
    };
