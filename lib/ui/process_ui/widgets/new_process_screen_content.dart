// <<<<<<< HEAD
import 'package:flutter/material.dart';
import 'package:flutter/src/widgets/framework.dart';
import 'package:flutter/src/widgets/placeholder.dart';
import 'package:provider/provider.dart';
import 'package:registration_client/model/field.dart';
import 'package:registration_client/model/screen.dart';
import 'package:registration_client/provider/global_provider.dart';
import 'package:registration_client/ui/process_ui/widgets/custom_checkbox.dart';
import 'package:registration_client/ui/process_ui/widgets/custom_html_box.dart';
import 'package:registration_client/ui/process_ui/widgets/custom_preferred_lang_button.dart';
// =======
import 'dart:developer';
// >>>>>>> new_registration_ui

import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

import 'package:registration_client/model/field.dart';
import 'package:registration_client/model/screen.dart';
import 'package:registration_client/ui/process_ui/demographic_details/dropdown.dart';
import 'package:registration_client/ui/process_ui/demographic_details/textbox.dart';
import 'package:registration_client/utils/app_config.dart';

import '../../../provider/global_provider.dart';
import '../demographic_details/age_date.dart';
import '../demographic_details/button.dart';

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
  Map<String, dynamic> formValues = {};

  List<Widget> buildFormFields() {
    List<Field?> data = widget.screen.fields!;
    log(widget.screen.fields!.toString());
    List<Widget> formFields = [];

    for (var field in data) {
      final label = field!.label!['eng'].toString();
      final type = field.controlType.toString();
      RegExp regexPattern = RegExp(r'^.*$');

      if (field.validators!.isNotEmpty) {
        final validation = field.validators?.first?.validator;
        if (validation != null) {
          regexPattern = RegExp(validation);
        }
      }

      List<Widget> formField = [];

      // if (requiredFeild == "false") {
      //   if (requiredOn.isEmpty || mvelEvalutation == false) {
      //     continue;
      //   }
      // }

      if (type == 'textbox') {
        for (var lang in context.read<GlobalProvider>().chosenLang) {
          formField.add(
            TextBox(
              onChanged: (value) => formValues[label] = value,
              label: label,
              validation: regexPattern,
            ),
          );
          formField.add(const SizedBox(
            height: 10,
          ));
        }
      } else if (type == 'button') {
        List<String> values = [];
        if (label == "Gender") {
          values = ["Female", "Male", "Others"];
        } else if (label == "Residence Status") {
          values = ["Permanent", "Temporary"];
        }
        formField.add(
          RadioFormField(
              values: values, onChanged: (value) => formValues[label] = value),
        );
      } else if (type == 'dropdown') {
        formField.add(
          CustomDropDown(onChanged: (value) => formValues[label] = value),
        );
      } else if (type == 'ageDate') {
        formField.add(AgeDate(
            validation: regexPattern,
            onChanged: (value) => formValues[label] = value));
      }

      if (formField.isNotEmpty) {
        formFields.add(Text(
          label,
          style: TextStyle(fontWeight: semiBold),
        ));
        formFields.add(const SizedBox(
          height: 10,
        ));
        for (var feild in formField) {
          formFields.add(feild);
        }
        formFields.add(const SizedBox(
          height: 16,
        ));
        formFields.add(const Divider(
          thickness: 1,
          color: Color(0xffE5EBFA),
        ));
        formFields.add(const SizedBox(
          height: 16,
        ));
      }
    }
    return formFields;
  }

  Widget widgetType(Field e) {
    if (e.controlType == "checkbox") {
      return CustomCheckbox(field: e);
    }
    if (e.controlType == "html") {
      return CustomHtmlBox(field: e);
    }
    if (e.controlType == "button") {
      if (e.subType == "preferredLang") {
        return CustomPreferredLangButton(field: e);
      }
      return Text("${e.controlType}");
    }
    return Text("${e.controlType}");
  }

  @override
  Widget build(BuildContext context) {
// <<<<<<< HEAD
//     return Column(
    // children: [
    //   ...screen.fields!.map((e){
    //     if(e!.inputRequired == true){
    //       return widgetType(e);
    //     }
    //     return Container();
    //   }).toList(),
//       ],
// =======
    return context.watch<GlobalProvider>().newProcessTabIndex == 0
        ? Column(
            children: [
              ...widget.screen.fields!.map(
                (e) {
                  if (e!.inputRequired == true) {
                    return widgetType(e);
                  }
                  return Container();
                },
              ).toList(),
            ],
          )
        : Card(
            margin: const EdgeInsets.all(14),
            child: Padding(
              padding: const EdgeInsets.all(16),
              child: Form(
                key: context.read<GlobalProvider>().formKey,
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: buildFormFields(),
                ),
              ),
            ),
          );
  }
}
