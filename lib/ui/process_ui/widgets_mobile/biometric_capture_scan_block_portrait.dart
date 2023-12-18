import 'dart:convert';

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:percent_indicator/linear_percent_indicator.dart';
import 'package:provider/provider.dart';
import 'package:registration_client/model/biometric_attribute_data.dart';
import 'package:registration_client/model/biometrics_dto.dart';
import 'package:registration_client/model/field.dart';
import 'package:registration_client/pigeon/biometrics_pigeon.dart';
import 'package:registration_client/provider/biometric_capture_control_provider.dart';
import 'package:registration_client/provider/global_provider.dart';
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
  @override
  void initState() {
    context
        .read<BiometricCaptureControlProvider>()
        .biometricCaptureScanBlockTabIndex = 1;
    if (context
            .read<BiometricCaptureControlProvider>()
            .biometricAttributePortrait ==
        "Iris") {
      biometricAttributeData =
          context.read<BiometricCaptureControlProvider>().iris;
    }
    if (context
            .read<BiometricCaptureControlProvider>()
            .biometricAttributePortrait ==
        "Right Hand") {
      biometricAttributeData =
          context.read<BiometricCaptureControlProvider>().rightHand;
    }
    if (context
            .read<BiometricCaptureControlProvider>()
            .biometricAttributePortrait ==
        "Left Hand") {
      biometricAttributeData =
          context.read<BiometricCaptureControlProvider>().leftHand;
    }
    if (context
            .read<BiometricCaptureControlProvider>()
            .biometricAttributePortrait ==
        "Thumbs") {
      biometricAttributeData =
          context.read<BiometricCaptureControlProvider>().thumbs;
    }
    if (context
            .read<BiometricCaptureControlProvider>()
            .biometricAttributePortrait ==
        "Face") {
      biometricAttributeData =
          context.read<BiometricCaptureControlProvider>().face;
    }
    if (context
            .read<BiometricCaptureControlProvider>()
            .biometricAttributePortrait ==
        "Exception") {
      biometricAttributeData =
          context.read<BiometricCaptureControlProvider>().exception;
    }
    super.initState();
  }

  _showScanDialogBox(List<Uint8List?> temp) async {
    int currentAttemptNo = await BiometricsApi().getBioAttempt(
        widget.field.id!, biometricAttributeData.title.replaceAll(" ", ""));
    showDialog<String>(
      context: context,
      builder: (BuildContext context) => AlertDialog(
        content: SizedBox(
          height: 610,
          width: 760,
          child: Column(
            // mainAxisAlignment: MainAxisAlignment.spaceBetween,
            children: [
              const SizedBox(
                height: 26,
              ),
              Text(
                "${biometricAttributeData.title.replaceAll(" ", "")} Capture",
                style: Theme.of(context).textTheme.bodyLarge?.copyWith(
                    fontSize: 28, fontWeight: bold, color: blackShade1),
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
                      height: 275,
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
                    "${biometricAttributeData.noOfCapturesAllowed - currentAttemptNo} attempts left",
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
    );
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
          height: 353,
          child: (biometricAttributeData.isScanned == false)
              ? Row(
                  mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                  children: [
                    ...biometricAttributeData.listofImages
                        .map((e) => Image.asset(
                              "${e}",
                              height: 109,
                            ))
                  ],
                )
              : Row(
                  mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                  children: [
                    ...biometricAttributeData.listofImages
                        .map((e) => Image.memory(
                              e,
                              height: 109,
                            ))
                  ],
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
          height: 315,
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.center,
            children: [
              const SizedBox(
                width: double.infinity,
              ),
              Padding(
                padding: const EdgeInsets.fromLTRB(0, 20, 0, 18),
                child: Text(
                  "Quality",
                  style: TextStyle(
                      fontSize: 24, color: blackShade1, fontWeight: semiBold),
                ),
              ),
              Text(
                "Threshold ${biometricAttributeData.thresholdPercentage}%",
                style: Theme.of(context).textTheme.bodyLarge?.copyWith(
                      fontSize: 23,
                      fontWeight: regular,
                      color: secondaryColors.elementAt(1),
                    ),
              ),
              const SizedBox(
                height: 21,
              ),
              Row(
                mainAxisAlignment: MainAxisAlignment.center,
                children: [
                  LinearPercentIndicator(
                    width: 599,
                    lineHeight: 15.4,
                    percent: biometricAttributeData.qualityPercentage / 100,
                    backgroundColor: Colors.grey,
                    progressColor:
                        (biometricAttributeData.qualityPercentage.toInt() <
                                int.parse(
                                    biometricAttributeData.thresholdPercentage))
                            ? secondaryColors.elementAt(20)
                            : secondaryColors.elementAt(11),
                  ),
                  SizedBox(
                    width: 43,
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
              const SizedBox(
                height: 26,
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
                height: 6,
              ),
              Column(
                crossAxisAlignment: CrossAxisAlignment.center,
                children: [
                  Text(
                    "Attempts",
                    style: Theme.of(context).textTheme.bodyLarge?.copyWith(
                        fontSize: 24, color: blackShade1, fontWeight: semiBold),
                  ),
                  SizedBox(
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
            ],
          ),
        ),
        SizedBox(
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
                biometricAttributeData.qualityPercentage = context
                    .read<BiometricCaptureControlProvider>()
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
            "SCAN",
            style: Theme.of(context)
                .textTheme
                .bodySmall
                ?.copyWith(fontSize: 27, fontWeight: bold, color: pureWhite),
          ),
          style: OutlinedButton.styleFrom(
            padding: EdgeInsets.symmetric(horizontal: 46, vertical: 34),
            backgroundColor: (biometricAttributeData.exceptions.contains(false)
                ? solidPrimary
                : secondaryColors.elementAt(22)),
            shape: RoundedRectangleBorder(
              borderRadius: BorderRadius.circular(5),
            ),
          ),
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
        Container(
          decoration: BoxDecoration(
            color: pureWhite,
            border: Border.all(color: secondaryColors.elementAt(14), width: 1),
          ),
          height: 353,
          child: _returnExceptionImage(),
        ),
        const SizedBox(
          height: 40,
        ),
        Text(
          "Exception Type",
          style:
              TextStyle(fontSize: 25, fontWeight: semiBold, color: blackShade1),
        ),
        const SizedBox(
          height: 18,
        ),
        Row(
          children: [
            OutlinedButton(
              onPressed: () {
                if (biometricAttributeData.exceptions.contains(true)) {
                  biometricAttributeData.exceptionType = "Permanent";
                }
              },
              style: ButtonStyle(
                backgroundColor: MaterialStateProperty.all<Color>(
                    (biometricAttributeData.exceptionType == "Permanent")
                        ? secondaryColors.elementAt(12)
                        : pureWhite),
                shape: MaterialStateProperty.all<OutlinedBorder>(
                  RoundedRectangleBorder(
                    borderRadius: BorderRadius.circular(36),
                  ),
                ),
                side: MaterialStateProperty.all<BorderSide>(
                    BorderSide(color: secondaryColors.elementAt(12))),
                padding: MaterialStateProperty.all<EdgeInsetsGeometry>(
                  const EdgeInsets.symmetric(horizontal: 109, vertical: 20),
                ),
              ),
              child: Text(
                "Permanent",
                style: TextStyle(
                    fontSize: 24,
                    fontWeight: FontWeight.w400,
                    color: (biometricAttributeData.exceptionType == "Permanent")
                        ? pureWhite
                        : blackShade1),
              ),
            ),
            const SizedBox(
              width: 30,
            ),
            OutlinedButton(
              onPressed: () {
                if (biometricAttributeData.exceptions.contains(true)) {
                  biometricAttributeData.exceptionType = "Temporary";
                }
              },
              style: ButtonStyle(
                backgroundColor: MaterialStateProperty.all<Color>(
                    (biometricAttributeData.exceptionType == "Temporary")
                        ? secondaryColors.elementAt(12)
                        : pureWhite),
                shape: MaterialStateProperty.all<OutlinedBorder>(
                  RoundedRectangleBorder(
                    borderRadius: BorderRadius.circular(36),
                  ),
                ),
                side: MaterialStateProperty.all<BorderSide>(
                    BorderSide(color: secondaryColors.elementAt(12))),
                padding: MaterialStateProperty.all<EdgeInsetsGeometry>(
                  const EdgeInsets.symmetric(horizontal: 109, vertical: 20),
                ),
              ),
              child: Text(
                "Temporary",
                style: TextStyle(
                    fontSize: 24,
                    fontWeight: FontWeight.w400,
                    color: (biometricAttributeData.exceptionType == "Temporary")
                        ? pureWhite
                        : blackShade1),
              ),
            ),
          ],
        ),
        const SizedBox(
          height: 40,
        ),
        Text(
          "Comments",
          style:
              TextStyle(fontSize: 25, fontWeight: semiBold, color: blackShade1),
        ),
        const SizedBox(
          height: 20,
        ),
        TextField(
          maxLines: 10,
          decoration: InputDecoration(
              fillColor: pureWhite,
              hintText: "Add comments for marking the exception",
              hintStyle: TextStyle(
                  fontSize: 28,
                  fontWeight: regular,
                  color: secondaryColors.elementAt(1)),
              border: OutlineInputBorder(
                borderRadius: BorderRadius.circular(6),
                borderSide: BorderSide(
                  color: secondaryColors.elementAt(12),
                ),
              )),
        ),
      ],
    );
  }

  Widget _returnExceptionImage() {
    if (biometricAttributeData.title == "Iris") {
      return SizedBox(
        height: 164,
        child: Row(mainAxisAlignment: MainAxisAlignment.spaceEvenly, children: [
          Column(
            mainAxisAlignment: MainAxisAlignment.spaceEvenly,
            children: [
              Text("LEFT",
                  style: Theme.of(context).textTheme.bodyLarge?.copyWith(
                      fontSize: 14, fontWeight: semiBold, color: blackShade1)),
              Stack(
                children: [
                  InkWell(
                    onTap: () async {
                      if (!(context
                              .read<BiometricCaptureControlProvider>()
                              .iris
                              .exceptions
                              .elementAt(0)) ==
                          true) {
                        await BiometricsApi().addBioException(
                            widget.field.id!, "Iris", "leftEye");
                        resetAfterException(
                            widget.field.id!,
                            context
                                .read<BiometricCaptureControlProvider>()
                                .iris);
                        context
                            .read<BiometricCaptureControlProvider>()
                            .customSetterIris(false, "isScanned");
                        context
                            .read<BiometricCaptureControlProvider>()
                            .customSetterIris(0, "attemptNo");
                        context
                            .read<BiometricCaptureControlProvider>()
                            .customSetterIris([
                          "assets/images/Left Eye@2x.png",
                          "assets/images/Right Eye@2x.png"
                        ], "listofImages");
                        List<BiometricsDto> listOfBiometrics = [];
                        context
                            .read<BiometricCaptureControlProvider>()
                            .customSetterIris(
                                listOfBiometrics, "listOfBiometricsDto");
                        context
                            .read<BiometricCaptureControlProvider>()
                            .customSetterIris(0.0, "qualityPercentage");
                        context
                            .read<BiometricCaptureControlProvider>()
                            .customSetterIris("0", "thresholdPercentage");
                      } else {
                        await BiometricsApi().removeBioException(
                            widget.field.id!, "Iris", "leftEye");
                        resetAfterException(
                            widget.field.id!,
                            context
                                .read<BiometricCaptureControlProvider>()
                                .iris);
                        context
                            .read<BiometricCaptureControlProvider>()
                            .customSetterIris(false, "isScanned");
                        context
                            .read<BiometricCaptureControlProvider>()
                            .customSetterIris(0, "attemptNo");
                        context
                            .read<BiometricCaptureControlProvider>()
                            .customSetterIris([
                          "assets/images/Left Eye@2x.png",
                          "assets/images/Right Eye@2x.png"
                        ], "listofImages");
                        List<BiometricsDto> listOfBiometrics = [];
                        context
                            .read<BiometricCaptureControlProvider>()
                            .customSetterIris(
                                listOfBiometrics, "listOfBiometricsDto");
                        context
                            .read<BiometricCaptureControlProvider>()
                            .customSetterIris(0, "qualityPercentage");
                        context
                            .read<BiometricCaptureControlProvider>()
                            .customSetterIris("0", "thresholdPercentage");
                      }
                      List<bool> exceptionListCopy = context
                          .read<BiometricCaptureControlProvider>()
                          .iris
                          .exceptions;
                      exceptionListCopy[0] = !(exceptionListCopy.elementAt(0));
                      context
                          .read<BiometricCaptureControlProvider>()
                          .customSetterIris(exceptionListCopy, "exceptions");

                      if (context
                          .read<BiometricCaptureControlProvider>()
                          .iris
                          .exceptions
                          .contains(true)) {
                        if (context
                            .read<BiometricCaptureControlProvider>()
                            .iris
                            .exceptionType
                            .isEmpty) {
                          context
                              .read<BiometricCaptureControlProvider>()
                              .customSetterIris("Permanent", "exceptionType");
                        }
                      }
                      if (!context
                          .read<BiometricCaptureControlProvider>()
                          .iris
                          .exceptions
                          .contains(true)) {
                        context
                            .read<BiometricCaptureControlProvider>()
                            .customSetterIris("", "exceptionType");
                      }
                    },
                    child: Image.asset(
                      "assets/images/Left Eye@2x.png",
                      height: 72,
                    ),
                  ),
                  (context
                              .read<BiometricCaptureControlProvider>()
                              .iris
                              .exceptions[0] ==
                          true)
                      ? Positioned(
                          top: 0,
                          left: 0,
                          child: Icon(
                            Icons.cancel_rounded,
                            color: secondaryColors.elementAt(15),
                            size: 30,
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
                  style: Theme.of(context).textTheme.bodyLarge?.copyWith(
                      fontSize: 14, fontWeight: semiBold, color: blackShade1)),
              Stack(
                children: [
                  InkWell(
                    onTap: () async {
                      if (!(context
                              .read<BiometricCaptureControlProvider>()
                              .iris
                              .exceptions
                              .elementAt(1)) ==
                          true) {
                        await BiometricsApi().addBioException(
                            widget.field.id!, "Iris", "rightEye");
                        resetAfterException(
                            widget.field.id!,
                            context
                                .read<BiometricCaptureControlProvider>()
                                .iris);
                        context
                            .read<BiometricCaptureControlProvider>()
                            .customSetterIris(false, "isScanned");
                        context
                            .read<BiometricCaptureControlProvider>()
                            .customSetterIris(0, "attemptNo");
                        context
                            .read<BiometricCaptureControlProvider>()
                            .customSetterIris([
                          "assets/images/Left Eye@2x.png",
                          "assets/images/Right Eye@2x.png"
                        ], "listofImages");
                        List<BiometricsDto> listOfBiometrics = [];
                        context
                            .read<BiometricCaptureControlProvider>()
                            .customSetterIris(
                                listOfBiometrics, "listOfBiometricsDto");
                        context
                            .read<BiometricCaptureControlProvider>()
                            .customSetterIris(0, "qualityPercentage");
                        context
                            .read<BiometricCaptureControlProvider>()
                            .customSetterIris("0", "thresholdPercentage");
                      } else {
                        await BiometricsApi().removeBioException(
                            widget.field.id!, "Iris", "rightEye");
                        resetAfterException(
                            widget.field.id!,
                            context
                                .read<BiometricCaptureControlProvider>()
                                .iris);
                        context
                            .read<BiometricCaptureControlProvider>()
                            .customSetterIris(false, "isScanned");
                        context
                            .read<BiometricCaptureControlProvider>()
                            .customSetterIris(0, "attemptNo");
                        context
                            .read<BiometricCaptureControlProvider>()
                            .customSetterIris([
                          "assets/images/Left Eye@2x.png",
                          "assets/images/Right Eye@2x.png"
                        ], "listofImages");
                        List<BiometricsDto> listOfBiometrics = [];
                        context
                            .read<BiometricCaptureControlProvider>()
                            .customSetterIris(
                                listOfBiometrics, "listOfBiometricsDto");
                        context
                            .read<BiometricCaptureControlProvider>()
                            .customSetterIris(0, "qualityPercentage");
                        context
                            .read<BiometricCaptureControlProvider>()
                            .customSetterIris("0", "thresholdPercentage");
                      }

                      List<bool> exceptionListCopy = context
                          .read<BiometricCaptureControlProvider>()
                          .iris
                          .exceptions;
                      exceptionListCopy[1] = !(exceptionListCopy.elementAt(1));
                      context
                          .read<BiometricCaptureControlProvider>()
                          .customSetterIris(exceptionListCopy, "exceptions");
                      if (context
                          .read<BiometricCaptureControlProvider>()
                          .iris
                          .exceptions
                          .contains(true)) {
                        if (context
                            .read<BiometricCaptureControlProvider>()
                            .iris
                            .exceptionType
                            .isEmpty) {
                          context
                              .read<BiometricCaptureControlProvider>()
                              .customSetterIris("Permanent", "exceptionType");
                        }
                      }
                      if (!context
                          .read<BiometricCaptureControlProvider>()
                          .iris
                          .exceptions
                          .contains(true)) {
                        context
                            .read<BiometricCaptureControlProvider>()
                            .customSetterIris("", "exceptionType");
                      }
                    },
                    child: Image.asset(
                      "assets/images/Right Eye@2x.png",
                      height: 72,
                    ),
                  ),
                  (context
                              .read<BiometricCaptureControlProvider>()
                              .iris
                              .exceptions[1] ==
                          true)
                      ? Positioned(
                          top: 0,
                          right: 0,
                          child: Icon(
                            Icons.cancel_rounded,
                            color: secondaryColors.elementAt(15),
                            size: 30,
                          ))
                      : Container(),
                ],
              )
            ],
          ),
        ]),
      );
    }
    if (biometricAttributeData.title == "Right Hand") {
      return Center(
        child: Container(
          // color: Colors.amber,
          height: 400,
          child: Image.asset(
            "assets/images/Right Hand@2x.png",
            fit: BoxFit.fitHeight,
          ),
        ),
      );
    }
    if (biometricAttributeData.title == "Left Hand") {
      return SizedBox(
        height: 164,
        child: Row(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Column(
              mainAxisAlignment: MainAxisAlignment.spaceEvenly,
              children: [
                Text("Left Hand",
                    style: Theme.of(context).textTheme.bodyLarge?.copyWith(
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
                                    .read<BiometricCaptureControlProvider>()
                                    .leftHand
                                    .exceptions
                                    .elementAt(0)) ==
                                true) {
                              await BiometricsApi().addBioException(
                                  widget.field.id!, "LeftHand", "leftIndex");
                              resetAfterException(
                                  widget.field.id!,
                                  context
                                      .read<BiometricCaptureControlProvider>()
                                      .leftHand);
                              context
                                  .read<BiometricCaptureControlProvider>()
                                  .customSetterLeftHand(false, "isScanned");
                              context
                                  .read<BiometricCaptureControlProvider>()
                                  .customSetterLeftHand(0, "attemptNo");
                              context
                                  .read<BiometricCaptureControlProvider>()
                                  .customSetterLeftHand(
                                      ["assets/images/Left Hand@2x.png"],
                                      "listofImages");
                              List<BiometricsDto> listOfBiometrics = [];
                              context
                                  .read<BiometricCaptureControlProvider>()
                                  .customSetterLeftHand(
                                      listOfBiometrics, "listOfBiometricsDto");
                              context
                                  .read<BiometricCaptureControlProvider>()
                                  .customSetterLeftHand(0, "qualityPercentage");
                              context
                                  .read<BiometricCaptureControlProvider>()
                                  .customSetterLeftHand(
                                      "0", "thresholdPercentage");
                            } else {
                              await BiometricsApi().removeBioException(
                                  widget.field.id!, "LeftHand", "leftIndex");
                              resetAfterException(
                                  widget.field.id!,
                                  context
                                      .read<BiometricCaptureControlProvider>()
                                      .leftHand);
                              context
                                  .read<BiometricCaptureControlProvider>()
                                  .customSetterLeftHand(false, "isScanned");
                              context
                                  .read<BiometricCaptureControlProvider>()
                                  .customSetterLeftHand(0, "attemptNo");
                              context
                                  .read<BiometricCaptureControlProvider>()
                                  .customSetterLeftHand(
                                      ["assets/images/Left Hand@2x.png"],
                                      "listofImages");
                              List<BiometricsDto> listOfBiometrics = [];
                              context
                                  .read<BiometricCaptureControlProvider>()
                                  .customSetterLeftHand(
                                      listOfBiometrics, "listOfBiometricsDto");
                              context
                                  .read<BiometricCaptureControlProvider>()
                                  .customSetterLeftHand(0, "qualityPercentage");
                              context
                                  .read<BiometricCaptureControlProvider>()
                                  .customSetterLeftHand(
                                      "0", "thresholdPercentage");
                            }
                            List<bool> exceptionListCopy = context
                                .read<BiometricCaptureControlProvider>()
                                .leftHand
                                .exceptions;
                            exceptionListCopy[0] =
                                !(exceptionListCopy.elementAt(0));
                            context
                                .read<BiometricCaptureControlProvider>()
                                .customSetterLeftHand(
                                    exceptionListCopy, "exceptions");

                            if (context
                                .read<BiometricCaptureControlProvider>()
                                .leftHand
                                .exceptions
                                .contains(true)) {
                              if (context
                                  .read<BiometricCaptureControlProvider>()
                                  .leftHand
                                  .exceptionType
                                  .isEmpty) {
                                context
                                    .read<BiometricCaptureControlProvider>()
                                    .customSetterLeftHand(
                                        "Permanent", "exceptionType");
                              }
                            }
                            if (!context
                                .read<BiometricCaptureControlProvider>()
                                .leftHand
                                .exceptions
                                .contains(true)) {
                              context
                                  .read<BiometricCaptureControlProvider>()
                                  .customSetterLeftHand("", "exceptionType");
                            }
                          },
                          child: Icon(
                            Icons.cancel_rounded,
                            color: (context
                                        .read<BiometricCaptureControlProvider>()
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
                                    .read<BiometricCaptureControlProvider>()
                                    .leftHand
                                    .exceptions
                                    .elementAt(1)) ==
                                true) {
                              await BiometricsApi().addBioException(
                                  widget.field.id!, "LeftHand", "leftMiddle");
                              resetAfterException(
                                  widget.field.id!,
                                  context
                                      .read<BiometricCaptureControlProvider>()
                                      .leftHand);
                              context
                                  .read<BiometricCaptureControlProvider>()
                                  .customSetterLeftHand(false, "isScanned");
                              context
                                  .read<BiometricCaptureControlProvider>()
                                  .customSetterLeftHand(0, "attemptNo");
                              context
                                  .read<BiometricCaptureControlProvider>()
                                  .customSetterLeftHand(
                                      ["assets/images/Left Hand@2x.png"],
                                      "listofImages");
                              List<BiometricsDto> listOfBiometrics = [];
                              context
                                  .read<BiometricCaptureControlProvider>()
                                  .customSetterLeftHand(
                                      listOfBiometrics, "listOfBiometricsDto");
                              context
                                  .read<BiometricCaptureControlProvider>()
                                  .customSetterLeftHand(0, "qualityPercentage");
                              context
                                  .read<BiometricCaptureControlProvider>()
                                  .customSetterLeftHand(
                                      "0", "thresholdPercentage");
                            } else {
                              await BiometricsApi().removeBioException(
                                  widget.field.id!, "LeftHand", "leftMiddle");
                              resetAfterException(
                                  widget.field.id!,
                                  context
                                      .read<BiometricCaptureControlProvider>()
                                      .leftHand);
                              context
                                  .read<BiometricCaptureControlProvider>()
                                  .customSetterLeftHand(false, "isScanned");
                              context
                                  .read<BiometricCaptureControlProvider>()
                                  .customSetterLeftHand(0, "attemptNo");
                              context
                                  .read<BiometricCaptureControlProvider>()
                                  .customSetterLeftHand(
                                      ["assets/images/Left Hand@2x.png"],
                                      "listofImages");
                              List<BiometricsDto> listOfBiometrics = [];
                              context
                                  .read<BiometricCaptureControlProvider>()
                                  .customSetterLeftHand(
                                      listOfBiometrics, "listOfBiometricsDto");
                              context
                                  .read<BiometricCaptureControlProvider>()
                                  .customSetterLeftHand(0, "qualityPercentage");
                              context
                                  .read<BiometricCaptureControlProvider>()
                                  .customSetterLeftHand(
                                      "0", "thresholdPercentage");
                            }
                            List<bool> exceptionListCopy = context
                                .read<BiometricCaptureControlProvider>()
                                .leftHand
                                .exceptions;
                            exceptionListCopy[1] =
                                !(exceptionListCopy.elementAt(1));
                            context
                                .read<BiometricCaptureControlProvider>()
                                .customSetterLeftHand(
                                    exceptionListCopy, "exceptions");

                            if (context
                                .read<BiometricCaptureControlProvider>()
                                .leftHand
                                .exceptions
                                .contains(true)) {
                              if (context
                                  .read<BiometricCaptureControlProvider>()
                                  .leftHand
                                  .exceptionType
                                  .isEmpty) {
                                context
                                    .read<BiometricCaptureControlProvider>()
                                    .customSetterLeftHand(
                                        "Permanent", "exceptionType");
                              }
                            }
                            if (!context
                                .read<BiometricCaptureControlProvider>()
                                .leftHand
                                .exceptions
                                .contains(true)) {
                              context
                                  .read<BiometricCaptureControlProvider>()
                                  .customSetterLeftHand("", "exceptionType");
                            }
                          },
                          child: Icon(
                            Icons.cancel_rounded,
                            color: (context
                                        .read<BiometricCaptureControlProvider>()
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
                                    .read<BiometricCaptureControlProvider>()
                                    .leftHand
                                    .exceptions
                                    .elementAt(2)) ==
                                true) {
                              await BiometricsApi().addBioException(
                                  widget.field.id!, "LeftHand", "leftRing");
                              resetAfterException(
                                  widget.field.id!,
                                  context
                                      .read<BiometricCaptureControlProvider>()
                                      .leftHand);
                              context
                                  .read<BiometricCaptureControlProvider>()
                                  .customSetterLeftHand(false, "isScanned");
                              context
                                  .read<BiometricCaptureControlProvider>()
                                  .customSetterLeftHand(0, "attemptNo");
                              context
                                  .read<BiometricCaptureControlProvider>()
                                  .customSetterLeftHand(
                                      ["assets/images/Left Hand@2x.png"],
                                      "listofImages");
                              List<BiometricsDto> listOfBiometrics = [];
                              context
                                  .read<BiometricCaptureControlProvider>()
                                  .customSetterLeftHand(
                                      listOfBiometrics, "listOfBiometricsDto");
                              context
                                  .read<BiometricCaptureControlProvider>()
                                  .customSetterLeftHand(0, "qualityPercentage");
                              context
                                  .read<BiometricCaptureControlProvider>()
                                  .customSetterLeftHand(
                                      "0", "thresholdPercentage");
                            } else {
                              await BiometricsApi().removeBioException(
                                  widget.field.id!, "LeftHand", "leftRing");
                              resetAfterException(
                                  widget.field.id!,
                                  context
                                      .read<BiometricCaptureControlProvider>()
                                      .leftHand);
                              context
                                  .read<BiometricCaptureControlProvider>()
                                  .customSetterLeftHand(false, "isScanned");
                              context
                                  .read<BiometricCaptureControlProvider>()
                                  .customSetterLeftHand(0, "attemptNo");
                              context
                                  .read<BiometricCaptureControlProvider>()
                                  .customSetterLeftHand(
                                      ["assets/images/Left Hand@2x.png"],
                                      "listofImages");
                              List<BiometricsDto> listOfBiometrics = [];
                              context
                                  .read<BiometricCaptureControlProvider>()
                                  .customSetterLeftHand(
                                      listOfBiometrics, "listOfBiometricsDto");
                              context
                                  .read<BiometricCaptureControlProvider>()
                                  .customSetterLeftHand(0, "qualityPercentage");
                              context
                                  .read<BiometricCaptureControlProvider>()
                                  .customSetterLeftHand(
                                      "0", "thresholdPercentage");
                            }
                            List<bool> exceptionListCopy = context
                                .read<BiometricCaptureControlProvider>()
                                .leftHand
                                .exceptions;
                            exceptionListCopy[2] =
                                !(exceptionListCopy.elementAt(2));
                            context
                                .read<BiometricCaptureControlProvider>()
                                .customSetterLeftHand(
                                    exceptionListCopy, "exceptions");

                            if (context
                                .read<BiometricCaptureControlProvider>()
                                .leftHand
                                .exceptions
                                .contains(true)) {
                              if (context
                                  .read<BiometricCaptureControlProvider>()
                                  .leftHand
                                  .exceptionType
                                  .isEmpty) {
                                context
                                    .read<BiometricCaptureControlProvider>()
                                    .customSetterLeftHand(
                                        "Permanent", "exceptionType");
                              }
                            }
                            if (!context
                                .read<BiometricCaptureControlProvider>()
                                .leftHand
                                .exceptions
                                .contains(true)) {
                              context
                                  .read<BiometricCaptureControlProvider>()
                                  .customSetterLeftHand("", "exceptionType");
                            }
                          },
                          child: Icon(
                            Icons.cancel_rounded,
                            color: (context
                                        .read<BiometricCaptureControlProvider>()
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
                                    .read<BiometricCaptureControlProvider>()
                                    .leftHand
                                    .exceptions
                                    .elementAt(3)) ==
                                true) {
                              await BiometricsApi().addBioException(
                                  widget.field.id!, "LeftHand", "leftLittle");
                              resetAfterException(
                                  widget.field.id!,
                                  context
                                      .read<BiometricCaptureControlProvider>()
                                      .leftHand);
                              context
                                  .read<BiometricCaptureControlProvider>()
                                  .customSetterLeftHand(false, "isScanned");
                              context
                                  .read<BiometricCaptureControlProvider>()
                                  .customSetterLeftHand(0, "attemptNo");
                              context
                                  .read<BiometricCaptureControlProvider>()
                                  .customSetterLeftHand(
                                      ["assets/images/Left Hand@2x.png"],
                                      "listofImages");
                              List<BiometricsDto> listOfBiometrics = [];
                              context
                                  .read<BiometricCaptureControlProvider>()
                                  .customSetterLeftHand(
                                      listOfBiometrics, "listOfBiometricsDto");
                              context
                                  .read<BiometricCaptureControlProvider>()
                                  .customSetterLeftHand(0, "qualityPercentage");
                              context
                                  .read<BiometricCaptureControlProvider>()
                                  .customSetterLeftHand(
                                      "0", "thresholdPercentage");
                            } else {
                              await BiometricsApi().removeBioException(
                                  widget.field.id!, "LeftHand", "leftLittle");
                              resetAfterException(
                                  widget.field.id!,
                                  context
                                      .read<BiometricCaptureControlProvider>()
                                      .leftHand);
                              context
                                  .read<BiometricCaptureControlProvider>()
                                  .customSetterLeftHand(false, "isScanned");
                              context
                                  .read<BiometricCaptureControlProvider>()
                                  .customSetterLeftHand(0, "attemptNo");
                              context
                                  .read<BiometricCaptureControlProvider>()
                                  .customSetterLeftHand(
                                      ["assets/images/Left Hand@2x.png"],
                                      "listofImages");
                              List<BiometricsDto> listOfBiometrics = [];
                              context
                                  .read<BiometricCaptureControlProvider>()
                                  .customSetterLeftHand(
                                      listOfBiometrics, "listOfBiometricsDto");
                              context
                                  .read<BiometricCaptureControlProvider>()
                                  .customSetterLeftHand(0, "qualityPercentage");
                              context
                                  .read<BiometricCaptureControlProvider>()
                                  .customSetterLeftHand(
                                      "0", "thresholdPercentage");
                            }
                            List<bool> exceptionListCopy = context
                                .read<BiometricCaptureControlProvider>()
                                .leftHand
                                .exceptions;
                            exceptionListCopy[3] =
                                !(exceptionListCopy.elementAt(3));
                            context
                                .read<BiometricCaptureControlProvider>()
                                .customSetterLeftHand(
                                    exceptionListCopy, "exceptions");

                            if (context
                                .read<BiometricCaptureControlProvider>()
                                .leftHand
                                .exceptions
                                .contains(true)) {
                              if (context
                                  .read<BiometricCaptureControlProvider>()
                                  .leftHand
                                  .exceptionType
                                  .isEmpty) {
                                context
                                    .read<BiometricCaptureControlProvider>()
                                    .customSetterLeftHand(
                                        "Permanent", "exceptionType");
                              }
                            }
                            if (!context
                                .read<BiometricCaptureControlProvider>()
                                .leftHand
                                .exceptions
                                .contains(true)) {
                              context
                                  .read<BiometricCaptureControlProvider>()
                                  .customSetterLeftHand("", "exceptionType");
                            }
                          },
                          child: Icon(
                            Icons.cancel_rounded,
                            color: (context
                                        .read<BiometricCaptureControlProvider>()
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
      );
    }
    if (biometricAttributeData.title == "Thumbs") {
      return SizedBox(
        height: 164,
        child: Row(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Column(
              mainAxisAlignment: MainAxisAlignment.spaceEvenly,
              children: [
                Text("Thumbs",
                    style: Theme.of(context).textTheme.bodyLarge?.copyWith(
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
                                    .read<BiometricCaptureControlProvider>()
                                    .thumbs
                                    .exceptions
                                    .elementAt(0)) ==
                                true) {
                              await BiometricsApi().addBioException(
                                  widget.field.id!, "Thumbs", "leftThumb");
                              resetAfterException(
                                  widget.field.id!,
                                  context
                                      .read<BiometricCaptureControlProvider>()
                                      .thumbs);

                              context
                                  .read<BiometricCaptureControlProvider>()
                                  .customSetterThumbs(false, "isScanned");
                              context
                                  .read<BiometricCaptureControlProvider>()
                                  .customSetterThumbs(0, "attemptNo");
                              context
                                  .read<BiometricCaptureControlProvider>()
                                  .customSetterThumbs(
                                      ["assets/images/Thumbs@2x.png"],
                                      "listofImages");
                              List<BiometricsDto> listOfBiometrics = [];
                              context
                                  .read<BiometricCaptureControlProvider>()
                                  .customSetterThumbs(
                                      listOfBiometrics, "listOfBiometricsDto");
                              context
                                  .read<BiometricCaptureControlProvider>()
                                  .customSetterThumbs(0, "qualityPercentage");
                              context
                                  .read<BiometricCaptureControlProvider>()
                                  .customSetterThumbs(
                                      "0", "thresholdPercentage");
                            } else {
                              await BiometricsApi().removeBioException(
                                  widget.field.id!, "Thumbs", "leftThumb");
                              resetAfterException(
                                  widget.field.id!,
                                  context
                                      .read<BiometricCaptureControlProvider>()
                                      .thumbs);
                              context
                                  .read<BiometricCaptureControlProvider>()
                                  .customSetterThumbs(false, "isScanned");
                              context
                                  .read<BiometricCaptureControlProvider>()
                                  .customSetterThumbs(0, "attemptNo");
                              context
                                  .read<BiometricCaptureControlProvider>()
                                  .customSetterThumbs(
                                      ["assets/images/Thumbs@2x.png"],
                                      "listofImages");
                              List<BiometricsDto> listOfBiometrics = [];
                              context
                                  .read<BiometricCaptureControlProvider>()
                                  .customSetterThumbs(
                                      listOfBiometrics, "listOfBiometricsDto");
                              context
                                  .read<BiometricCaptureControlProvider>()
                                  .customSetterThumbs(0, "qualityPercentage");
                              context
                                  .read<BiometricCaptureControlProvider>()
                                  .customSetterThumbs(
                                      "0", "thresholdPercentage");
                            }

                            List<bool> exceptionListCopy = context
                                .read<BiometricCaptureControlProvider>()
                                .thumbs
                                .exceptions;
                            exceptionListCopy[0] =
                                !(exceptionListCopy.elementAt(0));
                            context
                                .read<BiometricCaptureControlProvider>()
                                .customSetterThumbs(
                                    exceptionListCopy, "exceptions");
                            if (context
                                .read<BiometricCaptureControlProvider>()
                                .thumbs
                                .exceptions
                                .contains(true)) {
                              if (context
                                  .read<BiometricCaptureControlProvider>()
                                  .thumbs
                                  .exceptionType
                                  .isEmpty) {
                                context
                                    .read<BiometricCaptureControlProvider>()
                                    .customSetterThumbs(
                                        "Permanent", "exceptionType");
                              }
                            }
                            if (!context
                                .read<BiometricCaptureControlProvider>()
                                .thumbs
                                .exceptions
                                .contains(true)) {
                              context
                                  .read<BiometricCaptureControlProvider>()
                                  .customSetterThumbs("", "exceptionType");
                            }
                          },
                          child: Icon(
                            Icons.cancel_rounded,
                            color: (context
                                        .read<BiometricCaptureControlProvider>()
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
                                    .read<BiometricCaptureControlProvider>()
                                    .thumbs
                                    .exceptions
                                    .elementAt(1)) ==
                                true) {
                              await BiometricsApi().addBioException(
                                  widget.field.id!, "Thumbs", "rightThumb");
                              resetAfterException(
                                  widget.field.id!,
                                  context
                                      .read<BiometricCaptureControlProvider>()
                                      .thumbs);
                              context
                                  .read<BiometricCaptureControlProvider>()
                                  .customSetterThumbs(false, "isScanned");
                              context
                                  .read<BiometricCaptureControlProvider>()
                                  .customSetterThumbs(0, "attemptNo");
                              context
                                  .read<BiometricCaptureControlProvider>()
                                  .customSetterThumbs(
                                      ["assets/images/Thumbs@2x.png"],
                                      "listofImages");
                              List<BiometricsDto> listOfBiometrics = [];
                              context
                                  .read<BiometricCaptureControlProvider>()
                                  .customSetterThumbs(
                                      listOfBiometrics, "listOfBiometricsDto");
                              context
                                  .read<BiometricCaptureControlProvider>()
                                  .customSetterThumbs(0, "qualityPercentage");
                              context
                                  .read<BiometricCaptureControlProvider>()
                                  .customSetterThumbs(
                                      "0", "thresholdPercentage");
                            } else {
                              await BiometricsApi().removeBioException(
                                  widget.field.id!, "Thumbs", "rightThumb");
                              resetAfterException(
                                  widget.field.id!,
                                  context
                                      .read<BiometricCaptureControlProvider>()
                                      .thumbs);
                              context
                                  .read<BiometricCaptureControlProvider>()
                                  .customSetterThumbs(false, "isScanned");
                              context
                                  .read<BiometricCaptureControlProvider>()
                                  .customSetterThumbs(0, "attemptNo");
                              context
                                  .read<BiometricCaptureControlProvider>()
                                  .customSetterThumbs(
                                      ["assets/images/Thumbs@2x.png"],
                                      "listofImages");
                              List<BiometricsDto> listOfBiometrics = [];
                              context
                                  .read<BiometricCaptureControlProvider>()
                                  .customSetterThumbs(
                                      listOfBiometrics, "listOfBiometricsDto");
                              context
                                  .read<BiometricCaptureControlProvider>()
                                  .customSetterThumbs(0, "qualityPercentage");
                              context
                                  .read<BiometricCaptureControlProvider>()
                                  .customSetterThumbs(
                                      "0", "thresholdPercentage");
                            }
                            List<bool> exceptionListCopy = context
                                .read<BiometricCaptureControlProvider>()
                                .thumbs
                                .exceptions;
                            exceptionListCopy[1] =
                                !(exceptionListCopy.elementAt(1));
                            context
                                .read<BiometricCaptureControlProvider>()
                                .customSetterThumbs(
                                    exceptionListCopy, "exceptions");
                            if (context
                                .read<BiometricCaptureControlProvider>()
                                .thumbs
                                .exceptions
                                .contains(true)) {
                              if (context
                                  .read<BiometricCaptureControlProvider>()
                                  .thumbs
                                  .exceptionType
                                  .isEmpty) {
                                context
                                    .read<BiometricCaptureControlProvider>()
                                    .customSetterThumbs(
                                        "Permanent", "exceptionType");
                              }
                            }
                            if (!context
                                .read<BiometricCaptureControlProvider>()
                                .thumbs
                                .exceptions
                                .contains(true)) {
                              context
                                  .read<BiometricCaptureControlProvider>()
                                  .customSetterThumbs("", "exceptionType");
                            }
                          },
                          child: Icon(
                            Icons.cancel_rounded,
                            color: (context
                                        .read<BiometricCaptureControlProvider>()
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
      );
    }
    if (biometricAttributeData.title == "Face") {
      return SizedBox(
        height: 228,
        child: Row(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Column(
              mainAxisAlignment: MainAxisAlignment.spaceEvenly,
              children: [
                Text("Face",
                    style: Theme.of(context).textTheme.bodyLarge?.copyWith(
                        fontSize: 14,
                        fontWeight: semiBold,
                        color: blackShade1)),
                SizedBox(
                  height: 64,
                ),
                Image.asset(
                  "assets/images/Face@2x.png",
                  height: 114,
                ),
              ],
            ),
          ],
        ),
      );
    }
    if (biometricAttributeData.title == "Exception") {
      return SizedBox(
        height: 228,
        child: Row(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Column(
              mainAxisAlignment: MainAxisAlignment.spaceEvenly,
              children: [
                Text("Exception",
                    style: Theme.of(context).textTheme.bodyLarge?.copyWith(
                        fontSize: 14,
                        fontWeight: semiBold,
                        color: blackShade1)),
                SizedBox(
                  height: 64,
                ),
                Image.asset(
                  "assets/images/Exception@2x.png",
                  height: 114,
                ),
              ],
            ),
          ],
        ),
      );
    }

    return Container();
  }

  late BiometricAttributeData biometricAttributeData;
  @override
  Widget build(BuildContext context) {
    return Padding(
      padding: const EdgeInsets.fromLTRB(20, 40, 20, 0),
      child: Container(
        height: 1000,
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
                            ? Border()
                            : Border(
                                bottom:
                                    BorderSide(color: solidPrimary, width: 3),
                              ),
                      ),
                      height: 84,
                      child: Center(
                        child: Text(
                          "${context.read<BiometricCaptureControlProvider>().biometricAttributePortrait} Scan",
                          style: TextStyle(
                              fontSize: 24,
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
                            ? Border()
                            : Border(
                                bottom:
                                    BorderSide(color: solidPrimary, width: 3),
                              ),
                      ),
                      height: 84,
                      child: Center(
                        child: Text(
                          "Mark Exception",
                          style: TextStyle(
                              fontSize: 24,
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
    );
  }
}
