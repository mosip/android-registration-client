import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:provider/provider.dart';
import 'package:registration_client/model/screen.dart';
import 'package:registration_client/pigeon/dynamic_response_pigeon.dart';
import 'package:registration_client/provider/global_provider.dart';
import 'package:registration_client/provider/registration_task_provider.dart';
import 'package:registration_client/ui/scanner/qr_code_scanner.dart';
import 'package:registration_client/ui/widgets/validator_alert.dart';
import 'package:registration_client/utils/app_config.dart';
import 'package:flutter_gen/gen_l10n/app_localizations.dart';

class PreRegDataControl extends StatefulWidget {
  final Screen screen;
  final VoidCallback onFetched;
  const PreRegDataControl({super.key, required this.screen, required this.onFetched});


  @override
  State<PreRegDataControl> createState() => _PreRegDataControlState();
}

class _PreRegDataControlState extends State<PreRegDataControl> {
  late RegistrationTaskProvider registrationTaskProvider;
  late GlobalProvider globalProvider;
  final TextEditingController preRegIdController =
      TextEditingController(text: "");
  final _formFieldKey = GlobalKey<FormFieldState>();
  int? index;
  @override
  void initState() {
    registrationTaskProvider =
        Provider.of<RegistrationTaskProvider>(context, listen: false);
    globalProvider =
        Provider.of<GlobalProvider>(context,listen: false);
    if(globalProvider.preRegId!=""){
      preRegIdController.text = globalProvider.preRegId;
    }
    super.initState();
  }



  widgetValue(Screen screen,Map<String?, Object?> value) async {

    List<GenericData?> temp = [];
    List<GenericData?> allValue =[];
    if(screen.fields!=null) {
        for (var e in screen.fields!) {
          globalProvider.removeInputMapValue(
              e!.id!, globalProvider.fieldInputValue);
          for(String? key in value.keys) {
            Object? values = value[key];
            if(e.id == key) {
              if (e.type == 'simpleType') {
                List<Object?> data = values as List<Object?>;
                for (var obj in data) {
                  if (obj is Map) {
                    String name = obj['name'] ?? '';
                    String language = obj['language'] ?? '';
                    String value = obj["value"] ?? '';
                    if(e.controlType == "dropdown"){
                      List<String> selectedLang =[];
                      for (var lang in globalProvider.chosenLang) {
                        String langCode = globalProvider.langToCode(lang);
                        selectedLang.add(langCode);
                      }
                      temp = await registrationTaskProvider
                          .getLocationValuesBasedOnParent(
                          value, e.subType!, globalProvider.selectedLanguage,selectedLang);
                    }
                  } else {
                    debugPrint('Unknown type: $obj');
                  }
                }
                allValue.addAll(temp);
              }
            }
          }
        }
      }


    if(screen.fields!=null) {
      for (var e in screen.fields!) {
        for(String? key in value.keys) {
          Object? values = value[key];
          if(e!.id == key) {
            String lang = globalProvider.mandatoryLanguages[0]!;
            if (e.type == 'simpleType') {
              List<Object?> data = values as List<Object?>;
              for (var obj in data) {
                if (obj is Map) {
                  String name = obj['name'] ?? '';
                  String language = obj['language'] ?? '';
                  String value = obj["value"] ?? '';
                  GenericData data = GenericData(name: value, code: value, langCode: language);
                  if(e.controlType == "textbox"){
                    globalProvider.setLanguageSpecificValue(
                      key!,
                      value,
                      language,
                      globalProvider.fieldInputValue,
                    );
                  }
                  if(e.controlType == "dropdown"){
                    setState(() {
                      index = globalProvider.hierarchyReverse.indexOf(e.subType!);
                    });
                    if(e.id == "gender"){
                      globalProvider.setLanguageSpecificValue(
                        key!,
                        value,
                        language,
                        globalProvider.fieldInputValue,
                      );
                    }
                    if(e.fieldType == "dynamic"){
                      globalProvider.setLanguageSpecificValue(
                        key!,
                        value,
                        language,
                        globalProvider.fieldInputValue,
                      );
                    }
                    if(e.id != "gender" && e.fieldType != "dynamic") {
                      temp = await registrationTaskProvider.getLocationValues("$index", globalProvider.selectedLanguage);
                      for(var subData in temp) {
                        GenericData dataSubValue = GenericData(name: subData!.name, code: subData.code, langCode: lang);
                        if (language == lang) {
                          if (data.code == subData.code) {
                            globalProvider.setLanguageSpecificValue(
                              "${e.group}${e.subType}",
                              dataSubValue,
                              lang,
                              globalProvider.fieldInputValue,
                            );
                            globalProvider.setLocationHierarchy(
                                e.group!, subData.code, index!);
                          }
                        }
                      }
                    }
                    for(var parentData in allValue){
                          String dynamicName = parentData!.name ?? '';
                          String dynamicValue = parentData.code ?? '';
                          GenericData dataValue = GenericData(name: dynamicName, code: dynamicValue, langCode: lang);
                          if(dynamicValue == value){
                            globalProvider.setLocationHierarchy(
                                e.group!, dynamicValue, index!);
                            globalProvider.setLanguageSpecificValue(
                              "${e.group}${e.subType}",
                              dataValue,
                              lang,
                              globalProvider.fieldInputValue,
                            );
                          }
                    }
                  }

                } else {
                  debugPrint('Unknown type: $obj');
                }
              }
            } else {
              globalProvider.setInputMapValue(
                key!,
                values!,
                globalProvider.fieldInputValue,
              );
              if(e.id != "gender" && e.fieldType != "dynamic"){
                GenericData result = GenericData(name: values.toString(), code: values.toString(), langCode: lang);
                globalProvider.setInputMapValue(
                  "${e.group}${e.subType}",
                  result,
                  globalProvider.fieldInputValue,
                );
              }
            }
          }
        }
      }
    }
    globalProvider.preRegControllerRefresh = false;
  }


