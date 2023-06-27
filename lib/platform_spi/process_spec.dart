import 'package:registration_client/platform_android/process_spec_impl.dart';

abstract class ProcessSpec {
  Future<String> getUISchema();
  Future<String> getStringValueGlobalParam(String key);
  Future<List<String?>> getNewProcessSpec();

  factory ProcessSpec() => getProcessSpecImpl();
}