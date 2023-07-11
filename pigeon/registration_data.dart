import 'package:pigeon/pigeon.dart';

class RegistrationData {
  final List<String?> languages;
  final String demographicsData;
  final String biometricsData;
  final String documentsData;

  RegistrationData({
    required this.languages, 
    required this.demographicsData,
    required this.biometricsData,
    required this.documentsData,
  });
}

@HostApi()
abstract class RegistrationDataApi {
  @async
  bool registration(RegistrationData registrationData);
}
