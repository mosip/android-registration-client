/*
 * Copyright (c) Modular Open Source Identity Platform
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *
*/

import 'dart:convert';

import 'package:flutter/material.dart';

import 'package:provider/provider.dart';
import 'package:registration_client/model/field.dart';
import 'package:registration_client/model/screen.dart';
import 'package:registration_client/provider/global_provider.dart';
import 'package:registration_client/provider/registration_task_provider.dart';
import 'package:registration_client/ui/process_ui/widgets/age_date_control.dart';
import 'package:registration_client/ui/process_ui/widgets/biometric_capture_control.dart';

import 'package:registration_client/ui/process_ui/widgets/checkbox_control.dart';
import 'package:registration_client/ui/process_ui/widgets/date_control.dart';
import 'package:registration_client/ui/process_ui/widgets/document_upload_control.dart';
import 'package:registration_client/ui/process_ui/widgets/dropdown_control.dart';
import 'package:registration_client/ui/process_ui/widgets/dynamic_dropdown_control.dart';
import 'package:registration_client/ui/process_ui/widgets/gender_control.dart';
import 'package:registration_client/ui/process_ui/widgets/html_box_control.dart';

import 'package:registration_client/ui/process_ui/widgets/button_control.dart';
import 'package:registration_client/ui/process_ui/widgets/terms_and_conditions.dart';
import 'package:registration_client/ui/process_ui/widgets/pre_reg_data_control.dart';
import 'package:registration_client/ui/process_ui/widgets/textbox_control.dart';

import 'radio_button_control.dart';

class NewProcessScreenContent extends StatefulWidget {
  const NewProcessScreenContent(
      {super.key, required this.context, required this.screen});
  final BuildContext context;
  final Screen screen;

  @override
  State<NewProcessScreenContent> createState() =>
      _NewProcessScreenContentState();
}

class _NewProcessScreenContentState extends State<NewProcessScreenContent> {
  late GlobalProvider globalProvider;
  late RegistrationTaskProvider registrationTaskProvider;
  int refreshValue = 0;

  @override
  void initState() {
    globalProvider = Provider.of<GlobalProvider>(context, listen: false);
    registrationTaskProvider =
        Provider.of<RegistrationTaskProvider>(context, listen: false);
    super.initState();
  }

  Widget widgetType(Field e) {
    debugPrint("UI spec"+e.toString());
    RegExp regexPattern = RegExp(r'^.*$');

    if (e.validators != null && e.validators!.isNotEmpty) {
      final validation = e.validators?.first?.validator;
      if (validation != null) {
        regexPattern = RegExp(validation);
      }
    }

    if (e.id == "preferredLang") {
      return const SizedBox.shrink();
    }

    switch (e.controlType) {
      case "checkbox":
        if (e.subType == "gender") {
          return RadioButtonControl(field: e);
        }
        if (e.group!.toLowerCase() == "consent") {
          return TermsAndConditions(field: e);
        }
        return CheckboxControl(field: e);
      case "html":
        return HtmlBoxControl(field: e);
      case "biometrics":
        if (context.watch<GlobalProvider>().mvelRequiredFields[e.id] ?? true) {
          return BiometricCaptureControl(e: e);
        }
        return Container();
      case "button":
        if (e.subType == "preferredLang") {
          return ButtonControl(field: e);
        }
        if (e.subType == "gender" || e.subType == "residenceStatus") {
          return RadioButtonControl(field: e);
        }
        //feature will implement
        if (e.subType == "selectedHandles") {
          return const SizedBox.shrink();
        }
        return Text("${e.controlType}");
      case "textbox":
        return TextBoxControl(e: e, validation: regexPattern);
      case "dropdown":
        if (e.id == "gender") {
          return GenderControl(field: e, validation: regexPattern);
        }
        if (e.fieldType == "dynamic") {
          return (e.id != "countryCode") ? DynamicDropDownControl(field: e, validation: regexPattern):const SizedBox.shrink();
        }
        return DropDownControl(
          validation: regexPattern,
          field: e,
        );

      case "ageDate":
        return AgeDateControl(
          field: e,
          validation: regexPattern,
        );
      case "date":
        return DateControl(
          validation: regexPattern,
          field: e,
        );
      case "fileupload":
        return DocumentUploadControl(
          field: e,
          validation: regexPattern,
        );
      default:
        return (e.controlType!=null)? Text("${e.controlType}"): const SizedBox.shrink();
    }
  }

  evaluateMVELVisible(String fieldData, Field e) async {
    registrationTaskProvider.evaluateMVELVisible(fieldData).then((value) {
      if (!value) {
        globalProvider.removeFieldFromMap(
            e.id!, globalProvider.fieldInputValue);
        registrationTaskProvider.removeDemographicField(e.id!);
      }
      globalProvider.setMvelVisibleFields(e.id!, value);
    });
  }

  evaluateMVELRequired(String fieldData, Field e) async {
    registrationTaskProvider.evaluateMVELRequired(fieldData).then((value) {
      globalProvider.setMvelRequiredFields(e.id!, value);
    });
  }

  _checkMvelVisible(Field e) async {
    if (e.required == false) {
      if (e.requiredOn != null && e.requiredOn!.isNotEmpty) {
        await evaluateMVELVisible(jsonEncode(e.toJson()), e);
        await evaluateMVELRequired(jsonEncode(e.toJson()), e);
      }
    }
  }

  @override
  Widget build(BuildContext context) {
    return Column(
      children: [
        // if (widget.screen.preRegFetchRequired == true) ...[
        //   PreRegDataControl(
        //       screen: widget.screen,
        //       onFetched: () {
        //         setState(() {
        //           refreshValue = 1;
        //         });
        //       }),
        // ],
        const SizedBox(height: 10),
        (context.watch<GlobalProvider>().preRegControllerRefresh)
            ? const CircularProgressIndicator()
            : Form(
                key: context.watch<GlobalProvider>().formKey,
                child: Column(
                  children: [
                    ...widget.screen.fields!.map((e) {
                      _checkMvelVisible(e!);
                      if (context
                              .watch<GlobalProvider>()
                              .mvelVisibleFields[e.id] ??
                          true) {
                        return widgetType(e);
                      }
                      return Container();
                    }).toList(),
                  ],
                ),
              ),
      ],
    );
  }
}
