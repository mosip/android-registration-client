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
      exceptionType: "",
      exceptions: [false, false],
      isScanned: false,
      listofImages: [
        "assets/images/Left Eye@2x.png",
        "assets/images/Right Eye@2x.png"
      ],
      listOfBiometricsDto: [],
      qualityPercentage: 0);
  BiometricAttributeData rightHand = BiometricAttributeData(
      exceptionType: "",
      exceptions: [false, false, false, false],
      isScanned: false,
      listofImages: ["assets/images/Right Hand@2x.png"],
      listOfBiometricsDto: [],
      qualityPercentage: 0);
  BiometricAttributeData leftHand = BiometricAttributeData(
      exceptionType: "",
      exceptions: [false, false, false, false],
      isScanned: false,
      listofImages: ["assets/images/Left Hand@2x.png"],
      listOfBiometricsDto: [],
      qualityPercentage: 0);
  BiometricAttributeData thumbs = BiometricAttributeData(
      exceptionType: "",
      exceptions: [false, false],
      isScanned: false,
      listofImages: ["assets/images/Thumbs@2x.png"],
      listOfBiometricsDto: [],
      qualityPercentage: 0);
  BiometricAttributeData face = BiometricAttributeData(
      exceptionType: "",
      exceptions: [false],
      isScanned: false,
      listofImages: ["assets/images/Face@2x.png"],
      listOfBiometricsDto: [],
      qualityPercentage: 0);
  BiometricAttributeData exception = BiometricAttributeData(
      exceptionType: "",
      exceptions: [false],
      isScanned: false,
      listofImages: ["assets/images/Person@2x.png"],
      listOfBiometricsDto: [],
      qualityPercentage: 0);

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

  @override
  Widget build(BuildContext context) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        SizedBox(
          width: double.infinity,
        ),
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
                          onTap: () {
                            setState(() {
                              widget.biometricAttribute = "Iris";
                            });
                          },
                          child: Padding(
                            padding: const EdgeInsets.fromLTRB(10, 10, 10, 5),
                            child: Container(
                              height: 78.h,
                              width: 78.h,
                              decoration: BoxDecoration(
                                  border: Border.all(
                                      color: (widget.iris.isScanned == true)
                                          ? (widget.iris.exceptions
                                                  .contains(true))
                                              ? secondaryColors.elementAt(16)
                                              : secondaryColors.elementAt(11)
                                          : (widget.biometricAttribute ==
                                                  "Iris")
                                              ? secondaryColors.elementAt(12)
                                              : secondaryColors.elementAt(14)),
                                  borderRadius: BorderRadius.circular(10)),
                              child: Padding(
                                padding: const EdgeInsets.all(8.0),
                                child: Image.asset(
                                  "assets/images/Eye@2x.png",
                                  height: 18.h,
                                  width: 64.h,
                                ),
                              ),
                            ),
                          ),
                        ),
                      if (widget.field.bioAttributes!.contains("rightIndex") &&
                          widget.field.bioAttributes!.contains("rightLittle") &&
                          widget.field.bioAttributes!.contains("rightRing") &&
                          widget.field.bioAttributes!.contains("rightMiddle"))
                        InkWell(
                          onTap: () {
                            setState(() {
                              widget.biometricAttribute = "Right Hand";
                            });
                          },
                          child: Padding(
                            padding: const EdgeInsets.fromLTRB(10, 5, 10, 5),
                            child: Container(
                              height: 78.h,
                              width: 78.h,
                              decoration: BoxDecoration(
                                  border: Border.all(
                                      color: (widget.rightHand.isScanned == true)
                                          ? (widget.rightHand.exceptions
                                                  .contains(true))
                                              ? secondaryColors.elementAt(16)
                                              : secondaryColors.elementAt(11)
                                          : (widget.biometricAttribute ==
                                                  "Right Hand")
                                              ? secondaryColors.elementAt(12)
                                              : secondaryColors.elementAt(14)),
                                  borderRadius: BorderRadius.circular(10)),
                              child: Padding(
                                padding: const EdgeInsets.all(8.0),
                                child: Image.asset(
                                  "assets/images/Right Hand@2x.png",
                                  height: 18.h,
                                  width: 64.h,
                                ),
                              ),
                            ),
                          ),
                        ),
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
                          child: Padding(
                            padding: const EdgeInsets.fromLTRB(10, 5, 10, 5),
                            child: Container(
                              height: 78.h,
                              width: 78.h,
                              decoration: BoxDecoration(
                                  border: Border.all(
                                      color: (widget.leftHand.isScanned == true)
                                          ? (widget.leftHand.exceptions
                                                  .contains(true))
                                              ? secondaryColors.elementAt(16)
                                              : secondaryColors.elementAt(11)
                                          : (widget.biometricAttribute ==
                                                  "Left Hand")
                                              ? secondaryColors.elementAt(12)
                                              : secondaryColors.elementAt(14)),
                                  borderRadius: BorderRadius.circular(10)),
                              child: Padding(
                                padding: const EdgeInsets.all(8.0),
                                child: Image.asset(
                                  "assets/images/Left Hand@2x.png",
                                  height: 18.h,
                                  width: 64.h,
                                ),
                              ),
                            ),
                          ),
                        ),
                      if (widget.field.bioAttributes!.contains("leftThumb") &&
                          widget.field.bioAttributes!.contains("rightThumb"))
                        InkWell(
                          onTap: () {
                            setState(() {
                              widget.biometricAttribute = "Thumbs";
                            });
                          },
                          child: Padding(
                            padding: const EdgeInsets.fromLTRB(10, 5, 10, 5),
                            child: Container(
                              height: 78.h,
                              width: 78.h,
                              decoration: BoxDecoration(
                                  border: Border.all(
                                      color: (widget.thumbs.isScanned == true)
                                          ? (widget.thumbs.exceptions
                                                  .contains(true))
                                              ? secondaryColors.elementAt(16)
                                              : secondaryColors.elementAt(11)
                                          : (widget.biometricAttribute ==
                                                  "Thumbs")
                                              ? secondaryColors.elementAt(12)
                                              : secondaryColors.elementAt(14)),
                                  borderRadius: BorderRadius.circular(10)),
                              child: Padding(
                                padding: const EdgeInsets.all(8.0),
                                child: Image.asset(
                                  "assets/images/Thumbs@2x.png",
                                  height: 18.h,
                                  width: 64.h,
                                ),
                              ),
                            ),
                          ),
                        ),
                      if (widget.field.bioAttributes!.contains("face"))
                        InkWell(
                          onTap: () {
                            setState(() {
                              widget.biometricAttribute = "Face";
                            });
                          },
                          child: Padding(
                            padding: const EdgeInsets.fromLTRB(10, 5, 10, 5),
                            child: Container(
                              height: 78.h,
                              width: 78.h,
                              decoration: BoxDecoration(
                                  border: Border.all(
                                      color: (widget.face.isScanned == true)
                                          ? (widget.face.exceptions
                                                  .contains(true))
                                              ? secondaryColors.elementAt(16)
                                              : secondaryColors.elementAt(11)
                                          : (widget.biometricAttribute ==
                                                  "Face")
                                              ? secondaryColors.elementAt(12)
                                              : secondaryColors.elementAt(14)),
                                  borderRadius: BorderRadius.circular(10)),
                              child: Padding(
                                padding: const EdgeInsets.all(8.0),
                                child: Image.asset(
                                  "assets/images/Face@2x.png",
                                  height: 18.h,
                                  width: 64.h,
                                ),
                              ),
                            ),
                          ),
                        ),
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
                          child: Padding(
                            padding: const EdgeInsets.fromLTRB(10, 5, 10, 10),
                            child: Container(
                              height: 78.h,
                              width: 78.h,
                              decoration: BoxDecoration(
                                  border: Border.all(
                                      color: (widget.exception.isScanned == true)
                                          ? (widget.exception.exceptions
                                                  .contains(true))
                                              ? secondaryColors.elementAt(16)
                                              : secondaryColors.elementAt(11)
                                          : (widget.biometricAttribute ==
                                                  "Exception")
                                              ? secondaryColors.elementAt(12)
                                              : secondaryColors.elementAt(14)),
                                  borderRadius: BorderRadius.circular(10)),
                              child: Padding(
                                padding: const EdgeInsets.all(8.0),
                                child: Image.asset(
                                  "assets/images/Person@2x.png",
                                  height: 18.h,
                                  width: 64.h,
                                ),
                              ),
                            ),
                          ),
                        ),
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
                                  .getBestBiometrics(widget.field.id!, "Iris");
                              await BiometricsApi()
                                  .extractImageValues()
                                  .then((value) {
                                temp = value;
                              });
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
                                                        .extractImageValues()
                                                        .then((value) {
                                                      temp = value;
                                                    });
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
                                                      .extractImageValues()
                                                      .then((value) {
                                                    widget.iris.listofImages =
                                                        value;
                                                  });
                                                  widget.iris.isScanned = true;
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
                            height: 157.h,
                            width: 338.h,
                            decoration: BoxDecoration(
                              color: pure_white,
                              border: Border.all(
                                color: secondaryColors.elementAt(14),
                              ),
                            ),
                            child: Column(
                              children: [
                                SizedBox(
                                  height: 20.h,
                                ),
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
                                SizedBox(
                                  height: 42.h,
                                ),
                                Text(
                                  "Threshold ${widget.iris.qualityPercentage.toInt()}%",
                                  style: Theme.of(context)
                                      .textTheme
                                      .bodyLarge
                                      ?.copyWith(
                                        fontSize: 14,
                                        fontWeight: regular,
                                        color: secondaryColors.elementAt(1),
                                      ),
                                ),
                                SizedBox(
                                  height: 16.h,
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
                                  .extractImageValues()
                                  .then((value) {
                                temp = value;
                              });
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
                                                        .extractImageValues()
                                                        .then((value) {
                                                      temp = value;
                                                    });
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
                                                      .extractImageValues()
                                                      .then((value) {
                                                    widget.rightHand
                                                        .listofImages = value;
                                                  });
                                                  widget.rightHand.isScanned =
                                                      true;
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
                            height: 157.h,
                            width: 338.h,
                            decoration: BoxDecoration(
                              color: pure_white,
                              border: Border.all(
                                color: secondaryColors.elementAt(14),
                              ),
                            ),
                            child: Column(
                              children: [
                                SizedBox(
                                  height: 20.h,
                                ),
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
                                SizedBox(
                                  height: 42.h,
                                ),
                                Text(
                                  "Threshold ${widget.rightHand.qualityPercentage.toInt()}%",
                                  style: Theme.of(context)
                                      .textTheme
                                      .bodyLarge
                                      ?.copyWith(
                                        fontSize: 14,
                                        fontWeight: regular,
                                        color: secondaryColors.elementAt(1),
                                      ),
                                ),
                                SizedBox(
                                  height: 16.h,
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
                                  .extractImageValues()
                                  .then((value) {
                                temp = value;
                              });
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
                                                        .extractImageValues()
                                                        .then((value) {
                                                      temp = value;
                                                    });
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
                                                      .extractImageValues()
                                                      .then((value) {
                                                    widget.leftHand
                                                        .listofImages = value;
                                                  });
                                                  widget.leftHand.isScanned =
                                                      true;
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
                            height: 157.h,
                            width: 338.h,
                            decoration: BoxDecoration(
                              color: pure_white,
                              border: Border.all(
                                color: secondaryColors.elementAt(14),
                              ),
                            ),
                            child: Column(
                              children: [
                                SizedBox(
                                  height: 20.h,
                                ),
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
                                SizedBox(
                                  height: 42.h,
                                ),
                                Text(
                                  "Threshold ${widget.leftHand.qualityPercentage.toInt()}%",
                                  style: Theme.of(context)
                                      .textTheme
                                      .bodyLarge
                                      ?.copyWith(
                                        fontSize: 14,
                                        fontWeight: regular,
                                        color: secondaryColors.elementAt(1),
                                      ),
                                ),
                                SizedBox(
                                  height: 16.h,
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
                                  .extractImageValues()
                                  .then((value) {
                                temp = value;
                              });
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
                                                        .extractImageValues()
                                                        .then((value) {
                                                      temp = value;
                                                    });
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
                                                      .extractImageValues()
                                                      .then((value) {
                                                    widget.thumbs.listofImages =
                                                        value;
                                                  });
                                                  widget.thumbs.isScanned =
                                                      true;
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
                            height: 157.h,
                            width: 338.h,
                            decoration: BoxDecoration(
                              color: pure_white,
                              border: Border.all(
                                color: secondaryColors.elementAt(14),
                              ),
                            ),
                            child: Column(
                              children: [
                                SizedBox(
                                  height: 20.h,
                                ),
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
                                SizedBox(
                                  height: 42.h,
                                ),
                                Text(
                                  "Threshold ${widget.thumbs.qualityPercentage.toInt()}%",
                                  style: Theme.of(context)
                                      .textTheme
                                      .bodyLarge
                                      ?.copyWith(
                                        fontSize: 14,
                                        fontWeight: regular,
                                        color: secondaryColors.elementAt(1),
                                      ),
                                ),
                                SizedBox(
                                  height: 16.h,
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
                                  .extractImageValues()
                                  .then((value) {
                                temp = value;
                              });
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
                                                        .extractImageValues()
                                                        .then((value) {
                                                      temp = value;
                                                    });
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
                                                      .extractImageValues()
                                                      .then((value) {
                                                    widget.face.listofImages =
                                                        value;
                                                  });
                                                  widget.face.isScanned = true;
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
                            height: 157.h,
                            width: 338.h,
                            decoration: BoxDecoration(
                              color: pure_white,
                              border: Border.all(
                                color: secondaryColors.elementAt(14),
                              ),
                            ),
                            child: Column(
                              children: [
                                SizedBox(
                                  height: 20.h,
                                ),
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
                                SizedBox(
                                  height: 42.h,
                                ),
                                Text(
                                  "Threshold ${widget.face.qualityPercentage.toInt()}%",
                                  style: Theme.of(context)
                                      .textTheme
                                      .bodyLarge
                                      ?.copyWith(
                                        fontSize: 14,
                                        fontWeight: regular,
                                        color: secondaryColors.elementAt(1),
                                      ),
                                ),
                                SizedBox(
                                  height: 16.h,
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
                                  .extractImageValues()
                                  .then((value) {
                                temp = value;
                              });
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
                                                        .extractImageValues()
                                                        .then((value) {
                                                      temp = value;
                                                    });
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
                                                      .extractImageValues()
                                                      .then((value) {
                                                    widget.exception
                                                        .listofImages = value;
                                                  });
                                                  widget.exception.isScanned =
                                                      true;
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
                            height: 157.h,
                            width: 338.h,
                            decoration: BoxDecoration(
                              color: pure_white,
                              border: Border.all(
                                color: secondaryColors.elementAt(14),
                              ),
                            ),
                            child: Column(
                              children: [
                                SizedBox(
                                  height: 20.h,
                                ),
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
                                SizedBox(
                                  height: 42.h,
                                ),
                                Text(
                                  "Threshold ${widget.exception.qualityPercentage.toInt()}%",
                                  style: Theme.of(context)
                                      .textTheme
                                      .bodyLarge
                                      ?.copyWith(
                                        fontSize: 14,
                                        fontWeight: regular,
                                        color: secondaryColors.elementAt(1),
                                      ),
                                ),
                                SizedBox(
                                  height: 16.h,
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
                                      onTap: () {
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
                                      onTap: () {
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
                                        onTap: () {
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
                                        onTap: () {
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
                                        onTap: () {
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
                                        onTap: () {
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
                                        onTap: () {
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
                                        onTap: () {
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
                                        onTap: () {
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
                                        onTap: () {
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
                                        onTap: () {
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
                                        onTap: () {
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
                      height: 164.h,
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
                              Stack(
                                children: [
                                  InkWell(
                                    onTap: () {
                                      setState(() {
                                        widget.face.exceptions[0] = !(widget
                                            .face.exceptions
                                            .elementAt(0));
                                        if (widget.face.exceptions
                                            .contains(true)) {
                                          if (widget
                                              .face.exceptionType.isEmpty) {
                                            widget.face.exceptionType =
                                                "Permanent";
                                          }
                                        }
                                        if (!widget.face.exceptions
                                            .contains(true)) {
                                          widget.face.exceptionType = "";
                                        }
                                      });
                                    },
                                    child: Image.asset(
                                      "assets/images/Face@2x.png",
                                      height: 114,
                                    ),
                                  ),
                                  Positioned(
                                      top: 1,
                                      left: 48,
                                      child: Icon(
                                        Icons.cancel_rounded,
                                        color:
                                            (widget.face.exceptions[0] == true)
                                                ? secondaryColors.elementAt(15)
                                                : Colors.transparent,
                                        size: 20,
                                      )),
                                ],
                              )
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
                      height: 164.h,
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
              ],
            ),
          ),
        )
      ],
    );
  }
}
