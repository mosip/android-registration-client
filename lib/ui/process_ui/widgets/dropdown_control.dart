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

import 'package:flutter_gen/gen_l10n/app_localizations.dart';

class DropDownControl extends StatefulWidget {
  const DropDownControl({
    super.key,
    required this.field,
    required this.validation,
  });

  final Field field;
  final RegExp validation;

  @override
  State<DropDownControl> createState() => _CustomDropDownState();
}

class _CustomDropDownState extends State<DropDownControl> {
  GenericData? selected;
  late GlobalProvider globalProvider;
  late RegistrationTaskProvider registrationTaskProvider;

  int? index;
  int maxLen = 0;
  List<GenericData?> list = [];

  @override
  void initState() {
    globalProvider = Provider.of<GlobalProvider>(context, listen: false);
    registrationTaskProvider =
        Provider.of<RegistrationTaskProvider>(context, listen: false);
    setHierarchyReverse();
    super.initState();
  }

  setHierarchyReverse() {
    maxLen = globalProvider.hierarchyReverse.length;
  }

  @override
  void didChangeDependencies() {
    super.didChangeDependencies();
    setState(() {
      index = globalProvider.hierarchyReverse.indexOf(widget.field.subType!);
    });
    _getOptionsList();
  }

  void saveData(value) {
    for (int i = index! + 1; i < maxLen; i++) {
      registrationTaskProvider
          .removeDemographicField(globalProvider.hierarchyReverse[i]);
    }
    if (value != null) {
      if (widget.field.type == 'simpleType') {
        for (var element in globalProvider.chosenLang) {
          String code = globalProvider.languageToCodeMapper[element]!;
          registrationTaskProvider.addSimpleTypeDemographicField(
              widget.field.id ?? "", value, value, code);
        }
      } else {
        registrationTaskProvider.addDemographicField(
            widget.field.id ?? "", value);
      }
    }
  }

  void _saveDataToMap(GenericData? value) {
    String lang = globalProvider.mandatoryLanguages[0]!;
    for (int i = index! + 1; i < maxLen; i++) {
      globalProvider.removeFieldFromMap(
        "${widget.field.group}${globalProvider.hierarchyReverse[i]}",
        globalProvider.fieldInputValue,
      );
    }
    if (value != null) {
      if (widget.field.type == 'simpleType') {
        globalProvider.setLanguageSpecificValue(
          "${widget.field.group}${widget.field.subType}",
          value,
          lang,
          globalProvider.fieldInputValue,
        );
      } else {
        globalProvider.setInputMapValue(
          "${widget.field.group}${widget.field.subType}",
          value,
          globalProvider.fieldInputValue,
        );
      }
    }
  }

  void _getSelectedValueFromMap(String lang, List<GenericData?> list) {
    GenericData? response;
    if (widget.field.type == 'simpleType') {
      if ((globalProvider.fieldInputValue[
                  "${widget.field.group}${widget.field.subType}"]
              as Map<String, dynamic>)
          .containsKey(lang)) {
        response = globalProvider
                .fieldInputValue["${widget.field.group}${widget.field.subType}"]
            [lang] as GenericData;
      }
    } else {
      response = globalProvider
              .fieldInputValue["${widget.field.group}${widget.field.subType}"]
          as GenericData;
    }
    setState(() {
      for (var element in list) {
        if (element!.name == response!.name) {
          selected = element;
        }
      }
    });
  }

  Future<List<GenericData?>> _getLocationValues(
      String hierarchyLevelName, String langCode) async {
    return await registrationTaskProvider.getLocationValues(
        hierarchyLevelName, langCode);
  }

  Future<List<GenericData?>> _getLocationValuesBasedOnParent(
      String? parentCode, String hierarchyLevelName, String langCode) async {
    return await registrationTaskProvider.getLocationValuesBasedOnParent(
        parentCode, hierarchyLevelName, langCode);
  }

  _isFieldIdPresent() {
    return globalProvider.fieldInputValue
        .containsKey("${widget.field.group}${widget.field.subType}");
  }

  _getOptionsList() async {
    List<GenericData?> temp;
    String lang = globalProvider.mandatoryLanguages[0]!;
    if (index == 1) {
      temp =
          await _getLocationValues("$index", globalProvider.selectedLanguage);
    } else {
      var parentCode = context
          .watch<GlobalProvider>()
          .groupedHierarchyValues[widget.field.group]![index! - 1];
      temp = await _getLocationValuesBasedOnParent(
          parentCode, widget.field.subType!, globalProvider.selectedLanguage);
    }
    setState(() {
      selected = null;
    });
    setState(() {
      list = temp;
    });
    if (_isFieldIdPresent()) {
      _getSelectedValueFromMap(lang, list);
    }
  }

  @override
  Widget build(BuildContext context) {
    _getOptionsList();
    bool isPortrait =
        MediaQuery.of(context).orientation == Orientation.portrait;
    return Column(
      children: [
        Card(
          elevation: 5,
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
                DropdownButtonFormField<GenericData>(
                  icon: const Icon(null),
                  decoration: InputDecoration(
                    contentPadding:
                        const EdgeInsets.symmetric(horizontal: 16.0),
                    border: OutlineInputBorder(
                      borderRadius: BorderRadius.circular(8.0),
                      borderSide: const BorderSide(
                        color: Colors.grey,
                        width: 1.0,
                      ),
                    ),
                    hintText: "Select Option",
                    hintStyle: const TextStyle(
                      color: appBlackShade3,
                    ),
                    suffixIcon: const Icon(Icons.keyboard_arrow_down,
                        color: Colors.grey),
                  ),
                  items: list
                      .map((option) => DropdownMenuItem(
                            value: option,
                            child: Text(option!.name),
                          ))
                      .toList(),
                  autovalidateMode: AutovalidateMode.onUserInteraction,
                  value: selected,
                  validator: (value) {
                    if (!widget.field.required!) {
                      if (widget.field.requiredOn == null ||
                          widget.field.requiredOn!.isEmpty ||
                          !(globalProvider
                                  .mvelRequiredFields[widget.field.id] ??
                              true)) {
                        return null;
                      }
                    }
                    if (value == null) {
                      return AppLocalizations.of(context)!.select_value_message;
                    }
                    if (!widget.validation.hasMatch(value.name)) {
                      return AppLocalizations.of(context)!.select_value_message;
                    }
                    return null;
                  },
                  onChanged: (value) {
                    if (value != selected) {
                      saveData(value!.name);
                      _saveDataToMap(value);
                      globalProvider.setLocationHierarchy(
                          widget.field.group!, value.code, index!);
                      String lang = globalProvider.mandatoryLanguages[0]!;
                      _getSelectedValueFromMap(lang, list);
                    }
                  },
                ),
              ],
            ),
          ),
        ),
      ],
    );
  }
}
