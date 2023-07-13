import 'package:registration_client/pigeon/auth_response_pigeon.dart';
import 'package:registration_client/pigeon/registration_data_pigeon.dart';

import 'package:registration_client/pigeon/user_pigeon.dart';
import 'package:registration_client/platform_android/auth_impl.dart';
import 'package:registration_client/platform_android/registration_impl.dart';

abstract class Registration {
  Future<bool> registerApplicant(RegistrationData registrationData);
  Future<bool> checkMVEL(String data, String expression);
  Future<String> getPreviewTemplate(String data, bool isPreview);

  factory Registration() => getRegistrationImpl();
}
