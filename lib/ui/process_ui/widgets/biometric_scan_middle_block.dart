/*
 * Copyright (c) Modular Open Source Identity Platform
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *
*/

import 'dart:convert';

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:percent_indicator/linear_percent_indicator.dart';
import 'package:provider/provider.dart';
import 'package:registration_client/model/biometric_attribute_data.dart';
import 'package:registration_client/model/biometrics_dto.dart';
import 'package:registration_client/model/field.dart';
import 'package:registration_client/pigeon/biometrics_pigeon.dart';
import 'package:registration_client/utils/app_config.dart';

import '../../../provider/global_provider.dart';
import 'package:flutter_gen/gen_l10n/app_localizations.dart';

class BiometricScanMiddleBlock extends StatefulWidget {
  const BiometricScanMiddleBlock(
      {super.key,
      required this.biometricAttributeData,
      required this.field,
      required this.imageHeight,
      required this.imageWidth,
      required this.parameterTitle});
  final BiometricAttributeData biometricAttributeData;
  final Field field;
  final double imageHeight;
  final double imageWidth;
  final String parameterTitle;
  @override
  State<BiometricScanMiddleBlock> createState() =>
      _BiometricScanMiddleBlockState();
}

class _BiometricScanMiddleBlockState extends State<BiometricScanMiddleBlock> {
  avgScore(List<BiometricsDto> list) {
    double avg = 0;
    int i;
    for (i = 0; i < list.length; i++) {
      avg = avg + list[i].qualityScore!;
    }
    avg = avg / i;
    return avg;
  }

