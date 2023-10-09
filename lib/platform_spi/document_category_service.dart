
import 'package:registration_client/platform_android/document_category_impl.dart';

abstract class DocumentCategory {
  Future<List<String?>> getDocumentCategories(String categoryCode,String langCode);

  factory DocumentCategory() => getDocumentCategoryImpl();
}