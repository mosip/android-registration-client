
import 'dart:io';

import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';

import 'package:provider/provider.dart';
import 'package:registration_client/model/upload_document_data.dart';
import 'package:registration_client/pigeon/document_pigeon.dart';
import 'package:registration_client/provider/registration_task_provider.dart';
import 'package:registration_client/ui/scanner/scanner.dart';
import 'package:flutter_gen/gen_l10n/app_localizations.dart';
import 'package:registration_client/utils/app_style.dart';

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
      imageBytesList.clear();
      setState(() {
        imageBytesList.addAll(scannedPagesMap);
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

  Map<String, String> referenceLang = {
    "ara": "رقم المرجع",
    "fra": "Numéro de réference",
    "eng": "Reference Number",
    "kan": "ಉಲ್ಲೇಖ ಸಂಖ್ಯೆ",
    "hin": "संदर्भ संख्या",
    "tam": "குறிப்பு எண்",
  };

  String _getDataFromMap(String lang) {
    String response = "";
    if (context
        .read<GlobalProvider>()
        .fieldInputValue
        .containsKey(widget.field.id)) {
      if (widget.field.type == 'simpleType') {
        if ((context.read<GlobalProvider>().fieldInputValue[widget.field.id]
        as Map<String, dynamic>)
            .containsKey(lang)) {
          response =
          context.read<GlobalProvider>().fieldInputValue[widget.field.id][lang].referenceNumber;
          doc.referenceNumber = context.read<GlobalProvider>().fieldInputValue[widget.field.id][lang].referenceNumber;
          referenceNumber = context.read<GlobalProvider>().fieldInputValue[widget.field.id][lang].referenceNumber;
        }
      } else {
        response = context.read<GlobalProvider>().fieldInputValue[widget.field.id].referenceNumber;
        doc.referenceNumber = context.read<GlobalProvider>().fieldInputValue[widget.field.id].referenceNumber;
        referenceNumber = context.read<GlobalProvider>().fieldInputValue[widget.field.id].referenceNumber;
      }
    }
    return response;
  }

  void focusNextField(FocusNode currentFocus, FocusNode nextFocus) {
    currentFocus.unfocus();
    FocusScope.of(context).requestFocus(nextFocus);
  }

  List<String> poaList = [];
  List<Uint8List?> imageBytesList = List.empty(growable: true); // list of image bytes

  _getAddDocumentProvider(Field e, Uint8List myBytes, String referenceNumber) {
    context
        .read<RegistrationTaskProvider>()
        .addDocument(e.id!, selected!, referenceNumber, myBytes);
  }

  Future<void> addDocument(String item, Field e,String referenceNumber) async {
    final bytes = await getImageBytes(item);

    debugPrint("The selected value for dropdown for ${e.id!} is $selected");
    Uint8List myBytes = Uint8List.fromList(bytes);
    // context
    //     .read<RegistrationTaskProvider>()
    //     .addDocument(e.id!, selected!, "reference", myBytes);
    _getAddDocumentProvider(e, myBytes, referenceNumber);
  }

  _getRemoveDocumentProvider(Field e, int index){
    context.read<RegistrationTaskProvider>().removeDocument(e.id!, index);
  }

  _setScannedPages(Field e, List<Uint8List?> listOfScannedDoc) {
    context.read<GlobalProvider>().setScannedPages(e.id!, listOfScannedDoc);
  }

  _setRemoveScannedPages(Field e, Uint8List? item, List<Uint8List?> listOfScannedDoc){
    context.read<GlobalProvider>().removeScannedPages(e.id!,item,listOfScannedDoc);
  }

  _setValueInMap() {
    context.read<GlobalProvider>().fieldInputValue[widget.field.id!] = doc;
  }

  Future<void> getScannedDocuments(Field e) async {
    try {
      imageBytesList.clear();
      final listOfScannedDoc = await DocumentApi().getScannedPages(e.id!);
      String refNumber = "";
      List<Uint8List?> scannedDoc = List.empty(growable: true);
      for (var element in listOfScannedDoc) {
        setState(() {
          scannedDoc.addAll(element!.doc);
          refNumber = element.referenceNumber;
        });
      }
      _setScannedPages(e, scannedDoc);
      setState(() {
        imageBytesList.addAll(scannedDoc);
        doc.listofImages = imageBytesList;
        doc.referenceNumber = refNumber;
        referenceNumber = refNumber;
      });
      if (doc.title.isNotEmpty) {
        _setValueInMap();
      }
    } catch (e) {
      debugPrint("Error while getting scanned pages $e");
    }
  }

  _documentScanClickedAudit() async {
    await context.read<GlobalProvider>().getAudit("REG-EVT-004", "REG-MOD-103");
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
    referenceNumber: "",
  );
  String? selected;
  String referenceNumber = "";

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

  void _deleteImage(Field e, Uint8List? item) async {
    for(int i =0;i<=imageBytesList.length;i++){
      if(imageBytesList[i] == item){
        setState(() {
          imageBytesList.remove(item);
        });
        await _getRemoveDocumentProvider(e, i);
        _setRemoveScannedPages(e, item, imageBytesList);
      }
    }
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
                    Card(
                      elevation: 0,
                      margin: const EdgeInsets.symmetric(
                          vertical: 1, horizontal: 12),
                      child: Padding(
                        padding: const EdgeInsets.symmetric(
                            vertical: 24, horizontal: 16),
                        child: Column(
                          crossAxisAlignment: CrossAxisAlignment.start,
                          children: [
                            CustomLabel(field: Field(label: referenceLang,
                              required: widget
                                  .field.required,
                              requiredOn: widget
                                  .field.requiredOn,
                            )),
                            const SizedBox(
                              height: 10,
                            ),
                            TextFormField(
                              autovalidateMode: AutovalidateMode.onUserInteraction,
                              initialValue: _getDataFromMap("eng"),
                              textCapitalization: TextCapitalization.words,
                              onChanged: (value) {
                                setState(() {
                                  doc.referenceNumber = value;
                                  referenceNumber = value;
                                });
                              },
                              validator: (value) {
                                if (!widget.field.required! && widget.field.requiredOn!.isEmpty) {
                                  if (value == null || value.isEmpty) {
                                    return null;
                                  } else if (!widget.validation.hasMatch(value)) {
                                    return 'Invalid input';
                                  }
                                }
                                if (value == null || value.isEmpty) {
                                  return 'Please enter a value';
                                }
                                if (!widget.validation.hasMatch(value)) {
                                  return 'Invalid input';
                                }
                                return null;
                              },
                              textAlign: TextAlign.left,
                              decoration: InputDecoration(
                                border: OutlineInputBorder(
                                  borderRadius: BorderRadius.circular(8.0),
                                  borderSide: const BorderSide(
                                      color: AppStyle.appGreyShade, width: 1),
                                ),
                                contentPadding: const EdgeInsets.symmetric(
                                    horizontal: 16),
                                hintText: "Enter Reference Number",
                                hintStyle: const TextStyle(
                                    color: AppStyle.appBlackShade3, fontSize: 14),
                              ),
                            ),
                          ],
                        ),
                      ),
                    ),


                    const SizedBox(
                      height: 5,
                    ),
                    Row(
                      children: [
                        Expanded(
                          child: OutlinedButton(
                            onPressed: (referenceNumber == "")
                                ? null
                                :() async {
                              _documentScanClickedAudit();
                              var doc = await Navigator.push(
                                context,
                                MaterialPageRoute(
                                    builder: (context) =>
                                        const Scanner(title: "Scan Document")),
                              );

                              await addDocument(doc, widget.field, referenceNumber);
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
                            height: 110,
                            child: ListView(
                              scrollDirection: Axis.horizontal,
                              children: imageBytesList.map((item) {
                                return Card(
                                  child: Column(
                                    children: [
                                      SizedBox(
                                        height: 70,
                                        width: 100,
                                        child: Image.memory(item!),
                                      ),
                                      const SizedBox(height: 10,),
                                      GestureDetector(
                                        onTap: (){
                                          _deleteImage(widget.field,item);
                                        },
                                        child: Row(
                                          mainAxisAlignment: MainAxisAlignment.center,
                                          children: [
                                            const Icon(Icons.delete_forever_outlined,color: Colors.red,size: 14,),
                                            const SizedBox(width: 5,),
                                            Text(AppLocalizations.of(context)!.delete,style: const TextStyle(fontSize: 13,color: Colors.red)),
                                          ],
                                        ),
                                      )
                                    ],
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
                        Expanded(
                          child: Card(
                            elevation: 0,
                            margin: const EdgeInsets.symmetric(
                                vertical: 1, horizontal: 12),
                            child: Padding(
                              padding: const EdgeInsets.symmetric(
                                  vertical: 24, horizontal: 16),
                              child: Column(
                                crossAxisAlignment: CrossAxisAlignment.start,
                                children: [
                                  CustomLabel(field: Field(label: referenceLang,
                                    required: widget
                                        .field.required,
                                    requiredOn: widget
                                        .field.requiredOn,
                                  )),
                                  const SizedBox(
                                    height: 10,
                                  ),
                                  TextFormField(
                                    autovalidateMode: AutovalidateMode.onUserInteraction,
                                    initialValue: _getDataFromMap("eng"),
                                    textCapitalization: TextCapitalization.words,
                                    onChanged: (value) {
                                      doc.referenceNumber = value;
                                      referenceNumber = value;
                                    },
                                    validator: (value) {
                                      if (!widget.field.required! && widget.field.requiredOn!.isEmpty) {
                                        if (value == null || value.isEmpty) {
                                          return null;
                                        } else if (!widget.validation.hasMatch(value)) {
                                          return 'Invalid input';
                                        }
                                      }
                                      if (value == null || value.isEmpty) {
                                        return 'Please enter a value';
                                      }
                                      if (!widget.validation.hasMatch(value)) {
                                        return 'Invalid input';
                                      }
                                      return null;
                                    },
                                    textAlign: TextAlign.left,
                                    decoration: InputDecoration(
                                      border: OutlineInputBorder(
                                        borderRadius: BorderRadius.circular(8.0),
                                        borderSide: const BorderSide(
                                            color: AppStyle.appGreyShade, width: 1),
                                      ),
                                      contentPadding: const EdgeInsets.symmetric(
                                          horizontal: 16),
                                      hintText: "Enter Reference Number",
                                      hintStyle: const TextStyle(
                                          color: AppStyle.appBlackShade3, fontSize: 14),
                                    ),
                                  ),
                                ],
                              ),
                            ),
                          ),
                        ),
                        SizedBox(
                          width: 300,
                          child: Row(
                            children: [
                              Expanded(
                                child: OutlinedButton(
                                  onPressed: (referenceNumber == "")
                                      ? null
                                      :() async {
                                    var doc = await Navigator.push(
                                      context,
                                      MaterialPageRoute(
                                          builder: (context) => const Scanner(
                                              title: "Scan Document")),
                                    );

                                    await addDocument(doc, widget.field, referenceNumber);

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
                            height: 110,
                            child: ListView(
                              scrollDirection: Axis.horizontal,
                              children: imageBytesList.map((item) {
                                return Card(
                                  child: Column(
                                    children: [
                                      SizedBox(
                                        height: 70,
                                        width: 100,
                                        child: Image.memory(item!),
                                      ),
                                      const SizedBox(height: 10,),
                                      GestureDetector(
                                        onTap: (){
                                          _deleteImage(widget.field,item);
                                        },
                                        child: Row(
                                          mainAxisAlignment: MainAxisAlignment.center,
                                          children: [
                                            const Icon(Icons.delete_forever_outlined,color: Colors.red,size: 14,),
                                            const SizedBox(width: 5,),
                                            Text(AppLocalizations.of(context)!.delete,style: const TextStyle(fontSize: 13,color: Colors.red)),
                                          ],
                                        ),
                                      )
                                    ],
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
