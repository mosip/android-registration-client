import 'package:flutter/material.dart';
import 'package:registration_client/model/biometric_attribute_data.dart';
import 'package:registration_client/model/biometrics_dto.dart';

class BiometricCaptureControlProvider with ChangeNotifier {
  //Variables
  bool isBiometricCaptureControl = false;
  late String biometricAttribute;
  String biometricAttributePortrait = "";
  int biometricCaptureScanBlockTabIndex = 1;
  BiometricAttributeData iris = BiometricAttributeData(
      title: "Iris",
      attemptNo: 0,
      exceptionType: "",
      exceptions: [false, false],
      isScanned: false,
      listofImages: [
        "assets/images/Left Eye@2x.png",
        "assets/images/Right Eye@2x.png"
      ],
      listOfBiometricsDto: [],
      qualityPercentage: 0,
      thresholdPercentage: "0",
      noOfCapturesAllowed: 0);
  BiometricAttributeData rightHand = BiometricAttributeData(
      title: "Right Hand",
      attemptNo: 0,
      exceptionType: "",
      exceptions: [false, false, false, false],
      isScanned: false,
      listofImages: ["assets/images/Right Hand@2x.png"],
      listOfBiometricsDto: [],
      qualityPercentage: 0,
      thresholdPercentage: "0",
      noOfCapturesAllowed: 0);
  BiometricAttributeData leftHand = BiometricAttributeData(
      title: "Left Hand",
      attemptNo: 0,
      exceptionType: "",
      exceptions: [false, false, false, false],
      isScanned: false,
      listofImages: ["assets/images/Left Hand@2x.png"],
      listOfBiometricsDto: [],
      qualityPercentage: 0,
      thresholdPercentage: "0",
      noOfCapturesAllowed: 0);
  BiometricAttributeData thumbs = BiometricAttributeData(
      title: "Thumbs",
      attemptNo: 0,
      exceptionType: "",
      exceptions: [false, false],
      isScanned: false,
      listofImages: ["assets/images/Thumbs@2x.png"],
      listOfBiometricsDto: [],
      qualityPercentage: 0,
      thresholdPercentage: "0",
      noOfCapturesAllowed: 0);
  BiometricAttributeData face = BiometricAttributeData(
      title: "Face",
      attemptNo: 0,
      exceptionType: "",
      exceptions: [false],
      isScanned: false,
      listofImages: ["assets/images/Face@2x.png"],
      listOfBiometricsDto: [],
      qualityPercentage: 0,
      thresholdPercentage: "0",
      noOfCapturesAllowed: 0);
  BiometricAttributeData exception = BiometricAttributeData(
      title: "Exception",
      attemptNo: 0,
      exceptionType: "",
      exceptions: [false],
      isScanned: false,
      listofImages: ["assets/images/Exception@2x.png"],
      listOfBiometricsDto: [],
      qualityPercentage: 0,
      thresholdPercentage: "0",
      noOfCapturesAllowed: 3);

  customSetterIris(dynamic value, String valueTitle) {
    if (valueTitle == "title") {
      iris.title = value;
    }
    if (valueTitle == "attemptNo") {
      iris.attemptNo = value;
    }
    if (valueTitle == "exceptionType") {
      iris.exceptionType = value;
    }
    if (valueTitle == "exceptions") {
      iris.exceptions = value;
    }
    if (valueTitle == "isScanned") {
      iris.isScanned = value;
    }
    if (valueTitle == "listofImages") {
      iris.listofImages = value;
    }
    if (valueTitle == "listOfBiometricsDto") {
      iris.listOfBiometricsDto = value;
    }
    if (valueTitle == "qualityPercentage") {
      iris.qualityPercentage = value.toDouble();
    }
    if (valueTitle == "thresholdPercentage") {
      iris.thresholdPercentage = value;
    }
    if (valueTitle == "noOfCapturesAllowed") {
      iris.noOfCapturesAllowed = value;
    }
    //notifyListeners();
  }

  customSetterRightHand(dynamic value, String valueTitle) {
    if (valueTitle == "title") {
      rightHand.title = value;
    }
    if (valueTitle == "attemptNo") {
      rightHand.attemptNo = value;
    }
    if (valueTitle == "exceptionType") {
      rightHand.exceptionType = value;
    }
    if (valueTitle == "exceptions") {
      rightHand.exceptions = value;
    }
    if (valueTitle == "isScanned") {
      rightHand.isScanned = value;
    }
    if (valueTitle == "listofImages") {
      rightHand.listofImages = value;
    }
    if (valueTitle == "listOfBiometricsDto") {
      rightHand.listOfBiometricsDto = value;
    }
    if (valueTitle == "qualityPercentage") {
      rightHand.qualityPercentage = value.toDouble();
    }
    if (valueTitle == "thresholdPercentage") {
      rightHand.thresholdPercentage = value;
    }
    if (valueTitle == "noOfCapturesAllowed") {
      rightHand.noOfCapturesAllowed = value;
    }
    //notifyListeners();
  }

