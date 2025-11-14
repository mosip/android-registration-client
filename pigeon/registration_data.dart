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
  String startRegistration(
      List<String> languages, String flowType, String process);

  @async
  bool evaluateMVELVisible(String fieldData);

  @async
  bool evaluateMVELRequired(String fieldData);

  @async
  String getPreviewTemplate(bool isPreview, Map<String, String> templateValues);

  @async
  RegistrationSubmitResponse submitRegistrationDto(String makerName);

  @async
  void setApplicationId(String applicationId);

  @async
  void setAdditionalReqId(String additionalReqId);

  @async
  void setMachineLocation(double latitude, double longitude);
}
