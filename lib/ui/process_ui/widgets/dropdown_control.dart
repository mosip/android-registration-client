import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';

import 'package:provider/provider.dart';
import 'package:registration_client/pigeon/dynamic_response_pigeon.dart';
import 'package:registration_client/provider/registration_task_provider.dart';
import 'package:registration_client/utils/app_config.dart';

import '../../../model/field.dart';
import '../../../provider/global_provider.dart';
import 'custom_label.dart';

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

  int? index;
  int maxLen = 0;
  List<GenericData?> list = [];

  @override
  void initState() {
    setHierarchyReverse();
    super.initState();
  }

  setHierarchyReverse() {
    maxLen = context.read<GlobalProvider>().hierarchyReverse.length;
  }

  @override
  void didChangeDependencies() {
    super.didChangeDependencies();
    setState(() {
      index = context
          .read<GlobalProvider>()
          .hierarchyReverse
          .indexOf(widget.field.subType!);
    });
    _getOptionsList();
  }

  void saveData(value) {
    for (int i = index! + 1; i < maxLen; i++) {
      context.read<RegistrationTaskProvider>().removeDemographicField(
          context.read<GlobalProvider>().hierarchyReverse[i]);
    }
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

  void _saveDataToMap(GenericData? value) {
    String lang = context.read<GlobalProvider>().mandatoryLanguages[0]!;
    for (int i = index! + 1; i < maxLen; i++) {
      context.read<GlobalProvider>().removeFieldFromMap(
            "${widget.field.group}${context.read<GlobalProvider>().hierarchyReverse[i]}",
            context.read<GlobalProvider>().fieldInputValue,
          );
    }
    if (value != null) {
      if (widget.field.type == 'simpleType') {
        context.read<GlobalProvider>().setLanguageSpecificValue(
              "${widget.field.group}${widget.field.subType}",
              value,
              lang,
              context.read<GlobalProvider>().fieldInputValue,
            );
      } else {
        context.read<GlobalProvider>().setInputMapValue(
              "${widget.field.group}${widget.field.subType}",
              value,
              context.read<GlobalProvider>().fieldInputValue,
            );
      }
    }
  }

  void _getSelectedValueFromMap(String lang, List<GenericData?> list) {
    GenericData? response;
    if (widget.field.type == 'simpleType') {
      if ((context.read<GlobalProvider>().fieldInputValue[
                  "${widget.field.group}${widget.field.subType}"]
              as Map<String, dynamic>)
          .containsKey(lang)) {
        response = context
                .read<GlobalProvider>()
                .fieldInputValue["${widget.field.group}${widget.field.subType}"]
            [lang] as GenericData;
      }
    } else {
      response = context
              .read<GlobalProvider>()
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
    return await context
        .read<RegistrationTaskProvider>()
        .getLocationValues(hierarchyLevelName, langCode);
  }

  Future<List<GenericData?>> _getLocationValuesBasedOnParent(
      String? parentCode, String hierarchyLevelName, String langCode) async {
    return await context
        .read<RegistrationTaskProvider>()
        .getLocationValuesBasedOnParent(
            parentCode, hierarchyLevelName, langCode);
  }

  _isFieldIdPresent() {
    return context
        .read<GlobalProvider>()
        .fieldInputValue
        .containsKey("${widget.field.group}${widget.field.subType}");
  }

  _getOptionsList() async {
    List<GenericData?> temp;
    String lang = context.read<GlobalProvider>().mandatoryLanguages[0]!;
    if (index == 1) {
      temp = await _getLocationValues("$index", context.read<GlobalProvider>().selectedLanguage);
    } else {
      var parentCode = context
          .watch<GlobalProvider>()
          .groupedHierarchyValues[widget.field.group]![index! - 1];
      temp = await _getLocationValuesBasedOnParent(
          parentCode, widget.field.subType!, context.read<GlobalProvider>().selectedLanguage);
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
                    if (!widget.field.required! &&
                        widget.field.requiredOn!.isEmpty) {
                      return null;
                    }
                    if (value == null) {
                      return 'Please enter a value';
                    }
                    if (!widget.validation.hasMatch(value.name)) {
                      return 'Invalid input';
                    }
                    return null;
                  },
                  onChanged: (value) {
                    if (value != selected) {
                      saveData(value!.name);
                      _saveDataToMap(value);
                      context.read<GlobalProvider>().setLocationHierarchy(
                          widget.field.group!, value.code, index!);
                      String lang =
                          context.read<GlobalProvider>().mandatoryLanguages[0]!;
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
