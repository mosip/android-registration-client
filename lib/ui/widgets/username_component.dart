import 'package:flutter/material.dart';
import 'package:flutter_gen/gen_l10n/app_localizations.dart';
import 'package:provider/provider.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:registration_client/provider/global_provider.dart';
import 'package:registration_client/ui/widgets/language_component.dart';
import 'package:registration_client/utils/app_config.dart';
import 'package:registration_client/utils/app_style.dart';

class UsernameComponent extends StatefulWidget {
  const UsernameComponent({
    Key? key,
    required this.onTap,
    required this.languages,
    required this.mp,
    required this.onChanged,
    required this.isDisabled,
    required this.isMobile,
  }) : super(key: key);

  final List<String?> languages;
  final Map<String, String> mp;
  final VoidCallback onTap;
  final Function onChanged;
  final bool isDisabled;
  final bool isMobile;

  @override
  State<UsernameComponent> createState() => _UsernameComponentState();
}

class _UsernameComponentState extends State<UsernameComponent> {
  String? selectedLanguage;

  @override
  Widget build(BuildContext context) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        SizedBox(
          child: Text(
            AppLocalizations.of(context)!.language,
            style: widget.isMobile && !isMobileSize
                ? AppTextStyle.tabletPortraitTextfieldHeader
                : AppTextStyle.mobileTextfieldHeader,
          ),
        ),
        SizedBox(
          height: widget.isMobile && !isMobileSize ? 14.h : 8.h,
        ),
        _getLanguageRowList(),
        SizedBox(
          height: 27.h,
        ),
        Row(
          children: [
            Text(
              AppLocalizations.of(context)!.username,
              style: widget.isMobile && !isMobileSize
                  ? AppTextStyle.tabletPortraitTextfieldHeader
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
          height: widget.isMobile && !isMobileSize ? 82.h : 52.h,
          alignment: Alignment.centerLeft,
          padding: EdgeInsets.symmetric(
            horizontal: 17.w,
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
            // controller: usernameController,
            decoration: InputDecoration(
              hintText: AppLocalizations.of(context)!.enter_username,
              hintStyle: widget.isMobile && !isMobileSize
                  ? AppTextStyle.tabletPortraitTextfieldHintText
                  : AppTextStyle.mobileTextfieldHintText,
              border: InputBorder.none,
            ),
            style: TextStyle(
              fontSize: widget.isMobile && !isMobileSize ? 22 : 14,
              color: appBlack,
            ),
            onChanged: (v) {
              widget.onChanged(v);
            },
          ),
        ),
        SizedBox(
          height: 30.h,
        ),
        InkWell(
          onTap: !widget.isDisabled ? widget.onTap : null,
          child: Container(
            height: widget.isMobile && !isMobileSize ? 82.h : 52.h,
            decoration: BoxDecoration(
              color: !widget.isDisabled
                  ? appSolidPrimary
                  : appGreyShade,
              border: Border.all(
                width: 1.w,
                color: !widget.isDisabled
                    ? appBlueShade1
                    : appGreyShade,
              ),
              borderRadius: const BorderRadius.all(
                Radius.circular(5),
              ),
            ),
            child: Center(
              child: Text(
                AppLocalizations.of(context)!.next_button,
                style: widget.isMobile && !isMobileSize
                    ? AppTextStyle.tabletPortraitButtonText
                    : AppTextStyle.mobileButtonText,
              ),
            ),
          ),
        ),
      ],
    );
  }

  _getLanguageRowList() {
    final appLanguage = Provider.of<GlobalProvider>(context, listen: false);
    return Wrap(
      spacing: 8.w,
      runSpacing: 8.h,
      children: widget.languages.map((e) {
        return LanguageComponent(
          title: widget.mp[e] ?? "",
          isDisabled: context.read<GlobalProvider>().disabledLanguageMap[e] ?? false,
          isSelected: context.read<GlobalProvider>().selectedLanguage == e,
          onTap: () {
            selectedLanguage = e;
            appLanguage.toggleLocale(e!);
          },
          isMobile: widget.isMobile,
          isFreezed: false,
        );
      }).toList(),
    );
  }
}
