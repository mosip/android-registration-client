import 'package:flutter/material.dart';
import 'package:registration_client/model/biometric_attribute_data.dart';
import 'package:registration_client/model/biometrics_dto.dart';

class BiometricCaptureControlProvider with ChangeNotifier {
  //Variables
  bool _isBiometricCaptureControl = false;
  late String _biometricAttribute;
  String _biometricAttributePortrait = "";
  int _biometricCaptureScanBlockTabIndex = 1;
  BiometricAttributeData _iris = BiometricAttributeData(
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
  BiometricAttributeData _rightHand = BiometricAttributeData(
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
  BiometricAttributeData _leftHand = BiometricAttributeData(
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
  BiometricAttributeData _thumbs = BiometricAttributeData(
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
  BiometricAttributeData _face = BiometricAttributeData(
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
  BiometricAttributeData _exception = BiometricAttributeData(
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

  //Getters and Setters
  bool get isBiometricCaptureControl => _isBiometricCaptureControl;

  set isBiometricCaptureControl(bool value) {
    _isBiometricCaptureControl = value;
    // //notifyListeners();
  }

  int get biometricCaptureScanBlockTabIndex =>
      _biometricCaptureScanBlockTabIndex;
  set biometricCaptureScanBlockTabIndex(int value) {
    _biometricCaptureScanBlockTabIndex = value;
  }

  String get biometricAttribute => _biometricAttribute;

  set biometricAttribute(String value) {
    _biometricAttribute = value;
    // //notifyListeners();
  }

  String get biometricAttributePortrait => _biometricAttributePortrait;
  set biometricAttributePortrait(String value) {
    _biometricAttributePortrait = value;
    // //notifyListeners();
  }

  BiometricAttributeData get iris => _iris;
  BiometricAttributeData get rightHand => _rightHand;
  BiometricAttributeData get leftHand => _leftHand;
  BiometricAttributeData get thumbs => _thumbs;
  BiometricAttributeData get face => _face;
  BiometricAttributeData get exception => _exception;

  set iris(BiometricAttributeData value) {
    _iris = value;
    // //notifyListeners();
  }

  set rightHand(BiometricAttributeData value) {
    _rightHand = value;
    // //notifyListeners();
  }

  set leftHand(BiometricAttributeData value) {
    _leftHand = value;
    // //notifyListeners();
  }

  set thumbs(BiometricAttributeData value) {
    _thumbs = value;
    // //notifyListeners();
  }

  set face(BiometricAttributeData value) {
    _face = value;
    // //notifyListeners();
  }

  set exception(BiometricAttributeData value) {
    _exception = value;
    //notifyListeners();
  }

  customSetterIris(dynamic value, String valueTitle) {
    if (valueTitle == "title") {
      _iris.title = value;
    }
    if (valueTitle == "attemptNo") {
      _iris.attemptNo = value;
    }
    if (valueTitle == "exceptionType") {
      _iris.exceptionType = value;
    }
    if (valueTitle == "exceptions") {
      _iris.exceptions = value;
    }
    if (valueTitle == "isScanned") {
      _iris.isScanned = value;
    }
    if (valueTitle == "listofImages") {
      _iris.listofImages = value;
    }
    if (valueTitle == "listOfBiometricsDto") {
      _iris.listOfBiometricsDto = value;
    }
    if (valueTitle == "qualityPercentage") {
      _iris.qualityPercentage = value.toDouble();
    }
    if (valueTitle == "thresholdPercentage") {
      _iris.thresholdPercentage = value;
    }
    if (valueTitle == "noOfCapturesAllowed") {
      _iris.noOfCapturesAllowed = value;
    }
    //notifyListeners();
  }

  customSetterRightHand(dynamic value, String valueTitle) {
    if (valueTitle == "title") {
      _rightHand.title = value;
    }
    if (valueTitle == "attemptNo") {
      _rightHand.attemptNo = value;
    }
    if (valueTitle == "exceptionType") {
      _rightHand.exceptionType = value;
    }
    if (valueTitle == "exceptions") {
      _rightHand.exceptions = value;
    }
    if (valueTitle == "isScanned") {
      _rightHand.isScanned = value;
    }
    if (valueTitle == "listofImages") {
      _rightHand.listofImages = value;
    }
    if (valueTitle == "listOfBiometricsDto") {
      _rightHand.listOfBiometricsDto = value;
    }
    if (valueTitle == "qualityPercentage") {
      _rightHand.qualityPercentage = value.toDouble();
    }
    if (valueTitle == "thresholdPercentage") {
      _rightHand.thresholdPercentage = value;
    }
    if (valueTitle == "noOfCapturesAllowed") {
      _rightHand.noOfCapturesAllowed = value;
    }
    //notifyListeners();
  }

  customSetterLeftHand(dynamic value, String valueTitle) {
    if (valueTitle == "title") {
      _leftHand.title = value;
    }
    if (valueTitle == "attemptNo") {
      _leftHand.attemptNo = value;
    }
    if (valueTitle == "exceptionType") {
      _leftHand.exceptionType = value;
    }
    if (valueTitle == "exceptions") {
      _leftHand.exceptions = value;
    }
    if (valueTitle == "isScanned") {
      _leftHand.isScanned = value;
    }
    if (valueTitle == "listofImages") {
      _leftHand.listofImages = value;
    }
    if (valueTitle == "listOfBiometricsDto") {
      _leftHand.listOfBiometricsDto = value;
    }
    if (valueTitle == "qualityPercentage") {
      _leftHand.qualityPercentage = value.toDouble();
    }
    if (valueTitle == "thresholdPercentage") {
      _leftHand.thresholdPercentage = value;
    }
    if (valueTitle == "noOfCapturesAllowed") {
      _leftHand.noOfCapturesAllowed = value;
    }
    //notifyListeners();
  }

  customSetterThumbs(dynamic value, String valueTitle) {
    if (valueTitle == "title") {
      _thumbs.title = value;
    }
    if (valueTitle == "attemptNo") {
      _thumbs.attemptNo = value;
    }
    if (valueTitle == "exceptionType") {
      _thumbs.exceptionType = value;
    }
    if (valueTitle == "exceptions") {
      _thumbs.exceptions = value;
    }
    if (valueTitle == "isScanned") {
      _thumbs.isScanned = value;
    }
    if (valueTitle == "listofImages") {
      _thumbs.listofImages = value;
    }
    if (valueTitle == "listOfBiometricsDto") {
      _thumbs.listOfBiometricsDto = value;
    }
    if (valueTitle == "qualityPercentage") {
      _thumbs.qualityPercentage = value.toDouble();
    }
    if (valueTitle == "thresholdPercentage") {
      _thumbs.thresholdPercentage = value;
    }
    if (valueTitle == "noOfCapturesAllowed") {
      _thumbs.noOfCapturesAllowed = value;
    }
    //notifyListeners();
  }

  customSetterFace(dynamic value, String valueTitle) {
    if (valueTitle == "title") {
      _face.title = value;
    }
    if (valueTitle == "attemptNo") {
      _face.attemptNo = value;
    }
    if (valueTitle == "exceptionType") {
      _face.exceptionType = value;
    }
    if (valueTitle == "exceptions") {
      _face.exceptions = value;
    }
    if (valueTitle == "isScanned") {
      _face.isScanned = value;
    }
    if (valueTitle == "listofImages") {
      _face.listofImages = value;
    }
    if (valueTitle == "listOfBiometricsDto") {
      _face.listOfBiometricsDto = value;
    }
    if (valueTitle == "qualityPercentage") {
      _face.qualityPercentage = value.toDouble();
    }
    if (valueTitle == "thresholdPercentage") {
      _face.thresholdPercentage = value;
    }
    if (valueTitle == "noOfCapturesAllowed") {
      _face.noOfCapturesAllowed = value;
    }
    //notifyListeners();
  }

  customSetterException(dynamic value, String valueTitle) {
    if (valueTitle == "title") {
      _exception.title = value;
    }
    if (valueTitle == "attemptNo") {
      _exception.attemptNo = value;
    }
    if (valueTitle == "exceptionType") {
      _exception.exceptionType = value;
    }
    if (valueTitle == "exceptions") {
      _exception.exceptions = value;
    }
    if (valueTitle == "isScanned") {
      _exception.isScanned = value;
    }
    if (valueTitle == "listofImages") {
      _exception.listofImages = value;
    }
    if (valueTitle == "listOfBiometricsDto") {
      _exception.listOfBiometricsDto = value;
    }
    if (valueTitle == "qualityPercentage") {
      _exception.qualityPercentage = value.toDouble();
    }
    if (valueTitle == "thresholdPercentage") {
      _exception.thresholdPercentage = value;
    }
    if (valueTitle == "noOfCapturesAllowed") {
      _exception.noOfCapturesAllowed = value;
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