  @override
  Widget build(BuildContext context) {
    bool isPortrait =
        MediaQuery.of(context).orientation == Orientation.portrait;
    return Card(
      elevation: 5,
      color: pureWhite,
      shape: RoundedRectangleBorder(
        borderRadius: BorderRadius.circular(6.0),
      ),
      margin: EdgeInsets.symmetric(
          vertical: 20.h, horizontal: isPortrait ? 16.w : 0),
      child: Padding(
        padding: EdgeInsets.symmetric(vertical: 24.h, horizontal: 16.w),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text(
              "Application ID",
              style: TextStyle(
                  fontSize: isPortrait && !isMobileSize ? 18 : 14),
            ),
            SizedBox(
              height: 10.h,
            ),
            Row(
              children: [
                Expanded(
                  flex: 3,
                  child: TextFormField(
                    key: _formFieldKey,
                    autovalidateMode: AutovalidateMode.onUserInteraction,
                    textCapitalization: TextCapitalization.words,
                    controller: preRegIdController,
                    onChanged: (value) {
                      //preRegIdController.text = value;
                      globalProvider.setPreRegistrationId(value);
                    },
                    textAlign: TextAlign.left,
                    decoration: InputDecoration(
                      border: OutlineInputBorder(
                        borderRadius: BorderRadius.circular(8.0),
                        borderSide:
                            const BorderSide(color: appGreyShade, width: 1),
                      ),
                      contentPadding: const EdgeInsets.symmetric(horizontal: 16,vertical: 14),
                      hintText: "Enter Application ID",
                      hintStyle:
                          const TextStyle(color: appBlackShade3, fontSize: 14),
                    ),
                  ),
                ),
                Expanded(
                  flex: 3,
                  child: Padding(
                    padding: EdgeInsets.symmetric(horizontal: 20.w),
                    child: OutlinedButton(
                      style: OutlinedButton.styleFrom(
                        fixedSize: const Size(100, 50),
                        elevation: 0,
                        backgroundColor: Colors.white,
                        side: BorderSide(width: 1.0, color: solidPrimary),
                        shape: const RoundedRectangleBorder(
                          borderRadius: BorderRadius.all(Radius.circular(2)),
                        ),
                      ),
                      onPressed: () async {
                        widget.onFetched();
                        if(preRegIdController.text.isEmpty){
                          globalProvider.preRegControllerRefresh = true;
                          showDialog(
                            context: context,
                            builder: (BuildContext context) => ValidatorAlert(errorMessage: AppLocalizations.of(context)!.enter_application_id),
                          );
                          globalProvider.preRegControllerRefresh = false;
                        } else if(!RegExp(r'^\d{14}$').hasMatch(preRegIdController.text)){
                          globalProvider.preRegControllerRefresh = true;
                          showDialog(
                            context: context,
                            builder: (BuildContext context) => ValidatorAlert(errorMessage: AppLocalizations.of(context)!.application_id_not_exist,subError: AppLocalizations.of(context)!.correct_application_id),
                          );
                          globalProvider.preRegControllerRefresh = false;
                        } else {
                          globalProvider.preRegControllerRefresh = true;
                            Map<String?, Object?> value = await context.read<
                                RegistrationTaskProvider>()
                                .fetchPreRegistrationDetail(
                                preRegIdController.text);
                            if (value.isNotEmpty) {
                              widgetValue(widget.screen, value);
                            } else {
                              globalProvider.preRegControllerRefresh = false;
                            }
                        }
                      },
                      child: Text(
                        AppLocalizations.of(context)!.fetch_data,
                        style: TextStyle(fontSize: isPortrait && !isMobileSize ? 22 : 14, color: solidPrimary,fontWeight: FontWeight.bold),
                      ),
                    ),
                  ),
                ),
                Expanded(
                  flex: 1,
                  child: OutlinedButton(
                    style: OutlinedButton.styleFrom(
                      fixedSize: const Size(50, 50),
                      elevation: 0,
                      backgroundColor: Colors.white,
                      side: BorderSide(width: 1.0, color: solidPrimary),
                      shape: const RoundedRectangleBorder(
                        borderRadius: BorderRadius.all(Radius.circular(2)),
                      ),
                    ),
                    onPressed: () async {
                      widget.onFetched();
                      var data = await Navigator.push(
                        context,
                        MaterialPageRoute(
                            builder: (context) =>
                                QRCodeScannerApp()),
                      );
                      if(data!=null && !RegExp(r'^\d{14}$').hasMatch(data)){
                        globalProvider.preRegControllerRefresh = true;
                        showDialog(
                          context: context,
                          builder: (BuildContext context) =>  ValidatorAlert(errorMessage: AppLocalizations.of(context)!.application_id_not_exist,subError: AppLocalizations.of(context)!.correct_application_id),
                        );
                        globalProvider.preRegControllerRefresh = false;
                      } else if(data!=null) {
                        globalProvider.preRegControllerRefresh = true;
                        setState(() {
                          preRegIdController.text = data.toString();
                        });
                        globalProvider.setPreRegistrationId(data);
                        Map<String?, Object?> value = await context.read<
                            RegistrationTaskProvider>()
                            .fetchPreRegistrationDetail(
                            preRegIdController.text);
                        if (value.isNotEmpty) {
                          widgetValue(widget.screen, value);
                        } else {
                          globalProvider.preRegControllerRefresh = false;
                        }
                      }
                    },
                    child: Icon(Icons.crop_free,size: 32.6,color: solidPrimary),
                  ),
                ),
                const Spacer(),
              ],
            )
          ],
        ),
      ),
    );
  }
}
