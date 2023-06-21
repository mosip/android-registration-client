import 'dart:developer';

import 'package:flutter/material.dart';
import 'package:flutter/src/widgets/framework.dart';
import 'package:flutter/src/widgets/placeholder.dart';
import 'package:registration_client/model/process.dart';
import 'package:registration_client/ui/process_ui/new_process.dart';
import 'package:registration_client/utils/app_config.dart';

class NewProcessLanguageSelection extends StatelessWidget {
  const NewProcessLanguageSelection({super.key, required this.newProcess});
  final Process newProcess;

  @override
  Widget build(BuildContext context) {
    List<Widget> language = [
      Row(
        children: [
          SizedBox(
            width: 40,
          ),
          Checkbox(
              value: true,
              onChanged: (_) {},
              fillColor: MaterialStateProperty.resolveWith<Color>(
                  (Set<MaterialState> states) {
                if (states.contains(MaterialState.disabled)) {
                  return solid_primary;
                }
                return solid_primary;
              })),
          const Text("English")
        ],
      ),
      Row(
        children: [
          SizedBox(
            width: 40,
          ),
          Checkbox(
              value: true,
              onChanged: (_) {},
              fillColor: MaterialStateProperty.resolveWith<Color>(
                  (Set<MaterialState> states) {
                if (states.contains(MaterialState.disabled)) {
                  return solid_primary;
                }
                return solid_primary;
              })),
          const Text("English")
        ],
      ),
      Row(
        children: [
          SizedBox(
            width: 40,
          ),
          Checkbox(
              value: true,
              onChanged: (_) {},
              fillColor: MaterialStateProperty.resolveWith<Color>(
                  (Set<MaterialState> states) {
                if (states.contains(MaterialState.disabled)) {
                  return solid_primary;
                }
                return solid_primary;
              })),
          const Text("English")
        ],
      ),
    ];

    return AlertDialog(
      title: const Text(
        "Select Language",
        style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold),
      ),
      contentPadding: const EdgeInsets.all(4),
      content: Column(
        children: [
          const Divider(),
          ListTile(
            minLeadingWidth: 0,
            title: Text(
              "Please select any two language for data entry",
              style: Theme.of(context).textTheme.bodyMedium,
            ),
            leading: Icon(
              Icons.check,
              color: Colors.green,
            ),
          ),
          ListTile(
            minLeadingWidth: 0,
            title: Text(
              "English is Mandatory for the residents to select as per government guidelines. The order of selection will lead to page view for demographic data.",
              style: Theme.of(context).textTheme.bodyMedium,
            ),
            leading: Icon(
              Icons.check,
              color: Colors.green,
            ),
          ),
          (MediaQuery.of(context).size.width >= 480)
              ? Row(
                  mainAxisAlignment: MainAxisAlignment.start,
                  children: language)
              : Column(
                  children: language,
                )
        ],
      ),
      actions: [
        const Divider(),
        Row(
          children: [
            Expanded(
              child: OutlinedButton(
                onPressed: () {},
                child: const Text("CANCEL"),
              ),
            ),
            const SizedBox(
              width: 10,
            ),
            Expanded(
              child: ElevatedButton(
                  onPressed: () {
                    Navigator.pushNamed(context, NewProcess.routeName,
                        arguments: {"process": newProcess});
                  },
                  child: const Text("SUBMIT")),
            )
          ],
        )
      ],
    );
  }
}
