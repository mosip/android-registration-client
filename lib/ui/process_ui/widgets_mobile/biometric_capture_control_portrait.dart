/*
 * Copyright (c) Modular Open Source Identity Platform
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *
*/

import 'package:flutter/material.dart';

import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:flutter_svg/flutter_svg.dart';

import 'package:provider/provider.dart';
import 'package:registration_client/model/biometric_attribute_data.dart';

import 'package:registration_client/model/field.dart';

import 'package:registration_client/provider/biometric_capture_control_provider.dart';

import 'package:registration_client/provider/global_provider.dart';
import 'package:registration_client/ui/process_ui/widgets_mobile/biometric_capture_scan_block_portrait.dart';

import 'package:registration_client/utils/app_config.dart';
import 'package:responsive_grid_list/responsive_grid_list.dart';
import 'package:flutter_gen/gen_l10n/app_localizations.dart';

class BiometricCaptureControlPortrait extends StatefulWidget {
  const BiometricCaptureControlPortrait({super.key, required this.field});
  final Field field;

  @override
  State<BiometricCaptureControlPortrait> createState() =>
      _BiometricCaptureControlPortraitState();
}

class _BiometricCaptureControlPortraitState
    extends State<BiometricCaptureControlPortrait> {
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

  Widget _getBiometricCaptureSelectionBlockMobile(
      BiometricAttributeData biometricAttributeData) {
    return InkWell(
        onTap: () {
          context.read<BiometricCaptureControlProvider>().biometricAttribute =
              biometricAttributeData.title;
          final providerCopy = Provider.of<BiometricCaptureControlProvider>(
              context,
              listen: false);
          Navigator.push(
              context,
              MaterialPageRoute(
                  builder: (context) => ChangeNotifierProvider.value(
                        value: providerCopy,
                        child: BiometricCaptureScanBlockPortrait(
                            field: widget.field),
                      )));
        },
        child: Center(
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
                            : (context
                                        .watch<
                                            BiometricCaptureControlProvider>()
                                        .biometricAttribute ==
                                    biometricAttributeData.title)
                                ? secondaryColors.elementAt(12)
                                : secondaryColors.elementAt(14)),
                    borderRadius: BorderRadius.circular(10)),
                child: Column(
                  mainAxisAlignment: MainAxisAlignment.center,
                  children: [
                    Padding(
                      padding: const EdgeInsets.all(8.0),
                      child: SvgPicture.asset(
                        "assets/svg/${biometricAttributeData.title}.svg",
                        height: 200.h,
                        width: 200.h,
                      ),
                    ),
                    SizedBox(
                      height: 10.h,
                    ),
                    Text(
                      "${biometricAttributeData.viewTitle} ${AppLocalizations.of(context)!.scan}",
                      style: TextStyle(
                        fontSize: 28,
                        fontWeight: semiBold,
                        color: blackShade1,
                      ),
                    )
                  ],
                ),
              ),
              if (biometricAttributeData.isScanned == true)
                Positioned(
                    top: 15,
                    right: 15,
                    child: (biometricAttributeData.exceptions.contains(true))
                        ? Image.asset(
                            "assets/images/Group 57548@2x.png",
                          )
                        : Image.asset(
                            "assets/images/Group 57745@2x.png",
                          )),
              if (!biometricAttributeData.exceptions.contains(false))
                Positioned(
                    top: 15,
                    right: 15,
                    child: Image.asset(
                      "assets/images/Group 57548@2x.png",
                    )),
              if (biometricAttributeData.isScanned == true)
                Positioned(
                    top: 20,
                    left: 20,
                    child: Container(
                      padding: const EdgeInsets.symmetric(
                          horizontal: 10, vertical: 7),
                      decoration: BoxDecoration(
                          color: (biometricAttributeData.qualityPercentage
                                      .toInt() <
                                  int.parse(biometricAttributeData
                                      .thresholdPercentage))
                              ? secondaryColors.elementAt(26)
                              : secondaryColors.elementAt(11),
                          borderRadius: BorderRadius.circular(50)),
                      height: 40,
                      child: Text(
                          "${biometricAttributeData.qualityPercentage.toInt()}%",
                          style: TextStyle(
                              fontSize: 20,
                              color: pureWhite,
                              fontWeight: semiBold)),
                    )),
            ],
          ),
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
        SizedBox(
          width: double.infinity,
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
                                  fontSize: (isMobileSize) ? 16.w : 24.w,
                                  color: blackShade1,
                                  fontWeight: semiBold,
                                  overflow: TextOverflow.ellipsis),
                          children: [
                            if (widget.field.required! ||
                                (widget.field.requiredOn != null &&
                                    widget.field.requiredOn!.isNotEmpty &&
                                    (context
                                                .read<GlobalProvider>()
                                                .mvelRequiredFields[
                                            widget.field.id] ??
                                        false)))
                              const TextSpan(
                                text: " *",
                                style:
                                    TextStyle(color: Colors.red, fontSize: 15),
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
                                  fontSize: (isMobileSize) ? 16.w : 24.w,
                                  color: blackShade1,
                                  fontWeight: semiBold,
                                  overflow: TextOverflow.ellipsis),
                        ),
                  // SizedBox(
                  //   height: (isMobileSize)?20.h:52.h,
                  // ),
                ],
              ),
            ),
          ),
        ),
        SizedBox(
          height: 15.h,
        ),
        Column(
          children: [
            (widget.field.conditionalBioAttributes!.first!.ageGroup!
                        .compareTo(context.read<GlobalProvider>().ageGroup) ==
                    0)
                ? Container(
                    margin: const EdgeInsets.fromLTRB(20, 0, 20, 20),
                    height: (isMobileSize)
                        ? (((((context
                                            .read<
                                                BiometricCaptureControlProvider>()
                                            .returnNoOfAttributes(widget
                                                .field
                                                .conditionalBioAttributes!
                                                .first!
                                                .bioAttributes!))
                                        .toDouble()))
                                    .ceil() *
                                409.h) +
                            70.h)
                        : ((((context
                                                .read<
                                                    BiometricCaptureControlProvider>()
                                                .returnNoOfAttributes(widget
                                                    .field
                                                    .conditionalBioAttributes!
                                                    .first!
                                                    .bioAttributes!))
                                            .toDouble()) /
                                        2)
                                    .ceil() *
                                335.h) +
                            70.h,
                    width: double.infinity,
                    child: ResponsiveGridList(
                      listViewBuilderOptions: ListViewBuilderOptions(
                          physics: const NeverScrollableScrollPhysics()),
                      minItemWidth: 372.h,
                      verticalGridSpacing: 17,
                      horizontalGridMargin: 30,
                      minItemsPerRow: (isMobileSize) ? 1 : 2,
                      maxItemsPerRow: (isMobileSize) ? 1 : 2,
                      children: [
                        if (widget.field.conditionalBioAttributes!.first!
                                .bioAttributes!
                                .contains("leftEye") &&
                            widget.field.conditionalBioAttributes!.first!
                                .bioAttributes!
                                .contains("rightEye"))
                          _getBiometricCaptureSelectionBlockMobile(context
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
                          _getBiometricCaptureSelectionBlockMobile(context
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
                          _getBiometricCaptureSelectionBlockMobile(context
                              .read<BiometricCaptureControlProvider>()
                              .leftHand),
                        if (widget.field.conditionalBioAttributes!.first!
                                .bioAttributes!
                                .contains("leftThumb") &&
                            widget.field.conditionalBioAttributes!.first!
                                .bioAttributes!
                                .contains("rightThumb"))
                          _getBiometricCaptureSelectionBlockMobile(context
                              .read<BiometricCaptureControlProvider>()
                              .thumbs),
                        if (widget.field.conditionalBioAttributes!.first!
                            .bioAttributes!
                            .contains("face"))
                          _getBiometricCaptureSelectionBlockMobile(context
                              .read<BiometricCaptureControlProvider>()
                              .face),
                        if (context
                                .watch<BiometricCaptureControlProvider>()
                                .iris
                                .exceptions
                                .contains(true) ||
                            context
                                .watch<BiometricCaptureControlProvider>()
                                .rightHand
                                .exceptions
                                .contains(true) ||
                            context
                                .watch<BiometricCaptureControlProvider>()
                                .leftHand
                                .exceptions
                                .contains(true) ||
                            context
                                .watch<BiometricCaptureControlProvider>()
                                .thumbs
                                .exceptions
                                .contains(true) ||
                            context
                                .watch<BiometricCaptureControlProvider>()
                                .face
                                .exceptions
                                .contains(true))
                          _getBiometricCaptureSelectionBlockMobile(context
                              .read<BiometricCaptureControlProvider>()
                              .exception),
                      ],
                    ),
                  )
                : Container(
                    margin: const EdgeInsets.fromLTRB(20, 0, 20, 20),
                    height: (isMobileSize)
                        ? (((((context
                                            .read<
                                                BiometricCaptureControlProvider>()
                                            .returnNoOfAttributes(
                                                widget.field.bioAttributes!))
                                        .toDouble()))
                                    .ceil() *
                                335.h) +
                            409.h)
                        : (((((context
                                                .read<
                                                    BiometricCaptureControlProvider>()
                                                .returnNoOfAttributes(widget
                                                    .field.bioAttributes!))
                                            .toDouble()) /
                                        2)
                                    .ceil() *
                                335.h) +
                        410.h),
                    width: double.infinity,
                    child: ResponsiveGridList(
                        listViewBuilderOptions: ListViewBuilderOptions(
                            physics: const NeverScrollableScrollPhysics()),
                        minItemWidth: 372.h,
                        verticalGridSpacing: 17,
                        horizontalGridMargin: 30,
                        minItemsPerRow: (isMobileSize) ? 1 : 2,
                        maxItemsPerRow: (isMobileSize) ? 1 : 2,
                        children: [
                          if (widget.field.bioAttributes!.contains("leftEye") &&
                              widget.field.bioAttributes!.contains("rightEye"))
                            _getBiometricCaptureSelectionBlockMobile(context
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
                            _getBiometricCaptureSelectionBlockMobile(context
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
                            _getBiometricCaptureSelectionBlockMobile(context
                                .read<BiometricCaptureControlProvider>()
                                .leftHand),
                          if (widget.field.bioAttributes!
                                  .contains("leftThumb") &&
                              widget.field.bioAttributes!
                                  .contains("rightThumb"))
                            _getBiometricCaptureSelectionBlockMobile(context
                                .read<BiometricCaptureControlProvider>()
                                .thumbs),
                          if (widget.field.bioAttributes!.contains("face"))
                            _getBiometricCaptureSelectionBlockMobile(context
                                .read<BiometricCaptureControlProvider>()
                                .face),
                          if (context
                                  .watch<BiometricCaptureControlProvider>()
                                  .iris
                                  .exceptions
                                  .contains(true) ||
                              context
                                  .watch<BiometricCaptureControlProvider>()
                                  .rightHand
                                  .exceptions
                                  .contains(true) ||
                              context
                                  .watch<BiometricCaptureControlProvider>()
                                  .leftHand
                                  .exceptions
                                  .contains(true) ||
                              context
                                  .watch<BiometricCaptureControlProvider>()
                                  .thumbs
                                  .exceptions
                                  .contains(true) ||
                              context
                                  .watch<BiometricCaptureControlProvider>()
                                  .face
                                  .exceptions
                                  .contains(true))
                            _getBiometricCaptureSelectionBlockMobile(context
                                .read<BiometricCaptureControlProvider>()
                                .exception),
                        ]),
                  )
          ],
        ),
      ],
    );
  }
}
