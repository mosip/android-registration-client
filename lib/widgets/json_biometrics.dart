/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
*/

import 'package:flutter/material.dart';
import 'package:flutter/src/widgets/framework.dart';
import 'package:registration_client/data/models/field.dart';
import 'package:registration_client/provider/global_provider.dart';

import 'package:provider/provider.dart';

class JsonBiometrics extends StatelessWidget {
  const JsonBiometrics(
      {super.key, required this.screenIndex, required this.fieldIndex});

  final int screenIndex;
  final int fieldIndex;

  @override
  Widget build(BuildContext context) {
    var height = MediaQuery.of(context).size.height;
    Field data = context
        .watch<GlobalProvider>()
        .processParsed!
        .screens!
        .elementAt(screenIndex)!
        .fields!
        .elementAt(fieldIndex)!;

    return Column(
      children: [
        (data.required == true)
            ? Text(
                "${context.read<GlobalProvider>().chooseLanguage(data.label!)} *")
            : Text(
                "${context.read<GlobalProvider>().chooseLanguage(data.label!)}"),
        SizedBox(
          height: 15,
        ),
        Container(
          height: height * .7,
          child: GridView(
            // shrinkWrap: true,
            gridDelegate:
                SliverGridDelegateWithFixedCrossAxisCount(crossAxisCount: 2),
            children: [
              InkWell(
                onTap: () {
                  Navigator.push(
                    context,
                    MaterialPageRoute(
                      builder: (context) => BiometricsScanner(
                          title: "Left Hand",
                          icon: "assets/images/left_hand.png",
                          thresholdValue: 40),
                    ),
                  );
                },
                child: Card(
                  shape: RoundedRectangleBorder(
                      borderRadius: BorderRadius.circular(20)),
                  child: Padding(
                    padding: const EdgeInsets.all(16.0),
                    child: Column(
                      children: [
                        Image.asset(
                          "assets/images/left_hand.png",
                          scale: 5,
                        ),
                        SizedBox(
                          height: 10,
                        ),
                        Text(
                          "Left Hand",
                          style: Theme.of(context).textTheme.titleLarge,
                        )
                      ],
                    ),
                  ),
                ),
              ),
              InkWell(
                onTap: () {
                  Navigator.push(
                    context,
                    MaterialPageRoute(
                      builder: (context) => BiometricsScanner(
                          title: "Right Hand",
                          icon: "assets/images/right_hand.png",
                          thresholdValue: 40),
                    ),
                  );
                },
                child: Card(
                  shape: RoundedRectangleBorder(
                      borderRadius: BorderRadius.circular(20)),
                  child: Padding(
                    padding: const EdgeInsets.all(16.0),
                    child: Column(
                      children: [
                        Image.asset(
                          "assets/images/right_hand.png",
                          scale: 5,
                        ),
                        SizedBox(
                          height: 10,
                        ),
                        Text(
                          "Right Hand",
                          style: Theme.of(context).textTheme.titleLarge,
                        )
                      ],
                    ),
                  ),
                ),
              ),
              InkWell(
                onTap: () {
                  Navigator.push(
                    context,
                    MaterialPageRoute(
                      builder: (context) => BiometricsScanner(
                          title: "Left Thumb",
                          icon: "assets/images/left_thumb.png",
                          thresholdValue: 60),
                    ),
                  );
                },
                child: Card(
                  shape: RoundedRectangleBorder(
                      borderRadius: BorderRadius.circular(20)),
                  child: Padding(
                    padding: const EdgeInsets.all(16.0),
                    child: Column(
                      children: [
                        Image.asset(
                          "assets/images/left_thumb.png",
                          scale: 5,
                        ),
                        SizedBox(
                          height: 10,
                        ),
                        Text(
                          "Left Thumb",
                          style: Theme.of(context).textTheme.titleLarge,
                        )
                      ],
                    ),
                  ),
                ),
              ),
              InkWell(
                onTap: () {
                  Navigator.push(
                    context,
                    MaterialPageRoute(
                      builder: (context) => BiometricsScanner(
                          title: "Right Thumb",
                          icon: "assets/images/right_thumb.png",
                          thresholdValue: 60),
                    ),
                  );
                },
                child: Card(
                  shape: RoundedRectangleBorder(
                      borderRadius: BorderRadius.circular(20)),
                  child: Padding(
                    padding: const EdgeInsets.all(16.0),
                    child: Column(
                      children: [
                        Image.asset(
                          "assets/images/right_thumb.png",
                          scale: 5,
                        ),
                        SizedBox(
                          height: 10,
                        ),
                        Text(
                          "Right Thumb",
                          style: Theme.of(context).textTheme.titleLarge,
                        )
                      ],
                    ),
                  ),
                ),
              ),
              InkWell(
                onTap: () {
                  Navigator.push(
                    context,
                    MaterialPageRoute(
                      builder: (context) => BiometricsScanner(
                          title: "Iris",
                          icon: "assets/images/eye-scanner.png",
                          thresholdValue: 80),
                    ),
                  );
                },
                child: Card(
                  shape: RoundedRectangleBorder(
                      borderRadius: BorderRadius.circular(20)),
                  child: Padding(
                    padding: const EdgeInsets.all(16.0),
                    child: Column(
                      children: [
                        Image.asset(
                          "assets/images/eye-scanner.png",
                          scale: 5,
                        ),
                        SizedBox(
                          height: 10,
                        ),
                        Text(
                          "Iris",
                          style: Theme.of(context).textTheme.titleLarge,
                        )
                      ],
                    ),
                  ),
                ),
              ),
              InkWell(
                onTap: () {
                  Navigator.push(
                    context,
                    MaterialPageRoute(
                      builder: (context) => BiometricsScanner(
                          title: "Face",
                          icon: "assets/images/face-id.png",
                          thresholdValue: 90),
                    ),
                  );
                },
                child: Card(
                  shape: RoundedRectangleBorder(
                      borderRadius: BorderRadius.circular(20)),
                  child: Padding(
                    padding: const EdgeInsets.all(16.0),
                    child: Column(
                      children: [
                        Image.asset(
                          "assets/images/face-id.png",
                          scale: 5,
                        ),
                        SizedBox(
                          height: 10,
                        ),
                        Text(
                          "Face",
                          style: Theme.of(context).textTheme.titleLarge,
                        )
                      ],
                    ),
                  ),
                ),
              ),
            ],
          ),
        )
      ],
    );
  }
}

