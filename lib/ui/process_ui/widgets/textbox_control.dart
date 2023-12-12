// ignore_for_file: deprecated_member_use

import 'dart:developer';

import 'package:flutter/material.dart';

import 'package:provider/provider.dart';
import 'package:registration_client/provider/registration_task_provider.dart';
import 'package:registration_client/utils/app_style.dart';
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

  @override
  void initState() {
    super.initState();
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
      context
          .read<RegistrationTaskProvider>()
          .addSimpleTypeDemographicField(widget.e.id!, value!, lang);
    } else {
      context
          .read<RegistrationTaskProvider>()
          .addDemographicField(widget.e.id!, value!);
    }
  }

  void _saveDataToMap(value, lang) {
    if (widget.e.type == 'simpleType') {
      context.read<GlobalProvider>().setLanguageSpecificValue(
            widget.e.id!,
            value!,
            lang,
            context.read<GlobalProvider>().fieldInputValue,
          );
    } else {
      context.read<GlobalProvider>().setInputMapValue(
            widget.e.id!,
            value!,
            context.read<GlobalProvider>().fieldInputValue,
          );
    }
  }

  String _getDataFromMap(String lang) {
    String response = "";
    if (context
        .read<GlobalProvider>()
        .fieldInputValue
        .containsKey(widget.e.id)) {
      if (widget.e.type == 'simpleType') {
        if ((context.read<GlobalProvider>().fieldInputValue[widget.e.id]
                as Map<String, dynamic>)
            .containsKey(lang)) {
          response =
              context.read<GlobalProvider>().fieldInputValue[widget.e.id][lang];
        }
      } else {
        response = context.read<GlobalProvider>().fieldInputValue[widget.e.id];
      }
    }
    return response;
  }

  @override
  Widget build(BuildContext context) {
    List<String> choosenLang = context.read<GlobalProvider>().chosenLang;
    if (!(widget.e.type == "simpleType")) {
      choosenLang = ["English"];
    }

    return Card(
      elevation: 0,
      margin: const EdgeInsets.symmetric(vertical: 1, horizontal: 12),
      child: Padding(
        padding: const EdgeInsets.symmetric(vertical: 20, horizontal: 12),
        child: Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
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
              String lang = context.read<GlobalProvider>().langToCode(code);
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
                    if (lang ==
                        context.read<GlobalProvider>().mandatoryLanguages[0]) {
                      for (var target in choosenLang) {
                        if (target != "English") {
                          // ignore: use_build_context_synchronously
                          String targetCode =
                              context.read<GlobalProvider>().langToCode(target);
                          try {
                            String result = await TransliterationServiceImpl()
                                .transliterate(TransliterationOptions(
                                    input: value,
                                    sourceLanguage: lang.substring(0, 2),
                                    targetLanguage:
                                        targetCode.substring(0, 2)));
                            if (result != "") {
                              _saveDataToMap(result, targetCode);
                              saveData(result, targetCode);
                              setState(() {
                                controllerMap[targetCode]!.text = result;
                              });
                              log("Transliteration success : $result");
                            }
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
                    if (!widget.e.required! && widget.e.requiredOn!.isEmpty) {
                      if (value == null || value.isEmpty) {
                        return null;
                      } else if (!widget.validation.hasMatch(value)) {
                        return 'Invalid input';
                      }
                    }
                    if (value == null || value.isEmpty) {
                      return 'Please enter a value';
                    }
                    if (!widget.validation.hasMatch(value)) {
                      return 'Invalid input';
                    }
                    return null;
                  },
                  textAlign: (lang == 'ara') ? TextAlign.right : TextAlign.left,
                  decoration: InputDecoration(
                    border: OutlineInputBorder(
                      borderRadius: BorderRadius.circular(8.0),
                      borderSide: const BorderSide(
                          color: AppStyle.appGreyShade, width: 1),
                    ),
                    contentPadding: const EdgeInsets.symmetric(
                        vertical: 14, horizontal: 16),
                    hintText: widget.e.label![lang],
                    hintStyle: const TextStyle(
                        color: AppStyle.appBlackShade3, fontSize: 14),
                  ),
                ),
              );
            }).toList(),
          ),
        ]),
      ),
    );
  }
}
