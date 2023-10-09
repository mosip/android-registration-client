// ignore_for_file: public_member_api_docs, sort_constructors_first
import 'package:pigeon/pigeon.dart';


@HostApi()
abstract class DocumentCategoryApi {
  @async
  List<String> getDocumentCategories(String categoryCode,String langCode);
}
