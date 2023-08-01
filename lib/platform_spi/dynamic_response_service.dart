import 'package:registration_client/platform_android/dynamic_response_service_impl.dart';

abstract class DynamicResponseService {
  Future<List<String?>> fetchFieldValues(String fieldName, String langCode);

  Future<List<String?>> fetchLocationValues(
      String hierarchyLevelName, String langCode);
  Future<List<String?>> fetchDocumentValues(
      String categoryCode, String? applicantType, String langCode);

  factory DynamicResponseService() => getDynamicResponseServiceImpl();
}
