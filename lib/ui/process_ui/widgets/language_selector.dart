/*
 * Copyright (c) Modular Open Source Identity Platform
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *
*/

import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';

import 'package:provider/provider.dart';
import 'package:registration_client/model/process.dart';

import 'package:registration_client/provider/global_provider.dart';
import 'package:registration_client/provider/registration_task_provider.dart';

import 'package:registration_client/ui/process_ui/new_process.dart';
import 'package:registration_client/ui/widgets/language_component.dart';
import 'package:registration_client/utils/app_config.dart';
import 'package:flutter_gen/gen_l10n/app_localizations.dart';

class LanguageSelector extends StatefulWidget {
  const LanguageSelector({super.key, required this.newProcess});
  final Process newProcess;

  @override
  State<LanguageSelector> createState() => _LanguageSelectorState();
}

class _LanguageSelectorState extends State<LanguageSelector> {
  bool isMobile = true;
  String? notificationLanguage;
  String? mandatoryLanguage;
  _getRegistrationError() {
    return context.read<RegistrationTaskProvider>().registrationStartError;
  }

  _triggerNavigation() {
    Navigator.pushNamed(context, NewProcess.routeName,
        arguments: {"process": widget.newProcess});
  }

  _showInSnackBar(String value) {
    ScaffoldMessenger.of(context).showSnackBar(
      SnackBar(
        content: Text(value),
      ),
    );
  }

  _navigateBack() {
    Navigator.of(context).pop();
  }

  _getRegistrationLanguageList() {
    return context.read<GlobalProvider>().chosenLang.map((e) {
      return context.read<GlobalProvider>().langToCode(e) as String;
    }).toList();
  }

  _startRegistration(List<String> langList) async {
    await context.read<RegistrationTaskProvider>().startRegistration(langList);
  }

  _navigateToConsentPage() async {
    context.read<GlobalProvider>().fieldDisplayValues = {};
    await context.read<GlobalProvider>().fieldValues(widget.newProcess);

    List<String> langList = _getRegistrationLanguageList();
    await _startRegistration(langList);
    _addNotificationLanguage();
    String registrationStartError = _getRegistrationError();
    _navigateBack();
    if (registrationStartError.isEmpty) {
      _triggerNavigation();
    } else {
      _showInSnackBar(registrationStartError);
    }
  }

  _addNotificationLanguage() {
    context.read<RegistrationTaskProvider>().addDemographicField(
        "preferredLang",
        context.read<GlobalProvider>().fieldInputValue["preferredLang"] ?? "");
  }

  @override
  void initState() {
    String lang = context.read<GlobalProvider>().mandatoryLanguages.first!;
    mandatoryLanguage =
        context.read<GlobalProvider>().codeToLanguageMapper[lang];
    _languageSelectorPageLoadedAudit();
    super.initState();
  }

  _languageSelectorPageLoadedAudit() async {
    await context
        .read<GlobalProvider>()
        .getAudit("REG-LOAD-006", "REG-MOD-103");
  }

  _getNotificationLabel() {
    String notificationLanguage = "";
    context.watch<GlobalProvider>().chosenLang.forEach((element) {
      String code =
          context.read<GlobalProvider>().languageToCodeMapper[element]!;
      notificationLanguage +=
          " / ${AppLocalizations.of(context)!.notificationLanguage(code)}";
    });
    return notificationLanguage.substring(3);
  }

