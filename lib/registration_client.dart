/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
*/

import 'dart:convert';
import 'dart:io';

import 'package:document_scanner/document_scanner.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:registration_client/app_config.dart';
import 'package:registration_client/const/utils.dart';
import 'package:registration_client/demographic_details_view.dart';
import 'package:provider/provider.dart';

import 'provider/global_provider.dart';

class RegistrationClient extends StatefulWidget {
  const RegistrationClient({super.key});

  @override
  State<RegistrationClient> createState() => _RegistrationClientState();
}

class _RegistrationClientState extends State<RegistrationClient> {
  // This widget is the root of your application.
  static const platform =
      MethodChannel('com.flutter.dev/keymanager.test-machine');
  String syncDataResponse = '';

  @override
  Widget build(BuildContext context) {
    return SafeArea(
      child: Scaffold(
        appBar: AppBar(
          title: const Text('Welcome Piyush!'),
        ),
        body: GridView.count(
          primary: false,
          padding: const EdgeInsets.all(20),
          crossAxisSpacing: 10,
          mainAxisSpacing: 10,
          crossAxisCount: 2,
          children: [
            InkWell(
              onTap: () {
                Navigator.push(
                    context,
                    MaterialPageRoute(
                      builder: (context) =>
                          const MyHomePage(title: "Mosip New Registration"),
                    ));
              },
              child: Container(
                padding: const EdgeInsets.all(8),
                color: Utils.appSolidPrimary,
                child: Center(
                  child: Text(
                    "New registration",
                    style: Utils.mobileButtonText,
                  ),
                ),
              ),
            ),
            InkWell(
              onTap: () {
                _masterDataSync();
              },
              child: Container(
                padding: const EdgeInsets.all(8),
                color: Utils.appSolidPrimary,
                child: Center(
                    child: Text(
                  'Master Data Sync',
                  style: Utils.mobileButtonText,
                )),
              ),
            ),
            Container(
              padding: const EdgeInsets.all(8),
              color: Utils.appSolidPrimary,
              child: Center(
                  child: Text(
                'Packet Bundling',
                style: Utils.mobileButtonText,
              )),
            ),
            Container(
              padding: const EdgeInsets.all(8),
              color: Utils.appSolidPrimary,
              child: Center(
                  child: Text(
                'Biometrics Enabled',
                style: Utils.mobileButtonText,
              )),
            ),
          ],
        ),
      ),
    );
  }

  Future<void> _masterDataSync() async {
    String result;
    try {
      result = await platform.invokeMethod("masterDataSync");
    } on PlatformException catch (e) {
      result = "Some Error Occurred: $e";
    }
    setState(() {
      syncDataResponse = result;
    });
    debugPrint(syncDataResponse);
  }
}

class MyHomePage extends StatefulWidget {
  const MyHomePage({super.key, required this.title});

  final String title;

  @override
  State<MyHomePage> createState() => _MyHomePageState();
}

class _MyHomePageState extends State<MyHomePage> {
  int _counter = 0;

  void _incrementCounter() {
    setState(() {
      _counter++;
    });
  }

  @override
  Widget build(BuildContext context) {
    File scannedDocument;
    var height = MediaQuery.of(context).size.height;
    return Scaffold(
      appBar: AppBar(
        title: Text(widget.title),
      ),
      body: Center(
        child: Column(
          children: <Widget>[
            SizedBox(
              height: 25,
            ),
            Image.asset(
              appIcon,
              scale: appIconScale,
            ),
            SizedBox(
              height: height * .2,
            ),
            // SvgPicture.asset("assets/images/svg-1.svg",
            //     semanticsLabel: "SVG Icon"),
            // OutlinedButton(
            //   onPressed: () {

            //   },
            //   child: Text("Scanner"),
            // ),

            Text(
              "Choose your language",
              style: Theme.of(context).textTheme.titleLarge,
            ),
            Row(
              mainAxisAlignment: MainAxisAlignment.spaceEvenly,
              children: [
                Row(
                  children: [
                    Radio(
                        value: 1,
                        groupValue:
                            context.watch<GlobalProvider>().selectedLangCode,
                        onChanged: (value) {
                          context.read<GlobalProvider>().selectedLangCode =
                              value;
                        }),
                    Text("English"),
                  ],
                ),
                Row(
                  children: [
                    Radio(
                        value: 2,
                        groupValue:
                            context.watch<GlobalProvider>().selectedLangCode,
                        onChanged: (value) {
                          context.read<GlobalProvider>().selectedLangCode =
                              value;
                        }),
                    Text("Arabic"),
                  ],
                ),
                Row(
                  children: [
                    Radio(
                        value: 3,
                        groupValue:
                            context.watch<GlobalProvider>().selectedLangCode,
                        onChanged: (value) {
                          context.read<GlobalProvider>().selectedLangCode =
                              value;
                        }),
                    Text("French"),
                  ],
                ),
              ],
            ),
            SizedBox(
              height: height * .2,
            ),
            ElevatedButton(
                onPressed: () {
                  Navigator.push(
                      context,
                      MaterialPageRoute(
                          builder: (context) => DemographicDetailsView()));
                },
                child: Text("Start New Registration")),
            // const Text(
            //   'You have pushed the button this many times:',
            // ),
            // Text(
            //   '$_counter',
            //   style: Theme.of(context).textTheme.headlineMedium,
            // ),
          ],
        ),
      ),
      // floatingActionButton: FloatingActionButton(
      //   onPressed: _incrementCounter,
      //   tooltip: 'Increment',
      //   child: const Icon(Icons.add),
      // ), // This trailing comma makes auto-formatting nicer for build methods.
    );
  }
}
