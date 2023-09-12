import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:registration_client/pigeon/process_spec_pigeon.dart';
import 'package:registration_client/platform_spi/process_spec.dart';

class ProcessSpecImpl implements ProcessSpec {
  @override
  Future<List<String?>> getNewProcessSpec() async {
    List<String?> listOfProcesses;
    try {
      listOfProcesses = await ProcessSpecApi().getNewProcessSpec();
    } on PlatformException {
      debugPrint("Process Spec Api failed!");
      listOfProcesses = List.empty();
    }  catch (e) {
      listOfProcesses = List.empty();
      debugPrint("Process spec fetch error: $e");
    }
    
    return listOfProcesses;
  }

  @override
  Future<String> getStringValueGlobalParam(String key) async {
    String result;
    try {
      result = await ProcessSpecApi().getStringValueGlobalParam(key);
    } on PlatformException catch (e) {
      debugPrint("Some Error Occurred: $e");
      result = "REG_GLOBAL_PARAM_ERROR";
    }
    return result;
  }

  @override
  Future<String> getUISchema() async {
    String result;
    try {
      result = await ProcessSpecApi().getUISchema();
    } on PlatformException catch (e) {
      debugPrint("Some Error Occurred: $e");
      result = "REG_UI_SCHEMA_ERROR";
    }
    return result;
  }
  
  @override
  Future<List<String?>> getMandatoryLanguageCodes() async {
    List<String?> mandatoryLanguageCodes;
    try {
      mandatoryLanguageCodes = await ProcessSpecApi().getMandatoryLanguageCodes();
    } on PlatformException {
      debugPrint("Process Spec Api failed!");
      mandatoryLanguageCodes = List.empty();
    }  catch (e) {
      mandatoryLanguageCodes = List.empty();
      debugPrint("Process spec fetch error: $e");
    }
    return mandatoryLanguageCodes;
  }
  
  @override
  Future<int> getMaxLanguageCount() async {
    int maxLangCount = 0;
    try {
      maxLangCount = await ProcessSpecApi().getMaxLanguageCount();
    } on PlatformException {
      debugPrint("Process Spec Api failed!");
    }  catch (e) {
      debugPrint("Process spec fetch error: $e");
    }
    return maxLangCount;
  }
  
  @override
  Future<int> getMinLanguageCount() async {
    int minLangCount = 0;
    try {
      minLangCount = await ProcessSpecApi().getMinLanguageCount();
    } on PlatformException {
      debugPrint("Process Spec Api failed!");
    }  catch (e) {
      debugPrint("Process spec fetch error: $e");
    }
    return minLangCount;
  }
  
  @override
  Future<List<String?>> getOptionalLanguageCodes() async {
    List<String?> optionalLanguageCodes;
    try {
      optionalLanguageCodes = await ProcessSpecApi().getOptionalLanguageCodes();
    } on PlatformException {
      debugPrint("Process Spec Api failed!");
      optionalLanguageCodes = List.empty();
    }  catch (e) {
      optionalLanguageCodes = List.empty();
      debugPrint("Process spec fetch error: $e");
    }
    return optionalLanguageCodes;
  }
}

ProcessSpec getProcessSpecImpl() => ProcessSpecImpl();