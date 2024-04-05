/*
 * Copyright (c) Modular Open Source Identity Platform
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *
*/

import 'dart:typed_data';

import 'package:flutter/material.dart';

import '../../utils/app_config.dart';

class PreviewScreen extends StatefulWidget {
  final Uint8List bytes;
  const PreviewScreen({
    required this.bytes,
    super.key});

  @override
  State<PreviewScreen> createState() => _PreviewScreenState();
}

class _PreviewScreenState extends State<PreviewScreen> {
  @override
  Widget build(BuildContext context) {
    return  Scaffold(
      appBar: AppBar(
        automaticallyImplyLeading: true,
        elevation: 1,
        surfaceTintColor: transparentColor,
        backgroundColor: appWhite,
      ),
      body: Padding(
        padding: const EdgeInsets.all(10.0),
        child: Column(
          children: [
            SizedBox(
              height: MediaQuery.of(context).size.height-100,
                width: MediaQuery.of(context).size.width,
                child: Image.memory(widget.bytes)),
          ],
        ),
      ),
    );
  }
}
