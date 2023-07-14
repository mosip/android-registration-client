import 'dart:io';

import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';

import 'package:provider/provider.dart';
import 'package:registration_client/model/field.dart';
import 'package:registration_client/model/screen.dart';
import 'package:registration_client/provider/global_provider.dart';
import 'package:registration_client/ui/process_ui/widgets/age_date_control.dart';
import 'package:registration_client/ui/process_ui/widgets/checkbox_control.dart';
import 'package:registration_client/ui/process_ui/widgets/dropdown_control.dart';
import 'package:registration_client/ui/process_ui/widgets/html_box_control.dart';
import 'package:registration_client/ui/process_ui/widgets/custom_label.dart';
import 'package:registration_client/ui/process_ui/widgets/preferred_lang_button_control.dart';
import 'package:registration_client/ui/scanner/scanner.dart';
import 'dart:developer';

import 'package:registration_client/utils/app_config.dart';

import 'package:registration_client/ui/process_ui/widgets/textbox_control.dart';

import 'radio_button_control.dart';

class NewProcessScreenContent extends StatefulWidget {
  const NewProcessScreenContent(
      {super.key, required this.context, required this.screen});
  final BuildContext context;
  final Screen screen;

  @override
  State<NewProcessScreenContent> createState() =>
      _NewProcessScreenContentState();
}

class _NewProcessScreenContentState extends State<NewProcessScreenContent> {
  Map<String, dynamic> formValues = {};
  List<String> poaList = [];
  List<String> poiList = [];
  List<String> porList = [];
  List<String> pobList = [];
  List<String> poeList = [];
  void addPoaToList(String item) {
    setState(() {
      poaList.add(item);
    });
  }

  void addPoiToList(String item) {
    setState(() {
      poiList.add(item);
    });
  }

  void addPorToList(String item) {
    setState(() {
      porList.add(item);
    });
  }

  void addPobToList(String item) {
    setState(() {
      pobList.add(item);
    });
  }

  void addPoeToList(String item) {
    setState(() {
      poeList.add(item);
    });
  }

