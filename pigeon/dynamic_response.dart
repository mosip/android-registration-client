// ignore_for_file: public_member_api_docs, sort_constructors_first
import 'package:pigeon/pigeon.dart';

class GenericData {
  final String name;
  final String code;
  final String langCode;
  final int hierarchyLevel;

  GenericData({
    required this.name,
    required this.code,
    required this.langCode,
    required this.hierarchyLevel,
  });
}

class LanguageData {
  final String code;
  final String name;
  final String nativeName;

  LanguageData({
    required this.code,
    required this.name,
    required this.nativeName,
  });
}

@HostApi()
abstract class DynamicResponseApi {
  @async
  List<String> getFieldValues(String fieldName, String langCode);

  @async
  List<GenericData> getLocationValues(
      String hierarchyLevelName, String langCode);

  @async
  List<String> getDocumentValues(
      String categoryCode, String? applicantType, String langCode);

  @async
  List<GenericData> getLocationValuesBasedOnParent(
      String? parentCode, String hierarchyLevelName, String langCode);

  @async
  List<LanguageData> getAllLanguages();

  @async
  Map<String, String> getLocationHierarchyMap();
}
