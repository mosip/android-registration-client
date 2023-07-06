import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:registration_client/utils/app_config.dart';

import '../../../provider/global_provider.dart';

class RadioButtonControl extends StatefulWidget {
  const RadioButtonControl({super.key, required this.values, required this.id});

  final List<String> values;
  final String id;

  @override
  _RadioFormFieldState createState() => _RadioFormFieldState();
}

class _RadioFormFieldState extends State<RadioButtonControl> {
  String? selectedOption;

  void handleOptionChange(String? value) {
    context.read<GlobalProvider>().setInputMapValue(widget.id, value);
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
