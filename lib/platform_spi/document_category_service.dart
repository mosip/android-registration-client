import 'package:registration_client/platform_android/document_category_impl.dart';

import '../pigeon/document_category_pigeon.dart';

abstract class DocumentCategory {
  Future<List<DocumentType?>> getDocumentCategories(String categoryCode, String langCode, List<String> languages);

  Future<String> getDocumentSize();

  factory DocumentCategory() => getDocumentCategoryImpl();
}