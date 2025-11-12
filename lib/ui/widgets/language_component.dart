/*
 * Copyright (c) Modular Open Source Identity Platform
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *
*/

import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:registration_client/utils/app_config.dart';

class LanguageComponent extends StatefulWidget {
  const LanguageComponent({
    super.key,
    required this.title,
    required this.isSelected,
    required this.onTap,
    required this.isMobile,
    required this.isFreezed,
    required this.isDisabled,
  });
  final String title;
  final bool isSelected;
  final VoidCallback onTap;
  final bool isMobile;
  final bool isFreezed;
  final bool isDisabled;

  @override
  State<LanguageComponent> createState() => _LanguageComponentState();
}

class _LanguageComponentState extends State<LanguageComponent> {
  @override
  Widget build(BuildContext context) {
    return InkWell(
      onTap: () {
        if (!widget.isFreezed && !widget.isDisabled) {
          widget.onTap();
        }
      },

      child: Container(
        height: 60.h,
        padding: EdgeInsets.only(
          left: 25.w,
          right: 25.w,
          top: widget.isMobile && !isMobileSize ? 15.h : 9.h,
          bottom: widget.isMobile && !isMobileSize ? 15.h : 9.h,
        ),
        decoration: BoxDecoration(
          color: widget.isFreezed
              ? appButtonBorderText
              : widget.isSelected
              ? appButtonBorderText
              : Colors.transparent,
          border: Border.all(
            width: 1,
            color: widget.isDisabled
                ? appBlackShade3
                : widget.isSelected
                ? appButtonBorderText
                : languageSelectedColor,
          ),
          borderRadius: const BorderRadius.all(
            Radius.circular(36),
          ),
        ),
        child: Row(
          mainAxisSize: MainAxisSize.min,
          children: [
            widget.isFreezed || widget.isSelected
                ? Icon(
              Icons.check,
              color: widget.isFreezed ? appGreyShade : appWhite,
            )
                : const SizedBox(),
            SizedBox(
              width: widget.isFreezed || widget.isSelected ? 15.02 : 0,
            ),
            Directionality(
              textDirection: TextDirection.ltr,
              child: Text(
                widget.title,
                style: TextStyle(
                  fontSize: widget.isMobile && !isMobileSize ? 24 : 16,
                  color: widget.isDisabled
                      ? appBlackShade3
                      : widget.isFreezed
                      ? appGreyShade
                      : widget.isSelected
                      ? appWhite
                      : appBlackShade1,
                ),
              ),
            ),
          ],
        ),
      ),
    );
  }
}
