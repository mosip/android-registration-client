import 'package:flutter/material.dart';

import 'package:flutter_screenutil/flutter_screenutil.dart';

import 'package:provider/provider.dart';
import 'package:registration_client/model/biometric_attribute_data.dart';

import 'package:registration_client/model/field.dart';
import 'package:registration_client/pigeon/biometrics_pigeon.dart';

import 'package:registration_client/provider/global_provider.dart';
import 'package:registration_client/ui/process_ui/widgets/biometric_capture_exception_block.dart';
import 'package:registration_client/ui/process_ui/widgets/biometric_capture_scan_block.dart';
import 'package:registration_client/ui/process_ui/widgets/biometric_scan_middle_block.dart';

import 'package:registration_client/utils/app_config.dart';

class BiometricCaptureControl extends StatefulWidget {
  const BiometricCaptureControl({super.key, required this.field});
  final Field field;

  @override
  State<BiometricCaptureControl> createState() =>
      _BiometricCaptureControlState();
}

class _BiometricCaptureControlState extends State<BiometricCaptureControl> {
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

  Widget _getBiometricCaptureSelectionBlock(
      BiometricAttributeData biometricAttributeData) {
    return InkWell(
        onTap: () {
          setState(() {
            biometricAttribute = biometricAttributeData.title;
          });
        },
        child: Stack(
          children: [
            Padding(
              padding: const EdgeInsets.fromLTRB(10, 5, 10, 5),
              child: Container(
                height: 78.h,
                width: 78.h,
                decoration: BoxDecoration(
                    border: Border.all(
                        color: (biometricAttributeData.isScanned == true)
                            ? (biometricAttributeData.exceptions.contains(true))
                                ? secondaryColors.elementAt(16)
                                : secondaryColors.elementAt(11)
                            : (biometricAttribute ==
                                    biometricAttributeData.title)
                                ? secondaryColors.elementAt(12)
                                : secondaryColors.elementAt(14)),
                    borderRadius: BorderRadius.circular(10)),
                child: (biometricAttributeData.isScanned == true)
                    ? Row(
                        mainAxisAlignment: MainAxisAlignment.center,
                        children: [
                          ...biometricAttributeData.listofImages
                              .map((e) => Flexible(
                                    child: Image.memory(
                                      e,
                                      fit: BoxFit.fill,
                                    ),
                                  )),
                        ],
                      )
                    : Padding(
                        padding: const EdgeInsets.all(8.0),
                        child: Image.asset(
                          "assets/images/${biometricAttributeData.title}@2x.png",
                          height: 18.h,
                          width: 64.h,
                        ),
                      ),
              ),
            ),
            if (biometricAttributeData.isScanned == true)
              Positioned(
                  top: 14,
                  left: 14,
                  child: (biometricAttributeData.exceptions.contains(true))
                      ? Image.asset("assets/images/Ellipse 1183.png")
                      : Image.asset("assets/images/Ellipse 1181.png")),
            if (biometricAttributeData.isScanned == true)
              Positioned(
                  top: 6,
                  right: 6,
                  child: (biometricAttributeData.exceptions.contains(true))
                      ? Image.asset("assets/images/Group 57548.png")
                      : Image.asset("assets/images/Group 57745.png")),
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
        Padding(
          padding: const EdgeInsets.fromLTRB(0, 26, 0, 27),
          child: (widget.field.inputRequired!)
              ? RichText(
                  text: TextSpan(
                  text: context
                      .read<GlobalProvider>()
                      .chooseLanguage(widget.field.label!),
                  style: Theme.of(context).textTheme.titleLarge?.copyWith(
                      fontSize: 20, color: blackShade1, fontWeight: semiBold),
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
                      fontSize: 20, color: blackShade1, fontWeight: semiBold),
                ),
        ),
        Card(
          child: Padding(
            padding: const EdgeInsets.all(20.0),
            child: Row(
              mainAxisAlignment: MainAxisAlignment.spaceEvenly,
              children: [
                Container(
                  height: 547.h,
                  decoration: BoxDecoration(
                      color: secondaryColors.elementAt(3),
                      borderRadius: BorderRadius.circular(10),
                      border: Border.all(
                          color: secondaryColors.elementAt(13), width: 1)),
                  child: (widget
                              .field.conditionalBioAttributes!.first!.ageGroup!
                              .compareTo(
                                  context.read<GlobalProvider>().ageGroup) ==
                          0)
                      ? Column(
                          children: [
                            const SizedBox(
                              height: 5,
                            ),
                            if (widget.field.conditionalBioAttributes!.first!
                                    .bioAttributes!
                                    .contains("leftEye") &&
                                widget.field.conditionalBioAttributes!.first!
                                    .bioAttributes!
                                    .contains("rightEye"))
                              _getBiometricCaptureSelectionBlock(iris),
                            if (widget.field.conditionalBioAttributes!.first!
                                    .bioAttributes!
                                    .contains("rightIndex") &&
                                widget.field.conditionalBioAttributes!.first!
                                    .bioAttributes!
                                    .contains("rightLittle") &&
                                widget.field.conditionalBioAttributes!.first!
                                    .bioAttributes!
                                    .contains("rightRing") &&
                                widget.field.conditionalBioAttributes!.first!
                                    .bioAttributes!
                                    .contains("rightMiddle"))
                              _getBiometricCaptureSelectionBlock(rightHand),
                            if (widget.field.conditionalBioAttributes!.first!
                                    .bioAttributes!
                                    .contains("leftIndex") &&
                                widget.field.conditionalBioAttributes!.first!
                                    .bioAttributes!
                                    .contains("leftLittle") &&
                                widget.field.conditionalBioAttributes!.first!
                                    .bioAttributes!
                                    .contains("leftRing") &&
                                widget.field.conditionalBioAttributes!.first!
                                    .bioAttributes!
                                    .contains("leftMiddle"))
                              _getBiometricCaptureSelectionBlock(leftHand),
                            if (widget.field.conditionalBioAttributes!.first!
                                    .bioAttributes!
                                    .contains("leftThumb") &&
                                widget.field.conditionalBioAttributes!.first!
                                    .bioAttributes!
                                    .contains("rightThumb"))
                              _getBiometricCaptureSelectionBlock(thumbs),
                            if (widget.field.conditionalBioAttributes!.first!
                                .bioAttributes!
                                .contains("face"))
                              _getBiometricCaptureSelectionBlock(face),
                            if (iris.exceptions.contains(true) ||
                                rightHand.exceptions.contains(true) ||
                                leftHand.exceptions.contains(true) ||
                                thumbs.exceptions.contains(true) ||
                                face.exceptions.contains(true))
                              _getBiometricCaptureSelectionBlock(exception),
                            const SizedBox(
                              height: 5,
                            ),
                          ],
                        )
                      : Column(
                          children: [
                            const SizedBox(
                              height: 5,
                            ),
                            if (widget.field.bioAttributes!
                                    .contains("leftEye") &&
                                widget.field.bioAttributes!
                                    .contains("rightEye"))
                              _getBiometricCaptureSelectionBlock(iris),
                            if (widget.field.bioAttributes!
                                    .contains("rightIndex") &&
                                widget.field.bioAttributes!
                                    .contains("rightLittle") &&
                                widget.field.bioAttributes!
                                    .contains("rightRing") &&
                                widget.field.bioAttributes!
                                    .contains("rightMiddle"))
                              _getBiometricCaptureSelectionBlock(rightHand),
                            if (widget.field.bioAttributes!
                                    .contains("leftIndex") &&
                                widget.field.bioAttributes!
                                    .contains("leftLittle") &&
                                widget.field.bioAttributes!
                                    .contains("leftRing") &&
                                widget.field.bioAttributes!
                                    .contains("leftMiddle"))
                              _getBiometricCaptureSelectionBlock(leftHand),
                            if (widget.field.bioAttributes!
                                    .contains("leftThumb") &&
                                widget.field.bioAttributes!
                                    .contains("rightThumb"))
                              _getBiometricCaptureSelectionBlock(thumbs),
                            if (widget.field.bioAttributes!.contains("face"))
                              _getBiometricCaptureSelectionBlock(face),
                            if (iris.exceptions.contains(true) ||
                                rightHand.exceptions.contains(true) ||
                                leftHand.exceptions.contains(true) ||
                                thumbs.exceptions.contains(true) ||
                                face.exceptions.contains(true))
                              _getBiometricCaptureSelectionBlock(exception),
                            const SizedBox(
                              height: 5,
                            ),
                          ],
                        ),
                ),
                if (biometricAttribute == "Iris")
                  BiometricCaptureScanBlock(
                    title: "IrisScan",
                    middleBlock: BiometricScanMiddleBlock(
                        biometricAttributeData: iris,
                        field: widget.field,
                        imageHeight: 310.h,
                        imageWidth: 310.h,
                        parameterTitle: "Iris"),
                  ),
                if (biometricAttribute == "Right Hand")
                  BiometricCaptureScanBlock(
                      title: "RightHandScan",
                      middleBlock: BiometricScanMiddleBlock(
                        biometricAttributeData: rightHand,
                        field: widget.field,
                        imageHeight: 190.h,
                        imageWidth: 190.h,
                        parameterTitle: "RightHand",
                      )),
                if (biometricAttribute == "Left Hand")
                  BiometricCaptureScanBlock(
                      title: "LeftHandScan",
                      middleBlock: BiometricScanMiddleBlock(
                        biometricAttributeData: leftHand,
                        field: widget.field,
                        imageHeight: 190.h,
                        imageWidth: 190.h,
                        parameterTitle: "LeftHand",
                      )),
                if (biometricAttribute == "Thumbs")
                  BiometricCaptureScanBlock(
                      title: "ThumbsScan",
                      middleBlock: BiometricScanMiddleBlock(
                        biometricAttributeData: thumbs,
                        field: widget.field,
                        imageHeight: 310.h,
                        imageWidth: 310.h,
                        parameterTitle: "Thumbs",
                      )),
                if (biometricAttribute == "Face")
                  BiometricCaptureScanBlock(
                      title: "FaceScan",
                      middleBlock: BiometricScanMiddleBlock(
                        biometricAttributeData: face,
                        field: widget.field,
                        imageHeight: 310.h,
                        imageWidth: 310.h,
                        parameterTitle: "Face",
                      )),
                if (biometricAttribute == "Exception")
                  BiometricCaptureScanBlock(
                      title: "ExceptionScan",
                      middleBlock: BiometricScanMiddleBlock(
                        biometricAttributeData: exception,
                        field: widget.field,
                        imageHeight: 310.h,
                        imageWidth: 310.h,
                        parameterTitle: "Exception",
                      )),
                if (biometricAttribute == "Iris")
                  BiometricCaptureExceptionBlock(
                    attribute: iris,
                    exceptionImage: SizedBox(
                      height: 164.h,
                      child: Row(
                          mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                          children: [
                            Column(
                              mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                              children: [
                                Text("LEFT",
                                    style: Theme.of(context)
                                        .textTheme
                                        .bodyLarge
                                        ?.copyWith(
                                            fontSize: 14,
                                            fontWeight: semiBold,
                                            color: blackShade1)),
                                Stack(
                                  children: [
                                    InkWell(
                                      onTap: () async {
                                        if (!(iris.exceptions.elementAt(0)) ==
                                            true) {
                                          await BiometricsApi().addBioException(
                                              widget.field.id!,
                                              "Iris",
                                              "leftEye");
                                        } else {
                                          await BiometricsApi()
                                              .removeBioException(
                                                  widget.field.id!,
                                                  "Iris",
                                                  "leftEye");
                                        }
                                        setState(() {
                                          iris.exceptions[0] =
                                              !(iris.exceptions.elementAt(0));

                                          if (iris.exceptions.contains(true)) {
                                            if (iris.exceptionType.isEmpty) {
                                              iris.exceptionType = "Permanent";
                                              iris.isScanned = false;
                                              iris.attemptNo = 0;

                                              iris.listofImages = [
                                                "assets/images/Left Eye@2x.png",
                                                "assets/images/Right Eye@2x.png"
                                              ];
                                              iris.listOfBiometricsDto = [];
                                              iris.qualityPercentage = 0;
                                              iris.thresholdPercentage = "0";

                                              if (!(getElementPosition(
                                                      context
                                                              .read<
                                                                  GlobalProvider>()
                                                              .fieldInputValue[
                                                          widget.field.id!],
                                                      iris.title) ==
                                                  -1)) {
                                                context
                                                    .read<GlobalProvider>()
                                                    .fieldInputValue[
                                                        widget.field.id!]
                                                    .removeAt(getElementPosition(
                                                        context
                                                                .read<
                                                                    GlobalProvider>()
                                                                .fieldInputValue[
                                                            widget.field.id!],
                                                        iris.title));
                                              }
                                            }
                                          }
                                          if (!iris.exceptions.contains(true)) {
                                            iris.exceptionType = "";
                                            iris.isScanned = false;
                                            iris.attemptNo = 0;

                                            iris.listofImages = [
                                              "assets/images/Left Eye@2x.png",
                                              "assets/images/Right Eye@2x.png"
                                            ];
                                            iris.listOfBiometricsDto = [];
                                            iris.qualityPercentage = 0;
                                            iris.thresholdPercentage = "0";

                                            if (!(getElementPosition(
                                                    context
                                                            .read<GlobalProvider>()
                                                            .fieldInputValue[
                                                        widget.field.id!],
                                                    iris.title) ==
                                                -1)) {
                                              context
                                                  .read<GlobalProvider>()
                                                  .fieldInputValue[
                                                      widget.field.id!]
                                                  .removeAt(getElementPosition(
                                                      context
                                                              .read<
                                                                  GlobalProvider>()
                                                              .fieldInputValue[
                                                          widget.field.id!],
                                                      iris.title));
                                            }
                                          }
                                        });
                                      },
                                      child: Image.asset(
                                        "assets/images/Left Eye@2x.png",
                                        height: 72,
                                      ),
                                    ),
                                    (iris.exceptions[0] == true)
                                        ? Positioned(
                                            top: 0,
                                            left: 0,
                                            child: Icon(
                                              Icons.cancel_rounded,
                                              color:
                                                  secondaryColors.elementAt(15),
                                              size: 30.h,
                                            ))
                                        : Container(),
                                  ],
                                )
                              ],
                            ),
                            Column(
                              mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                              children: [
                                Text("RIGHT",
                                    style: Theme.of(context)
                                        .textTheme
                                        .bodyLarge
                                        ?.copyWith(
                                            fontSize: 14,
                                            fontWeight: semiBold,
                                            color: blackShade1)),
                                Stack(
                                  children: [
                                    InkWell(
                                      onTap: () async {
                                        if (!(iris.exceptions.elementAt(1)) ==
                                            true) {
                                          await BiometricsApi().addBioException(
                                              widget.field.id!,
                                              "Iris",
                                              "rightEye");
                                        } else {
                                          await BiometricsApi()
                                              .removeBioException(
                                                  widget.field.id!,
                                                  "Iris",
                                                  "rightEye");
                                        }
                                        setState(() {
                                          iris.exceptions[1] =
                                              !(iris.exceptions.elementAt(1));
                                          if (iris.exceptions.contains(true)) {
                                            if (iris.exceptionType.isEmpty) {
                                              iris.exceptionType = "Permanent";
                                              iris.isScanned = false;
                                              iris.attemptNo = 0;

                                              iris.listofImages = [
                                                "assets/images/Left Eye@2x.png",
                                                "assets/images/Right Eye@2x.png"
                                              ];
                                              iris.listOfBiometricsDto = [];
                                              iris.qualityPercentage = 0;
                                              iris.thresholdPercentage = "0";

                                              if (!(getElementPosition(
                                                      context
                                                              .read<
                                                                  GlobalProvider>()
                                                              .fieldInputValue[
                                                          widget.field.id!],
                                                      iris.title) ==
                                                  -1)) {
                                                context
                                                    .read<GlobalProvider>()
                                                    .fieldInputValue[
                                                        widget.field.id!]
                                                    .removeAt(getElementPosition(
                                                        context
                                                                .read<
                                                                    GlobalProvider>()
                                                                .fieldInputValue[
                                                            widget.field.id!],
                                                        iris.title));
                                              }
                                            }
                                          }
                                          if (!iris.exceptions.contains(true)) {
                                            iris.exceptionType = "";
                                            iris.isScanned = false;
                                            iris.attemptNo = 0;

                                            iris.listofImages = [
                                              "assets/images/Left Eye@2x.png",
                                              "assets/images/Right Eye@2x.png"
                                            ];
                                            iris.listOfBiometricsDto = [];
                                            iris.qualityPercentage = 0;
                                            iris.thresholdPercentage = "0";

                                            if (!(getElementPosition(
                                                    context
                                                            .read<GlobalProvider>()
                                                            .fieldInputValue[
                                                        widget.field.id!],
                                                    iris.title) ==
                                                -1)) {
                                              context
                                                  .read<GlobalProvider>()
                                                  .fieldInputValue[
                                                      widget.field.id!]
                                                  .removeAt(getElementPosition(
                                                      context
                                                              .read<
                                                                  GlobalProvider>()
                                                              .fieldInputValue[
                                                          widget.field.id!],
                                                      iris.title));
                                            }
                                          }
                                        });
                                      },
                                      child: Image.asset(
                                        "assets/images/Right Eye@2x.png",
                                        height: 72,
                                      ),
                                    ),
                                    (iris.exceptions[1] == true)
                                        ? Positioned(
                                            top: 0,
                                            right: 0,
                                            child: Icon(
                                              Icons.cancel_rounded,
                                              color:
                                                  secondaryColors.elementAt(15),
                                              size: 30.h,
                                            ))
                                        : Container(),
                                  ],
                                )
                              ],
                            ),
                          ]),
                    ),
                  ),
                if (biometricAttribute == "Right Hand")
                  BiometricCaptureExceptionBlock(
                    attribute: rightHand,
                    exceptionImage: SizedBox(
                      height: 164.h,
                      child: Row(
                        mainAxisAlignment: MainAxisAlignment.center,
                        children: [
                          Column(
                            mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                            children: [
                              Text("Right Hand",
                                  style: Theme.of(context)
                                      .textTheme
                                      .bodyLarge
                                      ?.copyWith(
                                          fontSize: 14,
                                          fontWeight: semiBold,
                                          color: blackShade1)),
                              Stack(
                                children: [
                                  Image.asset(
                                    "assets/images/Right Hand@2x.png",
                                    height: 114,
                                  ),
                                  Positioned(
                                      top: 11,
                                      left: 25,
                                      child: InkWell(
                                        onTap: () async {
                                          if (!(rightHand.exceptions
                                                  .elementAt(0)) ==
                                              true) {
                                            await BiometricsApi()
                                                .addBioException(
                                                    widget.field.id!,
                                                    "RightHand",
                                                    "rightIndex");
                                          } else {
                                            await BiometricsApi()
                                                .removeBioException(
                                                    widget.field.id!,
                                                    "RightHand",
                                                    "rightIndex");
                                          }
                                          setState(() {
                                            rightHand.exceptions[0] =
                                                !(rightHand.exceptions
                                                    .elementAt(0));
                                            if (rightHand.exceptions
                                                .contains(true)) {
                                              if (rightHand
                                                  .exceptionType.isEmpty) {
                                                rightHand.exceptionType =
                                                    "Permanent";
                                                rightHand.isScanned = false;
                                                rightHand.attemptNo = 0;

                                                rightHand.listofImages = [
                                                  "assets/images/Right Hand@2x.png"
                                                ];
                                                rightHand.listOfBiometricsDto =
                                                    [];
                                                rightHand.qualityPercentage = 0;
                                                rightHand.thresholdPercentage =
                                                    "0";

                                                if (!(getElementPosition(
                                                        context
                                                                .read<
                                                                    GlobalProvider>()
                                                                .fieldInputValue[
                                                            widget.field.id!],
                                                        rightHand.title) ==
                                                    -1)) {
                                                  context
                                                      .read<GlobalProvider>()
                                                      .fieldInputValue[
                                                          widget.field.id!]
                                                      .removeAt(getElementPosition(
                                                          context
                                                                  .read<
                                                                      GlobalProvider>()
                                                                  .fieldInputValue[
                                                              widget.field.id!],
                                                          rightHand.title));
                                                }
                                              }
                                            }
                                            if (!rightHand.exceptions
                                                .contains(true)) {
                                              rightHand.exceptionType = "";
                                              rightHand.isScanned = false;
                                              rightHand.attemptNo = 0;

                                              rightHand.listofImages = [
                                                "assets/images/Right Hand@2x.png"
                                              ];
                                              rightHand.listOfBiometricsDto =
                                                  [];
                                              rightHand.qualityPercentage = 0;
                                              rightHand.thresholdPercentage =
                                                  "0";

                                              if (!(getElementPosition(
                                                      context
                                                              .read<
                                                                  GlobalProvider>()
                                                              .fieldInputValue[
                                                          widget.field.id!],
                                                      rightHand.title) ==
                                                  -1)) {
                                                context
                                                    .read<GlobalProvider>()
                                                    .fieldInputValue[
                                                        widget.field.id!]
                                                    .removeAt(getElementPosition(
                                                        context
                                                                .read<
                                                                    GlobalProvider>()
                                                                .fieldInputValue[
                                                            widget.field.id!],
                                                        rightHand.title));
                                              }
                                            }
                                          });
                                        },
                                        child: Icon(
                                          Icons.cancel_rounded,
                                          color: (rightHand.exceptions[0] ==
                                                  true)
                                              ? secondaryColors.elementAt(15)
                                              : Colors.transparent,
                                          size: 20,
                                        ),
                                      )),
                                  Positioned(
                                      top: 1,
                                      left: 40,
                                      child: InkWell(
                                        onTap: () async {
                                          if (!(rightHand.exceptions
                                                  .elementAt(1)) ==
                                              true) {
                                            await BiometricsApi()
                                                .addBioException(
                                                    widget.field.id!,
                                                    "RightHand",
                                                    "rightMiddle");
                                          } else {
                                            await BiometricsApi()
                                                .removeBioException(
                                                    widget.field.id!,
                                                    "RightHand",
                                                    "rightMiddle");
                                          }
                                          setState(() {
                                            rightHand.exceptions[1] =
                                                !(rightHand.exceptions
                                                    .elementAt(1));
                                            if (rightHand.exceptions
                                                .contains(true)) {
                                              if (rightHand
                                                  .exceptionType.isEmpty) {
                                                rightHand.exceptionType =
                                                    "Permanent";
                                                rightHand.isScanned = false;
                                                rightHand.attemptNo = 0;

                                                rightHand.listofImages = [
                                                  "assets/images/Right Hand@2x.png"
                                                ];
                                                rightHand.listOfBiometricsDto =
                                                    [];
                                                rightHand.qualityPercentage = 0;
                                                rightHand.thresholdPercentage =
                                                    "0";

                                                if (!(getElementPosition(
                                                        context
                                                                .read<
                                                                    GlobalProvider>()
                                                                .fieldInputValue[
                                                            widget.field.id!],
                                                        rightHand.title) ==
                                                    -1)) {
                                                  context
                                                      .read<GlobalProvider>()
                                                      .fieldInputValue[
                                                          widget.field.id!]
                                                      .removeAt(getElementPosition(
                                                          context
                                                                  .read<
                                                                      GlobalProvider>()
                                                                  .fieldInputValue[
                                                              widget.field.id!],
                                                          rightHand.title));
                                                }
                                              }
                                            }
                                            if (!rightHand.exceptions
                                                .contains(true)) {
                                              rightHand.exceptionType = "";
                                              rightHand.isScanned = false;
                                              rightHand.attemptNo = 0;

                                              rightHand.listofImages = [
                                                "assets/images/Right Hand@2x.png"
                                              ];
                                              rightHand.listOfBiometricsDto =
                                                  [];
                                              rightHand.qualityPercentage = 0;
                                              rightHand.thresholdPercentage =
                                                  "0";

                                              if (!(getElementPosition(
                                                      context
                                                              .read<
                                                                  GlobalProvider>()
                                                              .fieldInputValue[
                                                          widget.field.id!],
                                                      rightHand.title) ==
                                                  -1)) {
                                                context
                                                    .read<GlobalProvider>()
                                                    .fieldInputValue[
                                                        widget.field.id!]
                                                    .removeAt(getElementPosition(
                                                        context
                                                                .read<
                                                                    GlobalProvider>()
                                                                .fieldInputValue[
                                                            widget.field.id!],
                                                        rightHand.title));
                                              }
                                            }
                                          });
                                        },
                                        child: Icon(
                                          Icons.cancel_rounded,
                                          color: (rightHand.exceptions[1] ==
                                                  true)
                                              ? secondaryColors.elementAt(15)
                                              : Colors.transparent,
                                          size: 20,
                                        ),
                                      )),
                                  Positioned(
                                      top: 17,
                                      right: 16,
                                      child: InkWell(
                                        onTap: () async {
                                          if (!(rightHand.exceptions
                                                  .elementAt(2)) ==
                                              true) {
                                            await BiometricsApi()
                                                .addBioException(
                                                    widget.field.id!,
                                                    "RightHand",
                                                    "rightRing");
                                          } else {
                                            await BiometricsApi()
                                                .removeBioException(
                                                    widget.field.id!,
                                                    "RightHand",
                                                    "rightRing");
                                          }
                                          setState(() {
                                            rightHand.exceptions[2] =
                                                !(rightHand.exceptions
                                                    .elementAt(2));
                                            if (rightHand.exceptions
                                                .contains(true)) {
                                              if (rightHand
                                                  .exceptionType.isEmpty) {
                                                rightHand.exceptionType =
                                                    "Permanent";
                                                rightHand.isScanned = false;
                                                rightHand.attemptNo = 0;

                                                rightHand.listofImages = [
                                                  "assets/images/Right Hand@2x.png"
                                                ];
                                                rightHand.listOfBiometricsDto =
                                                    [];
                                                rightHand.qualityPercentage = 0;
                                                rightHand.thresholdPercentage =
                                                    "0";

                                                if (!(getElementPosition(
                                                        context
                                                                .read<
                                                                    GlobalProvider>()
                                                                .fieldInputValue[
                                                            widget.field.id!],
                                                        rightHand.title) ==
                                                    -1)) {
                                                  context
                                                      .read<GlobalProvider>()
                                                      .fieldInputValue[
                                                          widget.field.id!]
                                                      .removeAt(getElementPosition(
                                                          context
                                                                  .read<
                                                                      GlobalProvider>()
                                                                  .fieldInputValue[
                                                              widget.field.id!],
                                                          rightHand.title));
                                                }
                                              }
                                            }
                                            if (!rightHand.exceptions
                                                .contains(true)) {
                                              rightHand.exceptionType = "";
                                              rightHand.isScanned = false;
                                              rightHand.attemptNo = 0;

                                              rightHand.listofImages = [
                                                "assets/images/Right Hand@2x.png"
                                              ];
                                              rightHand.listOfBiometricsDto =
                                                  [];
                                              rightHand.qualityPercentage = 0;
                                              rightHand.thresholdPercentage =
                                                  "0";

                                              if (!(getElementPosition(
                                                      context
                                                              .read<
                                                                  GlobalProvider>()
                                                              .fieldInputValue[
                                                          widget.field.id!],
                                                      rightHand.title) ==
                                                  -1)) {
                                                context
                                                    .read<GlobalProvider>()
                                                    .fieldInputValue[
                                                        widget.field.id!]
                                                    .removeAt(getElementPosition(
                                                        context
                                                                .read<
                                                                    GlobalProvider>()
                                                                .fieldInputValue[
                                                            widget.field.id!],
                                                        rightHand.title));
                                              }
                                            }
                                          });
                                        },
                                        child: Icon(
                                          Icons.cancel_rounded,
                                          color: (rightHand.exceptions[2] ==
                                                  true)
                                              ? secondaryColors.elementAt(15)
                                              : Colors.transparent,
                                          size: 20,
                                        ),
                                      )),
                                  Positioned(
                                      top: 33,
                                      right: 5,
                                      child: InkWell(
                                        onTap: () async {
                                          if (!(rightHand.exceptions
                                                  .elementAt(3)) ==
                                              true) {
                                            await BiometricsApi()
                                                .addBioException(
                                                    widget.field.id!,
                                                    "RightHand",
                                                    "rightLittle");
                                          } else {
                                            await BiometricsApi()
                                                .removeBioException(
                                                    widget.field.id!,
                                                    "RightHand",
                                                    "rightLittle");
                                          }
                                          setState(() {
                                            rightHand.exceptions[3] =
                                                !(rightHand.exceptions
                                                    .elementAt(3));
                                            if (rightHand.exceptions
                                                .contains(true)) {
                                              if (rightHand
                                                  .exceptionType.isEmpty) {
                                                rightHand.exceptionType =
                                                    "Permanent";
                                                rightHand.isScanned = false;
                                                rightHand.attemptNo = 0;

                                                rightHand.listofImages = [
                                                  "assets/images/Right Hand@2x.png"
                                                ];
                                                rightHand.listOfBiometricsDto =
                                                    [];
                                                rightHand.qualityPercentage = 0;
                                                rightHand.thresholdPercentage =
                                                    "0";

                                                if (!(getElementPosition(
                                                        context
                                                                .read<
                                                                    GlobalProvider>()
                                                                .fieldInputValue[
                                                            widget.field.id!],
                                                        rightHand.title) ==
                                                    -1)) {
                                                  context
                                                      .read<GlobalProvider>()
                                                      .fieldInputValue[
                                                          widget.field.id!]
                                                      .removeAt(getElementPosition(
                                                          context
                                                                  .read<
                                                                      GlobalProvider>()
                                                                  .fieldInputValue[
                                                              widget.field.id!],
                                                          rightHand.title));
                                                }
                                              }
                                            }
                                            if (!rightHand.exceptions
                                                .contains(true)) {
                                              rightHand.exceptionType = "";
                                              rightHand.isScanned = false;
                                              rightHand.attemptNo = 0;

                                              rightHand.listofImages = [
                                                "assets/images/Right Hand@2x.png"
                                              ];
                                              rightHand.listOfBiometricsDto =
                                                  [];
                                              rightHand.qualityPercentage = 0;
                                              rightHand.thresholdPercentage =
                                                  "0";

                                              if (!(getElementPosition(
                                                      context
                                                              .read<
                                                                  GlobalProvider>()
                                                              .fieldInputValue[
                                                          widget.field.id!],
                                                      rightHand.title) ==
                                                  -1)) {
                                                context
                                                    .read<GlobalProvider>()
                                                    .fieldInputValue[
                                                        widget.field.id!]
                                                    .removeAt(getElementPosition(
                                                        context
                                                                .read<
                                                                    GlobalProvider>()
                                                                .fieldInputValue[
                                                            widget.field.id!],
                                                        rightHand.title));
                                              }
                                            }
                                          });
                                        },
                                        child: Icon(
                                          Icons.cancel_rounded,
                                          color: (rightHand.exceptions[3] ==
                                                  true)
                                              ? secondaryColors.elementAt(15)
                                              : Colors.transparent,
                                          size: 20,
                                        ),
                                      )),
                                ],
                              )
                            ],
                          ),
                        ],
                      ),
                    ),
                  ),
                if (biometricAttribute == "Left Hand")
                  BiometricCaptureExceptionBlock(
                    attribute: leftHand,
                    exceptionImage: SizedBox(
                      height: 164.h,
                      child: Row(
                        mainAxisAlignment: MainAxisAlignment.center,
                        children: [
                          Column(
                            mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                            children: [
                              Text("Left Hand",
                                  style: Theme.of(context)
                                      .textTheme
                                      .bodyLarge
                                      ?.copyWith(
                                          fontSize: 14,
                                          fontWeight: semiBold,
                                          color: blackShade1)),
                              Stack(
                                children: [
                                  Image.asset(
                                    "assets/images/Left Hand@2x.png",
                                    height: 114,
                                  ),
                                  Positioned(
                                      top: 11,
                                      right: 25,
                                      child: InkWell(
                                        onTap: () async {
                                          if (!(leftHand.exceptions
                                                  .elementAt(0)) ==
                                              true) {
                                            await BiometricsApi()
                                                .addBioException(
                                                    widget.field.id!,
                                                    "LeftHand",
                                                    "leftIndex");
                                          } else {
                                            await BiometricsApi()
                                                .removeBioException(
                                                    widget.field.id!,
                                                    "LeftHand",
                                                    "leftIndex");
                                          }
                                          setState(() {
                                            leftHand.exceptions[0] = !(leftHand
                                                .exceptions
                                                .elementAt(0));
                                            if (leftHand.exceptions
                                                .contains(true)) {
                                              if (leftHand
                                                  .exceptionType.isEmpty) {
                                                leftHand.exceptionType =
                                                    "Permanent";
                                                leftHand.isScanned = false;
                                                leftHand.attemptNo = 0;

                                                leftHand.listofImages = [
                                                  "assets/images/Left Hand@2x.png"
                                                ];
                                                leftHand.listOfBiometricsDto =
                                                    [];
                                                leftHand.qualityPercentage = 0;
                                                leftHand.thresholdPercentage =
                                                    "0";

                                                if (!(getElementPosition(
                                                        context
                                                                .read<
                                                                    GlobalProvider>()
                                                                .fieldInputValue[
                                                            widget.field.id!],
                                                        leftHand.title) ==
                                                    -1)) {
                                                  context
                                                      .read<GlobalProvider>()
                                                      .fieldInputValue[
                                                          widget.field.id!]
                                                      .removeAt(getElementPosition(
                                                          context
                                                                  .read<
                                                                      GlobalProvider>()
                                                                  .fieldInputValue[
                                                              widget.field.id!],
                                                          leftHand.title));
                                                }
                                              }
                                            }
                                            if (!leftHand.exceptions
                                                .contains(true)) {
                                              leftHand.exceptionType = "";
                                              leftHand.isScanned = false;
                                              leftHand.attemptNo = 0;

                                              leftHand.listofImages = [
                                                "assets/images/Left Hand@2x.png"
                                              ];
                                              leftHand.listOfBiometricsDto = [];
                                              leftHand.qualityPercentage = 0;
                                              leftHand.thresholdPercentage =
                                                  "0";

                                              if (!(getElementPosition(
                                                      context
                                                              .read<
                                                                  GlobalProvider>()
                                                              .fieldInputValue[
                                                          widget.field.id!],
                                                      leftHand.title) ==
                                                  -1)) {
                                                context
                                                    .read<GlobalProvider>()
                                                    .fieldInputValue[
                                                        widget.field.id!]
                                                    .removeAt(getElementPosition(
                                                        context
                                                                .read<
                                                                    GlobalProvider>()
                                                                .fieldInputValue[
                                                            widget.field.id!],
                                                        leftHand.title));
                                              }
                                            }
                                          });
                                        },
                                        child: Icon(
                                          Icons.cancel_rounded,
                                          color: (leftHand.exceptions[0] ==
                                                  true)
                                              ? secondaryColors.elementAt(15)
                                              : Colors.transparent,
                                          size: 20,
                                        ),
                                      )),
                                  Positioned(
                                      top: 1,
                                      right: 40,
                                      child: InkWell(
                                        onTap: () async {
                                          if (!(leftHand.exceptions
                                                  .elementAt(1)) ==
                                              true) {
                                            await BiometricsApi()
                                                .addBioException(
                                                    widget.field.id!,
                                                    "LeftHand",
                                                    "leftMiddle");
                                          } else {
                                            await BiometricsApi()
                                                .removeBioException(
                                                    widget.field.id!,
                                                    "LeftHand",
                                                    "leftMiddle");
                                          }
                                          setState(() {
                                            leftHand.exceptions[1] = !(leftHand
                                                .exceptions
                                                .elementAt(1));
                                            if (leftHand.exceptions
                                                .contains(true)) {
                                              if (leftHand
                                                  .exceptionType.isEmpty) {
                                                leftHand.exceptionType =
                                                    "Permanent";
                                                leftHand.isScanned = false;
                                                leftHand.attemptNo = 0;

                                                leftHand.listofImages = [
                                                  "assets/images/Left Hand@2x.png"
                                                ];
                                                leftHand.listOfBiometricsDto =
                                                    [];
                                                leftHand.qualityPercentage = 0;
                                                leftHand.thresholdPercentage =
                                                    "0";

                                                if (!(getElementPosition(
                                                        context
                                                                .read<
                                                                    GlobalProvider>()
                                                                .fieldInputValue[
                                                            widget.field.id!],
                                                        leftHand.title) ==
                                                    -1)) {
                                                  context
                                                      .read<GlobalProvider>()
                                                      .fieldInputValue[
                                                          widget.field.id!]
                                                      .removeAt(getElementPosition(
                                                          context
                                                                  .read<
                                                                      GlobalProvider>()
                                                                  .fieldInputValue[
                                                              widget.field.id!],
                                                          leftHand.title));
                                                }
                                              }
                                            }
                                            if (!leftHand.exceptions
                                                .contains(true)) {
                                              leftHand.exceptionType = "";
                                              leftHand.isScanned = false;
                                              leftHand.attemptNo = 0;

                                              leftHand.listofImages = [
                                                "assets/images/Left Hand@2x.png"
                                              ];
                                              leftHand.listOfBiometricsDto = [];
                                              leftHand.qualityPercentage = 0;
                                              leftHand.thresholdPercentage =
                                                  "0";

                                              if (!(getElementPosition(
                                                      context
                                                              .read<
                                                                  GlobalProvider>()
                                                              .fieldInputValue[
                                                          widget.field.id!],
                                                      leftHand.title) ==
                                                  -1)) {
                                                context
                                                    .read<GlobalProvider>()
                                                    .fieldInputValue[
                                                        widget.field.id!]
                                                    .removeAt(getElementPosition(
                                                        context
                                                                .read<
                                                                    GlobalProvider>()
                                                                .fieldInputValue[
                                                            widget.field.id!],
                                                        leftHand.title));
                                              }
                                            }
                                          });
                                        },
                                        child: Icon(
                                          Icons.cancel_rounded,
                                          color: (leftHand.exceptions[1] ==
                                                  true)
                                              ? secondaryColors.elementAt(15)
                                              : Colors.transparent,
                                          size: 20,
                                        ),
                                      )),
                                  Positioned(
                                      top: 17,
                                      left: 16,
                                      child: InkWell(
                                        onTap: () async {
                                          if (!(leftHand.exceptions
                                                  .elementAt(2)) ==
                                              true) {
                                            await BiometricsApi()
                                                .addBioException(
                                                    widget.field.id!,
                                                    "LeftHand",
                                                    "leftRing");
                                          } else {
                                            await BiometricsApi()
                                                .removeBioException(
                                                    widget.field.id!,
                                                    "LeftHand",
                                                    "leftRing");
                                          }
                                          setState(() {
                                            leftHand.exceptions[2] = !(leftHand
                                                .exceptions
                                                .elementAt(2));
                                            if (leftHand.exceptions
                                                .contains(true)) {
                                              if (leftHand
                                                  .exceptionType.isEmpty) {
                                                leftHand.exceptionType =
                                                    "Permanent";
                                                leftHand.isScanned = false;
                                                leftHand.attemptNo = 0;

                                                leftHand.listofImages = [
                                                  "assets/images/Left Hand@2x.png"
                                                ];
                                                leftHand.listOfBiometricsDto =
                                                    [];
                                                leftHand.qualityPercentage = 0;
                                                leftHand.thresholdPercentage =
                                                    "0";

                                                if (!(getElementPosition(
                                                        context
                                                                .read<
                                                                    GlobalProvider>()
                                                                .fieldInputValue[
                                                            widget.field.id!],
                                                        leftHand.title) ==
                                                    -1)) {
                                                  context
                                                      .read<GlobalProvider>()
                                                      .fieldInputValue[
                                                          widget.field.id!]
                                                      .removeAt(getElementPosition(
                                                          context
                                                                  .read<
                                                                      GlobalProvider>()
                                                                  .fieldInputValue[
                                                              widget.field.id!],
                                                          leftHand.title));
                                                }
                                              }
                                            }
                                            if (!leftHand.exceptions
                                                .contains(true)) {
                                              leftHand.exceptionType = "";
                                              leftHand.isScanned = false;
                                              leftHand.attemptNo = 0;

                                              leftHand.listofImages = [
                                                "assets/images/Left Hand@2x.png"
                                              ];
                                              leftHand.listOfBiometricsDto = [];
                                              leftHand.qualityPercentage = 0;
                                              leftHand.thresholdPercentage =
                                                  "0";

                                              if (!(getElementPosition(
                                                      context
                                                              .read<
                                                                  GlobalProvider>()
                                                              .fieldInputValue[
                                                          widget.field.id!],
                                                      leftHand.title) ==
                                                  -1)) {
                                                context
                                                    .read<GlobalProvider>()
                                                    .fieldInputValue[
                                                        widget.field.id!]
                                                    .removeAt(getElementPosition(
                                                        context
                                                                .read<
                                                                    GlobalProvider>()
                                                                .fieldInputValue[
                                                            widget.field.id!],
                                                        leftHand.title));
                                              }
                                            }
                                          });
                                        },
                                        child: Icon(
                                          Icons.cancel_rounded,
                                          color: (leftHand.exceptions[2] ==
                                                  true)
                                              ? secondaryColors.elementAt(15)
                                              : Colors.transparent,
                                          size: 20,
                                        ),
                                      )),
                                  Positioned(
                                      top: 33,
                                      left: 5,
                                      child: InkWell(
                                        onTap: () async {
                                          if (!(leftHand.exceptions
                                                  .elementAt(3)) ==
                                              true) {
                                            await BiometricsApi()
                                                .addBioException(
                                                    widget.field.id!,
                                                    "LeftHand",
                                                    "leftLittle");
                                          } else {
                                            await BiometricsApi()
                                                .removeBioException(
                                                    widget.field.id!,
                                                    "LeftHand",
                                                    "leftLittle");
                                          }
                                          setState(() {
                                            leftHand.exceptions[3] = !(leftHand
                                                .exceptions
                                                .elementAt(3));
                                            if (leftHand.exceptions
                                                .contains(true)) {
                                              if (leftHand
                                                  .exceptionType.isEmpty) {
                                                leftHand.exceptionType =
                                                    "Permanent";
                                                leftHand.isScanned = false;
                                                leftHand.attemptNo = 0;

                                                leftHand.listofImages = [
                                                  "assets/images/Left Hand@2x.png"
                                                ];
                                                leftHand.listOfBiometricsDto =
                                                    [];
                                                leftHand.qualityPercentage = 0;
                                                leftHand.thresholdPercentage =
                                                    "0";

                                                if (!(getElementPosition(
                                                        context
                                                                .read<
                                                                    GlobalProvider>()
                                                                .fieldInputValue[
                                                            widget.field.id!],
                                                        leftHand.title) ==
                                                    -1)) {
                                                  context
                                                      .read<GlobalProvider>()
                                                      .fieldInputValue[
                                                          widget.field.id!]
                                                      .removeAt(getElementPosition(
                                                          context
                                                                  .read<
                                                                      GlobalProvider>()
                                                                  .fieldInputValue[
                                                              widget.field.id!],
                                                          leftHand.title));
                                                }
                                              }
                                            }
                                            if (!leftHand.exceptions
                                                .contains(true)) {
                                              leftHand.exceptionType = "";
                                              leftHand.isScanned = false;
                                              leftHand.attemptNo = 0;

                                              leftHand.listofImages = [
                                                "assets/images/Left Hand@2x.png"
                                              ];
                                              leftHand.listOfBiometricsDto = [];
                                              leftHand.qualityPercentage = 0;
                                              leftHand.thresholdPercentage =
                                                  "0";

                                              if (!(getElementPosition(
                                                      context
                                                              .read<
                                                                  GlobalProvider>()
                                                              .fieldInputValue[
                                                          widget.field.id!],
                                                      leftHand.title) ==
                                                  -1)) {
                                                context
                                                    .read<GlobalProvider>()
                                                    .fieldInputValue[
                                                        widget.field.id!]
                                                    .removeAt(getElementPosition(
                                                        context
                                                                .read<
                                                                    GlobalProvider>()
                                                                .fieldInputValue[
                                                            widget.field.id!],
                                                        leftHand.title));
                                              }
                                            }
                                          });
                                        },
                                        child: Icon(
                                          Icons.cancel_rounded,
                                          color: (leftHand.exceptions[3] ==
                                                  true)
                                              ? secondaryColors.elementAt(15)
                                              : Colors.transparent,
                                          size: 20,
                                        ),
                                      )),
                                ],
                              )
                            ],
                          ),
                        ],
                      ),
                    ),
                  ),
                if (biometricAttribute == "Thumbs")
                  BiometricCaptureExceptionBlock(
                    attribute: thumbs,
                    exceptionImage: SizedBox(
                      height: 164.h,
                      child: Row(
                        mainAxisAlignment: MainAxisAlignment.center,
                        children: [
                          Column(
                            mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                            children: [
                              Text("Thumbs",
                                  style: Theme.of(context)
                                      .textTheme
                                      .bodyLarge
                                      ?.copyWith(
                                          fontSize: 14,
                                          fontWeight: semiBold,
                                          color: blackShade1)),
                              Stack(
                                children: [
                                  Image.asset(
                                    "assets/images/Thumbs@2x.png",
                                    height: 114,
                                  ),
                                  Positioned(
                                      top: 22,
                                      left: 25,
                                      child: InkWell(
                                        onTap: () async {
                                          if (!(thumbs.exceptions
                                                  .elementAt(0)) ==
                                              true) {
                                            await BiometricsApi()
                                                .addBioException(
                                                    widget.field.id!,
                                                    "Thumbs",
                                                    "leftThumb");
                                          } else {
                                            await BiometricsApi()
                                                .removeBioException(
                                                    widget.field.id!,
                                                    "Thumbs",
                                                    "leftThumb");
                                          }
                                          setState(() {
                                            thumbs.exceptions[0] = !(thumbs
                                                .exceptions
                                                .elementAt(0));
                                            if (thumbs.exceptions
                                                .contains(true)) {
                                              if (thumbs
                                                  .exceptionType.isEmpty) {
                                                thumbs.exceptionType =
                                                    "Permanent";
                                                thumbs.isScanned = false;
                                                thumbs.attemptNo = 0;

                                                thumbs.listofImages = [
                                                  "assets/images/Thumbs@2x.png"
                                                ];
                                                thumbs.listOfBiometricsDto = [];
                                                thumbs.qualityPercentage = 0;
                                                thumbs.thresholdPercentage =
                                                    "0";

                                                if (!(getElementPosition(
                                                        context
                                                                .read<
                                                                    GlobalProvider>()
                                                                .fieldInputValue[
                                                            widget.field.id!],
                                                        thumbs.title) ==
                                                    -1)) {
                                                  context
                                                      .read<GlobalProvider>()
                                                      .fieldInputValue[
                                                          widget.field.id!]
                                                      .removeAt(getElementPosition(
                                                          context
                                                                  .read<
                                                                      GlobalProvider>()
                                                                  .fieldInputValue[
                                                              widget.field.id!],
                                                          thumbs.title));
                                                }
                                              }
                                            }
                                            if (!thumbs.exceptions
                                                .contains(true)) {
                                              thumbs.exceptionType = "";
                                              thumbs.isScanned = false;
                                              thumbs.attemptNo = 0;

                                              thumbs.listofImages = [
                                                "assets/images/Thumbs@2x.png"
                                              ];
                                              thumbs.listOfBiometricsDto = [];
                                              thumbs.qualityPercentage = 0;
                                              thumbs.thresholdPercentage = "0";

                                              if (!(getElementPosition(
                                                      context
                                                              .read<
                                                                  GlobalProvider>()
                                                              .fieldInputValue[
                                                          widget.field.id!],
                                                      thumbs.title) ==
                                                  -1)) {
                                                context
                                                    .read<GlobalProvider>()
                                                    .fieldInputValue[
                                                        widget.field.id!]
                                                    .removeAt(getElementPosition(
                                                        context
                                                                .read<
                                                                    GlobalProvider>()
                                                                .fieldInputValue[
                                                            widget.field.id!],
                                                        thumbs.title));
                                              }
                                            }
                                          });
                                        },
                                        child: Icon(
                                          Icons.cancel_rounded,
                                          color: (thumbs.exceptions[0] == true)
                                              ? secondaryColors.elementAt(15)
                                              : Colors.transparent,
                                          size: 20,
                                        ),
                                      )),
                                  Positioned(
                                      top: 22,
                                      right: 25,
                                      child: InkWell(
                                        onTap: () async {
                                          if (!(thumbs.exceptions
                                                  .elementAt(1)) ==
                                              true) {
                                            await BiometricsApi()
                                                .addBioException(
                                                    widget.field.id!,
                                                    "Thumbs",
                                                    "rightThumb");
                                          } else {
                                            await BiometricsApi()
                                                .removeBioException(
                                                    widget.field.id!,
                                                    "Thumbs",
                                                    "rightThumb");
                                          }
                                          setState(() {
                                            thumbs.exceptions[1] = !(thumbs
                                                .exceptions
                                                .elementAt(1));
                                            if (thumbs.exceptions
                                                .contains(true)) {
                                              if (thumbs
                                                  .exceptionType.isEmpty) {
                                                thumbs.exceptionType =
                                                    "Permanent";
                                                thumbs.isScanned = false;
                                                thumbs.attemptNo = 0;

                                                thumbs.listofImages = [
                                                  "assets/images/Thumbs@2x.png"
                                                ];
                                                thumbs.listOfBiometricsDto = [];
                                                thumbs.qualityPercentage = 0;
                                                thumbs.thresholdPercentage =
                                                    "0";

                                                if (!(getElementPosition(
                                                        context
                                                                .read<
                                                                    GlobalProvider>()
                                                                .fieldInputValue[
                                                            widget.field.id!],
                                                        thumbs.title) ==
                                                    -1)) {
                                                  context
                                                      .read<GlobalProvider>()
                                                      .fieldInputValue[
                                                          widget.field.id!]
                                                      .removeAt(getElementPosition(
                                                          context
                                                                  .read<
                                                                      GlobalProvider>()
                                                                  .fieldInputValue[
                                                              widget.field.id!],
                                                          thumbs.title));
                                                }
                                              }
                                            }
                                            if (!thumbs.exceptions
                                                .contains(true)) {
                                              thumbs.exceptionType = "";
                                              thumbs.isScanned = false;
                                              thumbs.attemptNo = 0;

                                              thumbs.listofImages = [
                                                "assets/images/Thumbs@2x.png"
                                              ];
                                              thumbs.listOfBiometricsDto = [];
                                              thumbs.qualityPercentage = 0;
                                              thumbs.thresholdPercentage = "0";

                                              if (!(getElementPosition(
                                                      context
                                                              .read<
                                                                  GlobalProvider>()
                                                              .fieldInputValue[
                                                          widget.field.id!],
                                                      thumbs.title) ==
                                                  -1)) {
                                                context
                                                    .read<GlobalProvider>()
                                                    .fieldInputValue[
                                                        widget.field.id!]
                                                    .removeAt(getElementPosition(
                                                        context
                                                                .read<
                                                                    GlobalProvider>()
                                                                .fieldInputValue[
                                                            widget.field.id!],
                                                        thumbs.title));
                                              }
                                            }
                                          });
                                        },
                                        child: Icon(
                                          Icons.cancel_rounded,
                                          color: (thumbs.exceptions[1] == true)
                                              ? secondaryColors.elementAt(15)
                                              : Colors.transparent,
                                          size: 20,
                                        ),
                                      )),
                                ],
                              )
                            ],
                          ),
                        ],
                      ),
                    ),
                  ),
                if (biometricAttribute == "Face")
                  BiometricCaptureExceptionBlock(
                    attribute: face,
                    exceptionImage: SizedBox(
                      height: 228.h,
                      child: Row(
                        mainAxisAlignment: MainAxisAlignment.center,
                        children: [
                          Column(
                            mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                            children: [
                              Text("Face",
                                  style: Theme.of(context)
                                      .textTheme
                                      .bodyLarge
                                      ?.copyWith(
                                          fontSize: 14,
                                          fontWeight: semiBold,
                                          color: blackShade1)),
                              SizedBox(
                                height: 64.h,
                              ),
                              Image.asset(
                                "assets/images/Face@2x.png",
                                height: 114,
                              ),
                            ],
                          ),
                        ],
                      ),
                    ),
                  ),
                if (biometricAttribute == "Exception")
                  BiometricCaptureExceptionBlock(
                    attribute: exception,
                    exceptionImage: SizedBox(
                      height: 228.h,
                      child: Row(
                        mainAxisAlignment: MainAxisAlignment.center,
                        children: [
                          Column(
                            mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                            children: [
                              Text("Exception",
                                  style: Theme.of(context)
                                      .textTheme
                                      .bodyLarge
                                      ?.copyWith(
                                          fontSize: 14,
                                          fontWeight: semiBold,
                                          color: blackShade1)),
                              SizedBox(
                                height: 64.h,
                              ),
                              Image.asset(
                                "assets/images/Exception@2x.png",
                                height: 114,
                              ),
                            ],
                          ),
                        ],
                      ),
                    ),
                  ),
              ],
            ),
          ),
        )
      ],
    );
  }
}
