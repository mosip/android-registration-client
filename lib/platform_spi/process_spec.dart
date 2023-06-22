abstract class ProcessSpec {
  Future<String> getUISchema();
  Future<String> getStringValueGlobalParam(String key);
  Future<List<String?>> getNewProcessSpec();
}