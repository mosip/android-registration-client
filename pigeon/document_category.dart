import 'package:pigeon/pigeon.dart';

class DocumentType {
  final String code;
  final String label;

  DocumentType({
    required this.code,
    required this.label,
  });
}

@HostApi()
abstract class DocumentCategoryApi {
  @async
  List<DocumentType> getDocumentCategories(
      String categoryCode, String langCode, List<String> languages);

  @async
  String getDocumentSize();
}