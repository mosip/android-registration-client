import 'dart:developer';
import 'dart:io';

import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';

import 'package:intl/intl.dart';
import 'package:provider/provider.dart';
import 'package:registration_client/pigeon/document_pigeon.dart';
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
    _getSavedDocument();

    super.initState();
  }

  void focusNextField(FocusNode currentFocus, FocusNode nextFocus) {
    currentFocus.unfocus();
    FocusScope.of(context).requestFocus(nextFocus);
  }

  List<String> poaList = [];
  List<Uint8List?> imageBytesList = []; // list of image bytes

  void _getSavedDocument() async {
    final listofscannedDoc = await context
        .read<RegistrationTaskProvider>()
        .getScannedDocument(widget.field.id!);
    setState(() {
      imageBytesList = listofscannedDoc;
    });
  }

  Future<void> addDocument(String item, Field e) async {
    final bytes = await getImageBytes(item);

    Uint8List myBytes = Uint8List.fromList(bytes);
    // setState(() {
    //   poaList.add(item);
    // });
    context
        .read<RegistrationTaskProvider>()
        .addDocument(e.id!, e.type!, "reference", myBytes);
  }

  Future<void> getScannedDocuments(Field e) async {
    // final listofscannedDoc = await context
    //     .read<RegistrationTaskProvider>()
    //     .getScannedDocument(e.id!);
    try {
      final listofscannedDoc = await DocumentApi().getScannedPages(e.id!);
      setState(() {
        imageBytesList = listofscannedDoc;
        //imageBytesList = bytes;
      });
    } catch (e) {
      print("Error while getting scanned pages ${e}");
    }
  }

  Future<List<int>> getImageBytes(String imagePath) async {
    final File imageFile = File(imagePath);
    if (!imageFile.existsSync()) {
      throw Exception("File not found");
    }
    return await imageFile.readAsBytes();
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
                    DropDownDocumentControl(
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
                              addDocument(doc, widget.field);
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
                          children: imageBytesList
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
                                          // child: kIsWeb
                                          //     ? Image.network(item)
                                          //     : Image.file(File(item)),
                                          child: Image.memory(item!),
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

                                    await addDocument(doc, widget.field);

                                    await getScannedDocuments(widget.field);
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
                    //poaList.isNotEmpty
                    imageBytesList.isNotEmpty
                        ? Container(
                            height: 80,
                            //width: 60,
                            child: ListView(
                              scrollDirection: Axis.horizontal,
                              //children: poaList
                              children: imageBytesList
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
                                                // child: kIsWeb
                                                //     ? Image.network(item)
                                                //     : Image.file(File(item)),
                                                child: Image.memory(item!))
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
}
