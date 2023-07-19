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
import 'package:registration_client/ui/process_ui/widgets/biometric_capture_control.dart';
import 'package:registration_client/ui/process_ui/widgets/checkbox_control.dart';
import 'package:registration_client/ui/process_ui/widgets/dropdown_control.dart';
import 'package:registration_client/ui/process_ui/widgets/html_box_control.dart';
import 'package:registration_client/ui/process_ui/widgets/custom_label.dart';

import 'package:registration_client/ui/process_ui/widgets/button_control.dart';
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

  Widget widgetType(Field e) {
    RegExp regexPattern = RegExp(r'^.*$');

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
    if (e.controlType == "biometrics") {
      return BiometricCaptureControl(field: e);
    }
    if (e.controlType == "button") {
      if (e.subType == "preferredLang") {
        return ButtonControl(field: e);
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
                  type: e.type ?? "",
                ),
              ],
            ),
          ),
        );
      }
      return Text("${e.controlType}");
    }
    if (e.controlType == "textbox") {
      return TextBoxControl(e: e, validation: regexPattern);
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
          case "Province":
            options = locationResponse.provinceList;
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
                validation: regexPattern,
                id: e.id ?? "",
                options: options,
                type: e.type ?? "",
              ),
            ],
          ),
        ),
      );
    }
    if (e.controlType == "ageDate") {
      return AgeDateControl(
        field: e,
        validation: regexPattern,
      );
    }

    return Text("${e.controlType}");
  }

  @override
  Widget build(BuildContext context) {
    return Form(
      key: context.watch<GlobalProvider>().formKey,
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
