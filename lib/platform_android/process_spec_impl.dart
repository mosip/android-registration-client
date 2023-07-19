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
}

ProcessSpec getProcessSpecImpl() => ProcessSpecImpl();