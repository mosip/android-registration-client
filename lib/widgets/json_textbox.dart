/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
*/

import 'package:flutter/material.dart';
import 'package:flutter/src/widgets/framework.dart';
import 'package:registration_client/data/models/field.dart';
import 'package:registration_client/provider/global_provider.dart';

import 'package:provider/provider.dart';

class JsonTextBox extends StatefulWidget {
  const JsonTextBox(
      {super.key, required this.screenIndex, required this.fieldIndex});

  final int screenIndex;
  final int fieldIndex;

  @override
  State<JsonTextBox> createState() => _JsonTextBoxState();
}

class _JsonTextBoxState extends State<JsonTextBox> {
  @override
  Widget build(BuildContext context) {
    Field data = context
        .watch<GlobalProvider>()
        .processParsed!
        .screens!
        .elementAt(widget.screenIndex)!
        .fields!
        .elementAt(widget.fieldIndex)!;

    return TextFormField(
      onChanged: (value) {
        context
            .read<GlobalProvider>()
            .twoDArray![widget.screenIndex]![widget.fieldIndex] = value;
      },
      enabled: (data.requiredOn!.isEmpty)
          ? data.required
          : fetchEnableOnCondition(context, data.requiredOn!.first!.expr!),
      validator: (value) {
        if (data.inputRequired == true) {
          if (value!.isEmpty) {
            return '${context.read<GlobalProvider>().chooseLanguage(data.label!)}';
          }
          if (!RegExp(data.validators!.first!.validator!).hasMatch(value)) {
            return '${context.read<GlobalProvider>().chooseLanguage(data.label!)}';
          }
          return null;
        }
      },
      decoration: InputDecoration(
          hintText: "${context.read<GlobalProvider>().chooseLanguage(data.label!)}",
          label: Text("${context.read<GlobalProvider>().chooseLanguage(data.label!)}"),
          border: OutlineInputBorder(borderRadius: BorderRadius.circular(20))),
    );
  }
}

fetchEnableOnCondition(BuildContext context, String condition) {
  if ((context.watch<GlobalProvider>().twoDArray![1]![1])
          .runtimeType
          .toString() ==
      "int") {
    if (context.watch<GlobalProvider>().twoDArray![1]![1] >= 18) {
      return true;
    }
  }

  return false;
}
