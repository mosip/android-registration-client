import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';

import 'package:provider/provider.dart';
import 'package:registration_client/model/process.dart';

import 'package:registration_client/provider/global_provider.dart';
import 'package:registration_client/provider/registration_task_provider.dart';

import 'package:registration_client/ui/process_ui/new_process.dart';
import 'package:registration_client/ui/widgets/language_component.dart';
import 'package:registration_client/utils/app_config.dart';

class LanguageSelector extends StatefulWidget {
  const LanguageSelector({super.key, required this.newProcess});
  final Process newProcess;

  @override
  State<LanguageSelector> createState() => _LanguageSelectorState();
}

class _LanguageSelectorState extends State<LanguageSelector> {
  bool isMobile = true;

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
    context.read<GlobalProvider>().getThresholdValues();
    context.read<GlobalProvider>().fieldDisplayValues = {};
    await context.read<GlobalProvider>().fieldValues(widget.newProcess);

    List<String> langList = _getRegistrationLanguageList();
    await _startRegistration(langList);
    String registrationStartError = _getRegistrationError();
    _navigateBack();
    if (registrationStartError.isEmpty) {
      _triggerNavigation();
    } else {
      _showInSnackBar(registrationStartError);
    }
  }

  @override
  void initState() {
    _languageSelectorPageLoadedAudit();
    super.initState();
  }

  _languageSelectorPageLoadedAudit() async {
    await context
        .read<GlobalProvider>()
        .getAudit("REG-LOAD-006", "REG-MOD-103");
  }

  @override
  Widget build(BuildContext context) {
    int minLanguage = context.read<GlobalProvider>().minLanguageCount;
    int maxLanguage = context.read<GlobalProvider>().maxLanguageCount;
    isMobile = MediaQuery.of(context).orientation == Orientation.portrait;
    return AlertDialog(
      title: Text(
        "Select Language",
        style: TextStyle(
            fontSize: isMobile ? 24 : 18, fontWeight: FontWeight.bold),
      ),
      contentPadding: EdgeInsets.symmetric(horizontal: 20.w, vertical: 20.h),
      content: SizedBox(
        width: isMobile ? 644.w : 824.32.w,
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          mainAxisSize: MainAxisSize.min,
          children: [
            const Divider(),
            ListTile(
              minLeadingWidth: 0,
              title: Text(
                "Please select any $minLanguage language for data entry",
                style: const TextStyle(
                  fontSize: 22,
                ),
              ),
              leading: const Icon(
                Icons.check,
                color: Colors.green,
              ),
            ),
            const ListTile(
              minLeadingWidth: 0,
              title: Text(
                "English is Mandatory for the residents to select as per government guidelines. The order of selection will lead to page view for demographic data.",
                style: TextStyle(
                  fontSize: 22,
                ),
              ),
              leading: Icon(
                Icons.check,
                color: Colors.green,
              ),
            ),
            SizedBox(
              height: 45.h,
            ),
            Padding(
              padding: EdgeInsets.symmetric(horizontal: 30.25.w),
              child: Wrap(
                  spacing: 8.w,
                  runSpacing: 8.h,
                  children: context.watch<GlobalProvider>().languages.map((e) {
                    return LanguageComponent(
                      title: context
                          .watch<GlobalProvider>()
                          .codeToLanguageMapper[e]!,
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
                            !context.read<GlobalProvider>().chosenLang.contains(
                                context
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
            // (size.width >= 512)
            //     ? Row(mainAxisAlignment: MainAxisAlignment.start, children: [
            //         ...context.watch<GlobalProvider>().languageMap.entries.map(
            //               (e) => Row(
            //                 children: [
            //                   const SizedBox(
            //                     width: 40,
            //                   ),
            //                   Checkbox(
            //                     value: e.value,
            //                     onChanged: context
            //                                 .read<GlobalProvider>()
            //                                 .mandatoryLanguageMap[e.key] ??
            //                             false
            //                         ? null
            //                         : (bool? newValue) {
            //                             if (!(context
            //                                     .read<GlobalProvider>()
            //                                     .mandatoryLanguageMap[e.key] ??
            //                                 false)) {
            //                               context
            //                                   .read<GlobalProvider>()
            //                                   .addRemoveLang(e.key, newValue!);
            //                             }
            //                           },
            //                     activeColor: solidPrimary,
            //                   ),
            //                   Text(
            //                     e.key,
            //                     style: Theme.of(context)
            //                         .textTheme
            //                         .titleSmall
            //                         ?.copyWith(
            //                             color: context
            //                                             .read<GlobalProvider>()
            //                                             .mandatoryLanguageMap[
            //                                         e.key] ??
            //                                     false
            //                                 ? Colors.grey
            //                                 : const Color(0xff333333)),
            //                   )
            //                 ],
            //               ),
            //             )
            //       ])
            //     : Column(
            //         children: [
            //           ...context
            //               .watch<GlobalProvider>()
            //               .languageMap
            //               .entries
            //               .map(
            //                 (e) => Row(
            //                   children: [
            //                     const SizedBox(
            //                       width: 40,
            //                     ),
            //                     Checkbox(
            //                       value: e.value,
            //                       onChanged: context
            //                                   .read<GlobalProvider>()
            //                                   .mandatoryLanguageMap[e.key] ??
            //                               false
            //                           ? null
            //                           : (bool? newValue) {
            //                               if (!(context
            //                                           .read<GlobalProvider>()
            //                                           .mandatoryLanguageMap[
            //                                       e.key] ??
            //                                   false)) {
            //                                 context
            //                                     .read<GlobalProvider>()
            //                                     .addRemoveLang(
            //                                         e.key, newValue!);
            //                               }
            //                             },
            //                       activeColor: solidPrimary,
            //                     ),
            //                     Text(
            //                       e.key,
            //                       style: Theme.of(context)
            //                           .textTheme
            //                           .titleSmall
            //                           ?.copyWith(
            //                             color: context
            //                                         .read<GlobalProvider>()
            //                                         .langToCode(e.key) ==
            //                                     context
            //                                         .read<GlobalProvider>()
            //                                         .selectedLanguage
            //                                 ? Colors.grey
            //                                 : AppStyle.appBlackShade1,
            //                           ),
            //                     )
            //                   ],
            //                 ),
            //               )
            //         ],
            //       ),
            // const Spacer(),
            SizedBox(
              height: 31.h,
            ),
            Container(
              padding: const EdgeInsets.all(12),
              width: double.infinity,
              margin: const EdgeInsets.symmetric(horizontal: 16),
              color: const Color(0xffFFFAF0),
              child: const Text(
                "Please note that the language might be based on data entered during Pre-registration.",
                style: TextStyle(
                  color: Color(
                    0xff764B00,
                  ),
                  fontSize: 22,
                ),
              ),
            ),
          ],
        ),
      ),
      actions: [
        const Divider(),
        SizedBox(
          height: isMobile ? 30.h : 18.h,
        ),
        Row(
          children: [
            SizedBox(
              width: 20.w,
            ),
            Expanded(
              child: OutlinedButton(
                onPressed: () {
                  Navigator.of(context).pop();
                },
                child: SizedBox(
                  height: isMobile ? 62.h : 37.h,
                  child: Center(
                    child: Text(
                      "CANCEL",
                      style: TextStyle(
                        fontSize: isMobile ? 18 : 14,
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
                  _navigateToConsentPage();
                  // if (context.read<GlobalProvider>().chosenLang.length >=
                  //         minLanguage &&
                  //     context.read<GlobalProvider>().chosenLang.length <=
                  //         maxLanguage) {
                    
                  // }
                },
                style: ButtonStyle(
                  backgroundColor: (context
                                  .read<GlobalProvider>()
                                  .chosenLang
                                  .length >=
                              minLanguage &&
                          context.read<GlobalProvider>().chosenLang.length <=
                              maxLanguage)
                      ? MaterialStateProperty.all<Color>(solidPrimary)
                      : MaterialStateProperty.all<Color>(Colors.grey),
                ),
                child: SizedBox(
                  height: isMobile ? 62.h : 37.h,
                  child: Center(
                    child: Text(
                      "SUBMIT",
                      style: TextStyle(
                        fontSize: isMobile ? 18 : 14,
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
