import 'package:pigeon/pigeon.dart';

@HostApi()
abstract class BiometricsApi {
  @async
  String invokeDiscoverSbi(String fieldId, String modality);

  @async
  List<String> getBestBiometrics(String fieldId, String modality);

  @async
  List<String> getBiometrics(String fieldId, String modality, int attempt);

  @async
  List<Uint8List> extractImageValues(String fieldId, String modality);

  @async
  List<Uint8List> extractImageValuesByAttempt(
      String fieldId, String modality, int attempt);

  @async
  int incrementBioAttempt(String fieldId, String modality);

  @async
  int getBioAttempt(String fieldId, String modality);

  @async
  String startOperatorOnboarding();

  @async
  String saveOperatorBiometrics();

  @async
  String addBioException(String fieldId, String modality, String attribute);

  @async
  String removeBioException(String fieldId, String modality, String attribute);

  @async
  String getMapValue(String key);

  @async
  String getAgeGroup();

  @async
  bool conditionalBioAttributeValidation(String fieldId, String expression);
}
