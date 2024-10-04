class UploadDocumentData {
  String title;//document type(drop down)
  String referenceNumber; // document reference text field (text box)

  List<dynamic> listofImages; //
  UploadDocumentData({
    required this.title,
    required this.listofImages,
    required this.referenceNumber,
  });
}
//