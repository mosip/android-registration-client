import 'package:pigeon/pigeon.dart';

@HostApi()
abstract class DemographicsApi {
  @async
  String addDemographicField(String fieldId, String value);

  @async
  String getDemographicField(String fieldId);

  @async
  String getHashValue(Uint8List bytes);

  @async
  void addSimpleTypeDemographicField(
      String fieldId, String value, String name, String language);

  @async
  String getSimpleTypeDemographicField(String fieldId, String language);

  @async
  void setDateField(
      String fieldId, String subType, String day, String month, String year);

  @async
  void removeDemographicField(String fieldId);

  @async
  void setConsentField(String consentData);
}
