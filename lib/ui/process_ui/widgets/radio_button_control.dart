/*
 * Copyright (c) Modular Open Source Identity Platform
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *
*/

import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:flutter_gen/gen_l10n/app_localizations.dart';

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

  // Stable future — avoids FutureBuilder every rebuild
  late Future<List<DynamicFieldData?>> _fieldValuesFuture;

  String? _pendingFieldVal;

  Future<List<DynamicFieldData?>> _getFieldValues(
      String fieldName, String langCode) async {
    return await registrationTaskProvider
        .getFieldValues(fieldName, langCode, globalProvider.chosenLang);
  }

  bool showError = false;

  bool get _isFieldRequired {
    if (widget.field.required == true) {
      return true;
    }

    final fieldId = widget.field.id;
    return widget.field.requiredOn != null &&
        widget.field.requiredOn!.isNotEmpty &&
        fieldId != null &&
        globalProvider.mvelRequiredFields[fieldId] == true;
  }

  void _updateErrorState(bool shouldShowError) {
    if (!mounted || showError == shouldShowError) {
      return;
    }

    WidgetsBinding.instance.addPostFrameCallback((_) {
      if (mounted && showError != shouldShowError) {
        setState(() {
          showError = shouldShowError;
        });
      }
    });
  }

  @override
  void initState() {
    globalProvider = Provider.of<GlobalProvider>(context, listen: false);
    registrationTaskProvider =
        Provider.of<RegistrationTaskProvider>(context, listen: false);
    _fieldValuesFuture =
        _getFieldValues(widget.field.subType!, globalProvider.selectedLanguage);
    if (globalProvider.fieldInputValue.containsKey(widget.field.id)) {
      _getSelectedValueFromMap(globalProvider.selectedLanguage);
    }
    globalProvider.addListener(_onPreRegAutoFill);
    super.initState();
  }

  @override
  void dispose() {
    globalProvider.removeListener(_onPreRegAutoFill);
    super.dispose();
  }

  void _onPreRegAutoFill() {
    if (!mounted) {
      globalProvider.removeListener(_onPreRegAutoFill);
      return;
    }

    final dynamic fieldVal =
        globalProvider.fieldInputValue[widget.field.id ?? ""];

    if (fieldVal == null) {
      if (selectedOption != null) {
        setState(() {
          selectedOption = null;
        });
      }
      _pendingFieldVal = null;
      return;
    }

    if (fieldVal is! String || fieldVal.isEmpty) return;


    if (selectedOption != null || fieldVal == _pendingFieldVal) return;

    _pendingFieldVal = fieldVal;
    _applyPreRegValue(fieldVal);
  }


  void _applyPreRegValue(String fieldVal) async {
    final String lang = globalProvider.selectedLanguage;
    final List<DynamicFieldData?> data =
        await _getFieldValues(widget.field.subType!, lang);

    // Guard against stale async result (e.g. second fetch arrived first)
    if (!mounted || fieldVal != _pendingFieldVal) return;

    for (final element in data) {
      if (element != null &&
          (element.code == fieldVal ||
              element.name.toLowerCase() == fieldVal.toLowerCase())) {
        handleOptionChange(element.code, element.name);
        break;
      }
    }
  }

  void _getSelectedValueFromMap(String lang) async {
    final dynamic raw = globalProvider.fieldInputValue[widget.field.id];
    final String response = raw?.toString() ?? "";
    if (response.isEmpty) return;

    final List<DynamicFieldData?> data =
        await _getFieldValues(widget.field.subType!, lang);
    String updatedValue = "";
    for (final element in data) {
      if (element != null &&
          (element.code == response ||
              element.name.toLowerCase() == response.toLowerCase())) {
        updatedValue = element.name;
        break;
      }
    }
    if (mounted) {
      setState(() {
        selectedOption =
            updatedValue.isEmpty ? null : updatedValue.toLowerCase();
      });
    }
  }

  String? selectedOption;

  void handleOptionChange(String? value, String? name) {
    for (var element in globalProvider.chosenLang) {
      String code =
          globalProvider.languageToCodeMapper[element]!;
      registrationTaskProvider
          .addSimpleTypeDemographicField(widget.field.id ?? "", value!, code);
      globalProvider.fieldInputValue[widget.field.id!] = value;
    }
    setState(() {
      selectedOption = name?.toLowerCase() ?? value;
      showError = false;
    });
  }

  @override
  Widget build(BuildContext context) {
    bool isPortrait =
        MediaQuery.of(context).orientation == Orientation.portrait;
    return FutureBuilder(
      future: _fieldValuesFuture,
      builder: (BuildContext context,
          AsyncSnapshot<List<DynamicFieldData?>> snapshot) {
        return SizedBox(
          width: MediaQuery.of(context).size.width,
          child: Card(
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
                          if (!_isFieldRequired) {
                            _updateErrorState(false);
                            return null;
                          }

                          if (selectedOption == null || selectedOption!.isEmpty) {
                            _updateErrorState(true);
                            return AppLocalizations.of(context)!.select_option;
                          }
                          _updateErrorState(false);
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
                                        handleOptionChange(e.code, e.name);
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
                      ? Text(
                          "* ${AppLocalizations.of(context)!.select_option}",
                          style: const TextStyle(
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
