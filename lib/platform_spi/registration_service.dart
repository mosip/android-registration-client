import 'package:registration_client/pigeon/registration_data_pigeon.dart';
import 'package:registration_client/platform_android/registration_service_impl.dart';

abstract class RegistrationService {
  Future<String> startRegistration(List<String> languages);
  Future<bool> evaluateMVEL(String fieldData, String expression);
  Future<String> getPreviewTemplate(bool isPreview);
  Future<RegistrationSubmitResponse> submitRegistrationDto(String makerName);

  factory RegistrationService() => getRegistrationServiceImpl();
}
