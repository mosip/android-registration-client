import 'package:pigeon/pigeon.dart';

class RegistrationSubmitResponse {
  final String rId;
  final String? errorCode;

  RegistrationSubmitResponse({
    required this.rId,
    this.errorCode,
  });
}

@HostApi()
abstract class RegistrationDataApi {
  @async
  String startRegistration(List<String> languages);

  @async
  bool evaluateMVELVisible(String fieldData, String expression);
  
  @async
  bool evaluateMVELRequired(String fieldData, String expression);

  @async
  String getPreviewTemplate(bool isPreview, Map<String, String> templateValues);

  @async
  RegistrationSubmitResponse submitRegistrationDto(String makerName);
}
