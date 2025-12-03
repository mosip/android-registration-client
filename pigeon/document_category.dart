import 'package:pigeon/pigeon.dart';


@HostApi()
abstract class DocumentCategoryApi {
  @async
  List<String> getDocumentCategories(String categoryCode,String langCode);

  @async
  String getDocumentSize();
}