import 'package:flutter/material.dart';

import 'package:provider/provider.dart';
import 'package:registration_client/provider/registration_task_provider.dart';
import 'package:registration_client/utils/app_config.dart';

import '../../../provider/global_provider.dart';

class RadioButtonControl extends StatefulWidget {
  const RadioButtonControl({
    super.key,
    required this.values,
    required this.id,
    required this.type,
  });

  final List<String> values;
  final String id;
  final String type;

  @override
  _RadioFormFieldState createState() => _RadioFormFieldState();
}

class _RadioFormFieldState extends State<RadioButtonControl> {
  @override
  void initState() {
    if (!context
        .read<GlobalProvider>()
        .feildDemographicsValues
        .containsKey(widget.id)) {
      _setInitialValueToMap();
    } else {
      _getSelectedValueFromMap("eng");
    }
    super.initState();
  }

  void _setInitialValueToMap() {
    context
        .read<RegistrationTaskProvider>()
        .addSimpleTypeDemographicField(widget.id, widget.values[0], "eng");
    context.read<GlobalProvider>().setLanguageSpecificValue(
          widget.id,
          widget.values[0],
          "eng",
          context.read<GlobalProvider>().feildDemographicsValues,
        );
    setState(() {
      selectedOption = widget.values[0].toLowerCase();
    });
  }

  void _getSelectedValueFromMap(String lang) {
    String response = "";
    response =
        context.read<GlobalProvider>().feildDemographicsValues[widget.id][lang];
    setState(() {
      selectedOption = response.toLowerCase();
    });
  }

  String? selectedOption;

  void handleOptionChange(String? value) {
    context
        .read<RegistrationTaskProvider>()
        .addSimpleTypeDemographicField(widget.id, value!, "eng");
    context.read<GlobalProvider>().setLanguageSpecificValue(
          widget.id,
          value,
          "eng",
          context.read<GlobalProvider>().feildDemographicsValues,
        );
    setState(() {
      selectedOption = value;
    });
  }

  @override
  Widget build(BuildContext context) {
    return Row(
      mainAxisAlignment: MainAxisAlignment.start,
      children: widget.values
          .map(
            (e) => Row(
              children: [
                Radio<String>(
                  activeColor: solid_primary,
                  value: e.toLowerCase(),
                  groupValue: selectedOption,
                  onChanged: handleOptionChange,
                ),
                Text(e)
              ],
            ),
          )
          .toList(),
    );
  }
}
