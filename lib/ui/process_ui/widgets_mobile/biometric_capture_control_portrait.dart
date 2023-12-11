import 'package:flutter/material.dart';

import 'package:flutter_screenutil/flutter_screenutil.dart';

import 'package:provider/provider.dart';
import 'package:registration_client/model/biometric_attribute_data.dart';

import 'package:registration_client/model/field.dart';
import 'package:registration_client/pigeon/biometrics_pigeon.dart';

import 'package:registration_client/provider/global_provider.dart';

import 'package:registration_client/utils/app_config.dart';
import 'package:responsive_grid_list/responsive_grid_list.dart';

class BiometricCaptureControlPortrait extends StatefulWidget {
  const BiometricCaptureControlPortrait({super.key, required this.field});
  final Field field;

  @override
  State<BiometricCaptureControlPortrait> createState() =>
      _BiometricCaptureControlPortraitState();
}

class _BiometricCaptureControlPortraitState
    extends State<BiometricCaptureControlPortrait> {
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

  getElementPosition(List<BiometricAttributeData> list, String title) {
    for (int i = 0; i < list.length; i++) {
      if (list.elementAt(i).title.compareTo(title) == 0) {
        return i;
      }
    }
    return -1;
  }

  resetAfterException(String key, BiometricAttributeData data) {
    if (context.read<GlobalProvider>().fieldInputValue.containsKey(key)) {
      if (getElementPosition(
              context.read<GlobalProvider>().fieldInputValue[key],
              data.title) !=
          -1) {
        context.read<GlobalProvider>().fieldInputValue[key].removeAt(
            getElementPosition(
                context.read<GlobalProvider>().fieldInputValue[key],
                data.title));
      }
    }
  }

  int i = 0;
  String biometricAttribute = "Iris";
  setInitialBioAttribute() async {
    if (widget.field.conditionalBioAttributes!.first!.ageGroup!
            .compareTo(context.read<GlobalProvider>().ageGroup) ==
        0) {
      if (widget.field.conditionalBioAttributes!.first!.bioAttributes!
              .contains("leftEye") &&
          widget.field.conditionalBioAttributes!.first!.bioAttributes!
              .contains("rightEye")) {
        biometricAttribute = "Iris";
      } else if (widget.field.conditionalBioAttributes!.first!.bioAttributes!
              .contains("rightIndex") &&
          widget.field.conditionalBioAttributes!.first!.bioAttributes!
              .contains("rightLittle") &&
          widget.field.conditionalBioAttributes!.first!.bioAttributes!
              .contains("rightRing") &&
          widget.field.conditionalBioAttributes!.first!.bioAttributes!
              .contains("rightMiddle")) {
        biometricAttribute = "Right Hand";
      } else if (widget.field.conditionalBioAttributes!.first!.bioAttributes!
              .contains("leftIndex") &&
          widget.field.conditionalBioAttributes!.first!.bioAttributes!
              .contains("leftLittle") &&
          widget.field.conditionalBioAttributes!.first!.bioAttributes!
              .contains("leftRing") &&
          widget.field.conditionalBioAttributes!.first!.bioAttributes!
              .contains("leftMiddle")) {
        biometricAttribute = "Left Hand";
      } else if (widget.field.conditionalBioAttributes!.first!.bioAttributes!
              .contains("leftThumb") &&
          widget.field.conditionalBioAttributes!.first!.bioAttributes!
              .contains("rightThumb")) {
        biometricAttribute = "Thumbs";
      } else {
        biometricAttribute = "Face";
      }
    } else {
      if (widget.field.bioAttributes!.contains("leftEye") &&
          widget.field.bioAttributes!.contains("rightEye")) {
        biometricAttribute = "Iris";
      } else if (widget.field.bioAttributes!.contains("rightIndex") &&
          widget.field.bioAttributes!.contains("rightLittle") &&
          widget.field.bioAttributes!.contains("rightRing") &&
          widget.field.bioAttributes!.contains("rightMiddle")) {
        biometricAttribute = "Right Hand";
      } else if (widget.field.bioAttributes!.contains("leftIndex") &&
          widget.field.bioAttributes!.contains("leftLittle") &&
          widget.field.bioAttributes!.contains("leftRing") &&
          widget.field.bioAttributes!.contains("leftMiddle")) {
        biometricAttribute = "Left Hand";
      } else if (widget.field.bioAttributes!.contains("leftThumb") &&
          widget.field.bioAttributes!.contains("rightThumb")) {
        biometricAttribute = "Thumbs";
      } else {
        biometricAttribute = "Face";
      }
    }
    iris.noOfCapturesAllowed = int.parse(await BiometricsApi()
        .getThresholdValue("mosip.registration.num_of_iris_retries"));
    leftHand.noOfCapturesAllowed = int.parse(await BiometricsApi()
        .getThresholdValue("mosip.registration.num_of_fingerprint_retries"));
    rightHand.noOfCapturesAllowed = int.parse(await BiometricsApi()
        .getThresholdValue("mosip.registration.num_of_fingerprint_retries"));
    thumbs.noOfCapturesAllowed = int.parse(await BiometricsApi()
        .getThresholdValue("mosip.registration.num_of_fingerprint_retries"));
    face.noOfCapturesAllowed = int.parse(await BiometricsApi()
        .getThresholdValue("mosip.registration.num_of_face_retries"));
  }

  Widget _getBiometricCaptureSelectionBlockMobile(
      BiometricAttributeData biometricAttributeData) {
    return InkWell(
        onTap: () {
          setState(() {
            biometricAttribute = biometricAttributeData.title;
          });
        },
        child: Stack(
          children: [
            Container(
              height: 335.h,
              width: 372.h,
              decoration: BoxDecoration(
                  color: pureWhite,
                  border: Border.all(
                      color: (biometricAttributeData.isScanned == true)
                          ? (biometricAttributeData.exceptions.contains(true))
                              ? secondaryColors.elementAt(16)
                              : secondaryColors.elementAt(11)
                          : (biometricAttribute == biometricAttributeData.title)
                              ? secondaryColors.elementAt(12)
                              : secondaryColors.elementAt(14)),
                  borderRadius: BorderRadius.circular(10)),
              child: Column(
                mainAxisAlignment: MainAxisAlignment.center,
                children: [
                  Image.asset(
                    "assets/images/${biometricAttributeData.title}@2x.png",
                    height: 200.h,
                    width: 200.h,
                  ),
                  SizedBox(
                    height: 10.h,
                  ),
                  Text(
                    "${biometricAttributeData.title} Scan",
                    style: TextStyle(
                      fontSize: 28,
                      fontWeight: semiBold,
                      color: blackShade1,
                    ),
                  )
                ],
              ),
            ),
          ],
        ));
  }

  @override
  Widget build(BuildContext context) {
    if (i <= 0) {
      setInitialBioAttribute();
      i++;
    }
    leftHand.thresholdPercentage =
        context.read<GlobalProvider>().thresholdValuesMap[
            "mosip.registration.leftslap_fingerprint_threshold"]!;
    rightHand.thresholdPercentage =
        context.read<GlobalProvider>().thresholdValuesMap[
            "mosip.registration.rightslap_fingerprint_threshold"]!;
    iris.thresholdPercentage = context
        .read<GlobalProvider>()
        .thresholdValuesMap["mosip.registration.iris_threshold"]!;
    thumbs.thresholdPercentage = context
        .read<GlobalProvider>()
        .thresholdValuesMap["mosip.registration.thumbs_fingerprint_threshold"]!;
    face.thresholdPercentage = context
        .read<GlobalProvider>()
        .thresholdValuesMap["mosip.registration.face_threshold"]!;
    if (context
        .read<GlobalProvider>()
        .fieldInputValue
        .containsKey("${widget.field.id}")) {
      if (context
          .read<GlobalProvider>()
          .fieldInputValue[widget.field.id]
          .isNotEmpty) {
        if (getElementPosition(
                context.read<GlobalProvider>().fieldInputValue[widget.field.id],
                "Iris") !=
            -1) {
          iris = context
              .read<GlobalProvider>()
              .fieldInputValue[widget.field.id]
              .elementAt(getElementPosition(
                  context
                      .read<GlobalProvider>()
                      .fieldInputValue[widget.field.id],
                  "Iris"));
        }
        if (getElementPosition(
                context.read<GlobalProvider>().fieldInputValue[widget.field.id],
                "Right Hand") !=
            -1) {
          rightHand = context
              .read<GlobalProvider>()
              .fieldInputValue[widget.field.id]
              .elementAt(getElementPosition(
                  context
                      .read<GlobalProvider>()
                      .fieldInputValue[widget.field.id],
                  "Right Hand"));
        }
        if (getElementPosition(
                context.read<GlobalProvider>().fieldInputValue[widget.field.id],
                "Left Hand") !=
            -1) {
          leftHand = context
              .read<GlobalProvider>()
              .fieldInputValue[widget.field.id]
              .elementAt(getElementPosition(
                  context
                      .read<GlobalProvider>()
                      .fieldInputValue[widget.field.id],
                  "Left Hand"));
        }
        if (getElementPosition(
                context.read<GlobalProvider>().fieldInputValue[widget.field.id],
                "Thumbs") !=
            -1) {
          thumbs = context
              .read<GlobalProvider>()
              .fieldInputValue[widget.field.id]
              .elementAt(getElementPosition(
                  context
                      .read<GlobalProvider>()
                      .fieldInputValue[widget.field.id],
                  "Thumbs"));
        }
        if (getElementPosition(
                context.read<GlobalProvider>().fieldInputValue[widget.field.id],
                "Face") !=
            -1) {
          face = context
              .read<GlobalProvider>()
              .fieldInputValue[widget.field.id]
              .elementAt(getElementPosition(
                  context
                      .read<GlobalProvider>()
                      .fieldInputValue[widget.field.id],
                  "Face"));
        }
        if (getElementPosition(
                context.read<GlobalProvider>().fieldInputValue[widget.field.id],
                "Exception") !=
            -1) {
          exception = context
              .read<GlobalProvider>()
              .fieldInputValue[widget.field.id]
              .elementAt(getElementPosition(
                  context
                      .read<GlobalProvider>()
                      .fieldInputValue[widget.field.id],
                  "Exception"));
        }
      }
    }

    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        const SizedBox(
          width: double.infinity,
        ),
        SizedBox(
          width: double.infinity,
          child: Card(
            margin: EdgeInsets.all(0),
            child: Padding(
              padding: EdgeInsets.fromLTRB(20, 29, 0, 29),
              child: (widget.field.inputRequired!)
                  ? RichText(
                      text: TextSpan(
                      text: context
                          .read<GlobalProvider>()
                          .chooseLanguage(widget.field.label!),
                      style: Theme.of(context).textTheme.titleLarge?.copyWith(
                          fontSize: 24,
                          color: blackShade1,
                          fontWeight: semiBold),
                      children: const [
                        TextSpan(
                          text: " *",
                          style: TextStyle(color: Colors.red, fontSize: 15),
                        )
                      ],
                    ))
                  : Text(
                      context
                          .read<GlobalProvider>()
                          .chooseLanguage(widget.field.label!),
                      style: Theme.of(context).textTheme.titleLarge?.copyWith(
                          fontSize: 24,
                          color: blackShade1,
                          fontWeight: semiBold),
                    ),
            ),
          ),
        ),
        Container(
          margin: EdgeInsets.fromLTRB(20, 0, 20, 20),
          height: 1005.h,
          width: double.infinity,
          child: ResponsiveGridList(
              minItemWidth: 372.h,
              verticalGridSpacing: 17,
              horizontalGridSpacing: 17,
              minItemsPerRow: 2,
              maxItemsPerRow: 2,
              children: [
                if (widget.field.bioAttributes!.contains("leftEye") &&
                    widget.field.bioAttributes!.contains("rightEye"))
                  _getBiometricCaptureSelectionBlockMobile(iris),
                if (widget.field.bioAttributes!.contains("rightIndex") &&
                    widget.field.bioAttributes!.contains("rightLittle") &&
                    widget.field.bioAttributes!.contains("rightRing") &&
                    widget.field.bioAttributes!.contains("rightMiddle"))
                  _getBiometricCaptureSelectionBlockMobile(rightHand),
                if (widget.field.bioAttributes!.contains("leftIndex") &&
                    widget.field.bioAttributes!.contains("leftLittle") &&
                    widget.field.bioAttributes!.contains("leftRing") &&
                    widget.field.bioAttributes!.contains("leftMiddle"))
                  _getBiometricCaptureSelectionBlockMobile(leftHand),
                if (widget.field.bioAttributes!.contains("leftThumb") &&
                    widget.field.bioAttributes!.contains("rightThumb"))
                  _getBiometricCaptureSelectionBlockMobile(thumbs),
                if (widget.field.bioAttributes!.contains("face"))
                  _getBiometricCaptureSelectionBlockMobile(face),
                if (iris.exceptions.contains(true) ||
                    rightHand.exceptions.contains(true) ||
                    leftHand.exceptions.contains(true) ||
                    thumbs.exceptions.contains(true) ||
                    face.exceptions.contains(true))
                  _getBiometricCaptureSelectionBlockMobile(exception),
              ]),
        )
      ],
    );
  }
}
