import 'dart:typed_data';

class BiometricAttributeData {
  bool isScanned;
  List<bool> exceptions;
  String exceptionType;
  int qualityPercentage;

  List<Uint8List> listofImages;
  BiometricAttributeData({
    required this.isScanned,
    required this.exceptions,
    required this.exceptionType,
    required this.qualityPercentage,
    required this.listofImages,
  });
}
