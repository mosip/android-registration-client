/*
 * Copyright (c) Modular Open Source Identity Platform
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *
*/

import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';

import 'package:provider/provider.dart';
import 'package:registration_client/model/field.dart';
import 'package:registration_client/model/process.dart';
import 'package:registration_client/provider/global_provider.dart';
import 'package:registration_client/provider/registration_task_provider.dart';
import 'package:registration_client/ui/widgets/field_button_widget.dart';
import 'package:registration_client/utils/app_config.dart';
import 'package:registration_client/utils/life_cycle_event_handler.dart';
import 'package:responsive_grid_list/responsive_grid_list.dart';

class UpdateFieldSelector extends StatefulWidget {
  const UpdateFieldSelector({
    super.key,
    required this.process,
  });
  final Process process;

  @override
  State<UpdateFieldSelector> createState() => _UpdateFieldSelectorState();
}

class _UpdateFieldSelectorState extends State<UpdateFieldSelector>
    with WidgetsBindingObserver {
  late GlobalProvider globalProvider;
  late RegistrationTaskProvider registrationTaskProvider;
  Map<String, List<Field>> fieldsMap = {};
  final RegExp validation = RegExp(r'^([0-9]{10})$');
  TextEditingController controller = TextEditingController();

  @override
  void initState() {
    globalProvider = Provider.of<GlobalProvider>(context, listen: false);
    registrationTaskProvider =
        Provider.of<RegistrationTaskProvider>(context, listen: false);
        controller = TextEditingController(text: globalProvider.updateUINNumber);
    for (var screen in widget.process.screens!) {
      for (var field in screen!.fields!) {
        if(fieldsMap[field!.group!] == null) {
          fieldsMap[field.group!] = [];
        }
        fieldsMap[field.group]!.add(field);
      }
    }
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

  _getFieldTitle(Field field) {
    String title = "";
    for (var element in globalProvider.chosenLang) {
      String code = globalProvider.languageToCodeMapper[element]!;
      title +=
          " / ${field.groupLabel![code]}";
    }
    return title.substring(3);
  }

  _addFieldsToRegistrationDTO(String key) async {
    List<String> fieldIds = [];
    for (var element in fieldsMap[key]!) {
      fieldIds.add(element.id!);
    }
    registrationTaskProvider.addUpdatableFields(fieldIds);
  }

  _addFieldGroupToRegistrationDTO(String key) async {
    registrationTaskProvider.addUpdatableFieldGroup(key);
  }

  _removeFieldsFromRegistrationDTO(String key) async {
    List<String> fieldIds = [];
    for (var element in fieldsMap[key]!) {
      fieldIds.add(element.id!);
    }
    registrationTaskProvider.removeUpdatableFields(fieldIds);
  }

  _removeFieldGroupFromRegistrationDTO(String key) async {
    registrationTaskProvider.removeUpdatableFieldGroup(key);
  }

  @override
  void dispose() {
    WidgetsBinding.instance.removeObserver(this);
    super.dispose();
  }

  void closeKeyboard() {
    FocusScope.of(context).unfocus();
  }

  @override
  Widget build(BuildContext context) {
    bool isPortrait =
        MediaQuery.of(context).orientation == Orientation.portrait;

    return Column(
      children: [
        SizedBox(
          height: 20.h,
        ),
        Card(
          elevation: 5,
          color: pureWhite,
          margin: EdgeInsets.symmetric(
              vertical: 1.h, horizontal: isPortrait ? 16.w : 0),
          child: Padding(
            padding: EdgeInsets.symmetric(vertical: 24.h, horizontal: 16.w),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                SingleChildScrollView(
                  scrollDirection: Axis.horizontal,
                  child: Row(
                    children: [
                      Text(
                        "UIN Number",
                        style: TextStyle(
                            fontSize: isPortrait && !isMobileSize ? 18 : 14,
                            fontWeight: semiBold),
                      ),
                      const SizedBox(
                        width: 5,
                      ),
                      Text(
                        "*",
                        style: TextStyle(
                            color: Colors.red,
                            fontSize: isPortrait && !isMobileSize ? 18 : 14),
                      )
                    ],
                  ),
                ),
                const SizedBox(
                  height: 10,
                ),
                Container(
                  margin: const EdgeInsets.only(bottom: 8),
                  child: Form(
                    key: context.watch<GlobalProvider>().updateFieldKey,
                    child: TextFormField(
                      autovalidateMode: AutovalidateMode.onUserInteraction,
                      textCapitalization: TextCapitalization.words,
                      // initialValue: globalProvider.updateUINNumber,
                      controller: controller,
                      onChanged: (value) {
                        globalProvider.updateUINNumber = value;
                      },
                      validator: (value) {
                        if (value == null) {
                          return "Please enter a valid UIN";
                        } else if(!validation.hasMatch(value)) {
                          return "Please enter a valid UIN";
                        }
                        return null;
                      },
                      decoration: InputDecoration(
                        border: OutlineInputBorder(
                          borderRadius: BorderRadius.circular(8.0),
                          borderSide:
                              const BorderSide(color: appGreyShade, width: 1),
                        ),
                        contentPadding: const EdgeInsets.symmetric(
                            vertical: 14, horizontal: 16),
                        hintText: "Enter UIN Number",
                        hintStyle:
                            const TextStyle(color: appBlackShade3, fontSize: 14),
                      ),
                    ),
                  ),
                ),
              ],
            ),
          ),
        ),
        SizedBox(
          height: 20.h,
        ),
        Card(
          elevation: 5,
          color: pureWhite,
          margin: EdgeInsets.symmetric(
              vertical: 1.h, horizontal: isPortrait ? 16.w : 0),
          child: Padding(
            padding: EdgeInsets.symmetric(vertical: 24.h, horizontal: 16.w),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                SingleChildScrollView(
                  scrollDirection: Axis.horizontal,
                  child: Row(
                    children: [
                      Text(
                        "Select all the attributes that need to be updated",
                        style: TextStyle(
                            fontSize: isPortrait && !isMobileSize ? 18 : 14,
                            fontWeight: semiBold),
                      ),
                      const SizedBox(
                        width: 5,
                      ),
                      Text(
                        "*",
                        style: TextStyle(
                            color: Colors.red,
                            fontSize: isPortrait && !isMobileSize ? 18 : 14),
                      )
                    ],
                  ),
                ),
                const SizedBox(
                  height: 10,
                ),
                ResponsiveGridList(
                  listViewBuilderOptions:
                      ListViewBuilderOptions(primary: false),
                  shrinkWrap: true,
                  minItemWidth: 400,
                  horizontalGridSpacing: 16,
                  verticalGridSpacing: 12,
                  children: fieldsMap.keys.map((key) {
                    return FieldButtonWidget(
                      isSelected: context.watch<GlobalProvider>().selectedUpdateFields[key] != null,
                      onTap: () {
                        if(globalProvider.selectedUpdateFields[key] == null) {
                          globalProvider.addSelectedUpdateFieldKey(key);
                          _addFieldsToRegistrationDTO(key);
                          _addFieldGroupToRegistrationDTO(key);
                        } else {
                          globalProvider.removeSelectedUpdateFieldKey(key);
                          _removeFieldsFromRegistrationDTO(key);
                          _removeFieldGroupFromRegistrationDTO(key);
                        }
                      },
                      isMobile: isPortrait,
                      fieldTitle: _getFieldTitle(fieldsMap[key]![0]),
                    );
                  }).toList(),
                )
              ],
            ),
          ),
        ),
      ],
    );
  }
}
