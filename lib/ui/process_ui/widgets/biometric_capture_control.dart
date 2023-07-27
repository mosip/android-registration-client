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

  @override
  State<BiometricCaptureControl> createState() =>
      _BiometricCaptureControlState();
}

class _BiometricCaptureControlState extends State<BiometricCaptureControl> {
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
    widget.leftHand.thresholdPercentage =
        context.read<GlobalProvider>().thresholdValuesMap[
            "mosip.registration.leftslap_fingerprint_threshold"]!;
    widget.rightHand.thresholdPercentage =
        context.read<GlobalProvider>().thresholdValuesMap[
            "mosip.registration.rightslap_fingerprint_threshold"]!;
    widget.iris.thresholdPercentage =
        context.read<GlobalProvider>().thresholdValuesMap[
            "mosip.registration.iris_threshold"]!;
    widget.thumbs.thresholdPercentage =
        context.read<GlobalProvider>().thresholdValuesMap[
            "mosip.registration.thumbs_fingerprint_threshold"]!;
    widget.face.thresholdPercentage =
        context.read<GlobalProvider>().thresholdValuesMap[
            "mosip.registration.face_threshold"]!;
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
          widget.iris = context
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
          widget.rightHand = context
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
          widget.leftHand = context
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
          widget.thumbs = context
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
          widget.face = context
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
          widget.exception = context
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
        // ElevatedButton(
        //     child: Text("temp"),
        //     onPressed: () async {
        //       await BiometricsApi()
        //           .getThresholdValue("mosip.registration.iris_threshold")
        //           .then((value) {
        //         print(value);
        //       });
        //     }),
        Padding(
          padding: const EdgeInsets.fromLTRB(0, 26, 0, 27),
          child: Text(
            context.read<GlobalProvider>().chooseLanguage(widget.field.label!),
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
                          onTap: () async {
                            setState(() {
                              widget.biometricAttribute = "Iris";
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
                                          color: (widget.iris.isScanned == true)
                                              ? (widget.iris.exceptions
                                                      .contains(true))
                                                  ? secondaryColors
                                                      .elementAt(16)
                                                  : secondaryColors
                                                      .elementAt(11)
                                              : (widget.biometricAttribute ==
                                                      "Iris")
                                                  ? secondaryColors
                                                      .elementAt(12)
                                                  : secondaryColors
                                                      .elementAt(14)),
                                      borderRadius: BorderRadius.circular(10)),
                                  child: (widget.iris.isScanned == true)
                                      ? Row(
                                          mainAxisAlignment:
                                              MainAxisAlignment.center,
                                          children: [
                                            ...widget.iris.listofImages
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
                              if (widget.iris.isScanned == true)
                                Positioned(
                                    top: 14,
                                    left: 14,
                                    child: (widget.iris.exceptions
                                            .contains(true))
                                        ? Image.asset(
                                            "assets/images/Ellipse 1183.png")
                                        : Image.asset(
                                            "assets/images/Ellipse 1181.png")),
                              if (widget.iris.isScanned == true)
                                Positioned(
                                    top: 6,
                                    right: 6,
                                    child: (widget.iris.exceptions
                                            .contains(true))
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
                            onTap: () async {
                              setState(() {
                                widget.biometricAttribute = "Right Hand";
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
                                            color: (widget
                                                        .rightHand.isScanned ==
                                                    true)
                                                ? (widget.rightHand.exceptions
                                                        .contains(true))
                                                    ? secondaryColors
                                                        .elementAt(16)
                                                    : secondaryColors
                                                        .elementAt(11)
                                                : (widget.biometricAttribute ==
                                                        "Right Hand")
                                                    ? secondaryColors
                                                        .elementAt(12)
                                                    : secondaryColors
                                                        .elementAt(14)),
                                        borderRadius:
                                            BorderRadius.circular(10)),
                                    child: (widget.rightHand.isScanned == true)
                                        ? Row(
                                            mainAxisAlignment:
                                                MainAxisAlignment.center,
                                            children: [
                                              ...widget.rightHand.listofImages
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
                                if (widget.rightHand.isScanned == true)
                                  Positioned(
                                      top: 14,
                                      left: 14,
                                      child: (widget.rightHand.exceptions
                                              .contains(true))
                                          ? Image.asset(
                                              "assets/images/Ellipse 1183.png")
                                          : Image.asset(
                                              "assets/images/Ellipse 1181.png")),
                                if (widget.rightHand.isScanned == true)
                                  Positioned(
                                      top: 6,
                                      right: 6,
                                      child: (widget.rightHand.exceptions
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
                                widget.biometricAttribute = "Left Hand";
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
                                            color: (widget.leftHand.isScanned ==
                                                    true)
                                                ? (widget.leftHand.exceptions
                                                        .contains(true))
                                                    ? secondaryColors
                                                        .elementAt(16)
                                                    : secondaryColors
                                                        .elementAt(11)
                                                : (widget.biometricAttribute ==
                                                        "Left Hand")
                                                    ? secondaryColors
                                                        .elementAt(12)
                                                    : secondaryColors
                                                        .elementAt(14)),
                                        borderRadius:
                                            BorderRadius.circular(10)),
                                    child: (widget.leftHand.isScanned == true)
                                        ? Row(
                                            mainAxisAlignment:
                                                MainAxisAlignment.center,
                                            children: [
                                              ...widget.leftHand.listofImages
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
                                if (widget.leftHand.isScanned == true)
                                  Positioned(
                                      top: 14,
                                      left: 14,
                                      child: (widget.leftHand.exceptions
                                              .contains(true))
                                          ? Image.asset(
                                              "assets/images/Ellipse 1183.png")
                                          : Image.asset(
                                              "assets/images/Ellipse 1181.png")),
                                if (widget.leftHand.isScanned == true)
                                  Positioned(
                                      top: 6,
                                      right: 6,
                                      child: (widget.leftHand.exceptions
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
                                widget.biometricAttribute = "Thumbs";
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
                                            color: (widget.thumbs.isScanned ==
                                                    true)
                                                ? (widget.thumbs.exceptions
                                                        .contains(true))
                                                    ? secondaryColors
                                                        .elementAt(16)
                                                    : secondaryColors
                                                        .elementAt(11)
                                                : (widget.biometricAttribute ==
                                                        "Thumbs")
                                                    ? secondaryColors
                                                        .elementAt(12)
                                                    : secondaryColors
                                                        .elementAt(14)),
                                        borderRadius:
                                            BorderRadius.circular(10)),
                                    child: (widget.thumbs.isScanned == true)
                                        ? Row(
                                            mainAxisAlignment:
                                                MainAxisAlignment.center,
                                            children: [
                                              ...widget.thumbs.listofImages
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
                                if (widget.thumbs.isScanned == true)
                                  Positioned(
                                      top: 14,
                                      left: 14,
                                      child: (widget.thumbs.exceptions
                                              .contains(true))
                                          ? Image.asset(
                                              "assets/images/Ellipse 1183.png")
                                          : Image.asset(
                                              "assets/images/Ellipse 1181.png")),
                                if (widget.thumbs.isScanned == true)
                                  Positioned(
                                      top: 6,
                                      right: 6,
                                      child: (widget.thumbs.exceptions
                                              .contains(true))
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
                                widget.biometricAttribute = "Face";
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
                                            color: (widget.face.isScanned ==
                                                    true)
                                                ? (widget.face.exceptions
                                                        .contains(true))
                                                    ? secondaryColors
                                                        .elementAt(16)
                                                    : secondaryColors
                                                        .elementAt(11)
                                                : (widget.biometricAttribute ==
                                                        "Face")
                                                    ? secondaryColors
                                                        .elementAt(12)
                                                    : secondaryColors
                                                        .elementAt(14)),
                                        borderRadius:
                                            BorderRadius.circular(10)),
                                    child: (widget.face.isScanned == true)
                                        ? Row(
                                            mainAxisAlignment:
                                                MainAxisAlignment.center,
                                            children: [
                                              ...widget.face.listofImages
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
                                if (widget.face.isScanned == true)
                                  Positioned(
                                      top: 14,
                                      left: 14,
                                      child: (widget.face.exceptions
                                              .contains(true))
                                          ? Image.asset(
                                              "assets/images/Ellipse 1183.png")
                                          : Image.asset(
                                              "assets/images/Ellipse 1181.png")),
                                if (widget.face.isScanned == true)
                                  Positioned(
                                      top: 6,
                                      right: 6,
                                      child: (widget.face.exceptions
                                              .contains(true))
                                          ? Image.asset(
                                              "assets/images/Group 57548.png")
                                          : Image.asset(
                                              "assets/images/Group 57745.png")),
                              ],
                            )),
                      if (widget.iris.exceptions.contains(true) ||
                          widget.rightHand.exceptions.contains(true) ||
                          widget.leftHand.exceptions.contains(true) ||
                          widget.thumbs.exceptions.contains(true) ||
                          widget.face.exceptions.contains(true))
                        InkWell(
                            onTap: () {
                              setState(() {
                                widget.biometricAttribute = "Exception";
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
                                            color: (widget
                                                        .exception.isScanned ==
                                                    true)
                                                ? (widget.exception.exceptions
                                                        .contains(true))
                                                    ? secondaryColors
                                                        .elementAt(16)
                                                    : secondaryColors
                                                        .elementAt(11)
                                                : (widget.biometricAttribute ==
                                                        "Exception")
                                                    ? secondaryColors
                                                        .elementAt(12)
                                                    : secondaryColors
                                                        .elementAt(14)),
                                        borderRadius:
                                            BorderRadius.circular(10)),
                                    child: (widget.exception.isScanned == true)
                                        ? Row(
                                            mainAxisAlignment:
                                                MainAxisAlignment.center,
                                            children: [
                                              ...widget.exception.listofImages
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
                                if (widget.exception.isScanned == true)
                                  Positioned(
                                      top: 14,
                                      left: 14,
                                      child: (widget.exception.exceptions
                                              .contains(true))
                                          ? Image.asset(
                                              "assets/images/Ellipse 1183.png")
                                          : Image.asset(
                                              "assets/images/Ellipse 1181.png")),
                                if (widget.exception.isScanned == true)
                                  Positioned(
                                      top: 6,
                                      right: 6,
                                      child: (widget.exception.exceptions
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
                if (widget.biometricAttribute == "Iris")
                  BiometricCaptureScanBlock(
                    title: "IrisScan",
                    middleBlock: Container(
                      height: 460.h,
                      child: Column(
                        mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                        children: [
                          (widget.iris.listOfBiometricsDto.isEmpty)
                              ? Row(
                                  mainAxisAlignment:
                                      MainAxisAlignment.spaceEvenly,
                                  children: [
                                    ...listOfImages(widget.iris.listofImages)
                                        .map(
                                      (e) => e,
                                    ),
                                  ],
                                )
                              : Row(
                                  mainAxisAlignment:
                                      MainAxisAlignment.spaceEvenly,
                                  children: [
                                    ...listOfResultImages(
                                            widget.iris.listofImages)
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
                              widget.iris.attemptNo = await BiometricsApi()
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
                                                    widget.iris.attemptNo =
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
                                                  widget
                                                      .iris.listOfBiometricsDto
                                                      .clear();
                                                  await BiometricsApi()
                                                      .getBestBiometrics(
                                                          widget.field.id!,
                                                          "Iris")
                                                      .then((value) async {
                                                    for (var e in value) {
                                                      widget.iris
                                                          .listOfBiometricsDto
                                                          .add(BiometricsDto
                                                              .fromJson(json
                                                                  .decode(e!)));
                                                    }
                                                  });
                                                  widget.iris
                                                          .qualityPercentage =
                                                      avgScore(widget.iris
                                                          .listOfBiometricsDto);
                                                  await BiometricsApi()
                                                      .extractImageValues(
                                                          widget.field.id!,
                                                          "Iris")
                                                      .then((value) {
                                                    widget.iris.listofImages =
                                                        value;
                                                  });
                                                  widget.iris.isScanned = true;
                                                  generateList(
                                                      context,
                                                      "${widget.field.id}",
                                                      widget.iris);

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
                                      Text("${widget.iris.attemptNo}"),
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
                                          "${noOfTrue(widget.iris.exceptions)}"),
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
                                  "Threshold ${widget.iris.thresholdPercentage}%",
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
                                          widget.iris.qualityPercentage / 100,
                                      backgroundColor: Colors.grey,
                                      progressColor:
                                          secondaryColors.elementAt(11),
                                    ),
                                    SizedBox(
                                      width: 16.h,
                                    ),
                                    Text(
                                      "${widget.iris.qualityPercentage.toInt()}%",
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
                                        if (widget.iris.attemptNo >= 1) {
                                          await BiometricsApi()
                                              .getBiometrics(
                                                  widget.field.id!, "Iris", 1)
                                              .then((value) {
                                            widget.iris.listOfBiometricsDto
                                                .clear();
                                            for (var e in value) {
                                              widget.iris.listOfBiometricsDto
                                                  .add(BiometricsDto.fromJson(
                                                      json.decode(e!)));
                                            }
                                          });

                                          setState(() {
                                            widget.iris.qualityPercentage =
                                                avgScore(widget
                                                    .iris.listOfBiometricsDto);
                                          });
                                          await BiometricsApi()
                                              .extractImageValuesByAttempt(
                                                  widget.field.id!, "Iris", 1)
                                              .then((value) {
                                            widget.iris.listofImages = value;
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
                                          color: (widget.iris.attemptNo < 1)
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
                                                      (widget.iris.attemptNo <
                                                              1)
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
                                        if (widget.iris.attemptNo >= 2) {
                                          await BiometricsApi()
                                              .getBiometrics(
                                                  widget.field.id!, "Iris", 2)
                                              .then((value) {
                                            widget.iris.listOfBiometricsDto
                                                .clear();
                                            for (var e in value) {
                                              widget.iris.listOfBiometricsDto
                                                  .add(BiometricsDto.fromJson(
                                                      json.decode(e!)));
                                            }
                                          });
                                          setState(() {
                                            widget.iris.qualityPercentage =
                                                avgScore(widget
                                                    .iris.listOfBiometricsDto);
                                          });
                                          await BiometricsApi()
                                              .extractImageValuesByAttempt(
                                                  widget.field.id!, "Iris", 2)
                                              .then((value) {
                                            widget.iris.listofImages = value;
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
                                          color: (widget.iris.attemptNo < 2)
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
                                                      (widget.iris.attemptNo <
                                                              2)
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
                                        if (widget.iris.attemptNo >= 3) {
                                          await BiometricsApi()
                                              .getBiometrics(
                                                  widget.field.id!, "Iris", 3)
                                              .then((value) {
                                            widget.iris.listOfBiometricsDto
                                                .clear();
                                            for (var e in value) {
                                              widget.iris.listOfBiometricsDto
                                                  .add(BiometricsDto.fromJson(
                                                      json.decode(e!)));
                                            }
                                          });
                                          setState(() {
                                            widget.iris.qualityPercentage =
                                                avgScore(widget
                                                    .iris.listOfBiometricsDto);
                                          });
                                          await BiometricsApi()
                                              .extractImageValuesByAttempt(
                                                  widget.field.id!, "Iris", 3)
                                              .then((value) {
                                            widget.iris.listofImages = value;
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
                                          color: (widget.iris.attemptNo < 3)
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
                                                      (widget.iris.attemptNo <
                                                              3)
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
                if (widget.biometricAttribute == "Right Hand")
                  BiometricCaptureScanBlock(
                    title: "RightHandScan",
                    middleBlock: Container(
                      height: 460.h,
                      child: Column(
                        mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                        children: [
                          (widget.rightHand.listOfBiometricsDto.isEmpty)
                              ? Row(
                                  mainAxisAlignment:
                                      MainAxisAlignment.spaceEvenly,
                                  children: [
                                    ...listOfImages(
                                            widget.rightHand.listofImages)
                                        .map(
                                      (e) => e,
                                    ),
                                  ],
                                )
                              : Row(
                                  mainAxisAlignment:
                                      MainAxisAlignment.spaceEvenly,
                                  children: [
                                    ...listOfResultImages(
                                            widget.rightHand.listofImages)
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
                              widget.rightHand.attemptNo = await BiometricsApi()
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
                                                    widget.rightHand.attemptNo =
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
                                                  widget
                                                      .rightHand
                                                      .listOfBiometricsDto
                                                      .clear;
                                                  await BiometricsApi()
                                                      .getBestBiometrics(
                                                          widget.field.id!,
                                                          "RightHand")
                                                      .then((value) async {
                                                    for (var e in value) {
                                                      widget.rightHand
                                                          .listOfBiometricsDto
                                                          .add(BiometricsDto
                                                              .fromJson(json
                                                                  .decode(e!)));
                                                    }
                                                  });
                                                  widget.rightHand
                                                          .qualityPercentage =
                                                      avgScore(widget.rightHand
                                                          .listOfBiometricsDto);
                                                  await BiometricsApi()
                                                      .extractImageValues(
                                                          widget.field.id!,
                                                          "RightHand")
                                                      .then((value) {
                                                    widget.rightHand
                                                        .listofImages = value;
                                                  });
                                                  widget.rightHand.isScanned =
                                                      true;
                                                  generateList(
                                                      context,
                                                      "${widget.field.id}",
                                                      widget.rightHand);
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
                                      Text("${widget.rightHand.attemptNo}"),
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
                                          "${noOfTrue(widget.rightHand.exceptions)}"),
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
                                  "Threshold ${widget.rightHand.thresholdPercentage}%",
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
                                          widget.rightHand.qualityPercentage /
                                              100,
                                      backgroundColor: Colors.grey,
                                      progressColor:
                                          secondaryColors.elementAt(11),
                                    ),
                                    SizedBox(
                                      width: 16.h,
                                    ),
                                    Text(
                                      "${widget.rightHand.qualityPercentage.toInt()}%",
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
                                        if (widget.rightHand.attemptNo >= 1) {
                                          await BiometricsApi()
                                              .getBiometrics(widget.field.id!,
                                                  "RightHand", 1)
                                              .then((value) {
                                            widget.rightHand.listOfBiometricsDto
                                                .clear();
                                            for (var e in value) {
                                              widget
                                                  .rightHand.listOfBiometricsDto
                                                  .add(BiometricsDto.fromJson(
                                                      json.decode(e!)));
                                            }
                                          });

                                          setState(() {
                                            widget.rightHand.qualityPercentage =
                                                avgScore(widget.rightHand
                                                    .listOfBiometricsDto);
                                          });
                                          await BiometricsApi()
                                              .extractImageValuesByAttempt(
                                                  widget.field.id!,
                                                  "RightHand",
                                                  1)
                                              .then((value) {
                                            widget.rightHand.listofImages =
                                                value;
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
                                          color: (widget.rightHand.attemptNo <
                                                  1)
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
                                                  color: (widget.rightHand
                                                              .attemptNo <
                                                          1)
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
                                        if (widget.rightHand.attemptNo >= 2) {
                                          await BiometricsApi()
                                              .getBiometrics(widget.field.id!,
                                                  "RightHand", 2)
                                              .then((value) {
                                            widget.rightHand.listOfBiometricsDto
                                                .clear();
                                            for (var e in value) {
                                              widget
                                                  .rightHand.listOfBiometricsDto
                                                  .add(BiometricsDto.fromJson(
                                                      json.decode(e!)));
                                            }
                                          });
                                          setState(() {
                                            widget.rightHand.qualityPercentage =
                                                avgScore(widget.rightHand
                                                    .listOfBiometricsDto);
                                          });
                                          await BiometricsApi()
                                              .extractImageValuesByAttempt(
                                                  widget.field.id!,
                                                  "RightHand",
                                                  2)
                                              .then((value) {
                                            widget.rightHand.listofImages =
                                                value;
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
                                          color: (widget.rightHand.attemptNo <
                                                  2)
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
                                                  color: (widget.rightHand
                                                              .attemptNo <
                                                          2)
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
                                        if (widget.rightHand.attemptNo >= 3) {
                                          await BiometricsApi()
                                              .getBiometrics(widget.field.id!,
                                                  "RightHand", 3)
                                              .then((value) {
                                            widget.rightHand.listOfBiometricsDto
                                                .clear();
                                            for (var e in value) {
                                              widget
                                                  .rightHand.listOfBiometricsDto
                                                  .add(BiometricsDto.fromJson(
                                                      json.decode(e!)));
                                            }
                                          });
                                          setState(() {
                                            widget.rightHand.qualityPercentage =
                                                avgScore(widget.rightHand
                                                    .listOfBiometricsDto);
                                          });
                                          await BiometricsApi()
                                              .extractImageValuesByAttempt(
                                                  widget.field.id!,
                                                  "RightHand",
                                                  3)
                                              .then((value) {
                                            widget.rightHand.listofImages =
                                                value;
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
                                          color: (widget.rightHand.attemptNo <
                                                  3)
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
                                                  color: (widget.rightHand
                                                              .attemptNo <
                                                          3)
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
                if (widget.biometricAttribute == "Left Hand")
                  BiometricCaptureScanBlock(
                    title: "LeftHandScan",
                    middleBlock: Container(
                      height: 460.h,
                      child: Column(
                        mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                        children: [
                          (widget.leftHand.listOfBiometricsDto.isEmpty)
                              ? Row(
                                  mainAxisAlignment:
                                      MainAxisAlignment.spaceEvenly,
                                  children: [
                                    ...listOfImages(
                                            widget.leftHand.listofImages)
                                        .map(
                                      (e) => e,
                                    ),
                                  ],
                                )
                              : Row(
                                  mainAxisAlignment:
                                      MainAxisAlignment.spaceEvenly,
                                  children: [
                                    ...listOfResultImages(
                                            widget.leftHand.listofImages)
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
                              widget.leftHand.attemptNo = await BiometricsApi()
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
                                                    widget.leftHand.attemptNo =
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
                                                  widget
                                                      .leftHand
                                                      .listOfBiometricsDto
                                                      .clear;
                                                  await BiometricsApi()
                                                      .getBestBiometrics(
                                                          widget.field.id!,
                                                          "LeftHand")
                                                      .then((value) async {
                                                    for (var e in value) {
                                                      widget.leftHand
                                                          .listOfBiometricsDto
                                                          .add(BiometricsDto
                                                              .fromJson(json
                                                                  .decode(e!)));
                                                    }
                                                  });
                                                  widget.leftHand
                                                          .qualityPercentage =
                                                      avgScore(widget.leftHand
                                                          .listOfBiometricsDto);
                                                  await BiometricsApi()
                                                      .extractImageValues(
                                                          widget.field.id!,
                                                          "LeftHand")
                                                      .then((value) {
                                                    widget.leftHand
                                                        .listofImages = value;
                                                  });
                                                  widget.leftHand.isScanned =
                                                      true;
                                                  generateList(
                                                      context,
                                                      "${widget.field.id}",
                                                      widget.leftHand);
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
                                      Text("${widget.leftHand.attemptNo}"),
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
                                          "${noOfTrue(widget.leftHand.exceptions)}"),
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
                                  "Threshold ${widget.leftHand.thresholdPercentage}%",
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
                                          widget.leftHand.qualityPercentage /
                                              100,
                                      backgroundColor: Colors.grey,
                                      progressColor:
                                          secondaryColors.elementAt(11),
                                    ),
                                    SizedBox(
                                      width: 16.h,
                                    ),
                                    Text(
                                      "${widget.leftHand.qualityPercentage.toInt()}%",
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
                                        if (widget.leftHand.attemptNo >= 1) {
                                          await BiometricsApi()
                                              .getBiometrics(widget.field.id!,
                                                  "LeftHand", 1)
                                              .then((value) {
                                            widget.leftHand.listOfBiometricsDto
                                                .clear();
                                            for (var e in value) {
                                              widget
                                                  .leftHand.listOfBiometricsDto
                                                  .add(BiometricsDto.fromJson(
                                                      json.decode(e!)));
                                            }
                                          });
                                          setState(() {
                                            widget.leftHand.qualityPercentage =
                                                avgScore(widget.leftHand
                                                    .listOfBiometricsDto);
                                          });
                                          await BiometricsApi()
                                              .extractImageValuesByAttempt(
                                                  widget.field.id!,
                                                  "LeftHand",
                                                  1)
                                              .then((value) {
                                            widget.leftHand.listofImages =
                                                value;
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
                                          color: (widget.leftHand.attemptNo < 1)
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
                                                  color: (widget.leftHand
                                                              .attemptNo <
                                                          1)
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
                                        if (widget.leftHand.attemptNo >= 2) {
                                          await BiometricsApi()
                                              .getBiometrics(widget.field.id!,
                                                  "LeftHand", 2)
                                              .then((value) {
                                            widget.leftHand.listOfBiometricsDto
                                                .clear();
                                            for (var e in value) {
                                              widget
                                                  .leftHand.listOfBiometricsDto
                                                  .add(BiometricsDto.fromJson(
                                                      json.decode(e!)));
                                            }
                                          });
                                          setState(() {
                                            widget.leftHand.qualityPercentage =
                                                avgScore(widget.leftHand
                                                    .listOfBiometricsDto);
                                          });
                                          await BiometricsApi()
                                              .extractImageValuesByAttempt(
                                                  widget.field.id!,
                                                  "LeftHand",
                                                  2)
                                              .then((value) {
                                            widget.leftHand.listofImages =
                                                value;
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
                                          color: (widget.leftHand.attemptNo < 2)
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
                                                  color: (widget.leftHand
                                                              .attemptNo <
                                                          2)
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
                                        if (widget.leftHand.attemptNo >= 3) {
                                          await BiometricsApi()
                                              .getBiometrics(widget.field.id!,
                                                  "LeftHand", 3)
                                              .then((value) {
                                            widget.leftHand.listOfBiometricsDto
                                                .clear();
                                            for (var e in value) {
                                              widget
                                                  .leftHand.listOfBiometricsDto
                                                  .add(BiometricsDto.fromJson(
                                                      json.decode(e!)));
                                            }
                                          });
                                          setState(() {
                                            widget.leftHand.qualityPercentage =
                                                avgScore(widget.leftHand
                                                    .listOfBiometricsDto);
                                          });
                                          await BiometricsApi()
                                              .extractImageValuesByAttempt(
                                                  widget.field.id!,
                                                  "LeftHand",
                                                  3)
                                              .then((value) {
                                            widget.leftHand.listofImages =
                                                value;
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
                                          color: (widget.leftHand.attemptNo < 3)
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
                                                  color: (widget.leftHand
                                                              .attemptNo <
                                                          3)
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
                if (widget.biometricAttribute == "Thumbs")
                  BiometricCaptureScanBlock(
                    title: "ThumbsScan",
                    middleBlock: Container(
                      height: 460.h,
                      child: Column(
                        mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                        children: [
                          (widget.thumbs.listOfBiometricsDto.isEmpty)
                              ? Row(
                                  mainAxisAlignment:
                                      MainAxisAlignment.spaceEvenly,
                                  children: [
                                    ...listOfImages(widget.thumbs.listofImages)
                                        .map(
                                      (e) => e,
                                    ),
                                  ],
                                )
                              : Row(
                                  mainAxisAlignment:
                                      MainAxisAlignment.spaceEvenly,
                                  children: [
                                    ...listOfResultImages(
                                            widget.thumbs.listofImages)
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
                              widget.thumbs.attemptNo = await BiometricsApi()
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
                                                    widget.thumbs.attemptNo =
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
                                                  widget
                                                      .thumbs
                                                      .listOfBiometricsDto
                                                      .clear;
                                                  await BiometricsApi()
                                                      .getBestBiometrics(
                                                          widget.field.id!,
                                                          "Thumbs")
                                                      .then((value) async {
                                                    for (var e in value) {
                                                      widget.thumbs
                                                          .listOfBiometricsDto
                                                          .add(BiometricsDto
                                                              .fromJson(json
                                                                  .decode(e!)));
                                                    }
                                                  });
                                                  widget.thumbs
                                                          .qualityPercentage =
                                                      avgScore(widget.thumbs
                                                          .listOfBiometricsDto);
                                                  await BiometricsApi()
                                                      .extractImageValues(
                                                          widget.field.id!,
                                                          "Thumbs")
                                                      .then((value) {
                                                    widget.thumbs.listofImages =
                                                        value;
                                                  });
                                                  widget.thumbs.isScanned =
                                                      true;
                                                  generateList(
                                                      context,
                                                      "${widget.field.id}",
                                                      widget.thumbs);
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
                                      Text("${widget.thumbs.attemptNo}"),
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
                                          "${noOfTrue(widget.thumbs.exceptions)}"),
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
                                  "Threshold ${widget.thumbs.thresholdPercentage}%",
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
                                          widget.thumbs.qualityPercentage / 100,
                                      backgroundColor: Colors.grey,
                                      progressColor:
                                          secondaryColors.elementAt(11),
                                    ),
                                    SizedBox(
                                      width: 16.h,
                                    ),
                                    Text(
                                      "${widget.thumbs.qualityPercentage.toInt()}%",
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
                                        if (widget.thumbs.attemptNo >= 1) {
                                          await BiometricsApi()
                                              .getBiometrics(
                                                  widget.field.id!, "Thumbs", 1)
                                              .then((value) {
                                            widget.thumbs.listOfBiometricsDto
                                                .clear();
                                            for (var e in value) {
                                              widget.thumbs.listOfBiometricsDto
                                                  .add(BiometricsDto.fromJson(
                                                      json.decode(e!)));
                                            }
                                          });
                                          setState(() {
                                            widget.thumbs.qualityPercentage =
                                                avgScore(widget.thumbs
                                                    .listOfBiometricsDto);
                                          });
                                          await BiometricsApi()
                                              .extractImageValuesByAttempt(
                                                  widget.field.id!, "Thumbs", 1)
                                              .then((value) {
                                            widget.thumbs.listofImages = value;
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
                                          color: (widget.thumbs.attemptNo < 1)
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
                                                      (widget.thumbs.attemptNo <
                                                              1)
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
                                        if (widget.thumbs.attemptNo >= 2) {
                                          await BiometricsApi()
                                              .getBiometrics(
                                                  widget.field.id!, "Thumbs", 2)
                                              .then((value) {
                                            widget.thumbs.listOfBiometricsDto
                                                .clear();
                                            for (var e in value) {
                                              widget.thumbs.listOfBiometricsDto
                                                  .add(BiometricsDto.fromJson(
                                                      json.decode(e!)));
                                            }
                                          });
                                          setState(() {
                                            widget.thumbs.qualityPercentage =
                                                avgScore(widget.thumbs
                                                    .listOfBiometricsDto);
                                          });
                                          await BiometricsApi()
                                              .extractImageValuesByAttempt(
                                                  widget.field.id!, "Thumbs", 2)
                                              .then((value) {
                                            widget.thumbs.listofImages = value;
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
                                          color: (widget.thumbs.attemptNo < 2)
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
                                                      (widget.thumbs.attemptNo <
                                                              2)
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
                                        if (widget.thumbs.attemptNo >= 3) {
                                          await BiometricsApi()
                                              .getBiometrics(
                                                  widget.field.id!, "Thumbs", 3)
                                              .then((value) {
                                            widget.thumbs.listOfBiometricsDto
                                                .clear();
                                            for (var e in value) {
                                              widget.thumbs.listOfBiometricsDto
                                                  .add(BiometricsDto.fromJson(
                                                      json.decode(e!)));
                                            }
                                          });
                                          setState(() {
                                            widget.thumbs.qualityPercentage =
                                                avgScore(widget.thumbs
                                                    .listOfBiometricsDto);
                                          });
                                          await BiometricsApi()
                                              .extractImageValuesByAttempt(
                                                  widget.field.id!, "Thumbs", 3)
                                              .then((value) {
                                            widget.thumbs.listofImages = value;
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
                                          color: (widget.thumbs.attemptNo < 3)
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
                                                      (widget.thumbs.attemptNo <
                                                              3)
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
                if (widget.biometricAttribute == "Face")
                  BiometricCaptureScanBlock(
                    title: "FaceScan",
                    middleBlock: Container(
                      height: 460.h,
                      child: Column(
                        mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                        children: [
                          (widget.face.listOfBiometricsDto.isEmpty)
                              ? Row(
                                  mainAxisAlignment:
                                      MainAxisAlignment.spaceEvenly,
                                  children: [
                                    ...listOfImages(widget.face.listofImages)
                                        .map(
                                      (e) => e,
                                    ),
                                  ],
                                )
                              : Row(
                                  mainAxisAlignment:
                                      MainAxisAlignment.spaceEvenly,
                                  children: [
                                    ...listOfResultImages(
                                            widget.face.listofImages)
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
                              widget.face.attemptNo = await BiometricsApi()
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
                                                    widget.face.attemptNo =
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
                                                  widget
                                                      .face
                                                      .listOfBiometricsDto
                                                      .clear;
                                                  await BiometricsApi()
                                                      .getBestBiometrics(
                                                          widget.field.id!,
                                                          "Face")
                                                      .then((value) async {
                                                    for (var e in value) {
                                                      widget.face
                                                          .listOfBiometricsDto
                                                          .add(BiometricsDto
                                                              .fromJson(json
                                                                  .decode(e!)));
                                                    }
                                                  });
                                                  widget.face
                                                          .qualityPercentage =
                                                      avgScore(widget.face
                                                          .listOfBiometricsDto);
                                                  await BiometricsApi()
                                                      .extractImageValues(
                                                          widget.field.id!,
                                                          "Face")
                                                      .then((value) {
                                                    widget.face.listofImages =
                                                        value;
                                                  });
                                                  widget.face.isScanned = true;
                                                  generateList(
                                                      context,
                                                      "${widget.field.id}",
                                                      widget.face);
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
                                        "${widget.face.attemptNo}",
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
                                  "Threshold ${widget.face.thresholdPercentage}%",
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
                                          widget.face.qualityPercentage / 100,
                                      backgroundColor: Colors.grey,
                                      progressColor:
                                          secondaryColors.elementAt(11),
                                    ),
                                    SizedBox(
                                      width: 16.h,
                                    ),
                                    Text(
                                      "${widget.face.qualityPercentage.toInt()}%",
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
                                        if (widget.face.attemptNo >= 1) {
                                          await BiometricsApi()
                                              .getBiometrics(
                                                  widget.field.id!, "Face", 1)
                                              .then((value) {
                                            widget.face.listOfBiometricsDto
                                                .clear();
                                            for (var e in value) {
                                              widget.face.listOfBiometricsDto
                                                  .add(BiometricsDto.fromJson(
                                                      json.decode(e!)));
                                            }
                                          });
                                          setState(() {
                                            widget.face.qualityPercentage =
                                                avgScore(widget
                                                    .face.listOfBiometricsDto);
                                          });
                                          await BiometricsApi()
                                              .extractImageValuesByAttempt(
                                                  widget.field.id!, "Face", 1)
                                              .then((value) {
                                            widget.face.listofImages = value;
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
                                          color: (widget.face.attemptNo < 1)
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
                                                      (widget.face.attemptNo <
                                                              1)
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
                                        if (widget.face.attemptNo >= 2) {
                                          await BiometricsApi()
                                              .getBiometrics(
                                                  widget.field.id!, "Face", 2)
                                              .then((value) {
                                            widget.face.listOfBiometricsDto
                                                .clear();
                                            for (var e in value) {
                                              widget.face.listOfBiometricsDto
                                                  .add(BiometricsDto.fromJson(
                                                      json.decode(e!)));
                                            }
                                          });
                                          setState(() {
                                            widget.face.qualityPercentage =
                                                avgScore(widget
                                                    .face.listOfBiometricsDto);
                                          });
                                          await BiometricsApi()
                                              .extractImageValuesByAttempt(
                                                  widget.field.id!, "Face", 2)
                                              .then((value) {
                                            widget.face.listofImages = value;
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
                                          color: (widget.face.attemptNo < 2)
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
                                                      (widget.face.attemptNo <
                                                              2)
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
                                        if (widget.face.attemptNo >= 3) {
                                          await BiometricsApi()
                                              .getBiometrics(
                                                  widget.field.id!, "Face", 3)
                                              .then((value) {
                                            widget.face.listOfBiometricsDto
                                                .clear();
                                            for (var e in value) {
                                              widget.face.listOfBiometricsDto
                                                  .add(BiometricsDto.fromJson(
                                                      json.decode(e!)));
                                            }
                                          });
                                          setState(() {
                                            widget.face.qualityPercentage =
                                                avgScore(widget
                                                    .face.listOfBiometricsDto);
                                          });
                                          await BiometricsApi()
                                              .extractImageValuesByAttempt(
                                                  widget.field.id!, "Face", 3)
                                              .then((value) {
                                            widget.face.listofImages = value;
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
                                          color: (widget.face.attemptNo < 3)
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
                                                      (widget.face.attemptNo <
                                                              3)
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
                if (widget.biometricAttribute == "Exception")
                  BiometricCaptureScanBlock(
                    title: "ExceptionScan",
                    middleBlock: Container(
                      height: 460.h,
                      child: Column(
                        mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                        children: [
                          (widget.exception.listOfBiometricsDto.isEmpty)
                              ? Row(
                                  mainAxisAlignment:
                                      MainAxisAlignment.spaceEvenly,
                                  children: [
                                    ...listOfImages(
                                            widget.exception.listofImages)
                                        .map(
                                      (e) => e,
                                    ),
                                  ],
                                )
                              : Row(
                                  mainAxisAlignment:
                                      MainAxisAlignment.spaceEvenly,
                                  children: [
                                    ...listOfResultImages(
                                            widget.exception.listofImages)
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
                              widget.exception.attemptNo = await BiometricsApi()
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
                                                    widget.exception.attemptNo =
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
                                                  widget
                                                      .exception
                                                      .listOfBiometricsDto
                                                      .clear;
                                                  await BiometricsApi()
                                                      .getBestBiometrics(
                                                          widget.field.id!,
                                                          "Exception")
                                                      .then((value) async {
                                                    for (var e in value) {
                                                      widget.exception
                                                          .listOfBiometricsDto
                                                          .add(BiometricsDto
                                                              .fromJson(json
                                                                  .decode(e!)));
                                                    }
                                                  });
                                                  widget.exception
                                                          .qualityPercentage =
                                                      avgScore(widget.exception
                                                          .listOfBiometricsDto);
                                                  await BiometricsApi()
                                                      .extractImageValues(
                                                          widget.field.id!,
                                                          "Exception")
                                                      .then((value) {
                                                    widget.exception
                                                        .listofImages = value;
                                                  });
                                                  widget.exception.isScanned =
                                                      true;
                                                  generateList(
                                                      context,
                                                      "${widget.field.id}",
                                                      widget.exception);
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
                                        "${widget.exception.attemptNo}",
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
                                          widget.exception.qualityPercentage /
                                              100,
                                      backgroundColor: Colors.grey,
                                      progressColor:
                                          secondaryColors.elementAt(11),
                                    ),
                                    SizedBox(
                                      width: 16.h,
                                    ),
                                    Text(
                                      "${widget.exception.qualityPercentage.toInt()}%",
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
                                        if (widget.exception.attemptNo >= 1) {
                                          await BiometricsApi()
                                              .getBiometrics(widget.field.id!,
                                                  "Exception", 1)
                                              .then((value) {
                                            widget.exception.listOfBiometricsDto
                                                .clear();
                                            for (var e in value) {
                                              widget
                                                  .exception.listOfBiometricsDto
                                                  .add(BiometricsDto.fromJson(
                                                      json.decode(e!)));
                                            }
                                          });
                                          setState(() {
                                            widget.exception.qualityPercentage =
                                                avgScore(widget.exception
                                                    .listOfBiometricsDto);
                                          });
                                          await BiometricsApi()
                                              .extractImageValuesByAttempt(
                                                  widget.field.id!,
                                                  "Exception",
                                                  1)
                                              .then((value) {
                                            widget.exception.listofImages =
                                                value;
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
                                          color: (widget.exception.attemptNo <
                                                  1)
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
                                                  color: (widget.exception
                                                              .attemptNo <
                                                          1)
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
                                        if (widget.exception.attemptNo >= 2) {
                                          await BiometricsApi()
                                              .getBiometrics(widget.field.id!,
                                                  "Exception", 2)
                                              .then((value) {
                                            widget.exception.listOfBiometricsDto
                                                .clear();
                                            for (var e in value) {
                                              widget
                                                  .exception.listOfBiometricsDto
                                                  .add(BiometricsDto.fromJson(
                                                      json.decode(e!)));
                                            }
                                          });
                                          setState(() {
                                            widget.exception.qualityPercentage =
                                                avgScore(widget.exception
                                                    .listOfBiometricsDto);
                                          });
                                          await BiometricsApi()
                                              .extractImageValuesByAttempt(
                                                  widget.field.id!,
                                                  "Exception",
                                                  2)
                                              .then((value) {
                                            widget.exception.listofImages =
                                                value;
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
                                          color: (widget.exception.attemptNo <
                                                  2)
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
                                                  color: (widget.exception
                                                              .attemptNo <
                                                          2)
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
                                        if (widget.exception.attemptNo >= 3) {
                                          await BiometricsApi()
                                              .getBiometrics(widget.field.id!,
                                                  "Exception", 3)
                                              .then((value) {
                                            widget.exception.listOfBiometricsDto
                                                .clear();
                                            for (var e in value) {
                                              widget
                                                  .exception.listOfBiometricsDto
                                                  .add(BiometricsDto.fromJson(
                                                      json.decode(e!)));
                                            }
                                          });
                                          setState(() {
                                            widget.exception.qualityPercentage =
                                                avgScore(widget.exception
                                                    .listOfBiometricsDto);
                                          });
                                          await BiometricsApi()
                                              .extractImageValuesByAttempt(
                                                  widget.field.id!,
                                                  "Exception",
                                                  3)
                                              .then((value) {
                                            widget.exception.listofImages =
                                                value;
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
                                          color: (widget.exception.attemptNo <
                                                  3)
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
                                                  color: (widget.exception
                                                              .attemptNo <
                                                          3)
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
                if (widget.biometricAttribute == "Iris")
                  BiometricCaptureExceptionBlock(
                    attribute: widget.iris,
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
                                        if (!(widget.iris.exceptions
                                                .elementAt(0)) ==
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
                                          widget.iris.exceptions[0] = !(widget
                                              .iris.exceptions
                                              .elementAt(0));

                                          if (widget.iris.exceptions
                                              .contains(true)) {
                                            if (widget
                                                .iris.exceptionType.isEmpty) {
                                              widget.iris.exceptionType =
                                                  "Permanent";
                                            }
                                          }
                                          if (!widget.iris.exceptions
                                              .contains(true)) {
                                            widget.iris.exceptionType = "";
                                          }
                                        });
                                      },
                                      child: Image.asset(
                                        "assets/images/Left Eye@2x.png",
                                        height: 72,
                                      ),
                                    ),
                                    (widget.iris.exceptions[0] == true)
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
                                        if (!(widget.iris.exceptions
                                                .elementAt(1)) ==
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
                                          widget.iris.exceptions[1] = !(widget
                                              .iris.exceptions
                                              .elementAt(1));
                                          if (widget.iris.exceptions
                                              .contains(true)) {
                                            if (widget
                                                .iris.exceptionType.isEmpty) {
                                              widget.iris.exceptionType =
                                                  "Permanent";
                                            }
                                          }
                                          if (!widget.iris.exceptions
                                              .contains(true)) {
                                            widget.iris.exceptionType = "";
                                          }
                                        });
                                      },
                                      child: Image.asset(
                                        "assets/images/Right Eye@2x.png",
                                        height: 72,
                                      ),
                                    ),
                                    (widget.iris.exceptions[1] == true)
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
                if (widget.biometricAttribute == "Right Hand")
                  BiometricCaptureExceptionBlock(
                    attribute: widget.rightHand,
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
                                          if (!(widget.rightHand.exceptions
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
                                            widget.rightHand.exceptions[0] =
                                                !(widget.rightHand.exceptions
                                                    .elementAt(0));
                                            if (widget.rightHand.exceptions
                                                .contains(true)) {
                                              if (widget.rightHand.exceptionType
                                                  .isEmpty) {
                                                widget.rightHand.exceptionType =
                                                    "Permanent";
                                              }
                                            }
                                            if (!widget.rightHand.exceptions
                                                .contains(true)) {
                                              widget.rightHand.exceptionType =
                                                  "";
                                            }
                                          });
                                        },
                                        child: Icon(
                                          Icons.cancel_rounded,
                                          color: (widget.rightHand
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
                                          if (!(widget.rightHand.exceptions
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
                                            widget.rightHand.exceptions[1] =
                                                !(widget.rightHand.exceptions
                                                    .elementAt(1));
                                            if (widget.rightHand.exceptions
                                                .contains(true)) {
                                              if (widget.rightHand.exceptionType
                                                  .isEmpty) {
                                                widget.rightHand.exceptionType =
                                                    "Permanent";
                                              }
                                            }
                                            if (!widget.rightHand.exceptions
                                                .contains(true)) {
                                              widget.rightHand.exceptionType =
                                                  "";
                                            }
                                          });
                                        },
                                        child: Icon(
                                          Icons.cancel_rounded,
                                          color: (widget.rightHand
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
                                          if (!(widget.rightHand.exceptions
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
                                            widget.rightHand.exceptions[2] =
                                                !(widget.rightHand.exceptions
                                                    .elementAt(2));
                                            if (widget.rightHand.exceptions
                                                .contains(true)) {
                                              if (widget.rightHand.exceptionType
                                                  .isEmpty) {
                                                widget.rightHand.exceptionType =
                                                    "Permanent";
                                              }
                                            }
                                            if (!widget.rightHand.exceptions
                                                .contains(true)) {
                                              widget.rightHand.exceptionType =
                                                  "";
                                            }
                                          });
                                        },
                                        child: Icon(
                                          Icons.cancel_rounded,
                                          color: (widget.rightHand
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
                                          if (!(widget.rightHand.exceptions
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
                                            widget.rightHand.exceptions[3] =
                                                !(widget.rightHand.exceptions
                                                    .elementAt(3));
                                            if (widget.rightHand.exceptions
                                                .contains(true)) {
                                              if (widget.rightHand.exceptionType
                                                  .isEmpty) {
                                                widget.rightHand.exceptionType =
                                                    "Permanent";
                                              }
                                            }
                                            if (!widget.rightHand.exceptions
                                                .contains(true)) {
                                              widget.rightHand.exceptionType =
                                                  "";
                                            }
                                          });
                                        },
                                        child: Icon(
                                          Icons.cancel_rounded,
                                          color: (widget.rightHand
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
                if (widget.biometricAttribute == "Left Hand")
                  BiometricCaptureExceptionBlock(
                    attribute: widget.leftHand,
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
                                          if (!(widget.leftHand.exceptions
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
                                            widget.leftHand.exceptions[0] =
                                                !(widget.leftHand.exceptions
                                                    .elementAt(0));
                                            if (widget.leftHand.exceptions
                                                .contains(true)) {
                                              if (widget.leftHand.exceptionType
                                                  .isEmpty) {
                                                widget.leftHand.exceptionType =
                                                    "Permanent";
                                              }
                                            }
                                            if (!widget.leftHand.exceptions
                                                .contains(true)) {
                                              widget.leftHand.exceptionType =
                                                  "";
                                            }
                                          });
                                        },
                                        child: Icon(
                                          Icons.cancel_rounded,
                                          color: (widget
                                                      .leftHand.exceptions[0] ==
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
                                          if (!(widget.leftHand.exceptions
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
                                            widget.leftHand.exceptions[1] =
                                                !(widget.leftHand.exceptions
                                                    .elementAt(1));
                                            if (widget.leftHand.exceptions
                                                .contains(true)) {
                                              if (widget.leftHand.exceptionType
                                                  .isEmpty) {
                                                widget.leftHand.exceptionType =
                                                    "Permanent";
                                              }
                                            }
                                            if (!widget.leftHand.exceptions
                                                .contains(true)) {
                                              widget.leftHand.exceptionType =
                                                  "";
                                            }
                                          });
                                        },
                                        child: Icon(
                                          Icons.cancel_rounded,
                                          color: (widget
                                                      .leftHand.exceptions[1] ==
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
                                          if (!(widget.leftHand.exceptions
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
                                            widget.leftHand.exceptions[2] =
                                                !(widget.leftHand.exceptions
                                                    .elementAt(2));
                                            if (widget.leftHand.exceptions
                                                .contains(true)) {
                                              if (widget.leftHand.exceptionType
                                                  .isEmpty) {
                                                widget.leftHand.exceptionType =
                                                    "Permanent";
                                              }
                                            }
                                            if (!widget.leftHand.exceptions
                                                .contains(true)) {
                                              widget.leftHand.exceptionType =
                                                  "";
                                            }
                                          });
                                        },
                                        child: Icon(
                                          Icons.cancel_rounded,
                                          color: (widget
                                                      .leftHand.exceptions[2] ==
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
                                          if (!(widget.leftHand.exceptions
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
                                            widget.leftHand.exceptions[3] =
                                                !(widget.leftHand.exceptions
                                                    .elementAt(3));
                                            if (widget.leftHand.exceptions
                                                .contains(true)) {
                                              if (widget.leftHand.exceptionType
                                                  .isEmpty) {
                                                widget.leftHand.exceptionType =
                                                    "Permanent";
                                              }
                                            }
                                            if (!widget.leftHand.exceptions
                                                .contains(true)) {
                                              widget.leftHand.exceptionType =
                                                  "";
                                            }
                                          });
                                        },
                                        child: Icon(
                                          Icons.cancel_rounded,
                                          color: (widget
                                                      .leftHand.exceptions[3] ==
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
                if (widget.biometricAttribute == "Thumbs")
                  BiometricCaptureExceptionBlock(
                    attribute: widget.thumbs,
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
                                          if (!(widget.thumbs.exceptions
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
                                            widget.thumbs.exceptions[0] =
                                                !(widget.thumbs.exceptions
                                                    .elementAt(0));
                                            if (widget.thumbs.exceptions
                                                .contains(true)) {
                                              if (widget.thumbs.exceptionType
                                                  .isEmpty) {
                                                widget.thumbs.exceptionType =
                                                    "Permanent";
                                              }
                                            }
                                            if (!widget.thumbs.exceptions
                                                .contains(true)) {
                                              widget.thumbs.exceptionType = "";
                                            }
                                          });
                                        },
                                        child: Icon(
                                          Icons.cancel_rounded,
                                          color: (widget.thumbs.exceptions[0] ==
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
                                          if (!(widget.thumbs.exceptions
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
                                            widget.thumbs.exceptions[1] =
                                                !(widget.thumbs.exceptions
                                                    .elementAt(1));
                                            if (widget.thumbs.exceptions
                                                .contains(true)) {
                                              if (widget.thumbs.exceptionType
                                                  .isEmpty) {
                                                widget.thumbs.exceptionType =
                                                    "Permanent";
                                              }
                                            }
                                            if (!widget.thumbs.exceptions
                                                .contains(true)) {
                                              widget.thumbs.exceptionType = "";
                                            }
                                          });
                                        },
                                        child: Icon(
                                          Icons.cancel_rounded,
                                          color: (widget.thumbs.exceptions[1] ==
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
                if (widget.biometricAttribute == "Face")
                  BiometricCaptureExceptionBlock(
                    attribute: widget.face,
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
                if (widget.biometricAttribute == "Exception")
                  BiometricCaptureExceptionBlock(
                    attribute: widget.exception,
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
