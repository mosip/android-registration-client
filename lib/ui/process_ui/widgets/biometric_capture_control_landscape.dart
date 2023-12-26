import 'package:flutter/material.dart';

import 'package:flutter_screenutil/flutter_screenutil.dart';

import 'package:provider/provider.dart';
import 'package:registration_client/model/biometric_attribute_data.dart';
import 'package:registration_client/model/biometrics_dto.dart';

import 'package:registration_client/model/field.dart';
import 'package:registration_client/pigeon/biometrics_pigeon.dart';
import 'package:registration_client/provider/biometric_capture_control_provider.dart';

import 'package:registration_client/provider/global_provider.dart';
import 'package:registration_client/ui/process_ui/widgets/biometric_capture_exception_block.dart';
import 'package:registration_client/ui/process_ui/widgets/biometric_capture_scan_block.dart';
import 'package:registration_client/ui/process_ui/widgets/biometric_scan_middle_block.dart';

import 'package:registration_client/utils/app_config.dart';

class BiometricCaptureControlLandscape extends StatefulWidget {
  const BiometricCaptureControlLandscape({super.key, required this.field});
  final Field field;

  @override
  State<BiometricCaptureControlLandscape> createState() =>
      _BiometricCaptureControlLandscapeState();
}

class _BiometricCaptureControlLandscapeState
    extends State<BiometricCaptureControlLandscape> {
  resetAfterException(String key, BiometricAttributeData data) {
    if (context.read<GlobalProvider>().fieldInputValue.containsKey(key)) {
      if (context.read<BiometricCaptureControlProvider>().getElementPosition(
              context.read<GlobalProvider>().fieldInputValue[key],
              data.title) !=
          -1) {
        context.read<GlobalProvider>().fieldInputValue[key].removeAt(context
            .read<BiometricCaptureControlProvider>()
            .getElementPosition(
                context.read<GlobalProvider>().fieldInputValue[key],
                data.title));
      }
    }
  }

  Widget _getBiometricCaptureSelectionBlock(
      BiometricAttributeData biometricAttributeData) {
    return InkWell(
        onTap: () {
          context.read<BiometricCaptureControlProvider>().biometricAttribute =
              biometricAttributeData.title;
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
                            : (context
                                        .read<
                                            BiometricCaptureControlProvider>()
                                        .biometricAttribute ==
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
                              _getBiometricCaptureSelectionBlock(context
                                  .read<BiometricCaptureControlProvider>()
                                  .iris),
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
                              _getBiometricCaptureSelectionBlock(context
                                  .read<BiometricCaptureControlProvider>()
                                  .rightHand),
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
                              _getBiometricCaptureSelectionBlock(context
                                  .read<BiometricCaptureControlProvider>()
                                  .leftHand),
                            if (widget.field.conditionalBioAttributes!.first!
                                    .bioAttributes!
                                    .contains("leftThumb") &&
                                widget.field.conditionalBioAttributes!.first!
                                    .bioAttributes!
                                    .contains("rightThumb"))
                              _getBiometricCaptureSelectionBlock(context
                                  .read<BiometricCaptureControlProvider>()
                                  .thumbs),
                            if (widget.field.conditionalBioAttributes!.first!
                                .bioAttributes!
                                .contains("face"))
                              _getBiometricCaptureSelectionBlock(context
                                  .read<BiometricCaptureControlProvider>()
                                  .face),
                            if (context
                                    .read<BiometricCaptureControlProvider>()
                                    .iris
                                    .exceptions
                                    .contains(true) ||
                                context
                                    .read<BiometricCaptureControlProvider>()
                                    .rightHand
                                    .exceptions
                                    .contains(true) ||
                                context
                                    .read<BiometricCaptureControlProvider>()
                                    .leftHand
                                    .exceptions
                                    .contains(true) ||
                                context
                                    .read<BiometricCaptureControlProvider>()
                                    .thumbs
                                    .exceptions
                                    .contains(true) ||
                                context
                                    .read<BiometricCaptureControlProvider>()
                                    .face
                                    .exceptions
                                    .contains(true))
                              _getBiometricCaptureSelectionBlock(context
                                  .read<BiometricCaptureControlProvider>()
                                  .exception),
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
                              _getBiometricCaptureSelectionBlock(context
                                  .read<BiometricCaptureControlProvider>()
                                  .iris),
                            if (widget.field.bioAttributes!
                                    .contains("rightIndex") &&
                                widget.field.bioAttributes!
                                    .contains("rightLittle") &&
                                widget.field.bioAttributes!
                                    .contains("rightRing") &&
                                widget.field.bioAttributes!
                                    .contains("rightMiddle"))
                              _getBiometricCaptureSelectionBlock(context
                                  .read<BiometricCaptureControlProvider>()
                                  .rightHand),
                            if (widget.field.bioAttributes!
                                    .contains("leftIndex") &&
                                widget.field.bioAttributes!
                                    .contains("leftLittle") &&
                                widget.field.bioAttributes!
                                    .contains("leftRing") &&
                                widget.field.bioAttributes!
                                    .contains("leftMiddle"))
                              _getBiometricCaptureSelectionBlock(context
                                  .read<BiometricCaptureControlProvider>()
                                  .leftHand),
                            if (widget.field.bioAttributes!
                                    .contains("leftThumb") &&
                                widget.field.bioAttributes!
                                    .contains("rightThumb"))
                              _getBiometricCaptureSelectionBlock(context
                                  .read<BiometricCaptureControlProvider>()
                                  .thumbs),
                            if (widget.field.bioAttributes!.contains("face"))
                              _getBiometricCaptureSelectionBlock(context
                                  .read<BiometricCaptureControlProvider>()
                                  .face),
                            if (context
                                    .read<BiometricCaptureControlProvider>()
                                    .iris
                                    .exceptions
                                    .contains(true) ||
                                context
                                    .read<BiometricCaptureControlProvider>()
                                    .rightHand
                                    .exceptions
                                    .contains(true) ||
                                context
                                    .read<BiometricCaptureControlProvider>()
                                    .leftHand
                                    .exceptions
                                    .contains(true) ||
                                context
                                    .read<BiometricCaptureControlProvider>()
                                    .thumbs
                                    .exceptions
                                    .contains(true) ||
                                context
                                    .read<BiometricCaptureControlProvider>()
                                    .face
                                    .exceptions
                                    .contains(true))
                              _getBiometricCaptureSelectionBlock(context
                                  .read<BiometricCaptureControlProvider>()
                                  .exception),
                            const SizedBox(
                              height: 5,
                            ),
                          ],
                        ),
                ),
                if (context
                        .read<BiometricCaptureControlProvider>()
                        .biometricAttribute ==
                    "Iris")
                  BiometricCaptureScanBlock(
                    title: "IrisScan",
                    middleBlock: BiometricScanMiddleBlock(
                        biometricAttributeData: context
                            .read<BiometricCaptureControlProvider>()
                            .iris,
                        field: widget.field,
                        imageHeight: 310.h,
                        imageWidth: 310.h,
                        parameterTitle: "Iris"),
                  ),
                if (context
                        .read<BiometricCaptureControlProvider>()
                        .biometricAttribute ==
                    "Right Hand")
                  BiometricCaptureScanBlock(
                      title: "RightHandScan",
                      middleBlock: BiometricScanMiddleBlock(
                        biometricAttributeData: context
                            .read<BiometricCaptureControlProvider>()
                            .rightHand,
                        field: widget.field,
                        imageHeight: 190.h,
                        imageWidth: 190.h,
                        parameterTitle: "RightHand",
                      )),
                if (context
                        .read<BiometricCaptureControlProvider>()
                        .biometricAttribute ==
                    "Left Hand")
                  BiometricCaptureScanBlock(
                      title: "LeftHandScan",
                      middleBlock: BiometricScanMiddleBlock(
                        biometricAttributeData: context
                            .read<BiometricCaptureControlProvider>()
                            .leftHand,
                        field: widget.field,
                        imageHeight: 190.h,
                        imageWidth: 190.h,
                        parameterTitle: "LeftHand",
                      )),
                if (context
                        .read<BiometricCaptureControlProvider>()
                        .biometricAttribute ==
                    "Thumbs")
                  BiometricCaptureScanBlock(
                      title: "ThumbsScan",
                      middleBlock: BiometricScanMiddleBlock(
                        biometricAttributeData: context
                            .read<BiometricCaptureControlProvider>()
                            .thumbs,
                        field: widget.field,
                        imageHeight: 310.h,
                        imageWidth: 310.h,
                        parameterTitle: "Thumbs",
                      )),
                if (context
                        .read<BiometricCaptureControlProvider>()
                        .biometricAttribute ==
                    "Face")
                  BiometricCaptureScanBlock(
                      title: "FaceScan",
                      middleBlock: BiometricScanMiddleBlock(
                        biometricAttributeData: context
                            .read<BiometricCaptureControlProvider>()
                            .face,
                        field: widget.field,
                        imageHeight: 310.h,
                        imageWidth: 310.h,
                        parameterTitle: "Face",
                      )),
                if (context
                        .read<BiometricCaptureControlProvider>()
                        .biometricAttribute ==
                    "Exception")
                  BiometricCaptureScanBlock(
                      title: "ExceptionScan",
                      middleBlock: BiometricScanMiddleBlock(
                        biometricAttributeData: context
                            .read<BiometricCaptureControlProvider>()
                            .exception,
                        field: widget.field,
                        imageHeight: 310.h,
                        imageWidth: 310.h,
                        parameterTitle: "Exception",
                      )),
                if (context
                        .read<BiometricCaptureControlProvider>()
                        .biometricAttribute ==
                    "Iris")
                  BiometricCaptureExceptionBlock(
                    attribute:
                        context.read<BiometricCaptureControlProvider>().iris,
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
                                        if (!(context
                                                .read<
                                                    BiometricCaptureControlProvider>()
                                                .iris
                                                .exceptions
                                                .elementAt(0)) ==
                                            true) {
                                          await BiometricsApi().addBioException(
                                              widget.field.id!,
                                              "Iris",
                                              "leftEye");
                                          resetAfterException(
                                              widget.field.id!,
                                              context
                                                  .read<
                                                      BiometricCaptureControlProvider>()
                                                  .iris);
                                          context
                                              .read<
                                                  BiometricCaptureControlProvider>()
                                              .customSetterIris(
                                                  false, "isScanned");
                                          context
                                              .read<
                                                  BiometricCaptureControlProvider>()
                                              .customSetterIris(0, "attemptNo");
                                          context
                                              .read<
                                                  BiometricCaptureControlProvider>()
                                              .customSetterIris([
                                            "assets/images/Left Eye@2x.png",
                                            "assets/images/Right Eye@2x.png"
                                          ], "listofImages");
                                          List<BiometricsDto> listOfBiometrics =
                                              [];
                                          context
                                              .read<
                                                  BiometricCaptureControlProvider>()
                                              .customSetterIris(
                                                  listOfBiometrics, "listOfBiometricsDto");
                                          context
                                              .read<
                                                  BiometricCaptureControlProvider>()
                                              .customSetterIris(
                                                  0.0, "qualityPercentage");
                                          context
                                              .read<
                                                  BiometricCaptureControlProvider>()
                                              .customSetterIris(
                                                  "0", "thresholdPercentage");
                                        } else {
                                          await BiometricsApi()
                                              .removeBioException(
                                                  widget.field.id!,
                                                  "Iris",
                                                  "leftEye");
                                          resetAfterException(
                                              widget.field.id!,
                                              context
                                                  .read<
                                                      BiometricCaptureControlProvider>()
                                                  .iris);
                                          context
                                              .read<
                                                  BiometricCaptureControlProvider>()
                                              .customSetterIris(
                                                  false, "isScanned");
                                          context
                                              .read<
                                                  BiometricCaptureControlProvider>()
                                              .customSetterIris(0, "attemptNo");
                                          context
                                              .read<
                                                  BiometricCaptureControlProvider>()
                                              .customSetterIris([
                                            "assets/images/Left Eye@2x.png",
                                            "assets/images/Right Eye@2x.png"
                                          ], "listofImages");
                                          List<BiometricsDto> listOfBiometrics =
                                              [];
                                          context
                                              .read<
                                                  BiometricCaptureControlProvider>()
                                              .customSetterIris(
                                                  listOfBiometrics, "listOfBiometricsDto");
                                          context
                                              .read<
                                                  BiometricCaptureControlProvider>()
                                              .customSetterIris(
                                                  0, "qualityPercentage");
                                          context
                                              .read<
                                                  BiometricCaptureControlProvider>()
                                              .customSetterIris(
                                                  "0", "thresholdPercentage");
                                        }
                                        List<bool> exceptionListCopy = context
                                            .read<
                                                BiometricCaptureControlProvider>()
                                            .iris
                                            .exceptions;
                                        exceptionListCopy[0] =
                                            !(exceptionListCopy.elementAt(0));
                                        context
                                            .read<
                                                BiometricCaptureControlProvider>()
                                            .customSetterIris(exceptionListCopy,
                                                "exceptions");

                                        if (context
                                            .read<
                                                BiometricCaptureControlProvider>()
                                            .iris
                                            .exceptions
                                            .contains(true)) {
                                          if (context
                                              .read<
                                                  BiometricCaptureControlProvider>()
                                              .iris
                                              .exceptionType
                                              .isEmpty) {
                                            context
                                                .read<
                                                    BiometricCaptureControlProvider>()
                                                .customSetterIris("Permanent",
                                                    "exceptionType");
                                          }
                                        }
                                        if (!context
                                            .read<
                                                BiometricCaptureControlProvider>()
                                            .iris
                                            .exceptions
                                            .contains(true)) {
                                          context
                                              .read<
                                                  BiometricCaptureControlProvider>()
                                              .customSetterIris(
                                                  "", "exceptionType");
                                        }
                                      },
                                      child: Image.asset(
                                        "assets/images/Left Eye@2x.png",
                                        height: 72,
                                      ),
                                    ),
                                    (context
                                                .read<
                                                    BiometricCaptureControlProvider>()
                                                .iris
                                                .exceptions[0] ==
                                            true)
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
                                        if (!(context
                                                .read<
                                                    BiometricCaptureControlProvider>()
                                                .iris
                                                .exceptions
                                                .elementAt(1)) ==
                                            true) {
                                          await BiometricsApi().addBioException(
                                              widget.field.id!,
                                              "Iris",
                                              "rightEye");
                                          resetAfterException(
                                              widget.field.id!,
                                              context
                                                  .read<
                                                      BiometricCaptureControlProvider>()
                                                  .iris);
                                          context
                                              .read<
                                                  BiometricCaptureControlProvider>()
                                              .customSetterIris(
                                                  false, "isScanned");
                                          context
                                              .read<
                                                  BiometricCaptureControlProvider>()
                                              .customSetterIris(0, "attemptNo");
                                          context
                                              .read<
                                                  BiometricCaptureControlProvider>()
                                              .customSetterIris([
                                            "assets/images/Left Eye@2x.png",
                                            "assets/images/Right Eye@2x.png"
                                          ], "listofImages");
                                          List<BiometricsDto> listOfBiometrics =
                                              [];
                                          context
                                              .read<
                                                  BiometricCaptureControlProvider>()
                                              .customSetterIris(
                                                  listOfBiometrics, "listOfBiometricsDto");
                                          context
                                              .read<
                                                  BiometricCaptureControlProvider>()
                                              .customSetterIris(
                                                  0, "qualityPercentage");
                                          context
                                              .read<
                                                  BiometricCaptureControlProvider>()
                                              .customSetterIris(
                                                  "0", "thresholdPercentage");
                                        } else {
                                          await BiometricsApi()
                                              .removeBioException(
                                                  widget.field.id!,
                                                  "Iris",
                                                  "rightEye");
                                          resetAfterException(
                                              widget.field.id!,
                                              context
                                                  .read<
                                                      BiometricCaptureControlProvider>()
                                                  .iris);
                                          context
                                              .read<
                                                  BiometricCaptureControlProvider>()
                                              .customSetterIris(
                                                  false, "isScanned");
                                          context
                                              .read<
                                                  BiometricCaptureControlProvider>()
                                              .customSetterIris(0, "attemptNo");
                                          context
                                              .read<
                                                  BiometricCaptureControlProvider>()
                                              .customSetterIris([
                                            "assets/images/Left Eye@2x.png",
                                            "assets/images/Right Eye@2x.png"
                                          ], "listofImages");
                                          List<BiometricsDto> listOfBiometrics =
                                              [];
                                          context
                                              .read<
                                                  BiometricCaptureControlProvider>()
                                              .customSetterIris(
                                                  listOfBiometrics, "listOfBiometricsDto");
                                          context
                                              .read<
                                                  BiometricCaptureControlProvider>()
                                              .customSetterIris(
                                                  0, "qualityPercentage");
                                          context
                                              .read<
                                                  BiometricCaptureControlProvider>()
                                              .customSetterIris(
                                                  "0", "thresholdPercentage");
                                        }

                                        List<bool> exceptionListCopy = context
                                            .read<
                                                BiometricCaptureControlProvider>()
                                            .iris
                                            .exceptions;
                                        exceptionListCopy[1] =
                                            !(exceptionListCopy.elementAt(1));
                                        context
                                            .read<
                                                BiometricCaptureControlProvider>()
                                            .customSetterIris(exceptionListCopy,
                                                "exceptions");
                                        if (context
                                            .read<
                                                BiometricCaptureControlProvider>()
                                            .iris
                                            .exceptions
                                            .contains(true)) {
                                          if (context
                                              .read<
                                                  BiometricCaptureControlProvider>()
                                              .iris
                                              .exceptionType
                                              .isEmpty) {
                                            context
                                                .read<
                                                    BiometricCaptureControlProvider>()
                                                .customSetterIris("Permanent",
                                                    "exceptionType");
                                          }
                                        }
                                        if (!context
                                            .read<
                                                BiometricCaptureControlProvider>()
                                            .iris
                                            .exceptions
                                            .contains(true)) {
                                          context
                                              .read<
                                                  BiometricCaptureControlProvider>()
                                              .customSetterIris(
                                                  "", "exceptionType");
                                        }
                                      },
                                      child: Image.asset(
                                        "assets/images/Right Eye@2x.png",
                                        height: 72,
                                      ),
                                    ),
                                    (context
                                                .read<
                                                    BiometricCaptureControlProvider>()
                                                .iris
                                                .exceptions[1] ==
                                            true)
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
                if (context
                        .read<BiometricCaptureControlProvider>()
                        .biometricAttribute ==
                    "Right Hand")
                  BiometricCaptureExceptionBlock(
                    attribute: context
                        .read<BiometricCaptureControlProvider>()
                        .rightHand,
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
                                          if (!(context
                                                  .read<
                                                      BiometricCaptureControlProvider>()
                                                  .rightHand
                                                  .exceptions
                                                  .elementAt(0)) ==
                                              true) {
                                            await BiometricsApi()
                                                .addBioException(
                                                    widget.field.id!,
                                                    "RightHand",
                                                    "rightIndex");
                                            resetAfterException(
                                                widget.field.id!,
                                                context
                                                    .read<
                                                        BiometricCaptureControlProvider>()
                                                    .rightHand);
                                            context
                                                .read<
                                                    BiometricCaptureControlProvider>()
                                                .customSetterRightHand(
                                                    false, "isScanned");
                                            context
                                                .read<
                                                    BiometricCaptureControlProvider>()
                                                .customSetterRightHand(
                                                    0, "attemptNo");
                                            context
                                                .read<
                                                    BiometricCaptureControlProvider>()
                                                .customSetterRightHand([
                                              "assets/images/Right Hand@2x.png"
                                            ], "listofImages");
                                            List<BiometricsDto> listOfBiometrics =
                                              [];
                                            context
                                                .read<
                                                    BiometricCaptureControlProvider>()
                                                .customSetterRightHand(
                                                    listOfBiometrics, "listOfBiometricsDto");
                                            context
                                                .read<
                                                    BiometricCaptureControlProvider>()
                                                .customSetterRightHand(
                                                    0, "qualityPercentage");
                                            context
                                                .read<
                                                    BiometricCaptureControlProvider>()
                                                .customSetterRightHand(
                                                    "0", "thresholdPercentage");
                                          } else {
                                            await BiometricsApi()
                                                .removeBioException(
                                                    widget.field.id!,
                                                    "RightHand",
                                                    "rightIndex");
                                            resetAfterException(
                                                widget.field.id!,
                                                context
                                                    .read<
                                                        BiometricCaptureControlProvider>()
                                                    .rightHand);
                                            context
                                                .read<
                                                    BiometricCaptureControlProvider>()
                                                .customSetterRightHand(
                                                    false, "isScanned");
                                            context
                                                .read<
                                                    BiometricCaptureControlProvider>()
                                                .customSetterRightHand(
                                                    0, "attemptNo");
                                            context
                                                .read<
                                                    BiometricCaptureControlProvider>()
                                                .customSetterRightHand([
                                              "assets/images/Right Hand@2x.png"
                                            ], "listofImages");
                                            List<BiometricsDto> listOfBiometrics =
                                              [];
                                            context
                                                .read<
                                                    BiometricCaptureControlProvider>()
                                                .customSetterRightHand(
                                                    listOfBiometrics, "listOfBiometricsDto");
                                            context
                                                .read<
                                                    BiometricCaptureControlProvider>()
                                                .customSetterRightHand(
                                                    0, "qualityPercentage");
                                            context
                                                .read<
                                                    BiometricCaptureControlProvider>()
                                                .customSetterRightHand(
                                                    "0", "thresholdPercentage");
                                          }

                                          List<bool> exceptionListCopy = context
                                              .read<
                                                  BiometricCaptureControlProvider>()
                                              .rightHand
                                              .exceptions;
                                          exceptionListCopy[0] =
                                              !(exceptionListCopy.elementAt(0));
                                          context
                                              .read<
                                                  BiometricCaptureControlProvider>()
                                              .customSetterRightHand(
                                                  exceptionListCopy,
                                                  "exceptions");

                                          if (context
                                              .read<
                                                  BiometricCaptureControlProvider>()
                                              .rightHand
                                              .exceptions
                                              .contains(true)) {
                                            if (context
                                                .read<
                                                    BiometricCaptureControlProvider>()
                                                .rightHand
                                                .exceptionType
                                                .isEmpty) {
                                              context
                                                  .read<
                                                      BiometricCaptureControlProvider>()
                                                  .customSetterRightHand(
                                                      "Permanent",
                                                      "exceptionType");
                                            }
                                          }
                                          if (!context
                                              .read<
                                                  BiometricCaptureControlProvider>()
                                              .rightHand
                                              .exceptions
                                              .contains(true)) {
                                            context
                                                .read<
                                                    BiometricCaptureControlProvider>()
                                                .customSetterRightHand(
                                                    "", "exceptionType");
                                          }
                                        },
                                        child: Icon(
                                          Icons.cancel_rounded,
                                          color: (context
                                                      .read<
                                                          BiometricCaptureControlProvider>()
                                                      .rightHand
                                                      .exceptions[0] ==
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
                                          if (!(context
                                                  .read<
                                                      BiometricCaptureControlProvider>()
                                                  .rightHand
                                                  .exceptions
                                                  .elementAt(1)) ==
                                              true) {
                                            await BiometricsApi()
                                                .addBioException(
                                                    widget.field.id!,
                                                    "RightHand",
                                                    "rightMiddle");
                                            resetAfterException(
                                                widget.field.id!,
                                                context
                                                    .read<
                                                        BiometricCaptureControlProvider>()
                                                    .rightHand);

                                            context
                                                .read<
                                                    BiometricCaptureControlProvider>()
                                                .customSetterRightHand(
                                                    false, "isScanned");
                                            context
                                                .read<
                                                    BiometricCaptureControlProvider>()
                                                .customSetterRightHand(
                                                    0, "attemptNo");
                                            context
                                                .read<
                                                    BiometricCaptureControlProvider>()
                                                .customSetterRightHand([
                                              "assets/images/Right Hand@2x.png"
                                            ], "listofImages");
                                            List<BiometricsDto> listOfBiometrics =
                                              [];
                                            context
                                                .read<
                                                    BiometricCaptureControlProvider>()
                                                .customSetterRightHand(
                                                    listOfBiometrics, "listOfBiometricsDto");
                                            context
                                                .read<
                                                    BiometricCaptureControlProvider>()
                                                .customSetterRightHand(
                                                    0, "qualityPercentage");
                                            context
                                                .read<
                                                    BiometricCaptureControlProvider>()
                                                .customSetterRightHand(
                                                    "0", "thresholdPercentage");
                                          } else {
                                            await BiometricsApi()
                                                .removeBioException(
                                                    widget.field.id!,
                                                    "RightHand",
                                                    "rightMiddle");
                                            resetAfterException(
                                                widget.field.id!,
                                                context
                                                    .read<
                                                        BiometricCaptureControlProvider>()
                                                    .rightHand);
                                            context
                                                .read<
                                                    BiometricCaptureControlProvider>()
                                                .customSetterRightHand(
                                                    false, "isScanned");
                                            context
                                                .read<
                                                    BiometricCaptureControlProvider>()
                                                .customSetterRightHand(
                                                    0, "attemptNo");
                                            context
                                                .read<
                                                    BiometricCaptureControlProvider>()
                                                .customSetterRightHand([
                                              "assets/images/Right Hand@2x.png"
                                            ], "listofImages");
                                            List<BiometricsDto> listOfBiometrics =
                                              [];
                                            context
                                                .read<
                                                    BiometricCaptureControlProvider>()
                                                .customSetterRightHand(
                                                    listOfBiometrics, "listOfBiometricsDto");
                                            context
                                                .read<
                                                    BiometricCaptureControlProvider>()
                                                .customSetterRightHand(
                                                    0, "qualityPercentage");
                                            context
                                                .read<
                                                    BiometricCaptureControlProvider>()
                                                .customSetterRightHand(
                                                    "0", "thresholdPercentage");
                                          }
                                          List<bool> exceptionListCopy = context
                                              .read<
                                                  BiometricCaptureControlProvider>()
                                              .rightHand
                                              .exceptions;
                                          exceptionListCopy[1] =
                                              !(exceptionListCopy.elementAt(1));
                                          context
                                              .read<
                                                  BiometricCaptureControlProvider>()
                                              .customSetterRightHand(
                                                  exceptionListCopy,
                                                  "exceptions");

                                          if (context
                                              .read<
                                                  BiometricCaptureControlProvider>()
                                              .rightHand
                                              .exceptions
                                              .contains(true)) {
                                            if (context
                                                .read<
                                                    BiometricCaptureControlProvider>()
                                                .rightHand
                                                .exceptionType
                                                .isEmpty) {
                                              context
                                                  .read<
                                                      BiometricCaptureControlProvider>()
                                                  .customSetterRightHand(
                                                      "Permanent",
                                                      "exceptionType");
                                            }
                                          }
                                          if (!context
                                              .read<
                                                  BiometricCaptureControlProvider>()
                                              .rightHand
                                              .exceptions
                                              .contains(true)) {
                                            context
                                                .read<
                                                    BiometricCaptureControlProvider>()
                                                .customSetterRightHand(
                                                    "", "exceptionType");
                                          }
                                        },
                                        child: Icon(
                                          Icons.cancel_rounded,
                                          color: (context
                                                      .read<
                                                          BiometricCaptureControlProvider>()
                                                      .rightHand
                                                      .exceptions[1] ==
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
                                          if (!(context
                                                  .read<
                                                      BiometricCaptureControlProvider>()
                                                  .rightHand
                                                  .exceptions
                                                  .elementAt(2)) ==
                                              true) {
                                            await BiometricsApi()
                                                .addBioException(
                                                    widget.field.id!,
                                                    "RightHand",
                                                    "rightRing");
                                            resetAfterException(
                                                widget.field.id!,
                                                context
                                                    .read<
                                                        BiometricCaptureControlProvider>()
                                                    .rightHand);
                                            context
                                                .read<
                                                    BiometricCaptureControlProvider>()
                                                .customSetterRightHand(
                                                    false, "isScanned");
                                            context
                                                .read<
                                                    BiometricCaptureControlProvider>()
                                                .customSetterRightHand(
                                                    0, "attemptNo");
                                            context
                                                .read<
                                                    BiometricCaptureControlProvider>()
                                                .customSetterRightHand([
                                              "assets/images/Right Hand@2x.png"
                                            ], "listofImages");
                                            List<BiometricsDto> listOfBiometrics =
                                              [];
                                            context
                                                .read<
                                                    BiometricCaptureControlProvider>()
                                                .customSetterRightHand(
                                                    listOfBiometrics, "listOfBiometricsDto");
                                            context
                                                .read<
                                                    BiometricCaptureControlProvider>()
                                                .customSetterRightHand(
                                                    0, "qualityPercentage");
                                            context
                                                .read<
                                                    BiometricCaptureControlProvider>()
                                                .customSetterRightHand(
                                                    "0", "thresholdPercentage");
                                          } else {
                                            await BiometricsApi()
                                                .removeBioException(
                                                    widget.field.id!,
                                                    "RightHand",
                                                    "rightRing");
                                            resetAfterException(
                                                widget.field.id!,
                                                context
                                                    .read<
                                                        BiometricCaptureControlProvider>()
                                                    .rightHand);
                                            context
                                                .read<
                                                    BiometricCaptureControlProvider>()
                                                .customSetterRightHand(
                                                    false, "isScanned");
                                            context
                                                .read<
                                                    BiometricCaptureControlProvider>()
                                                .customSetterRightHand(
                                                    0, "attemptNo");
                                            context
                                                .read<
                                                    BiometricCaptureControlProvider>()
                                                .customSetterRightHand([
                                              "assets/images/Right Hand@2x.png"
                                            ], "listofImages");
                                            List<BiometricsDto> listOfBiometrics =
                                              [];
                                            context
                                                .read<
                                                    BiometricCaptureControlProvider>()
                                                .customSetterRightHand(
                                                    listOfBiometrics, "listOfBiometricsDto");
                                            context
                                                .read<
                                                    BiometricCaptureControlProvider>()
                                                .customSetterRightHand(
                                                    0, "qualityPercentage");
                                            context
                                                .read<
                                                    BiometricCaptureControlProvider>()
                                                .customSetterRightHand(
                                                    "0", "thresholdPercentage");
                                          }
                                          List<bool> exceptionListCopy = context
                                              .read<
                                                  BiometricCaptureControlProvider>()
                                              .rightHand
                                              .exceptions;
                                          exceptionListCopy[2] =
                                              !(exceptionListCopy.elementAt(2));
                                          context
                                              .read<
                                                  BiometricCaptureControlProvider>()
                                              .customSetterRightHand(
                                                  exceptionListCopy,
                                                  "exceptions");

                                          if (context
                                              .read<
                                                  BiometricCaptureControlProvider>()
                                              .rightHand
                                              .exceptions
                                              .contains(true)) {
                                            if (context
                                                .read<
                                                    BiometricCaptureControlProvider>()
                                                .rightHand
                                                .exceptionType
                                                .isEmpty) {
                                              context
                                                  .read<
                                                      BiometricCaptureControlProvider>()
                                                  .customSetterRightHand(
                                                      "Permanent",
                                                      "exceptionType");
                                            }
                                          }
                                          if (!context
                                              .read<
                                                  BiometricCaptureControlProvider>()
                                              .rightHand
                                              .exceptions
                                              .contains(true)) {
                                            context
                                                .read<
                                                    BiometricCaptureControlProvider>()
                                                .customSetterRightHand(
                                                    "", "exceptionType");
                                          }
                                        },
                                        child: Icon(
                                          Icons.cancel_rounded,
                                          color: (context
                                                      .read<
                                                          BiometricCaptureControlProvider>()
                                                      .rightHand
                                                      .exceptions[2] ==
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
                                          if (!(context
                                                  .read<
                                                      BiometricCaptureControlProvider>()
                                                  .rightHand
                                                  .exceptions
                                                  .elementAt(3)) ==
                                              true) {
                                            await BiometricsApi()
                                                .addBioException(
                                                    widget.field.id!,
                                                    "RightHand",
                                                    "rightLittle");
                                            resetAfterException(
                                                widget.field.id!,
                                                context
                                                    .read<
                                                        BiometricCaptureControlProvider>()
                                                    .rightHand);
                                            context
                                                .read<
                                                    BiometricCaptureControlProvider>()
                                                .customSetterRightHand(
                                                    false, "isScanned");
                                            context
                                                .read<
                                                    BiometricCaptureControlProvider>()
                                                .customSetterRightHand(
                                                    0, "attemptNo");
                                            context
                                                .read<
                                                    BiometricCaptureControlProvider>()
                                                .customSetterRightHand([
                                              "assets/images/Right Hand@2x.png"
                                            ], "listofImages");
                                            List<BiometricsDto> listOfBiometrics =
                                              [];
                                            context
                                                .read<
                                                    BiometricCaptureControlProvider>()
                                                .customSetterRightHand(
                                                    listOfBiometrics, "listOfBiometricsDto");
                                            context
                                                .read<
                                                    BiometricCaptureControlProvider>()
                                                .customSetterRightHand(
                                                    0, "qualityPercentage");
                                            context
                                                .read<
                                                    BiometricCaptureControlProvider>()
                                                .customSetterRightHand(
                                                    "0", "thresholdPercentage");
                                          } else {
                                            await BiometricsApi()
                                                .removeBioException(
                                                    widget.field.id!,
                                                    "RightHand",
                                                    "rightLittle");
                                            resetAfterException(
                                                widget.field.id!,
                                                context
                                                    .read<
                                                        BiometricCaptureControlProvider>()
                                                    .rightHand);
                                            context
                                                .read<
                                                    BiometricCaptureControlProvider>()
                                                .customSetterRightHand(
                                                    false, "isScanned");
                                            context
                                                .read<
                                                    BiometricCaptureControlProvider>()
                                                .customSetterRightHand(
                                                    0, "attemptNo");
                                            context
                                                .read<
                                                    BiometricCaptureControlProvider>()
                                                .customSetterRightHand([
                                              "assets/images/Right Hand@2x.png"
                                            ], "listofImages");
                                            List<BiometricsDto> listOfBiometrics =
                                              [];
                                            context
                                                .read<
                                                    BiometricCaptureControlProvider>()
                                                .customSetterRightHand(
                                                    listOfBiometrics, "listOfBiometricsDto");
                                            context
                                                .read<
                                                    BiometricCaptureControlProvider>()
                                                .customSetterRightHand(
                                                    0, "qualityPercentage");
                                            context
                                                .read<
                                                    BiometricCaptureControlProvider>()
                                                .customSetterRightHand(
                                                    "0", "thresholdPercentage");
                                          }
                                          List<bool> exceptionListCopy = context
                                              .read<
                                                  BiometricCaptureControlProvider>()
                                              .rightHand
                                              .exceptions;
                                          exceptionListCopy[3] =
                                              !(exceptionListCopy.elementAt(3));
                                          context
                                              .read<
                                                  BiometricCaptureControlProvider>()
                                              .customSetterRightHand(
                                                  exceptionListCopy,
                                                  "exceptions");

                                          if (context
                                              .read<
                                                  BiometricCaptureControlProvider>()
                                              .rightHand
                                              .exceptions
                                              .contains(true)) {
                                            if (context
                                                .read<
                                                    BiometricCaptureControlProvider>()
                                                .rightHand
                                                .exceptionType
                                                .isEmpty) {
                                              context
                                                  .read<
                                                      BiometricCaptureControlProvider>()
                                                  .customSetterRightHand(
                                                      "Permanent",
                                                      "exceptionType");
                                            }
                                          }
                                          if (!context
                                              .read<
                                                  BiometricCaptureControlProvider>()
                                              .rightHand
                                              .exceptions
                                              .contains(true)) {
                                            context
                                                .read<
                                                    BiometricCaptureControlProvider>()
                                                .customSetterRightHand(
                                                    "", "exceptionType");
                                          }
                                        },
                                        child: Icon(
                                          Icons.cancel_rounded,
                                          color: (context
                                                      .read<
                                                          BiometricCaptureControlProvider>()
                                                      .rightHand
                                                      .exceptions[3] ==
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
                if (context
                        .read<BiometricCaptureControlProvider>()
                        .biometricAttribute ==
                    "Left Hand")
                  BiometricCaptureExceptionBlock(
                    attribute: context
                        .read<BiometricCaptureControlProvider>()
                        .leftHand,
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
                                          if (!(context
                                                  .read<
                                                      BiometricCaptureControlProvider>()
                                                  .leftHand
                                                  .exceptions
                                                  .elementAt(0)) ==
                                              true) {
                                            await BiometricsApi()
                                                .addBioException(
                                                    widget.field.id!,
                                                    "LeftHand",
                                                    "leftIndex");
                                            resetAfterException(
                                                widget.field.id!,
                                                context
                                                    .read<
                                                        BiometricCaptureControlProvider>()
                                                    .leftHand);
                                            context
                                                .read<
                                                    BiometricCaptureControlProvider>()
                                                .customSetterLeftHand(
                                                    false, "isScanned");
                                            context
                                                .read<
                                                    BiometricCaptureControlProvider>()
                                                .customSetterLeftHand(
                                                    0, "attemptNo");
                                            context
                                                .read<
                                                    BiometricCaptureControlProvider>()
                                                .customSetterLeftHand([
                                              "assets/images/Left Hand@2x.png"
                                            ], "listofImages");
                                            List<BiometricsDto> listOfBiometrics =
                                              [];
                                            context
                                                .read<
                                                    BiometricCaptureControlProvider>()
                                                .customSetterLeftHand(
                                                    listOfBiometrics, "listOfBiometricsDto");
                                            context
                                                .read<
                                                    BiometricCaptureControlProvider>()
                                                .customSetterLeftHand(
                                                    0, "qualityPercentage");
                                            context
                                                .read<
                                                    BiometricCaptureControlProvider>()
                                                .customSetterLeftHand(
                                                    "0", "thresholdPercentage");
                                          } else {
                                            await BiometricsApi()
                                                .removeBioException(
                                                    widget.field.id!,
                                                    "LeftHand",
                                                    "leftIndex");
                                            resetAfterException(
                                                widget.field.id!,
                                                context
                                                    .read<
                                                        BiometricCaptureControlProvider>()
                                                    .leftHand);
                                            context
                                                .read<
                                                    BiometricCaptureControlProvider>()
                                                .customSetterLeftHand(
                                                    false, "isScanned");
                                            context
                                                .read<
                                                    BiometricCaptureControlProvider>()
                                                .customSetterLeftHand(
                                                    0, "attemptNo");
                                            context
                                                .read<
                                                    BiometricCaptureControlProvider>()
                                                .customSetterLeftHand([
                                              "assets/images/Left Hand@2x.png"
                                            ], "listofImages");
                                            List<BiometricsDto> listOfBiometrics =
                                              [];
                                            context
                                                .read<
                                                    BiometricCaptureControlProvider>()
                                                .customSetterLeftHand(
                                                    listOfBiometrics, "listOfBiometricsDto");
                                            context
                                                .read<
                                                    BiometricCaptureControlProvider>()
                                                .customSetterLeftHand(
                                                    0, "qualityPercentage");
                                            context
                                                .read<
                                                    BiometricCaptureControlProvider>()
                                                .customSetterLeftHand(
                                                    "0", "thresholdPercentage");
                                          }
                                          List<bool> exceptionListCopy = context
                                              .read<
                                                  BiometricCaptureControlProvider>()
                                              .leftHand
                                              .exceptions;
                                          exceptionListCopy[0] =
                                              !(exceptionListCopy.elementAt(0));
                                          context
                                              .read<
                                                  BiometricCaptureControlProvider>()
                                              .customSetterLeftHand(
                                                  exceptionListCopy,
                                                  "exceptions");

                                          if (context
                                              .read<
                                                  BiometricCaptureControlProvider>()
                                              .leftHand
                                              .exceptions
                                              .contains(true)) {
                                            if (context
                                                .read<
                                                    BiometricCaptureControlProvider>()
                                                .leftHand
                                                .exceptionType
                                                .isEmpty) {
                                              context
                                                  .read<
                                                      BiometricCaptureControlProvider>()
                                                  .customSetterLeftHand(
                                                      "Permanent",
                                                      "exceptionType");
                                            }
                                          }
                                          if (!context
                                              .read<
                                                  BiometricCaptureControlProvider>()
                                              .leftHand
                                              .exceptions
                                              .contains(true)) {
                                            context
                                                .read<
                                                    BiometricCaptureControlProvider>()
                                                .customSetterLeftHand(
                                                    "", "exceptionType");
                                          }
                                        },
                                        child: Icon(
                                          Icons.cancel_rounded,
                                          color: (context
                                                      .read<
                                                          BiometricCaptureControlProvider>()
                                                      .leftHand
                                                      .exceptions[0] ==
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
                                          if (!(context
                                                  .read<
                                                      BiometricCaptureControlProvider>()
                                                  .leftHand
                                                  .exceptions
                                                  .elementAt(1)) ==
                                              true) {
                                            await BiometricsApi()
                                                .addBioException(
                                                    widget.field.id!,
                                                    "LeftHand",
                                                    "leftMiddle");
                                            resetAfterException(
                                                widget.field.id!,
                                                context
                                                    .read<
                                                        BiometricCaptureControlProvider>()
                                                    .leftHand);
                                            context
                                                .read<
                                                    BiometricCaptureControlProvider>()
                                                .customSetterLeftHand(
                                                    false, "isScanned");
                                            context
                                                .read<
                                                    BiometricCaptureControlProvider>()
                                                .customSetterLeftHand(
                                                    0, "attemptNo");
                                            context
                                                .read<
                                                    BiometricCaptureControlProvider>()
                                                .customSetterLeftHand([
                                              "assets/images/Left Hand@2x.png"
                                            ], "listofImages");
                                            List<BiometricsDto> listOfBiometrics =
                                              [];
                                            context
                                                .read<
                                                    BiometricCaptureControlProvider>()
                                                .customSetterLeftHand(
                                                    listOfBiometrics, "listOfBiometricsDto");
                                            context
                                                .read<
                                                    BiometricCaptureControlProvider>()
                                                .customSetterLeftHand(
                                                    0, "qualityPercentage");
                                            context
                                                .read<
                                                    BiometricCaptureControlProvider>()
                                                .customSetterLeftHand(
                                                    "0", "thresholdPercentage");
                                          } else {
                                            await BiometricsApi()
                                                .removeBioException(
                                                    widget.field.id!,
                                                    "LeftHand",
                                                    "leftMiddle");
                                            resetAfterException(
                                                widget.field.id!,
                                                context
                                                    .read<
                                                        BiometricCaptureControlProvider>()
                                                    .leftHand);
                                            context
                                                .read<
                                                    BiometricCaptureControlProvider>()
                                                .customSetterLeftHand(
                                                    false, "isScanned");
                                            context
                                                .read<
                                                    BiometricCaptureControlProvider>()
                                                .customSetterLeftHand(
                                                    0, "attemptNo");
                                            context
                                                .read<
                                                    BiometricCaptureControlProvider>()
                                                .customSetterLeftHand([
                                              "assets/images/Left Hand@2x.png"
                                            ], "listofImages");
                                            List<BiometricsDto> listOfBiometrics =
                                              [];
                                            context
                                                .read<
                                                    BiometricCaptureControlProvider>()
                                                .customSetterLeftHand(
                                                    listOfBiometrics, "listOfBiometricsDto");
                                            context
                                                .read<
                                                    BiometricCaptureControlProvider>()
                                                .customSetterLeftHand(
                                                    0, "qualityPercentage");
                                            context
                                                .read<
                                                    BiometricCaptureControlProvider>()
                                                .customSetterLeftHand(
                                                    "0", "thresholdPercentage");
                                          }
                                          List<bool> exceptionListCopy = context
                                              .read<
                                                  BiometricCaptureControlProvider>()
                                              .leftHand
                                              .exceptions;
                                          exceptionListCopy[1] =
                                              !(exceptionListCopy.elementAt(1));
                                          context
                                              .read<
                                                  BiometricCaptureControlProvider>()
                                              .customSetterLeftHand(
                                                  exceptionListCopy,
                                                  "exceptions");

                                          if (context
                                              .read<
                                                  BiometricCaptureControlProvider>()
                                              .leftHand
                                              .exceptions
                                              .contains(true)) {
                                            if (context
                                                .read<
                                                    BiometricCaptureControlProvider>()
                                                .leftHand
                                                .exceptionType
                                                .isEmpty) {
                                              context
                                                  .read<
                                                      BiometricCaptureControlProvider>()
                                                  .customSetterLeftHand(
                                                      "Permanent",
                                                      "exceptionType");
                                            }
                                          }
                                          if (!context
                                              .read<
                                                  BiometricCaptureControlProvider>()
                                              .leftHand
                                              .exceptions
                                              .contains(true)) {
                                            context
                                                .read<
                                                    BiometricCaptureControlProvider>()
                                                .customSetterLeftHand(
                                                    "", "exceptionType");
                                          }
                                        },
                                        child: Icon(
                                          Icons.cancel_rounded,
                                          color: (context
                                                      .read<
                                                          BiometricCaptureControlProvider>()
                                                      .leftHand
                                                      .exceptions[1] ==
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
                                          if (!(context
                                                  .read<
                                                      BiometricCaptureControlProvider>()
                                                  .leftHand
                                                  .exceptions
                                                  .elementAt(2)) ==
                                              true) {
                                            await BiometricsApi()
                                                .addBioException(
                                                    widget.field.id!,
                                                    "LeftHand",
                                                    "leftRing");
                                            resetAfterException(
                                                widget.field.id!,
                                                context
                                                    .read<
                                                        BiometricCaptureControlProvider>()
                                                    .leftHand);
                                            context
                                                .read<
                                                    BiometricCaptureControlProvider>()
                                                .customSetterLeftHand(
                                                    false, "isScanned");
                                            context
                                                .read<
                                                    BiometricCaptureControlProvider>()
                                                .customSetterLeftHand(
                                                    0, "attemptNo");
                                            context
                                                .read<
                                                    BiometricCaptureControlProvider>()
                                                .customSetterLeftHand([
                                              "assets/images/Left Hand@2x.png"
                                            ], "listofImages");
                                            List<BiometricsDto> listOfBiometrics =
                                              [];
                                            context
                                                .read<
                                                    BiometricCaptureControlProvider>()
                                                .customSetterLeftHand(
                                                    listOfBiometrics, "listOfBiometricsDto");
                                            context
                                                .read<
                                                    BiometricCaptureControlProvider>()
                                                .customSetterLeftHand(
                                                    0, "qualityPercentage");
                                            context
                                                .read<
                                                    BiometricCaptureControlProvider>()
                                                .customSetterLeftHand(
                                                    "0", "thresholdPercentage");
                                          } else {
                                            await BiometricsApi()
                                                .removeBioException(
                                                    widget.field.id!,
                                                    "LeftHand",
                                                    "leftRing");
                                            resetAfterException(
                                                widget.field.id!,
                                                context
                                                    .read<
                                                        BiometricCaptureControlProvider>()
                                                    .leftHand);
                                            context
                                                .read<
                                                    BiometricCaptureControlProvider>()
                                                .customSetterLeftHand(
                                                    false, "isScanned");
                                            context
                                                .read<
                                                    BiometricCaptureControlProvider>()
                                                .customSetterLeftHand(
                                                    0, "attemptNo");
                                            context
                                                .read<
                                                    BiometricCaptureControlProvider>()
                                                .customSetterLeftHand([
                                              "assets/images/Left Hand@2x.png"
                                            ], "listofImages");
                                            List<BiometricsDto> listOfBiometrics =
                                              [];
                                            context
                                                .read<
                                                    BiometricCaptureControlProvider>()
                                                .customSetterLeftHand(
                                                    listOfBiometrics, "listOfBiometricsDto");
                                            context
                                                .read<
                                                    BiometricCaptureControlProvider>()
                                                .customSetterLeftHand(
                                                    0, "qualityPercentage");
                                            context
                                                .read<
                                                    BiometricCaptureControlProvider>()
                                                .customSetterLeftHand(
                                                    "0", "thresholdPercentage");
                                          }
                                          List<bool> exceptionListCopy = context
                                              .read<
                                                  BiometricCaptureControlProvider>()
                                              .leftHand
                                              .exceptions;
                                          exceptionListCopy[2] =
                                              !(exceptionListCopy.elementAt(2));
                                          context
                                              .read<
                                                  BiometricCaptureControlProvider>()
                                              .customSetterLeftHand(
                                                  exceptionListCopy,
                                                  "exceptions");

                                          if (context
                                              .read<
                                                  BiometricCaptureControlProvider>()
                                              .leftHand
                                              .exceptions
                                              .contains(true)) {
                                            if (context
                                                .read<
                                                    BiometricCaptureControlProvider>()
                                                .leftHand
                                                .exceptionType
                                                .isEmpty) {
                                              context
                                                  .read<
                                                      BiometricCaptureControlProvider>()
                                                  .customSetterLeftHand(
                                                      "Permanent",
                                                      "exceptionType");
                                            }
                                          }
                                          if (!context
                                              .read<
                                                  BiometricCaptureControlProvider>()
                                              .leftHand
                                              .exceptions
                                              .contains(true)) {
                                            context
                                                .read<
                                                    BiometricCaptureControlProvider>()
                                                .customSetterLeftHand(
                                                    "", "exceptionType");
                                          }
                                        },
                                        child: Icon(
                                          Icons.cancel_rounded,
                                          color: (context
                                                      .read<
                                                          BiometricCaptureControlProvider>()
                                                      .leftHand
                                                      .exceptions[2] ==
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
                                          if (!(context
                                                  .read<
                                                      BiometricCaptureControlProvider>()
                                                  .leftHand
                                                  .exceptions
                                                  .elementAt(3)) ==
                                              true) {
                                            await BiometricsApi()
                                                .addBioException(
                                                    widget.field.id!,
                                                    "LeftHand",
                                                    "leftLittle");
                                            resetAfterException(
                                                widget.field.id!,
                                                context
                                                    .read<
                                                        BiometricCaptureControlProvider>()
                                                    .leftHand);
                                            context
                                                .read<
                                                    BiometricCaptureControlProvider>()
                                                .customSetterLeftHand(
                                                    false, "isScanned");
                                            context
                                                .read<
                                                    BiometricCaptureControlProvider>()
                                                .customSetterLeftHand(
                                                    0, "attemptNo");
                                            context
                                                .read<
                                                    BiometricCaptureControlProvider>()
                                                .customSetterLeftHand([
                                              "assets/images/Left Hand@2x.png"
                                            ], "listofImages");
                                            List<BiometricsDto> listOfBiometrics =
                                              [];
                                            context
                                                .read<
                                                    BiometricCaptureControlProvider>()
                                                .customSetterLeftHand(
                                                    listOfBiometrics, "listOfBiometricsDto");
                                            context
                                                .read<
                                                    BiometricCaptureControlProvider>()
                                                .customSetterLeftHand(
                                                    0, "qualityPercentage");
                                            context
                                                .read<
                                                    BiometricCaptureControlProvider>()
                                                .customSetterLeftHand(
                                                    "0", "thresholdPercentage");
                                          } else {
                                            await BiometricsApi()
                                                .removeBioException(
                                                    widget.field.id!,
                                                    "LeftHand",
                                                    "leftLittle");
                                            resetAfterException(
                                                widget.field.id!,
                                                context
                                                    .read<
                                                        BiometricCaptureControlProvider>()
                                                    .leftHand);
                                            context
                                                .read<
                                                    BiometricCaptureControlProvider>()
                                                .customSetterLeftHand(
                                                    false, "isScanned");
                                            context
                                                .read<
                                                    BiometricCaptureControlProvider>()
                                                .customSetterLeftHand(
                                                    0, "attemptNo");
                                            context
                                                .read<
                                                    BiometricCaptureControlProvider>()
                                                .customSetterLeftHand([
                                              "assets/images/Left Hand@2x.png"
                                            ], "listofImages");
                                            List<BiometricsDto> listOfBiometrics =
                                              [];
                                            context
                                                .read<
                                                    BiometricCaptureControlProvider>()
                                                .customSetterLeftHand(
                                                    listOfBiometrics, "listOfBiometricsDto");
                                            context
                                                .read<
                                                    BiometricCaptureControlProvider>()
                                                .customSetterLeftHand(
                                                    0, "qualityPercentage");
                                            context
                                                .read<
                                                    BiometricCaptureControlProvider>()
                                                .customSetterLeftHand(
                                                    "0", "thresholdPercentage");
                                          }
                                          List<bool> exceptionListCopy = context
                                              .read<
                                                  BiometricCaptureControlProvider>()
                                              .leftHand
                                              .exceptions;
                                          exceptionListCopy[3] =
                                              !(exceptionListCopy.elementAt(3));
                                          context
                                              .read<
                                                  BiometricCaptureControlProvider>()
                                              .customSetterLeftHand(
                                                  exceptionListCopy,
                                                  "exceptions");

                                          if (context
                                              .read<
                                                  BiometricCaptureControlProvider>()
                                              .leftHand
                                              .exceptions
                                              .contains(true)) {
                                            if (context
                                                .read<
                                                    BiometricCaptureControlProvider>()
                                                .leftHand
                                                .exceptionType
                                                .isEmpty) {
                                              context
                                                  .read<
                                                      BiometricCaptureControlProvider>()
                                                  .customSetterLeftHand(
                                                      "Permanent",
                                                      "exceptionType");
                                            }
                                          }
                                          if (!context
                                              .read<
                                                  BiometricCaptureControlProvider>()
                                              .leftHand
                                              .exceptions
                                              .contains(true)) {
                                            context
                                                .read<
                                                    BiometricCaptureControlProvider>()
                                                .customSetterLeftHand(
                                                    "", "exceptionType");
                                          }
                                        },
                                        child: Icon(
                                          Icons.cancel_rounded,
                                          color: (context
                                                      .read<
                                                          BiometricCaptureControlProvider>()
                                                      .leftHand
                                                      .exceptions[3] ==
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
                if (context
                        .read<BiometricCaptureControlProvider>()
                        .biometricAttribute ==
                    "Thumbs")
                  BiometricCaptureExceptionBlock(
                    attribute:
                        context.read<BiometricCaptureControlProvider>().thumbs,
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
                                          if (!(context
                                                  .read<
                                                      BiometricCaptureControlProvider>()
                                                  .thumbs
                                                  .exceptions
                                                  .elementAt(0)) ==
                                              true) {
                                            await BiometricsApi()
                                                .addBioException(
                                                    widget.field.id!,
                                                    "Thumbs",
                                                    "leftThumb");
                                            resetAfterException(
                                                widget.field.id!,
                                                context
                                                    .read<
                                                        BiometricCaptureControlProvider>()
                                                    .thumbs);

                                            context
                                                .read<
                                                    BiometricCaptureControlProvider>()
                                                .customSetterThumbs(
                                                    false, "isScanned");
                                            context
                                                .read<
                                                    BiometricCaptureControlProvider>()
                                                .customSetterThumbs(
                                                    0, "attemptNo");
                                            context
                                                .read<
                                                    BiometricCaptureControlProvider>()
                                                .customSetterThumbs([
                                              "assets/images/Thumbs@2x.png"
                                            ], "listofImages");
                                            List<BiometricsDto> listOfBiometrics =
                                              [];
                                            context
                                                .read<
                                                    BiometricCaptureControlProvider>()
                                                .customSetterThumbs(
                                                    listOfBiometrics, "listOfBiometricsDto");
                                            context
                                                .read<
                                                    BiometricCaptureControlProvider>()
                                                .customSetterThumbs(
                                                    0, "qualityPercentage");
                                            context
                                                .read<
                                                    BiometricCaptureControlProvider>()
                                                .customSetterThumbs(
                                                    "0", "thresholdPercentage");
                                          } else {
                                            await BiometricsApi()
                                                .removeBioException(
                                                    widget.field.id!,
                                                    "Thumbs",
                                                    "leftThumb");
                                            resetAfterException(
                                                widget.field.id!,
                                                context
                                                    .read<
                                                        BiometricCaptureControlProvider>()
                                                    .thumbs);
                                            context
                                                .read<
                                                    BiometricCaptureControlProvider>()
                                                .customSetterThumbs(
                                                    false, "isScanned");
                                            context
                                                .read<
                                                    BiometricCaptureControlProvider>()
                                                .customSetterThumbs(
                                                    0, "attemptNo");
                                            context
                                                .read<
                                                    BiometricCaptureControlProvider>()
                                                .customSetterThumbs([
                                              "assets/images/Thumbs@2x.png"
                                            ], "listofImages");
                                            List<BiometricsDto> listOfBiometrics =
                                              [];
                                            context
                                                .read<
                                                    BiometricCaptureControlProvider>()
                                                .customSetterThumbs(
                                                    listOfBiometrics, "listOfBiometricsDto");
                                            context
                                                .read<
                                                    BiometricCaptureControlProvider>()
                                                .customSetterThumbs(
                                                    0, "qualityPercentage");
                                            context
                                                .read<
                                                    BiometricCaptureControlProvider>()
                                                .customSetterThumbs(
                                                    "0", "thresholdPercentage");
                                          }

                                          List<bool> exceptionListCopy = context
                                              .read<
                                                  BiometricCaptureControlProvider>()
                                              .thumbs
                                              .exceptions;
                                          exceptionListCopy[0] =
                                              !(exceptionListCopy.elementAt(0));
                                          context
                                              .read<
                                                  BiometricCaptureControlProvider>()
                                              .customSetterThumbs(
                                                  exceptionListCopy,
                                                  "exceptions");
                                          if (context
                                              .read<
                                                  BiometricCaptureControlProvider>()
                                              .thumbs
                                              .exceptions
                                              .contains(true)) {
                                            if (context
                                                .read<
                                                    BiometricCaptureControlProvider>()
                                                .thumbs
                                                .exceptionType
                                                .isEmpty) {
                                              context
                                                  .read<
                                                      BiometricCaptureControlProvider>()
                                                  .customSetterThumbs(
                                                      "Permanent",
                                                      "exceptionType");
                                            }
                                          }
                                          if (!context
                                              .read<
                                                  BiometricCaptureControlProvider>()
                                              .thumbs
                                              .exceptions
                                              .contains(true)) {
                                            context
                                                .read<
                                                    BiometricCaptureControlProvider>()
                                                .customSetterThumbs(
                                                    "", "exceptionType");
                                          }
                                        },
                                        child: Icon(
                                          Icons.cancel_rounded,
                                          color: (context
                                                      .read<
                                                          BiometricCaptureControlProvider>()
                                                      .thumbs
                                                      .exceptions[0] ==
                                                  true)
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
                                          if (!(context
                                                  .read<
                                                      BiometricCaptureControlProvider>()
                                                  .thumbs
                                                  .exceptions
                                                  .elementAt(1)) ==
                                              true) {
                                            await BiometricsApi()
                                                .addBioException(
                                                    widget.field.id!,
                                                    "Thumbs",
                                                    "rightThumb");
                                            resetAfterException(
                                                widget.field.id!,
                                                context
                                                    .read<
                                                        BiometricCaptureControlProvider>()
                                                    .thumbs);
                                            context
                                                .read<
                                                    BiometricCaptureControlProvider>()
                                                .customSetterThumbs(
                                                    false, "isScanned");
                                            context
                                                .read<
                                                    BiometricCaptureControlProvider>()
                                                .customSetterThumbs(
                                                    0, "attemptNo");
                                            context
                                                .read<
                                                    BiometricCaptureControlProvider>()
                                                .customSetterThumbs([
                                              "assets/images/Thumbs@2x.png"
                                            ], "listofImages");
                                            List<BiometricsDto> listOfBiometrics =
                                              [];
                                            context
                                                .read<
                                                    BiometricCaptureControlProvider>()
                                                .customSetterThumbs(
                                                   listOfBiometrics, "listOfBiometricsDto");
                                            context
                                                .read<
                                                    BiometricCaptureControlProvider>()
                                                .customSetterThumbs(
                                                    0, "qualityPercentage");
                                            context
                                                .read<
                                                    BiometricCaptureControlProvider>()
                                                .customSetterThumbs(
                                                    "0", "thresholdPercentage");
                                          } else {
                                            await BiometricsApi()
                                                .removeBioException(
                                                    widget.field.id!,
                                                    "Thumbs",
                                                    "rightThumb");
                                            resetAfterException(
                                                widget.field.id!,
                                                context
                                                    .read<
                                                        BiometricCaptureControlProvider>()
                                                    .thumbs);
                                            context
                                                .read<
                                                    BiometricCaptureControlProvider>()
                                                .customSetterThumbs(
                                                    false, "isScanned");
                                            context
                                                .read<
                                                    BiometricCaptureControlProvider>()
                                                .customSetterThumbs(
                                                    0, "attemptNo");
                                            context
                                                .read<
                                                    BiometricCaptureControlProvider>()
                                                .customSetterThumbs([
                                              "assets/images/Thumbs@2x.png"
                                            ], "listofImages");
                                            List<BiometricsDto> listOfBiometrics =
                                              [];
                                            context
                                                .read<
                                                    BiometricCaptureControlProvider>()
                                                .customSetterThumbs(
                                                    listOfBiometrics, "listOfBiometricsDto");
                                            context
                                                .read<
                                                    BiometricCaptureControlProvider>()
                                                .customSetterThumbs(
                                                    0, "qualityPercentage");
                                            context
                                                .read<
                                                    BiometricCaptureControlProvider>()
                                                .customSetterThumbs(
                                                    "0", "thresholdPercentage");
                                          }
                                          List<bool> exceptionListCopy = context
                                              .read<
                                                  BiometricCaptureControlProvider>()
                                              .thumbs
                                              .exceptions;
                                          exceptionListCopy[1] =
                                              !(exceptionListCopy.elementAt(1));
                                          context
                                              .read<
                                                  BiometricCaptureControlProvider>()
                                              .customSetterThumbs(
                                                  exceptionListCopy,
                                                  "exceptions");
                                          if (context
                                              .read<
                                                  BiometricCaptureControlProvider>()
                                              .thumbs
                                              .exceptions
                                              .contains(true)) {
                                            if (context
                                                .read<
                                                    BiometricCaptureControlProvider>()
                                                .thumbs
                                                .exceptionType
                                                .isEmpty) {
                                              context
                                                  .read<
                                                      BiometricCaptureControlProvider>()
                                                  .customSetterThumbs(
                                                      "Permanent",
                                                      "exceptionType");
                                            }
                                          }
                                          if (!context
                                              .read<
                                                  BiometricCaptureControlProvider>()
                                              .thumbs
                                              .exceptions
                                              .contains(true)) {
                                            context
                                                .read<
                                                    BiometricCaptureControlProvider>()
                                                .customSetterThumbs(
                                                    "", "exceptionType");
                                          }
                                        },
                                        child: Icon(
                                          Icons.cancel_rounded,
                                          color: (context
                                                      .read<
                                                          BiometricCaptureControlProvider>()
                                                      .thumbs
                                                      .exceptions[1] ==
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
                if (context
                        .read<BiometricCaptureControlProvider>()
                        .biometricAttribute ==
                    "Face")
                  BiometricCaptureExceptionBlock(
                    attribute:
                        context.read<BiometricCaptureControlProvider>().face,
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
                if (context
                        .read<BiometricCaptureControlProvider>()
                        .biometricAttribute ==
                    "Exception")
                  BiometricCaptureExceptionBlock(
                    attribute: context
                        .read<BiometricCaptureControlProvider>()
                        .exception,
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
