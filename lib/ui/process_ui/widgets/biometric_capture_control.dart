import 'dart:convert';

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutter/src/widgets/framework.dart';

import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:percent_indicator/percent_indicator.dart';
import 'package:provider/provider.dart';
import 'package:registration_client/model/biometric_attribute_data.dart';
import 'package:registration_client/model/biometrics_dto.dart';
import 'package:registration_client/model/field.dart';
import 'package:registration_client/pigeon/biometrics_pigeon.dart';
import 'package:registration_client/provider/global_provider.dart';
import 'package:registration_client/ui/process_ui/widgets/biometric_capture_exception_block.dart';
import 'package:registration_client/ui/process_ui/widgets/biometric_capture_scan_block.dart';

import 'package:registration_client/utils/app_config.dart';

class BiometricCaptureControl extends StatefulWidget {
  BiometricCaptureControl({super.key, required this.field});
  final Field field;

  @override
  State<BiometricCaptureControl> createState() =>
      _BiometricCaptureControlState();
}

class _BiometricCaptureControlState extends State<BiometricCaptureControl> {
  String biometricAttribute = "Iris";
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
      thresholdPercentage: "0");
  BiometricAttributeData rightHand = BiometricAttributeData(
      title: "Right Hand",
      attemptNo: 0,
      exceptionType: "",
      exceptions: [false, false, false, false],
      isScanned: false,
      listofImages: ["assets/images/Right Hand@2x.png"],
      listOfBiometricsDto: [],
      qualityPercentage: 0,
      thresholdPercentage: "0");
  BiometricAttributeData leftHand = BiometricAttributeData(
      title: "Left Hand",
      attemptNo: 0,
      exceptionType: "",
      exceptions: [false, false, false, false],
      isScanned: false,
      listofImages: ["assets/images/Left Hand@2x.png"],
      listOfBiometricsDto: [],
      qualityPercentage: 0,
      thresholdPercentage: "0");
  BiometricAttributeData thumbs = BiometricAttributeData(
      title: "Thumbs",
      attemptNo: 0,
      exceptionType: "",
      exceptions: [false, false],
      isScanned: false,
      listofImages: ["assets/images/Thumbs@2x.png"],
      listOfBiometricsDto: [],
      qualityPercentage: 0,
      thresholdPercentage: "0");
  BiometricAttributeData face = BiometricAttributeData(
      title: "Face",
      attemptNo: 0,
      exceptionType: "",
      exceptions: [false],
      isScanned: false,
      listofImages: ["assets/images/Face@2x.png"],
      listOfBiometricsDto: [],
      qualityPercentage: 0,
      thresholdPercentage: "0");
  BiometricAttributeData exception = BiometricAttributeData(
      title: "Exception",
      attemptNo: 0,
      exceptionType: "",
      exceptions: [false],
      isScanned: false,
      listofImages: ["assets/images/Person@2x.png"],
      listOfBiometricsDto: [],
      qualityPercentage: 0,
      thresholdPercentage: "0");

  listOfResultImages(List<dynamic> list) {
    List<Widget> temp = [];
    for (var e in list) {
      temp.add(
        Flexible(
          child: Padding(
            padding: EdgeInsets.symmetric(vertical: 48.h, horizontal: 27.h),
            child: Image.memory(
              e!,
              height: 100,
            ),
          ),
        ),
      );
    }
    return temp;
  }

  listOfImages(List<dynamic> images) {
    List<Widget> temp = [];
    for (var e in images) {
      temp.add(
        Container(
          height: 164.h,
          width: 164.h,
          decoration: BoxDecoration(
            color: pure_white,
            border: Border.all(
              color: secondaryColors.elementAt(14),
            ),
          ),
          child: Padding(
            padding: EdgeInsets.symmetric(vertical: 48.h, horizontal: 27.h),
            child: Image.asset(
              e,
            ),
          ),
        ),
      );
    }
    return temp;
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

  generateList(BuildContext context, String key, BiometricAttributeData data) {
    List<BiometricAttributeData> list = [];

    if (context.read<GlobalProvider>().fieldBiometricsValue.containsKey(key)) {
      if (getElementPosition(
              context.read<GlobalProvider>().fieldBiometricsValue[key],
              data.title) ==
          -1) {
        context.read<GlobalProvider>().fieldBiometricsValue[key].add(data);
      } else {
        context.read<GlobalProvider>().fieldBiometricsValue[key].removeAt(
            getElementPosition(
                context.read<GlobalProvider>().fieldBiometricsValue[key],
                data.title));
        context.read<GlobalProvider>().fieldBiometricsValue[key].add(data);
      }
    } else {
      list.add(data);
      context.read<GlobalProvider>().fieldBiometricsValue[key] = list;
    }
  }

  getElementPosition(List<BiometricAttributeData> list, String title) {
    for (int i = 0; i < list.length; i++) {
      if (list.elementAt(i).title.compareTo(title) == 0) {
        return i;
      }
    }
    return -1;
  }

  noOfTrue(List<bool> list) {
    int i = 0;
    for (var e in list) {
      if (e == true) {
        i++;
      }
    }
    return i;
  }

  @override
  Widget build(BuildContext context) {
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
        .fieldBiometricsValue
        .containsKey("${widget.field.id}")) {
      if (context
          .read<GlobalProvider>()
          .fieldBiometricsValue[widget.field.id]
          .isNotEmpty) {
        if (getElementPosition(
                context
                    .read<GlobalProvider>()
                    .fieldBiometricsValue[widget.field.id],
                "Iris") !=
            -1) {
          iris = context
              .read<GlobalProvider>()
              .fieldBiometricsValue[widget.field.id]
              .elementAt(getElementPosition(
                  context
                      .read<GlobalProvider>()
                      .fieldBiometricsValue[widget.field.id],
                  "Iris"));
        }
        if (getElementPosition(
                context
                    .read<GlobalProvider>()
                    .fieldBiometricsValue[widget.field.id],
                "Right Hand") !=
            -1) {
          rightHand = context
              .read<GlobalProvider>()
              .fieldBiometricsValue[widget.field.id]
              .elementAt(getElementPosition(
                  context
                      .read<GlobalProvider>()
                      .fieldBiometricsValue[widget.field.id],
                  "Right Hand"));
        }
        if (getElementPosition(
                context
                    .read<GlobalProvider>()
                    .fieldBiometricsValue[widget.field.id],
                "Left Hand") !=
            -1) {
          leftHand = context
              .read<GlobalProvider>()
              .fieldBiometricsValue[widget.field.id]
              .elementAt(getElementPosition(
                  context
                      .read<GlobalProvider>()
                      .fieldBiometricsValue[widget.field.id],
                  "Left Hand"));
        }
        if (getElementPosition(
                context
                    .read<GlobalProvider>()
                    .fieldBiometricsValue[widget.field.id],
                "Thumbs") !=
            -1) {
          thumbs = context
              .read<GlobalProvider>()
              .fieldBiometricsValue[widget.field.id]
              .elementAt(getElementPosition(
                  context
                      .read<GlobalProvider>()
                      .fieldBiometricsValue[widget.field.id],
                  "Thumbs"));
        }
        if (getElementPosition(
                context
                    .read<GlobalProvider>()
                    .fieldBiometricsValue[widget.field.id],
                "Face") !=
            -1) {
          face = context
              .read<GlobalProvider>()
              .fieldBiometricsValue[widget.field.id]
              .elementAt(getElementPosition(
                  context
                      .read<GlobalProvider>()
                      .fieldBiometricsValue[widget.field.id],
                  "Face"));
        }
        if (getElementPosition(
                context
                    .read<GlobalProvider>()
                    .fieldBiometricsValue[widget.field.id],
                "Exception") !=
            -1) {
          exception = context
              .read<GlobalProvider>()
              .fieldBiometricsValue[widget.field.id]
              .elementAt(getElementPosition(
                  context
                      .read<GlobalProvider>()
                      .fieldBiometricsValue[widget.field.id],
                  "Exception"));
        }
      }
    }

    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        SizedBox(
          width: double.infinity,
        ),
        Padding(
          padding: const EdgeInsets.fromLTRB(0, 26, 0, 27),
          child: (widget.field.required!)
              ? RichText(
                  text: TextSpan(
                  text: context
                      .read<GlobalProvider>()
                      .chooseLanguage(widget.field.label!),
                  style: Theme.of(context).textTheme.titleLarge?.copyWith(
                      fontSize: 20, color: black_shade_1, fontWeight: semiBold),
                  children: [
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
                      fontSize: 20, color: black_shade_1, fontWeight: semiBold),
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
                  child: Column(
                    children: [
                      if (widget.field.bioAttributes!.contains("leftEye") &&
                          widget.field.bioAttributes!.contains("rightEye"))
                        InkWell(
                          onTap: () {
                            setState(() {
                              biometricAttribute = "Iris";
                            });
                          },
                          child: Stack(
                            children: [
                              Padding(
                                padding:
                                    const EdgeInsets.fromLTRB(10, 10, 10, 5),
                                child: Container(
                                  height: 78.h,
                                  width: 78.h,
                                  decoration: BoxDecoration(
                                      border: Border.all(
                                          color: (iris.isScanned == true)
                                              ? (iris.exceptions.contains(true))
                                                  ? secondaryColors
                                                      .elementAt(16)
                                                  : secondaryColors
                                                      .elementAt(11)
                                              : (biometricAttribute == "Iris")
                                                  ? secondaryColors
                                                      .elementAt(12)
                                                  : secondaryColors
                                                      .elementAt(14)),
                                      borderRadius: BorderRadius.circular(10)),
                                  child: (iris.isScanned == true)
                                      ? Row(
                                          mainAxisAlignment:
                                              MainAxisAlignment.center,
                                          children: [
                                            ...iris.listofImages
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
                                            "assets/images/Eye@2x.png",
                                            height: 18.h,
                                            width: 64.h,
                                          ),
                                        ),
                                ),
                              ),
                              if (iris.isScanned == true)
                                Positioned(
                                    top: 14,
                                    left: 14,
                                    child: (iris.exceptions.contains(true))
                                        ? Image.asset(
                                            "assets/images/Ellipse 1183.png")
                                        : Image.asset(
                                            "assets/images/Ellipse 1181.png")),
                              if (iris.isScanned == true)
                                Positioned(
                                    top: 6,
                                    right: 6,
                                    child: (iris.exceptions.contains(true))
                                        ? Image.asset(
                                            "assets/images/Group 57548.png")
                                        : Image.asset(
                                            "assets/images/Group 57745.png")),
                            ],
                          ),
                        ),
                      if (widget.field.bioAttributes!.contains("rightIndex") &&
                          widget.field.bioAttributes!.contains("rightLittle") &&
                          widget.field.bioAttributes!.contains("rightRing") &&
                          widget.field.bioAttributes!.contains("rightMiddle"))
                        InkWell(
                            onTap: () {
                              setState(() {
                                biometricAttribute = "Right Hand";
                              });
                            },
                            child: Stack(
                              children: [
                                Padding(
                                  padding:
                                      const EdgeInsets.fromLTRB(10, 5, 10, 5),
                                  child: Container(
                                    height: 78.h,
                                    width: 78.h,
                                    decoration: BoxDecoration(
                                        border: Border.all(
                                            color: (rightHand.isScanned == true)
                                                ? (rightHand.exceptions
                                                        .contains(true))
                                                    ? secondaryColors
                                                        .elementAt(16)
                                                    : secondaryColors
                                                        .elementAt(11)
                                                : (biometricAttribute ==
                                                        "Right Hand")
                                                    ? secondaryColors
                                                        .elementAt(12)
                                                    : secondaryColors
                                                        .elementAt(14)),
                                        borderRadius:
                                            BorderRadius.circular(10)),
                                    child: (rightHand.isScanned == true)
                                        ? Row(
                                            mainAxisAlignment:
                                                MainAxisAlignment.center,
                                            children: [
                                              ...rightHand.listofImages
                                                  .map((e) => Flexible(
                                                        child: Image.memory(
                                                          e,
                                                          height: 18.h,
                                                          width: 64.h,
                                                        ),
                                                      )),
                                            ],
                                          )
                                        : Padding(
                                            padding: const EdgeInsets.all(8.0),
                                            child: Image.asset(
                                              "assets/images/Right Hand@2x.png",
                                              height: 18.h,
                                              width: 64.h,
                                            ),
                                          ),
                                  ),
                                ),
                                if (rightHand.isScanned == true)
                                  Positioned(
                                      top: 14,
                                      left: 14,
                                      child: (rightHand.exceptions
                                              .contains(true))
                                          ? Image.asset(
                                              "assets/images/Ellipse 1183.png")
                                          : Image.asset(
                                              "assets/images/Ellipse 1181.png")),
                                if (rightHand.isScanned == true)
                                  Positioned(
                                      top: 6,
                                      right: 6,
                                      child: (rightHand.exceptions
                                              .contains(true))
                                          ? Image.asset(
                                              "assets/images/Group 57548.png")
                                          : Image.asset(
                                              "assets/images/Group 57745.png")),
                              ],
                            )),
                      if (widget.field.bioAttributes!.contains("leftIndex") &&
                          widget.field.bioAttributes!.contains("leftLittle") &&
                          widget.field.bioAttributes!.contains("leftRing") &&
                          widget.field.bioAttributes!.contains("leftMiddle"))
                        InkWell(
                            onTap: () {
                              setState(() {
                                biometricAttribute = "Left Hand";
                              });
                            },
                            child: Stack(
                              children: [
                                Padding(
                                  padding:
                                      const EdgeInsets.fromLTRB(10, 5, 10, 5),
                                  child: Container(
                                    height: 78.h,
                                    width: 78.h,
                                    decoration: BoxDecoration(
                                        border: Border.all(
                                            color: (leftHand.isScanned == true)
                                                ? (leftHand.exceptions
                                                        .contains(true))
                                                    ? secondaryColors
                                                        .elementAt(16)
                                                    : secondaryColors
                                                        .elementAt(11)
                                                : (biometricAttribute ==
                                                        "Left Hand")
                                                    ? secondaryColors
                                                        .elementAt(12)
                                                    : secondaryColors
                                                        .elementAt(14)),
                                        borderRadius:
                                            BorderRadius.circular(10)),
                                    child: (leftHand.isScanned == true)
                                        ? Row(
                                            mainAxisAlignment:
                                                MainAxisAlignment.center,
                                            children: [
                                              ...leftHand.listofImages
                                                  .map((e) => Flexible(
                                                        child: Image.memory(
                                                          e,
                                                          height: 18.h,
                                                          width: 64.h,
                                                        ),
                                                      )),
                                            ],
                                          )
                                        : Padding(
                                            padding: const EdgeInsets.all(8.0),
                                            child: Image.asset(
                                              "assets/images/Left Hand@2x.png",
                                              height: 18.h,
                                              width: 64.h,
                                            ),
                                          ),
                                  ),
                                ),
                                if (leftHand.isScanned == true)
                                  Positioned(
                                      top: 14,
                                      left: 14,
                                      child: (leftHand.exceptions
                                              .contains(true))
                                          ? Image.asset(
                                              "assets/images/Ellipse 1183.png")
                                          : Image.asset(
                                              "assets/images/Ellipse 1181.png")),
                                if (leftHand.isScanned == true)
                                  Positioned(
                                      top: 6,
                                      right: 6,
                                      child: (leftHand.exceptions
                                              .contains(true))
                                          ? Image.asset(
                                              "assets/images/Group 57548.png")
                                          : Image.asset(
                                              "assets/images/Group 57745.png")),
                              ],
                            )),
                      if (widget.field.bioAttributes!.contains("leftThumb") &&
                          widget.field.bioAttributes!.contains("rightThumb"))
                        InkWell(
                            onTap: () {
                              setState(() {
                                biometricAttribute = "Thumbs";
                              });
                            },
                            child: Stack(
                              children: [
                                Padding(
                                  padding:
                                      const EdgeInsets.fromLTRB(10, 5, 10, 5),
                                  child: Container(
                                    height: 78.h,
                                    width: 78.h,
                                    decoration: BoxDecoration(
                                        border: Border.all(
                                            color: (thumbs.isScanned == true)
                                                ? (thumbs.exceptions
                                                        .contains(true))
                                                    ? secondaryColors
                                                        .elementAt(16)
                                                    : secondaryColors
                                                        .elementAt(11)
                                                : (biometricAttribute ==
                                                        "Thumbs")
                                                    ? secondaryColors
                                                        .elementAt(12)
                                                    : secondaryColors
                                                        .elementAt(14)),
                                        borderRadius:
                                            BorderRadius.circular(10)),
                                    child: (thumbs.isScanned == true)
                                        ? Row(
                                            mainAxisAlignment:
                                                MainAxisAlignment.center,
                                            children: [
                                              ...thumbs.listofImages
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
                                              "assets/images/Thumbs@2x.png",
                                              height: 18.h,
                                              width: 64.h,
                                            ),
                                          ),
                                  ),
                                ),
                                if (thumbs.isScanned == true)
                                  Positioned(
                                      top: 14,
                                      left: 14,
                                      child: (thumbs.exceptions.contains(true))
                                          ? Image.asset(
                                              "assets/images/Ellipse 1183.png")
                                          : Image.asset(
                                              "assets/images/Ellipse 1181.png")),
                                if (thumbs.isScanned == true)
                                  Positioned(
                                      top: 6,
                                      right: 6,
                                      child: (thumbs.exceptions.contains(true))
                                          ? Image.asset(
                                              "assets/images/Group 57548.png")
                                          : Image.asset(
                                              "assets/images/Group 57745.png")),
                              ],
                            )),
                      if (widget.field.bioAttributes!.contains("face"))
                        InkWell(
                            onTap: () {
                              setState(() {
                                biometricAttribute = "Face";
                              });
                            },
                            child: Stack(
                              children: [
                                Padding(
                                  padding:
                                      const EdgeInsets.fromLTRB(10, 5, 10, 5),
                                  child: Container(
                                    height: 78.h,
                                    width: 78.h,
                                    decoration: BoxDecoration(
                                        border: Border.all(
                                            color: (face.isScanned == true)
                                                ? (face.exceptions
                                                        .contains(true))
                                                    ? secondaryColors
                                                        .elementAt(16)
                                                    : secondaryColors
                                                        .elementAt(11)
                                                : (biometricAttribute == "Face")
                                                    ? secondaryColors
                                                        .elementAt(12)
                                                    : secondaryColors
                                                        .elementAt(14)),
                                        borderRadius:
                                            BorderRadius.circular(10)),
                                    child: (face.isScanned == true)
                                        ? Row(
                                            mainAxisAlignment:
                                                MainAxisAlignment.center,
                                            children: [
                                              ...face.listofImages
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
                                              "assets/images/Face@2x.png",
                                              height: 18.h,
                                              width: 64.h,
                                            ),
                                          ),
                                  ),
                                ),
                                if (face.isScanned == true)
                                  Positioned(
                                      top: 14,
                                      left: 14,
                                      child: (face.exceptions.contains(true))
                                          ? Image.asset(
                                              "assets/images/Ellipse 1183.png")
                                          : Image.asset(
                                              "assets/images/Ellipse 1181.png")),
                                if (face.isScanned == true)
                                  Positioned(
                                      top: 6,
                                      right: 6,
                                      child: (face.exceptions.contains(true))
                                          ? Image.asset(
                                              "assets/images/Group 57548.png")
                                          : Image.asset(
                                              "assets/images/Group 57745.png")),
                              ],
                            )),
                      if (iris.exceptions.contains(true) ||
                          rightHand.exceptions.contains(true) ||
                          leftHand.exceptions.contains(true) ||
                          thumbs.exceptions.contains(true) ||
                          face.exceptions.contains(true))
                        InkWell(
                            onTap: () {
                              setState(() {
                                biometricAttribute = "Exception";
                              });
                            },
                            child: Stack(
                              children: [
                                Padding(
                                  padding:
                                      const EdgeInsets.fromLTRB(10, 5, 10, 10),
                                  child: Container(
                                    height: 78.h,
                                    width: 78.h,
                                    decoration: BoxDecoration(
                                        border: Border.all(
                                            color: (exception.isScanned == true)
                                                ? (exception.exceptions
                                                        .contains(true))
                                                    ? secondaryColors
                                                        .elementAt(16)
                                                    : secondaryColors
                                                        .elementAt(11)
                                                : (biometricAttribute ==
                                                        "Exception")
                                                    ? secondaryColors
                                                        .elementAt(12)
                                                    : secondaryColors
                                                        .elementAt(14)),
                                        borderRadius:
                                            BorderRadius.circular(10)),
                                    child: (exception.isScanned == true)
                                        ? Row(
                                            mainAxisAlignment:
                                                MainAxisAlignment.center,
                                            children: [
                                              ...exception.listofImages
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
                                              "assets/images/Person@2x.png",
                                              height: 18.h,
                                              width: 64.h,
                                            ),
                                          ),
                                  ),
                                ),
                                if (exception.isScanned == true)
                                  Positioned(
                                      top: 14,
                                      left: 14,
                                      child: (exception.exceptions
                                              .contains(true))
                                          ? Image.asset(
                                              "assets/images/Ellipse 1183.png")
                                          : Image.asset(
                                              "assets/images/Ellipse 1181.png")),
                                if (exception.isScanned == true)
                                  Positioned(
                                      top: 6,
                                      right: 6,
                                      child: (exception.exceptions
                                              .contains(true))
                                          ? Image.asset(
                                              "assets/images/Group 57548.png")
                                          : Image.asset(
                                              "assets/images/Group 57745.png")),
                              ],
                            )),
                    ],
                  ),
                ),
                if (biometricAttribute == "Iris")
                  BiometricCaptureScanBlock(
                    title: "IrisScan",
                    middleBlock: Container(
                      height: 460.h,
                      child: Column(
                        mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                        children: [
                          (iris.isScanned == false)
                              ? Row(
                                  mainAxisAlignment:
                                      MainAxisAlignment.spaceEvenly,
                                  children: [
                                    ...listOfImages(iris.listofImages).map(
                                      (e) => e,
                                    ),
                                  ],
                                )
                              : Row(
                                  mainAxisAlignment:
                                      MainAxisAlignment.spaceEvenly,
                                  children: [
                                    ...listOfResultImages(iris.listofImages)
                                        .map(
                                      (e) => e,
                                    ),
                                  ],
                                ),
                          OutlinedButton.icon(
                            onPressed: () async {
                              List<Uint8List?> temp = [];
                              await BiometricsApi()
                                  .invokeDiscoverSbi(widget.field.id!, "Iris");
                              await BiometricsApi()
                                  .getBestBiometrics(widget.field.id!, "Iris")
                                  .then((value) {});
                              await BiometricsApi()
                                  .extractImageValues(widget.field.id!, "Iris")
                                  .then((value) {
                                temp = value;
                              });
                              await BiometricsApi().incrementBioAttempt(
                                  widget.field.id!, "Iris");
                              iris.attemptNo = await BiometricsApi()
                                  .getBioAttempt(widget.field.id!, "Iris");
                              showDialog<String>(
                                context: context,
                                builder: (BuildContext context) => AlertDialog(
                                    content: Container(
                                  height: 539.h,
                                  width: 768.w,
                                  child: Column(
                                      mainAxisAlignment:
                                          MainAxisAlignment.spaceBetween,
                                      children: [
                                        Row(
                                          mainAxisAlignment:
                                              MainAxisAlignment.spaceBetween,
                                          children: [
                                            Text(
                                              "Iris Capture",
                                              style: Theme.of(context)
                                                  .textTheme
                                                  .bodyLarge
                                                  ?.copyWith(
                                                      fontSize: 18,
                                                      fontWeight: bold,
                                                      color: black_shade_1),
                                            ),
                                            IconButton(
                                                onPressed: () {
                                                  Navigator.pop(context);
                                                },
                                                icon: Icon(
                                                  Icons.close,
                                                )),
                                          ],
                                        ),
                                        Divider(),
                                        Padding(
                                          padding: EdgeInsets.all(0),
                                          // EdgeInsets.fromLTRB(
                                          //     60.w, 35.h, 60.w, 35.h),
                                          child: Row(
                                            mainAxisAlignment:
                                                MainAxisAlignment.center,
                                            children: [
                                              ...temp.map((e) => Image.memory(
                                                    e!,
                                                    height: 310.h,
                                                    width: 310.h,
                                                  )),
                                            ],
                                          ),
                                        ),
                                        Divider(),
                                        Padding(
                                          padding: const EdgeInsets.all(0.0),
                                          child: Row(
                                            mainAxisAlignment:
                                                MainAxisAlignment.end,
                                            children: [
                                              OutlinedButton(
                                                  style: OutlinedButton.styleFrom(
                                                      maximumSize:
                                                          Size(160.w, 42.h),
                                                      minimumSize:
                                                          Size(160.w, 42.h),
                                                      side: BorderSide(
                                                          color:
                                                              solid_primary)),
                                                  onPressed: () async {
                                                    await BiometricsApi()
                                                        .invokeDiscoverSbi(
                                                            widget.field.id!,
                                                            "Iris");
                                                    await BiometricsApi()
                                                        .extractImageValues(
                                                            widget.field.id!,
                                                            "Iris")
                                                        .then((value) {
                                                      temp = value;
                                                    });
                                                    await BiometricsApi()
                                                        .incrementBioAttempt(
                                                            widget.field.id!,
                                                            "Iris");
                                                    iris.attemptNo =
                                                        await BiometricsApi()
                                                            .getBioAttempt(
                                                                widget
                                                                    .field.id!,
                                                                "Iris");
                                                  },
                                                  child: Text("RESCAN")),
                                              SizedBox(
                                                width: 10.w,
                                              ),
                                              ElevatedButton(
                                                style: ElevatedButton.styleFrom(
                                                    maximumSize:
                                                        Size(160.w, 42.h),
                                                    minimumSize:
                                                        Size(160.w, 42.h)),
                                                onPressed: () async {
                                                  iris.listOfBiometricsDto
                                                      .clear();
                                                  await BiometricsApi()
                                                      .getBestBiometrics(
                                                          widget.field.id!,
                                                          "Iris")
                                                      .then((value) async {
                                                    for (var e in value) {
                                                      iris.listOfBiometricsDto
                                                          .add(BiometricsDto
                                                              .fromJson(json
                                                                  .decode(e!)));
                                                    }
                                                  });
                                                  iris.qualityPercentage =
                                                      avgScore(iris
                                                          .listOfBiometricsDto);
                                                  await BiometricsApi()
                                                      .extractImageValues(
                                                          widget.field.id!,
                                                          "Iris")
                                                      .then((value) {
                                                    iris.listofImages = value;
                                                  });
                                                  iris.isScanned = true;
                                                  generateList(
                                                      context,
                                                      "${widget.field.id}",
                                                      iris);

                                                  setState(() {});
                                                  Navigator.pop(context);
                                                },
                                                child: Text("SAVE"),
                                              )
                                            ],
                                          ),
                                        )
                                      ]),
                                )),
                              );
                            },
                            icon: Icon(
                              Icons.crop_free,
                              color: solid_primary,
                              size: 14,
                            ),
                            label: Text(
                              "SCAN",
                              style: Theme.of(context)
                                  .textTheme
                                  .bodySmall
                                  ?.copyWith(
                                      fontSize: 14,
                                      fontWeight: bold,
                                      color: solid_primary),
                            ),
                            style: OutlinedButton.styleFrom(
                              side: BorderSide(color: solid_primary, width: 1),
                              shape: RoundedRectangleBorder(
                                borderRadius: BorderRadius.circular(5),
                              ),
                            ),
                          ),
                          Container(
                              height: 67.h,
                              width: 162.w,
                              decoration: BoxDecoration(
                                color: pure_white,
                                border: Border.all(
                                  color: secondaryColors.elementAt(14),
                                ),
                              ),
                              padding: EdgeInsets.all(12),
                              child: Row(
                                mainAxisAlignment:
                                    MainAxisAlignment.spaceBetween,
                                children: [
                                  Column(
                                    crossAxisAlignment:
                                        CrossAxisAlignment.start,
                                    children: [
                                      Text(
                                        "Attempts",
                                        style: Theme.of(context)
                                            .textTheme
                                            .bodyLarge
                                            ?.copyWith(
                                              fontSize: 14,
                                              color:
                                                  secondaryColors.elementAt(1),
                                            ),
                                      ),
                                      Text("${iris.attemptNo}"),
                                    ],
                                  ),
                                  Column(
                                    crossAxisAlignment:
                                        CrossAxisAlignment.start,
                                    children: [
                                      Text(
                                        "Exceptions",
                                        style: Theme.of(context)
                                            .textTheme
                                            .bodyLarge
                                            ?.copyWith(
                                              fontSize: 14,
                                              color:
                                                  secondaryColors.elementAt(1),
                                            ),
                                      ),
                                      Text("${noOfTrue(iris.exceptions)}"),
                                    ],
                                  ),
                                ],
                              )),
                          Container(
                            height: 157.h,
                            width: 338.h,
                            decoration: BoxDecoration(
                              color: pure_white,
                              border: Border.all(
                                color: secondaryColors.elementAt(14),
                              ),
                            ),
                            child: Column(
                              mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                              children: [
                                Text(
                                  "Quality",
                                  style: Theme.of(context)
                                      .textTheme
                                      .bodyLarge
                                      ?.copyWith(
                                          fontSize: 14,
                                          fontWeight: semiBold,
                                          color: black_shade_1),
                                ),
                                Text(
                                  "Threshold ${iris.thresholdPercentage}%",
                                  style: Theme.of(context)
                                      .textTheme
                                      .bodyLarge
                                      ?.copyWith(
                                        fontSize: 14,
                                        fontWeight: regular,
                                        color: secondaryColors.elementAt(1),
                                      ),
                                ),
                                Row(
                                  mainAxisAlignment: MainAxisAlignment.center,
                                  children: [
                                    LinearPercentIndicator(
                                      width: 260.h,
                                      lineHeight: 8,
                                      percent: iris.qualityPercentage / 100,
                                      backgroundColor: Colors.grey,
                                      progressColor:
                                          (iris.qualityPercentage.toInt() >
                                                  int.parse(
                                                      iris.thresholdPercentage))
                                              ? secondaryColors.elementAt(11)
                                              : secondaryColors.elementAt(20),
                                    ),
                                    SizedBox(
                                      width: 16.h,
                                    ),
                                    Text(
                                      "${iris.qualityPercentage.toInt()}%",
                                      style: Theme.of(context)
                                          .textTheme
                                          .bodyLarge
                                          ?.copyWith(
                                            fontSize: 14,
                                            fontWeight: regular,
                                            color: black_shade_1,
                                          ),
                                    ),
                                  ],
                                ),
                                Row(
                                  mainAxisAlignment: MainAxisAlignment.center,
                                  children: [
                                    Text(
                                      "Attempts",
                                      style: Theme.of(context)
                                          .textTheme
                                          .bodyLarge
                                          ?.copyWith(
                                              fontSize: 12,
                                              color: black_shade_1),
                                    ),
                                    SizedBox(
                                      width: 13.w,
                                    ),
                                    InkWell(
                                      onTap: () async {
                                        if (iris.attemptNo >= 1) {
                                          await BiometricsApi()
                                              .getBiometrics(
                                                  widget.field.id!, "Iris", 1)
                                              .then((value) {
                                            iris.listOfBiometricsDto.clear();
                                            for (var e in value) {
                                              iris.listOfBiometricsDto.add(
                                                  BiometricsDto.fromJson(
                                                      json.decode(e!)));
                                            }
                                          });

                                          setState(() {
                                            iris.qualityPercentage = avgScore(
                                                iris.listOfBiometricsDto);
                                          });
                                          await BiometricsApi()
                                              .extractImageValuesByAttempt(
                                                  widget.field.id!, "Iris", 1)
                                              .then((value) {
                                            iris.listofImages = value;
                                          });
                                          setState(() {});
                                        }
                                      },
                                      child: Container(
                                        padding: EdgeInsets.symmetric(
                                          vertical: 0,
                                          horizontal: 11,
                                        ),
                                        decoration: BoxDecoration(
                                          borderRadius:
                                              BorderRadius.circular(20),
                                          border: Border.all(
                                            color:
                                                secondaryColors.elementAt(17),
                                          ),
                                          color: (iris.attemptNo < 1)
                                              ? secondaryColors.elementAt(18)
                                              : secondaryColors.elementAt(11),
                                        ),
                                        child: Text(
                                          "1",
                                          style: Theme.of(context)
                                              .textTheme
                                              .bodyLarge
                                              ?.copyWith(
                                                  fontSize: 12,
                                                  color: (iris.attemptNo < 1)
                                                      ? secondaryColors
                                                          .elementAt(19)
                                                      : pure_white,
                                                  fontWeight: semiBold),
                                        ),
                                      ),
                                    ),
                                    SizedBox(
                                      width: 9.5.w,
                                    ),
                                    InkWell(
                                      onTap: () async {
                                        if (iris.attemptNo >= 2) {
                                          await BiometricsApi()
                                              .getBiometrics(
                                                  widget.field.id!, "Iris", 2)
                                              .then((value) {
                                            iris.listOfBiometricsDto.clear();
                                            for (var e in value) {
                                              iris.listOfBiometricsDto.add(
                                                  BiometricsDto.fromJson(
                                                      json.decode(e!)));
                                            }
                                          });
                                          setState(() {
                                            iris.qualityPercentage = avgScore(
                                                iris.listOfBiometricsDto);
                                          });
                                          await BiometricsApi()
                                              .extractImageValuesByAttempt(
                                                  widget.field.id!, "Iris", 2)
                                              .then((value) {
                                            iris.listofImages = value;
                                          });
                                          setState(() {});
                                        }
                                      },
                                      child: Container(
                                        padding: EdgeInsets.symmetric(
                                          vertical: 0,
                                          horizontal: 11,
                                        ),
                                        decoration: BoxDecoration(
                                          borderRadius:
                                              BorderRadius.circular(20),
                                          border: Border.all(
                                            color:
                                                secondaryColors.elementAt(17),
                                          ),
                                          color: (iris.attemptNo < 2)
                                              ? secondaryColors.elementAt(18)
                                              : secondaryColors.elementAt(11),
                                        ),
                                        child: Text(
                                          "2",
                                          style: Theme.of(context)
                                              .textTheme
                                              .bodyLarge
                                              ?.copyWith(
                                                  fontSize: 12,
                                                  color: (iris.attemptNo < 2)
                                                      ? secondaryColors
                                                          .elementAt(19)
                                                      : pure_white,
                                                  fontWeight: semiBold),
                                        ),
                                      ),
                                    ),
                                    SizedBox(
                                      width: 9.5.w,
                                    ),
                                    InkWell(
                                      onTap: () async {
                                        if (iris.attemptNo >= 3) {
                                          await BiometricsApi()
                                              .getBiometrics(
                                                  widget.field.id!, "Iris", 3)
                                              .then((value) {
                                            iris.listOfBiometricsDto.clear();
                                            for (var e in value) {
                                              iris.listOfBiometricsDto.add(
                                                  BiometricsDto.fromJson(
                                                      json.decode(e!)));
                                            }
                                          });
                                          setState(() {
                                            iris.qualityPercentage = avgScore(
                                                iris.listOfBiometricsDto);
                                          });
                                          await BiometricsApi()
                                              .extractImageValuesByAttempt(
                                                  widget.field.id!, "Iris", 3)
                                              .then((value) {
                                            iris.listofImages = value;
                                          });
                                          setState(() {});
                                        }
                                      },
                                      child: Container(
                                        padding: EdgeInsets.symmetric(
                                          vertical: 0,
                                          horizontal: 11,
                                        ),
                                        decoration: BoxDecoration(
                                          borderRadius:
                                              BorderRadius.circular(20),
                                          border: Border.all(
                                            color:
                                                secondaryColors.elementAt(17),
                                          ),
                                          color: (iris.attemptNo < 3)
                                              ? secondaryColors.elementAt(18)
                                              : secondaryColors.elementAt(11),
                                        ),
                                        child: Text(
                                          "3",
                                          style: Theme.of(context)
                                              .textTheme
                                              .bodyLarge
                                              ?.copyWith(
                                                  fontSize: 12,
                                                  color: (iris.attemptNo < 3)
                                                      ? secondaryColors
                                                          .elementAt(19)
                                                      : pure_white,
                                                  fontWeight: semiBold),
                                        ),
                                      ),
                                    ),
                                  ],
                                )
                              ],
                            ),
                          ),
                        ],
                      ),
                    ),
                  ),
                if (biometricAttribute == "Right Hand")
                  BiometricCaptureScanBlock(
                    title: "RightHandScan",
                    middleBlock: Container(
                      height: 460.h,
                      child: Column(
                        mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                        children: [
                          (rightHand.isScanned == false)
                              ? Row(
                                  mainAxisAlignment:
                                      MainAxisAlignment.spaceEvenly,
                                  children: [
                                    ...listOfImages(rightHand.listofImages).map(
                                      (e) => e,
                                    ),
                                  ],
                                )
                              : Row(
                                  mainAxisAlignment:
                                      MainAxisAlignment.spaceEvenly,
                                  children: [
                                    ...listOfResultImages(
                                            rightHand.listofImages)
                                        .map(
                                      (e) => e,
                                    ),
                                  ],
                                ),
                          OutlinedButton.icon(
                            onPressed: () async {
                              List<Uint8List?> temp = [];
                              await BiometricsApi().invokeDiscoverSbi(
                                  widget.field.id!, "RightHand");
                              await BiometricsApi().getBestBiometrics(
                                  widget.field.id!, "RightHand");
                              await BiometricsApi()
                                  .extractImageValues(
                                      widget.field.id!, "RightHand")
                                  .then((value) {
                                temp = value;
                              });
                              await BiometricsApi().incrementBioAttempt(
                                  widget.field.id!, "RightHand");
                              rightHand.attemptNo = await BiometricsApi()
                                  .getBioAttempt(widget.field.id!, "RightHand");
                              showDialog<String>(
                                context: context,
                                builder: (BuildContext context) => AlertDialog(
                                    content: Container(
                                  height: 539.h,
                                  width: 768.w,
                                  child: Column(
                                      mainAxisAlignment:
                                          MainAxisAlignment.spaceBetween,
                                      children: [
                                        Row(
                                          mainAxisAlignment:
                                              MainAxisAlignment.spaceBetween,
                                          children: [
                                            Text(
                                              "Right Hand Capture",
                                              style: Theme.of(context)
                                                  .textTheme
                                                  .bodyLarge
                                                  ?.copyWith(
                                                      fontSize: 18,
                                                      fontWeight: bold,
                                                      color: black_shade_1),
                                            ),
                                            IconButton(
                                                onPressed: () {
                                                  Navigator.pop(context);
                                                },
                                                icon: Icon(
                                                  Icons.close,
                                                )),
                                          ],
                                        ),
                                        Divider(),
                                        Padding(
                                          padding: EdgeInsets.all(0),
                                          // EdgeInsets.fromLTRB(
                                          //     60.w, 35.h, 60.w, 35.h),
                                          child: Row(
                                            mainAxisAlignment:
                                                MainAxisAlignment.center,
                                            children: [
                                              ...temp.map((e) => Image.memory(
                                                    e!,
                                                    height: 190.h,
                                                    width: 190.h,
                                                  )),
                                            ],
                                          ),
                                        ),
                                        Divider(),
                                        Padding(
                                          padding: const EdgeInsets.all(0.0),
                                          child: Row(
                                            mainAxisAlignment:
                                                MainAxisAlignment.end,
                                            children: [
                                              OutlinedButton(
                                                  style: OutlinedButton.styleFrom(
                                                      maximumSize:
                                                          Size(160.w, 42.h),
                                                      minimumSize:
                                                          Size(160.w, 42.h),
                                                      side: BorderSide(
                                                          color:
                                                              solid_primary)),
                                                  onPressed: () async {
                                                    await BiometricsApi()
                                                        .invokeDiscoverSbi(
                                                            widget.field.id!,
                                                            "RightHand");
                                                    await BiometricsApi()
                                                        .extractImageValues(
                                                            widget.field.id!,
                                                            "RightHand")
                                                        .then((value) {
                                                      temp = value;
                                                    });
                                                    await BiometricsApi()
                                                        .incrementBioAttempt(
                                                            widget.field.id!,
                                                            "RightHand");
                                                    rightHand.attemptNo =
                                                        await BiometricsApi()
                                                            .getBioAttempt(
                                                                widget
                                                                    .field.id!,
                                                                "RightHand");
                                                  },
                                                  child: Text("RESCAN")),
                                              SizedBox(
                                                width: 10.w,
                                              ),
                                              ElevatedButton(
                                                style: ElevatedButton.styleFrom(
                                                    maximumSize:
                                                        Size(160.w, 42.h),
                                                    minimumSize:
                                                        Size(160.w, 42.h)),
                                                onPressed: () async {
                                                  rightHand.listOfBiometricsDto
                                                      .clear;
                                                  await BiometricsApi()
                                                      .getBestBiometrics(
                                                          widget.field.id!,
                                                          "RightHand")
                                                      .then((value) async {
                                                    for (var e in value) {
                                                      rightHand
                                                          .listOfBiometricsDto
                                                          .add(BiometricsDto
                                                              .fromJson(json
                                                                  .decode(e!)));
                                                    }
                                                  });
                                                  rightHand.qualityPercentage =
                                                      avgScore(rightHand
                                                          .listOfBiometricsDto);
                                                  await BiometricsApi()
                                                      .extractImageValues(
                                                          widget.field.id!,
                                                          "RightHand")
                                                      .then((value) {
                                                    rightHand.listofImages =
                                                        value;
                                                  });
                                                  rightHand.isScanned = true;
                                                  generateList(
                                                      context,
                                                      "${widget.field.id}",
                                                      rightHand);
                                                  setState(() {});
                                                  Navigator.pop(context);
                                                },
                                                child: Text("SAVE"),
                                              )
                                            ],
                                          ),
                                        )
                                      ]),
                                )),
                              );
                            },
                            icon: Icon(
                              Icons.crop_free,
                              color: solid_primary,
                              size: 14,
                            ),
                            label: Text(
                              "SCAN",
                              style: Theme.of(context)
                                  .textTheme
                                  .bodySmall
                                  ?.copyWith(
                                      fontSize: 14,
                                      fontWeight: bold,
                                      color: solid_primary),
                            ),
                            style: OutlinedButton.styleFrom(
                              side: BorderSide(color: solid_primary, width: 1),
                              shape: RoundedRectangleBorder(
                                borderRadius: BorderRadius.circular(5),
                              ),
                            ),
                          ),
                          Container(
                              height: 67.h,
                              width: 162.w,
                              decoration: BoxDecoration(
                                color: pure_white,
                                border: Border.all(
                                  color: secondaryColors.elementAt(14),
                                ),
                              ),
                              padding: EdgeInsets.all(12),
                              child: Row(
                                mainAxisAlignment:
                                    MainAxisAlignment.spaceBetween,
                                children: [
                                  Column(
                                    crossAxisAlignment:
                                        CrossAxisAlignment.start,
                                    children: [
                                      Text(
                                        "Attempts",
                                        style: Theme.of(context)
                                            .textTheme
                                            .bodyLarge
                                            ?.copyWith(
                                              fontSize: 14,
                                              color:
                                                  secondaryColors.elementAt(1),
                                            ),
                                      ),
                                      Text("${rightHand.attemptNo}"),
                                    ],
                                  ),
                                  Column(
                                    crossAxisAlignment:
                                        CrossAxisAlignment.start,
                                    children: [
                                      Text(
                                        "Exceptions",
                                        style: Theme.of(context)
                                            .textTheme
                                            .bodyLarge
                                            ?.copyWith(
                                              fontSize: 14,
                                              color:
                                                  secondaryColors.elementAt(1),
                                            ),
                                      ),
                                      Text("${noOfTrue(rightHand.exceptions)}"),
                                    ],
                                  ),
                                ],
                              )),
                          Container(
                            height: 157.h,
                            width: 338.h,
                            decoration: BoxDecoration(
                              color: pure_white,
                              border: Border.all(
                                color: secondaryColors.elementAt(14),
                              ),
                            ),
                            child: Column(
                              mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                              children: [
                                Text(
                                  "Quality",
                                  style: Theme.of(context)
                                      .textTheme
                                      .bodyLarge
                                      ?.copyWith(
                                          fontSize: 14,
                                          fontWeight: semiBold,
                                          color: black_shade_1),
                                ),
                                Text(
                                  "Threshold ${rightHand.thresholdPercentage}%",
                                  style: Theme.of(context)
                                      .textTheme
                                      .bodyLarge
                                      ?.copyWith(
                                        fontSize: 14,
                                        fontWeight: regular,
                                        color: secondaryColors.elementAt(1),
                                      ),
                                ),
                                Row(
                                  mainAxisAlignment: MainAxisAlignment.center,
                                  children: [
                                    LinearPercentIndicator(
                                      width: 260.h,
                                      lineHeight: 8,
                                      percent:
                                          rightHand.qualityPercentage / 100,
                                      backgroundColor: Colors.grey,
                                      progressColor:
                                          (rightHand.qualityPercentage.toInt() >
                                                  int.parse(rightHand
                                                      .thresholdPercentage))
                                              ? secondaryColors.elementAt(11)
                                              : secondaryColors.elementAt(20),
                                    ),
                                    SizedBox(
                                      width: 16.h,
                                    ),
                                    Text(
                                      "${rightHand.qualityPercentage.toInt()}%",
                                      style: Theme.of(context)
                                          .textTheme
                                          .bodyLarge
                                          ?.copyWith(
                                            fontSize: 14,
                                            fontWeight: regular,
                                            color: black_shade_1,
                                          ),
                                    ),
                                  ],
                                ),
                                Row(
                                  mainAxisAlignment: MainAxisAlignment.center,
                                  children: [
                                    Text(
                                      "Attempts",
                                      style: Theme.of(context)
                                          .textTheme
                                          .bodyLarge
                                          ?.copyWith(
                                              fontSize: 12,
                                              color: black_shade_1),
                                    ),
                                    SizedBox(
                                      width: 13.w,
                                    ),
                                    InkWell(
                                      onTap: () async {
                                        if (rightHand.attemptNo >= 1) {
                                          await BiometricsApi()
                                              .getBiometrics(widget.field.id!,
                                                  "RightHand", 1)
                                              .then((value) {
                                            rightHand.listOfBiometricsDto
                                                .clear();
                                            for (var e in value) {
                                              rightHand.listOfBiometricsDto.add(
                                                  BiometricsDto.fromJson(
                                                      json.decode(e!)));
                                            }
                                          });

                                          setState(() {
                                            rightHand.qualityPercentage =
                                                avgScore(rightHand
                                                    .listOfBiometricsDto);
                                          });
                                          await BiometricsApi()
                                              .extractImageValuesByAttempt(
                                                  widget.field.id!,
                                                  "RightHand",
                                                  1)
                                              .then((value) {
                                            rightHand.listofImages = value;
                                          });
                                          setState(() {});
                                        }
                                      },
                                      child: Container(
                                        padding: EdgeInsets.symmetric(
                                          vertical: 0,
                                          horizontal: 11,
                                        ),
                                        decoration: BoxDecoration(
                                          borderRadius:
                                              BorderRadius.circular(20),
                                          border: Border.all(
                                            color:
                                                secondaryColors.elementAt(17),
                                          ),
                                          color: (rightHand.attemptNo < 1)
                                              ? secondaryColors.elementAt(18)
                                              : secondaryColors.elementAt(11),
                                        ),
                                        child: Text(
                                          "1",
                                          style: Theme.of(context)
                                              .textTheme
                                              .bodyLarge
                                              ?.copyWith(
                                                  fontSize: 12,
                                                  color:
                                                      (rightHand.attemptNo < 1)
                                                          ? secondaryColors
                                                              .elementAt(19)
                                                          : pure_white,
                                                  fontWeight: semiBold),
                                        ),
                                      ),
                                    ),
                                    SizedBox(
                                      width: 9.5.w,
                                    ),
                                    InkWell(
                                      onTap: () async {
                                        if (rightHand.attemptNo >= 2) {
                                          await BiometricsApi()
                                              .getBiometrics(widget.field.id!,
                                                  "RightHand", 2)
                                              .then((value) {
                                            rightHand.listOfBiometricsDto
                                                .clear();
                                            for (var e in value) {
                                              rightHand.listOfBiometricsDto.add(
                                                  BiometricsDto.fromJson(
                                                      json.decode(e!)));
                                            }
                                          });
                                          setState(() {
                                            rightHand.qualityPercentage =
                                                avgScore(rightHand
                                                    .listOfBiometricsDto);
                                          });
                                          await BiometricsApi()
                                              .extractImageValuesByAttempt(
                                                  widget.field.id!,
                                                  "RightHand",
                                                  2)
                                              .then((value) {
                                            rightHand.listofImages = value;
                                          });
                                          setState(() {});
                                        }
                                      },
                                      child: Container(
                                        padding: EdgeInsets.symmetric(
                                          vertical: 0,
                                          horizontal: 11,
                                        ),
                                        decoration: BoxDecoration(
                                          borderRadius:
                                              BorderRadius.circular(20),
                                          border: Border.all(
                                            color:
                                                secondaryColors.elementAt(17),
                                          ),
                                          color: (rightHand.attemptNo < 2)
                                              ? secondaryColors.elementAt(18)
                                              : secondaryColors.elementAt(11),
                                        ),
                                        child: Text(
                                          "2",
                                          style: Theme.of(context)
                                              .textTheme
                                              .bodyLarge
                                              ?.copyWith(
                                                  fontSize: 12,
                                                  color:
                                                      (rightHand.attemptNo < 2)
                                                          ? secondaryColors
                                                              .elementAt(19)
                                                          : pure_white,
                                                  fontWeight: semiBold),
                                        ),
                                      ),
                                    ),
                                    SizedBox(
                                      width: 9.5.w,
                                    ),
                                    InkWell(
                                      onTap: () async {
                                        if (rightHand.attemptNo >= 3) {
                                          await BiometricsApi()
                                              .getBiometrics(widget.field.id!,
                                                  "RightHand", 3)
                                              .then((value) {
                                            rightHand.listOfBiometricsDto
                                                .clear();
                                            for (var e in value) {
                                              rightHand.listOfBiometricsDto.add(
                                                  BiometricsDto.fromJson(
                                                      json.decode(e!)));
                                            }
                                          });
                                          setState(() {
                                            rightHand.qualityPercentage =
                                                avgScore(rightHand
                                                    .listOfBiometricsDto);
                                          });
                                          await BiometricsApi()
                                              .extractImageValuesByAttempt(
                                                  widget.field.id!,
                                                  "RightHand",
                                                  3)
                                              .then((value) {
                                            rightHand.listofImages = value;
                                          });
                                          setState(() {});
                                        }
                                      },
                                      child: Container(
                                        padding: EdgeInsets.symmetric(
                                          vertical: 0,
                                          horizontal: 11,
                                        ),
                                        decoration: BoxDecoration(
                                          borderRadius:
                                              BorderRadius.circular(20),
                                          border: Border.all(
                                            color:
                                                secondaryColors.elementAt(17),
                                          ),
                                          color: (rightHand.attemptNo < 3)
                                              ? secondaryColors.elementAt(18)
                                              : secondaryColors.elementAt(11),
                                        ),
                                        child: Text(
                                          "3",
                                          style: Theme.of(context)
                                              .textTheme
                                              .bodyLarge
                                              ?.copyWith(
                                                  fontSize: 12,
                                                  color:
                                                      (rightHand.attemptNo < 3)
                                                          ? secondaryColors
                                                              .elementAt(19)
                                                          : pure_white,
                                                  fontWeight: semiBold),
                                        ),
                                      ),
                                    ),
                                  ],
                                )
                              ],
                            ),
                          ),
                        ],
                      ),
                    ),
                  ),
                if (biometricAttribute == "Left Hand")
                  BiometricCaptureScanBlock(
                    title: "LeftHandScan",
                    middleBlock: Container(
                      height: 460.h,
                      child: Column(
                        mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                        children: [
                          (leftHand.isScanned == false)
                              ? Row(
                                  mainAxisAlignment:
                                      MainAxisAlignment.spaceEvenly,
                                  children: [
                                    ...listOfImages(leftHand.listofImages).map(
                                      (e) => e,
                                    ),
                                  ],
                                )
                              : Row(
                                  mainAxisAlignment:
                                      MainAxisAlignment.spaceEvenly,
                                  children: [
                                    ...listOfResultImages(leftHand.listofImages)
                                        .map(
                                      (e) => e,
                                    ),
                                  ],
                                ),
                          OutlinedButton.icon(
                            onPressed: () async {
                              List<Uint8List?> temp = [];
                              await BiometricsApi().invokeDiscoverSbi(
                                  widget.field.id!, "LeftHand");
                              await BiometricsApi().getBestBiometrics(
                                  widget.field.id!, "LeftHand");
                              await BiometricsApi()
                                  .extractImageValues(
                                      widget.field.id!, "LeftHand")
                                  .then((value) {
                                temp = value;
                              });
                              await BiometricsApi().incrementBioAttempt(
                                  widget.field.id!, "LeftHand");
                              leftHand.attemptNo = await BiometricsApi()
                                  .getBioAttempt(widget.field.id!, "LeftHand");
                              showDialog<String>(
                                context: context,
                                builder: (BuildContext context) => AlertDialog(
                                    content: Container(
                                  height: 539.h,
                                  width: 768.w,
                                  child: Column(
                                      mainAxisAlignment:
                                          MainAxisAlignment.spaceBetween,
                                      children: [
                                        Row(
                                          mainAxisAlignment:
                                              MainAxisAlignment.spaceBetween,
                                          children: [
                                            Text(
                                              "Left Hand Capture",
                                              style: Theme.of(context)
                                                  .textTheme
                                                  .bodyLarge
                                                  ?.copyWith(
                                                      fontSize: 18,
                                                      fontWeight: bold,
                                                      color: black_shade_1),
                                            ),
                                            IconButton(
                                                onPressed: () {
                                                  Navigator.pop(context);
                                                },
                                                icon: Icon(
                                                  Icons.close,
                                                )),
                                          ],
                                        ),
                                        Divider(),
                                        Padding(
                                          padding: EdgeInsets.all(0),
                                          // EdgeInsets.fromLTRB(
                                          //     60.w, 35.h, 60.w, 35.h),
                                          child: Row(
                                            mainAxisAlignment:
                                                MainAxisAlignment.center,
                                            children: [
                                              ...temp.map((e) => Image.memory(
                                                    e!,
                                                    height: 190.h,
                                                    width: 190.h,
                                                  )),
                                            ],
                                          ),
                                        ),
                                        Divider(),
                                        Padding(
                                          padding: const EdgeInsets.all(0.0),
                                          child: Row(
                                            mainAxisAlignment:
                                                MainAxisAlignment.end,
                                            children: [
                                              OutlinedButton(
                                                  style: OutlinedButton.styleFrom(
                                                      maximumSize:
                                                          Size(160.w, 42.h),
                                                      minimumSize:
                                                          Size(160.w, 42.h),
                                                      side: BorderSide(
                                                          color:
                                                              solid_primary)),
                                                  onPressed: () async {
                                                    await BiometricsApi()
                                                        .invokeDiscoverSbi(
                                                            widget.field.id!,
                                                            "LeftHand");
                                                    await BiometricsApi()
                                                        .extractImageValues(
                                                            widget.field.id!,
                                                            "LeftHand")
                                                        .then((value) {
                                                      temp = value;
                                                    });
                                                    await BiometricsApi()
                                                        .incrementBioAttempt(
                                                            widget.field.id!,
                                                            "LeftHand");
                                                    leftHand.attemptNo =
                                                        await BiometricsApi()
                                                            .getBioAttempt(
                                                                widget
                                                                    .field.id!,
                                                                "LeftHand");
                                                  },
                                                  child: Text("RESCAN")),
                                              SizedBox(
                                                width: 10.w,
                                              ),
                                              ElevatedButton(
                                                style: ElevatedButton.styleFrom(
                                                    maximumSize:
                                                        Size(160.w, 42.h),
                                                    minimumSize:
                                                        Size(160.w, 42.h)),
                                                onPressed: () async {
                                                  leftHand.listOfBiometricsDto
                                                      .clear;
                                                  await BiometricsApi()
                                                      .getBestBiometrics(
                                                          widget.field.id!,
                                                          "LeftHand")
                                                      .then((value) async {
                                                    for (var e in value) {
                                                      leftHand
                                                          .listOfBiometricsDto
                                                          .add(BiometricsDto
                                                              .fromJson(json
                                                                  .decode(e!)));
                                                    }
                                                  });
                                                  leftHand.qualityPercentage =
                                                      avgScore(leftHand
                                                          .listOfBiometricsDto);
                                                  await BiometricsApi()
                                                      .extractImageValues(
                                                          widget.field.id!,
                                                          "LeftHand")
                                                      .then((value) {
                                                    leftHand.listofImages =
                                                        value;
                                                  });
                                                  leftHand.isScanned = true;
                                                  generateList(
                                                      context,
                                                      "${widget.field.id}",
                                                      leftHand);
                                                  setState(() {});
                                                  Navigator.pop(context);
                                                },
                                                child: Text("SAVE"),
                                              )
                                            ],
                                          ),
                                        )
                                      ]),
                                )),
                              );
                            },
                            icon: Icon(
                              Icons.crop_free,
                              color: solid_primary,
                              size: 14,
                            ),
                            label: Text(
                              "SCAN",
                              style: Theme.of(context)
                                  .textTheme
                                  .bodySmall
                                  ?.copyWith(
                                      fontSize: 14,
                                      fontWeight: bold,
                                      color: solid_primary),
                            ),
                            style: OutlinedButton.styleFrom(
                              side: BorderSide(color: solid_primary, width: 1),
                              shape: RoundedRectangleBorder(
                                borderRadius: BorderRadius.circular(5),
                              ),
                            ),
                          ),
                          Container(
                              height: 67.h,
                              width: 162.w,
                              decoration: BoxDecoration(
                                color: pure_white,
                                border: Border.all(
                                  color: secondaryColors.elementAt(14),
                                ),
                              ),
                              padding: EdgeInsets.all(12),
                              child: Row(
                                mainAxisAlignment:
                                    MainAxisAlignment.spaceBetween,
                                children: [
                                  Column(
                                    crossAxisAlignment:
                                        CrossAxisAlignment.start,
                                    children: [
                                      Text(
                                        "Attempts",
                                        style: Theme.of(context)
                                            .textTheme
                                            .bodyLarge
                                            ?.copyWith(
                                              fontSize: 14,
                                              color:
                                                  secondaryColors.elementAt(1),
                                            ),
                                      ),
                                      Text("${leftHand.attemptNo}"),
                                    ],
                                  ),
                                  Column(
                                    crossAxisAlignment:
                                        CrossAxisAlignment.start,
                                    children: [
                                      Text(
                                        "Exceptions",
                                        style: Theme.of(context)
                                            .textTheme
                                            .bodyLarge
                                            ?.copyWith(
                                              fontSize: 14,
                                              color:
                                                  secondaryColors.elementAt(1),
                                            ),
                                      ),
                                      Text("${noOfTrue(leftHand.exceptions)}"),
                                    ],
                                  ),
                                ],
                              )),
                          Container(
                            height: 157.h,
                            width: 338.h,
                            decoration: BoxDecoration(
                              color: pure_white,
                              border: Border.all(
                                color: secondaryColors.elementAt(14),
                              ),
                            ),
                            child: Column(
                              mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                              children: [
                                Text(
                                  "Quality",
                                  style: Theme.of(context)
                                      .textTheme
                                      .bodyLarge
                                      ?.copyWith(
                                          fontSize: 14,
                                          fontWeight: semiBold,
                                          color: black_shade_1),
                                ),
                                Text(
                                  "Threshold ${leftHand.thresholdPercentage}%",
                                  style: Theme.of(context)
                                      .textTheme
                                      .bodyLarge
                                      ?.copyWith(
                                        fontSize: 14,
                                        fontWeight: regular,
                                        color: secondaryColors.elementAt(1),
                                      ),
                                ),
                                Row(
                                  mainAxisAlignment: MainAxisAlignment.center,
                                  children: [
                                    LinearPercentIndicator(
                                      width: 260.h,
                                      lineHeight: 8,
                                      percent: leftHand.qualityPercentage / 100,
                                      backgroundColor: Colors.grey,
                                      progressColor: (leftHand.qualityPercentage
                                                  .toInt() >
                                              int.parse(
                                                  leftHand.thresholdPercentage))
                                          ? secondaryColors.elementAt(11)
                                          : secondaryColors.elementAt(20),
                                    ),
                                    SizedBox(
                                      width: 16.h,
                                    ),
                                    Text(
                                      "${leftHand.qualityPercentage.toInt()}%",
                                      style: Theme.of(context)
                                          .textTheme
                                          .bodyLarge
                                          ?.copyWith(
                                            fontSize: 14,
                                            fontWeight: regular,
                                            color: black_shade_1,
                                          ),
                                    ),
                                  ],
                                ),
                                Row(
                                  mainAxisAlignment: MainAxisAlignment.center,
                                  children: [
                                    Text(
                                      "Attempts",
                                      style: Theme.of(context)
                                          .textTheme
                                          .bodyLarge
                                          ?.copyWith(
                                              fontSize: 12,
                                              color: black_shade_1),
                                    ),
                                    SizedBox(
                                      width: 13.w,
                                    ),
                                    InkWell(
                                      onTap: () async {
                                        if (leftHand.attemptNo >= 1) {
                                          await BiometricsApi()
                                              .getBiometrics(widget.field.id!,
                                                  "LeftHand", 1)
                                              .then((value) {
                                            leftHand.listOfBiometricsDto
                                                .clear();
                                            for (var e in value) {
                                              leftHand.listOfBiometricsDto.add(
                                                  BiometricsDto.fromJson(
                                                      json.decode(e!)));
                                            }
                                          });
                                          setState(() {
                                            leftHand.qualityPercentage =
                                                avgScore(leftHand
                                                    .listOfBiometricsDto);
                                          });
                                          await BiometricsApi()
                                              .extractImageValuesByAttempt(
                                                  widget.field.id!,
                                                  "LeftHand",
                                                  1)
                                              .then((value) {
                                            leftHand.listofImages = value;
                                          });
                                          setState(() {});
                                        }
                                      },
                                      child: Container(
                                        padding: EdgeInsets.symmetric(
                                          vertical: 0,
                                          horizontal: 11,
                                        ),
                                        decoration: BoxDecoration(
                                          borderRadius:
                                              BorderRadius.circular(20),
                                          border: Border.all(
                                            color:
                                                secondaryColors.elementAt(17),
                                          ),
                                          color: (leftHand.attemptNo < 1)
                                              ? secondaryColors.elementAt(18)
                                              : secondaryColors.elementAt(11),
                                        ),
                                        child: Text(
                                          "1",
                                          style: Theme.of(context)
                                              .textTheme
                                              .bodyLarge
                                              ?.copyWith(
                                                  fontSize: 12,
                                                  color:
                                                      (leftHand.attemptNo < 1)
                                                          ? secondaryColors
                                                              .elementAt(19)
                                                          : pure_white,
                                                  fontWeight: semiBold),
                                        ),
                                      ),
                                    ),
                                    SizedBox(
                                      width: 9.5.w,
                                    ),
                                    InkWell(
                                      onTap: () async {
                                        if (leftHand.attemptNo >= 2) {
                                          await BiometricsApi()
                                              .getBiometrics(widget.field.id!,
                                                  "LeftHand", 2)
                                              .then((value) {
                                            leftHand.listOfBiometricsDto
                                                .clear();
                                            for (var e in value) {
                                              leftHand.listOfBiometricsDto.add(
                                                  BiometricsDto.fromJson(
                                                      json.decode(e!)));
                                            }
                                          });
                                          setState(() {
                                            leftHand.qualityPercentage =
                                                avgScore(leftHand
                                                    .listOfBiometricsDto);
                                          });
                                          await BiometricsApi()
                                              .extractImageValuesByAttempt(
                                                  widget.field.id!,
                                                  "LeftHand",
                                                  2)
                                              .then((value) {
                                            leftHand.listofImages = value;
                                          });
                                          setState(() {});
                                        }
                                      },
                                      child: Container(
                                        padding: EdgeInsets.symmetric(
                                          vertical: 0,
                                          horizontal: 11,
                                        ),
                                        decoration: BoxDecoration(
                                          borderRadius:
                                              BorderRadius.circular(20),
                                          border: Border.all(
                                            color:
                                                secondaryColors.elementAt(17),
                                          ),
                                          color: (leftHand.attemptNo < 2)
                                              ? secondaryColors.elementAt(18)
                                              : secondaryColors.elementAt(11),
                                        ),
                                        child: Text(
                                          "2",
                                          style: Theme.of(context)
                                              .textTheme
                                              .bodyLarge
                                              ?.copyWith(
                                                  fontSize: 12,
                                                  color:
                                                      (leftHand.attemptNo < 2)
                                                          ? secondaryColors
                                                              .elementAt(19)
                                                          : pure_white,
                                                  fontWeight: semiBold),
                                        ),
                                      ),
                                    ),
                                    SizedBox(
                                      width: 9.5.w,
                                    ),
                                    InkWell(
                                      onTap: () async {
                                        if (leftHand.attemptNo >= 3) {
                                          await BiometricsApi()
                                              .getBiometrics(widget.field.id!,
                                                  "LeftHand", 3)
                                              .then((value) {
                                            leftHand.listOfBiometricsDto
                                                .clear();
                                            for (var e in value) {
                                              leftHand.listOfBiometricsDto.add(
                                                  BiometricsDto.fromJson(
                                                      json.decode(e!)));
                                            }
                                          });
                                          setState(() {
                                            leftHand.qualityPercentage =
                                                avgScore(leftHand
                                                    .listOfBiometricsDto);
                                          });
                                          await BiometricsApi()
                                              .extractImageValuesByAttempt(
                                                  widget.field.id!,
                                                  "LeftHand",
                                                  3)
                                              .then((value) {
                                            leftHand.listofImages = value;
                                          });
                                          setState(() {});
                                        }
                                      },
                                      child: Container(
                                        padding: EdgeInsets.symmetric(
                                          vertical: 0,
                                          horizontal: 11,
                                        ),
                                        decoration: BoxDecoration(
                                          borderRadius:
                                              BorderRadius.circular(20),
                                          border: Border.all(
                                            color:
                                                secondaryColors.elementAt(17),
                                          ),
                                          color: (leftHand.attemptNo < 3)
                                              ? secondaryColors.elementAt(18)
                                              : secondaryColors.elementAt(11),
                                        ),
                                        child: Text(
                                          "3",
                                          style: Theme.of(context)
                                              .textTheme
                                              .bodyLarge
                                              ?.copyWith(
                                                  fontSize: 12,
                                                  color:
                                                      (leftHand.attemptNo < 3)
                                                          ? secondaryColors
                                                              .elementAt(19)
                                                          : pure_white,
                                                  fontWeight: semiBold),
                                        ),
                                      ),
                                    ),
                                  ],
                                )
                              ],
                            ),
                          ),
                        ],
                      ),
                    ),
                  ),
                if (biometricAttribute == "Thumbs")
                  BiometricCaptureScanBlock(
                    title: "ThumbsScan",
                    middleBlock: Container(
                      height: 460.h,
                      child: Column(
                        mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                        children: [
                          (thumbs.isScanned == false)
                              ? Row(
                                  mainAxisAlignment:
                                      MainAxisAlignment.spaceEvenly,
                                  children: [
                                    ...listOfImages(thumbs.listofImages).map(
                                      (e) => e,
                                    ),
                                  ],
                                )
                              : Row(
                                  mainAxisAlignment:
                                      MainAxisAlignment.spaceEvenly,
                                  children: [
                                    ...listOfResultImages(thumbs.listofImages)
                                        .map(
                                      (e) => e,
                                    ),
                                  ],
                                ),
                          OutlinedButton.icon(
                            onPressed: () async {
                              List<Uint8List?> temp = [];
                              await BiometricsApi().invokeDiscoverSbi(
                                  widget.field.id!, "Thumbs");
                              await BiometricsApi().getBestBiometrics(
                                  widget.field.id!, "Thumbs");
                              await BiometricsApi()
                                  .extractImageValues(
                                      widget.field.id!, "Thumbs")
                                  .then((value) {
                                temp = value;
                              });
                              await BiometricsApi().incrementBioAttempt(
                                  widget.field.id!, "Thumbs");
                              thumbs.attemptNo = await BiometricsApi()
                                  .getBioAttempt(widget.field.id!, "Thumbs");
                              showDialog<String>(
                                context: context,
                                builder: (BuildContext context) => AlertDialog(
                                    content: Container(
                                  height: 539.h,
                                  width: 768.w,
                                  child: Column(
                                      mainAxisAlignment:
                                          MainAxisAlignment.spaceBetween,
                                      children: [
                                        Row(
                                          mainAxisAlignment:
                                              MainAxisAlignment.spaceBetween,
                                          children: [
                                            Text(
                                              "Thumbs Capture",
                                              style: Theme.of(context)
                                                  .textTheme
                                                  .bodyLarge
                                                  ?.copyWith(
                                                      fontSize: 18,
                                                      fontWeight: bold,
                                                      color: black_shade_1),
                                            ),
                                            IconButton(
                                                onPressed: () {
                                                  Navigator.pop(context);
                                                },
                                                icon: Icon(
                                                  Icons.close,
                                                )),
                                          ],
                                        ),
                                        Divider(),
                                        Padding(
                                          padding: EdgeInsets.all(0),
                                          // EdgeInsets.fromLTRB(
                                          //     60.w, 35.h, 60.w, 35.h),
                                          child: Row(
                                            mainAxisAlignment:
                                                MainAxisAlignment.center,
                                            children: [
                                              ...temp.map((e) => Image.memory(
                                                    e!,
                                                    height: 310.h,
                                                    width: 310.h,
                                                  )),
                                            ],
                                          ),
                                        ),
                                        Divider(),
                                        Padding(
                                          padding: const EdgeInsets.all(0.0),
                                          child: Row(
                                            mainAxisAlignment:
                                                MainAxisAlignment.end,
                                            children: [
                                              OutlinedButton(
                                                  style: OutlinedButton.styleFrom(
                                                      maximumSize:
                                                          Size(160.w, 42.h),
                                                      minimumSize:
                                                          Size(160.w, 42.h),
                                                      side: BorderSide(
                                                          color:
                                                              solid_primary)),
                                                  onPressed: () async {
                                                    await BiometricsApi()
                                                        .invokeDiscoverSbi(
                                                            widget.field.id!,
                                                            "Thumbs");
                                                    await BiometricsApi()
                                                        .extractImageValues(
                                                            widget.field.id!,
                                                            "Thumbs")
                                                        .then((value) {
                                                      temp = value;
                                                    });
                                                    await BiometricsApi()
                                                        .incrementBioAttempt(
                                                            widget.field.id!,
                                                            "Thumbs");
                                                    thumbs.attemptNo =
                                                        await BiometricsApi()
                                                            .getBioAttempt(
                                                                widget
                                                                    .field.id!,
                                                                "Thumbs");
                                                  },
                                                  child: Text("RESCAN")),
                                              SizedBox(
                                                width: 10.w,
                                              ),
                                              ElevatedButton(
                                                style: ElevatedButton.styleFrom(
                                                    maximumSize:
                                                        Size(160.w, 42.h),
                                                    minimumSize:
                                                        Size(160.w, 42.h)),
                                                onPressed: () async {
                                                  thumbs.listOfBiometricsDto
                                                      .clear;
                                                  await BiometricsApi()
                                                      .getBestBiometrics(
                                                          widget.field.id!,
                                                          "Thumbs")
                                                      .then((value) async {
                                                    for (var e in value) {
                                                      thumbs.listOfBiometricsDto
                                                          .add(BiometricsDto
                                                              .fromJson(json
                                                                  .decode(e!)));
                                                    }
                                                  });
                                                  thumbs.qualityPercentage =
                                                      avgScore(thumbs
                                                          .listOfBiometricsDto);
                                                  await BiometricsApi()
                                                      .extractImageValues(
                                                          widget.field.id!,
                                                          "Thumbs")
                                                      .then((value) {
                                                    thumbs.listofImages = value;
                                                  });
                                                  thumbs.isScanned = true;
                                                  generateList(
                                                      context,
                                                      "${widget.field.id}",
                                                      thumbs);
                                                  setState(() {});
                                                  Navigator.pop(context);
                                                },
                                                child: Text("SAVE"),
                                              )
                                            ],
                                          ),
                                        )
                                      ]),
                                )),
                              );
                            },
                            icon: Icon(
                              Icons.crop_free,
                              color: solid_primary,
                              size: 14,
                            ),
                            label: Text(
                              "SCAN",
                              style: Theme.of(context)
                                  .textTheme
                                  .bodySmall
                                  ?.copyWith(
                                      fontSize: 14,
                                      fontWeight: bold,
                                      color: solid_primary),
                            ),
                            style: OutlinedButton.styleFrom(
                              side: BorderSide(color: solid_primary, width: 1),
                              shape: RoundedRectangleBorder(
                                borderRadius: BorderRadius.circular(5),
                              ),
                            ),
                          ),
                          Container(
                              height: 67.h,
                              width: 162.w,
                              decoration: BoxDecoration(
                                color: pure_white,
                                border: Border.all(
                                  color: secondaryColors.elementAt(14),
                                ),
                              ),
                              padding: EdgeInsets.all(12),
                              child: Row(
                                mainAxisAlignment:
                                    MainAxisAlignment.spaceBetween,
                                children: [
                                  Column(
                                    crossAxisAlignment:
                                        CrossAxisAlignment.start,
                                    children: [
                                      Text(
                                        "Attempts",
                                        style: Theme.of(context)
                                            .textTheme
                                            .bodyLarge
                                            ?.copyWith(
                                              fontSize: 14,
                                              color:
                                                  secondaryColors.elementAt(1),
                                            ),
                                      ),
                                      Text("${thumbs.attemptNo}"),
                                    ],
                                  ),
                                  Column(
                                    crossAxisAlignment:
                                        CrossAxisAlignment.start,
                                    children: [
                                      Text(
                                        "Exceptions",
                                        style: Theme.of(context)
                                            .textTheme
                                            .bodyLarge
                                            ?.copyWith(
                                              fontSize: 14,
                                              color:
                                                  secondaryColors.elementAt(1),
                                            ),
                                      ),
                                      Text("${noOfTrue(thumbs.exceptions)}"),
                                    ],
                                  ),
                                ],
                              )),
                          Container(
                            height: 157.h,
                            width: 338.h,
                            decoration: BoxDecoration(
                              color: pure_white,
                              border: Border.all(
                                color: secondaryColors.elementAt(14),
                              ),
                            ),
                            child: Column(
                              mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                              children: [
                                Text(
                                  "Quality",
                                  style: Theme.of(context)
                                      .textTheme
                                      .bodyLarge
                                      ?.copyWith(
                                          fontSize: 14,
                                          fontWeight: semiBold,
                                          color: black_shade_1),
                                ),
                                Text(
                                  "Threshold ${thumbs.thresholdPercentage}%",
                                  style: Theme.of(context)
                                      .textTheme
                                      .bodyLarge
                                      ?.copyWith(
                                        fontSize: 14,
                                        fontWeight: regular,
                                        color: secondaryColors.elementAt(1),
                                      ),
                                ),
                                Row(
                                  mainAxisAlignment: MainAxisAlignment.center,
                                  children: [
                                    LinearPercentIndicator(
                                      width: 260.h,
                                      lineHeight: 8,
                                      percent: thumbs.qualityPercentage / 100,
                                      backgroundColor: Colors.grey,
                                      progressColor: (thumbs.qualityPercentage
                                                  .toInt() >
                                              int.parse(
                                                  thumbs.thresholdPercentage))
                                          ? secondaryColors.elementAt(11)
                                          : secondaryColors.elementAt(20),
                                    ),
                                    SizedBox(
                                      width: 16.h,
                                    ),
                                    Text(
                                      "${thumbs.qualityPercentage.toInt()}%",
                                      style: Theme.of(context)
                                          .textTheme
                                          .bodyLarge
                                          ?.copyWith(
                                            fontSize: 14,
                                            fontWeight: regular,
                                            color: black_shade_1,
                                          ),
                                    ),
                                  ],
                                ),
                                Row(
                                  mainAxisAlignment: MainAxisAlignment.center,
                                  children: [
                                    Text(
                                      "Attempts",
                                      style: Theme.of(context)
                                          .textTheme
                                          .bodyLarge
                                          ?.copyWith(
                                              fontSize: 12,
                                              color: black_shade_1),
                                    ),
                                    SizedBox(
                                      width: 13.w,
                                    ),
                                    InkWell(
                                      onTap: () async {
                                        if (thumbs.attemptNo >= 1) {
                                          await BiometricsApi()
                                              .getBiometrics(
                                                  widget.field.id!, "Thumbs", 1)
                                              .then((value) {
                                            thumbs.listOfBiometricsDto.clear();
                                            for (var e in value) {
                                              thumbs.listOfBiometricsDto.add(
                                                  BiometricsDto.fromJson(
                                                      json.decode(e!)));
                                            }
                                          });
                                          setState(() {
                                            thumbs.qualityPercentage = avgScore(
                                                thumbs.listOfBiometricsDto);
                                          });
                                          await BiometricsApi()
                                              .extractImageValuesByAttempt(
                                                  widget.field.id!, "Thumbs", 1)
                                              .then((value) {
                                            thumbs.listofImages = value;
                                          });
                                          setState(() {});
                                        }
                                      },
                                      child: Container(
                                        padding: EdgeInsets.symmetric(
                                          vertical: 0,
                                          horizontal: 11,
                                        ),
                                        decoration: BoxDecoration(
                                          borderRadius:
                                              BorderRadius.circular(20),
                                          border: Border.all(
                                            color:
                                                secondaryColors.elementAt(17),
                                          ),
                                          color: (thumbs.attemptNo < 1)
                                              ? secondaryColors.elementAt(18)
                                              : secondaryColors.elementAt(11),
                                        ),
                                        child: Text(
                                          "1",
                                          style: Theme.of(context)
                                              .textTheme
                                              .bodyLarge
                                              ?.copyWith(
                                                  fontSize: 12,
                                                  color: (thumbs.attemptNo < 1)
                                                      ? secondaryColors
                                                          .elementAt(19)
                                                      : pure_white,
                                                  fontWeight: semiBold),
                                        ),
                                      ),
                                    ),
                                    SizedBox(
                                      width: 9.5.w,
                                    ),
                                    InkWell(
                                      onTap: () async {
                                        if (thumbs.attemptNo >= 2) {
                                          await BiometricsApi()
                                              .getBiometrics(
                                                  widget.field.id!, "Thumbs", 2)
                                              .then((value) {
                                            thumbs.listOfBiometricsDto.clear();
                                            for (var e in value) {
                                              thumbs.listOfBiometricsDto.add(
                                                  BiometricsDto.fromJson(
                                                      json.decode(e!)));
                                            }
                                          });
                                          setState(() {
                                            thumbs.qualityPercentage = avgScore(
                                                thumbs.listOfBiometricsDto);
                                          });
                                          await BiometricsApi()
                                              .extractImageValuesByAttempt(
                                                  widget.field.id!, "Thumbs", 2)
                                              .then((value) {
                                            thumbs.listofImages = value;
                                          });
                                          setState(() {});
                                        }
                                      },
                                      child: Container(
                                        padding: EdgeInsets.symmetric(
                                          vertical: 0,
                                          horizontal: 11,
                                        ),
                                        decoration: BoxDecoration(
                                          borderRadius:
                                              BorderRadius.circular(20),
                                          border: Border.all(
                                            color:
                                                secondaryColors.elementAt(17),
                                          ),
                                          color: (thumbs.attemptNo < 2)
                                              ? secondaryColors.elementAt(18)
                                              : secondaryColors.elementAt(11),
                                        ),
                                        child: Text(
                                          "2",
                                          style: Theme.of(context)
                                              .textTheme
                                              .bodyLarge
                                              ?.copyWith(
                                                  fontSize: 12,
                                                  color: (thumbs.attemptNo < 2)
                                                      ? secondaryColors
                                                          .elementAt(19)
                                                      : pure_white,
                                                  fontWeight: semiBold),
                                        ),
                                      ),
                                    ),
                                    SizedBox(
                                      width: 9.5.w,
                                    ),
                                    InkWell(
                                      onTap: () async {
                                        if (thumbs.attemptNo >= 3) {
                                          await BiometricsApi()
                                              .getBiometrics(
                                                  widget.field.id!, "Thumbs", 3)
                                              .then((value) {
                                            thumbs.listOfBiometricsDto.clear();
                                            for (var e in value) {
                                              thumbs.listOfBiometricsDto.add(
                                                  BiometricsDto.fromJson(
                                                      json.decode(e!)));
                                            }
                                          });
                                          setState(() {
                                            thumbs.qualityPercentage = avgScore(
                                                thumbs.listOfBiometricsDto);
                                          });
                                          await BiometricsApi()
                                              .extractImageValuesByAttempt(
                                                  widget.field.id!, "Thumbs", 3)
                                              .then((value) {
                                            thumbs.listofImages = value;
                                          });
                                          setState(() {});
                                        }
                                      },
                                      child: Container(
                                        padding: EdgeInsets.symmetric(
                                          vertical: 0,
                                          horizontal: 11,
                                        ),
                                        decoration: BoxDecoration(
                                          borderRadius:
                                              BorderRadius.circular(20),
                                          border: Border.all(
                                            color:
                                                secondaryColors.elementAt(17),
                                          ),
                                          color: (thumbs.attemptNo < 3)
                                              ? secondaryColors.elementAt(18)
                                              : secondaryColors.elementAt(11),
                                        ),
                                        child: Text(
                                          "3",
                                          style: Theme.of(context)
                                              .textTheme
                                              .bodyLarge
                                              ?.copyWith(
                                                  fontSize: 12,
                                                  color: (thumbs.attemptNo < 3)
                                                      ? secondaryColors
                                                          .elementAt(19)
                                                      : pure_white,
                                                  fontWeight: semiBold),
                                        ),
                                      ),
                                    ),
                                  ],
                                )
                              ],
                            ),
                          ),
                        ],
                      ),
                    ),
                  ),
                if (biometricAttribute == "Face")
                  BiometricCaptureScanBlock(
                    title: "FaceScan",
                    middleBlock: Container(
                      height: 460.h,
                      child: Column(
                        mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                        children: [
                          (face.isScanned == false)
                              ? Row(
                                  mainAxisAlignment:
                                      MainAxisAlignment.spaceEvenly,
                                  children: [
                                    ...listOfImages(face.listofImages).map(
                                      (e) => e,
                                    ),
                                  ],
                                )
                              : Row(
                                  mainAxisAlignment:
                                      MainAxisAlignment.spaceEvenly,
                                  children: [
                                    ...listOfResultImages(face.listofImages)
                                        .map(
                                      (e) => e,
                                    ),
                                  ],
                                ),
                          OutlinedButton.icon(
                            onPressed: () async {
                              List<Uint8List?> temp = [];
                              await BiometricsApi()
                                  .invokeDiscoverSbi(widget.field.id!, "Face");
                              await BiometricsApi()
                                  .getBestBiometrics(widget.field.id!, "Face");
                              await BiometricsApi()
                                  .extractImageValues(widget.field.id!, "Face")
                                  .then((value) {
                                temp = value;
                              });
                              await BiometricsApi().incrementBioAttempt(
                                  widget.field.id!, "Face");
                              face.attemptNo = await BiometricsApi()
                                  .getBioAttempt(widget.field.id!, "Face");
                              showDialog<String>(
                                context: context,
                                builder: (BuildContext context) => AlertDialog(
                                    content: Container(
                                  height: 539.h,
                                  width: 768.w,
                                  child: Column(
                                      mainAxisAlignment:
                                          MainAxisAlignment.spaceBetween,
                                      children: [
                                        Row(
                                          mainAxisAlignment:
                                              MainAxisAlignment.spaceBetween,
                                          children: [
                                            Text(
                                              "Face Capture",
                                              style: Theme.of(context)
                                                  .textTheme
                                                  .bodyLarge
                                                  ?.copyWith(
                                                      fontSize: 18,
                                                      fontWeight: bold,
                                                      color: black_shade_1),
                                            ),
                                            IconButton(
                                                onPressed: () {
                                                  Navigator.pop(context);
                                                },
                                                icon: Icon(
                                                  Icons.close,
                                                )),
                                          ],
                                        ),
                                        Divider(),
                                        Padding(
                                          padding: EdgeInsets.all(0),
                                          // EdgeInsets.fromLTRB(
                                          //     60.w, 35.h, 60.w, 35.h),
                                          child: Row(
                                            mainAxisAlignment:
                                                MainAxisAlignment.center,
                                            children: [
                                              ...temp.map((e) => Image.memory(
                                                    e!,
                                                    height: 310.h,
                                                    width: 310.h,
                                                  )),
                                            ],
                                          ),
                                        ),
                                        Divider(),
                                        Padding(
                                          padding: const EdgeInsets.all(0.0),
                                          child: Row(
                                            mainAxisAlignment:
                                                MainAxisAlignment.end,
                                            children: [
                                              OutlinedButton(
                                                  style: OutlinedButton.styleFrom(
                                                      maximumSize:
                                                          Size(160.w, 42.h),
                                                      minimumSize:
                                                          Size(160.w, 42.h),
                                                      side: BorderSide(
                                                          color:
                                                              solid_primary)),
                                                  onPressed: () async {
                                                    await BiometricsApi()
                                                        .invokeDiscoverSbi(
                                                            widget.field.id!,
                                                            "Face");
                                                    await BiometricsApi()
                                                        .extractImageValues(
                                                            widget.field.id!,
                                                            "Face")
                                                        .then((value) {
                                                      temp = value;
                                                    });
                                                    await BiometricsApi()
                                                        .incrementBioAttempt(
                                                            widget.field.id!,
                                                            "Face");
                                                    face.attemptNo =
                                                        await BiometricsApi()
                                                            .getBioAttempt(
                                                                widget
                                                                    .field.id!,
                                                                "Face");
                                                  },
                                                  child: Text("RESCAN")),
                                              SizedBox(
                                                width: 10.w,
                                              ),
                                              ElevatedButton(
                                                style: ElevatedButton.styleFrom(
                                                    maximumSize:
                                                        Size(160.w, 42.h),
                                                    minimumSize:
                                                        Size(160.w, 42.h)),
                                                onPressed: () async {
                                                  face.listOfBiometricsDto
                                                      .clear;
                                                  await BiometricsApi()
                                                      .getBestBiometrics(
                                                          widget.field.id!,
                                                          "Face")
                                                      .then((value) async {
                                                    for (var e in value) {
                                                      face.listOfBiometricsDto
                                                          .add(BiometricsDto
                                                              .fromJson(json
                                                                  .decode(e!)));
                                                    }
                                                  });
                                                  face.qualityPercentage =
                                                      avgScore(face
                                                          .listOfBiometricsDto);
                                                  await BiometricsApi()
                                                      .extractImageValues(
                                                          widget.field.id!,
                                                          "Face")
                                                      .then((value) {
                                                    face.listofImages = value;
                                                  });
                                                  face.isScanned = true;
                                                  generateList(
                                                      context,
                                                      "${widget.field.id}",
                                                      face);
                                                  setState(() {});
                                                  Navigator.pop(context);
                                                },
                                                child: Text("SAVE"),
                                              )
                                            ],
                                          ),
                                        )
                                      ]),
                                )),
                              );
                            },
                            icon: Icon(
                              Icons.crop_free,
                              color: solid_primary,
                              size: 14,
                            ),
                            label: Text(
                              "SCAN",
                              style: Theme.of(context)
                                  .textTheme
                                  .bodySmall
                                  ?.copyWith(
                                      fontSize: 14,
                                      fontWeight: bold,
                                      color: solid_primary),
                            ),
                            style: OutlinedButton.styleFrom(
                              side: BorderSide(color: solid_primary, width: 1),
                              shape: RoundedRectangleBorder(
                                borderRadius: BorderRadius.circular(5),
                              ),
                            ),
                          ),
                          Container(
                              height: 67.h,
                              width: 162.w,
                              decoration: BoxDecoration(
                                color: pure_white,
                                border: Border.all(
                                  color: secondaryColors.elementAt(14),
                                ),
                              ),
                              padding: EdgeInsets.all(12),
                              child: Row(
                                mainAxisAlignment:
                                    MainAxisAlignment.spaceBetween,
                                children: [
                                  Column(
                                    crossAxisAlignment:
                                        CrossAxisAlignment.start,
                                    children: [
                                      Text(
                                        "Attempts",
                                        style: Theme.of(context)
                                            .textTheme
                                            .bodyLarge
                                            ?.copyWith(
                                              fontSize: 14,
                                              color:
                                                  secondaryColors.elementAt(1),
                                            ),
                                      ),
                                      Text(
                                        "${face.attemptNo}",
                                        style: Theme.of(context)
                                            .textTheme
                                            .bodyLarge
                                            ?.copyWith(
                                                fontSize: 16,
                                                color: black_shade_1,
                                                fontWeight: semiBold),
                                      ),
                                    ],
                                  ),
                                  Column(
                                    crossAxisAlignment:
                                        CrossAxisAlignment.start,
                                    children: [
                                      Text(
                                        "Exceptions",
                                        style: Theme.of(context)
                                            .textTheme
                                            .bodyLarge
                                            ?.copyWith(
                                              fontSize: 14,
                                              color:
                                                  secondaryColors.elementAt(1),
                                            ),
                                      ),
                                      Text(
                                        "-",
                                        style: Theme.of(context)
                                            .textTheme
                                            .bodyLarge
                                            ?.copyWith(
                                                fontSize: 16,
                                                color: black_shade_1,
                                                fontWeight: semiBold),
                                      ),
                                    ],
                                  ),
                                ],
                              )),
                          Container(
                            height: 157.h,
                            width: 338.h,
                            decoration: BoxDecoration(
                              color: pure_white,
                              border: Border.all(
                                color: secondaryColors.elementAt(14),
                              ),
                            ),
                            child: Column(
                              mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                              children: [
                                Text(
                                  "Quality",
                                  style: Theme.of(context)
                                      .textTheme
                                      .bodyLarge
                                      ?.copyWith(
                                          fontSize: 14,
                                          fontWeight: semiBold,
                                          color: black_shade_1),
                                ),
                                Text(
                                  "Threshold ${face.thresholdPercentage}%",
                                  style: Theme.of(context)
                                      .textTheme
                                      .bodyLarge
                                      ?.copyWith(
                                        fontSize: 14,
                                        fontWeight: regular,
                                        color: secondaryColors.elementAt(1),
                                      ),
                                ),
                                Row(
                                  mainAxisAlignment: MainAxisAlignment.center,
                                  children: [
                                    LinearPercentIndicator(
                                      width: 260.h,
                                      lineHeight: 8,
                                      percent: face.qualityPercentage / 100,
                                      backgroundColor: Colors.grey,
                                      progressColor:
                                          (face.qualityPercentage.toInt() >
                                                  int.parse(
                                                      face.thresholdPercentage))
                                              ? secondaryColors.elementAt(11)
                                              : secondaryColors.elementAt(20),
                                    ),
                                    SizedBox(
                                      width: 16.h,
                                    ),
                                    Text(
                                      "${face.qualityPercentage.toInt()}%",
                                      style: Theme.of(context)
                                          .textTheme
                                          .bodyLarge
                                          ?.copyWith(
                                            fontSize: 14,
                                            fontWeight: regular,
                                            color: black_shade_1,
                                          ),
                                    ),
                                  ],
                                ),
                                Row(
                                  mainAxisAlignment: MainAxisAlignment.center,
                                  children: [
                                    Text(
                                      "Attempts",
                                      style: Theme.of(context)
                                          .textTheme
                                          .bodyLarge
                                          ?.copyWith(
                                              fontSize: 12,
                                              color: black_shade_1),
                                    ),
                                    SizedBox(
                                      width: 13.w,
                                    ),
                                    InkWell(
                                      onTap: () async {
                                        if (face.attemptNo >= 1) {
                                          await BiometricsApi()
                                              .getBiometrics(
                                                  widget.field.id!, "Face", 1)
                                              .then((value) {
                                            face.listOfBiometricsDto.clear();
                                            for (var e in value) {
                                              face.listOfBiometricsDto.add(
                                                  BiometricsDto.fromJson(
                                                      json.decode(e!)));
                                            }
                                          });
                                          setState(() {
                                            face.qualityPercentage = avgScore(
                                                face.listOfBiometricsDto);
                                          });
                                          await BiometricsApi()
                                              .extractImageValuesByAttempt(
                                                  widget.field.id!, "Face", 1)
                                              .then((value) {
                                            face.listofImages = value;
                                          });
                                          setState(() {});
                                        }
                                      },
                                      child: Container(
                                        padding: EdgeInsets.symmetric(
                                          vertical: 0,
                                          horizontal: 11,
                                        ),
                                        decoration: BoxDecoration(
                                          borderRadius:
                                              BorderRadius.circular(20),
                                          border: Border.all(
                                            color:
                                                secondaryColors.elementAt(17),
                                          ),
                                          color: (face.attemptNo < 1)
                                              ? secondaryColors.elementAt(18)
                                              : secondaryColors.elementAt(11),
                                        ),
                                        child: Text(
                                          "1",
                                          style: Theme.of(context)
                                              .textTheme
                                              .bodyLarge
                                              ?.copyWith(
                                                  fontSize: 12,
                                                  color: (face.attemptNo < 1)
                                                      ? secondaryColors
                                                          .elementAt(19)
                                                      : pure_white,
                                                  fontWeight: semiBold),
                                        ),
                                      ),
                                    ),
                                    SizedBox(
                                      width: 9.5.w,
                                    ),
                                    InkWell(
                                      onTap: () async {
                                        if (face.attemptNo >= 2) {
                                          await BiometricsApi()
                                              .getBiometrics(
                                                  widget.field.id!, "Face", 2)
                                              .then((value) {
                                            face.listOfBiometricsDto.clear();
                                            for (var e in value) {
                                              face.listOfBiometricsDto.add(
                                                  BiometricsDto.fromJson(
                                                      json.decode(e!)));
                                            }
                                          });
                                          setState(() {
                                            face.qualityPercentage = avgScore(
                                                face.listOfBiometricsDto);
                                          });
                                          await BiometricsApi()
                                              .extractImageValuesByAttempt(
                                                  widget.field.id!, "Face", 2)
                                              .then((value) {
                                            face.listofImages = value;
                                          });
                                          setState(() {});
                                        }
                                      },
                                      child: Container(
                                        padding: EdgeInsets.symmetric(
                                          vertical: 0,
                                          horizontal: 11,
                                        ),
                                        decoration: BoxDecoration(
                                          borderRadius:
                                              BorderRadius.circular(20),
                                          border: Border.all(
                                            color:
                                                secondaryColors.elementAt(17),
                                          ),
                                          color: (face.attemptNo < 2)
                                              ? secondaryColors.elementAt(18)
                                              : secondaryColors.elementAt(11),
                                        ),
                                        child: Text(
                                          "2",
                                          style: Theme.of(context)
                                              .textTheme
                                              .bodyLarge
                                              ?.copyWith(
                                                  fontSize: 12,
                                                  color: (face.attemptNo < 2)
                                                      ? secondaryColors
                                                          .elementAt(19)
                                                      : pure_white,
                                                  fontWeight: semiBold),
                                        ),
                                      ),
                                    ),
                                    SizedBox(
                                      width: 9.5.w,
                                    ),
                                    InkWell(
                                      onTap: () async {
                                        if (face.attemptNo >= 3) {
                                          await BiometricsApi()
                                              .getBiometrics(
                                                  widget.field.id!, "Face", 3)
                                              .then((value) {
                                            face.listOfBiometricsDto.clear();
                                            for (var e in value) {
                                              face.listOfBiometricsDto.add(
                                                  BiometricsDto.fromJson(
                                                      json.decode(e!)));
                                            }
                                          });
                                          setState(() {
                                            face.qualityPercentage = avgScore(
                                                face.listOfBiometricsDto);
                                          });
                                          await BiometricsApi()
                                              .extractImageValuesByAttempt(
                                                  widget.field.id!, "Face", 3)
                                              .then((value) {
                                            face.listofImages = value;
                                          });
                                          setState(() {});
                                        }
                                      },
                                      child: Container(
                                        padding: EdgeInsets.symmetric(
                                          vertical: 0,
                                          horizontal: 11,
                                        ),
                                        decoration: BoxDecoration(
                                          borderRadius:
                                              BorderRadius.circular(20),
                                          border: Border.all(
                                            color:
                                                secondaryColors.elementAt(17),
                                          ),
                                          color: (face.attemptNo < 3)
                                              ? secondaryColors.elementAt(18)
                                              : secondaryColors.elementAt(11),
                                        ),
                                        child: Text(
                                          "3",
                                          style: Theme.of(context)
                                              .textTheme
                                              .bodyLarge
                                              ?.copyWith(
                                                  fontSize: 12,
                                                  color: (face.attemptNo < 3)
                                                      ? secondaryColors
                                                          .elementAt(19)
                                                      : pure_white,
                                                  fontWeight: semiBold),
                                        ),
                                      ),
                                    ),
                                  ],
                                )
                              ],
                            ),
                          ),
                        ],
                      ),
                    ),
                  ),
                if (biometricAttribute == "Exception")
                  BiometricCaptureScanBlock(
                    title: "ExceptionScan",
                    middleBlock: Container(
                      height: 460.h,
                      child: Column(
                        mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                        children: [
                          (exception.isScanned == false)
                              ? Row(
                                  mainAxisAlignment:
                                      MainAxisAlignment.spaceEvenly,
                                  children: [
                                    ...listOfImages(exception.listofImages).map(
                                      (e) => e,
                                    ),
                                  ],
                                )
                              : Row(
                                  mainAxisAlignment:
                                      MainAxisAlignment.spaceEvenly,
                                  children: [
                                    ...listOfResultImages(
                                            exception.listofImages)
                                        .map(
                                      (e) => e,
                                    ),
                                  ],
                                ),
                          OutlinedButton.icon(
                            onPressed: () async {
                              List<Uint8List?> temp = [];
                              await BiometricsApi().invokeDiscoverSbi(
                                  widget.field.id!, "Exception");
                              await BiometricsApi().getBestBiometrics(
                                  widget.field.id!, "Exception");
                              await BiometricsApi()
                                  .extractImageValues(
                                      widget.field.id!, "Exception")
                                  .then((value) {
                                temp = value;
                              });
                              await BiometricsApi().incrementBioAttempt(
                                  widget.field.id!, "Exception");
                              exception.attemptNo = await BiometricsApi()
                                  .getBioAttempt(widget.field.id!, "Exception");
                              showDialog<String>(
                                context: context,
                                builder: (BuildContext context) => AlertDialog(
                                    content: Container(
                                  height: 539.h,
                                  width: 768.w,
                                  child: Column(
                                      mainAxisAlignment:
                                          MainAxisAlignment.spaceBetween,
                                      children: [
                                        Row(
                                          mainAxisAlignment:
                                              MainAxisAlignment.spaceBetween,
                                          children: [
                                            Text(
                                              "Exception Capture",
                                              style: Theme.of(context)
                                                  .textTheme
                                                  .bodyLarge
                                                  ?.copyWith(
                                                      fontSize: 18,
                                                      fontWeight: bold,
                                                      color: black_shade_1),
                                            ),
                                            IconButton(
                                                onPressed: () {
                                                  Navigator.pop(context);
                                                },
                                                icon: Icon(
                                                  Icons.close,
                                                )),
                                          ],
                                        ),
                                        Divider(),
                                        Padding(
                                          padding: EdgeInsets.all(0),
                                          // EdgeInsets.fromLTRB(
                                          //     60.w, 35.h, 60.w, 35.h),
                                          child: Row(
                                            mainAxisAlignment:
                                                MainAxisAlignment.center,
                                            children: [
                                              ...temp.map((e) => Image.memory(
                                                    e!,
                                                    height: 310.h,
                                                    width: 310.h,
                                                  )),
                                            ],
                                          ),
                                        ),
                                        Divider(),
                                        Padding(
                                          padding: const EdgeInsets.all(0.0),
                                          child: Row(
                                            mainAxisAlignment:
                                                MainAxisAlignment.end,
                                            children: [
                                              OutlinedButton(
                                                  style: OutlinedButton.styleFrom(
                                                      maximumSize:
                                                          Size(160.w, 42.h),
                                                      minimumSize:
                                                          Size(160.w, 42.h),
                                                      side: BorderSide(
                                                          color:
                                                              solid_primary)),
                                                  onPressed: () async {
                                                    await BiometricsApi()
                                                        .invokeDiscoverSbi(
                                                            widget.field.id!,
                                                            "Exception");
                                                    await BiometricsApi()
                                                        .extractImageValues(
                                                            widget.field.id!,
                                                            "Exception")
                                                        .then((value) {
                                                      temp = value;
                                                    });
                                                    await BiometricsApi()
                                                        .incrementBioAttempt(
                                                            widget.field.id!,
                                                            "Exception");
                                                    exception.attemptNo =
                                                        await BiometricsApi()
                                                            .getBioAttempt(
                                                                widget
                                                                    .field.id!,
                                                                "Exception");
                                                  },
                                                  child: Text("RESCAN")),
                                              SizedBox(
                                                width: 10.w,
                                              ),
                                              ElevatedButton(
                                                style: ElevatedButton.styleFrom(
                                                    maximumSize:
                                                        Size(160.w, 42.h),
                                                    minimumSize:
                                                        Size(160.w, 42.h)),
                                                onPressed: () async {
                                                  exception.listOfBiometricsDto
                                                      .clear;
                                                  await BiometricsApi()
                                                      .getBestBiometrics(
                                                          widget.field.id!,
                                                          "Exception")
                                                      .then((value) async {
                                                    for (var e in value) {
                                                      exception
                                                          .listOfBiometricsDto
                                                          .add(BiometricsDto
                                                              .fromJson(json
                                                                  .decode(e!)));
                                                    }
                                                  });
                                                  exception.qualityPercentage =
                                                      avgScore(exception
                                                          .listOfBiometricsDto);
                                                  await BiometricsApi()
                                                      .extractImageValues(
                                                          widget.field.id!,
                                                          "Exception")
                                                      .then((value) {
                                                    exception.listofImages =
                                                        value;
                                                  });
                                                  exception.isScanned = true;
                                                  generateList(
                                                      context,
                                                      "${widget.field.id}",
                                                      exception);
                                                  setState(() {});
                                                  Navigator.pop(context);
                                                },
                                                child: Text("SAVE"),
                                              )
                                            ],
                                          ),
                                        )
                                      ]),
                                )),
                              );
                            },
                            icon: Icon(
                              Icons.crop_free,
                              color: solid_primary,
                              size: 14,
                            ),
                            label: Text(
                              "SCAN",
                              style: Theme.of(context)
                                  .textTheme
                                  .bodySmall
                                  ?.copyWith(
                                      fontSize: 14,
                                      fontWeight: bold,
                                      color: solid_primary),
                            ),
                            style: OutlinedButton.styleFrom(
                              side: BorderSide(color: solid_primary, width: 1),
                              shape: RoundedRectangleBorder(
                                borderRadius: BorderRadius.circular(5),
                              ),
                            ),
                          ),
                          Container(
                              height: 67.h,
                              width: 162.w,
                              decoration: BoxDecoration(
                                color: pure_white,
                                border: Border.all(
                                  color: secondaryColors.elementAt(14),
                                ),
                              ),
                              padding: EdgeInsets.all(12),
                              child: Row(
                                mainAxisAlignment:
                                    MainAxisAlignment.spaceBetween,
                                children: [
                                  Column(
                                    crossAxisAlignment:
                                        CrossAxisAlignment.start,
                                    children: [
                                      Text(
                                        "Attempts",
                                        style: Theme.of(context)
                                            .textTheme
                                            .bodyLarge
                                            ?.copyWith(
                                              fontSize: 14,
                                              color:
                                                  secondaryColors.elementAt(1),
                                            ),
                                      ),
                                      Text(
                                        "${exception.attemptNo}",
                                        style: Theme.of(context)
                                            .textTheme
                                            .bodyLarge
                                            ?.copyWith(
                                                fontSize: 16,
                                                color: black_shade_1,
                                                fontWeight: semiBold),
                                      ),
                                    ],
                                  ),
                                  Column(
                                    crossAxisAlignment:
                                        CrossAxisAlignment.start,
                                    children: [
                                      Text(
                                        "Exceptions",
                                        style: Theme.of(context)
                                            .textTheme
                                            .bodyLarge
                                            ?.copyWith(
                                              fontSize: 14,
                                              color:
                                                  secondaryColors.elementAt(1),
                                            ),
                                      ),
                                      Text(
                                        "-",
                                        style: Theme.of(context)
                                            .textTheme
                                            .bodyLarge
                                            ?.copyWith(
                                                fontSize: 16,
                                                color: black_shade_1,
                                                fontWeight: semiBold),
                                      ),
                                    ],
                                  ),
                                ],
                              )),
                          Container(
                            height: 157.h,
                            width: 338.h,
                            decoration: BoxDecoration(
                              color: pure_white,
                              border: Border.all(
                                color: secondaryColors.elementAt(14),
                              ),
                            ),
                            child: Column(
                              mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                              children: [
                                Text(
                                  "Quality",
                                  style: Theme.of(context)
                                      .textTheme
                                      .bodyLarge
                                      ?.copyWith(
                                          fontSize: 14,
                                          fontWeight: semiBold,
                                          color: black_shade_1),
                                ),
                                Text(
                                  "Threshold NA%",
                                  style: Theme.of(context)
                                      .textTheme
                                      .bodyLarge
                                      ?.copyWith(
                                        fontSize: 14,
                                        fontWeight: regular,
                                        color: secondaryColors.elementAt(1),
                                      ),
                                ),
                                Row(
                                  mainAxisAlignment: MainAxisAlignment.center,
                                  children: [
                                    LinearPercentIndicator(
                                      width: 260.h,
                                      lineHeight: 8,
                                      percent:
                                          exception.qualityPercentage / 100,
                                      backgroundColor: Colors.grey,
                                      progressColor:
                                          (exception.qualityPercentage.toInt() >
                                                  int.parse(exception
                                                      .thresholdPercentage))
                                              ? secondaryColors.elementAt(11)
                                              : secondaryColors.elementAt(20),
                                    ),
                                    SizedBox(
                                      width: 16.h,
                                    ),
                                    Text(
                                      "${exception.qualityPercentage.toInt()}%",
                                      style: Theme.of(context)
                                          .textTheme
                                          .bodyLarge
                                          ?.copyWith(
                                            fontSize: 14,
                                            fontWeight: regular,
                                            color: black_shade_1,
                                          ),
                                    ),
                                  ],
                                ),
                                Row(
                                  mainAxisAlignment: MainAxisAlignment.center,
                                  children: [
                                    Text(
                                      "Attempts",
                                      style: Theme.of(context)
                                          .textTheme
                                          .bodyLarge
                                          ?.copyWith(
                                              fontSize: 12,
                                              color: black_shade_1),
                                    ),
                                    SizedBox(
                                      width: 13.w,
                                    ),
                                    InkWell(
                                      onTap: () async {
                                        if (exception.attemptNo >= 1) {
                                          await BiometricsApi()
                                              .getBiometrics(widget.field.id!,
                                                  "Exception", 1)
                                              .then((value) {
                                            exception.listOfBiometricsDto
                                                .clear();
                                            for (var e in value) {
                                              exception.listOfBiometricsDto.add(
                                                  BiometricsDto.fromJson(
                                                      json.decode(e!)));
                                            }
                                          });
                                          setState(() {
                                            exception.qualityPercentage =
                                                avgScore(exception
                                                    .listOfBiometricsDto);
                                          });
                                          await BiometricsApi()
                                              .extractImageValuesByAttempt(
                                                  widget.field.id!,
                                                  "Exception",
                                                  1)
                                              .then((value) {
                                            exception.listofImages = value;
                                          });
                                          setState(() {});
                                        }
                                      },
                                      child: Container(
                                        padding: EdgeInsets.symmetric(
                                          vertical: 0,
                                          horizontal: 11,
                                        ),
                                        decoration: BoxDecoration(
                                          borderRadius:
                                              BorderRadius.circular(20),
                                          border: Border.all(
                                            color:
                                                secondaryColors.elementAt(17),
                                          ),
                                          color: (exception.attemptNo < 1)
                                              ? secondaryColors.elementAt(18)
                                              : secondaryColors.elementAt(11),
                                        ),
                                        child: Text(
                                          "1",
                                          style: Theme.of(context)
                                              .textTheme
                                              .bodyLarge
                                              ?.copyWith(
                                                  fontSize: 12,
                                                  color:
                                                      (exception.attemptNo < 1)
                                                          ? secondaryColors
                                                              .elementAt(19)
                                                          : pure_white,
                                                  fontWeight: semiBold),
                                        ),
                                      ),
                                    ),
                                    SizedBox(
                                      width: 9.5.w,
                                    ),
                                    InkWell(
                                      onTap: () async {
                                        if (exception.attemptNo >= 2) {
                                          await BiometricsApi()
                                              .getBiometrics(widget.field.id!,
                                                  "Exception", 2)
                                              .then((value) {
                                            exception.listOfBiometricsDto
                                                .clear();
                                            for (var e in value) {
                                              exception.listOfBiometricsDto.add(
                                                  BiometricsDto.fromJson(
                                                      json.decode(e!)));
                                            }
                                          });
                                          setState(() {
                                            exception.qualityPercentage =
                                                avgScore(exception
                                                    .listOfBiometricsDto);
                                          });
                                          await BiometricsApi()
                                              .extractImageValuesByAttempt(
                                                  widget.field.id!,
                                                  "Exception",
                                                  2)
                                              .then((value) {
                                            exception.listofImages = value;
                                          });
                                          setState(() {});
                                        }
                                      },
                                      child: Container(
                                        padding: EdgeInsets.symmetric(
                                          vertical: 0,
                                          horizontal: 11,
                                        ),
                                        decoration: BoxDecoration(
                                          borderRadius:
                                              BorderRadius.circular(20),
                                          border: Border.all(
                                            color:
                                                secondaryColors.elementAt(17),
                                          ),
                                          color: (exception.attemptNo < 2)
                                              ? secondaryColors.elementAt(18)
                                              : secondaryColors.elementAt(11),
                                        ),
                                        child: Text(
                                          "2",
                                          style: Theme.of(context)
                                              .textTheme
                                              .bodyLarge
                                              ?.copyWith(
                                                  fontSize: 12,
                                                  color:
                                                      (exception.attemptNo < 2)
                                                          ? secondaryColors
                                                              .elementAt(19)
                                                          : pure_white,
                                                  fontWeight: semiBold),
                                        ),
                                      ),
                                    ),
                                    SizedBox(
                                      width: 9.5.w,
                                    ),
                                    InkWell(
                                      onTap: () async {
                                        if (exception.attemptNo >= 3) {
                                          await BiometricsApi()
                                              .getBiometrics(widget.field.id!,
                                                  "Exception", 3)
                                              .then((value) {
                                            exception.listOfBiometricsDto
                                                .clear();
                                            for (var e in value) {
                                              exception.listOfBiometricsDto.add(
                                                  BiometricsDto.fromJson(
                                                      json.decode(e!)));
                                            }
                                          });
                                          setState(() {
                                            exception.qualityPercentage =
                                                avgScore(exception
                                                    .listOfBiometricsDto);
                                          });
                                          await BiometricsApi()
                                              .extractImageValuesByAttempt(
                                                  widget.field.id!,
                                                  "Exception",
                                                  3)
                                              .then((value) {
                                            exception.listofImages = value;
                                          });
                                          setState(() {});
                                        }
                                      },
                                      child: Container(
                                        padding: EdgeInsets.symmetric(
                                          vertical: 0,
                                          horizontal: 11,
                                        ),
                                        decoration: BoxDecoration(
                                          borderRadius:
                                              BorderRadius.circular(20),
                                          border: Border.all(
                                            color:
                                                secondaryColors.elementAt(17),
                                          ),
                                          color: (exception.attemptNo < 3)
                                              ? secondaryColors.elementAt(18)
                                              : secondaryColors.elementAt(11),
                                        ),
                                        child: Text(
                                          "3",
                                          style: Theme.of(context)
                                              .textTheme
                                              .bodyLarge
                                              ?.copyWith(
                                                  fontSize: 12,
                                                  color:
                                                      (exception.attemptNo < 3)
                                                          ? secondaryColors
                                                              .elementAt(19)
                                                          : pure_white,
                                                  fontWeight: semiBold),
                                        ),
                                      ),
                                    ),
                                  ],
                                )
                              ],
                            ),
                          ),
                        ],
                      ),
                    ),
                  ),
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
                                            color: black_shade_1)),
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
                                            }
                                          }
                                          if (!iris.exceptions.contains(true)) {
                                            iris.exceptionType = "";
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
                                            color: black_shade_1)),
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
                                            }
                                          }
                                          if (!iris.exceptions.contains(true)) {
                                            iris.exceptionType = "";
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
                                          color: black_shade_1)),
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
                                              }
                                            }
                                            if (!rightHand.exceptions
                                                .contains(true)) {
                                              rightHand.exceptionType = "";
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
                                              }
                                            }
                                            if (!rightHand.exceptions
                                                .contains(true)) {
                                              rightHand.exceptionType = "";
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
                                              }
                                            }
                                            if (!rightHand.exceptions
                                                .contains(true)) {
                                              rightHand.exceptionType = "";
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
                                              }
                                            }
                                            if (!rightHand.exceptions
                                                .contains(true)) {
                                              rightHand.exceptionType = "";
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
                                          color: black_shade_1)),
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
                                              }
                                            }
                                            if (!leftHand.exceptions
                                                .contains(true)) {
                                              leftHand.exceptionType = "";
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
                                              }
                                            }
                                            if (!leftHand.exceptions
                                                .contains(true)) {
                                              leftHand.exceptionType = "";
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
                                              }
                                            }
                                            if (!leftHand.exceptions
                                                .contains(true)) {
                                              leftHand.exceptionType = "";
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
                                              }
                                            }
                                            if (!leftHand.exceptions
                                                .contains(true)) {
                                              leftHand.exceptionType = "";
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
                                          color: black_shade_1)),
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
                                              }
                                            }
                                            if (!thumbs.exceptions
                                                .contains(true)) {
                                              thumbs.exceptionType = "";
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
                                              }
                                            }
                                            if (!thumbs.exceptions
                                                .contains(true)) {
                                              thumbs.exceptionType = "";
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
                                          color: black_shade_1)),
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
                                          color: black_shade_1)),
                              SizedBox(
                                height: 64.h,
                              ),
                              Image.asset(
                                "assets/images/Person@2x.png",
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
