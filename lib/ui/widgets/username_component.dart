import 'package:flutter/material.dart';
import 'package:flutter_gen/gen_l10n/app_localizations.dart';
import 'package:provider/provider.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:registration_client/provider/global_provider.dart';
import 'package:registration_client/ui/widgets/language_component.dart';
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
            style: widget.isMobile
                ? AppStyle.tabletPortraitTextfieldHeader
                : AppStyle.mobileTextfieldHeader,
          ),
        ),
        SizedBox(
          height: widget.isMobile ? 14.h : 8.h,
        ),
        // _getLanguageDropdownButton(context),
        _getLanguageRowList(),
        SizedBox(
          height: 30.h,
        ),
        Row(
          children: [
            Text(
              AppLocalizations.of(context)!.username,
              style: widget.isMobile
                  ? AppStyle.tabletPortraitTextfieldHeader
                  : AppStyle.mobileTextfieldHeader,
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
        Container(
          height: widget.isMobile ? 82.h : 52.h,
          alignment: Alignment.centerLeft,
          padding: EdgeInsets.symmetric(
            horizontal: 17.w,
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
            // controller: usernameController,
            decoration: InputDecoration(
              hintText: AppLocalizations.of(context)!.enter_username,
              hintStyle: widget.isMobile
                  ? AppStyle.tabletPortraitTextfieldHintText
                  : AppStyle.mobileTextfieldHintText,
              border: InputBorder.none,
            ),
            style: TextStyle(
              fontSize: widget.isMobile ? 22 : 14,
              color: AppStyle.appBlack,
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
            height: widget.isMobile ? 82.h : 52.h,
            decoration: BoxDecoration(
              color: !widget.isDisabled
                  ? AppStyle.appSolidPrimary
                  : AppStyle.appGreyShade,
              border: Border.all(
                width: 1.w,
                color: !widget.isDisabled
                    ? AppStyle.appBlueShade1
                    : AppStyle.appGreyShade,
              ),
              borderRadius: const BorderRadius.all(
                Radius.circular(5),
              ),
            ),
            child: Center(
              child: Text(
                AppLocalizations.of(context)!.next_button,
                style: widget.isMobile
                    ? AppStyle.tabletPortraitButtonText
                    : AppStyle.mobileButtonText,
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
          title: widget.mp[e]!,
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

  // List<DropdownMenuItem<String>> _addDividersAfterItems(List<String?> items) {
  //   List<DropdownMenuItem<String>> menuItems = [];
  //   for (var item in items) {
  //     menuItems.addAll(
  //       [
  //         DropdownMenuItem<String>(
  //           value: item,
  //           child: Text(
  //             widget.mp[item]!,
  //             style: widget.isMobile
  //                 ? AppStyle.tabletPortraitDropdownText
  //                 : AppStyle.mobileDropdownText,
  //           ),
  //         ),
  //         //If it's last item, we will not add Divider after it.
  //         if (item != items.last)
  //           const DropdownMenuItem<String>(
  //             enabled: false,
  //             child: Divider(),
  //           ),
  //       ],
  //     );
  //   }
  //   return menuItems;
  // }

  // List<double> _getCustomItemsHeights() {
  //   List<double> itemsHeights = [];
  //   for (var i = 0; i < (widget.languages.length * 2) - 1; i++) {
  //     if (i.isEven) {
  //       itemsHeights.add(82.h);
  //     }
  //     if (i.isOdd) {
  //       itemsHeights.add(4.h);
  //     }
  //   }
  //   return itemsHeights;
  // }

  // _getLanguageDropdownButton(BuildContext context) {
  //   final appLanguage = Provider.of<GlobalProvider>(context, listen: false);
  //   return DropdownButtonHideUnderline(
  //     child: DropdownButton2(
  //       items: _addDividersAfterItems(widget.languages),
  //       value: context.read<GlobalProvider>().selectedLanguage,
  //       onChanged: (newValue) {
  //         selectedLanguage = newValue;
  //         appLanguage.toggleLocale(newValue!);
  //       },
  //       hint: Text(
  //         'Select a value!',
  //         style: widget.isMobile
  //             ? AppStyle.tabletPortraitDropdownHintText
  //             : AppStyle.mobileDropdownHintText,
  //       ),
  //       buttonStyleData: ButtonStyleData(
  //         height: widget.isMobile ? 82.h : 52.h,
  //         width: widget.isMobile ? 556.w : 384.w,
  //         padding: EdgeInsets.only(
  //           left: 17.w,
  //           right: (14.42).w,
  //         ),
  //         decoration: BoxDecoration(
  //           borderRadius: BorderRadius.circular(6),
  //           border: Border.all(
  //             width: 1.h,
  //             color: AppStyle.appGreyShade,
  //           ),
  //         ),
  //       ),
  //       dropdownStyleData: DropdownStyleData(
  //         maxHeight: widget.isMobile ? 248.h : 164.h,
  //         decoration: BoxDecoration(
  //             color: AppStyle.appWhite,
  //             borderRadius: BorderRadius.circular(6),
  //             border: Border.all(
  //               width: 1.h,
  //               color: AppStyle.appGreyShade,
  //             )),
  //       ),
  //       menuItemStyleData: MenuItemStyleData(
  //         customHeights: _getCustomItemsHeights(),
  //       ),
  //       iconStyleData: const IconStyleData(
  //           icon: Icon(
  //         Icons.keyboard_arrow_down_outlined,
  //         color: AppStyle.appGreyShade,
  //       )),
  //     ),
  //   );
  // }
}
