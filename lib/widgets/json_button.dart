/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
*/

import 'package:flutter/material.dart';
import 'package:flutter/src/widgets/framework.dart';
import 'package:flutter/src/widgets/placeholder.dart';
import 'package:registration_client/data/models/field.dart';
import 'package:registration_client/demographic_details_view.dart';
import 'package:registration_client/provider/global_provider.dart';

import 'package:provider/provider.dart';

class JsonButton extends StatelessWidget {
  const JsonButton(
      {super.key, required this.fieldIndex, required this.screenIndex});

  final int fieldIndex;
  final int screenIndex;

  @override
  Widget build(BuildContext context) {
    Field data = context
        .watch<GlobalProvider>()
        .processParsed!
        .screens!
        .elementAt(screenIndex)!
        .fields!
        .elementAt(fieldIndex)!;
    return ElevatedButton(
      onPressed: () {
        context
        .watch<GlobalProvider>()
        .processParsed;
      },
      style: ButtonStyle(
        backgroundColor: MaterialStateProperty.all<Color>(primaryColor),
        // minimumSize: MaterialStateProperty.all<Size>(Size(width, 35)),
      ),
      child: Text(
        "${context.read<GlobalProvider>().chooseLanguage(data.label!)}",
        style: TextStyle(color: Colors.white),
      ),
    );
  }
}
