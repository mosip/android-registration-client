/*
 * Copyright (c) Modular Open Source Identity Platform
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *
*/

import 'package:flutter/material.dart';

import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:registration_client/model/biometric_attribute_data.dart';
import 'package:registration_client/utils/app_config.dart';
import 'package:flutter_gen/gen_l10n/app_localizations.dart';

class BiometricCaptureExceptionBlock extends StatefulWidget {
  const BiometricCaptureExceptionBlock(
      {super.key, required this.exceptionImage, required this.attribute});
  final Widget exceptionImage;
  final BiometricAttributeData attribute;

  @override
  State<BiometricCaptureExceptionBlock> createState() =>
      _BiometricCaptureExceptionBlockState();
}

class _BiometricCaptureExceptionBlockState
    extends State<BiometricCaptureExceptionBlock> {
  @override
  Widget build(BuildContext context) {
    return Container(
      height: 547.h,
      width: 370.h,
      decoration: BoxDecoration(
          color: secondaryColors.elementAt(3),
          borderRadius: BorderRadius.circular(10),
          border: Border.all(color: secondaryColors.elementAt(13), width: 1)),
      child: Column(
        mainAxisAlignment: MainAxisAlignment.spaceEvenly,
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Padding(
            padding: const EdgeInsets.fromLTRB(0, 16, 0, 36),
            child: Center(
              child: Text(
                AppLocalizations.of(context)!.mark_exception,
                style: Theme.of(context).textTheme.bodyLarge?.copyWith(
                    fontSize: 18, fontWeight: semiBold, color: blackShade1),
              ),
            ),
          ),
          widget.exceptionImage,
          if (widget.attribute.title == "Face" ||
              widget.attribute.title == "Exception")
            Center(
                child: Padding(
              padding: EdgeInsets.only(top: 27.h),
              child: Text(
                "${AppLocalizations.of(context)!.marking_exceptions_on} ${widget.attribute.viewTitle.toLowerCase()} ${AppLocalizations.of(context)!.is_not_allowed}",
                style: Theme.of(context).textTheme.bodyLarge?.copyWith(
                    fontSize: 14, fontWeight: regular, color: blackShade1),
              ),
            )),
          (widget.attribute.title != "Face" &&
                  widget.attribute.title != "Exception")
              ? Padding(
                  padding: const EdgeInsets.fromLTRB(20, 0, 20, 0),
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Text(
                        AppLocalizations.of(context)!.exception_type,
                        style: Theme.of(context).textTheme.bodyLarge?.copyWith(
                            fontSize: 14,
                            fontWeight: semiBold,
                            color: blackShade1),
                      ),
                      SizedBox(
                        height: 17.h,
                      ),
                      Row(
                        children: [
                          Row(
                            children: [
                              SizedBox(
                                height: 10.h,
                                width: 10.h,
                                child: Radio(
                                  activeColor: solidPrimary,
                                  value: "Permanent",
                                  groupValue: widget.attribute.exceptionType,
                                  onChanged: ((value) {
                                    setState(() {
                                      widget.attribute.exceptionType = value!;
                                    });
                                  }),
                                ),
                              ),
                              SizedBox(
                                width: 8.w,
                              ),
                              Text(
                                AppLocalizations.of(context)!.permanent,
                                style: Theme.of(context)
                                    .textTheme
                                    .bodyLarge
                                    ?.copyWith(
                                        fontSize: 14,
                                        fontWeight: regular,
                                        color: blackShade1),
                              ),
                            ],
                          ),
                          SizedBox(
                            width: 28.w,
                          ),
                          Row(
                            children: [
                              SizedBox(
                                height: 10.h,
                                width: 10.h,
                                child: Radio(
                                  activeColor: solidPrimary,
                                  value: "Temporary",
                                  groupValue: widget.attribute.exceptionType,
                                  onChanged: ((value) {
                                    setState(() {
                                      widget.attribute.exceptionType = value!;
                                    });
                                  }),
                                ),
                              ),
                              SizedBox(
                                width: 8.w,
                              ),
                              Text(
                                AppLocalizations.of(context)!.temporary,
                                style: Theme.of(context)
                                    .textTheme
                                    .bodyLarge
                                    ?.copyWith(
                                        fontSize: 14,
                                        fontWeight: regular,
                                        color: blackShade1),
                              ),
                            ],
                          ),
                        ],
                      ),
                    ],
                  ),
                )
              : const Spacer(),
          (widget.attribute.title != "Face" &&
                  widget.attribute.title != "Exception")
              ? Padding(
                  padding: const EdgeInsets.fromLTRB(20, 0, 20, 0),
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Text(
                        AppLocalizations.of(context)!.comments,
                        style: Theme.of(context).textTheme.bodyLarge?.copyWith(
                            fontSize: 14,
                            fontWeight: semiBold,
                            color: blackShade1),
                      ),
                      SizedBox(
                        height: 11.h,
                      ),
                      SizedBox(
                        width: 284.w,
                        height: 129.h,
                        child: TextField(
                          maxLines: 5,
                          decoration: InputDecoration(
                            border: OutlineInputBorder(
                              borderRadius: BorderRadius.circular(5),
                              borderSide: BorderSide(
                                color: secondaryColors.elementAt(14),
                              ),
                            ),
                            hintText: AppLocalizations.of(context)!
                                .add_comments_for_marking_the_exception,
                            hintStyle: Theme.of(context)
                                .textTheme
                                .bodyLarge
                                ?.copyWith(
                                    fontSize: 14,
                                    fontWeight: regular,
                                    color: secondaryColors.elementAt(2)),
                          ),
                        ),
                      ),
                    ],
                  ),
                )
              : const Spacer(),
        ],
      ),
    );
  }
}