  @override
  Widget build(BuildContext context) {
    int minLanguage = context.read<GlobalProvider>().minLanguageCount;
    int maxLanguage = context.read<GlobalProvider>().maxLanguageCount;
    isMobile = MediaQuery.of(context).orientation == Orientation.portrait;
    return AlertDialog(
      title: Text(
        AppLocalizations.of(context)!.select_language,
        style: TextStyle(
            fontSize: isMobile && !isMobileSize ? 24 : 18,
            fontWeight: FontWeight.bold),
      ),
      contentPadding: EdgeInsets.symmetric(
          horizontal: isMobileSize ? 5.w : 20.w, vertical: 20.h),
      content: SizedBox(
        width: isMobile && !isMobileSize ? 644.w : 824.32.w,
        child: SingleChildScrollView(
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            mainAxisSize: MainAxisSize.min,
            children: [
              const Divider(),
              ListTile(
                minLeadingWidth: 0,
                title: Text(
                  AppLocalizations.of(context)!.select_two_languages,
                  style: TextStyle(
                    fontSize: isMobileSize ? 14 : 22,
                  ),
                ),
                leading: const Icon(
                  Icons.check,
                  color: Colors.green,
                ),
              ),
              ListTile(
                minLeadingWidth: 0,
                title: Text(
                  AppLocalizations.of(context)!
                      .language_mandatory(mandatoryLanguage!),
                  style: TextStyle(
                    fontSize: isMobileSize ? 14 : 22,
                  ),
                ),
                leading: const Icon(
                  Icons.check,
                  color: Colors.green,
                ),
              ),
              SizedBox(
                height: 45.h,
              ),
              Padding(
                padding: EdgeInsets.symmetric(
                  horizontal: 10.25.w,
                ),
                child: Wrap(
                    spacing: 8.w,
                    runSpacing: 8.h,
                    children:
                        context.watch<GlobalProvider>().languages.map((e) {
                      return LanguageComponent(
                        title: context
                            .watch<GlobalProvider>()
                            .codeToLanguageMapper[e]!,
                        isDisabled: context
                                .read<GlobalProvider>()
                                .disabledLanguageMap[e] ??
                            false,
                        isSelected: context
                            .read<GlobalProvider>()
                            .chosenLang
                            .contains(context
                                .watch<GlobalProvider>()
                                .codeToLanguageMapper[e]!),
                        onTap: () {
                          context.read<GlobalProvider>().addRemoveLang(
                              context
                                  .read<GlobalProvider>()
                                  .codeToLanguageMapper[e]!,
                              !context
                                  .read<GlobalProvider>()
                                  .chosenLang
                                  .contains(context
                                      .read<GlobalProvider>()
                                      .codeToLanguageMapper[e]!));
                        },
                        isMobile: true,
                        isFreezed:
                            context.read<GlobalProvider>().mandatoryLanguageMap[
                                    context
                                        .watch<GlobalProvider>()
                                        .codeToLanguageMapper[e]!] ??
                                false,
                      );
                    }).toList()),
              ),
              SizedBox(
                height: isMobile ? 42.h : 31.h,
              ),
              Padding(
                padding: EdgeInsets.symmetric(
                  horizontal: 10.25.w,
                ),
                child: Text(
                  _getNotificationLabel(),
                  style: TextStyle(
                    fontSize: isMobileSize ? 10 : 16,
                    fontWeight: semiBold,
                  ),
                ),
              ),
              SizedBox(
                height: 15.h,
              ),
              Padding(
                padding: EdgeInsets.symmetric(
                  horizontal: 10.25.w,
                ),
                child: Wrap(
                  spacing: 8.w,
                  runSpacing: 8.h,
                  children: context
                      .watch<GlobalProvider>()
                      .notificationLanguages
                      .map((e) {
                    return LanguageComponent(
                      title: e!,
                      isDisabled: false,
                      isSelected: context
                              .watch<GlobalProvider>()
                              .fieldInputValue["preferredLang"] ==
                          e,
                      onTap: () {
                        context.read<GlobalProvider>().setInputMapValue(
                              "preferredLang",
                              e,
                              context.read<GlobalProvider>().fieldInputValue,
                            );
                      },
                      isMobile: isMobile,
                      isFreezed: false,
                    );
                  }).toList(),
                ),
              ),
              SizedBox(
                height: 24.h,
              ),
              Container(
                padding: const EdgeInsets.all(12),
                width: double.infinity,
                margin: const EdgeInsets.symmetric(horizontal: 16),
                color: const Color(0xffFFFAF0),
                child: Text(
                  AppLocalizations.of(context)!.language_selector_note,
                  style: TextStyle(
                    color: const Color(
                      0xff764B00,
                    ),
                    fontSize: isMobileSize ? 14 : 22,
                  ),
                ),
              ),
            ],
          ),
        ),
      ),
      actions: [
        const Divider(),
        SizedBox(
          height: isMobile ? 30.h : 18.h,
        ),
        Row(
          children: [
            isMobileSize
                ? const SizedBox()
                : const Expanded(
                    child: SizedBox(),
                  ),
            isMobileSize
                ? const SizedBox()
                : const Expanded(
                    child: SizedBox(),
                  ),
            Expanded(
              child: OutlinedButton(
                onPressed: () {
                  Navigator.of(context).pop();
                },
                child: SizedBox(
                  height: isMobile && !isMobileSize ? 62.h : 37.h,
                  child: Center(
                    child: Text(
                      AppLocalizations.of(context)!.cancel,
                      style: TextStyle(
                        fontSize: isMobile && !isMobileSize ? 18 : 14,
                      ),
                    ),
                  ),
                ),
              ),
            ),
            const SizedBox(
              width: 10,
            ),
            Expanded(
              child: ElevatedButton(
                onPressed: () {
                  if (context
                              .read<GlobalProvider>()
                              .chosenLang
                              .length >=
                          minLanguage &&
                      context.read<GlobalProvider>().chosenLang.length <=
                          maxLanguage &&
                      context
                              .read<GlobalProvider>()
                              .fieldInputValue["preferredLang"] !=
                          null) {
                    _navigateToConsentPage();
                  }
                },
                style: ButtonStyle(
                  backgroundColor: (context
                                  .read<GlobalProvider>()
                                  .chosenLang
                                  .length >=
                              minLanguage &&
                          context.read<GlobalProvider>().chosenLang.length <=
                              maxLanguage &&
                          context
                                  .read<GlobalProvider>()
                                  .fieldInputValue["preferredLang"] !=
                              null)
                      ? MaterialStateProperty.all<Color>(solidPrimary)
                      : MaterialStateProperty.all<Color>(Colors.grey),
                ),
                child: SizedBox(
                  height: isMobile && !isMobileSize ? 62.h : 37.h,
                  child: Center(
                    child: Text(
                      AppLocalizations.of(context)!.submit,
                      style: TextStyle(
                        fontSize: isMobile && !isMobileSize ? 18 : 14,
                      ),
                    ),
                  ),
                ),
              ),
            ),
            SizedBox(
              width: 20.w,
            ),
          ],
        ),
        SizedBox(
          height: isMobile ? 21.h : 12.6.h,
        ),
      ],
      shape: const RoundedRectangleBorder(
        borderRadius: BorderRadius.all(
          Radius.circular(12),
        ),
      ),
    );
  }
}
