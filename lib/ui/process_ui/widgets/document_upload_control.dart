
import 'dart:io';

import 'package:flutter/cupertino.dart';
import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';

import 'package:provider/provider.dart';
import 'package:registration_client/model/upload_document_data.dart';
import 'package:registration_client/pigeon/document_pigeon.dart';
import 'package:registration_client/provider/registration_task_provider.dart';
import 'package:registration_client/ui/scanner/custom_scanner.dart';
import 'package:flutter_gen/gen_l10n/app_localizations.dart';
import 'package:registration_client/ui/scanner/preview_screen.dart';

import '../../../model/field.dart';
import '../../../provider/global_provider.dart';
import '../../../utils/app_style.dart';
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
      documentController.text = context
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
        .addDocument(e.id!, documentController.text, referenceNumber, myBytes);
  }

  Future<void> addDocument(var item, Field e,String referenceNumber) async {
   // final bytes = await getImageBytes(item);

    debugPrint("The selected value for dropdown for ${e.id!} is ${documentController.text}");
   // Uint8List myBytes = Uint8List.fromList(bytes);
    // context
    //     .read<RegistrationTaskProvider>()
    //     .addDocument(e.id!, selected!, "reference", myBytes);
    _getAddDocumentProvider(e, item, referenceNumber);
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
  final TextEditingController documentController = TextEditingController(text:"");

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
      padding: EdgeInsets.symmetric(vertical: 10.h),
      child: Card(
        elevation: 3,
        margin: EdgeInsets.symmetric(vertical: 1.h, horizontal: 10.w),
        child: Padding(
          padding: EdgeInsets.symmetric(vertical: 24.h, horizontal: 14.w),
          child: isMobile
              ? Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    FutureBuilder(
                        future: _getDocumentValues(
                            widget.field.subType!, "eng", null),
                        builder: (BuildContext context,
                            AsyncSnapshot<List<String?>> snapshot) {
                          return Card(
                            elevation: 0,
                            margin: EdgeInsets.symmetric(horizontal: 12.w),
                            child: Padding(
                              padding: EdgeInsets.symmetric(
                                  vertical: 16.h, horizontal: 16.w),
                              child: Column(
                                crossAxisAlignment:
                                CrossAxisAlignment.start,
                                children: [
                                  CustomLabel(field: widget.field),
                                  SizedBox(
                                    height: 10.h,
                                  ),
                                  snapshot.hasData
                                      ? TextFormField(
                                    readOnly: true,
                                    autovalidateMode: AutovalidateMode.onUserInteraction,
                                    controller: documentController,
                                    onTap: (){
                                      _showDropdownBottomSheet(snapshot,widget.field,context);
                                    },
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
                                        return AppLocalizations.of(context)!.select_value_message;
                                      }
                                      if (!widget.validation
                                          .hasMatch(value!)) {
                                        return AppLocalizations.of(context)!.invalid_input;
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
                                      contentPadding: EdgeInsets.symmetric(
                                          vertical: 14.h, horizontal: 16.w),
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
                        }),
                    Card(
                      elevation: 0,
                      margin: EdgeInsets.symmetric(horizontal: 12.w),
                      child: Padding(
                        padding: EdgeInsets.symmetric(
                            vertical: 16.h, horizontal: 16.w),
                        child: Column(
                          crossAxisAlignment: CrossAxisAlignment.start,
                          children: [
                            CustomLabel(field: Field(label: referenceLang,
                              required: widget
                                  .field.required,
                              requiredOn: widget
                                  .field.requiredOn,
                            )),
                             SizedBox(
                              height: 10.h,
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
                                    return AppLocalizations.of(context)!.invalid_input;
                                  }
                                }
                                if (value == null || value.isEmpty) {
                                  return AppLocalizations.of(context)!.reference_number_validation;
                                }
                                if (!widget.validation.hasMatch(value)) {
                                  return AppLocalizations.of(context)!.invalid_input;
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
                                contentPadding: EdgeInsets.symmetric(
                                    horizontal: 16.w),
                                hintText: AppLocalizations.of(context)!.reference_number,
                                hintStyle: const TextStyle(
                                    color: AppStyle.appBlackShade3, fontSize: 14),
                              ),
                            ),
                          ],
                        ),
                      ),
                    ),
                    SizedBox(
                      height: 10.h,
                    ),
                    SizedBox(
                      width: 300.w,
                      child: Row(
                        mainAxisAlignment: MainAxisAlignment.center,
                        children: [
                          Expanded(
                            child: Padding(
                              padding: EdgeInsets.symmetric(horizontal:24.w),
                              child: ElevatedButton(
                                style: ElevatedButton.styleFrom(
                                  minimumSize: Size(100.w, 50.h),
                                ),
                                onPressed: (documentController.text == "" || referenceNumber == "")
                                  ? null :() async {
                                  _documentScanClickedAudit();
                                  var doc = await Navigator.push(
                                    context,
                                    MaterialPageRoute(
                                        builder: (context) =>
                                            CustomScanner(
                                                field: widget.field)),
                                  );

                                  await addDocument(doc, widget.field, referenceNumber);
                                  await getScannedDocuments(widget.field);
                                },
                                child: Row(
                                  mainAxisAlignment: MainAxisAlignment.center,
                                  children: [
                                    const Icon(Icons.crop_free_sharp,color: Colors.white,size: 14,),
                                    SizedBox(width: 5.w),
                                    Text(
                                      AppLocalizations.of(context)!.scan,
                                      style: const TextStyle(fontSize: 16,color: Colors.white),
                                    ),
                                  ],
                                ),
                              ),
                            ),
                          ),
                        ],
                      ),
                    ),
                    SizedBox(
                      height: 10.h,
                    ),
                    imageBytesList.isNotEmpty
                        ? SizedBox(
                            height: 110.h,
                            child: ListView(
                              scrollDirection: Axis.horizontal,
                              children: imageBytesList.map((item) {
                                return Card(
                                  child: Column(
                                    children: [
                                      InkWell(
                                        onTap : () {
                                          Navigator.push(context,
                                            MaterialPageRoute(
                                                builder: (context) => PreviewScreen(bytes: item)),
                                          );
                                        },
                                        child: SizedBox(
                                          height: 70.h,
                                          width: 100.w,
                                          child: Image.memory(item!),
                                        ),
                                      ),
                                      SizedBox(height: 10.h),
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
                                            SizedBox(width: 5.w),
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
                                    margin: EdgeInsets.symmetric(
                                        vertical: 1.h, horizontal: 5.w),
                                    child: Padding(
                                      padding: EdgeInsets.symmetric(
                                          vertical: 24.h),
                                      child: Column(
                                        crossAxisAlignment:
                                        CrossAxisAlignment.start,
                                        children: [
                                          CustomLabel(field: widget.field),
                                          SizedBox(
                                            height: 10.h,
                                          ),
                                          snapshot.hasData
                                              ? TextFormField(
                                            readOnly: true,
                                            autovalidateMode: AutovalidateMode.onUserInteraction,
                                            controller: documentController,
                                            onTap: (){
                                              _showDropdownBottomSheet(snapshot,widget.field,context);
                                            },
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
                                                return AppLocalizations.of(context)!.select_value_message;
                                              }
                                              if (!widget.validation
                                                  .hasMatch(value!)) {
                                                return AppLocalizations.of(context)!.invalid_input;
                                              }
                                              return null;
                                            },
                                            textAlign: TextAlign.left,
                                            decoration: InputDecoration(
                                              border: OutlineInputBorder(
                                                borderRadius: BorderRadius.circular(8.0),
                                                borderSide: BorderSide(
                                                    color: Colors.grey, width: 1.w),
                                              ),
                                              contentPadding: EdgeInsets.symmetric(
                                                  vertical: 14.h, horizontal: 16.w),
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
                        Expanded(
                          child: Card(
                            elevation: 0,
                            margin: EdgeInsets.symmetric(
                                vertical: 1.h, horizontal: 5.w),
                            child: Padding(
                              padding: EdgeInsets.symmetric(
                                  vertical: 24.h),
                              child: Column(
                                crossAxisAlignment: CrossAxisAlignment.start,
                                children: [
                                  CustomLabel(field: Field(label: referenceLang,
                                    required: widget
                                        .field.required,
                                    requiredOn: widget
                                        .field.requiredOn,
                                  )),
                                  SizedBox(
                                    height: 10.h,
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
                                          return AppLocalizations.of(context)!.invalid_input;
                                        }
                                      }
                                      if (value == null || value.isEmpty) {
                                        return AppLocalizations.of(context)!.reference_number_validation;
                                      }
                                      if (!widget.validation.hasMatch(value)) {
                                        return AppLocalizations.of(context)!.invalid_input;
                                      }
                                      return null;
                                    },
                                    textAlign: TextAlign.left,
                                    decoration: InputDecoration(
                                      border: OutlineInputBorder(
                                        borderRadius: BorderRadius.circular(8.0),
                                        borderSide: BorderSide(
                                            color: AppStyle.appGreyShade, width: 1.w),
                                      ),
                                      contentPadding: EdgeInsets.symmetric(
                                          vertical: 14.h, horizontal: 16.w),
                                      hintText: AppLocalizations.of(context)!.reference_number,
                                      hintStyle: const TextStyle(
                                          color: AppStyle.appBlackShade3, fontSize: 14),
                                    ),
                                  ),
                                ],
                              ),
                            ),
                          ),
                        ),
                        Expanded(
                          child: Padding(
                            padding: EdgeInsets.only(top: 20.h,left: 10.w),
                            child: ElevatedButton(
                              style: ElevatedButton.styleFrom(
                                minimumSize: Size(100.w, 46.h),
                              ),
                              onPressed: (documentController.text == "" || referenceNumber == "")
                                  ? null :() async {
                                var doc = await Navigator.push(
                                  context,
                                  MaterialPageRoute(
                                      builder: (context) =>  CustomScanner(
                                         field: widget.field)),
                                );
                                await addDocument(doc, widget.field, referenceNumber);

                                await getScannedDocuments(widget.field);
                              },
                              child: Row(
                                mainAxisAlignment: MainAxisAlignment.center,
                                children: [
                                  const Icon(Icons.crop_free_sharp,color: Colors.white,size: 14,),
                                  SizedBox(width: 5.w),
                                  Text(
                                    AppLocalizations.of(context)!.scan,
                                    style: const TextStyle(fontSize: 16,color: Colors.white),
                                  ),
                                ],
                              ),
                            ),
                          ),
                        ),
                      ],
                    ),
                    SizedBox(
                      height: 10.h,
                    ),
                    imageBytesList.isNotEmpty
                        ? SizedBox(
                            height: 110.h,
                            child: ListView(
                              scrollDirection: Axis.horizontal,
                              children: imageBytesList.map((item) {
                                return Card(
                                  child: Column(
                                    children: [
                                      InkWell(
                                        onTap : () {
                                          Navigator.push(context,
                                            MaterialPageRoute(
                                                builder: (context) => PreviewScreen(bytes: item)),
                                          );
                                        },
                                        child: SizedBox(
                                          height: 70.h,
                                          width: 100.w,
                                          child: Image.memory(item!),
                                        ),
                                      ),
                                      SizedBox(height: 10.h),
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
                                            SizedBox(width: 5.w),
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

  void _showDropdownBottomSheet(AsyncSnapshot? snapshot, Field field, BuildContext context) {
    setState(() {
      documentController.text = snapshot!.data[0];
      doc.title = snapshot.data[0];
    });
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
          height: snapshot!.data.length<=1 ? 200.h:350.h,
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
                  InkWell(onTap: (){
                    setState(() {
                      documentController.clear();
                      doc.title = "";
                    });
                    Navigator.of(context).pop();
                  }, child: Padding(
                    padding: const EdgeInsets.all(12.0),
                    child:  Text(AppLocalizations.of(context)!.clear,style: const TextStyle(color: AppStyle.appRed,fontSize: 22,fontWeight: FontWeight.w500)),
                  )),
                   Expanded(
                     child: Padding(
                       padding: const EdgeInsets.all(20.0),
                       child: Text(
                         context.read<GlobalProvider>().chooseLanguage(field.label!),
                        textAlign: TextAlign.center,
                        overflow: TextOverflow.ellipsis,
                        style: const TextStyle(
                          fontSize: 24.0,
                          fontWeight: FontWeight.w500,
                        ),
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
              const Divider(color: AppStyle.dropDownDividerColor,thickness: 1,),
          Expanded(
            flex:3,
            child: CupertinoPicker(
              itemExtent: 50,
              scrollController: FixedExtentScrollController(
                initialItem: 0,
              ),
              onSelectedItemChanged: (int index) {
                saveData(snapshot.data[index]);
                setState(() {
                  documentController.text = snapshot.data[index];
                  doc.title = snapshot.data[index];
                });
              },
              looping: false,
              backgroundColor: Colors.white,
              children: <Widget>[
                for (var i = 0; i < snapshot.data.length; i++)...[
                  ListTile(
                    title: Center(
                      child: Text(
                        snapshot.data[i],
                        style: const TextStyle(fontSize: 22,color: AppStyle.dropDownSelector,fontWeight: FontWeight.w500),
                      ),
                    ),
                    trailing: Icon(Icons.check,size: 30,color: (snapshot.data[i] == documentController.text)? AppStyle.dropDownSelector:Colors.white,)
                  ),
                  ],
              ],
            ),
          ),
            ],
          ),
        );
      },
    );
  }
}
