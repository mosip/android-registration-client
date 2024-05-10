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
import 'package:registration_client/ui/process_ui/widgets/textbox_control.dart';

import 'radio_button_control.dart';

class UpdateProcessScreenContent extends StatefulWidget {
  const UpdateProcessScreenContent(
      {super.key, required this.context, required this.screen});
  final BuildContext context;
  final Screen screen;

  @override
  State<UpdateProcessScreenContent> createState() =>
      _UpdateProcessScreenContentState();
}

class _UpdateProcessScreenContentState extends State<UpdateProcessScreenContent> {
  late GlobalProvider globalProvider;
  late RegistrationTaskProvider registrationTaskProvider;

  @override
  void initState() {
    globalProvider = Provider.of<GlobalProvider>(context, listen: false);
    registrationTaskProvider =
        Provider.of<RegistrationTaskProvider>(context, listen: false);
    super.initState();
  }

  Widget widgetType(Field e) {
    RegExp regexPattern = RegExp(r'^.*$');

    if (e.validators != null && e.validators!.isNotEmpty) {
      final validation = e.validators?.first?.validator;
      if (validation != null) {
        regexPattern = RegExp(validation);
      }
    }

    if(e.id == "preferredLang") {
      return const SizedBox.shrink();
    }

    switch (e.controlType) {
      case "checkbox":
        if (e.subType == "gender") {
          return RadioButtonControl(field: e);
        }
        return CheckboxControl(field: e);
      case "html":
        return HtmlBoxControl(field: e);
      case "biometrics":
        return BiometricCaptureControl(e: e);
      case "button":
        if (e.subType == "preferredLang") {
          return ButtonControl(field: e);
        }
        if (e.subType == "gender" || e.subType == "residenceStatus") {
          return RadioButtonControl(field: e);
        }
        return Text("${e.controlType}");
      case "textbox":
        return TextBoxControl(e: e, validation: regexPattern);
      case "dropdown":
        if (e.id == "gender") {
          return GenderControl(field: e, validation: regexPattern);
        }
        if (e.fieldType == "dynamic") {
          return DynamicDropDownControl(field: e, validation: regexPattern);
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
        return Text("${e.controlType}");
    }
  }

  evaluateMVELVisible(
      String fieldData, String? engine, String? expression, Field e) async {
    registrationTaskProvider.evaluateMVELVisible(fieldData, expression!).then((value) {
      if (!value) {
        globalProvider.removeFieldFromMap(
            e.id!, globalProvider.fieldInputValue);
        registrationTaskProvider.removeDemographicField(e.id!);
      }
      globalProvider.setMvelVisibleFields(e.id!, value);
    });
  }

  evaluateMVELRequired(
      String fieldData, String? engine, String? expression, Field e) async {
    registrationTaskProvider.evaluateMVELRequired(fieldData, expression!).then((value) {
      globalProvider.setMvelRequiredFields(e.id!, value);
    });
  }

  checkMvelVisible(Field e) async {
    if (e.required == false) {
      if (e.requiredOn != null && e.requiredOn!.isNotEmpty) {
        await evaluateMVELVisible(jsonEncode(e.toJson()), e.requiredOn?[0]?.engine,
            e.requiredOn?[0]?.expr, e);
        await evaluateMVELRequired(jsonEncode(e.toJson()), e.requiredOn?[0]?.engine,
            e.requiredOn?[0]?.expr, e);
      }
    }
  }

  @override
  Widget build(BuildContext context) {
    return Form(
      key: context.watch<GlobalProvider>().formKey,
      child: Column(
        children: [
          ...widget.screen.fields!.map((e) {
            // _checkMvel(e!);
            if(e!.group == "consent" || e.group == "consentText") {
              return widgetType(e);
            } else if (globalProvider.selectedUpdateFields[e.group] != null) {
              return widgetType(e);
            }
            return Container();
          }).toList(),
        ],
      ),
    );
  }
}
