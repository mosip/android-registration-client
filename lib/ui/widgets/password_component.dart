/*
 * Copyright (c) Modular Open Source Identity Platform
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *
*/

import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:registration_client/utils/app_config.dart';
import 'package:registration_client/utils/app_style.dart';
import 'package:flutter_gen/gen_l10n/app_localizations.dart';

class PasswordComponent extends StatelessWidget {
  const PasswordComponent({
    Key? key,
    required this.onTapLogin,
    required this.onTapBack,
    required this.onChanged,
    required this.isLoggingIn,
    required this.isDisabled,
    required this.isMobile,
    required this.onTapForgotPassword,
  }) : super(key: key);

  final VoidCallback onTapLogin;
  final VoidCallback onTapBack;
  final Function onChanged;
  final bool isLoggingIn;
  final bool isDisabled;
  final bool isMobile;
  final VoidCallback onTapForgotPassword;

  @override
  Widget build(BuildContext context) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Row(
          children: [
            Text(
              AppLocalizations.of(context)!.password,
              style: isMobile
              ? isMobileSize ? AppTextStyle.mobileTextfieldHeader
                  : AppTextStyle.tabletPortraitTextfieldHeader
                  : AppTextStyle.mobileTextfieldHeader,
            ),
            const Text(
              ' *',
              style: TextStyle(color: mandatoryField),
            ),
          ],
        ),
        SizedBox(
          height: 11.h,
        ),
        Container(
          height: isMobile && !isMobileSize ? 82.h : 52.h,
          alignment: Alignment.centerLeft,
          padding: EdgeInsets.symmetric(horizontal: 17.w),
          decoration: BoxDecoration(
            border: Border.all(
              width: 1.h,
              color: appGreyShade,
            ),
            borderRadius: const BorderRadius.all(
              Radius.circular(6),
            ),
          ),
          child: TextField(
            obscureText: true,
            onChanged: (v) {
              onChanged(v);
            },
            decoration: InputDecoration(
              hintText: AppLocalizations.of(context)!.enter_password,
              hintStyle: isMobile
              && !isMobileSize ? AppTextStyle.tabletPortraitTextfieldHintText
                  : AppTextStyle.mobileTextfieldHintText,
              border: InputBorder.none,
            ),
            style: TextStyle(
              fontSize: isMobile && !isMobileSize ? 22 : 14,
              color: appBlack,
            ),
          ),
        ),
        SizedBox(
          height: 30.h,
        ),
        InkWell(
          onTap: !isDisabled ? onTapLogin : null,
          child: Container(
            height: isMobile && !isMobileSize ? 82.h : 52.h,
            decoration: BoxDecoration(
              color: !isDisabled
                  ? appSolidPrimary
                  : buttonDisabled,
              border: Border.all(
                width: 1.w,
                color: !isDisabled
                    ? appBlueShade1
                    : buttonDisabled,
              ),
              borderRadius: const BorderRadius.all(
                Radius.circular(5),
              ),
            ),
            child: Center(
              child: isLoggingIn
                  ? const CircularProgressIndicator(
                      color: appWhite,
                    )
                  : Text(
                      AppLocalizations.of(context)!.login_button,
                      style: isMobile
                      && !isMobileSize ? AppTextStyle.tabletPortraitButtonText
                          : AppTextStyle.mobileButtonText,
                    ),
            ),
          ),
        ),
        SizedBox(
          height: 20.h,
        ),
        InkWell(
          onTap: onTapBack,
          child: Container(
            height: isMobile && !isMobileSize ? 82.h : 52.h,
            // width: 318.w,
            decoration: BoxDecoration(
              color: appWhite,
              border: Border.all(
                width: 1.w,
                color: appBackButtonBorder,
              ),
              borderRadius: const BorderRadius.all(
                Radius.circular(5),
              ),
            ),
            child: Center(
              child: Text(
                AppLocalizations.of(context)!.back_button,
                style: isMobile
                && !isMobileSize ? AppTextStyle.tabletPortraitBackButtonText
                    : AppTextStyle.mobileBackButtonText,
              ),
            ),
          ),
        ),
        SizedBox(
          height: 30.h,
        ),
        // InkWell(
        //   onTap: onTapForgotPassword,
        //   child: Container(
        //     alignment: Alignment.center,
        //     child: Text(
        //       AppLocalizations.of(context)!.forgot_password,
        //       style: isMobile
        //           && !isMobileSize ? AppTextStyle.tabletPortraitForgotPasswordText
        //           : AppTextStyle.mobileForgotPasswordText,
        //     ),
        //   ),
        // ),
        SizedBox(
          height: 20.h,
        ),
      ],
    );
  }
}
