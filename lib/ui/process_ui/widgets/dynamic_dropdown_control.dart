import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';

import 'package:provider/provider.dart';
import 'package:registration_client/pigeon/dynamic_response_pigeon.dart';
import 'package:registration_client/provider/registration_task_provider.dart';
import 'package:registration_client/utils/app_style.dart';

import '../../../model/field.dart';
import '../../../provider/global_provider.dart';
import 'custom_label.dart';

class DynamicDropDownControl extends StatefulWidget {
  const DynamicDropDownControl(
      {super.key, required this.field, required this.validation});

  final Field field;
  final RegExp validation;

  @override
  State<DynamicDropDownControl> createState() => _CustomDynamicDropDownState();
}

class _CustomDynamicDropDownState extends State<DynamicDropDownControl> {
  DynamicFieldData? selected;

  @override
  void initState() {
    if (context
        .read<GlobalProvider>()
        .fieldInputValue
        .containsKey(widget.field.id ?? "")) {
      _getSelectedValueFromMap("eng");
    }
    super.initState();
  }

  void saveData(value) {
    if (value != null) {
      if (widget.field.type == 'simpleType') {
        context
            .read<RegistrationTaskProvider>()
            .addSimpleTypeDemographicField(widget.field.id ?? "", value, "eng");
      } else {
        context
            .read<RegistrationTaskProvider>()
            .addDemographicField(widget.field.id ?? "", value);
      }
    }
  }

  void _saveDataToMap(value) {
    if (value != null) {
      if (widget.field.type == 'simpleType') {
        context.read<GlobalProvider>().setLanguageSpecificValue(
              widget.field.id ?? "",
              value!,
              "eng",
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

  void _getSelectedValueFromMap(String lang) async {
    DynamicFieldData? response;
    String updatedValue = "";
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
    List<DynamicFieldData?> data = await _getFieldValues(widget.field.subType!, "eng");
    for (var element in data) {
      if(element!.code == response!.code){
        setState(() {
          selected = response;
        });
      }
    }
  }

  Future<List<DynamicFieldData?>> _getFieldValues(String fieldId, String langCode) async {
    return await context
        .read<RegistrationTaskProvider>()
        .getFieldValues(fieldId, langCode);
  }

  @override
  Widget build(BuildContext context) {
    bool isPortrait =
        MediaQuery.of(context).orientation == Orientation.portrait;
    return FutureBuilder(
        future: _getFieldValues(widget.field.subType!, "eng"),
        builder: (BuildContext context, AsyncSnapshot<List<DynamicFieldData?>> snapshot) {
          return Card(
            elevation: 5,
            margin: EdgeInsets.symmetric(vertical: 1.h, horizontal: isPortrait ? 16.w : 0),
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
                      ? DropdownButtonFormField<DynamicFieldData>(
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
                              color: AppStyle.appBlackShade3,
                            ),
                          ),
                          items: snapshot.data!
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
                            saveData(value!.code);
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
