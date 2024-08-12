/*
 * Copyright (c) Modular Open Source Identity Platform
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *
*/

import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';

import 'package:provider/provider.dart';
import 'package:registration_client/pigeon/dynamic_response_pigeon.dart';
import 'package:registration_client/provider/registration_task_provider.dart';
import 'package:registration_client/utils/app_config.dart';

import '../../../model/field.dart';
import '../../../provider/global_provider.dart';
import 'custom_label.dart';

class GenderControl extends StatefulWidget {
  const GenderControl(
      {super.key, required this.field, required this.validation});

  final Field field;
  final RegExp validation;

  @override
  State<GenderControl> createState() => _CustomDynamicDropDownState();
}

class _CustomDynamicDropDownState extends State<GenderControl> {
  late String selected;
  late GlobalProvider globalProvider;
  late RegistrationTaskProvider registrationTaskProvider;
  late List<DynamicFieldData?> fieldValueData;

  @override
  void initState() {
    globalProvider = Provider.of<GlobalProvider>(context, listen: false);
    registrationTaskProvider =
        Provider.of<RegistrationTaskProvider>(context, listen: false);
    String lang = globalProvider.mandatoryLanguages[0]!;
    WidgetsBinding.instance.addPostFrameCallback((_) async {
      if (context
          .read<GlobalProvider>()
          .fieldInputValue
          .containsKey(widget.field.id ?? "")) {
        await _getSelectedValueFromMap(lang);
      } else {
        fieldValueData = await context
            .read<RegistrationTaskProvider>()
            .getFieldValues(
                widget.field.subType!, lang, globalProvider.chosenLang);
        setState(() {
          selected = fieldValueData[0]!.name;
        });
        saveData(fieldValueData[0]!.code, fieldValueData[0]!.name);
        _saveDataToMap(selected);
      }
    });

    super.initState();
  }

  void saveData(value, name) {
    if (widget.field.type == 'simpleType') {
      for (var element in globalProvider.chosenLang) {
        String code = globalProvider.languageToCodeMapper[element]!;
        registrationTaskProvider.addSimpleTypeDemographicField(
            widget.field.id ?? "", value, code);
      }
    } else {
      registrationTaskProvider.addDemographicField(
          widget.field.id ?? "", value);
    }
  }

  void _saveDataToMap(value) {
    String lang = globalProvider.mandatoryLanguages[0]!;
    if (widget.field.type == 'simpleType') {
      globalProvider.setLanguageSpecificValue(
        widget.field.id ?? "",
        value,
        lang,
        globalProvider.fieldInputValue,
      );
    } else {
      globalProvider.setInputMapValue(
        widget.field.id ?? "",
        value,
        globalProvider.fieldInputValue,
      );
    }
  }

  Future<void> _getSelectedValueFromMap(String lang) async {
    List<DynamicFieldData?> data =
        await registrationTaskProvider.getFieldValues(
            widget.field.subType!, "eng", globalProvider.chosenLang);
    String response = data[0]!.name;
    if (widget.field.type == 'simpleType') {
      if ((globalProvider.fieldInputValue[widget.field.id ?? ""]
              as Map<String, dynamic>)
          .containsKey(lang)) {
        response = globalProvider.fieldInputValue[widget.field.id ?? ""][lang];
      }
    } else {
      response = globalProvider.fieldInputValue[widget.field.id ?? ""];
    }
    setState(() {
      selected = response;
    });
  }

  // Future<List<Map<String, String?>>> _getFieldValues(
  //     String fieldId, String langCode) async {
  //   List<List<DynamicFieldData?>> labelsData = [];
  //   for (var lang in globalProvider.chosenLang) {
  //     String langC = globalProvider.langToCode(lang);
  //     List<DynamicFieldData?> data = await registrationTaskProvider
  //         .getFieldValues(fieldId, langC, globalProvider.chosenLang);
  //
  //     if (data.isEmpty) {
  //       data = await registrationTaskProvider
  //           .getFieldValues(fieldId, 'eng', globalProvider.chosenLang);
  //     }
  //     labelsData.add(data);
  //   }
  //
  //   List<Map<String, String?>> labels = [];
  //
  //   for (var i = 0; i < labelsData[0].length; i++) {
  //     labels.add({});
  //     List<String> choosenLang = globalProvider.chosenLang;
  //     for (var j = 0; j < choosenLang.length; j++) {
  //       labels[labels.length - 1]
  //           .putIfAbsent(choosenLang[j], () => labelsData[j][i]!.name);
  //     }
  //   }
  //   return labels;
  // }

