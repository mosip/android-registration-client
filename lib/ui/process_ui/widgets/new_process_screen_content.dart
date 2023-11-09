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
import 'package:registration_client/ui/process_ui/widgets/html_box_control.dart';

import 'package:registration_client/ui/process_ui/widgets/button_control.dart';
import 'package:registration_client/ui/process_ui/widgets/textbox_control.dart';
import '../../../platform_spi/registration_service.dart';
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
  @override
  void initState() {
    super.initState();
  }

  Widget widgetType(Field e) {
    RegExp regexPattern = RegExp(r'^.*$');

    if (e.validators!.isNotEmpty) {
      final validation = e.validators?.first?.validator;
      if (validation != null) {
        regexPattern = RegExp(validation);
      }
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
        return BiometricCaptureControl(field: e);
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

  evaluateMVEL(
      String fieldData, String? engine, String? expression, Field e) async {
    final RegistrationService registrationService = RegistrationService();
    registrationService.evaluateMVEL(fieldData, expression!).then((value) {
      if (!value) {
        context.read<GlobalProvider>().removeFieldFromMap(
            e.id!, context.read<GlobalProvider>().fieldInputValue);
        context.read<RegistrationTaskProvider>().removeDemographicField(e.id!);
      }
      context.read<GlobalProvider>().setMvelValues(e.id!, value);
    });
  }

  _checkMvel(Field e) {
    if (e.required == false) {
      if (e.requiredOn!.isNotEmpty) {
        evaluateMVEL(jsonEncode(e.toJson()), e.requiredOn?[0]?.engine,
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
            _checkMvel(e!);
            if (e.inputRequired == true) {
              if (context.read<GlobalProvider>().mvelValues[e.id] ?? true) {
                return widgetType(e);
              }
            }
            return Container();
          }).toList(),
        ],
      ),
    );
  }
}
