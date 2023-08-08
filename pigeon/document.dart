import 'package:pigeon/pigeon.dart';

@HostApi()
abstract class DocumentApi {
  @async
  void addDocument(String fieldId, String docType, String reference,
      Uint8List bytes); //byte[] bytes

  @async
  void removeDocument(String fieldId, int pageIndex);

  @async
  List<Uint8List> getScannedPages(String fieldId); //List<byte[]>

  @async
  bool hasDocument(String fieldId);

  @async
  void removeDocumentField(String fieldId);
}
