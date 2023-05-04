/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
*/

import 'dart:io';

import 'package:file_picker/file_picker.dart';
import 'package:flutter/material.dart';
import 'package:image_cropper/image_cropper.dart';
import 'package:image_picker/image_picker.dart';
import 'package:registration_client/app_config.dart';
import 'package:registration_client/data/models/field.dart';
import 'package:registration_client/provider/global_provider.dart';

import 'package:provider/provider.dart';

XFile? photo;
CroppedFile? croppedFile;
List<File>? files;

class JsonFileUpload extends StatefulWidget {
  const JsonFileUpload(
      {super.key, required this.screenIndex, required this.fieldIndex});

  final int screenIndex;
  final int fieldIndex;

  @override
  State<JsonFileUpload> createState() => _JsonFileUploadState();
}

class _JsonFileUploadState extends State<JsonFileUpload> {
  @override
  Widget build(BuildContext context) {
    if (context
            .read<GlobalProvider>()
            .twoDArray![widget.screenIndex]![widget.fieldIndex] !=
        -1) {
      files = context
          .read<GlobalProvider>()
          .twoDArray![widget.screenIndex]![widget.fieldIndex];
    } else {
      files = [];
    }
    final ImagePicker picker = ImagePicker();
    var width = MediaQuery.of(context).size.width;
    var height = MediaQuery.of(context).size.height;
    Field data = context
        .watch<GlobalProvider>()
        .processParsed!
        .screens!
        .elementAt(widget.screenIndex)!
        .fields!
        .elementAt(widget.fieldIndex)!;

    return Container(
      padding: EdgeInsets.all(8.0),
      decoration: BoxDecoration(
          border: Border.all(
            width: 1,
            color: Colors.grey,
          ),
          borderRadius: BorderRadius.circular(20)),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Row(
            children: [
              (data.required == true)
                  ? Container(
                      width: width * .47,
                      child: Text(
                        "${context.read<GlobalProvider>().chooseLanguage(data.label!)} * :",
                        style: Theme.of(context).textTheme.titleLarge,
                      ),
                    )
                  : Container(
                      width: width * .47,
                      child: Text(
                        "${context.read<GlobalProvider>().chooseLanguage(data.label!)} :",
                        style: Theme.of(context).textTheme.titleLarge,
                      ),
                    ),
              SizedBox(
                width: 10,
              ),
              OutlinedButton.icon(
                onPressed: () async {
                  photo = await picker.pickImage(source: ImageSource.camera);
                  if (photo != null) {
                    croppedFile = await ImageCropper().cropImage(
                      sourcePath: photo!.path,
                      compressFormat: ImageCompressFormat.jpg,
                      compressQuality: 100,
                      uiSettings: [
                        AndroidUiSettings(
                            toolbarTitle: 'Cropper',
                            toolbarColor: primarySolidColor1,
                            toolbarWidgetColor: primarySolidColor2,
                            activeControlsWidgetColor: secondaryColors[2],
                            initAspectRatio: CropAspectRatioPreset.original,
                            lockAspectRatio: false),
                      ],
                    );
                    if (croppedFile != null) {
                      File converter = File(croppedFile!.path);

                      Navigator.push(
                        context,
                        MaterialPageRoute(
                          builder: ((context) => Scaffold(
                                appBar: AppBar(
                                  automaticallyImplyLeading: false,
                                  title: Text("Preview"),
                                ),
                                body: SingleChildScrollView(
                                  child: SafeArea(
                                    child: Padding(
                                      padding: const EdgeInsets.fromLTRB(
                                          16, 15, 16, 5),
                                      child: Column(
                                        children: [
                                          SizedBox(
                                            height: 25,
                                            width: double.infinity,
                                          ),
                                          Image.file(
                                            converter,
                                          ),
                                          SizedBox(
                                            height: 25,
                                          ),
                                          Row(
                                            mainAxisAlignment:
                                                MainAxisAlignment.center,
                                            children: [
                                              ElevatedButton(
                                                  onPressed: () {
                                                    Navigator.pop(context);
                                                  },
                                                  child: Text("Cancel")),
                                              SizedBox(
                                                width: 25,
                                              ),
                                              ElevatedButton(
                                                  onPressed: () {
                                                    setState(() {
                                                      if (context
                                                                  .read<
                                                                      GlobalProvider>()
                                                                  .twoDArray![
                                                              widget
                                                                  .screenIndex]![widget
                                                              .fieldIndex] !=
                                                          -1) {
                                                        files = context
                                                                .read<
                                                                    GlobalProvider>()
                                                                .twoDArray![
                                                            widget
                                                                .screenIndex]![widget
                                                            .fieldIndex];
                                                      } else {
                                                        files = [];
                                                      }
                                                      files!.add(converter);

                                                      context
                                                              .read<
                                                                  GlobalProvider>()
                                                              .twoDArray![
                                                          widget
                                                              .screenIndex]![widget
                                                          .fieldIndex] = files;
                                                    });
                                                    Navigator.pop(context);
                                                  },
                                                  child: Text("Okay"))
                                            ],
                                          )
                                        ],
                                      ),
                                    ),
                                  ),
                                ),
                              )),
                        ),
                      );
                    }
                  }
                },
                label: Text("Camera"),
                icon: Icon(Icons.camera_alt),
                style: ButtonStyle(
                  visualDensity: VisualDensity.standard,
                  side: MaterialStateProperty.all<BorderSide>(
                    BorderSide(color: Theme.of(context).primaryColor),
                  ),
                ),
              ),
            ],
          ),
          (files != null)
              ? Row(
                  children: [
                    Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        SizedBox(height: 15),
                        ...files!
                            .map((e) => Row(
                                  children: [
                                    Container(
                                        width: width * .47,
                                        child:
                                            Text("${e.path.split("/").last},")),
                                    SizedBox(
                                      width: 10,
                                    ),
                                    OutlinedButton(
                                      onPressed: () {
                                        Navigator.push(
                                          context,
                                          MaterialPageRoute(
                                            builder: ((context) => Scaffold(
                                                  appBar: AppBar(
                                                    automaticallyImplyLeading:
                                                        false,
                                                    title: Text("Preview"),
                                                  ),
                                                  body: SingleChildScrollView(
                                                    child: SafeArea(
                                                      child: Padding(
                                                        padding:
                                                            const EdgeInsets
                                                                    .fromLTRB(
                                                                16, 15, 16, 5),
                                                        child: Column(
                                                          children: [
                                                            SizedBox(
                                                              height: 25,
                                                              width: double
                                                                  .infinity,
                                                            ),
                                                            Image.file(
                                                              e,
                                                            ),
                                                            SizedBox(
                                                              height: 25,
                                                            ),
                                                            Row(
                                                              mainAxisAlignment:
                                                                  MainAxisAlignment
                                                                      .center,
                                                              children: [
                                                                ElevatedButton(
                                                                    onPressed:
                                                                        () {
                                                                      setState(() {
                                                                        files!
                                                                          .remove(
                                                                              e);
                                                                      });
                                                                      Navigator.pop(
                                                                          context);
                                                                    },
                                                                    child: Text(
                                                                        "Delete")),
                                                                SizedBox(
                                                                  width: 25,
                                                                ),
                                                                ElevatedButton(
                                                                    onPressed:
                                                                        () {
                                                                      Navigator.pop(
                                                                          context);
                                                                    },
                                                                    child: Text(
                                                                        "Okay"))
                                                              ],
                                                            )
                                                          ],
                                                        ),
                                                      ),
                                                    ),
                                                  ),
                                                )),
                                          ),
                                        );
                                      },
                                      child: Text("Preview"),
                                    ),
                                  ],
                                ))
                            .toList(),
                      ],
                    ),
                  ],
                )
              : Container(),
        ],
      ),
    );
  }
}




// Upolad File Code
// OutlinedButton.icon(
                  //   onPressed: () async {
                  //     FilePickerResult? result = await FilePicker.platform
                  //         .pickFiles(allowMultiple: true);

                  //     if (result != null) {
                  //       setState(() {
                  //         if (context.read<GlobalProvider>().twoDArray![
                  //                 widget.screenIndex]![widget.fieldIndex] !=
                  //             -1) {
                  //           files = context.read<GlobalProvider>().twoDArray![
                  //               widget.screenIndex]![widget.fieldIndex];
                  //         } else {
                  //           files = [];
                  //         }
                  //         files!
                  //             .addAll(result.paths.map((path) => File(path!)));

                  //         context.read<GlobalProvider>().twoDArray![
                  //             widget.screenIndex]![widget.fieldIndex] = files;
                  //       });
                  //     } else {
                  //       // User canceled the picker
                  //     }
                  //   },
                  //   label: Text("Upload File"),
                  //   icon: Icon(Icons.file_upload),
                  //   style: ButtonStyle(
                  //     visualDensity: VisualDensity.standard,
                  //     side: MaterialStateProperty.all<BorderSide>(
                  //       BorderSide(color: Theme.of(context).primaryColor),
                  //     ),
                  //   ),
                  // ),