  Future<List<Map<String, String?>>> _getFieldValues(
      String fieldId, String langCode) async {
    // Fetch all languages in parallel
    List<String> chosenLangs = globalProvider.chosenLang;
    List<Future<List<DynamicFieldData?>>> futures = chosenLangs.map((lang) async {
      String langC = globalProvider.langToCode(lang);
      List<DynamicFieldData?> data = await registrationTaskProvider
          .getFieldValues(fieldId, langC, globalProvider.chosenLang);
      if (data.isEmpty) {
        data = await registrationTaskProvider.getFieldValues(
            fieldId, 'eng', globalProvider.chosenLang);
      }
      return data;
    }).toList();

    // Await all futures
    List<List<DynamicFieldData?>> labelsData = await Future.wait(futures);

    // Process the data into the desired format
    List<Map<String, String?>> labels = [];
    int itemCount = labelsData.isNotEmpty ? labelsData[0].length : 0;
    for (var i = 0; i < itemCount; i++) {
      Map<String, String?> labelMap = {};
      for (var j = 0; j < chosenLangs.length; j++) {
        labelMap[chosenLangs[j]] = labelsData[j][i]?.name;
      }
      labels.add(labelMap);
    }

    return labels;
  }


  @override
  Widget build(BuildContext context) {
    bool isPortrait =
        MediaQuery.of(context).orientation == Orientation.portrait;
    String mandatoryLangCode = globalProvider.mandatoryLanguages[0] ?? "eng";
    String mandatoryLang =
        globalProvider.codeToLanguageMapper[mandatoryLangCode] ?? "English";

    return FutureBuilder(
        future: _getFieldValues(widget.field.subType!, "eng"),
        builder: (BuildContext context,
            AsyncSnapshot<List<Map<String, String?>>> snapshot) {
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
                  CustomLabel(field: widget.field),
                  const SizedBox(
                    height: 10,
                  ),
                  snapshot.hasData
                      ? Row(
                          crossAxisAlignment: CrossAxisAlignment.start,
                          children: List.generate(
                            snapshot.data!.length + 1,
                            (index) {
                              if (index == 0) {
                                return Padding(
                                  padding: const EdgeInsets.only(right: 8),
                                  child: Column(
                                    children: globalProvider.chosenLang
                                        .map((e) => Chip(
                                              label: Text(e),
                                              shape: RoundedRectangleBorder(
                                                borderRadius: BorderRadius.all(
                                                    Radius.zero),
                                                side:
                                                    BorderSide(color: appWhite),
                                              ),
                                              labelStyle: Theme.of(context)
                                                  .textTheme
                                                  .bodySmall,
                                              backgroundColor: Colors.white,
                                            ))
                                        .toList(),
                                  ),
                                );
                              }
                              Map<String, String?> e =
                                  snapshot.data![index - 1];
                              bool chipSelected =
                                  selected.toString().toLowerCase() ==
                                      e[mandatoryLang].toString().toLowerCase();
                              return Padding(
                                padding: const EdgeInsets.only(right: 6),
                                child: Column(
                                  children: globalProvider.chosenLang.map(
                                    (lang) {
                                      bool isMandatoryLang =
                                          lang == mandatoryLang;
                                      return InkWell(
                                        splashColor: Colors.transparent,
                                        onTap: () {
                                          setState(() {
                                            selected = e[mandatoryLang] ?? "";
                                          });
                                          for (var e in fieldValueData) {
                                            if (e!.name == selected) {
                                              saveData(e.code, e.name);
                                            }
                                          }
                                          _saveDataToMap(e[mandatoryLang]);
                                        },
                                        child: ChoiceChip(
                                          label: SizedBox(
                                            width: 50,
                                            child: Text(
                                              e[lang] ?? "",
                                              overflow: TextOverflow.clip,
                                              textAlign: TextAlign.center,
                                              style: TextStyle(
                                                  fontWeight: isMandatoryLang
                                                      ? FontWeight.w500
                                                      : FontWeight.w400,
                                                  fontSize: 12,
                                                  color: chipSelected
                                                      ? isMandatoryLang
                                                          ? Colors.white
                                                          : solidPrimary
                                                      : Colors.black),
                                            ),
                                          ),
                                          labelPadding:
                                              const EdgeInsets.symmetric(
                                                  vertical: 4, horizontal: 8),
                                          selected: chipSelected,
                                          elevation: 0,
                                          selectedColor: isMandatoryLang
                                              ? solidPrimary
                                              : const Color(0xffEFF3FF),
                                          backgroundColor: Colors.white,
                                          disabledColor: Colors.white,
                                          shape: chipSelected
                                              ? null
                                              : RoundedRectangleBorder(
                                                  side: BorderSide(
                                                      color: isMandatoryLang
                                                          ? solidPrimary
                                                          : const Color(
                                                              0xffC2D0F2),
                                                      width: 1),
                                                  borderRadius:
                                                      BorderRadius.circular(
                                                          32)),
                                        ),
                                      );
                                    },
                                  ).toList(),
                                ),
                              );
                            },
                          ),
                        )
                      : SizedBox(
                          width: MediaQuery.of(context).size.width,
                          height: 50,
                          child: const Center(
                              child: SizedBox(
                                  height: 25,
                                  width: 25,
                                  child: CircularProgressIndicator())),
                        ),
                ],
              ),
            ),
          );
        });
  }
}
