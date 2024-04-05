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

class DynamicDropDownControl extends StatefulWidget {
  const DynamicDropDownControl(
      {super.key, required this.field, required this.validation});

  final Field field;
  final RegExp validation;

  @override
  State<DynamicDropDownControl> createState() => _CustomDynamicDropDownState();
}

class _CustomDynamicDropDownState extends State<DynamicDropDownControl> {
  String? selected;
  late GlobalProvider globalProvider;
  late RegistrationTaskProvider registrationTaskProvider;

  @override
  void initState() {
    globalProvider = Provider.of<GlobalProvider>(context, listen: false);
    registrationTaskProvider =
        Provider.of<RegistrationTaskProvider>(context, listen: false);
    String lang = globalProvider.mandatoryLanguages[0]!;
    if (context
        .read<GlobalProvider>()
        .fieldInputValue
        .containsKey(widget.field.id ?? "")) {
      _getSelectedValueFromMap(lang);
    }
    super.initState();
  }

  void saveData(value) {
    if (value != null) {
      if (widget.field.type == 'simpleType') {
        for (var element in globalProvider.chosenLang) {
          String code = globalProvider.languageToCodeMapper[element]!;
          registrationTaskProvider
              .addSimpleTypeDemographicField(
                  widget.field.id ?? "", value, code);
        }
      } else {
        registrationTaskProvider
            .addDemographicField(widget.field.id ?? "", value);
      }
    }
  }

  void _saveDataToMap(value) {
    String lang = globalProvider.mandatoryLanguages[0]!;
    if (value != null) {
      if (widget.field.type == 'simpleType') {
        globalProvider.setLanguageSpecificValue(
          widget.field.id ?? "",
          value!,
          lang,
          globalProvider.fieldInputValue,
        );
      } else {
        globalProvider.setInputMapValue(
          widget.field.id ?? "",
          value!,
          globalProvider.fieldInputValue,
        );
      }
    }
  }

  void _getSelectedValueFromMap(String lang) async {
    String response = "";
    if (widget.field.type == 'simpleType') {
      if ((globalProvider.fieldInputValue[widget.field.id ?? ""]
              as Map<String, dynamic>)
          .containsKey(lang)) {
        response = globalProvider
            .fieldInputValue[widget.field.id ?? ""][lang];
      }
    } else {
      response = globalProvider.fieldInputValue[widget.field.id ?? ""];
    }
    List<DynamicFieldData?> data =
        await _getFieldValues(widget.field.subType!, lang);
    for (var element in data) {
      if (element!.name == response) {
        setState(() {
          selected = response;
        });
      }
    }
  }

  Future<List<DynamicFieldData?>> _getFieldValues(String fieldId, String langCode) async {
    return await registrationTaskProvider
        .getFieldValues(fieldId, langCode);
  }

  @override
  Widget build(BuildContext context) {
    bool isPortrait =
        MediaQuery.of(context).orientation == Orientation.portrait;
    String mandatoryLanguageCode =
        globalProvider.mandatoryLanguages[0] ?? "eng";
    return FutureBuilder(
        future: _getFieldValues(
            widget.field.subType!, globalProvider.selectedLanguage),
        builder: (BuildContext context, AsyncSnapshot<List<DynamicFieldData?>> snapshot) {
          return Card(
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
                  snapshot.hasData
                      ? DropdownButtonFormField(
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
                            suffixIcon: const Icon(Icons.keyboard_arrow_down,color: Colors.grey),
                          ),
                          items: snapshot.data!
                              .map((option) => DropdownMenuItem(
                                    value: option!.name,
                                    child: Text(option.name),
                                  ))
                              .toList(),
                          autovalidateMode: AutovalidateMode.onUserInteraction,
                          value: selected,
                          validator: (value) {
                            if (!widget.field.required! &&
                                (widget.field.requiredOn == null ||
                                    widget.field.requiredOn!.isEmpty)) {
                              return null;
                            }
                            if (value == null || value.isEmpty) {
                              return AppLocalizations.of(context)!
                                  .demographicsScreenEmptyMessage(
                                      mandatoryLanguageCode);
                            }
                            if (!widget.validation.hasMatch(value)) {
                              return AppLocalizations.of(context)!
                                  .demographicsScreenInvalidMessage(
                                      mandatoryLanguageCode);
                            }
                            return null;
                          },
                          onChanged: (value) {
                            for (var e in snapshot.data!) {
                              if (e!.name == value) {
                                saveData(e.code);
                              }
                            }
                            _saveDataToMap(value);
                            setState(() {
                              selected = value;
                            });
                          },
                        )
                      : const SizedBox.shrink(),
                ],
              ),
            ),
          );
        });
  }
}
