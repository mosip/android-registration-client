import 'package:pigeon/pigeon.dart';

class DocumentData {
  final List<Uint8List?> doc;
  final String referenceNumber;

  DocumentData({
    required this.doc,
    required this.referenceNumber,
  });
}

@HostApi()
abstract class DocumentApi {
  @async
  void addDocument(String fieldId, String docType, String reference,
      Uint8List bytes); //byte[] bytes

  @async
  void removeDocument(String fieldId, int pageIndex);

  @async
  List<DocumentData> getScannedPages(String fieldId); //List<byte[]>

  @async
  bool hasDocument(String fieldId);

  @async
  void removeDocumentField(String fieldId);
}
