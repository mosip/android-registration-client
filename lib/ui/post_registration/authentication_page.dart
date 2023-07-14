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
  String username = '';
  String password = '';

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
            color: pure_white,
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
                    fontSize: 18.spMax,
                    fontWeight: semiBold,
                    color: AppStyle.appBlack),
              ),
              SizedBox(
                height: 35.h,
              ),
              Row(
                children: [
                  Text(
                    AppLocalizations.of(context)!.username,
                    style: AppStyle.mobileTextfieldHeader,
                  ),
                  const Text(
                    ' *',
                    style: TextStyle(
                      color: AppStyle.mandatoryField,
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
                    style: AppStyle.mobileTextfieldHeader,
                  ),
                  const Text(
                    ' *',
                    style: TextStyle(color: AppStyle.mandatoryField),
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
          color: AppStyle.authIconBorder,
          width: 2,
        ),
        color: AppStyle.authIconBackground,
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
          color: AppStyle.appGreyShade,
        ),
        borderRadius: const BorderRadius.all(
          Radius.circular(6),
        ),
      ),
      child: TextField(
        decoration: InputDecoration(
          hintText: AppLocalizations.of(context)!.enter_username,
          hintStyle: AppStyle.mobileTextfieldHintText,
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
          color: AppStyle.appGreyShade,
        ),
        borderRadius: const BorderRadius.all(
          Radius.circular(6),
        ),
      ),
      child: TextField(
        obscureText: true,
        decoration: InputDecoration(
          hintText: AppLocalizations.of(context)!.enter_password,
          hintStyle: AppStyle.mobileTextfieldHintText,
          border: InputBorder.none,
        ),
        onChanged: (v) {
          widget.onChangePassword(v);
        },
      ),
    );
  }
}
