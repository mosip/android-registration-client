import 'package:flutter/material.dart';

import 'package:provider/provider.dart';
import 'package:registration_client/provider/registration_task_provider.dart';
import 'package:registration_client/utils/app_config.dart';
import 'package:registration_client/utils/app_style.dart';

import '../../../model/field.dart';
import '../../../provider/global_provider.dart';
import 'custom_label.dart';

class RadioButtonControl extends StatefulWidget {
  const RadioButtonControl({super.key, required this.field});

  final Field field;
  @override
  State<RadioButtonControl> createState() => _RadioFormFieldState();
}

class _RadioFormFieldState extends State<RadioButtonControl> {
  Future<List<String?>> _getFieldValues(
      String fieldName, String langCode) async {
    return await context
        .read<RegistrationTaskProvider>()
        .getFieldValues(fieldName, langCode);
  }

  bool showError = false;

  @override
  void initState() {
    if (context
        .read<GlobalProvider>()
        .fieldInputValue
        .containsKey(widget.field.id)) {
      _getSelectedValueFromMap("eng");
    }
    super.initState();
  }

  void _getSelectedValueFromMap(String lang) {
    String response = "";
    response =
        context.read<GlobalProvider>().fieldInputValue[widget.field.id][lang];
    setState(() {
      selectedOption = response.toLowerCase();
    });
  }

  String? selectedOption;

  void handleOptionChange(String? value) {
    context
        .read<RegistrationTaskProvider>()
        .addSimpleTypeDemographicField(widget.field.id ?? "", value!, "eng");
    context.read<GlobalProvider>().setLanguageSpecificValue(
          widget.field.id ?? "",
          value,
          "eng",
          context.read<GlobalProvider>().fieldInputValue,
        );
    setState(() {
      selectedOption = value;
    });
  }

  @override
  Widget build(BuildContext context) {
    return FutureBuilder(
      future: _getFieldValues(widget.field.subType!, "eng"),
      builder: (BuildContext context, AsyncSnapshot<List<String?>> snapshot) {
        return SizedBox(
          width: MediaQuery.of(context).size.width,
          child: Card(
            elevation: 0,
            margin: const EdgeInsets.symmetric(vertical: 1, horizontal: 12),
            child: Padding(
              padding: const EdgeInsets.symmetric(vertical: 24, horizontal: 16),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  CustomLabel(field: widget.field),
                  const SizedBox(
                    height: 16,
                  ),
                  SizedBox(
                    width: 0,
                    height: 0,
                    child: TextFormField(
                        readOnly: true,
                        validator: (value) {
                          if (selectedOption == null) {
                            setState(() {
                              showError = true;
                            });
                            return "Select option";
                          }
                          setState(() {
                            showError = false;
                          });
                          return null;
                        }),
                  ),
                  snapshot.hasData
                      ? SingleChildScrollView(
                          scrollDirection: Axis.horizontal,
                          child: Row(
                            mainAxisAlignment: MainAxisAlignment.start,
                            children: snapshot.data!
                                .map(
                                  (e) => SelectableCard(
                                    title: e!,
                                    value: e.toLowerCase(),
                                    groupValue: selectedOption,
                                    onChanged: handleOptionChange,
                                  ),
                                )
                                .toList(),
                          ),
                        )
                      : const SizedBox.shrink(),
                  const SizedBox(
                    height: 10,
                  ),
                  showError
                      ? const Text(
                          "* Select Option",
                          style: TextStyle(
                              color: Color.fromARGB(255, 159, 21, 11),
                              fontSize: 12),
                        )
                      : const SizedBox.shrink(),
                ],
              ),
            ),
          ),
        );
      },
    );
  }
}

class SelectableCard extends StatelessWidget {
  final String value;
  final String title;
  final String? groupValue;
  final Function(String) onChanged;

  const SelectableCard({
    super.key,
    required this.value,
    required this.title,
    required this.groupValue,
    required this.onChanged,
  });

  @override
  Widget build(BuildContext context) {
    return Card(
      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(8)),
      color: groupValue == value ? solidPrimary : Colors.white,
      elevation: 0,
      child: InkWell(
        onTap: () {
          if (groupValue != value) {
            onChanged(value);
          }
        },
        child: Container(
          height: 50,
          width: 150,
          decoration: BoxDecoration(
              borderRadius: BorderRadius.circular(8),
              border: Border.all(
                color: solidPrimary,
                width: 1,
              )),
          alignment: Alignment.center,
          padding: const EdgeInsets.all(16.0),
          child: Row(
            children: [
              Text(
                title,
                style: TextStyle(
                  fontSize: 14,
                  color: value == groupValue
                      ? Colors.white
                      : AppStyle.appBlackShade1,
                  fontWeight: regular,
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }
}
