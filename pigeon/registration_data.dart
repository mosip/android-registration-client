import 'package:pigeon/pigeon.dart';

@HostApi()
abstract class RegistrationDataApi {
  @async
  String startRegistration(List<String> languages);

  @async
  bool evaluateMVEL(String fieldData, String expression);

  @async
  String getPreviewTemplate(bool isPreview);

  @async
  String submitRegistrationDto(String makerName);
}
