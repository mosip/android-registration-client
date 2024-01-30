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

class AuthenticationPage extends StatefulWidget {
  const AuthenticationPage({
    super.key,
    required this.onChangeUsername,
    required this.onChangePassword,
  });
  final Function onChangeUsername;
  final Function onChangePassword;

  @override
  State<AuthenticationPage> createState() => _AuthenticationPageState();
}

class _AuthenticationPageState extends State<AuthenticationPage> {
  @override
  Widget build(BuildContext context) {
    return Column(
      children: [
        SizedBox(
          height: 30.h,
        ),
        Container(
          width: 376.w,
          padding: EdgeInsets.only(
            top: 24.h,
            bottom: 28.h,
            left: 20.w,
            right: 20.w,
          ),
          decoration: BoxDecoration(
            borderRadius: const BorderRadius.all(
              Radius.circular(6),
            ),
            color: pureWhite,
          ),
          child: Column(
            children: [
              _getAuthIcon(),
              SizedBox(
                height: 26.h,
              ),
              Text(
                'Authentication using Password',
                style: TextStyle(
                    fontSize: 18,
                    fontWeight: semiBold,
                    color: appBlack),
              ),
              SizedBox(
                height: 35.h,
              ),
              Row(
                children: [
                  Text(
                    AppLocalizations.of(context)!.username,
                    style: AppTextStyle.tabletPortraitTextfieldHeader,
                  ),
                  const Text(
                    ' *',
                    style: TextStyle(
                      color: mandatoryField,
                    ),
                  ),
                ],
              ),
              SizedBox(
                height: 11.h,
              ),
              _getUsernameTextField(),
              SizedBox(
                height: 35.h,
              ),
              Row(
                children: [
                  Text(
                    AppLocalizations.of(context)!.password,
                    style: AppTextStyle.tabletPortraitTextfieldHeader,
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
              _getPasswordTextField(),
            ],
          ),
        ),
      ],
    );
  }

  _getAuthIcon() {
    return Container(
      height: 80.w,
      width: 80.w,
      decoration: BoxDecoration(
        shape: BoxShape.circle,
        border: Border.all(
          color: authIconBorder,
          width: 2,
        ),
        color: authIconBackground,
      ),
      child: Center(
        child: Image.asset('assets/images/Registering an Individual@2x.png'),
      ),
    );
  }

  _getUsernameTextField() {
    return Container(
      height: 52.h,
      alignment: Alignment.centerLeft,
      padding: EdgeInsets.symmetric(
        vertical: 12.h,
        horizontal: 12.w,
      ),
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
        decoration: InputDecoration(
          hintText: AppLocalizations.of(context)!.enter_username,
          hintStyle: AppTextStyle.tabletPortraitTextfieldHintText,
          border: InputBorder.none,
        ),
        onChanged: (v) {
          widget.onChangeUsername(v);
        },
      ),
    );
  }

  _getPasswordTextField() {
    return Container(
      height: 52.h,
      alignment: Alignment.centerLeft,
      padding: EdgeInsets.symmetric(
        vertical: 12.h,
        horizontal: 12.w,
      ),
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
        decoration: InputDecoration(
          hintText: AppLocalizations.of(context)!.enter_password,
          hintStyle: AppTextStyle.tabletPortraitTextfieldHintText,
          border: InputBorder.none,
        ),
        onChanged: (v) {
          widget.onChangePassword(v);
        },
      ),
    );
  }
}
