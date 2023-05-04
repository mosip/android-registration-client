/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
*/

import 'dart:convert';

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

class CredentialsPage extends StatefulWidget {
  const CredentialsPage({super.key});

  @override
  State<CredentialsPage> createState() => _CredentialsPageState();
}

class _CredentialsPageState extends State<CredentialsPage> {
  static const platform = MethodChannel('com.flutter.dev/keymanager.test-machine');
  String machineDetails = '';

  @override
  void initState() {
    super.initState();
    _getMachineDetails();
  }
  
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Device Credentials'),
      ),
      body: SelectableText(machineDetails),
    );
  }

  Future<void> _getMachineDetails() async {
    String resultText;
    try {
      resultText = await platform.invokeMethod('getMachineDetails');
      Map<String, dynamic> machineMap = jsonDecode(resultText);
      debugPrint("Machine Map $machineMap");
    } on PlatformException catch (e) {
      resultText = "Failed to get platform version: '${e.message}'.";
    }

    setState(() {
      machineDetails = resultText;
    });
  }
}