  listOfImages(List<dynamic> images) {
    List<Widget> temp = [];
    for (var e in images) {
      temp.add(
        Container(
          height: 164.h,
          width: 164.h,
          decoration: BoxDecoration(
            color: pureWhite,
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

  noOfTrue(List<bool> list) {
    int i = 0;
    for (var e in list) {
      if (e == true) {
        i++;
      }
    }
    return i;
  }

  getElementPosition(List<BiometricAttributeData> list, String title) {
    for (int i = 0; i < list.length; i++) {
      if (list.elementAt(i).title.compareTo(title) == 0) {
        return i;
      }
    }
    return -1;
  }

  generateList(String key, BiometricAttributeData data) {
    List<BiometricAttributeData> list = [];

    if (context.read<GlobalProvider>().fieldInputValue.containsKey(key)) {
      if (getElementPosition(
              context.read<GlobalProvider>().fieldInputValue[key],
              data.title) ==
          -1) {
        context.read<GlobalProvider>().fieldInputValue[key].add(data);
      } else {
        context.read<GlobalProvider>().fieldInputValue[key].removeAt(
            getElementPosition(
                context.read<GlobalProvider>().fieldInputValue[key],
                data.title));
        context.read<GlobalProvider>().fieldInputValue[key].add(data);
      }
    } else {
      list.add(data);
      context.read<GlobalProvider>().fieldInputValue[key] = list;
    }
  }

  _navigateBack() {
    Navigator.pop(context);
  }

  _showScanDialogBox(List<Uint8List?> temp) {
    showDialog<String>(
      context: context,
      builder: (BuildContext context) => AlertDialog(
        content: SizedBox(
          height: 539.h,
          width: 768.w,
          child: Column(
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            children: [
              Row(
                mainAxisAlignment: MainAxisAlignment.spaceBetween,
                children: [
                  Text(
                    "${widget.biometricAttributeData.viewTitle} ${AppLocalizations.of(context)!.capture}",
                    style: Theme.of(context).textTheme.bodyLarge?.copyWith(
                        fontSize: 18, fontWeight: bold, color: blackShade1),
                  ),
                  IconButton(
                    onPressed: () {
                      Navigator.pop(context);
                    },
                    icon: const Icon(
                      Icons.close,
                    ),
                  ),
                ],
              ),
              const Divider(),
              Padding(
                padding: const EdgeInsets.all(0),
                // EdgeInsets.fromLTRB(
                //     60.w, 35.h, 60.w, 35.h),
                child: Row(
                  mainAxisAlignment: MainAxisAlignment.center,
                  children: [
                    ...temp.map(
                      (e) => Image.memory(
                        e!,
                        height: widget.imageHeight,
                        width: widget.imageWidth,
                      ),
                    ),
                  ],
                ),
              ),
              const Divider(),
              Padding(
                padding: const EdgeInsets.all(0.0),
                child: Row(
                  mainAxisAlignment: MainAxisAlignment.end,
                  children: [
                    OutlinedButton(
                      style: OutlinedButton.styleFrom(
                        maximumSize: Size(160.w, 42.h),
                        minimumSize: Size(160.w, 42.h),
                        side: BorderSide(color: solidPrimary),
                      ),
                      onPressed: () async {
                        if (widget.biometricAttributeData.attemptNo <
                            widget.biometricAttributeData.noOfCapturesAllowed) {
                          await BiometricsApi().invokeDiscoverSbi(
                              widget.field.id!, widget.parameterTitle);
                          await BiometricsApi()
                              .extractImageValues(
                                  widget.field.id!, widget.parameterTitle)
                              .then((value) {
                            temp = value;
                          });
                        }
                      },
                      child: Text(AppLocalizations.of(context)!.rescan),
                    ),
                    SizedBox(
                      width: 10.w,
                    ),
                    ElevatedButton(
                      style: ElevatedButton.styleFrom(
                          maximumSize: Size(160.w, 42.h),
                          minimumSize: Size(160.w, 42.h)),
                      onPressed: () async {
                        widget.biometricAttributeData.listOfBiometricsDto
                            .clear();
                        await BiometricsApi().incrementBioAttempt(
                            widget.field.id!, widget.parameterTitle);
                        widget.biometricAttributeData.attemptNo =
                            await BiometricsApi().getBioAttempt(
                                widget.field.id!, widget.parameterTitle);
                        await BiometricsApi()
                            .getBestBiometrics(
                                widget.field.id!, widget.parameterTitle)
                            .then((value) async {
                          for (var e in value) {
                            widget.biometricAttributeData.listOfBiometricsDto
                                .add(
                              BiometricsDto.fromJson(
                                json.decode(e!),
                              ),
                            );
                          }
                        });
                        widget.biometricAttributeData.qualityPercentage =
                            avgScore(widget
                                .biometricAttributeData.listOfBiometricsDto);
                        await BiometricsApi()
                            .extractImageValues(
                                widget.field.id!, widget.parameterTitle)
                            .then((value) {
                          widget.biometricAttributeData.listofImages = value;
                        });
                        widget.biometricAttributeData.isScanned = true;
                        generateList("${widget.field.id}",
                            widget.biometricAttributeData);

                        setState(() {});
                        _navigateBack();
                      },
                      child: Text(AppLocalizations.of(context)!.save),
                    ),
                  ],
                ),
              )
            ],
          ),
        ),
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    (widget.biometricAttributeData.exceptions.contains(false))
        ? true
        : generateList("${widget.field.id}", widget.biometricAttributeData);
    return SizedBox(
      height: 460.h,
      child: Column(
        mainAxisAlignment: MainAxisAlignment.spaceEvenly,
        children: [
          (widget.biometricAttributeData.isScanned == false)
              ? Row(
                  mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                  children: [
                    ...listOfImages(widget.biometricAttributeData.listofImages)
                        .map(
                      (e) => e,
                    ),
                  ],
                )
              : Row(
                  mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                  children: [
                    ...listOfResultImages(
                            widget.biometricAttributeData.listofImages)
                        .map(
                      (e) => e,
                    ),
                  ],
                ),
          OutlinedButton.icon(
            onPressed: () async {
              if (widget.biometricAttributeData.exceptions.contains(false)) {
                if (widget.biometricAttributeData.attemptNo <
                    widget.biometricAttributeData.noOfCapturesAllowed) {
                  List<Uint8List?> temp = [];
                  await BiometricsApi().invokeDiscoverSbi(
                      widget.field.id!, widget.parameterTitle);
                  await BiometricsApi()
                      .getBestBiometrics(
                          widget.field.id!, widget.parameterTitle)
                      .then((value) {});
                  await BiometricsApi()
                      .extractImageValues(
                          widget.field.id!, widget.parameterTitle)
                      .then((value) {
                    temp = value;
                  });
                  _showScanDialogBox(temp);
                }
              }
            },
            icon: Icon(
              Icons.crop_free,
              color: (widget.biometricAttributeData.exceptions.contains(false))
                  ? solidPrimary
                  : secondaryColors.elementAt(2),
              size: 14,
            ),
            label: Text(
              AppLocalizations.of(context)!.scan,
              style: Theme.of(context).textTheme.bodySmall?.copyWith(
                  fontSize: 14,
                  fontWeight: bold,
                  color:
                      (widget.biometricAttributeData.exceptions.contains(false))
                          ? solidPrimary
                          : secondaryColors.elementAt(2)),
            ),
            style: OutlinedButton.styleFrom(
              side: BorderSide(
                  color:
                      (widget.biometricAttributeData.exceptions.contains(false))
                          ? solidPrimary
                          : secondaryColors.elementAt(2),
                  width: 1),
              shape: RoundedRectangleBorder(
                borderRadius: BorderRadius.circular(5),
              ),
            ),
          ),
          Container(
              height: 67.h,
              width: 162.w,
              decoration: BoxDecoration(
                color: pureWhite,
                border: Border.all(
                  color: secondaryColors.elementAt(14),
                ),
              ),
              padding: const EdgeInsets.all(12),
              child: Row(
                mainAxisAlignment: MainAxisAlignment.spaceBetween,
                children: [
                  Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Text(
                        AppLocalizations.of(context)!.attempts,
                        style: Theme.of(context).textTheme.bodyLarge?.copyWith(
                              fontSize: 14,
                              color: secondaryColors.elementAt(1),
                            ),
                      ),
                      Text("${widget.biometricAttributeData.attemptNo}"),
                    ],
                  ),
                  Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Text(
                        AppLocalizations.of(context)!.exceptions,
                        style: Theme.of(context).textTheme.bodyLarge?.copyWith(
                              fontSize: 14,
                              color: secondaryColors.elementAt(1),
                            ),
                      ),
                      Text(
                          "${noOfTrue(widget.biometricAttributeData.exceptions)}"),
                    ],
                  ),
                ],
              )),
          Container(
            height: 157.h,
            width: 338.h,
            decoration: BoxDecoration(
              color: pureWhite,
              border: Border.all(
                color: secondaryColors.elementAt(14),
              ),
            ),
            child: Column(
              mainAxisAlignment: MainAxisAlignment.spaceEvenly,
              children: [
                Text(
                  AppLocalizations.of(context)!.quality,
                  style: Theme.of(context).textTheme.bodyLarge?.copyWith(
                      fontSize: 14, fontWeight: semiBold, color: blackShade1),
                ),
                Text(
                  "${AppLocalizations.of(context)!.threshold} ${widget.biometricAttributeData.thresholdPercentage}%",
                  style: Theme.of(context).textTheme.bodyLarge?.copyWith(
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
                          widget.biometricAttributeData.qualityPercentage / 100,
                      backgroundColor: Colors.grey,
                      progressColor: (widget
                                  .biometricAttributeData.qualityPercentage
                                  .toInt() <
                              int.parse(widget
                                  .biometricAttributeData.thresholdPercentage))
                          ? secondaryColors.elementAt(26)
                          : secondaryColors.elementAt(11),
                    ),
                    SizedBox(
                      width: 16.h,
                    ),
                    Text(
                      "${widget.biometricAttributeData.qualityPercentage.toInt()}%",
                      style: Theme.of(context).textTheme.bodyLarge?.copyWith(
                            fontSize: 14,
                            fontWeight: regular,
                            color: blackShade1,
                          ),
                    ),
                  ],
                ),
                Row(
                  mainAxisAlignment: MainAxisAlignment.center,
                  children: [
                    Text(
                      AppLocalizations.of(context)!.attempts,
                      style: Theme.of(context)
                          .textTheme
                          .bodyLarge
                          ?.copyWith(fontSize: 12, color: blackShade1),
                    ),
                    SizedBox(
                      width: 13.w,
                    ),
                    for (int i = 1;
                        i <= widget.biometricAttributeData.noOfCapturesAllowed;
                        i++)
                      Padding(
                        padding: const EdgeInsets.only(right: 9.5),
                        child: InkWell(
                          onTap: () async {
                            if (widget.biometricAttributeData.attemptNo >= i) {
                              await BiometricsApi()
                                  .getBiometrics(widget.field.id!,
                                      widget.parameterTitle, i)
                                  .then((value) {
                                widget
                                    .biometricAttributeData.listOfBiometricsDto
                                    .clear();
                                for (var e in value) {
                                  widget.biometricAttributeData
                                      .listOfBiometricsDto
                                      .add(BiometricsDto.fromJson(
                                          json.decode(e!)));
                                }
                              });

                              setState(() {
                                widget.biometricAttributeData
                                        .qualityPercentage =
                                    avgScore(widget.biometricAttributeData
                                        .listOfBiometricsDto);
                              });
                              await BiometricsApi()
                                  .extractImageValuesByAttempt(widget.field.id!,
                                      widget.parameterTitle, i)
                                  .then((value) {
                                widget.biometricAttributeData.listofImages =
                                    value;
                              });
                              setState(() {});
                            }
                          },
                          child: Container(
                            padding: const EdgeInsets.symmetric(
                              vertical: 0,
                              horizontal: 11,
                            ),
                            decoration: BoxDecoration(
                              borderRadius: BorderRadius.circular(20),
                              border: Border.all(
                                color: secondaryColors.elementAt(17),
                              ),
                              color:
                                  (widget.biometricAttributeData.attemptNo < i)
                                      ? secondaryColors.elementAt(18)
                                      : secondaryColors.elementAt(11),
                            ),
                            child: Text(
                              i.toString(),
                              style: Theme.of(context)
                                  .textTheme
                                  .bodyLarge
                                  ?.copyWith(
                                      fontSize: 12,
                                      color: (widget.biometricAttributeData
                                                  .attemptNo <
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
          ),
        ],
      ),
    );
  }
}
