import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:registration_client/const/utils.dart';
import 'package:flutter_gen/gen_l10n/app_localizations.dart';

class PasswordComponent extends StatelessWidget {
  const PasswordComponent({
    Key? key,
    required this.onTapLogin,
    required this.onTapBack,
    required this.onChanged,
    required this.isLoggingIn,
    required this.isDisabled,
  }) : super(key: key);

  final VoidCallback onTapLogin;
  final VoidCallback onTapBack;
  final Function onChanged;
  final bool isLoggingIn;
  final bool isDisabled;

  @override
  Widget build(BuildContext context) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Container(
          height: 17.h,
          child: Text(
            AppLocalizations.of(context)!.password,
            style: Utils.mobileTextfieldHeader,
          ),
        ),
        SizedBox(
          height: 11.h,
        ),
        Container(
          height: 52.h,
          alignment: Alignment.centerLeft,
          padding: EdgeInsets.symmetric(horizontal: 17.w),
          decoration: BoxDecoration(
            border: Border.all(
              width: 1.h,
              color: Utils.appGreyShade,
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
              hintStyle: Utils.mobileTextfieldHintText,
              border: InputBorder.none,
            ),
          ),
        ),
        SizedBox(
          height: 15.h,
        ),
        InkWell(
          onTap: () {},
          child: Container(
            height: 17.h,
            alignment: Alignment.centerRight,
            child: Text(
              AppLocalizations.of(context)!.forgot_password,
              style: Utils.mobileForgotPasswordText,
            ),
          ),
        ),
        SizedBox(
          height: 30.h,
        ),
        InkWell(
          onTap: !isDisabled ? onTapLogin : null,
          child: Container(
            height: 52.h,
            decoration: BoxDecoration(
              color: !isDisabled ? Utils.appSolidPrimary : Utils.appGreyShade,
              border: Border.all(
                width: 1.w,
                color: Utils.appBlueShade1,
              ),
              borderRadius: const BorderRadius.all(
                Radius.circular(5),
              ),
            ),
            child: Center(
              child: isLoggingIn
                  ? const CircularProgressIndicator(
                      color: Utils.appWhite,
                    )
                  : Text(
                      AppLocalizations.of(context)!.login_text,
                      style: Utils.mobileButtonText,
                    ),
            ),
          ),
        ),
        SizedBox(
          height: 10.h,
        ),
        InkWell(
          onTap: onTapBack,
          child: Container(
            height: 52.h,
            // width: 318.w,
            decoration: BoxDecoration(
              color: Utils.appWhite,
              border: Border.all(
                width: 1.w,
                color: Utils.appBackButtonBorder,
              ),
              borderRadius: const BorderRadius.all(
                Radius.circular(5),
              ),
            ),
            child: Center(
              child: Text(
                AppLocalizations.of(context)!.back_button,
                style: Utils.mobileBackButtonText,
              ),
            ),
          ),
        ),
      ],
    );
  }
}
