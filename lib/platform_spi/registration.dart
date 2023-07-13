import 'package:registration_client/platform_android/registration_impl.dart';

abstract class Registration {
  Future<bool> startRegistration(List<String> languages);
  Future<bool> checkMVEL(String data, String expression);
  Future<String> getPreviewTemplate(String data, bool isPreview);

  factory Registration() => getRegistrationImpl();
}