  customSetterLeftHand(dynamic value, String valueTitle) {
    if (valueTitle == "title") {
      leftHand.title = value;
    }
    if (valueTitle == "attemptNo") {
      leftHand.attemptNo = value;
    }
    if (valueTitle == "exceptionType") {
      leftHand.exceptionType = value;
    }
    if (valueTitle == "exceptions") {
      leftHand.exceptions = value;
    }
    if (valueTitle == "isScanned") {
      leftHand.isScanned = value;
    }
    if (valueTitle == "listofImages") {
      leftHand.listofImages = value;
    }
    if (valueTitle == "listOfBiometricsDto") {
      leftHand.listOfBiometricsDto = value;
    }
    if (valueTitle == "qualityPercentage") {
      leftHand.qualityPercentage = value.toDouble();
    }
    if (valueTitle == "thresholdPercentage") {
      leftHand.thresholdPercentage = value;
    }
    if (valueTitle == "noOfCapturesAllowed") {
      leftHand.noOfCapturesAllowed = value;
    }
    //notifyListeners();
  }

  customSetterThumbs(dynamic value, String valueTitle) {
    if (valueTitle == "title") {
      thumbs.title = value;
    }
    if (valueTitle == "attemptNo") {
      thumbs.attemptNo = value;
    }
    if (valueTitle == "exceptionType") {
      thumbs.exceptionType = value;
    }
    if (valueTitle == "exceptions") {
      thumbs.exceptions = value;
    }
    if (valueTitle == "isScanned") {
      thumbs.isScanned = value;
    }
    if (valueTitle == "listofImages") {
      thumbs.listofImages = value;
    }
    if (valueTitle == "listOfBiometricsDto") {
      thumbs.listOfBiometricsDto = value;
    }
    if (valueTitle == "qualityPercentage") {
      thumbs.qualityPercentage = value.toDouble();
    }
    if (valueTitle == "thresholdPercentage") {
      thumbs.thresholdPercentage = value;
    }
    if (valueTitle == "noOfCapturesAllowed") {
      thumbs.noOfCapturesAllowed = value;
    }
    //notifyListeners();
  }

  customSetterFace(dynamic value, String valueTitle) {
    if (valueTitle == "title") {
      face.title = value;
    }
    if (valueTitle == "attemptNo") {
      face.attemptNo = value;
    }
    if (valueTitle == "exceptionType") {
      face.exceptionType = value;
    }
    if (valueTitle == "exceptions") {
      face.exceptions = value;
    }
    if (valueTitle == "isScanned") {
      face.isScanned = value;
    }
    if (valueTitle == "listofImages") {
      face.listofImages = value;
    }
    if (valueTitle == "listOfBiometricsDto") {
      face.listOfBiometricsDto = value;
    }
    if (valueTitle == "qualityPercentage") {
      face.qualityPercentage = value.toDouble();
    }
    if (valueTitle == "thresholdPercentage") {
      face.thresholdPercentage = value;
    }
    if (valueTitle == "noOfCapturesAllowed") {
      face.noOfCapturesAllowed = value;
    }
    //notifyListeners();
  }

  customSetterException(dynamic value, String valueTitle) {
    if (valueTitle == "title") {
      exception.title = value;
    }
    if (valueTitle == "attemptNo") {
      exception.attemptNo = value;
    }
    if (valueTitle == "exceptionType") {
      exception.exceptionType = value;
    }
    if (valueTitle == "exceptions") {
      exception.exceptions = value;
    }
    if (valueTitle == "isScanned") {
      exception.isScanned = value;
    }
    if (valueTitle == "listofImages") {
      exception.listofImages = value;
    }
    if (valueTitle == "listOfBiometricsDto") {
      exception.listOfBiometricsDto = value;
    }
    if (valueTitle == "qualityPercentage") {
      exception.qualityPercentage = value.toDouble();
    }
    if (valueTitle == "thresholdPercentage") {
      exception.thresholdPercentage = value;
    }
    if (valueTitle == "noOfCapturesAllowed") {
      exception.noOfCapturesAllowed = value;
    }
    //notifyListeners();
  }

  //Functions
  getElementPosition(List<BiometricAttributeData> list, String title) {
    for (int i = 0; i < list.length; i++) {
      if (list.elementAt(i).title.compareTo(title) == 0) {
        return i;
      }
    }
    return -1;
  }

  avgScore(List<BiometricsDto> list) {
    double avg = 0;
    int i;
    for (i = 0; i < list.length; i++) {
      avg = avg + list[i].qualityScore!;
    }
    avg = avg / i;
    return avg;
  }

  int returnNoOfAttributes(List<String?> attributes) {
    int count = 0;
    if (attributes.contains("leftEye") && attributes.contains("rightEye")) {
      count++;
    }
    if (attributes.contains("rightIndex") &&
        attributes.contains("rightLittle") &&
        attributes.contains("rightRing") &&
        attributes.contains("rightMiddle")) {
      count++;
    }
    if (attributes.contains("leftIndex") &&
        attributes.contains("leftLittle") &&
        attributes.contains("leftRing") &&
        attributes.contains("leftMiddle")) {
      count++;
    }
    if (attributes.contains("leftThumb") && attributes.contains("rightThumb")) {
      count++;
    }
    if (attributes.contains("face")) {
      count++;
    }

    return count;
  }
}
