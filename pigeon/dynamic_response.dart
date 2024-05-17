// ignore_for_file: public_member_api_docs, sort_constructors_first
import 'package:pigeon/pigeon.dart';

class GenericData {
  final String name;
  final String code;
  final String langCode;
  final int? hierarchyLevel;
  final String? concatenatedName;

  GenericData({
    required this.name,
    required this.code,
    required this.langCode,
    this.hierarchyLevel,
    this.concatenatedName,
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

class DynamicFieldData{
  final String code;
  final String name;
  final String concatenatedName;

  DynamicFieldData({
    required this.code,
    required this.name,
    required this.concatenatedName,
  });
}

@HostApi()
abstract class DynamicResponseApi {
  @async
  List<DynamicFieldData> getFieldValues(String fieldName, String langCode, List<String> language);

  @async
  List<GenericData> getLocationValues(
      String hierarchyLevelName, String langCode);

  @async
  List<String> getDocumentValues(
      String categoryCode, String? applicantType, String langCode);

  @async
  List<GenericData> getLocationValuesBasedOnParent(
      String? parentCode, String hierarchyLevelName, String langCode, List<String> languages);

  @async
  List<LanguageData> getAllLanguages();

  @async
  Map<String, String> getLocationHierarchyMap();
}
