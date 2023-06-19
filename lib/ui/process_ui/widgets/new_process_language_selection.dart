import 'package:flutter/material.dart';
import 'package:flutter/src/widgets/framework.dart';
import 'package:flutter/src/widgets/placeholder.dart';

import 'package:provider/provider.dart';
import 'package:registration_client/model/process.dart';
import 'package:registration_client/provider/global_provider.dart';

import 'package:registration_client/ui/process_ui/new_process.dart';

class NewProcessLanguageSelection extends StatelessWidget {
  const NewProcessLanguageSelection({super.key, required this.newProcess});
  final Process newProcess;

  @override
  Widget build(BuildContext context) {
    return AlertDialog(
      title: Text("Select Language"),
      content: Column(
        children: [
          Row(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              ...context
                  .watch<GlobalProvider>()
                  .languageMap
                  .entries
                  .map((e) => Column(
                        children: [
                          Text(
                            e.key,
                            style: Theme.of(context).textTheme.titleSmall,
                          ),
                          Checkbox(
                              value: e.value,
                              onChanged: (bool? newValue) {
                                if (e.key != "English") {
                                  context
                                      .read<GlobalProvider>()
                                      .addRemoveLang(e.key, newValue!);
                                }
                              })
                        ],
                      ))
                  .toList(),
            ],
          ),
          Row(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              ...context
                  .read<GlobalProvider>()
                  .chosenLang
                  .map((e) => Row(
                        children: [
                          Text(e),
                          SizedBox(
                            width: 10,
                          )
                        ],
                      ))
                  .toList(),
            ],
          ),
        ],
      ),
      actions: [
        OutlinedButton(
          onPressed: () {},
          child: Text("CANCEL"),
        ),
        ElevatedButton(
            onPressed: () {
              Navigator.pushNamed(context, NewProcess.routeName,
                  arguments: {"process": newProcess});
            },
            child: Text("SUBMIT"))
      ],
    );
  }
}
