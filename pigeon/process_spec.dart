import 'package:pigeon/pigeon.dart';

@HostApi()
abstract class ProcessSpecApi {
  @async
  String getUISchema();

  @async
  String getStringValueGlobalParam(String key);

  @async
  List<String?> getNewProcessSpec();

  @async
  String startRegistration(List<String> languages);
}