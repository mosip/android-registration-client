import 'package:flutter/material.dart';
import 'package:registration_client/pigeon/registration_data_pigeon.dart';
import 'package:registration_client/platform_spi/process_spec.dart';
import 'package:registration_client/platform_spi/registration.dart';

class RegistrationTaskProvider with ChangeNotifier {
  final Registration registration = Registration();
  final ProcessSpec processSpec = ProcessSpec();
  List<Object?> _listOfProcesses = List.empty(growable: true);
  String _stringValueGlobalParam = "";
  String _uiSchema = "";
  bool _isRegistered = false;
  bool _isMvelValid = false;
  String _previewTemplate = "";
  
  List<Object?> get listOfProcesses => this._listOfProcesses;
  String get stringValueGlobalParam => _stringValueGlobalParam;
  String get uiSchema => _uiSchema;
  bool get isRegistered => _isRegistered;
  String get previewTemplate => _previewTemplate;
  bool get isMvelValid => _isMvelValid;

  set listOfProcesses(List<Object?> value) {
    this._listOfProcesses = value;
    notifyListeners();
  }

  setStringValueGlobalParam(String value) {
    _stringValueGlobalParam = value;
    notifyListeners();
  }

  setUISchema(String value) {
    _uiSchema = value;
    notifyListeners();
  }
  
  getStringValueGlobalParam(String key) async {
    String result = await processSpec.getStringValueGlobalParam(key);
    if(result == "REG_GLOBAL_PARAM_ERROR") {
      _stringValueGlobalParam = "";
    } else {
      _stringValueGlobalParam = result;
    }
    notifyListeners();
  }

  getUISchema() async {
    String result = await processSpec.getUISchema();
    if(result == "REG_UI_SCHEMA_ERROR") {
      _uiSchema = "";
    } else {
      _uiSchema = result;
    }
    notifyListeners();
  }

  setListOfProcesses(List<String?> value) {
    _listOfProcesses = value;
    notifyListeners();
  }

  getListOfProcesses() async {
    List<String?> result = await processSpec.getNewProcessSpec();
    if(result.isEmpty) {
      _listOfProcesses = [];
    } else {
      _listOfProcesses = result;
    }
    notifyListeners();
  }

  registerApplicant(RegistrationData registrationData) async {
    _isRegistered = await registration.registerApplicant(registrationData);
    notifyListeners();
  }

  checkMVEL(String data, String expression) async {
    _isMvelValid = await registration.checkMVEL(data, expression);
    notifyListeners();
  }

  getPreviewTemplate(String data, bool isPreview) async {
    _previewTemplate = await registration.getPreviewTemplate(data, isPreview);
    notifyListeners();
  }
}