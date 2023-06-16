import 'package:flutter/material.dart';
import 'package:flutter/src/widgets/framework.dart';
import 'package:flutter/src/widgets/placeholder.dart';
import 'package:registration_client/model/process.dart';
import 'package:registration_client/ui/process_ui/new_process.dart';

class NewProcessLanguageSelection extends StatelessWidget {
  const NewProcessLanguageSelection({super.key, required this.newProcess});
  final Process newProcess;

  @override
  Widget build(BuildContext context) {
    return AlertDialog(
      title: Text("Select Language"),
      content: Column(),
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
