import 'dart:typed_data';

import 'package:registration_client/model/biometrics_dto.dart';

class BiometricAttributeData {
  bool isScanned;
  List<bool> exceptions;
  String exceptionType;
  double qualityPercentage;
  List<BiometricsDto> listOfBiometricsDto;
  List<dynamic> listofImages;
  BiometricAttributeData({
    required this.isScanned,
    required this.exceptions,
    required this.exceptionType,
    required this.qualityPercentage,
    required this.listofImages,
    required this.listOfBiometricsDto,
  });
}
