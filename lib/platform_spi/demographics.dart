import 'package:registration_client/platform_android/demographics_impl.dart';

abstract class Demographics {
  Future<void> addDemographicField(String fieldId, String value);
  Future<String> getDemographicField(String fieldId);

  Future<void> addSimpleTypeDemographicField(
      String fieldId, String value, String language);

  Future<String> getSimpleTypeDemographicField(String fieldId, String language);

  Future<void> setDateField(
      String fieldId, String subType, String day, String month, String year);

  Future<void> removeDemographicField(String fieldId);

  Future<void> setConsentField(String consentData);

  factory Demographics() => getDemographicsImpl();
}
