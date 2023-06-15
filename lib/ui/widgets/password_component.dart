import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
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
          // height: 17.h,
          child: Row(
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
              color: AppStyle.appGreyShade,
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
              hintStyle: AppStyle.mobileTextfieldHintText,
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
              style: AppStyle.mobileForgotPasswordText,
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
              color: !isDisabled ? AppStyle.appSolidPrimary : AppStyle.buttonDisabled,
              border: Border.all(
                width: 1.w,
                color: !isDisabled ? AppStyle.appBlueShade1 : AppStyle.buttonDisabled,
              ),
              borderRadius: const BorderRadius.all(
                Radius.circular(5),
              ),
            ),
            child: Center(
              child: isLoggingIn
                  ? const CircularProgressIndicator(
                      color: AppStyle.appWhite,
                    )
                  : Text(
                      AppLocalizations.of(context)!.login_button,
                      style: AppStyle.mobileButtonText,
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
              color: AppStyle.appWhite,
              border: Border.all(
                width: 1.w,
                color: AppStyle.appBackButtonBorder,
              ),
              borderRadius: const BorderRadius.all(
                Radius.circular(5),
              ),
            ),
            child: Center(
              child: Text(
                AppLocalizations.of(context)!.back_button,
                style: AppStyle.mobileBackButtonText,
              ),
            ),
          ),
        ),
      ],
    );
  }
}
