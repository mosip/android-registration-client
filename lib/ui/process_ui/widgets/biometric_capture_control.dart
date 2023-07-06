import 'package:flutter/material.dart';
import 'package:flutter/src/widgets/framework.dart';

import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:provider/provider.dart';
import 'package:registration_client/model/biometric_attribute_data.dart';
import 'package:registration_client/model/field.dart';
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
      listofImages: [],
      qualityPercentage: 0);
  BiometricAttributeData rightHand = BiometricAttributeData(
      exceptionType: "",
      exceptions: [false, false, false, false],
      isScanned: false,
      listofImages: [],
      qualityPercentage: 0);
  BiometricAttributeData leftHand = BiometricAttributeData(
      exceptionType: "",
      exceptions: [false, false, false, false],
      isScanned: false,
      listofImages: [],
      qualityPercentage: 0);
  BiometricAttributeData thumbs = BiometricAttributeData(
      exceptionType: "",
      exceptions: [false, false],
      isScanned: false,
      listofImages: [],
      qualityPercentage: 0);
  BiometricAttributeData face = BiometricAttributeData(
      exceptionType: "",
      exceptions: [false],
      isScanned: false,
      listofImages: [],
      qualityPercentage: 0);
  BiometricAttributeData extra = BiometricAttributeData(
      exceptionType: "",
      exceptions: [false],
      isScanned: false,
      listofImages: [],
      qualityPercentage: 0);

  @override
  State<BiometricCaptureControl> createState() =>
      _BiometricCaptureControlState();
}

class _BiometricCaptureControlState extends State<BiometricCaptureControl> {
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
                                      color:
                                          (widget.biometricAttribute == "Iris")
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
                                      color: (widget.biometricAttribute ==
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
                                      color: (widget.biometricAttribute ==
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
                                      color: (widget.biometricAttribute ==
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
                                      color:
                                          (widget.biometricAttribute == "Face")
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
                              widget.biometricAttribute = "Person";
                            });
                          },
                          child: Padding(
                            padding: const EdgeInsets.fromLTRB(10, 5, 10, 10),
                            child: Container(
                              height: 78.h,
                              width: 78.h,
                              decoration: BoxDecoration(
                                  border: Border.all(
                                      color: (widget.biometricAttribute ==
                                              "Person")
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
                      thresholdPercentage: widget.iris.qualityPercentage,
                      images: [
                        "assets/images/Left Eye@2x.png",
                        "assets/images/Right Eye@2x.png"
                      ]),
                if (widget.biometricAttribute == "Right Hand")
                  BiometricCaptureScanBlock(
                      title: "RightHandScan",
                      thresholdPercentage: widget.rightHand.qualityPercentage,
                      images: ["assets/images/Right Hand@2x.png"]),
                if (widget.biometricAttribute == "Left Hand")
                  BiometricCaptureScanBlock(
                      title: "LeftHandScan",
                      thresholdPercentage: widget.leftHand.qualityPercentage,
                      images: ["assets/images/Left Hand@2x.png"]),
                if (widget.biometricAttribute == "Thumbs")
                  BiometricCaptureScanBlock(
                      title: "ThumbsScan",
                      thresholdPercentage: widget.thumbs.qualityPercentage,
                      images: ["assets/images/Thumbs@2x.png"]),
                if (widget.biometricAttribute == "Face")
                  BiometricCaptureScanBlock(
                      title: "FaceScan",
                      thresholdPercentage: widget.face.qualityPercentage,
                      images: ["assets/images/Face@2x.png"]),
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
              ],
            ),
          ),
        )
      ],
    );
  }
}