  Widget widgetType(Field e) {
    RegExp regexPattern = RegExp(r'^.*$');

    if (e.validators!.isNotEmpty) {
      final validation = e.validators?.first?.validator;
      if (validation != null) {
        regexPattern = RegExp(validation);
      }
    }

    if (e.controlType == "checkbox") {
      return CheckboxControl(field: e);
    }
    if (e.controlType == "html") {
      return HtmlBoxControl(field: e);
    }
    if (e.controlType == "button") {
      if (e.subType == "preferredLang") {
        return PreferredLangButtonControl(field: e);
      }

      if (e.subType == "gender" || e.subType == "residenceStatus") {
        Map<String, List<String>> values = {
          'gender': ["Female", "Male", "Others"],
          'residenceStatus': ["Permanent", "Temporary"],
        };
        return Card(
          elevation: 0,
          margin: const EdgeInsets.symmetric(vertical: 1, horizontal: 12),
          child: Padding(
            padding: const EdgeInsets.symmetric(vertical: 24, horizontal: 18),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                CustomLabel(feild: e),
                RadioButtonControl(
                  onChanged: (value) => formValues[e.label!["eng"]!] = value,
                  values: values[e.subType] ?? [],
                ),
              ],
            ),
          ),
        );
      }
      return Text("${e.controlType}");
    }
    if (e.controlType == "textbox") {
      List<String> choosenLang = context.read<GlobalProvider>().chosenLang;
      List<String> singleTextBox = [
        "Phone",
        "Email",
        "introducerName",
        "RID",
        "UIN",
        "none"
      ];
      if (singleTextBox.contains(e.subType)) {
        choosenLang = ["English"];
      }

      return Card(
        elevation: 0,
        margin: const EdgeInsets.symmetric(vertical: 1, horizontal: 12),
        child: Padding(
          padding: const EdgeInsets.symmetric(vertical: 24, horizontal: 18),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              CustomLabel(feild: e),
              const SizedBox(
                height: 10,
              ),
              Column(
                children: choosenLang.map((code) {
                  String newCode =
                      context.read<GlobalProvider>().langToCode(code);
                  return TextBoxControl(
                      onChanged: (value) =>
                          formValues[e.label![newCode]!] = value,
                      label: e.label![newCode]!.toString(),
                      lang: newCode,
                      validation: regexPattern);
                }).toList(),
              ),
            ],
          ),
        ),
      );
    }
    if (e.controlType == "dropdown") {
      return Card(
        elevation: 0,
        margin: const EdgeInsets.symmetric(vertical: 1, horizontal: 12),
        child: Padding(
          padding: const EdgeInsets.symmetric(vertical: 24, horizontal: 18),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              CustomLabel(feild: e),
              const SizedBox(
                height: 10,
              ),
              DropDownControl(
                onChanged: (value) => formValues[e.label!["eng"]!] = value,
              ),
            ],
          ),
        ),
      );
    }
    if (e.controlType == "ageDate") {
      return Card(
        elevation: 0,
        margin: const EdgeInsets.symmetric(vertical: 1, horizontal: 12),
        child: Padding(
          padding: const EdgeInsets.symmetric(vertical: 24, horizontal: 18),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              CustomLabel(feild: e),
              const SizedBox(
                height: 10,
              ),
              AgeDateControl(
                onChanged: (value) => formValues[e.label!["eng"]!] = value,
                validation: regexPattern,
              ),
            ],
          ),
        ),
      );
    }
    if (e.controlType == "fileupload") {
      if (e.subType == "POA") {
        return Card(
          elevation: 0,
          margin: const EdgeInsets.symmetric(vertical: 1, horizontal: 12),
          child: Padding(
            padding: const EdgeInsets.symmetric(vertical: 24, horizontal: 18),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                CustomLabel(feild: e),
                const SizedBox(
                  height: 10,
                ),
                DropDownControl(
                  onChanged: (value) => formValues[e.label!["eng"]!] = value,
                ),
                const SizedBox(
                  height: 10,
                ),
                Row(
                  children: [
                    Expanded(
                      child: OutlinedButton(
                        onPressed: () async {
                          var doc = await Navigator.push(
                            context,
                            MaterialPageRoute(
                                builder: (context) =>
                                    Scanner(title: "Scan Document")),
                          );
                          //print('fileuploadFile $doc');
                          addPoaToList(doc);
                          
                        },
                        child: Text(
                          "Scan",
                          style: TextStyle(fontSize: 16),
                        ),
                      ),
                    ),
                  ],
                ),
              
                Container(
                    height: 80,
                    //width: 60,
                    child: ListView(
                      scrollDirection: Axis.horizontal,
                      children: poaList
                          .map(
                            (item) => Card(
                              child:
                                  //Row(
                                  //crossAxisAlignment: CrossAxisAlignment.start,
                                  //children: [
                                  Flexible(
                                child: Row(
                                  crossAxisAlignment: CrossAxisAlignment.start,
                                  children: [
                                    //Text(item),
                                    Container(
                                      height: 70,
                                      width: 90,
                                      child: kIsWeb
                                          ? Image.network(item)
                                          : Image.file(File(item)),
                                    )
                                    //Text(fileuploadFile ?? 'No text'),
                                  ],
                                ),
                              ),
                              //],
                              //),
                            ),
                          )
                          .toList(),
                    )),
              ],
            ),
          ),
        );
      
      } else if (e.subType == "POI") {
        return Card(
          elevation: 0,
          margin: const EdgeInsets.symmetric(vertical: 1, horizontal: 12),
          child: Padding(
            padding: const EdgeInsets.symmetric(vertical: 24, horizontal: 18),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                CustomLabel(feild: e),
                const SizedBox(
                  height: 10,
                ),
                DropDownControl(
                  onChanged: (value) => formValues[e.label!["eng"]!] = value,
                ),
                const SizedBox(
                  height: 10,
                ),
                Row(
                  children: [
                    Expanded(
                      child: OutlinedButton(
                        onPressed: () async {
                          var doc = await Navigator.push(
                            context,
                            MaterialPageRoute(
                                builder: (context) =>
                                    Scanner(title: "Scan Document")),
                          );
                          //print('fileuploadFile $doc');
                          addPoiToList(doc);
                          
                        },
                        child: Text(
                          "Scan",
                          style: TextStyle(fontSize: 16),
                        ),
                      ),
                    ),
                  ],
                ),
              
                Container(
                    height: 80,
                    //width: 60,
                    child: ListView(
                      scrollDirection: Axis.horizontal,
                      children: poiList
                          .map(
                            (item) => Card(
                              child:
                                  //Row(
                                  //crossAxisAlignment: CrossAxisAlignment.start,
                                  //children: [
                                  Flexible(
                                child: Row(
                                  crossAxisAlignment: CrossAxisAlignment.start,
                                  children: [
                                    //Text(item),
                                    Container(
                                      height: 70,
                                      width: 90,
                                      child: kIsWeb
                                          ? Image.network(item)
                                          : Image.file(File(item)),
                                    )
                                    //Text(fileuploadFile ?? 'No text'),
                                  ],
                                ),
                              ),
                              //],
                              //),
                            ),
                          )
                          .toList(),
                    )),
              ],
            ),
          ),
        );
      
      } else if (e.subType == "POR") {
        return Card(
          elevation: 0,
          margin: const EdgeInsets.symmetric(vertical: 1, horizontal: 12),
          child: Padding(
            padding: const EdgeInsets.symmetric(vertical: 24, horizontal: 18),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                CustomLabel(feild: e),
                const SizedBox(
                  height: 10,
                ),
                DropDownControl(
                  onChanged: (value) => formValues[e.label!["eng"]!] = value,
                ),
                const SizedBox(
                  height: 10,
                ),
                Row(
                  children: [
                    Expanded(
                      child: OutlinedButton(
                        onPressed: () async {
                          var doc = await Navigator.push(
                            context,
                            MaterialPageRoute(
                                builder: (context) =>
                                    Scanner(title: "Scan Document")),
                          );
                          //print('fileuploadFile $doc');
                          addPorToList(doc);
                          
                        },
                        child: Text(
                          "Scan",
                          style: TextStyle(fontSize: 16),
                        ),
                      ),
                    ),
                  ],
                ),
              
                Container(
                    height: 80,
                    //width: 60,
                    child: ListView(
                      scrollDirection: Axis.horizontal,
                      children: porList
                          .map(
                            (item) => Card(
                              child:
                                  //Row(
                                  //crossAxisAlignment: CrossAxisAlignment.start,
                                  //children: [
                                  Flexible(
                                child: Row(
                                  crossAxisAlignment: CrossAxisAlignment.start,
                                  children: [
                                    //Text(item),
                                    Container(
                                      height: 70,
                                      width: 90,
                                      child: kIsWeb
                                          ? Image.network(item)
                                          : Image.file(File(item)),
                                    )
                                    //Text(fileuploadFile ?? 'No text'),
                                  ],
                                ),
                              ),
                              //],
                              //),
                            ),
                          )
                          .toList(),
                    )),
              ],
            ),
          ),
        );
      
      } else if (e.subType == "POB") {
        return Card(
          elevation: 0,
          margin: const EdgeInsets.symmetric(vertical: 1, horizontal: 12),
          child: Padding(
            padding: const EdgeInsets.symmetric(vertical: 24, horizontal: 18),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                CustomLabel(feild: e),
                const SizedBox(
                  height: 10,
                ),
                DropDownControl(
                  onChanged: (value) => formValues[e.label!["eng"]!] = value,
                ),
                const SizedBox(
                  height: 10,
                ),
                Row(
                  children: [
                    Expanded(
                      child: OutlinedButton(
                        onPressed: () async {
                          var doc = await Navigator.push(
                            context,
                            MaterialPageRoute(
                                builder: (context) =>
                                    Scanner(title: "Scan Document")),
                          );
                          //print('fileuploadFile $doc');
                          addPobToList(doc);
                          
                        },
                        child: Text(
                          "Scan",
                          style: TextStyle(fontSize: 16),
                        ),
                      ),
                    ),
                  ],
                ),
              
                Container(
                    height: 80,
                    //width: 60,
                    child: ListView(
                      scrollDirection: Axis.horizontal,
                      children: pobList
                          .map(
                            (item) => Card(
                              child:
                                  //Row(
                                  //crossAxisAlignment: CrossAxisAlignment.start,
                                  //children: [
                                  Flexible(
                                child: Row(
                                  crossAxisAlignment: CrossAxisAlignment.start,
                                  children: [
                                    //Text(item),
                                    Container(
                                      height: 70,
                                      width: 90,
                                      child: kIsWeb
                                          ? Image.network(item)
                                          : Image.file(File(item)),
                                    )
                                    //Text(fileuploadFile ?? 'No text'),
                                  ],
                                ),
                              ),
                              //],
                              //),
                            ),
                          )
                          .toList(),
                    )),
              ],
            ),
          ),
        );
      
      } else if (e.subType == "POE") {
        return Card(
          elevation: 0,
          margin: const EdgeInsets.symmetric(vertical: 1, horizontal: 12),
          child: Padding(
            padding: const EdgeInsets.symmetric(vertical: 24, horizontal: 18),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                CustomLabel(feild: e),
                const SizedBox(
                  height: 10,
                ),
                DropDownControl(
                  onChanged: (value) => formValues[e.label!["eng"]!] = value,
                ),
                const SizedBox(
                  height: 10,
                ),
                Row(
                  children: [
                    Expanded(
                      child: OutlinedButton(
                        onPressed: () async {
                          var doc = await Navigator.push(
                            context,
                            MaterialPageRoute(
                                builder: (context) =>
                                    Scanner(title: "Scan Document")),
                          );
                          //print('fileuploadFile $doc');
                          addPoeToList(doc);
                          
                        },
                        child: Text(
                          "Scan",
                          style: TextStyle(fontSize: 16),
                        ),
                      ),
                    ),
                  ],
                ),
              
                Container(
                    height: 80,
                    //width: 60,
                    child: ListView(
                      scrollDirection: Axis.horizontal,
                      children: poeList
                          .map(
                            (item) => Card(
                              child:
                                  //Row(
                                  //crossAxisAlignment: CrossAxisAlignment.start,
                                  //children: [
                                  Flexible(
                                child: Row(
                                  crossAxisAlignment: CrossAxisAlignment.start,
                                  children: [
                                    //Text(item),
                                    Container(
                                      height: 70,
                                      width: 90,
                                      child: kIsWeb
                                          ? Image.network(item)
                                          : Image.file(File(item)),
                                    )
                                    //Text(fileuploadFile ?? 'No text'),
                                  ],
                                ),
                              ),
                              //],
                              //),
                            ),
                          )
                          .toList(),
                    )),
              ],
            ),
          ),
        );
      
      }
      // return Card(
      //   elevation: 0,
      //   margin: const EdgeInsets.symmetric(vertical: 1, horizontal: 12),
      //   child: Padding(
      //     padding: const EdgeInsets.symmetric(vertical: 24, horizontal: 18),
      //     child: Column(
      //       crossAxisAlignment: CrossAxisAlignment.start,
      //       children: [
      //         CustomLabel(feild: e),
      //         const SizedBox(
      //           height: 10,
      //         ),
      //         DropDownControl(
      //           onChanged: (value) => formValues[e.label!["eng"]!] = value,
      //         ),
      //         const SizedBox(
      //           height: 10,
      //         ),
      //         Row(
      //           children: [
      //             Expanded(
      //               child: OutlinedButton(
      //                 onPressed: () async {
      //                   var doc = await Navigator.push(
      //                     context,
      //                     MaterialPageRoute(
      //                         builder: (context) =>
      //                             Scanner(title: "Scan Document")),
      //                   );
      //                   print('fileuploadFile $doc');
      //                   addItemToList(doc);
      //                   //myList.add(doc);
      //                   // showGeneralDialog(
      //                   //   context: context,
      //                   //   barrierDismissible: true,
      //                   //   barrierLabel: MaterialLocalizations.of(context)
      //                   //       .modalBarrierDismissLabel,
      //                   //   barrierColor: Colors.black45,
      //                   //   transitionDuration:
      //                   //       const Duration(milliseconds: 200),
      //                   //   pageBuilder: (BuildContext buildContext,
      //                   //       Animation animation,
      //                   //       Animation secondaryAnimation) {
      //                   //     return Scanner(title: "Scan Document");
      //                   //     // return Center(
      //                   //     //   child: Container(
      //                   //     //     width: MediaQuery.of(context).size.width - 10,
      //                   //     //     height: MediaQuery.of(context).size.height -  80,
      //                   //     //     padding: EdgeInsets.all(20),
      //                   //     //     color: Colors.white,
      //                   //     //     child: Column(
      //                   //     //       children: [
      //                   //     //         ElevatedButton(
      //                   //     //           onPressed: () {
      //                   //     //             Navigator.of(context).pop();
      //                   //     //           },
      //                   //     //           child: Text(
      //                   //     //             "Save",
      //                   //     //             style: TextStyle(color: Colors.white),
      //                   //     //           ),
      //                   //     //           //color: const Color(0xFF1BC0C5),
      //                   //     //         )
      //                   //     //       ],
      //                   //     //     ),
      //                   //     //   ),
      //                   //     // );
      //                   //   },
      //                   // );
      //                 },
      //                 child: Text(
      //                   "Scan",
      //                   style: TextStyle(fontSize: 16),
      //                 ),
      //               ),
      //             ),
      //           ],
      //         ),
      //         // fileuploadFile==''? const SizedBox():Container(
      //         //   height: 60,
      //         //   //width: 90,
      //         //   child: kIsWeb
      //         //                   ? Image.network(fileuploadFile)
      //         //                   : Image.file(File(fileuploadFile)),
      //         // )
      //         //Text(fileuploadFile ?? 'No text'),
      //         Container(
      //             height: 200,
      //             width: 60,
      //             child: ListView(
      //               children: myList
      //                   .map(
      //                     (item) => Card(
      //                       child: Row(
      //                         crossAxisAlignment: CrossAxisAlignment.start,
      //                         children: [
      //                           Flexible(
      //                             child: Column(
      //                               crossAxisAlignment:
      //                                   CrossAxisAlignment.start,
      //                               children: [
      //                                 //Text(item),
      //                                 Container(
      //                                   height: 60,
      //                                   //width: 90,
      //                                   child: kIsWeb
      //                                       ? Image.network(item)
      //                                       : Image.file(File(item)),
      //                                 )
      //                                 //Text(fileuploadFile ?? 'No text'),
      //                               ],
      //                             ),
      //                           ),
      //                         ],
      //                       ),
      //                     ),
      //                   )
      //                   .toList(),
      //             )),
      //       ],
      //     ),
      //   ),
      // );
    
    }

    return Text("${e.controlType}");
  }

  @override
  Widget build(BuildContext context) {
    return Column(
      children: [
        ...widget.screen.fields!.map((e) {
          if (e!.inputRequired == true) {
            return widgetType(e);
          }
          return Container();
        }).toList(),
      ],
    );
  }
}
