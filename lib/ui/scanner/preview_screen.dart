import 'dart:typed_data';

import 'package:flutter/material.dart';

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
