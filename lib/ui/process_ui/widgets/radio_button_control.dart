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

class RadioButtonControl extends StatefulWidget {
  const RadioButtonControl({super.key, required this.field});

  final Field field;
  @override
  State<RadioButtonControl> createState() => _RadioFormFieldState();
}

class _RadioFormFieldState extends State<RadioButtonControl> {
  late GlobalProvider globalProvider;
  late RegistrationTaskProvider registrationTaskProvider;

  Future<List<DynamicFieldData?>> _getFieldValues(
      String fieldName, String langCode) async {
    return await registrationTaskProvider
        .getFieldValues(fieldName, langCode);
  }

  bool showError = false;

  @override
  void initState() {
    globalProvider = Provider.of<GlobalProvider>(context, listen: false);
    registrationTaskProvider =
        Provider.of<RegistrationTaskProvider>(context, listen: false);
    String lang = globalProvider.mandatoryLanguages[0]!;
    if (globalProvider
        .fieldInputValue
        .containsKey(widget.field.id)) {
      _getSelectedValueFromMap(lang);
    }
    super.initState();
  }

  void _getSelectedValueFromMap(String lang) async {
    String response = "";
    String updatedValue = "";
    response =
        globalProvider.fieldInputValue[widget.field.id];
    List<DynamicFieldData?> data = await _getFieldValues(widget.field.subType!, lang);
    for (var element in data) {
      if(element!.code == response){
        setState(() {
          updatedValue = element.name;
        });
      }
    }
    setState(() {
      selectedOption = updatedValue.toLowerCase();
    });
  }

  String? selectedOption;

  void handleOptionChange(String? value) {
    for (var element in globalProvider.chosenLang) {
      String code =
          globalProvider.languageToCodeMapper[element]!;
      registrationTaskProvider
          .addSimpleTypeDemographicField(widget.field.id ?? "", value!, code);
      globalProvider.fieldInputValue[widget.field.id!] = value;
    }
    setState(() {
      selectedOption = value;
    });
  }

  @override
  Widget build(BuildContext context) {
    bool isPortrait =
        MediaQuery.of(context).orientation == Orientation.portrait;
    return FutureBuilder(
      future: _getFieldValues(widget.field.subType!,
          globalProvider.selectedLanguage),
      builder: (BuildContext context, AsyncSnapshot<List<DynamicFieldData?>> snapshot) {
        return SizedBox(
          width: MediaQuery.of(context).size.width,
          child: Card(
            surfaceTintColor: transparentColor,
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
                    height: 16,
                  ),
                  SizedBox(
                    width: 0,
                    height: 0,
                    child: TextFormField(
                        readOnly: true,
                        validator: (value) {
                          if (selectedOption == null) {
                            setState(() {
                              showError = true;
                            });
                            return "Select option";
                          }
                          setState(() {
                            showError = false;
                          });
                          return null;
                        }),
                  ),
                  snapshot.hasData
                      ? SingleChildScrollView(
                          scrollDirection: Axis.horizontal,
                          child: Row(
                            mainAxisAlignment: MainAxisAlignment.start,
                            children: snapshot.data!
                                .map(
                                  (e) => SelectableCard(
                                    title: e!.name,
                                    value: e.name.toLowerCase(),
                                    groupValue: selectedOption,
                                    onChanged: (value) {
                                        setState(() {
                                          selectedOption = value;
                                        });
                                        handleOptionChange(e.code);
                                    }
                                    //handleOptionChange,
                                  ),
                                )
                                .toList(),
                          ),
                        )
                      : const SizedBox.shrink(),
                  const SizedBox(
                    height: 10,
                  ),
                  showError
                      ? const Text(
                          "* Select Option",
                          style: TextStyle(
                              color: Color.fromARGB(255, 159, 21, 11),
                              fontSize: 12),
                        )
                      : const SizedBox.shrink(),
                ],
              ),
            ),
          ),
        );
      },
    );
  }
}

class SelectableCard extends StatelessWidget {
  final String value;
  final String title;
  final String? groupValue;
  final Function(String) onChanged;

  const SelectableCard({
    super.key,
    required this.value,
    required this.title,
    required this.groupValue,
    required this.onChanged,
  });

  @override
  Widget build(BuildContext context) {
    return Card(
      surfaceTintColor: transparentColor,
      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(8)),
      color: groupValue == value ? solidPrimary : Colors.white,
      elevation: 0,
      child: InkWell(
        onTap: () {
          if (groupValue != value) {
            onChanged(value);
          }
        },
        child: Container(
          height: 50,
          width: 150,
          decoration: BoxDecoration(
              borderRadius: BorderRadius.circular(8),
              border: Border.all(
                color: solidPrimary,
                width: 1,
              )),
          alignment: Alignment.center,
          padding: const EdgeInsets.all(16.0),
          child: Row(
            children: [
              Text(
                title,
                style: TextStyle(
                  fontSize: 14,
                  color: value == groupValue ? Colors.white : appBlackShade1,
                  fontWeight: regular,
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }
}
