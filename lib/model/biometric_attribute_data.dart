import 'package:registration_client/model/biometrics_dto.dart';

class BiometricAttributeData {
  String title;
  String viewTitle;
  int attemptNo;
  bool isScanned;
  List<bool> exceptions;
  String exceptionType;
  String thresholdPercentage;
  double qualityPercentage;
  List<BiometricsDto> listOfBiometricsDto;
  List<dynamic> listofImages;
  int noOfCapturesAllowed;
  BiometricAttributeData({
    required this.title,
    required this.viewTitle,
    required this.attemptNo,
    required this.isScanned,
    required this.exceptions,
    required this.exceptionType,
    required this.qualityPercentage,
    required this.listofImages,
    required this.listOfBiometricsDto,
    required this.thresholdPercentage,
    required this.noOfCapturesAllowed,
  });
}
