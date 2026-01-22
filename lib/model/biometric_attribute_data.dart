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
  double sdkQualityPercentage;
  List<BiometricsDto> listOfBiometricsDto;
  List<dynamic> listofImages;
  int noOfCapturesAllowed;
  double imageHeightTablet;
  double imageHeightMobile;
  BiometricAttributeData({
    required this.title,
    required this.viewTitle,
    required this.imageHeightTablet,
    required this.imageHeightMobile,
    required this.attemptNo,
    required this.isScanned,
    required this.exceptions,
    required this.exceptionType,
    required this.qualityPercentage,
    this.sdkQualityPercentage = 0.0,
    required this.listofImages,
    required this.listOfBiometricsDto,
    required this.thresholdPercentage,
    required this.noOfCapturesAllowed,
  });
}
