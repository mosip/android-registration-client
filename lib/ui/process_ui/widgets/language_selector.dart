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
import 'package:registration_client/ui/process_ui/update_process.dart';
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
  late GlobalProvider globalProvider;
  late RegistrationTaskProvider registrationTaskProvider;

  _triggerNavigation() {
    if (widget.newProcess.id == "NEW") {
      Navigator.pushNamed(context, NewProcess.routeName,
          arguments: {"process": widget.newProcess});
    }

    if (widget.newProcess.id == "UPDATE") {
      Navigator.pushNamed(context, UpdateProcess.routeName,
          arguments: {"process": widget.newProcess});
    }
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
    return globalProvider.chosenLang.map((e) {
      return globalProvider.langToCode(e) as String;
    }).toList();
  }

  _navigateToConsentPage() async {
    context.read<GlobalProvider>().fieldDisplayValues = {};
    await context.read<GlobalProvider>().fieldValues(widget.newProcess);
    globalProvider.getThresholdValues();
    globalProvider.fieldDisplayValues = {};
    await globalProvider.fieldValues(widget.newProcess);

    List<String> langList = _getRegistrationLanguageList();
    await registrationTaskProvider.startRegistration(
        langList,
        widget.newProcess.flow! == "UPDATE"
            ? "Update"
            : widget.newProcess.flow!,
        widget.newProcess.id!);
    registrationTaskProvider.addDemographicField("preferredLang",
        globalProvider.fieldInputValue["preferredLang"].toString());
    String registrationStartError =
        registrationTaskProvider.registrationStartError;
    _navigateBack();
    if (registrationStartError.isEmpty) {
      _triggerNavigation();
    } else {
      _showInSnackBar(registrationStartError);
    }
  }

  @override
  void initState() {
    globalProvider = Provider.of<GlobalProvider>(context, listen: false);
    registrationTaskProvider =
        Provider.of<RegistrationTaskProvider>(context, listen: false);
    String lang = globalProvider.mandatoryLanguages.first!;
    mandatoryLanguage = globalProvider.codeToLanguageMapper[lang];
    globalProvider.getAudit("REG-LOAD-006", "REG-MOD-103");
    super.initState();
  }

  _getDataEntryLabel() {
    String dataEntryLanguage = "";
    context.watch<GlobalProvider>().chosenLang.forEach((element) {
      String code = context.read<GlobalProvider>().selectedLanguage;
      dataEntryLanguage +=
          " / ${AppLocalizations.of(context)!.dataEntryLanguage(code)}";
    });
    return dataEntryLanguage.substring(3);
  }

  _getNotificationLabel() {
    String notificationLanguage = "";
    context.watch<GlobalProvider>().chosenLang.forEach((element) {
      String code = context.read<GlobalProvider>().selectedLanguage;
      notificationLanguage +=
          " / ${AppLocalizations.of(context)!.notificationLanguage(code)}";
    });
    return notificationLanguage.substring(3);
  }

  @override
  Widget build(BuildContext context) {
    int minLanguage = globalProvider.minLanguageCount;
    int maxLanguage = globalProvider.maxLanguageCount;
    isMobile = MediaQuery.of(context).orientation == Orientation.portrait;

    debugPrint(context.watch<GlobalProvider>().languages.length.toString());
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
            // mainAxisSize: MainAxisSize.min,
            children: [
              const Divider(),
              (context.watch<GlobalProvider>().languages.length > 1) ?
              ListTile(
                minLeadingWidth: 0,
                title: Text(
                  AppLocalizations.of(context)!.select_two_languages,
                  style: TextStyle(
                    fontSize: isMobileSize ? 14 : 22,
                  ),
                ),
                leading: const Icon(
                  Icons.circle_rounded,
                  color: bulletPointColor,
                  size: 15,
                ),
              ) : const SizedBox.shrink(),
              (context.watch<GlobalProvider>().languages.length > 1) ?
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
                  Icons.circle_rounded,
                  color: bulletPointColor,
                  size: 15,
                ),
              ) : const SizedBox.shrink(),
              (context.watch<GlobalProvider>().languages.length > 1) ?
              SizedBox(
                height: 45.h,
              ) : const SizedBox.shrink(),
              Padding(
                padding: EdgeInsets.symmetric(
                  horizontal: 10.25.w,
                ),
                child: Text(
                  _getDataEntryLabel(),
                  style: TextStyle(
                    fontSize: isMobileSize ? 15 : 18,
                    fontWeight: bold,
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
                    children:
                        context.watch<GlobalProvider>().languages.map((e) {
                      return LanguageComponent(
                        title: context
                                .watch<GlobalProvider>()
                                .codeToLanguageMapper[e] ??
                            "English",
                        isDisabled:
                            globalProvider.disabledLanguageMap[e] ?? false,
                        isSelected: globalProvider.chosenLang.contains(context
                                .watch<GlobalProvider>()
                                .codeToLanguageMapper[e] ??
                            "English"),
                        onTap: () {
                          globalProvider.addRemoveLang(
                              globalProvider.codeToLanguageMapper[e]!,
                              !globalProvider.chosenLang.contains(
                                  globalProvider.codeToLanguageMapper[e] ??
                                      "English"));
                        },
                        isMobile: true,
                        isFreezed: globalProvider.mandatoryLanguageMap[context
                                    .watch<GlobalProvider>()
                                    .codeToLanguageMapper[e] ??
                                "English"] ??
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
                    fontSize: isMobileSize ? 15 : 18,
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
                      title: e!.name,
                      isDisabled: false,
                      isSelected: context
                              .watch<GlobalProvider>()
                              .fieldInputValue["preferredLang"] ==
                          e,
                      onTap: () {
                        globalProvider.setInputMapValue(
                          "preferredLang",
                          e,
                          globalProvider.fieldInputValue,
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
                  if (globalProvider.chosenLang.length >= minLanguage &&
                      globalProvider.chosenLang.length <= maxLanguage &&
                      globalProvider.fieldInputValue["preferredLang"] != null) {
                    _navigateToConsentPage();
                  }
                },
                style: ButtonStyle(
                  backgroundColor:
                      (globalProvider.chosenLang.length >= minLanguage &&
                              globalProvider.chosenLang.length <= maxLanguage &&
                              globalProvider.fieldInputValue["preferredLang"] !=
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
