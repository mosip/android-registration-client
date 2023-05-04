/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
*/

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutter/src/widgets/framework.dart';
import 'package:registration_client/data/models/field.dart';
import 'package:registration_client/provider/global_provider.dart';
import 'package:provider/provider.dart';

List<TextEditingController> _textEditingControllers =
    List.generate(3, (index) => TextEditingController());
TextEditingController textEditingController2 = TextEditingController();

class JsonAgeDate extends StatelessWidget {
  const JsonAgeDate(
      {super.key, required this.screenIndex, required this.fieldIndex});
  final int screenIndex;
  final int fieldIndex;

  @override
  Widget build(BuildContext context) {
    Field data = context
        .watch<GlobalProvider>()
        .processParsed!
        .screens!
        .elementAt(screenIndex)!
        .fields!
        .elementAt(fieldIndex)!;
    List<FocusNode> _focusNodes = List.generate(3, (index) => FocusNode());
    var width = MediaQuery.of(context).size.width;
    return InputDecorator(
      decoration: InputDecoration(
          label: Text("${context.read<GlobalProvider>().chooseLanguage(data.label!)}"),
          border: OutlineInputBorder(borderRadius: BorderRadius.circular(20))),
      child: Column(
        children: [
          Row(
            children: [
              Flexible(
                child: TextField(
                  keyboardType: TextInputType.number,
                  inputFormatters: <TextInputFormatter>[
                    FilteringTextInputFormatter.digitsOnly
                  ],
                  controller: _textEditingControllers[0],
                  focusNode: _focusNodes[0],
                  onChanged: (value) {
                    if (value.length == 2) {
                      _focusNodes[1].requestFocus();
                    }
                  },
                  decoration: InputDecoration(
                      hintText: "DD", contentPadding: EdgeInsets.all(0)),
                ),
              ),
              Text("   /   "),
              Flexible(
                child: TextField(
                  keyboardType: TextInputType.number,
                  inputFormatters: <TextInputFormatter>[
                    FilteringTextInputFormatter.digitsOnly
                  ],
                  controller: _textEditingControllers[1],
                  focusNode: _focusNodes[1],
                  onChanged: (value) {
                    if (value.length == 2) {
                      _focusNodes[2].requestFocus();
                    }
                  },
                  decoration: InputDecoration(
                      hintText: "MM", contentPadding: EdgeInsets.all(0)),
                ),
              ),
              Text("   /   "),
              Flexible(
                child: TextField(
                  keyboardType: TextInputType.number,
                  inputFormatters: <TextInputFormatter>[
                    FilteringTextInputFormatter.digitsOnly
                  ],
                  controller: _textEditingControllers[2],
                  focusNode: _focusNodes[2],
                  onChanged: (value) {
                    if (value.length == 4) {
                      int age = ageCalculator(
                          DateTime.now(),
                          DateTime(
                              int.parse(_textEditingControllers[2].text),
                              int.parse(_textEditingControllers[1].text),
                              int.parse(_textEditingControllers[0].text)));
                      textEditingController2.text = age.toString();
                      context
                          .read<GlobalProvider>()
                          .customSettwoDArray(screenIndex, fieldIndex, age);
                      // context
                      //     .read<GlobalProvider>()
                      //     .twoDArray![screenIndex]![fieldIndex] = age;
                    } else if (value.length < 4) {
                      textEditingController2.text = "";
                    }
                  },
                  decoration: InputDecoration(
                      hintText: "YYYY", contentPadding: EdgeInsets.all(0)),
                ),
              ),
            ],
          ),
          SizedBox(
            height: 8,
          ),
          Center(
              child: Text(
            "OR",
            style: Theme.of(context)
                .textTheme
                .titleMedium
                ?.copyWith(fontWeight: FontWeight.w600),
          )),
          SizedBox(
            height: 8,
          ),
          TextField(
            controller: textEditingController2,
            keyboardType: TextInputType.number,
            inputFormatters: <TextInputFormatter>[
              FilteringTextInputFormatter.digitsOnly
            ],
            onChanged: (value) {
              if (value != "") {
                ageToDate(int.parse(value));
                context.read<GlobalProvider>().customSettwoDArray(
                    screenIndex, fieldIndex, int.parse(value));
                // context
                //     .read<GlobalProvider>()
                //     .twoDArray![screenIndex]![fieldIndex] = int.parse(value);
              } else {
                _textEditingControllers[0].text = _textEditingControllers[1]
                    .text = _textEditingControllers[2].text = "";
              }
            },
            decoration: InputDecoration(
                hintText: "Age", contentPadding: EdgeInsets.all(0)),
          )
        ],
      ),
    );
  }
}

int ageCalculator(DateTime currentDate, DateTime birthDate) {
  int age = currentDate.year - birthDate.year;
  if (currentDate.month < birthDate.month ||
      (currentDate.month == birthDate.month &&
          currentDate.day < birthDate.day)) {
    age--;
  }
  return age;
}

ageToDate(int age) {
  int year = DateTime.now().year - age;
  _textEditingControllers[0].text = DateTime.now().day.toString();
  _textEditingControllers[1].text = DateTime.now().month.toString();
  _textEditingControllers[2].text = year.toString();
}
