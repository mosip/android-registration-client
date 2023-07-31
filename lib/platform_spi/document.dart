import 'package:registration_client/platform_android/document_impl.dart';

abstract class Document {
  Future<void> addDocument(
      String fieldId, String docType, String reference, List<String> bytes);
  Future<void> removeDocument(String fieldId, int pageIndex);

  Future<List<String?>> getScannedPages(String fieldId);

  Future<bool?> hasDocument(String fieldId);

  Future<bool?> removeDocumentField(String fieldId);

  factory Document() => getDocumentImpl();
}
