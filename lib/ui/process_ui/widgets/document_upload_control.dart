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
    //load from the map
    final scannedPagesMap =
        context.read<GlobalProvider>().scannedPages[widget.field.id];

    if (scannedPagesMap != null) {
      setState(() {
        imageBytesList = scannedPagesMap;
      });
    }

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

    print("The selected value for dropdown for ${e.id!} is ${selectedValue}");
    Uint8List myBytes = Uint8List.fromList(bytes);
    context
        .read<RegistrationTaskProvider>()
        .addDocument(e.id!, selectedValue, "reference", myBytes);
  }

  Future<void> getScannedDocuments(Field e) async {
    try {
      final listofscannedDoc = await DocumentApi().getScannedPages(e.id!);
      context.read<GlobalProvider>().setScannedPages(e.id!, listofscannedDoc);
      setState(() {
        imageBytesList = listofscannedDoc;
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

  String selectedValue = '';
  void onDropDownChanged(String value) {
    setState(() {
      selectedValue = value;
    });
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
                    // CustomLabel(field: widget.field),
                    // const SizedBox(
                    //   height: 10,
                    // ),
                    DropDownDocumentControl(
                      field: widget.field,
                      validation: widget.validation,
                      onChanged: onDropDownChanged,
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
                    const SizedBox(
                      height: 10,
                    ),
                    imageBytesList.isNotEmpty
                        ? Container(
                            height: 80,
                            child: ListView(
                              scrollDirection: Axis.horizontal,
                              children: imageBytesList.map((item) {
                                return Card(
                                  child: Container(
                                    height: 70,
                                    width: 90,
                                    child: Image.memory(item!),
                                  ),
                                );
                              }).toList(),
                            ))
                        : Container(),
                  ],
                )
              : Column(
                  children: [
                    // Row(
                    //   mainAxisAlignment: MainAxisAlignment.start,
                    //   children: [
                    //     CustomLabel(field: widget.field),
                    //   ],
                    // ),
                    // const SizedBox(
                    //   height: 10,
                    // ),
                    Row(
                      children: [
                        Expanded(
                          child: DropDownDocumentControl(
                            field: widget.field,
                            validation: widget.validation,
                            onChanged: onDropDownChanged,
                          ),
                        ),
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
                    imageBytesList.isNotEmpty
                        ? Container(
                            height: 80,
                            child: ListView(
                              scrollDirection: Axis.horizontal,
                              children: imageBytesList.map((item) {
                                return Card(
                                  child: Container(
                                    height: 70,
                                    width: 90,
                                    child: Image.memory(item!),
                                  ),
                                );
                              }).toList(),
                            ))
                        : Container(),
                  ],
                ),
        ),
      ),
    );
  }
}
