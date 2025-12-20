import 'package:registration_client/platform_android/document_category_impl.dart';

abstract class DocumentCategory {
  Future<List<String?>> getDocumentCategories(String categoryCode,String langCode);

  Future<String> getDocumentSize();

  factory DocumentCategory() => getDocumentCategoryImpl();
}