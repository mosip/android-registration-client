import 'package:pigeon/pigeon.dart';

@HostApi()
abstract class CommonApi {
  @async
  String getTemplateContent(String templateName, String langCode);
  @async
  String getPreviewTemplateContent(String templateTypeCode, String langCode);
  @async
  List<String> getDocumentTypes(String categoryCode, String applicantType, String langCode);
  @async
  List<String> getFieldValues(String fieldName, String langCode);
}
