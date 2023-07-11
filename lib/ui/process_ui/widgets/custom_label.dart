import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

import '../../../model/field.dart';
import '../../../provider/global_provider.dart';

class CustomLabel extends StatelessWidget {
  final Field feild;

  const CustomLabel({super.key, required this.feild});

  @override
  Widget build(BuildContext context) {
    return SingleChildScrollView(
      scrollDirection: Axis.horizontal,
      child: Row(
        children: [
          Text(
            context.read<GlobalProvider>().chooseLanguage(feild.label!),
          ),
          const SizedBox(
            width: 5,
          ),
          if (feild.inputRequired!)
            const Text(
              "*",
              style: TextStyle(color: Colors.red, fontSize: 15),
            )
        ],
      ),
    );
  }
}
