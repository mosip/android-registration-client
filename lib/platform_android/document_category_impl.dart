import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:registration_client/pigeon/document_category_pigeon.dart';
import 'package:registration_client/platform_spi/document_category_service.dart';

class DocumentCategoryImpl implements DocumentCategory {
  @override
  Future<List<String?>> getDocumentCategories(String categoryCode, String langCode) async{

    List<String?> documentValuesList = [];
    try {
      documentValuesList = await DocumentCategoryApi()
          .getDocumentCategories(categoryCode,langCode);
    } on PlatformException {
      debugPrint('DynamicServiceResponseApi call failed!');
    } catch (e) {
      debugPrint('Document Values not fetched! ${e.toString()}');
    }
    return documentValuesList;
  }

}
DocumentCategory getDocumentCategoryImpl() => DocumentCategoryImpl();