import 'dart:developer';
import 'dart:io';

import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';

import 'package:intl/intl.dart';
import 'package:provider/provider.dart';
import 'package:registration_client/provider/registration_task_provider.dart';
import 'package:registration_client/ui/process_ui/widgets/dropdown_control.dart';
import 'package:registration_client/ui/process_ui/widgets/dropdown_document_control.dart';
import 'package:registration_client/ui/scanner/scanner.dart';

import '../../../model/field.dart';
import '../../../provider/global_provider.dart';
import 'custom_label.dart';

class DocumentUploadControl extends StatefulWidget {
  const DocumentUploadControl(
      {super.key, required this.validation, required this.field});

  final RegExp validation;
  final Field field;

  @override
  State<DocumentUploadControl> createState() => _DocumentUploadControlState();
}

class _DocumentUploadControlState extends State<DocumentUploadControl> {
  @override
  void initState() {
    _getSavedDate();
    super.initState();
  }

  void focusNextField(FocusNode currentFocus, FocusNode nextFocus) {
    currentFocus.unfocus();
    FocusScope.of(context).requestFocus(nextFocus);
  }

  List<String> poaList = [];

  void saveData() {
    // String targetDateString = widget.field.format ??
    //     "yyyy/MM/dd"
    //         .replaceAll('dd', _dayController.text.padLeft(2, '0'))
    //         .replaceAll('MM', _monthController.text.padLeft(2, '0'))
    //         .replaceAll('yyyy', _yearController.text);

    // context.read<RegistrationTaskProvider>().setDateField(
    //       widget.field.id ?? "",
    //       widget.field.subType ?? "",
    //       _dayController.text.padLeft(2, '0'),
    //       _monthController.text.padLeft(2, '0'),
    //       _yearController.text,
    //     );
    // context.read<GlobalProvider>().setInputMapValue(
    //       widget.field.id!,
    //       targetDateString,
    //       context.read<GlobalProvider>().feildDemographicsValues,
    //     );
  }

  void _getSavedDate() {
    // if (context
    //     .read<GlobalProvider>()
    //     .feildDemographicsValues
    //     .containsKey(widget.field.id)) {
    //   String targetDateFormat = widget.field.format ?? "yyyy/MM/dd";

    //   String savedDate = context
    //       .read<GlobalProvider>()
    //       .feildDemographicsValues[widget.field.id];
    //   DateTime parsedDate = DateFormat(targetDateFormat).parse(savedDate);
    //   _dayController.text = parsedDate.day.toString().padLeft(2, '0');
    //   _monthController.text = parsedDate.month.toString().padLeft(2, '0');
    //   _yearController.text = parsedDate.year.toString();
    // }
  }
  void addPoaToList(String item, Field e) {
    setState(() {
      poaList.add(item);
    });

    context
        .read<RegistrationTaskProvider>()
        .addDocument(e.id!, e.type!, "reference", poaList);
  }

