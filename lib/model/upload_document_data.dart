import 'dart:typed_data';

import 'package:registration_client/model/biometrics_dto.dart';

class UploadDocumentData {
  String title; //document type(drop down)

  List<dynamic> listofImages; //
  UploadDocumentData({
    required this.title,
    required this.listofImages,
  });
}
//