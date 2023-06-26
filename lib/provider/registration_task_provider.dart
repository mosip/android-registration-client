import 'package:flutter/material.dart';
import 'package:registration_client/platform_android/process_spec_impl.dart';

class RegistrationTaskProvider with ChangeNotifier {
  List<Object?> _listOfProcesses = List.empty(growable: true);
  String _stringValueGlobalParam = "";
  String _uiSchema = "";
  
  List<Object?> get listOfProcesses => this._listOfProcesses;
  String get stringValueGlobalParam => _stringValueGlobalParam;
  String get uiSchema => _uiSchema;

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
    String result = await ProcessSpecImpl().getStringValueGlobalParam(key);
    if(result == "REG_GLOBAL_PARAM_ERROR") {
      _stringValueGlobalParam = "";
    } else {
      _stringValueGlobalParam = result;
    }
    notifyListeners();
  }

  getUISchema() async {
    String result = await ProcessSpecImpl().getUISchema();
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
    List<String?> result = await ProcessSpecImpl().getNewProcessSpec();
    if(result.isEmpty) {
      _listOfProcesses = [];
    } else {
      _listOfProcesses = result;
    }
    notifyListeners();
  }
}