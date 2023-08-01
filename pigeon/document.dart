import 'package:pigeon/pigeon.dart';

@HostApi()
abstract class DocumentApi {
  @async
  void addDocument(String fieldId, String docType, String reference,
      List<String> bytes); //byte[] bytes

  @async
  void removeDocument(String fieldId, int pageIndex);

  @async
  List<String> getScannedPages(String fieldId); //List<byte[]>

  @async
  bool hasDocument(String fieldId);

  @async
  void removeDocumentField(String fieldId);
}
