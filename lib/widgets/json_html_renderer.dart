/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
*/

import 'package:flutter/material.dart';
import 'package:flutter/src/widgets/framework.dart';
import 'package:flutter/src/widgets/placeholder.dart';
import 'package:flutter_html/flutter_html.dart';
import 'package:registration_client/data/models/field.dart';
import 'package:registration_client/provider/global_provider.dart';
import 'package:provider/provider.dart';

class JsonHtmlRenderer extends StatelessWidget {
  const JsonHtmlRenderer(
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

    return Html(data: data.description);
  }
}

