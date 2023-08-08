import 'dart:typed_data';

import 'package:registration_client/platform_android/document_impl.dart';

abstract class Document {
  Future<void> addDocument(
      String fieldId, String docType, String reference, Uint8List bytes);
  Future<void> removeDocument(String fieldId, int pageIndex);

  Future<List<Uint8List?>> getScannedPages(String fieldId);

  Future<bool?> hasDocument(String fieldId);

  Future<bool?> removeDocumentField(String fieldId);

  factory Document() => getDocumentImpl();
}
