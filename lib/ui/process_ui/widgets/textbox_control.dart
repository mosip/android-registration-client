import 'dart:convert';
import 'dart:developer';

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';

import 'package:provider/provider.dart';
import 'package:registration_client/provider/registration_task_provider.dart';
import 'package:registration_client/utils/app_style.dart';
import 'package:responsive_grid_list/responsive_grid_list.dart';

import '../../../model/field.dart';
import '../../../platform_spi/registration.dart';
import '../../../provider/global_provider.dart';
import 'custom_label.dart';

class TextBoxControl extends StatefulWidget {
  const TextBoxControl({super.key, required this.e, required this.validation});
  final Field e;
  final RegExp validation;

  @override
  State<TextBoxControl> createState() => _TextBoxControlState();
}

class _TextBoxControlState extends State<TextBoxControl> {
  bool isMvelValid = true;

  @override
  void initState() {
    super.initState();
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
            shrinkWrap: true,
            minItemWidth: 400,
            horizontalGridSpacing: 16,
            verticalGridSpacing: 12,
            children: choosenLang.map((code) {
              String lang = context.read<GlobalProvider>().langToCode(code);

              return Container(
                margin: const EdgeInsets.only(bottom: 8),
                child: TextFormField(
                  autovalidateMode: AutovalidateMode.onUserInteraction,
                  initialValue: _getDataFromMap(lang),
                  onChanged: (value) {
                    _saveDataToMap(value, lang);
                    saveData(value, lang);
                  },
                  validator: (value) {
                    if (!widget.e.required! && widget.e.requiredOn!.isEmpty) {
                      return null;
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
                      borderSide:
                          const BorderSide(color: AppStyle.appGreyShade, width: 1),
                    ),
                    contentPadding: const EdgeInsets.symmetric(
                        vertical: 14, horizontal: 16),
                    hintText: widget.e.label![lang],
                    hintStyle:
                        TextStyle(color: AppStyle.appBlackShade3, fontSize: 14.sp),
                    prefixIcon: (lang == 'ara')
                        ? const Icon(
                            Icons.keyboard_outlined,
                            size: 36,
                          )
                        : null,
                    suffixIcon: (lang == 'ara')
                        ? null
                        : const Icon(
                            Icons.keyboard_outlined,
                            size: 36,
                          ),
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
