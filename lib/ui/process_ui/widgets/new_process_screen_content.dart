import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

import 'package:provider/provider.dart';
import 'package:registration_client/model/field.dart';
import 'package:registration_client/model/screen.dart';
import 'package:registration_client/pigeon/location_response_pigeon.dart';
import 'package:registration_client/provider/global_provider.dart';
import 'package:registration_client/provider/location_provider.dart';
import 'package:registration_client/provider/registration_task_provider.dart';
import 'package:registration_client/ui/process_ui/widgets/age_date_control.dart';
import 'package:registration_client/ui/process_ui/widgets/checkbox_control.dart';
import 'package:registration_client/ui/process_ui/widgets/dropdown_control.dart';
import 'package:registration_client/ui/process_ui/widgets/html_box_control.dart';
import 'package:registration_client/ui/process_ui/widgets/custom_label.dart';
import 'package:registration_client/ui/process_ui/widgets/preferred_lang_button_control.dart';

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
  @override
  void initState() {
    context.read<LocationProvider>().setLocationResponse("eng");
    super.initState();
  }

  readJson() async {
    final String response = await rootBundle.loadString('assets/images/registration_data.json');
    return response;
  }

  validateExpression(String? engine, String? expression) async {
    String data = await readJson();
    context.read<RegistrationTaskProvider>().checkMVEL(data, expression!);
    return context.read<RegistrationTaskProvider>().isMvelValid;
  }

  Widget widgetType(Field e) {
    RegExp regexPattern = RegExp(r'^.*$');

    if (e.required == false) {
      if (e.requiredOn!.isEmpty) {
        return const SizedBox.shrink();
      }
      String? engine = e.requiredOn?[0]?.engine;
      String? expression = e.requiredOn?[0]?.expr;
      bool mvelEvalutation = validateExpression(engine, expression);
      if (mvelEvalutation == false) {
        return const SizedBox.shrink();
      }
    }

    if (e.validators!.isNotEmpty) {
      final validation = e.validators?.first?.validator;
      if (validation != null) {
        regexPattern = RegExp(validation);
      }
    }

    if (e.controlType == "checkbox") {
      return CheckboxControl(field: e);
    }
    if (e.controlType == "html") {
      return HtmlBoxControl(field: e);
    }
    if (e.controlType == "button") {
      if (e.subType == "preferredLang") {
        return PreferredLangButtonControl(field: e);
      }

      if (e.subType == "gender" || e.subType == "residenceStatus") {
        Map<String, List<String>> values = {
          'gender': ["Female", "Male", "Others"],
          'residenceStatus': ["Permanent", "Temporary"],
        };
        return Card(
          elevation: 0,
          margin: const EdgeInsets.symmetric(vertical: 1, horizontal: 12),
          child: Padding(
            padding: const EdgeInsets.symmetric(vertical: 24, horizontal: 16),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                CustomLabel(feild: e),
                RadioButtonControl(
                  id: e.id ?? "",
                  values: values[e.subType] ?? [],
                ),
              ],
            ),
          ),
        );
      }
      return Text("${e.controlType}");
    }
    if (e.controlType == "textbox") {
      List<String> choosenLang =
          widget.context.read<GlobalProvider>().chosenLang;
      List<String> singleTextBox = [
        "Phone",
        "Email",
        "introducerName",
        "RID",
        "UIN",
        "none"
      ];
      if (singleTextBox.contains(e.subType)) {
        choosenLang = ["English"];
      }

      return Card(
        elevation: 0,
        margin: const EdgeInsets.symmetric(vertical: 1, horizontal: 12),
        child: Padding(
          padding: const EdgeInsets.symmetric(vertical: 20, horizontal: 12),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              CustomLabel(feild: e),
              const SizedBox(
                height: 10,
              ),
              Column(
                children: choosenLang.map((code) {
                  String newCode =
                      widget.context.read<GlobalProvider>().langToCode(code);
                  return TextBoxControl(
                      id: e.id ?? "",
                      label: e.label![newCode]!.toString(),
                      lang: newCode,
                      validation: regexPattern);
                }).toList(),
              ),
            ],
          ),
        ),
      );
    }
    if (e.controlType == "dropdown") {
      List<String?> options = [];
      LocationResponse? locationResponse =
          context.watch<LocationProvider>().locationResponse;
      if (locationResponse != null) {
        switch (e.subType) {
          case "Region":
            options = locationResponse.regionList;
            break;
          case "City":
            options = locationResponse.cityList;
            break;
          case "Zone":
            options = locationResponse.zoneList;
            break;
          case "Postal Code":
            options = locationResponse.postalCodeList;
            break;
          default:
        }
      }

      return Card(
        elevation: 0,
        margin: const EdgeInsets.symmetric(vertical: 1, horizontal: 12),
        child: Padding(
          padding: const EdgeInsets.symmetric(vertical: 24, horizontal: 16),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              CustomLabel(feild: e),
              const SizedBox(
                height: 10,
              ),
              DropDownControl(
                id: e.id ?? "",
                options: options,
              ),
            ],
          ),
        ),
      );
    }
    if (e.controlType == "ageDate") {
      return Card(
        elevation: 0,
        margin: const EdgeInsets.symmetric(vertical: 1, horizontal: 12),
        child: Padding(
          padding: const EdgeInsets.symmetric(vertical: 24, horizontal: 16),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              CustomLabel(feild: e),
              const SizedBox(
                height: 10,
              ),
              AgeDateControl(
                format: e.format ?? "yyyy/MM/dd",
                id: e.id ?? "",
                validation: regexPattern,
              ),
            ],
          ),
        ),
      );
    }

    return Text("${e.controlType}");
  }

  @override
  Widget build(BuildContext context) {
    return Form(
      key: context.read<GlobalProvider>().formKey,
      child: Column(
        children: [
          ...widget.screen.fields!.map((e) {
            if (e!.inputRequired == true) {
              return widgetType(e);
            }
            return Container();
          }).toList(),
        ],
      ),
    );
  }
}
