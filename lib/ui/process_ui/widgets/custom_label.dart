import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

import '../../../model/field.dart';
import '../../../provider/global_provider.dart';

class CustomLabel extends StatelessWidget {
  final Field field;

  const CustomLabel({super.key, required this.field});

  @override
  Widget build(BuildContext context) {
    return SingleChildScrollView(
      scrollDirection: Axis.horizontal,
      child: Row(
        children: [
          Text(
            context.read<GlobalProvider>().chooseLanguage(field.label!),
          ),
          const SizedBox(
            width: 5,
          ),
          if (field.required! || field.requiredOn!.isNotEmpty)
            const Text(
              "*",
              style: TextStyle(color: Colors.red, fontSize: 15),
            )
        ],
      ),
    );
  }
}
