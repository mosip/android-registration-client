// ignore_for_file: use_build_context_synchronously

import 'package:flutter/material.dart';

import 'package:provider/provider.dart';
import 'package:registration_client/model/process.dart';

import 'package:registration_client/provider/global_provider.dart';
import 'package:registration_client/provider/registration_task_provider.dart';

import 'package:registration_client/ui/process_ui/new_process.dart';
import 'package:registration_client/utils/app_config.dart';
import 'package:registration_client/utils/app_style.dart';

class LanguageSelector extends StatefulWidget {
  const LanguageSelector({super.key, required this.newProcess});
  final Process newProcess;

  @override
  State<LanguageSelector> createState() => _LanguageSelectorState();
}

class _LanguageSelectorState extends State<LanguageSelector> {
  _navigateToConsentPage() async {
    context.read<GlobalProvider>().getThresholdValues();
    context.read<GlobalProvider>().fieldDisplayValues = {};

    context.read<GlobalProvider>().fieldValues(widget.newProcess);

    Navigator.of(context).pop();
    List<String> langList = context.read<GlobalProvider>().chosenLang.map((e) {
      return context.read<GlobalProvider>().langToCode(e) as String;
    }).toList();
    // print(langList);
    await context.read<RegistrationTaskProvider>().startRegistration(langList);
    String registrationStartError =
        context.read<RegistrationTaskProvider>().registrationStartError;

    if (registrationStartError.isEmpty) {
      Navigator.pushNamed(context, NewProcess.routeName,
          arguments: {"process": widget.newProcess});
    } else {
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(
          content: Text(registrationStartError),
        ),
      );
    }
  }

  @override
  Widget build(BuildContext context) {
    final size = MediaQuery.of(context).size;
    return AlertDialog(
      title: const Text(
        "Select Language",
        style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold),
      ),
      contentPadding: const EdgeInsets.all(4),
      content: SizedBox(
        height: size.width < 512 ? size.height / 2 : size.height,
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            const Divider(),
            ListTile(
              minLeadingWidth: 0,
              title: Text(
                "Please select any two language for data entry",
                style: Theme.of(context).textTheme.bodyMedium,
              ),
              leading: const Icon(
                Icons.check,
                color: Colors.green,
              ),
            ),
            ListTile(
              minLeadingWidth: 0,
              title: Text(
                "English is Mandatory for the residents to select as per government guidelines. The order of selection will lead to page view for demographic data.",
                style: Theme.of(context).textTheme.bodyMedium,
              ),
              leading: const Icon(
                Icons.check,
                color: Colors.green,
              ),
            ),
            (size.width >= 512)
                ? Row(mainAxisAlignment: MainAxisAlignment.start, children: [
                    ...context.watch<GlobalProvider>().languageMap.entries.map(
                          (e) => Row(
                            children: [
                              const SizedBox(
                                width: 40,
                              ),
                              Checkbox(
                                  value: e.value,
                                  onChanged: e.key == "English"
                                      ? null
                                      : (bool? newValue) {
                                          if (e.key != "English") {
                                            context
                                                .read<GlobalProvider>()
                                                .addRemoveLang(
                                                    e.key, newValue!);
                                          }
                                        },
                                  fillColor:
                                      MaterialStateProperty.resolveWith<Color>(
                                          (Set<MaterialState> states) {
                                    if (states
                                        .contains(MaterialState.disabled)) {
                                      return Colors.grey;
                                    }
                                    return solidPrimary;
                                  })),
                              Text(
                                e.key,
                                style: Theme.of(context)
                                    .textTheme
                                    .titleSmall
                                    ?.copyWith(
                                        color: e.key == "English"
                                            ? Colors.grey
                                            : const Color(0xff333333)),
                              )
                            ],
                          ),
                        )
                  ])
                : Column(
                    children: [
                      ...context
                          .watch<GlobalProvider>()
                          .languageMap
                          .entries
                          .map(
                            (e) => Row(
                              children: [
                                const SizedBox(
                                  width: 40,
                                ),
                                Checkbox(
                                    value: e.value,
                                    onChanged: e.key == "English"
                                        ? null
                                        : (bool? newValue) {
                                            if (e.key != "English") {
                                              context
                                                  .read<GlobalProvider>()
                                                  .addRemoveLang(
                                                      e.key, newValue!);
                                            }
                                          },
                                    fillColor:
                                        MaterialStateProperty.resolveWith<
                                            Color>((Set<MaterialState> states) {
                                      if (states
                                          .contains(MaterialState.disabled)) {
                                        return Colors.grey;
                                      }
                                      return solidPrimary;
                                    })),
                                Text(
                                  e.key,
                                  style: Theme.of(context)
                                      .textTheme
                                      .titleSmall
                                      ?.copyWith(
                                        color: e.key == "English"
                                            ? Colors.grey
                                            : AppStyle.appBlackShade1,
                                      ),
                                )
                              ],
                            ),
                          )
                    ],
                  ),
            const Spacer(),
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
                    fontSize: 13),
              ),
            )
          ],
        ),
      ),
      actions: [
        const Divider(),
        Row(
          children: [
            Expanded(
              child: OutlinedButton(
                onPressed: () {
                  Navigator.of(context).pop();
                },
                child: const Text("CANCEL"),
              ),
            ),
            const SizedBox(
              width: 10,
            ),
            Expanded(
              child: ElevatedButton(
                onPressed: () {
                  _navigateToConsentPage();
                },
                child: const Text("SUBMIT"),
              ),
            )
          ],
        )
      ],
    );
  }
}
