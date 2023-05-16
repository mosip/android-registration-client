import 'package:flutter/material.dart';
import 'package:flutter_gen/gen_l10n/app_localizations.dart';
import 'package:provider/provider.dart';
import 'package:registration_client/provider/app_language.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:registration_client/const/utils.dart';

class UsernameComponent extends StatelessWidget {
  UsernameComponent({
    Key? key,
    required this.onTap,
    required this.languages,
    required this.mp,
    required this.onChanged,
  }) : super(key: key);

  final List<String> languages;
  final Map<String, String> mp;
  final VoidCallback onTap;
  final Function onChanged;

  @override
  Widget build(BuildContext context) {
    final appLanguage = Provider.of<AppLanguage>(context, listen: false);
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Container(
          height: 17.h,
          child: Text(
            AppLocalizations.of(context)!.language,
            style: Utils.mobileTextfieldHeader,
          ),
        ),
        SizedBox(
          height: 8.h,
        ),
        Container(
          height: 48.h,
          // width: 318.w,
          padding: EdgeInsets.only(
            left: 17.w,
            right: (14.42).w,
          ),
          decoration: BoxDecoration(
            border: Border.all(
              width: 1.h,
              color: Utils.appGreyShade,
            ),
            borderRadius: const BorderRadius.all(
              Radius.circular(6),
            ),
          ),
          child: Row(
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            crossAxisAlignment: CrossAxisAlignment.center,
            children: [
              DropdownButton(
                value: context.watch<AppLanguage>().selectedLanguage,
                key: const Key('language_dropdown'),
                underline: const SizedBox.shrink(),
                icon: const SizedBox.shrink(),
                onChanged: (newValue) {
                  context.read<AppLanguage>().selectedLanguage = newValue!;
                  appLanguage.changeLanguage(Locale(newValue));
                },
                items: languages.map((lang) {
                  return DropdownMenuItem(
                    value: lang,
                    child: Container(
                      height: 17.h,
                      // width: 47.w,
                      child: Text(
                        mp[lang]!,
                        style: Utils.mobileDropdownText,
                      ),
                    ),
                  );
                }).toList(),
              ),
              Container(
                child: const Icon(
                  Icons.keyboard_arrow_down_outlined,
                  color: Utils.appGreyShade,
                ),
              )
            ],
          ),
        ),
        SizedBox(
          height: 30.h,
        ),
        Container(
          height: 17.h,
          child: Text(
            AppLocalizations.of(context)!.username,
            style: Utils.mobileTextfieldHeader,
          ),
        ),
        SizedBox(
          height: 11.h,
        ),
        Container(
          height: 52.h,
          alignment: Alignment.centerLeft,
          padding: EdgeInsets.symmetric(
            horizontal: 17.w,
          ),
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
            // controller: usernameController,
            decoration: InputDecoration(
              hintText: AppLocalizations.of(context)!.enter_username,
              hintStyle: Utils.mobileTextfieldHintText,
              border: InputBorder.none,
            ),
            onChanged: (v) {
              onChanged(v);
            },
          ),
        ),
        SizedBox(
          height: 30.h,
        ),
        InkWell(
          onTap: onTap,
          child: Container(
            height: 52.h,
            decoration: BoxDecoration(
              color: Utils.appSolidPrimary,
              border: Border.all(
                width: 1.w,
                color: Utils.appBlueShade1,
              ),
              borderRadius: const BorderRadius.all(
                Radius.circular(5),
              ),
            ),
            child: Center(
              child: Text(
                AppLocalizations.of(context)!.next_button,
                style: Utils.mobileButtonText,
              ),
            ),
          ),
        ),
      ],
    );
  }
}
