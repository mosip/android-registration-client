import 'package:flutter/material.dart';

import '../../utils/app_config.dart';

class TabletFooter extends StatelessWidget {
  const TabletFooter({super.key});

  @override
  Widget build(BuildContext context) {
    return Container(
      height: 50,
      width: double.infinity,
      decoration: const BoxDecoration(
        border: Border(
          top: BorderSide(
            color: Color.fromARGB(255, 249, 249, 249),
          ),
        ),
        color: Color(0xffFCFCFC),
      ),
      child: Center(
          child: Text(
        "Community Registration - Client Version 1.2.0.1.B2",
        style: TextStyle(
            color: const Color(0xff6F6E6E), fontSize: 14, fontWeight: regular),
      )),
    );
  }
}
