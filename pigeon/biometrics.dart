import 'package:pigeon/pigeon.dart';

@HostApi()
abstract class BiometricsApi {
  @async
  String invokeDiscoverSbi(String fieldId,String modality);

  @async
  List<String> getBestBiometrics(String fieldId, String modality);

  @async
  List<Uint8List> extractImageValues(String fieldId, String modality);
  

  @async
  String addBioException(String fieldId, String modality,String attribute);

  @async
  String removeBioException(String fieldId, String modality,String attribute);

}
