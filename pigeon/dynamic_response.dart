// ignore_for_file: public_member_api_docs, sort_constructors_first
import 'package:pigeon/pigeon.dart';

@HostApi()
abstract class DynamicResponseApi {
  @async
  List<String> getFieldValues(String fieldName, String langCode);

  @async
  List<String> getLocationValues(String hierarchyLevelName, String langCode);

  @async
  List<String> getDocumentValues(
      String categoryCode, String? applicantType, String langCode);
}
