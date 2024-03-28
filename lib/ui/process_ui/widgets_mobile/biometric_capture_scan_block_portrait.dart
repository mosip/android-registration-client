/*
 * Copyright (c) Modular Open Source Identity Platform
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *
*/

// ignore_for_file: deprecated_member_use

import 'dart:convert';

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:flutter_svg/flutter_svg.dart';
import 'package:percent_indicator/linear_percent_indicator.dart';
import 'package:provider/provider.dart';
import 'package:registration_client/model/biometric_attribute_data.dart';
import 'package:registration_client/model/biometrics_dto.dart';
import 'package:registration_client/model/field.dart';
import 'package:registration_client/pigeon/biometrics_pigeon.dart';
import 'package:registration_client/provider/biometric_capture_control_provider.dart';
import 'package:registration_client/provider/global_provider.dart';
import 'package:flutter_gen/gen_l10n/app_localizations.dart';

import 'package:registration_client/utils/app_config.dart';

class BiometricCaptureScanBlockPortrait extends StatefulWidget {
  const BiometricCaptureScanBlockPortrait({super.key, required this.field});
  final Field field;

  @override
  State<BiometricCaptureScanBlockPortrait> createState() =>
      _BiometricCaptureScanBlockPortraitState();
}

class _BiometricCaptureScanBlockPortraitState
    extends State<BiometricCaptureScanBlockPortrait> {
  bool isPortrait = true;
  @override
  void initState() {
    // SystemChrome.setPreferredOrientations([
    //   DeviceOrientation.portraitDown,
    //   DeviceOrientation.portraitUp,
    // ]);

    context
        .read<BiometricCaptureControlProvider>()
        .biometricCaptureScanBlockTabIndex = 1;

    super.initState();
  }

  setInitialState() {
    if (context.read<BiometricCaptureControlProvider>().biometricAttribute ==
        "Iris") {
      biometricAttributeData =
          context.read<BiometricCaptureControlProvider>().iris;
    }
    if (context.read<BiometricCaptureControlProvider>().biometricAttribute ==
        "Right Hand") {
      biometricAttributeData =
          context.read<BiometricCaptureControlProvider>().rightHand;
    }
    if (context.read<BiometricCaptureControlProvider>().biometricAttribute ==
        "Left Hand") {
      biometricAttributeData =
          context.read<BiometricCaptureControlProvider>().leftHand;
    }
    if (context.read<BiometricCaptureControlProvider>().biometricAttribute ==
        "Thumbs") {
      biometricAttributeData =
          context.read<BiometricCaptureControlProvider>().thumbs;
    }
    if (context.read<BiometricCaptureControlProvider>().biometricAttribute ==
        "Face") {
      biometricAttributeData =
          context.read<BiometricCaptureControlProvider>().face;
    }
    if (context.read<BiometricCaptureControlProvider>().biometricAttribute ==
        "Exception") {
      biometricAttributeData =
          context.read<BiometricCaptureControlProvider>().exception;
    }
  }

  _showCustomAlert(int currentAttemptNo, List<Uint8List?> temp) {
    return showDialog<String>(
      context: context,
      builder: (BuildContext context) => SingleChildScrollView(
        child: AlertDialog(
          content: SizedBox(
            height: (isMobileSize) ? 410.h : 610.h,
            width: 760.w,
            child: Column(
              children: [
                const SizedBox(
                  height: 26,
                ),
                Row(
                  children: [
                    const Spacer(),
                    const SizedBox(
                      width: 28,
                    ),
                    Text(
                      "${biometricAttributeData.title.replaceAll(" ", "")} ${AppLocalizations.of(context)!.capture}",
                      style: Theme.of(context).textTheme.bodyLarge?.copyWith(
                          fontSize: 28, fontWeight: bold, color: blackShade1),
                    ),
                    const Spacer(),
                    IconButton(
                        onPressed: () {
                          Navigator.pop(context);
                        },
                        icon: Icon(
                          Icons.close,
                          color: blackShade1,
                          weight: 25,
                          size: 28,
                        )),
                  ],
                ),
                Divider(
                  height: 66,
                  thickness: 1,
                  color: secondaryColors.elementAt(22),
                ),
                Row(
                  mainAxisAlignment: MainAxisAlignment.center,
                  children: [
                    ...temp.map(
                      (e) => Image.memory(
                        e!,
                        height: (isMobileSize) ? 130.h : 260.h,
                      ),
                    ),
                  ],
                ),
                Divider(
                  height: 82,
                  thickness: 1,
                  color: secondaryColors.elementAt(22),
                ),
                Container(
                  height: 96,
                  decoration: BoxDecoration(
                      color: secondaryColors.elementAt(23),
                      borderRadius: BorderRadius.circular(6)),
                  child: Center(
                    child: Text(
                      "${biometricAttributeData.noOfCapturesAllowed - currentAttemptNo} ${AppLocalizations.of(context)!.attempts_left}",
                      style: TextStyle(
                          fontSize: 25,
                          fontWeight: semiBold,
                          color: secondaryColors.elementAt(24)),
                    ),
                  ),
                )
              ],
            ),
          ),
        ),
      ),
    );
  }

  _showScanDialogBox(List<Uint8List?> temp) async {
    int currentAttemptNo = await BiometricsApi().getBioAttempt(
        widget.field.id!, biometricAttributeData.title.replaceAll(" ", ""));
    _showCustomAlert(currentAttemptNo, temp);
  }

  generateList(String key, BiometricAttributeData data) {
    List<BiometricAttributeData> list = [];

    if (context.read<GlobalProvider>().fieldInputValue.containsKey(key)) {
      if (context.read<BiometricCaptureControlProvider>().getElementPosition(
              context.read<GlobalProvider>().fieldInputValue[key],
              data.title) ==
          -1) {
        context.read<GlobalProvider>().fieldInputValue[key].add(data);
      } else {
        context.read<GlobalProvider>().fieldInputValue[key].removeAt(context
            .read<BiometricCaptureControlProvider>()
            .getElementPosition(
                context.read<GlobalProvider>().fieldInputValue[key],
                data.title));
        context.read<GlobalProvider>().fieldInputValue[key].add(data);
      }
    } else {
      list.add(data);
      context.read<GlobalProvider>().fieldInputValue[key] = list;
    }
  }

  Widget _scanBlock() {
    return Column(
      children: [
        Container(
          decoration: BoxDecoration(
            color: pureWhite,
            border: Border.all(color: secondaryColors.elementAt(14), width: 1),
          ),
          height: 353.h,
          width: (isPortrait) ? double.infinity : 760.w,
          child: (biometricAttributeData.isScanned == false)
              ? Directionality(
                  textDirection: TextDirection.ltr,
                  child: Row(
                    mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                    children: [
                      ...biometricAttributeData.listofImages
                          .map((e) => SvgPicture.asset(
                                "$e",
                                height: (isMobileSize)
                                    ? biometricAttributeData.imageHeightMobile.h
                                    : biometricAttributeData
                                        .imageHeightTablet.h,
                                fit: BoxFit.fitHeight,
                              ))
                    ],
                  ),
                )
              : Directionality(
                  textDirection: TextDirection.ltr,
                  child: Row(
                    mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                    children: [
                      ...biometricAttributeData.listofImages
                          .map((e) => Image.memory(
                                e,
                                height: (isMobileSize) ? 70.h : 130.h,
                              ))
                    ],
                  ),
                ),
        ),
        const SizedBox(
          height: 30,
        ),
        Container(
          decoration: BoxDecoration(
            color: pureWhite,
            border: Border.all(color: secondaryColors.elementAt(14), width: 1),
          ),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.center,
            children: [
              const SizedBox(
                width: double.infinity,
              ),
              Padding(
                padding: const EdgeInsets.fromLTRB(0, 20, 0, 18),
                child: Text(
                  AppLocalizations.of(context)!.quality,
                  style: TextStyle(
                      fontSize: 24, color: blackShade1, fontWeight: semiBold),
                ),
              ),
              Text(
                "${AppLocalizations.of(context)!.threshold} ${biometricAttributeData.thresholdPercentage}%",
                style: Theme.of(context).textTheme.bodyLarge?.copyWith(
                      fontSize: 23,
                      fontWeight: regular,
                      color: secondaryColors.elementAt(1),
                    ),
              ),
              SizedBox(
                height: 21.h,
              ),
              Row(
                mainAxisAlignment: MainAxisAlignment.center,
                children: [
                  LinearPercentIndicator(
                    width: (isMobileSize) ? 250.w : 450.w,
                    lineHeight: 15.4,
                    percent: biometricAttributeData.qualityPercentage / 100,
                    backgroundColor: Colors.grey,
                    progressColor:
                        (biometricAttributeData.qualityPercentage.toInt() <
                                int.parse(
                                    biometricAttributeData.thresholdPercentage))
                            ? secondaryColors.elementAt(26)
                            : secondaryColors.elementAt(11),
                  ),
                  SizedBox(
                    width: (isMobileSize) ? 20.w : 43.w,
                  ),
                  Text(
                    "${biometricAttributeData.qualityPercentage.toInt()}%",
                    style: Theme.of(context).textTheme.bodyLarge?.copyWith(
                          fontSize: 23,
                          fontWeight: semiBold,
                          color: secondaryColors.elementAt(1),
                        ),
                  ),
                ],
              ),
              SizedBox(
                height: 26.h,
              ),
              SizedBox(
                height: 13,
                child: Container(
                  decoration: BoxDecoration(
                    gradient: LinearGradient(
                      begin: Alignment.topCenter,
                      end: Alignment.bottomCenter,
                      colors: [
                        secondaryColors.elementAt(21),
                        pureWhite,
                      ],
                    ),
                  ),
                ),
              ),
              SizedBox(
                height: 6.h,
              ),
              Column(
                crossAxisAlignment: CrossAxisAlignment.center,
                children: [
                  Text(
                    AppLocalizations.of(context)!.attempts,
                    style: Theme.of(context).textTheme.bodyLarge?.copyWith(
                        fontSize: 24, color: blackShade1, fontWeight: semiBold),
                  ),
                  const SizedBox(
                    height: 29.4,
                  ),
                  Row(
                    mainAxisAlignment: MainAxisAlignment.center,
                    children: [
                      for (int i = 1;
                          i <= biometricAttributeData.noOfCapturesAllowed;
                          i++)
                        Padding(
                          padding: const EdgeInsets.only(right: 16.3),
                          child: InkWell(
                            onTap: () async {
                              if (biometricAttributeData.attemptNo >= i) {
                                await BiometricsApi()
                                    .getBiometrics(
                                        widget.field.id!,
                                        biometricAttributeData.title
                                            .replaceAll(" ", ""),
                                        i)
                                    .then((value) {
                                  biometricAttributeData.listOfBiometricsDto
                                      .clear();
                                  for (var e in value) {
                                    biometricAttributeData.listOfBiometricsDto
                                        .add(BiometricsDto.fromJson(
                                            json.decode(e!)));
                                  }
                                });

                                setState(() {
                                  biometricAttributeData.qualityPercentage =
                                      context
                                          .read<
                                              BiometricCaptureControlProvider>()
                                          .avgScore(biometricAttributeData
                                              .listOfBiometricsDto);
                                });
                                await BiometricsApi()
                                    .extractImageValuesByAttempt(
                                        widget.field.id!,
                                        biometricAttributeData.title
                                            .replaceAll(" ", ""),
                                        i)
                                    .then((value) {
                                  biometricAttributeData.listofImages = value;
                                });
                                setState(() {});
                              }
                            },
                            child: Container(
                              padding: const EdgeInsets.symmetric(
                                vertical: 7.3,
                                horizontal: 30,
                              ),
                              decoration: BoxDecoration(
                                borderRadius: BorderRadius.circular(20),
                                border: Border.all(
                                  color: secondaryColors.elementAt(17),
                                ),
                                color: (biometricAttributeData.attemptNo < i)
                                    ? secondaryColors.elementAt(18)
                                    : secondaryColors.elementAt(11),
                              ),
                              child: Text(
                                i.toString(),
                                style: Theme.of(context)
                                    .textTheme
                                    .bodyLarge
                                    ?.copyWith(
                                        fontSize: 21,
                                        color:
                                            (biometricAttributeData.attemptNo <
                                                    i)
                                                ? secondaryColors.elementAt(19)
                                                : pureWhite,
                                        fontWeight: semiBold),
                              ),
                            ),
                          ),
                        ),
                    ],
                  )
                ],
              ),
              SizedBox(
                height: 18.h,
              ),
            ],
          ),
        ),
        const SizedBox(
          height: 45,
        ),
        ElevatedButton.icon(
          onPressed: () async {
            if (biometricAttributeData.exceptions.contains(false)) {
              if (biometricAttributeData.attemptNo <
                  biometricAttributeData.noOfCapturesAllowed) {
                List<Uint8List?> tempImageList = [];
                await BiometricsApi().invokeDiscoverSbi(widget.field.id!,
                    biometricAttributeData.title.replaceAll(" ", ""));
                await BiometricsApi()
                    .getBestBiometrics(widget.field.id!,
                        biometricAttributeData.title.replaceAll(" ", ""))
                    .then((value) {});
                await BiometricsApi()
                    .extractImageValues(widget.field.id!,
                        biometricAttributeData.title.replaceAll(" ", ""))
                    .then((value) {
                  tempImageList = value;
                });

                biometricAttributeData.listOfBiometricsDto.clear();
                await BiometricsApi().incrementBioAttempt(widget.field.id!,
                    biometricAttributeData.title.replaceAll(" ", ""));
                biometricAttributeData.attemptNo = await BiometricsApi()
                    .getBioAttempt(widget.field.id!,
                        biometricAttributeData.title.replaceAll(" ", ""));
                await BiometricsApi()
                    .getBestBiometrics(widget.field.id!,
                        biometricAttributeData.title.replaceAll(" ", ""))
                    .then((value) async {
                  for (var e in value) {
                    biometricAttributeData.listOfBiometricsDto.add(
                      BiometricsDto.fromJson(
                        json.decode(e!),
                      ),
                    );
                  }
                });
                biometricAttributeData.qualityPercentage =
                    biometricCaptureControlProvider
                        .avgScore(biometricAttributeData.listOfBiometricsDto);
                await BiometricsApi()
                    .extractImageValues(widget.field.id!,
                        biometricAttributeData.title.replaceAll(" ", ""))
                    .then((value) {
                  biometricAttributeData.listofImages = value;
                });
                biometricAttributeData.isScanned = true;
                generateList("${widget.field.id}", biometricAttributeData);
                _showScanDialogBox(tempImageList);
              }
            }
          },
          icon: Icon(
            Icons.crop_free,
            color: pureWhite,
            size: 36.6,
          ),
          label: Text(
            AppLocalizations.of(context)!.scan,
            style: Theme.of(context)
                .textTheme
                .bodySmall
                ?.copyWith(fontSize: 27, fontWeight: bold, color: pureWhite),
          ),
          style: OutlinedButton.styleFrom(
            padding: (isMobileSize)
                ? const EdgeInsets.symmetric(horizontal: 35, vertical: 25)
                : const EdgeInsets.symmetric(horizontal: 46, vertical: 34),
            backgroundColor: (biometricAttributeData.exceptions.contains(false)
                ? solidPrimary
                : secondaryColors.elementAt(22)),
            shape: RoundedRectangleBorder(
              borderRadius: BorderRadius.circular(5),
            ),
          ),
        ),
        SizedBox(
          height: 20.h,
        ),
      ],
    );
  }

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

  Widget _exceptionBlock() {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Center(
          child: Container(
            decoration: BoxDecoration(
              color: pureWhite,
              border:
                  Border.all(color: secondaryColors.elementAt(14), width: 1),
            ),
            height: 353,
            width: (isPortrait) ? double.infinity : 760.w,
            child: _returnExceptionImage(),
          ),
        ),
        const SizedBox(
          height: 40,
        ),
        (biometricAttributeData.title != "Face" &&
                biometricAttributeData.title != "Exception")
            ? Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  const SizedBox(
                    height: 40,
                  ),
                  Text(
                    AppLocalizations.of(context)!.exception_type,
                    style: TextStyle(
                        fontSize: 25, fontWeight: semiBold, color: blackShade1),
                  ),
                  const SizedBox(
                    height: 18,
                  ),
                  Row(
                    children: [
                      Expanded(
                        child: OutlinedButton(
                          onPressed: () {
                            if (biometricAttributeData.exceptions
                                .contains(true)) {
                              biometricAttributeData.exceptionType =
                                  "Permanent";
                            }
                          },
                          style: ButtonStyle(
                            backgroundColor: MaterialStateProperty.all<Color>(
                                (biometricAttributeData.exceptionType ==
                                        "Permanent")
                                    ? secondaryColors.elementAt(12)
                                    : pureWhite),
                            shape: MaterialStateProperty.all<OutlinedBorder>(
                              RoundedRectangleBorder(
                                borderRadius: BorderRadius.circular(36),
                              ),
                            ),
                            side: MaterialStateProperty.all<BorderSide>(
                                BorderSide(
                                    color: secondaryColors.elementAt(12))),
                            padding:
                                MaterialStateProperty.all<EdgeInsetsGeometry>(
                              EdgeInsets.symmetric(
                                  horizontal: (isMobileSize) ? 10.w : 109.w,
                                  vertical: (isMobileSize) ? 10.h : 20.h),
                            ),
                          ),
                          child: Text(
                            AppLocalizations.of(context)!.permanent,
                            style: TextStyle(
                                fontSize: (isMobileSize) ? 16.h : 24.h,
                                fontWeight: FontWeight.w400,
                                color: (biometricAttributeData.exceptionType ==
                                        "Permanent")
                                    ? pureWhite
                                    : blackShade1),
                          ),
                        ),
                      ),
                      const SizedBox(
                        width: 30,
                      ),
                      Expanded(
                        child: OutlinedButton(
                          onPressed: () {
                            if (biometricAttributeData.exceptions
                                .contains(true)) {
                              biometricAttributeData.exceptionType =
                                  "Temporary";
                            }
                          },
                          style: ButtonStyle(
                            backgroundColor: MaterialStateProperty.all<Color>(
                                (biometricAttributeData.exceptionType ==
                                        "Temporary")
                                    ? secondaryColors.elementAt(12)
                                    : pureWhite),
                            shape: MaterialStateProperty.all<OutlinedBorder>(
                              RoundedRectangleBorder(
                                borderRadius: BorderRadius.circular(36),
                              ),
                            ),
                            side: MaterialStateProperty.all<BorderSide>(
                                BorderSide(
                                    color: secondaryColors.elementAt(12))),
                            padding:
                                MaterialStateProperty.all<EdgeInsetsGeometry>(
                              EdgeInsets.symmetric(
                                  horizontal: (isMobileSize) ? 10.w : 109.w,
                                  vertical: (isMobileSize) ? 10.h : 20.h),
                            ),
                          ),
                          child: Text(
                            AppLocalizations.of(context)!.temporary,
                            style: TextStyle(
                                fontSize: (isMobileSize) ? 16.h : 24.h,
                                fontWeight: FontWeight.w400,
                                color: (biometricAttributeData.exceptionType ==
                                        "Temporary")
                                    ? pureWhite
                                    : blackShade1),
                          ),
                        ),
                      ),
                    ],
                  ),
                  const SizedBox(
                    height: 40,
                  ),
                  Text(
                    AppLocalizations.of(context)!.comments,
                    style: TextStyle(
                        fontSize: 25, fontWeight: semiBold, color: blackShade1),
                  ),
                  const SizedBox(
                    height: 20,
                  ),
                  TextField(
                    maxLines: 10,
                    decoration: InputDecoration(
                      fillColor: pureWhite,
                      hintText: AppLocalizations.of(context)!
                          .add_comments_for_marking_the_exception,
                      hintStyle: TextStyle(
                          fontSize: 28,
                          fontWeight: regular,
                          color: secondaryColors.elementAt(1)),
                      border: OutlineInputBorder(
                        borderRadius: BorderRadius.circular(6),
                        borderSide: BorderSide(
                          color: secondaryColors.elementAt(12),
                        ),
                      ),
                    ),
                  ),
                ],
              )
            : Container(
                height: 96,
                decoration: BoxDecoration(
                    color: secondaryColors.elementAt(23),
                    borderRadius: BorderRadius.circular(6)),
                child: Center(
                  child: Text(
                    "${AppLocalizations.of(context)!.marking_exceptions_on} ${biometricAttributeData.viewTitle} ${AppLocalizations.of(context)!.is_not_allowed}",
                    style: TextStyle(
                        fontSize: 25,
                        fontWeight: semiBold,
                        color: secondaryColors.elementAt(24)),
                  ),
                ),
              ),
        SizedBox(
          height: 20.h,
        ),
      ],
    );
  }

  Widget _returnExceptionImage() {
    if (biometricAttributeData.title == "Iris") {
      return SizedBox(
        height: 300,
        child: Directionality(
          textDirection: TextDirection.ltr,
          child:
              Row(mainAxisAlignment: MainAxisAlignment.spaceEvenly, children: [
            Stack(
              children: [
                SizedBox(
                  height: (isMobileSize) ? 100.h : 150.h,
                  width: (isMobileSize) ? 165.w : 250.w,
                  child: SvgPicture.asset(
                    "assets/svg/Left Eye.svg",
                    fit: BoxFit.fitHeight,
                  ),
                ),
                Positioned(
                  top: 10,
                  left: 10,
                  child: SizedBox(
                      height: (isMobileSize) ? 100.h : 150.h,
                      width: (isMobileSize) ? 165.w : 250.w,
                      child: InkWell(
                        onTap: () async {
                          if (!(biometricAttributeData.exceptions[0]) == true) {
                            await BiometricsApi().addBioException(
                                widget.field.id!, "Iris", "leftEye");
                            resetAfterException(
                                widget.field.id!, biometricAttributeData);

                            biometricAttributeData.isScanned = false;
                            biometricAttributeData.attemptNo = 0;
                            biometricAttributeData.listofImages = [
                              "assets/svg/Left Eye.svg",
                              "assets/svg/Right Eye.svg"
                            ];
                            biometricAttributeData.listOfBiometricsDto = [];
                            biometricAttributeData.qualityPercentage = 0.0;
                            biometricAttributeData.thresholdPercentage = "0";
                          } else {
                            await BiometricsApi().removeBioException(
                                widget.field.id!, "Iris", "leftEye");
                            resetAfterException(
                                widget.field.id!, biometricAttributeData);
                            biometricAttributeData.isScanned = false;
                            biometricAttributeData.attemptNo = 0;
                            biometricAttributeData.listofImages = [
                              "assets/svg/Left Eye.svg",
                              "assets/svg/Right Eye.svg"
                            ];
                            biometricAttributeData.listOfBiometricsDto = [];
                            biometricAttributeData.qualityPercentage = 0.0;
                            biometricAttributeData.thresholdPercentage = "0";
                          }
                          biometricAttributeData.exceptions[0] =
                              !(biometricAttributeData.exceptions[0]);

                          if (biometricAttributeData.exceptions
                              .contains(true)) {
                            if (biometricAttributeData.exceptionType.isEmpty) {
                              biometricAttributeData.exceptionType =
                                  "Permanent";
                            }
                          }
                          if (!biometricAttributeData.exceptions
                              .contains(true)) {
                            biometricAttributeData.exceptionType = "";
                          }
                        },
                        child: SvgPicture.asset(
                          "assets/svg/left_iris.svg",
                          fit: BoxFit.fitHeight,
                          color: (biometricAttributeData.exceptions[0] == true)
                              ? secondaryColors.elementAt(25)
                              : Colors.transparent,
                        ),
                      )),
                ),
              ],
            ),
            Stack(
              children: [
                SizedBox(
                  height: (isMobileSize) ? 100.h : 150.h,
                  width: (isMobileSize) ? 165.w : 250.w,
                  child: SvgPicture.asset(
                    "assets/svg/Right Eye.svg",
                    fit: BoxFit.fitHeight,
                  ),
                ),
                Positioned(
                  top: 10,
                  right: 10,
                  child: SizedBox(
                      height: (isMobileSize) ? 100.h : 150.h,
                      width: (isMobileSize) ? 165.w : 250.w,
                      child: InkWell(
                        onTap: () async {
                          if (!(biometricAttributeData.exceptions[1]) == true) {
                            await BiometricsApi().addBioException(
                                widget.field.id!, "Iris", "rightEye");
                            resetAfterException(
                                widget.field.id!, biometricAttributeData);

                            biometricAttributeData.isScanned = false;
                            biometricAttributeData.attemptNo = 0;
                            biometricAttributeData.listofImages = [
                              "assets/svg/Left Eye.svg",
                              "assets/svg/Right Eye.svg"
                            ];
                            biometricAttributeData.listOfBiometricsDto = [];
                            biometricAttributeData.qualityPercentage = 0.0;
                            biometricAttributeData.thresholdPercentage = "0";
                          } else {
                            await BiometricsApi().removeBioException(
                                widget.field.id!, "Iris", "rightEye");
                            resetAfterException(
                                widget.field.id!, biometricAttributeData);
                            biometricAttributeData.isScanned = false;
                            biometricAttributeData.attemptNo = 0;
                            biometricAttributeData.listofImages = [
                              "assets/svg/Left Eye.svg",
                              "assets/svg/Right Eye.svg"
                            ];
                            biometricAttributeData.listOfBiometricsDto = [];
                            biometricAttributeData.qualityPercentage = 0.0;
                            biometricAttributeData.thresholdPercentage = "0";
                          }
                          biometricAttributeData.exceptions[1] =
                              !(biometricAttributeData.exceptions[1]);

                          if (biometricAttributeData.exceptions
                              .contains(true)) {
                            if (biometricAttributeData.exceptionType.isEmpty) {
                              biometricAttributeData.exceptionType =
                                  "Permanent";
                            }
                          }
                          if (!biometricAttributeData.exceptions
                              .contains(true)) {
                            biometricAttributeData.exceptionType = "";
                          }
                        },
                        child: SvgPicture.asset(
                          "assets/svg/right_iris.svg",
                          fit: BoxFit.fitHeight,
                          color: (biometricAttributeData.exceptions[1] == true)
                              ? secondaryColors.elementAt(25)
                              : Colors.transparent,
                        ),
                      )),
                ),
              ],
            ),
          ]),
        ),
      );
    }
    if (biometricAttributeData.title == "Right Hand") {
      return Stack(
        children: [
          SizedBox(
            height: 300,
            child: Row(
              mainAxisAlignment: MainAxisAlignment.center,
              children: [
                Stack(
                  children: [
                    SizedBox(
                      width: 300,
                      height: 300,
                      child: SvgPicture.asset(
                        "assets/svg/Right Hand.svg",
                        fit: BoxFit.fitHeight,
                      ),
                    ),
                    Positioned(
                        top: 95,
                        left: 67,
                        child: SvgPicture.asset(
                          "assets/svg/RH_1.svg",
                          height: 165,
                          color: (biometricAttributeData.exceptions[0] == true)
                              ? secondaryColors.elementAt(25)
                              : Colors.transparent,
                        )),
                    Positioned(
                        top: 55,
                        left: 117,
                        child: SvgPicture.asset(
                          "assets/svg/RH_2.svg",
                          height: 205,
                          color: (biometricAttributeData.exceptions[1] == true)
                              ? secondaryColors.elementAt(25)
                              : Colors.transparent,
                        )),
                    Positioned(
                        top: 97,
                        right: 97,
                        child: SvgPicture.asset(
                          "assets/svg/RH_3.svg",
                          height: 165,
                          color: (biometricAttributeData.exceptions[2] == true)
                              ? secondaryColors.elementAt(25)
                              : const Color.fromARGB(0, 221, 210, 210),
                        )),
                    Positioned(
                        top: 167,
                        right: 54,
                        child: SvgPicture.asset(
                          "assets/svg/RH_4.svg",
                          height: 100,
                          color: (biometricAttributeData.exceptions[3] == true)
                              ? secondaryColors.elementAt(25)
                              : Colors.transparent,
                        )),
                  ],
                ),
              ],
            ),
          ),
          Positioned(
              bottom: 20,
              right: 20,
              child: InkWell(
                  onTap: () {
                    showDialog<String>(
                      context: context,
                      builder: (context) => SingleChildScrollView(
                        child: Center(
                          child: AlertDialog(
                            insetPadding: EdgeInsets.symmetric(
                                vertical: (isMobileSize) ? 10 : 24,
                                horizontal: (isMobileSize) ? 10 : 40),
                            content: Container(
                              height: (isMobileSize) ? 500 : 720,
                              width: (isMobileSize) ? 404 : 760,
                              decoration: BoxDecoration(
                                  borderRadius: BorderRadius.circular(12)),
                              child: Column(
                                children: [
                                  Row(
                                    children: [
                                      const SizedBox(
                                        width: 50,
                                      ),
                                      const Spacer(),
                                      Text(
                                        "${biometricAttributeData.viewTitle} ${AppLocalizations.of(context)!.scan}",
                                        style: TextStyle(
                                            fontSize:
                                                (isMobileSize) ? 20.h : 28.h,
                                            fontWeight: bold,
                                            color: blackShade1,
                                            overflow: TextOverflow.ellipsis),
                                      ),
                                      const Spacer(),
                                      IconButton(
                                          onPressed: () {
                                            Navigator.pop(context);
                                          },
                                          icon: Icon(
                                            Icons.close,
                                            color: blackShade1,
                                            weight: 25,
                                            size: 28,
                                          )),
                                    ],
                                  ),
                                  Divider(
                                    height: 30,
                                    thickness: 1,
                                    color: secondaryColors.elementAt(22),
                                  ),
                                  Stack(
                                    children: [
                                      SizedBox(
                                        height: (isMobileSize) ? 339 : 639,
                                        width: (isMobileSize) ? 339 : 639,
                                        child: SvgPicture.asset(
                                          "assets/svg/Right Hand.svg",
                                          fit: BoxFit.fitHeight,
                                        ),
                                      ),
                                      Positioned(
                                          top: (isMobileSize) ? 109 : 205,
                                          left: (isMobileSize) ? 72 : 140,
                                          child: InkWell(
                                            onTap: () async {
                                              if (!(biometricAttributeData
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
                                                    biometricAttributeData);
                                                biometricAttributeData
                                                    .isScanned = false;
                                                biometricAttributeData
                                                    .attemptNo = 0;
                                                biometricAttributeData
                                                    .listofImages = [
                                                  "assets/svg/Right Hand.svg"
                                                ];
                                                biometricAttributeData
                                                    .listOfBiometricsDto = [];
                                                biometricAttributeData
                                                    .qualityPercentage = 0;
                                                biometricAttributeData
                                                    .thresholdPercentage = "0";
                                              } else {
                                                await BiometricsApi()
                                                    .removeBioException(
                                                        widget.field.id!,
                                                        "RightHand",
                                                        "rightIndex");
                                                resetAfterException(
                                                    widget.field.id!,
                                                    biometricAttributeData);
                                                biometricAttributeData
                                                    .isScanned = false;
                                                biometricAttributeData
                                                    .attemptNo = 0;
                                                biometricAttributeData
                                                    .listofImages = [
                                                  "assets/svg/Right Hand.svg"
                                                ];
                                                biometricAttributeData
                                                    .listOfBiometricsDto = [];
                                                biometricAttributeData
                                                    .qualityPercentage = 0;
                                                biometricAttributeData
                                                    .thresholdPercentage = "0";
                                              }
                                              biometricAttributeData
                                                      .exceptions[0] =
                                                  !(biometricAttributeData
                                                      .exceptions[0]);

                                              if (biometricAttributeData
                                                  .exceptions
                                                  .contains(true)) {
                                                if (biometricAttributeData
                                                    .exceptionType.isEmpty) {
                                                  biometricAttributeData
                                                          .exceptionType =
                                                      "Permanent";
                                                }
                                              }
                                              if (!biometricAttributeData
                                                  .exceptions
                                                  .contains(true)) {
                                                biometricAttributeData
                                                    .exceptionType = "";
                                              }
                                            },
                                            child: SvgPicture.asset(
                                              "assets/svg/RH_1.svg",
                                              height:
                                                  (isMobileSize) ? 204 : 385,
                                              color: (biometricAttributeData
                                                          .exceptions[0] ==
                                                      true)
                                                  ? secondaryColors
                                                      .elementAt(25)
                                                  : Colors.transparent,
                                            ),
                                          )),
                                      Positioned(
                                          top: (isMobileSize) ? 66 : 125,
                                          left: (isMobileSize) ? 127 : 245,
                                          child: InkWell(
                                            onTap: () async {
                                              if (!(biometricAttributeData
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
                                                    biometricAttributeData);
                                                biometricAttributeData
                                                    .isScanned = false;
                                                biometricAttributeData
                                                    .attemptNo = 0;
                                                biometricAttributeData
                                                    .listofImages = [
                                                  "assets/svg/Right Hand.svg"
                                                ];
                                                biometricAttributeData
                                                    .listOfBiometricsDto = [];
                                                biometricAttributeData
                                                    .qualityPercentage = 0;
                                                biometricAttributeData
                                                    .thresholdPercentage = "0";
                                              } else {
                                                await BiometricsApi()
                                                    .removeBioException(
                                                        widget.field.id!,
                                                        "RightHand",
                                                        "rightMiddle");
                                                resetAfterException(
                                                    widget.field.id!,
                                                    biometricAttributeData);
                                                biometricAttributeData
                                                    .isScanned = false;
                                                biometricAttributeData
                                                    .attemptNo = 0;
                                                biometricAttributeData
                                                    .listofImages = [
                                                  "assets/svg/Right Hand.svg"
                                                ];
                                                biometricAttributeData
                                                    .listOfBiometricsDto = [];
                                                biometricAttributeData
                                                    .qualityPercentage = 0;
                                                biometricAttributeData
                                                    .thresholdPercentage = "0";
                                              }
                                              biometricAttributeData
                                                      .exceptions[1] =
                                                  !(biometricAttributeData
                                                      .exceptions[1]);

                                              if (biometricAttributeData
                                                  .exceptions
                                                  .contains(true)) {
                                                if (biometricAttributeData
                                                    .exceptionType.isEmpty) {
                                                  biometricAttributeData
                                                          .exceptionType =
                                                      "Permanent";
                                                }
                                              }
                                              if (!biometricAttributeData
                                                  .exceptions
                                                  .contains(true)) {
                                                biometricAttributeData
                                                    .exceptionType = "";
                                              }
                                            },
                                            child: SvgPicture.asset(
                                              "assets/svg/RH_2.svg",
                                              height:
                                                  (isMobileSize) ? 247 : 465,
                                              color: (biometricAttributeData
                                                          .exceptions[1] ==
                                                      true)
                                                  ? secondaryColors
                                                      .elementAt(25)
                                                  : Colors.transparent,
                                            ),
                                          )),
                                      Positioned(
                                          top: (isMobileSize) ? 114 : 215,
                                          right: (isMobileSize) ? 104 : 203,
                                          child: InkWell(
                                            onTap: () async {
                                              if (!(biometricAttributeData
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
                                                    biometricAttributeData);
                                                biometricAttributeData
                                                    .isScanned = false;
                                                biometricAttributeData
                                                    .attemptNo = 0;
                                                biometricAttributeData
                                                    .listofImages = [
                                                  "assets/svg/Right Hand.svg"
                                                ];
                                                biometricAttributeData
                                                    .listOfBiometricsDto = [];
                                                biometricAttributeData
                                                    .qualityPercentage = 0;
                                                biometricAttributeData
                                                    .thresholdPercentage = "0";
                                              } else {
                                                await BiometricsApi()
                                                    .removeBioException(
                                                        widget.field.id!,
                                                        "RightHand",
                                                        "rightRing");
                                                resetAfterException(
                                                    widget.field.id!,
                                                    biometricAttributeData);
                                                biometricAttributeData
                                                    .isScanned = false;
                                                biometricAttributeData
                                                    .attemptNo = 0;
                                                biometricAttributeData
                                                    .listofImages = [
                                                  "assets/svg/Right Hand.svg"
                                                ];
                                                biometricAttributeData
                                                    .listOfBiometricsDto = [];
                                                biometricAttributeData
                                                    .qualityPercentage = 0;
                                                biometricAttributeData
                                                    .thresholdPercentage = "0";
                                              }
                                              biometricAttributeData
                                                      .exceptions[2] =
                                                  !(biometricAttributeData
                                                      .exceptions[2]);

                                              if (biometricAttributeData
                                                  .exceptions
                                                  .contains(true)) {
                                                if (biometricAttributeData
                                                    .exceptionType.isEmpty) {
                                                  biometricAttributeData
                                                          .exceptionType =
                                                      "Permanent";
                                                }
                                              }
                                              if (!biometricAttributeData
                                                  .exceptions
                                                  .contains(true)) {
                                                biometricAttributeData
                                                    .exceptionType = "";
                                              }
                                            },
                                            child: SvgPicture.asset(
                                              "assets/svg/RH_3.svg",
                                              height:
                                                  (isMobileSize) ? 204 : 385,
                                              color: (biometricAttributeData
                                                          .exceptions[2] ==
                                                      true)
                                                  ? secondaryColors
                                                      .elementAt(25)
                                                  : Colors.transparent,
                                            ),
                                          )),
                                      Positioned(
                                          top: (isMobileSize) ? 189 : 357,
                                          right: (isMobileSize) ? 56 : 110,
                                          child: InkWell(
                                            onTap: () async {
                                              if (!(biometricAttributeData
                                                      .exceptions
                                                      .elementAt(3)) ==
                                                  true) {
                                                await BiometricsApi()
                                                    .addBioException(
                                                        widget.field.id!,
                                                        "RightHand",
                                                        "rightIndex");
                                                resetAfterException(
                                                    widget.field.id!,
                                                    biometricAttributeData);
                                                biometricAttributeData
                                                    .isScanned = false;
                                                biometricAttributeData
                                                    .attemptNo = 0;
                                                biometricAttributeData
                                                    .listofImages = [
                                                  "assets/svg/Right Hand.svg"
                                                ];
                                                biometricAttributeData
                                                    .listOfBiometricsDto = [];
                                                biometricAttributeData
                                                    .qualityPercentage = 0;
                                                biometricAttributeData
                                                    .thresholdPercentage = "0";
                                              } else {
                                                await BiometricsApi()
                                                    .removeBioException(
                                                        widget.field.id!,
                                                        "RightHand",
                                                        "rightLittle");
                                                resetAfterException(
                                                    widget.field.id!,
                                                    biometricAttributeData);
                                                biometricAttributeData
                                                    .isScanned = false;
                                                biometricAttributeData
                                                    .attemptNo = 0;
                                                biometricAttributeData
                                                    .listofImages = [
                                                  "assets/svg/Right Hand.svg"
                                                ];
                                                biometricAttributeData
                                                    .listOfBiometricsDto = [];
                                                biometricAttributeData
                                                    .qualityPercentage = 0;
                                                biometricAttributeData
                                                    .thresholdPercentage = "0";
                                              }
                                              biometricAttributeData
                                                      .exceptions[3] =
                                                  !(biometricAttributeData
                                                      .exceptions[3]);

                                              if (biometricAttributeData
                                                  .exceptions
                                                  .contains(true)) {
                                                if (biometricAttributeData
                                                    .exceptionType.isEmpty) {
                                                  biometricAttributeData
                                                          .exceptionType =
                                                      "Permanent";
                                                }
                                              }
                                              if (!biometricAttributeData
                                                  .exceptions
                                                  .contains(true)) {
                                                biometricAttributeData
                                                    .exceptionType = "";
                                              }
                                            },
                                            child: SvgPicture.asset(
                                              "assets/svg/RH_4.svg",
                                              height:
                                                  (isMobileSize) ? 132 : 250,
                                              color: (biometricAttributeData
                                                          .exceptions[3] ==
                                                      true)
                                                  ? secondaryColors
                                                      .elementAt(25)
                                                  : Colors.transparent,
                                            ),
                                          )),
                                    ],
                                  ),
                                ],
                              ),
                            ),
                          ),
                        ),
                      ),
                    );
                  },
                  child: Container(
                      decoration: BoxDecoration(
                          borderRadius: BorderRadius.circular(1000),
                          color: solidPrimary),
                      height: 75,
                      width: 75,
                      child: Icon(
                        Icons.zoom_in,
                        color: pureWhite,
                        size: 35,
                      )))),
        ],
      );
    }
    if (biometricAttributeData.title == "Left Hand") {
      return Stack(
        children: [
          SizedBox(
            height: 300,
            child: Row(
              mainAxisAlignment: MainAxisAlignment.center,
              children: [
                Stack(
                  children: [
                    SizedBox(
                      width: 300,
                      height: 300,
                      child: SvgPicture.asset(
                        "assets/svg/Left Hand.svg",
                        fit: BoxFit.fitHeight,
                      ),
                    ),
                    Positioned(
                        top: 167,
                        left: 54,
                        child: SvgPicture.asset(
                          "assets/svg/LH_1.svg",
                          height: 100,
                          color: (biometricAttributeData.exceptions[3] == true)
                              ? secondaryColors.elementAt(25)
                              : Colors.transparent,
                        )),
                    Positioned(
                        top: 97,
                        left: 97,
                        child: SvgPicture.asset(
                          "assets/svg/LH_2.svg",
                          height: 165,
                          color: (biometricAttributeData.exceptions[2] == true)
                              ? secondaryColors.elementAt(25)
                              : Colors.transparent,
                        )),
                    Positioned(
                        top: 55,
                        right: 117,
                        child: SvgPicture.asset(
                          "assets/svg/LH_3.svg",
                          height: 205,
                          color: (biometricAttributeData.exceptions[1] == true)
                              ? secondaryColors.elementAt(25)
                              : Colors.transparent,
                        )),
                    Positioned(
                        top: 95,
                        right: 67,
                        child: SvgPicture.asset(
                          "assets/svg/LH_4.svg",
                          height: 165,
                          color: (biometricAttributeData.exceptions[0] == true)
                              ? secondaryColors.elementAt(25)
                              : Colors.transparent,
                        )),
                  ],
                ),
              ],
            ),
          ),
          Positioned(
              bottom: 20,
              right: 20,
              child: InkWell(
                  onTap: () {
                    showDialog<String>(
                      context: context,
                      builder: (context) => SingleChildScrollView(
                        child: AlertDialog(
                          content: Container(
                            height: (isMobileSize) ? 500 : 720,
                            width: (isMobileSize) ? 404 : 760,
                            decoration: BoxDecoration(
                                borderRadius: BorderRadius.circular(12)),
                            child: Column(
                              children: [
                                Row(
                                  children: [
                                    const SizedBox(
                                      width: 50,
                                    ),
                                    const Spacer(),
                                    Text(
                                      "${biometricAttributeData.viewTitle} ${AppLocalizations.of(context)!.scan}",
                                      style: TextStyle(
                                          fontSize:
                                              (isMobileSize) ? 20.h : 28.h,
                                          fontWeight: bold,
                                          color: blackShade1,
                                          overflow: TextOverflow.ellipsis),
                                    ),
                                    const Spacer(),
                                    IconButton(
                                        onPressed: () {
                                          Navigator.pop(context);
                                        },
                                        icon: Icon(
                                          Icons.close,
                                          color: blackShade1,
                                          weight: 25,
                                          size: 28,
                                        )),
                                  ],
                                ),
                                Divider(
                                  height: 30,
                                  thickness: 1,
                                  color: secondaryColors.elementAt(22),
                                ),
                                Stack(
                                  children: [
                                    SizedBox(
                                      height: (isMobileSize) ? 339 : 639,
                                      width: (isMobileSize) ? 339 : 639,
                                      child: SvgPicture.asset(
                                        "assets/svg/Left Hand.svg",
                                        fit: BoxFit.fitHeight,
                                      ),
                                    ),
                                    Positioned(
                                        top: (isMobileSize) ? 189 : 357,
                                        left: (isMobileSize) ? 52 : 110,
                                        child: InkWell(
                                          onTap: () async {
                                            if (!(biometricAttributeData
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
                                                  biometricAttributeData);
                                              biometricAttributeData.isScanned =
                                                  false;
                                              biometricAttributeData.attemptNo =
                                                  0;
                                              biometricAttributeData
                                                  .listofImages = [
                                                "assets/svg/Left Hand.svg"
                                              ];
                                              biometricAttributeData
                                                  .listOfBiometricsDto = [];
                                              biometricAttributeData
                                                  .qualityPercentage = 0;
                                              biometricAttributeData
                                                  .thresholdPercentage = "0";
                                            } else {
                                              await BiometricsApi()
                                                  .removeBioException(
                                                      widget.field.id!,
                                                      "LeftHand",
                                                      "leftLittle");
                                              resetAfterException(
                                                  widget.field.id!,
                                                  biometricAttributeData);
                                              biometricAttributeData.isScanned =
                                                  false;
                                              biometricAttributeData.attemptNo =
                                                  0;
                                              biometricAttributeData
                                                  .listofImages = [
                                                "assets/svg/Left Hand.svg"
                                              ];
                                              biometricAttributeData
                                                  .listOfBiometricsDto = [];
                                              biometricAttributeData
                                                  .qualityPercentage = 0;
                                              biometricAttributeData
                                                  .thresholdPercentage = "0";
                                            }
                                            biometricAttributeData
                                                    .exceptions[3] =
                                                !(biometricAttributeData
                                                    .exceptions[3]);

                                            if (biometricAttributeData
                                                .exceptions
                                                .contains(true)) {
                                              if (biometricAttributeData
                                                  .exceptionType.isEmpty) {
                                                biometricAttributeData
                                                        .exceptionType =
                                                    "Permanent";
                                              }
                                            }
                                            if (!biometricAttributeData
                                                .exceptions
                                                .contains(true)) {
                                              biometricAttributeData
                                                  .exceptionType = "";
                                            }
                                          },
                                          child: SvgPicture.asset(
                                            "assets/svg/LH_1.svg",
                                            height:
                                                (isMobileSize) ? 132.h : 250.h,
                                            color: (biometricAttributeData
                                                        .exceptions[3] ==
                                                    true)
                                                ? secondaryColors.elementAt(25)
                                                : Colors.transparent,
                                          ),
                                        )),
                                    Positioned(
                                        top: (isMobileSize) ? 114 : 215,
                                        left: (isMobileSize) ? 110 : 203,
                                        child: InkWell(
                                          onTap: () async {
                                            if (!(biometricAttributeData
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
                                                  biometricAttributeData);
                                              biometricAttributeData.isScanned =
                                                  false;
                                              biometricAttributeData.attemptNo =
                                                  0;
                                              biometricAttributeData
                                                  .listofImages = [
                                                "assets/svg/Left Hand.svg"
                                              ];
                                              biometricAttributeData
                                                  .listOfBiometricsDto = [];
                                              biometricAttributeData
                                                  .qualityPercentage = 0;
                                              biometricAttributeData
                                                  .thresholdPercentage = "0";
                                            } else {
                                              await BiometricsApi()
                                                  .removeBioException(
                                                      widget.field.id!,
                                                      "LeftHand",
                                                      "leftRing");
                                              resetAfterException(
                                                  widget.field.id!,
                                                  biometricAttributeData);
                                              biometricAttributeData.isScanned =
                                                  false;
                                              biometricAttributeData.attemptNo =
                                                  0;
                                              biometricAttributeData
                                                  .listofImages = [
                                                "assets/svg/Left Hand.svg"
                                              ];
                                              biometricAttributeData
                                                  .listOfBiometricsDto = [];
                                              biometricAttributeData
                                                  .qualityPercentage = 0;
                                              biometricAttributeData
                                                  .thresholdPercentage = "0";
                                            }
                                            biometricAttributeData
                                                    .exceptions[2] =
                                                !(biometricAttributeData
                                                    .exceptions[2]);

                                            if (biometricAttributeData
                                                .exceptions
                                                .contains(true)) {
                                              if (biometricAttributeData
                                                  .exceptionType.isEmpty) {
                                                biometricAttributeData
                                                        .exceptionType =
                                                    "Permanent";
                                              }
                                            }
                                            if (!biometricAttributeData
                                                .exceptions
                                                .contains(true)) {
                                              biometricAttributeData
                                                  .exceptionType = "";
                                            }
                                          },
                                          child: SvgPicture.asset(
                                            "assets/svg/LH_2.svg",
                                            height: (isMobileSize) ? 204 : 385,
                                            color: (biometricAttributeData
                                                        .exceptions[2] ==
                                                    true)
                                                ? secondaryColors.elementAt(25)
                                                : Colors.transparent,
                                          ),
                                        )),
                                    Positioned(
                                        top: (isMobileSize) ? 66 : 125,
                                        right: (isMobileSize) ? 130 : 245,
                                        child: InkWell(
                                          onTap: () async {
                                            if (!(biometricAttributeData
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
                                                  biometricAttributeData);
                                              biometricAttributeData.isScanned =
                                                  false;
                                              biometricAttributeData.attemptNo =
                                                  0;
                                              biometricAttributeData
                                                  .listofImages = [
                                                "assets/svg/Left Hand.svg"
                                              ];
                                              biometricAttributeData
                                                  .listOfBiometricsDto = [];
                                              biometricAttributeData
                                                  .qualityPercentage = 0;
                                              biometricAttributeData
                                                  .thresholdPercentage = "0";
                                            } else {
                                              await BiometricsApi()
                                                  .removeBioException(
                                                      widget.field.id!,
                                                      "LeftHand",
                                                      "leftMiddle");
                                              resetAfterException(
                                                  widget.field.id!,
                                                  biometricAttributeData);
                                              biometricAttributeData.isScanned =
                                                  false;
                                              biometricAttributeData.attemptNo =
                                                  0;
                                              biometricAttributeData
                                                  .listofImages = [
                                                "assets/svg/Left Hand.svg"
                                              ];
                                              biometricAttributeData
                                                  .listOfBiometricsDto = [];
                                              biometricAttributeData
                                                  .qualityPercentage = 0;
                                              biometricAttributeData
                                                  .thresholdPercentage = "0";
                                            }
                                            biometricAttributeData
                                                    .exceptions[1] =
                                                !(biometricAttributeData
                                                    .exceptions[1]);

                                            if (biometricAttributeData
                                                .exceptions
                                                .contains(true)) {
                                              if (biometricAttributeData
                                                  .exceptionType.isEmpty) {
                                                biometricAttributeData
                                                        .exceptionType =
                                                    "Permanent";
                                              }
                                            }
                                            if (!biometricAttributeData
                                                .exceptions
                                                .contains(true)) {
                                              biometricAttributeData
                                                  .exceptionType = "";
                                            }
                                          },
                                          child: SvgPicture.asset(
                                            "assets/svg/LH_3.svg",
                                            height: (isMobileSize) ? 247 : 465,
                                            color: (biometricAttributeData
                                                        .exceptions[1] ==
                                                    true)
                                                ? secondaryColors.elementAt(25)
                                                : Colors.transparent,
                                          ),
                                        )),
                                    Positioned(
                                        top: (isMobileSize) ? 109 : 205,
                                        right: (isMobileSize) ? 73 : 140,
                                        child: InkWell(
                                          onTap: () async {
                                            if (!(biometricAttributeData
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
                                                  biometricAttributeData);
                                              biometricAttributeData.isScanned =
                                                  false;
                                              biometricAttributeData.attemptNo =
                                                  0;
                                              biometricAttributeData
                                                  .listofImages = [
                                                "assets/svg/Left Hand.svg"
                                              ];
                                              biometricAttributeData
                                                  .listOfBiometricsDto = [];
                                              biometricAttributeData
                                                  .qualityPercentage = 0;
                                              biometricAttributeData
                                                  .thresholdPercentage = "0";
                                            } else {
                                              await BiometricsApi()
                                                  .removeBioException(
                                                      widget.field.id!,
                                                      "LeftHand",
                                                      "leftIndex");
                                              resetAfterException(
                                                  widget.field.id!,
                                                  biometricAttributeData);
                                              biometricAttributeData.isScanned =
                                                  false;
                                              biometricAttributeData.attemptNo =
                                                  0;
                                              biometricAttributeData
                                                  .listofImages = [
                                                "assets/svg/Left Hand.svg"
                                              ];
                                              biometricAttributeData
                                                  .listOfBiometricsDto = [];
                                              biometricAttributeData
                                                  .qualityPercentage = 0;
                                              biometricAttributeData
                                                  .thresholdPercentage = "0";
                                            }
                                            biometricAttributeData
                                                    .exceptions[0] =
                                                !(biometricAttributeData
                                                    .exceptions[0]);

                                            if (biometricAttributeData
                                                .exceptions
                                                .contains(true)) {
                                              if (biometricAttributeData
                                                  .exceptionType.isEmpty) {
                                                biometricAttributeData
                                                        .exceptionType =
                                                    "Permanent";
                                              }
                                            }
                                            if (!biometricAttributeData
                                                .exceptions
                                                .contains(true)) {
                                              biometricAttributeData
                                                  .exceptionType = "";
                                            }
                                          },
                                          child: SvgPicture.asset(
                                            "assets/svg/LH_4.svg",
                                            height: (isMobileSize) ? 204 : 385,
                                            color: (biometricAttributeData
                                                        .exceptions[0] ==
                                                    true)
                                                ? secondaryColors.elementAt(25)
                                                : Colors.transparent,
                                          ),
                                        )),
                                  ],
                                ),
                              ],
                            ),
                          ),
                        ),
                      ),
                    );
                  },
                  child: Container(
                      decoration: BoxDecoration(
                          borderRadius: BorderRadius.circular(1000),
                          color: solidPrimary),
                      height: 75,
                      width: 75,
                      child: Icon(
                        Icons.zoom_in,
                        color: pureWhite,
                        size: 35,
                      )))),
        ],
      );
    }
    if (biometricAttributeData.title == "Thumbs") {
      return SizedBox(
        height: 300,
        child: Row(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Stack(
              children: [
                SizedBox(
                  height: 300,
                  width: 300,
                  child: SvgPicture.asset(
                    "assets/svg/Thumbs.svg",
                    fit: BoxFit.fitHeight,
                  ),
                ),
                Positioned(
                  top: 49,
                  left: 5,
                  child: InkWell(
                    onTap: () async {
                      if (!(biometricAttributeData.exceptions.elementAt(0)) ==
                          true) {
                        await BiometricsApi().addBioException(
                            widget.field.id!, "Thumbs", "leftThumb");
                        resetAfterException(
                            widget.field.id!, biometricAttributeData);
                        biometricAttributeData.isScanned = false;
                        biometricAttributeData.attemptNo = 0;
                        biometricAttributeData.listofImages = [
                          "assets/svg/Thumbs.svg"
                        ];
                        biometricAttributeData.listOfBiometricsDto = [];
                        biometricAttributeData.qualityPercentage = 0;
                        biometricAttributeData.thresholdPercentage = "0";
                      } else {
                        await BiometricsApi().removeBioException(
                            widget.field.id!, "Thumbs", "leftThumb");
                        resetAfterException(
                            widget.field.id!, biometricAttributeData);
                        biometricAttributeData.isScanned = false;
                        biometricAttributeData.attemptNo = 0;
                        biometricAttributeData.listofImages = [
                          "assets/svg/Thumbs.svg"
                        ];
                        biometricAttributeData.listOfBiometricsDto = [];
                        biometricAttributeData.qualityPercentage = 0;
                        biometricAttributeData.thresholdPercentage = "0";
                      }
                      biometricAttributeData.exceptions[0] =
                          !(biometricAttributeData.exceptions[0]);

                      if (biometricAttributeData.exceptions.contains(true)) {
                        if (biometricAttributeData.exceptionType.isEmpty) {
                          biometricAttributeData.exceptionType = "Permanent";
                        }
                      }
                      if (!biometricAttributeData.exceptions.contains(true)) {
                        biometricAttributeData.exceptionType = "";
                      }
                    },
                    child: SvgPicture.asset(
                      "assets/svg/L_Thumb.svg",
                      color: (biometricAttributeData.exceptions[0] == true)
                          ? secondaryColors.elementAt(25)
                          : Colors.transparent,
                    ),
                  ),
                ),
                Positioned(
                    top: 49,
                    right: 5,
                    child: InkWell(
                      onTap: () async {
                        if (!(biometricAttributeData.exceptions.elementAt(1)) ==
                            true) {
                          await BiometricsApi().addBioException(
                              widget.field.id!, "Thumbs", "rightThumb");
                          resetAfterException(
                              widget.field.id!, biometricAttributeData);
                          biometricAttributeData.isScanned = false;
                          biometricAttributeData.attemptNo = 0;
                          biometricAttributeData.listofImages = [
                            "assets/svg/Thumbs.svg"
                          ];
                          biometricAttributeData.listOfBiometricsDto = [];
                          biometricAttributeData.qualityPercentage = 0;
                          biometricAttributeData.thresholdPercentage = "0";
                        } else {
                          await BiometricsApi().removeBioException(
                              widget.field.id!, "Thumbs", "rightThumb");
                          resetAfterException(
                              widget.field.id!, biometricAttributeData);
                          biometricAttributeData.isScanned = false;
                          biometricAttributeData.attemptNo = 0;
                          biometricAttributeData.listofImages = [
                            "assets/svg/Thumbs.svg"
                          ];
                          biometricAttributeData.listOfBiometricsDto = [];
                          biometricAttributeData.qualityPercentage = 0;
                          biometricAttributeData.thresholdPercentage = "0";
                        }
                        biometricAttributeData.exceptions[1] =
                            !(biometricAttributeData.exceptions[1]);

                        if (biometricAttributeData.exceptions.contains(true)) {
                          if (biometricAttributeData.exceptionType.isEmpty) {
                            biometricAttributeData.exceptionType = "Permanent";
                          }
                        }
                        if (!biometricAttributeData.exceptions.contains(true)) {
                          biometricAttributeData.exceptionType = "";
                        }
                      },
                      child: SvgPicture.asset(
                        "assets/svg/R_Thumb.svg",
                        color: (biometricAttributeData.exceptions[1] == true)
                            ? secondaryColors.elementAt(25)
                            : Colors.transparent,
                      ),
                    )),
              ],
            ),
          ],
        ),
      );
    }
    if (biometricAttributeData.title == "Face") {
      return SizedBox(
          height: 300,
          child: Row(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              SizedBox(
                height: 300,
                width: 300,
                child: SvgPicture.asset(
                  "assets/svg/Face.svg",
                  fit: BoxFit.fitHeight,
                ),
              ),
            ],
          ));
    }
    if (biometricAttributeData.title == "Exception") {
      return SizedBox(
          height: 300,
          child: Row(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              SizedBox(
                  height: 300,
                  width: 380,
                  child: SvgPicture.asset(
                    "assets/svg/Exception.svg",
                    fit: BoxFit.fitHeight,
                  )),
            ],
          ));
    }

    return Container();
  }

  _isExceptionPresent(String id) {
    bool isExceptionPresent = false;
    if (context.read<GlobalProvider>().fieldInputValue[id] != null) {
      context
          .read<GlobalProvider>()
          .fieldInputValue[id]
          .forEach((BiometricAttributeData x) {
        if (x.exceptions.contains(true)) {
          isExceptionPresent = true;
          return isExceptionPresent;
        }
      });
    }

    return isExceptionPresent;
  }

  _returnBiometricList(List<String?>? list, String id) {
    List<String> biometricList = [];
    if (list!.contains("leftEye") && list.contains("rightEye")) {
      biometricList.add("Iris");
    }
    if (list.contains("rightIndex") &&
        list.contains("rightLittle") &&
        list.contains("rightRing") &&
        list.contains("rightMiddle")) {
      biometricList.add("Right Hand");
    }
    if (list.contains("leftIndex") &&
        list.contains("leftLittle") &&
        list.contains("leftRing") &&
        list.contains("leftMiddle")) {
      biometricList.add("Left Hand");
    }
    if (list.contains("rightThumb") && list.contains("rightThumb")) {
      biometricList.add("Thumbs");
    }
    if (list.contains("face")) {
      biometricList.add("Face");
    }
    if (_isExceptionPresent(id) == true) {
      biometricList.add("Exception");
    }
    return biometricList;
  }

  _getNextElement(List<String> list, String title) {
    for (int i = 0; i < list.length; i++) {
      if (list[i] == title) {
        if (i < list.length - 1) {
          return list[i + 1];
        } else {
          return null;
        }
      }
    }
  }

  // @override
  // void dispose() {
  //   SystemChrome.setPreferredOrientations([
  //     DeviceOrientation.landscapeRight,
  //     DeviceOrientation.landscapeLeft,
  //     DeviceOrientation.portraitUp,
  //     DeviceOrientation.portraitDown,
  //   ]);
  //   super.dispose();
  // }

  late BiometricAttributeData biometricAttributeData;
  late BiometricCaptureControlProvider biometricCaptureControlProvider;
  @override
  Widget build(BuildContext context) {
    isPortrait = MediaQuery.of(context).orientation == Orientation.portrait;
    setInitialState();
    biometricCaptureControlProvider =
        Provider.of<BiometricCaptureControlProvider>(context, listen: false);

    return SafeArea(
      child: Scaffold(
        bottomNavigationBar: Container(
          color: pureWhite,
          padding: EdgeInsets.symmetric(
            horizontal: (isMobileSize) ? 30.w : 80.w,
            vertical: 16.h,
          ),
          height: (isMobileSize) ? 60.w : 100.h,
          child: Row(
            mainAxisAlignment: MainAxisAlignment.start,
            children: [
              const Spacer(),
              ElevatedButton(
                style: ButtonStyle(
                  maximumSize:
                      MaterialStateProperty.all<Size>(const Size(215, 68)),
                  minimumSize:
                      MaterialStateProperty.all<Size>(const Size(215, 68)),
                ),
                onPressed: () {
                  List<String> bioAttributes = (widget
                              .field.conditionalBioAttributes!.first!.ageGroup!
                              .compareTo(
                                  context.read<GlobalProvider>().ageGroup) ==
                          0)
                      ? _returnBiometricList(
                          widget.field.conditionalBioAttributes!.first!
                              .bioAttributes!,
                          widget.field.id!)
                      : _returnBiometricList(
                          widget.field.bioAttributes!, widget.field.id!);

                  var nextElement = _getNextElement(
                      bioAttributes,
                      context
                          .read<BiometricCaptureControlProvider>()
                          .biometricAttribute);
                  if (nextElement != null) {
                    context
                        .read<BiometricCaptureControlProvider>()
                        .biometricAttribute = nextElement;
                  } else {
                    Navigator.pop(context);
                  }
                },
                child: Text(AppLocalizations.of(context)!.next_button,
                    style: TextStyle(fontSize: 24, fontWeight: bold)),
              ),
            ],
          ),
        ),
        appBar: PreferredSize(
          preferredSize: Size.fromHeight((isMobileSize) ? 60 : 70),
          child: AppBar(
            automaticallyImplyLeading: false,
            flexibleSpace: SizedBox(
              height: (isMobileSize) ? 60 : 70,
              child: Card(
                margin: const EdgeInsets.all(0),
                child: Padding(
                  padding: (isMobileSize)
                      ? const EdgeInsets.fromLTRB(20, 9, 0, 9)
                      : const EdgeInsets.fromLTRB(20, 18, 0, 18),
                  child: Row(
                    mainAxisAlignment: MainAxisAlignment.spaceBetween,
                    children: [
                      (widget.field.inputRequired!)
                          ? RichText(
                              text: TextSpan(
                              text: context
                                  .read<GlobalProvider>()
                                  .chooseLanguage(widget.field.label!),
                              style: Theme.of(context)
                                  .textTheme
                                  .titleLarge
                                  ?.copyWith(
                                      fontSize: (isMobileSize) ? 14.h : 24.h,
                                      color: blackShade1,
                                      fontWeight: semiBold,
                                      overflow: TextOverflow.ellipsis),
                              children: const [
                                TextSpan(
                                  text: " *",
                                  style: TextStyle(
                                      color: Colors.red, fontSize: 15),
                                )
                              ],
                            ))
                          : Text(
                              context
                                  .read<GlobalProvider>()
                                  .chooseLanguage(widget.field.label!),
                              style: Theme.of(context)
                                  .textTheme
                                  .titleLarge
                                  ?.copyWith(
                                      fontSize: (isMobileSize) ? 14.h : 24.h,
                                      color: blackShade1,
                                      fontWeight: semiBold,
                                      overflow: TextOverflow.ellipsis),
                            ),
                      Padding(
                        padding: const EdgeInsets.only(right: 30),
                        child: InkWell(
                          onTap: () {
                            Navigator.pop(context);
                          },
                          child: Image.asset(
                            "assets/images/Group 57951.png",
                            height: (isMobileSize) ? 30.h : 52.h,
                          ),
                        ),
                      )
                    ],
                  ),
                ),
              ),
            ),
          ),
        ),
        body: SingleChildScrollView(
          child: Padding(
            padding: const EdgeInsets.fromLTRB(20, 40, 20, 0),
            child: Column(
              children: [
                const SizedBox(
                  width: double.infinity,
                ),
                Row(
                  mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                  children: [
                    Expanded(
                      child: InkWell(
                        onTap: () {
                          context
                              .read<BiometricCaptureControlProvider>()
                              .biometricCaptureScanBlockTabIndex = 1;
                        },
                        child: Container(
                          decoration: BoxDecoration(
                            color: (context
                                        .read<BiometricCaptureControlProvider>()
                                        .biometricCaptureScanBlockTabIndex ==
                                    1)
                                ? solidPrimary
                                : pureWhite,
                            border: (context
                                        .read<BiometricCaptureControlProvider>()
                                        .biometricCaptureScanBlockTabIndex ==
                                    1)
                                ? const Border()
                                : Border(
                                    bottom: BorderSide(
                                        color: solidPrimary, width: 3),
                                  ),
                          ),
                          height: 84,
                          child: Center(
                            child: Text(
                              "${biometricAttributeData.viewTitle} ${AppLocalizations.of(context)!.scan}",
                              style: TextStyle(
                                  fontSize: (isMobileSize) ? 18 : 24,
                                  fontWeight: semiBold,
                                  color: (context
                                              .read<
                                                  BiometricCaptureControlProvider>()
                                              .biometricCaptureScanBlockTabIndex ==
                                          1)
                                      ? pureWhite
                                      : blackShade1),
                            ),
                          ),
                        ),
                      ),
                    ),
                    Expanded(
                      child: InkWell(
                        onTap: () {
                          context
                              .read<BiometricCaptureControlProvider>()
                              .biometricCaptureScanBlockTabIndex = 2;
                        },
                        child: Container(
                          decoration: BoxDecoration(
                            color: (context
                                        .read<BiometricCaptureControlProvider>()
                                        .biometricCaptureScanBlockTabIndex ==
                                    2)
                                ? solidPrimary
                                : pureWhite,
                            border: (context
                                        .read<BiometricCaptureControlProvider>()
                                        .biometricCaptureScanBlockTabIndex ==
                                    2)
                                ? const Border()
                                : Border(
                                    bottom: BorderSide(
                                        color: solidPrimary, width: 3),
                                  ),
                          ),
                          height: 84,
                          child: Center(
                            child: Text(
                              AppLocalizations.of(context)!.mark_exception,
                              style: TextStyle(
                                  fontSize: (isMobileSize) ? 18 : 24,
                                  fontWeight: semiBold,
                                  color: (context
                                              .read<
                                                  BiometricCaptureControlProvider>()
                                              .biometricCaptureScanBlockTabIndex ==
                                          2)
                                      ? pureWhite
                                      : blackShade1),
                            ),
                          ),
                        ),
                      ),
                    )
                  ],
                ),
                const SizedBox(
                  height: 40,
                ),
                (context
                            .read<BiometricCaptureControlProvider>()
                            .biometricCaptureScanBlockTabIndex ==
                        1)
                    ? _scanBlock()
                    : _exceptionBlock()
              ],
            ),
          ),
        ),
      ),
    );
  }
}
