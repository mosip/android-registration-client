import 'package:registration_client/pigeon/registration_data_pigeon.dart';
import 'package:registration_client/platform_android/registration_impl.dart';

abstract class Registration {
  Future<String> startRegistration(List<String> languages);
  Future<bool> evaluateMVEL(String fieldData, String expression);
  Future<String> getPreviewTemplate(bool isPreview);
  Future<RegistrationSubmitResponse> submitRegistrationDto(String makerName);

  factory Registration() => getRegistrationImpl();
}
