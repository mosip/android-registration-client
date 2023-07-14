import 'package:pigeon/pigeon.dart';

@HostApi()
abstract class DemographicsApi {
  @async
  void addDemographicField(String fieldId, String value);

  @async
  void addSimpleTypeDemographicField(String fieldId, String value, String language);
  
  @async 
  void setDateField(String fieldId, String subType, String day, String month, String year);

  @async
  void removeDemographicField(String fieldId);

  @async
  void setConsentField(String consentData);
}
