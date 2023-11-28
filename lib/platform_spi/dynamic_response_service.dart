import 'package:registration_client/pigeon/dynamic_response_pigeon.dart';
import 'package:registration_client/platform_android/dynamic_response_service_impl.dart';

abstract class DynamicResponseService {
  Future<List<String?>> fetchFieldValues(String fieldName, String langCode);

  Future<List<GenericData?>> fetchLocationValues(
      String hierarchyLevelName, String langCode);

  Future<List<String?>> fetchDocumentValues(
      String categoryCode, String? applicantType, String langCode);

  Future<List<GenericData?>> fetchLocationValuesBasedOnParent(
      String? parentCode, String hierarchyLevelName, String langCode);

  Future<List<LanguageData?>> fetchAllLanguages();

  Future<Map<String?, String?>> fetchLocationHierarchyMap();

  factory DynamicResponseService() => getDynamicResponseServiceImpl();
}
