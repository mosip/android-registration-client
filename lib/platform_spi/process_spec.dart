import 'package:registration_client/platform_android/process_spec_impl.dart';

abstract class ProcessSpec {
  Future<String> getUISchema();
  Future<String> getStringValueGlobalParam(String key);
  Future<List<String?>> getNewProcessSpec();
  Future<List<String?>> getMandatoryLanguageCodes();
  Future<List<String?>> getOptionalLanguageCodes();
  Future<int> getMinLanguageCount();
  Future<int> getMaxLanguageCount();

  factory ProcessSpec() => getProcessSpecImpl();
}