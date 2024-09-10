/*
 * Copyright (c) Modular Open Source Identity Platform
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *
*/

// ignore_for_file: deprecated_member_use

import 'dart:developer';
import 'package:intl/intl.dart';

import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';

import 'package:flutter_gen/gen_l10n/app_localizations.dart';

import 'package:provider/provider.dart';
import 'package:registration_client/pigeon/dynamic_response_pigeon.dart';
import 'package:registration_client/provider/registration_task_provider.dart';
import 'package:registration_client/utils/app_config.dart';
import 'package:responsive_grid_list/responsive_grid_list.dart';

import '../../../model/field.dart';

import '../../../pigeon/transliteration_pigeon.dart';
import '../../../platform_android/transliteration_service_impl.dart';
import '../../../provider/global_provider.dart';
import '../../../utils/life_cycle_event_handler.dart';
import 'custom_label.dart';

class TextBoxControl extends StatefulWidget {
  const TextBoxControl({super.key, required this.e, required this.validation});
  final Field e;
  final RegExp validation;

  @override
  State<TextBoxControl> createState() => _TextBoxControlState();
}

class _TextBoxControlState extends State<TextBoxControl>
    with WidgetsBindingObserver {
  bool isMvelValid = true;
  Map<String, TextEditingController> controllerMap = {};
  late GlobalProvider globalProvider;
  late RegistrationTaskProvider registrationTaskProvider;
  String countryCode = "";

  @override
  void initState() {
    globalProvider = Provider.of<GlobalProvider>(context, listen: false);
    registrationTaskProvider =
        Provider.of<RegistrationTaskProvider>(context, listen: false);
    super.initState();
    getCountryCode(globalProvider.selectedLanguage);
    WidgetsBinding.instance.addObserver(LifecycleEventHandler(
      resumeCallBack: () async {
        if (mounted) {
          setState(() {
            closeKeyboard();
          });
        }
      },
      suspendingCallBack: () async {
        if (mounted) {
          setState(() {
            closeKeyboard();
          });
        }
      },
    ));
  }

  @override
  void dispose() {
    WidgetsBinding.instance.removeObserver(this);
    super.dispose();
  }

  void closeKeyboard() {
    FocusScope.of(context).unfocus();
  }

  void saveData(value, lang) {
    if (widget.e.type == 'simpleType') {
      registrationTaskProvider
          .addSimpleTypeDemographicField(widget.e.id!, value!, lang);
    } else {
      registrationTaskProvider.addDemographicField(widget.e.id!, value!);
    }
  }

  void _saveDataToMap(value, lang) {
    if (widget.e.type == 'simpleType') {
      globalProvider.setLanguageSpecificValue(
        widget.e.id!,
        value!,
        lang,
        globalProvider.fieldInputValue,
      );
    } else {
      globalProvider.setInputMapValue(
        widget.e.id!,
        value!,
        globalProvider.fieldInputValue,
      );
    }
  }

  String _getDataFromMap(String lang) {
    String response = "";
    if (globalProvider.fieldInputValue.containsKey(widget.e.id)) {
      if (widget.e.type == 'simpleType') {
        if ((globalProvider.fieldInputValue[widget.e.id]
                as Map<String, dynamic>)
            .containsKey(lang)) {
          response = globalProvider.fieldInputValue[widget.e.id][lang];
        }
      } else {
        response = globalProvider.fieldInputValue[widget.e.id];
      }
    }
    return response;
  }

  void getCountryCode(String lang) async {
    List<DynamicFieldData?> data =
    await _getFieldValues("countryCode", lang);
    for (var element in data) {
      setState(() {
        countryCode = element!.name;
        debugPrint("countryCode.........."+countryCode);
      });
    }
  }

  Future<List<DynamicFieldData?>> _getFieldValues(
      String fieldId, String langCode) async {
    List<String> selectedLang = [];
    for (var lang in globalProvider.chosenLang) {
      String langCode = globalProvider.langToCode(lang);
      selectedLang.add(langCode);
    }
    return await registrationTaskProvider.getFieldValues(
        fieldId, langCode, selectedLang);
  }

  @override
  Widget build(BuildContext context) {
    bool isPortrait =
        MediaQuery.of(context).orientation == Orientation.portrait;
    List<String> choosenLang = globalProvider.chosenLang;
    if (!(widget.e.type == "simpleType")) {
      choosenLang = [choosenLang[0]];
    }
    Map<String, String> tranliterationLangMapper = {
      "eng": "Latin",
      "fra": "fr",
      "ara": "Arabic",
      "hin": "Devanagari",
      "kan": "Kannada",
      "tam": "Tamil",
    };

    String mandatoryLanguageCode =
        globalProvider.mandatoryLanguages[0] ?? "eng";

    return Card(
      elevation: 5,
      color: pureWhite,
      margin: EdgeInsets.symmetric(
          vertical: 1.h, horizontal: isPortrait ? 16.w : 0),
      child: Padding(
        padding: EdgeInsets.symmetric(vertical: 24.h, horizontal: 16.w),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            CustomLabel(field: widget.e),
            const SizedBox(
              height: 10,
            ),
            ResponsiveGridList(
              //Setting primary listener false
              listViewBuilderOptions: ListViewBuilderOptions(primary: false),
              shrinkWrap: true,
              minItemWidth: 400,
              horizontalGridSpacing: 16,
              verticalGridSpacing: 12,
              children: choosenLang.map((code) {
                String lang = globalProvider.langToCode(code);
                  setState(() {
                    controllerMap.putIfAbsent(lang,
                        () => TextEditingController(text: _getDataFromMap(lang)));
                  });
                return Container(
                  margin: const EdgeInsets.only(bottom: 8),
                  child: TextFormField(
                    autovalidateMode: AutovalidateMode.onUserInteraction,
                    controller: controllerMap[lang],
                    textCapitalization: TextCapitalization.words,
                    onChanged: (value) async {
                      if (lang == mandatoryLanguageCode) {
                        for (var target in choosenLang) {
                          String targetCode = globalProvider.langToCode(target);
                          if (targetCode != mandatoryLanguageCode) {
                            log("$mandatoryLanguageCode ----> $targetCode");
                            try {
                              String result = await TransliterationServiceImpl()
                                  .transliterate(TransliterationOptions(
                                      input: value,
                                      sourceLanguage: "Any",
                                      targetLanguage: tranliterationLangMapper[
                                              targetCode] ??
                                          targetCode.substring(0, 2)));
                              _saveDataToMap(result, targetCode);
                              saveData(result, targetCode);
                              setState(() {
                                controllerMap[targetCode]!.text = result;
                              });
                              log("Transliteration success : $result");
                            } catch (e) {
                              log("Transliteration failed : $e");
                            }
                          }
                        }
                      }
                      _saveDataToMap(value, lang);
                      saveData(value, lang);
                    },
                    validator: (value) {
                      if (!widget.e.required!) {
                        if (widget.e.requiredOn == null ||
                            widget.e.requiredOn!.isEmpty ||
                            !(globalProvider.mvelRequiredFields[widget.e.id] ??
                                true)) {
                          if (value == null || value.isEmpty) {
                            return null;
                          } else if (!widget.validation.hasMatch(value)) {
                            return AppLocalizations.of(context)!.invalid_input;
                          }
                        }
                      }
                      // if (!widget.e.required! &&
                      //     (widget.e.requiredOn == null ||
                      //         widget.e.requiredOn!.isEmpty)) {
                      //   if (value == null || value.isEmpty) {
                      //     return null;
                      //   } else if (!widget.validation.hasMatch(value)) {
                      //     return AppLocalizations.of(context)!.invalid_input;
                      //   }
                      // }
                      if (value == null || value.isEmpty) {
                        return AppLocalizations.of(context)!
                            .enter_value_message;
                      }
                      if (!widget.validation.hasMatch(value)) {
                        return AppLocalizations.of(context)!.invalid_input;
                      }
                      return null;
                    },
                    textAlign: Bidi.isRtlLanguage(lang.substring(0, 2))
                        ? TextAlign.right
                        : TextAlign.left,
                    decoration: (widget.e.subType == "Phone" && countryCode!= "")?
                    InputDecoration(
                     prefixIcon:Padding(
                     padding: const EdgeInsets.all(10),
                     child: Text(countryCode,style: const TextStyle(fontWeight: FontWeight.bold,fontSize: 18))),
                     prefixIconConstraints: const BoxConstraints(minWidth: 0, minHeight: 0),
                     border: OutlineInputBorder(
                     borderRadius: BorderRadius.circular(8.0),
                     borderSide:
                       const BorderSide(color: appGreyShade, width: 1),
                    ),
                    contentPadding: const EdgeInsets.symmetric(
                    vertical: 14, horizontal: 16),
                    hintText: widget.e.label![lang],
                    hintStyle:
                   const TextStyle(color: appBlackShade3, fontSize: 14),
                   ):InputDecoration(
                      border: OutlineInputBorder(
                        borderRadius: BorderRadius.circular(8.0),
                        borderSide:
                            const BorderSide(color: appGreyShade, width: 1),
                      ),
                      contentPadding: const EdgeInsets.symmetric(
                          vertical: 14, horizontal: 16),
                      hintText: widget.e.label![lang],
                      hintStyle:
                          const TextStyle(color: appBlackShade3, fontSize: 14),
                    ),
                  ),
                );
              }).toList(),
            ),
          ],
        ),
      ),
    );
  }
}
