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
  List<String> getMandatoryLanguageCodes();

  @async
  List<String> getOptionalLanguageCodes();

  @async
  int getMinLanguageCount();

  @async
  int getMaxLanguageCount(); 
}