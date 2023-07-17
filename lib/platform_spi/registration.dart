import 'package:registration_client/platform_android/registration_impl.dart';

abstract class Registration {
  Future<String> startRegistration(List<String> languages);
  Future<bool> evaluateMVEL(String fieldData, String expression);
  Future<String> getPreviewTemplate(bool isPreview);

  factory Registration() => getRegistrationImpl();
}
