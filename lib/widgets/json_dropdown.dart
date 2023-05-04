/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
*/

import 'package:flutter/material.dart';
import 'package:flutter/src/widgets/framework.dart';
import 'package:registration_client/data/models/field.dart';
import 'package:registration_client/demographic_details_view.dart';
import 'package:registration_client/provider/global_provider.dart';
import 'package:provider/provider.dart';

List<String> list = <String>["Value 1", "Value 2", "Value 3", "Value 4"];
String dropdownValue = list.first;

class JsonDropdown extends StatefulWidget {
  const JsonDropdown({super.key, required this.data});
  final Field data;

  @override
  State<JsonDropdown> createState() => _JsonDropdownState();
}

class _JsonDropdownState extends State<JsonDropdown> {
  @override
  Widget build(BuildContext context) {
    
    return InputDecorator(
      decoration: InputDecoration(
        labelText: "${context.read<GlobalProvider>().chooseLanguage(widget.data.label!)}",
        border: OutlineInputBorder(
          borderSide: const BorderSide(width: 2, color: Colors.grey),
          borderRadius: BorderRadius.circular(5),
        ),
        focusedBorder: OutlineInputBorder(
          borderSide: BorderSide(width: 2, color: secondaryColor),
          borderRadius: BorderRadius.circular(5),
        ),
      ),
      child: DropdownButton(
        value: dropdownValue,
        icon: const Icon(Icons.arrow_drop_down),
        hint: const Text("Payee"),
        isDense: true,
        isExpanded: true,
        underline: null,
        style: Theme.of(context)
            .textTheme
            .titleMedium
            ?.copyWith(color: Colors.black),
        onChanged: (String? value) {
          setState(() {
            dropdownValue = value!;
          });
        },
        items: list.map<DropdownMenuItem<String>>((String value) {
          return DropdownMenuItem<String>(
            value: value,
            child: Text(value),
          );
        }).toList(),
      ),
    );
  }
}