class BiometricsScanner extends StatelessWidget {
  const BiometricsScanner(
      {super.key,
      required this.title,
      required this.thresholdValue,
      required this.icon});
  final String title;
  final int thresholdValue;
  final String icon;

  @override
  Widget build(BuildContext context) {
    var height = MediaQuery.of(context).size.height;
    return Scaffold(
      appBar: AppBar(
        title: Padding(
          padding: const EdgeInsets.all(0),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Text(
                title,
                style: Theme.of(context)
                    .textTheme
                    .titleLarge
                    ?.copyWith(color: Colors.white),
              ),
              Text(
                "Threshold Score : ${thresholdValue.toString()}",
                style: Theme.of(context)
                    .textTheme
                    .titleSmall
                    ?.copyWith(color: Colors.white),
              )
            ],
          ),
        ),
      ),
      body: SingleChildScrollView(
        child: SafeArea(
            child: Padding(
          padding: const EdgeInsets.fromLTRB(16, 15, 16, 5),
          child: Column(
            children: [
              SizedBox(
                width: double.infinity,
              ),
              Text("Attempt : 1",
                  style: Theme.of(context).textTheme.titleSmall),
              Text("Quality Score : 0",
                  style: Theme.of(context).textTheme.titleSmall),
              SizedBox(
                height: height * .25,
              ),
              Image.asset(
                icon,
                scale: 2,
              )
            ],
          ),
        )),
      ),
    );
  }
}
