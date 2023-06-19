import 'package:flutter/material.dart';
import 'package:flutter/src/widgets/framework.dart';
import 'package:flutter/src/widgets/placeholder.dart';
import 'package:registration_client/model/screen.dart';

class NewProcessScreenContent extends StatelessWidget {
  const NewProcessScreenContent({super.key, required this.context, required this.screen});
  final BuildContext context;
  final Screen screen;

  @override
  Widget build(BuildContext context) {
    return Column(
      children: [
        ...screen.fields!.map((e) =>Text("${e!.controlType}"),)
      ],
    );
  }
}