  @override
  Widget build(BuildContext context) {
    bool isMobile = MediaQuery.of(context).size.width < 750;
    return Padding(
      padding: const EdgeInsets.all(12.0),
      child: Card(
        elevation: 3,
        margin: const EdgeInsets.symmetric(vertical: 1, horizontal: 12),
        child: Padding(
          padding: const EdgeInsets.symmetric(vertical: 24, horizontal: 18),
          child: isMobile
              ? Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    CustomLabel(feild: widget.field),
                    const SizedBox(
                      height: 10,
                    ),
                    DropDownControl(
                      field: widget.field,
                      // id: e.id ?? "",
                      // type: e.type ?? "",
                      validation: widget.validation,
                      // //onChanged: (value) => formValues[e.label!["eng"]!] = value,
                      // options: [],
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
                              addPoaToList(doc, widget.field);
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
                                      crossAxisAlignment:
                                          CrossAxisAlignment.start,
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
                )
              : Column(
                  children: [
                    Row(
                      mainAxisAlignment: MainAxisAlignment.start,
                      children: [
                        CustomLabel(feild: widget.field),
                      ],
                    ),
                    const SizedBox(
                      height: 10,
                    ),
                    Row(
                      //direction: Axis.horizontal,
                      //mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                      children: [
                        Expanded(
                          //child: Container(
                          //width: 400,
                          child: DropDownDocumentControl(
                            field: widget.field,
                            // id: e.id ?? "",
                            // type: e.type ?? "",
                            validation: widget.validation,
                            // //onChanged: (value) => formValues[e.label!["eng"]!] = value,
                            // options: [],
                          ),
                        ),
                        // ),
                        const SizedBox(
                          width: 50,
                        ),
                        Container(
                          width: 300,
                          child: Row(
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
                                    addPoaToList(doc, widget.field);
                                  },
                                  child: Text(
                                    "Scan",
                                    style: TextStyle(fontSize: 16),
                                  ),
                                ),
                              ),
                            ],
                          ),
                        ),
                      ],
                    ),
                    const SizedBox(
                      height: 10,
                    ),
                    poaList.isNotEmpty
                        ? Container(
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
                                          crossAxisAlignment:
                                              CrossAxisAlignment.start,
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
                            ))
                        : Container(),
                  ],
                ),
        ),
      ),
    );
  }

  // return Card(
  //   elevation: 0,
  //   margin: const EdgeInsets.symmetric(vertical: 1, horizontal: 12),
  //   child: Padding(
  //     padding: const EdgeInsets.symmetric(vertical: 24, horizontal: 16),
  //     child: Column(
  //       crossAxisAlignment: CrossAxisAlignment.start,
  //       children: [
  //         CustomLabel(feild: widget.field),
  //         const SizedBox(
  //           height: 10,
  //         ),
  //         Column(
  //           crossAxisAlignment: CrossAxisAlignment.start,
  //           children: [
  //             Row(
  //               crossAxisAlignment: CrossAxisAlignment.center,
  //               children: [
  //                 Flexible(
  //                   child: TextFormField(
  //                     onTap: () => _removeFocusFromAll("day"),
  //                     autovalidateMode: AutovalidateMode.onUserInteraction,
  //                     validator: (value) {
  //                       return feildValidation(value, "dd");
  //                     },
  //                     onChanged: (value) {
  //                       if (value.length >= 2) {
  //                         focusNextField(dayFocus, monthFocus);
  //                       }
  //                       saveData();
  //                     },
  //                     maxLength: 2,
  //                     focusNode: dayFocus,
  //                     keyboardType: TextInputType.number,
  //                     controller: _dayController,
  //                     decoration: InputDecoration(
  //                       contentPadding: const EdgeInsets.symmetric(
  //                           vertical: 12, horizontal: 16),
  //                       hintStyle: const TextStyle(
  //                           color: Color(0xff999999), fontSize: 14),
  //                       counterText: "",
  //                       hintText: 'DD',
  //                       border: OutlineInputBorder(
  //                         borderRadius: BorderRadius.circular(8.0),
  //                         borderSide: const BorderSide(
  //                             color: Color(0xff9B9B9F), width: 1),
  //                       ),
  //                     ),
  //                   ),
  //                 ),
  //                 const SizedBox(width: 8.0),
  //                 Flexible(
  //                   child: TextFormField(
  //                     onTap: () => _removeFocusFromAll("month"),
  //                     autovalidateMode: AutovalidateMode.onUserInteraction,
  //                     validator: (value) {
  //                       return feildValidation(value, "MM");
  //                     },
  //                     onChanged: (value) {
  //                       if (value.length >= 2) {
  //                         focusNextField(monthFocus, yearFocus);
  //                       }
  //                       saveData();
  //                     },
  //                     maxLength: 2,
  //                     focusNode: monthFocus,
  //                     keyboardType: TextInputType.number,
  //                     controller: _monthController,
  //                     decoration: InputDecoration(
  //                       contentPadding: const EdgeInsets.symmetric(
  //                           vertical: 12, horizontal: 16),
  //                       hintStyle: const TextStyle(
  //                           color: Color(0xff999999), fontSize: 14),
  //                       counterText: "",
  //                       hintText: 'MM',
  //                       border: OutlineInputBorder(
  //                         borderRadius: BorderRadius.circular(8.0),
  //                         borderSide: const BorderSide(
  //                             color: Color(0xff9B9B9F), width: 1),
  //                       ),
  //                     ),
  //                   ),
  //                 ),
  //                 const SizedBox(width: 8.0),
  //                 Flexible(
  //                   child: TextFormField(
  //                     validator: (value) {
  //                       return feildValidation(value, "yyyy");
  //                     },
  //                     onChanged: (value) {
  //                       saveData();
  //                     },
  //                     onTap: () => _removeFocusFromAll("year"),
  //                     autovalidateMode: AutovalidateMode.onUserInteraction,
  //                     maxLength: 4,
  //                     focusNode: yearFocus,
  //                     controller: _yearController,
  //                     keyboardType: TextInputType.number,
  //                     decoration: InputDecoration(
  //                       contentPadding: const EdgeInsets.symmetric(
  //                           vertical: 12, horizontal: 16),
  //                       hintStyle: const TextStyle(
  //                           color: Color(0xff999999), fontSize: 14),
  //                       counterText: "",
  //                       hintText: 'YYYY',
  //                       border: OutlineInputBorder(
  //                         borderRadius: BorderRadius.circular(8.0),
  //                         borderSide: const BorderSide(
  //                             color: Color(0xff9B9B9F), width: 1),
  //                       ),
  //                     ),
  //                   ),
  //                 ),
  //                 const SizedBox(width: 12),
  //                 const Text("OR"),
  //                 const SizedBox(width: 12),
  //                 Flexible(
  //                   child: TextFormField(
  //                     controller: _ageController,
  //                     keyboardType: TextInputType.number,
  //                     decoration: InputDecoration(
  //                       contentPadding: const EdgeInsets.symmetric(
  //                           vertical: 12, horizontal: 16),
  //                       hintStyle: const TextStyle(
  //                           color: Color(0xff999999), fontSize: 14),
  //                       hintText: 'Age',
  //                       border: OutlineInputBorder(
  //                         borderRadius: BorderRadius.circular(8.0),
  //                         borderSide: const BorderSide(
  //                             color: Color(0xff9B9B9F), width: 1),
  //                       ),
  //                     ),
  //                   ),
  //                 ),
  //               ],
  //             ),
  //             const SizedBox(
  //               height: 5,
  //             ),
  //           ],
  //         )
  //       ],
  //     ),
  //   ),
  // );
}
