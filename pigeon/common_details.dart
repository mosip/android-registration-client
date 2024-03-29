import 'package:pigeon/pigeon.dart';

@HostApi()
abstract class CommonDetailsApi {
  @async
  String getTemplateContent(String templateName, String langCode);
  @async
  String getPreviewTemplateContent(String templateTypeCode, String langCode);
  @async
  List<String> getDocumentTypes(String categoryCode, String applicantType, String langCode);
  @async
  List<String> getFieldValues(String fieldName, String langCode);
  @async
  String saveVersionToGlobalParam(String id, String value);
  @async
  String getVersionFromGlobalParam(String id);
  @async
  String saveScreenHeaderToGlobalParam(String id, String value);
}
