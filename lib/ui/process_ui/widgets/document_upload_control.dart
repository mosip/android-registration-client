
import 'dart:io';

import 'package:flutter/cupertino.dart';
import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';

import 'package:provider/provider.dart';
import 'package:registration_client/model/upload_document_data.dart';
import 'package:registration_client/pigeon/document_pigeon.dart';
import 'package:registration_client/provider/registration_task_provider.dart';
import 'package:registration_client/ui/scanner/scanner.dart';
import 'package:flutter_gen/gen_l10n/app_localizations.dart';
import 'package:registration_client/ui/scanner/scanner_page.dart';

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

  FixedExtentScrollController scrollController = FixedExtentScrollController();
  @override
  void initState() {
    //load from the map
    if(mounted) {
      getScannedDocuments(widget.field);
    }

    if (context
        .read<GlobalProvider>()
        .fieldInputValue
        .containsKey(widget.field.id ?? "")) {
      selected = context
          .read<GlobalProvider>()
          .fieldInputValue[widget.field.id]
          .title!;
      doc.title = context
          .read<GlobalProvider>()
          .fieldInputValue[widget.field.id]
          .title;
    }

    super.initState();
  }


  void focusNextField(FocusNode currentFocus, FocusNode nextFocus) {
    currentFocus.unfocus();
    FocusScope.of(context).requestFocus(nextFocus);
  }

  List<String> poaList = [];
  List<Uint8List?> imageBytesList = List.empty(growable: true); // list of image bytes

  _getAddDocumentProvider(Field e, Uint8List myBytes) {
    context
        .read<RegistrationTaskProvider>()
        .addDocument(e.id!, selected!, "reference", myBytes);
  }

  Future<void> addDocument(var item, Field e) async {
    final bytes = await getImageBytes(item);

    debugPrint("The selected value for dropdown for ${e.id!} is $selected");
    Uint8List myBytes = Uint8List.fromList(bytes);
    // context
    //     .read<RegistrationTaskProvider>()
    //     .addDocument(e.id!, selected!, "reference", myBytes);
    _getAddDocumentProvider(e, myBytes);
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

  _removeFieldValue(Field e, Uint8List? item) async{
    context.read<GlobalProvider>().removeValidFromMap(
        e.id!,item,context.read<GlobalProvider>().fieldInputValue);
  }

  _setValueInMap() {
    context.read<GlobalProvider>().fieldInputValue[widget.field.id!] = doc;
  }

  Future<void> getScannedDocuments(Field e) async {
    try {
      imageBytesList.clear();
      final listOfScannedDoc = await DocumentApi().getScannedPages(e.id!);
      _setScannedPages(e, listOfScannedDoc);
      setState(() {
        imageBytesList.addAll(listOfScannedDoc);
        doc.listofImages = imageBytesList;
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
  );
  String? selected;
  final TextEditingController textController = TextEditingController(text:"");

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
    for(int i =0;i<imageBytesList.length;i++){
      if(imageBytesList[i] == item){
        setState(() {
          imageBytesList.removeAt(i);
        });
        await _getRemoveDocumentProvider(e, i);
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

                    const SizedBox(
                      height: 10,
                    ),
                    Row(
                      children: [
                        Expanded(
                          child: OutlinedButton(
                            onPressed: (selected == null)
                              ? null :() async {
                              _documentScanClickedAudit();
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
                                          _removeFieldValue(widget.field,item);
                                          _setRemoveScannedPages(widget.field, item,imageBytesList);
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
                                              ? TextFormField(
                                            readOnly: true,
                                            autovalidateMode: AutovalidateMode.onUserInteraction,
                                            controller: textController,
                                            onTap: (){
                                              _showDropdownBottomSheet(snapshot,widget.field.label!["eng"],context);
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
                                                    color: Colors.grey, width: 1),
                                              ),
                                              contentPadding: const EdgeInsets.symmetric(
                                                  vertical: 14, horizontal: 16),
                                              hintText: "Select Value",
                                              hintStyle: const TextStyle(
                                                  color: Colors.grey, fontSize: 14),
                                              suffixIcon: const Icon(Icons.keyboard_arrow_down,color: Colors.grey),
                                            ),
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
                                child: Padding(
                                  padding: const EdgeInsets.only(top: 16),
                                  child: ElevatedButton(
                                    style: ElevatedButton.styleFrom(
                                      minimumSize: const Size(100, 50),
                                    ),
                                    onPressed: (selected == null)
                                        ? null :() async {
                                      var doc = await Navigator.push(
                                        context,
                                        MaterialPageRoute(
                                            builder: (context) => const ScannerPage(
                                                title: "Scan Document")),
                                      );
                                      await addDocument(doc, widget.field);

                                      await getScannedDocuments(widget.field);
                                    },
                                    child: const Row(
                                      mainAxisAlignment: MainAxisAlignment.center,
                                      children: [
                                        Icon(Icons.crop_free_sharp,color: Colors.white,size: 14,),
                                        SizedBox(width: 5,),
                                        Text(
                                          "SCAN",
                                          style: TextStyle(fontSize: 16,color: Colors.white),
                                        ),
                                      ],
                                    ),
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
                                          _removeFieldValue(widget.field,item);
                                          _setRemoveScannedPages(widget.field, item,imageBytesList);
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

  void _showDropdownBottomSheet(AsyncSnapshot? snapshot, String? title, BuildContext context) {
    showModalBottomSheet(
      isDismissible: false,
      context: context,
      shape: const RoundedRectangleBorder(
        borderRadius: BorderRadius.only(
          topRight: Radius.circular(15),
          topLeft: Radius.circular(15),
        ),
      ),
      builder: (BuildContext context) {
        return  Container(
          width: MediaQuery.of(context).size.width,
          height: double.maxFinite,
          decoration: const BoxDecoration(
            color: Colors.white,
            borderRadius: BorderRadius.only(
              topRight: Radius.circular(15),
              topLeft: Radius.circular(15),
            )
          ),
          padding: const EdgeInsets.all(16.0),
          child:  Column(
            children: [
               Row(
                mainAxisAlignment: MainAxisAlignment.spaceBetween,
                children: [
                   const SizedBox.shrink(),
                   Padding(
                     padding: const EdgeInsets.all(20.0),
                     child: Text(
                      "Select $title",
                      textAlign: TextAlign.center,
                      style: const TextStyle(
                        fontSize: 24.0,
                        fontWeight: FontWeight.w500,
                      ),
                  ),
                   ),
                  InkWell(onTap: (){
                    Navigator.of(context).pop();
                  }, child: const Padding(
                    padding:  EdgeInsets.all(12.0),
                    child:  Icon(Icons.clear,color: Colors.black,size: 35,),
                  ))
                ],
              ),
          Expanded(
            child: CupertinoPicker(
              itemExtent: 50,
              children: <Widget>[
                for (var i = 0;
                i < snapshot!.data.length;
                i++)
                  ListTile(
                    title: Center(
                      child: Text(
                        snapshot.data[i],
                        style: TextStyle(fontSize: 20,color: Colors.indigo,fontWeight: FontWeight.w500),
                      ),
                    ),
                    trailing: Icon(Icons.check,size: 35,color: Colors.indigo,)
                  ),
                  // Row(
                  //   mainAxisAlignment: MainAxisAlignment.center,
                  //   children: <Widget>[
                  //     Padding(
                  //       padding: EdgeInsets.only(left: 10),
                  //       child: Text(
                  //         snapshot[i],
                  //         style: TextStyle(fontSize: 20,color: Colors.indigo,fontWeight: FontWeight.w500),
                  //       ),
                  //     ),
                  //     Icon(Icons.check,size: 25,)
                  //   ],
                  // ),
              ],
              onSelectedItemChanged: (int index) {
                print('good boi');
              },
              looping: false,
              backgroundColor: Colors.white,
            ),
          ),

        // ListView.builder(
              //   shrinkWrap: true,
              //   itemCount: snapshot.data.length,
              //   itemBuilder: (BuildContext context, int index) {
              //   return GestureDetector(
              //     onTap: () {
              //       setState(() {
              //         textController.text = snapshot.data[index];
              //       });
              //       Navigator.of(context).pop();
              //     },
              //     child: Row(
              //       mainAxisAlignment: MainAxisAlignment.center,
              //       children: [
              //         Text(snapshot.data[index],style: const TextStyle(fontSize: 20),),
              //         const SizedBox(width: 80,),
              //         const Icon(Icons.check),
              //       ],
              //     ),
              //   );
              // })
            ],
          ),
        );
      },
    );
  }
}
