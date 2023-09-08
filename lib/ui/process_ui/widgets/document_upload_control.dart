
import 'dart:io';

import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';

import 'package:provider/provider.dart';
import 'package:registration_client/model/upload_document_data.dart';
import 'package:registration_client/pigeon/document_pigeon.dart';
import 'package:registration_client/provider/registration_task_provider.dart';
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

    if (context
        .read<GlobalProvider>()
        .fieldInputValue
        .containsKey(widget.field.id ?? "")) {
      selected = context
          .read<GlobalProvider>()
          .fieldInputValue[widget.field.id]
          .title!;
    }

    super.initState();
  }

  void focusNextField(FocusNode currentFocus, FocusNode nextFocus) {
    currentFocus.unfocus();
    FocusScope.of(context).requestFocus(nextFocus);
  }

  List<String> poaList = [];
  List<Uint8List?> imageBytesList = []; // list of image bytes

  _getAddDocumentProvider(Field e, Uint8List myBytes) {
    context
        .read<RegistrationTaskProvider>()
        .addDocument(e.id!, selected!, "reference", myBytes);
  }

  Future<void> addDocument(String item, Field e) async {
    final bytes = await getImageBytes(item);

    debugPrint("The selected value for dropdown for ${e.id!} is $selected");
    Uint8List myBytes = Uint8List.fromList(bytes);
    // context
    //     .read<RegistrationTaskProvider>()
    //     .addDocument(e.id!, selected!, "reference", myBytes);
    _getAddDocumentProvider(e, myBytes);
  }

  _setScannedPages(Field e, List<Uint8List?> listOfScannedDoc) {
    context.read<GlobalProvider>().setScannedPages(e.id!, listOfScannedDoc);
  }

  _setValueInMap() {
    context.read<GlobalProvider>().fieldInputValue[widget.field.id!] = doc;
  }

  Future<void> getScannedDocuments(Field e) async {
    try {
      final listOfScannedDoc = await DocumentApi().getScannedPages(e.id!);
      _setScannedPages(e, listOfScannedDoc);
      setState(() {
        imageBytesList = listOfScannedDoc;
        doc.listofImages = imageBytesList;
      });
      if (doc.title.isNotEmpty) {
        _setValueInMap();
      }
    } catch (e) {
      debugPrint("Error while getting scanned pages $e");
    }
  }

  Future<List<int>> getImageBytes(String imagePath) async {
    final File imageFile = File(imagePath);
    if (!imageFile.existsSync()) {
      throw Exception("File not found");
    }
    return await imageFile.readAsBytes();
  }

  UploadDocumentData doc = UploadDocumentData(
    title: "",
    listofImages: [],
  );
  String? selected;

  void saveData(value) {
    if (value != null) {
      if (widget.field.type == 'simpleType') {
        context
            .read<RegistrationTaskProvider>()
            .addSimpleTypeDemographicField(widget.field.id ?? "", value, "eng");
      } else {
        context
            .read<RegistrationTaskProvider>()
            .addDemographicField(widget.field.id ?? "", value);
      }
    }
  }

  Future<List<String?>> _getDocumentValues(
      String fieldName, String langCode, String? applicantType) async {
    return await context
        .read<RegistrationTaskProvider>()
        .getDocumentValues(fieldName, langCode, applicantType);
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
                    FutureBuilder(
                        future: _getDocumentValues(
                            widget.field.subType!, "eng", null),
                        builder: (BuildContext context,
                            AsyncSnapshot<List<String?>> snapshot) {
                          return Card(
                            elevation: 0,
                            margin: const EdgeInsets.symmetric(
                                vertical: 1, horizontal: 12),
                            child: Padding(
                              padding: const EdgeInsets.symmetric(
                                  vertical: 24, horizontal: 16),
                              child: Column(
                                crossAxisAlignment: CrossAxisAlignment.start,
                                children: [
                                  CustomLabel(field: widget.field),
                                  const SizedBox(
                                    height: 10,
                                  ),
                                  snapshot.hasData
                                      ? DropdownButtonFormField(
                                          icon: const Icon(null),
                                          decoration: InputDecoration(
                                            contentPadding:
                                                const EdgeInsets.symmetric(
                                                    horizontal: 16.0),
                                            border: OutlineInputBorder(
                                              borderRadius:
                                                  BorderRadius.circular(8.0),
                                              borderSide: const BorderSide(
                                                color: Colors.grey,
                                                width: 1.0,
                                              ),
                                            ),
                                            hintText: "Select Option",
                                            hintStyle: const TextStyle(
                                                color: Color(0xff999999)),
                                          ),
                                          items: snapshot.data!
                                              .map((option) => DropdownMenuItem(
                                                    value: option,
                                                    child: Text(option!),
                                                  ))
                                              .toList(),
                                          autovalidateMode: AutovalidateMode
                                              .onUserInteraction,
                                          value: selected,
                                          validator: (value) {
                                            if (!widget.field.required! &&
                                                widget.field.requiredOn!
                                                    .isEmpty) {
                                              return null;
                                            }
                                            if ((value == null ||
                                                    value.isEmpty) &&
                                                widget.field.inputRequired!) {
                                              return 'Please select a value';
                                            }
                                            if (!widget.validation
                                                .hasMatch(value!)) {
                                              return 'Invalid input';
                                            }
                                            return null;
                                          },
                                          onChanged: (value) {
                                            saveData(value);

                                            setState(() {
                                              selected = value!;
                                              doc.title = value;
                                            });
                                          },
                                        )
                                      : const SizedBox.shrink(),
                                ],
                              ),
                            ),
                          );
                        }),

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
                                        const Scanner(title: "Scan Document")),
                              );

                              await addDocument(doc, widget.field);
                              await getScannedDocuments(widget.field);
                            },
                            child: const Text(
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
                        ? SizedBox(
                            height: 80,
                            child: ListView(
                              scrollDirection: Axis.horizontal,
                              children: imageBytesList.map((item) {
                                return Card(
                                  child: SizedBox(
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
                            child: FutureBuilder(
                                future: _getDocumentValues(
                                    widget.field.subType!, "eng", null),
                                builder: (BuildContext context,
                                    AsyncSnapshot<List<String?>> snapshot) {
                                  return Card(
                                    elevation: 0,
                                    margin: const EdgeInsets.symmetric(
                                        vertical: 1, horizontal: 12),
                                    child: Padding(
                                      padding: const EdgeInsets.symmetric(
                                          vertical: 24, horizontal: 16),
                                      child: Column(
                                        crossAxisAlignment:
                                            CrossAxisAlignment.start,
                                        children: [
                                          CustomLabel(field: widget.field),
                                          const SizedBox(
                                            height: 10,
                                          ),
                                          snapshot.hasData
                                              ? DropdownButtonFormField(
                                                  icon: const Icon(null),
                                                  decoration: InputDecoration(
                                                    contentPadding:
                                                        const EdgeInsets
                                                                .symmetric(
                                                            horizontal: 16.0),
                                                    border: OutlineInputBorder(
                                                      borderRadius:
                                                          BorderRadius.circular(
                                                              8.0),
                                                      borderSide:
                                                          const BorderSide(
                                                        color: Colors.grey,
                                                        width: 1.0,
                                                      ),
                                                    ),
                                                    hintText: "Select Option",
                                                    hintStyle: const TextStyle(
                                                        color:
                                                            Color(0xff999999)),
                                                  ),
                                                  items: snapshot.data!
                                                      .map((option) =>
                                                          DropdownMenuItem(
                                                            value: option,
                                                            child:
                                                                Text(option!),
                                                          ))
                                                      .toList(),
                                                  autovalidateMode:
                                                      AutovalidateMode
                                                          .onUserInteraction,
                                                  value: selected,
                                                  validator: (value) {
                                                    if (!widget
                                                            .field.required! &&
                                                        widget.field.requiredOn!
                                                            .isEmpty) {
                                                      return null;
                                                    }
                                                    if ((value == null ||
                                                            value.isEmpty) &&
                                                        widget.field
                                                            .inputRequired!) {
                                                      return 'Please select a value';
                                                    }
                                                    if (!widget.validation
                                                        .hasMatch(value!)) {
                                                      return 'Invalid input';
                                                    }
                                                    return null;
                                                  },
                                                  onChanged: (value) {
                                                    saveData(value);

                                                    setState(() {
                                                      selected = value!;
                                                      doc.title = value;
                                                    });
                                                  },
                                                )
                                              : const SizedBox.shrink(),
                                        ],
                                      ),
                                    ),
                                  );
                                })),
                        const SizedBox(
                          width: 50,
                        ),
                        SizedBox(
                          width: 300,
                          child: Row(
                            children: [
                              Expanded(
                                child: OutlinedButton(
                                  onPressed: () async {
                                    var doc = await Navigator.push(
                                      context,
                                      MaterialPageRoute(
                                          builder: (context) => const Scanner(
                                              title: "Scan Document")),
                                    );

                                    await addDocument(doc, widget.field);

                                    await getScannedDocuments(widget.field);
                                  },
                                  child: const Text(
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
                        ? SizedBox(
                            height: 80,
                            child: ListView(
                              scrollDirection: Axis.horizontal,
                              children: imageBytesList.map((item) {
                                return Card(
                                  child: SizedBox(
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
