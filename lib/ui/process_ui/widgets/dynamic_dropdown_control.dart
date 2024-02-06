/*
 * Copyright (c) Modular Open Source Identity Platform
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *
*/

import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';

import 'package:provider/provider.dart';
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

  @override
  void initState() {
    String lang = context.read<GlobalProvider>().mandatoryLanguages[0]!;
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
        context.read<GlobalProvider>().chosenLang.forEach((element) {
          String code =
              context.read<GlobalProvider>().languageToCodeMapper[element]!;
          context
              .read<RegistrationTaskProvider>()
              .addSimpleTypeDemographicField(
                  widget.field.id ?? "", value, code);
        });
      } else {
        context
            .read<RegistrationTaskProvider>()
            .addDemographicField(widget.field.id ?? "", value);
      }
    }
  }

  void _saveDataToMap(value) {
    String lang = context.read<GlobalProvider>().mandatoryLanguages[0]!;
    if (value != null) {
      if (widget.field.type == 'simpleType') {
        context.read<GlobalProvider>().setLanguageSpecificValue(
              widget.field.id ?? "",
              value!,
              lang,
              context.read<GlobalProvider>().fieldInputValue,
            );
      } else {
        context.read<GlobalProvider>().setInputMapValue(
              widget.field.id ?? "",
              value!,
              context.read<GlobalProvider>().fieldInputValue,
            );
      }
    }
  }

  void _getSelectedValueFromMap(String lang) {
    String response = "";
    if (widget.field.type == 'simpleType') {
      if ((context.read<GlobalProvider>().fieldInputValue[widget.field.id ?? ""]
              as Map<String, dynamic>)
          .containsKey(lang)) {
        response = context
            .read<GlobalProvider>()
            .fieldInputValue[widget.field.id ?? ""][lang];
      }
    } else {
      response =
          context.read<GlobalProvider>().fieldInputValue[widget.field.id ?? ""];
    }
    setState(() {
      selected = response;
    });
  }

  Future<List<String?>> _getFieldValues(String fieldId, String langCode) async {
    return await context
        .read<RegistrationTaskProvider>()
        .getFieldValues(fieldId, langCode);
  }

  @override
  Widget build(BuildContext context) {
    bool isPortrait =
        MediaQuery.of(context).orientation == Orientation.portrait;
    String mandatoryLanguageCode =
        context.read<GlobalProvider>().mandatoryLanguages[0] ?? "eng";
    return FutureBuilder(
        future: _getFieldValues(widget.field.subType!,
            context.read<GlobalProvider>().selectedLanguage),
        builder: (BuildContext context, AsyncSnapshot<List<String?>> snapshot) {
          return Card(
            surfaceTintColor: transparentColor,
            color: appWhite,
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
                          ),
                          items: snapshot.data!
                              .map((option) => DropdownMenuItem(
                                    value: option,
                                    child: Text(option!),
                                  ))
                              .toList(),
                          autovalidateMode: AutovalidateMode.onUserInteraction,
                          value: selected,
                          validator: (value) {
                            if (!widget.field.required! &&
                                widget.field.requiredOn!.isEmpty) {
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
                            saveData(value);
                            _saveDataToMap(value);
                            setState(() {
                              selected = value!;
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
