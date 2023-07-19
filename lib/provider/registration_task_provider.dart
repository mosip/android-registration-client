import 'package:flutter/material.dart';
import 'package:registration_client/pigeon/registration_data_pigeon.dart';
import 'package:registration_client/platform_spi/demographics.dart';
import 'package:registration_client/platform_spi/process_spec.dart';
import 'package:registration_client/platform_spi/registration.dart';

class RegistrationTaskProvider with ChangeNotifier {
  final Registration registration = Registration();
  final ProcessSpec processSpec = ProcessSpec();
  final Demographics demographics = Demographics();
  List<Object?> _listOfProcesses = List.empty(growable: true);
  String _stringValueGlobalParam = "";
  String _uiSchema = "";
  String _registrationStartError = '';

  String _previewTemplate = "";

  List<Object?> get listOfProcesses => _listOfProcesses;
  String get stringValueGlobalParam => _stringValueGlobalParam;
  String get uiSchema => _uiSchema;
  String get previewTemplate => _previewTemplate;
  String get registrationStartError => _registrationStartError;

  set listOfProcesses(List<Object?> value) {
    _listOfProcesses = value;
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
    if (result == "REG_GLOBAL_PARAM_ERROR") {
      _stringValueGlobalParam = "";
    } else {
      _stringValueGlobalParam = result;
    }
    notifyListeners();
  }

  getUISchema() async {
    String result = await processSpec.getUISchema();
    if (result == "REG_UI_SCHEMA_ERROR") {
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
    if (result.isEmpty) {
      _listOfProcesses = [];
    } else {
      _listOfProcesses = result;
    }
    notifyListeners();
  }

  startRegistration(List<String> languages) async {
    _registrationStartError = await registration.startRegistration(languages);
    notifyListeners();
  }

  evaluateMVEL(String fieldData, String expression) async {
    return await registration.evaluateMVEL(fieldData, expression);
  }

  getPreviewTemplate(bool isPreview) async {
    _previewTemplate = await registration.getPreviewTemplate(isPreview);
    notifyListeners();
  }

  addDemographicField(String fieldId, String value) async {
    await demographics.addDemographicField(fieldId, value);
  }

  getDemographicField(String fieldId) async {
    return await demographics.getDemographicField(fieldId);
  }

  addSimpleTypeDemographicField(
      String fieldId, String value, String language) async {
    await demographics.addSimpleTypeDemographicField(fieldId, value, language);
  }

  getSimpleTypeDemographicField(String fieldId, String language) async {
    return await demographics.getSimpleTypeDemographicField(fieldId, language);
  }

  setDateField(String fieldId, String subType, String day, String month,
      String year) async {
    await demographics.setDateField(fieldId, subType, day, month, year);
  }

  removeDemographicField(String fieldId) async {
    await demographics.removeDemographicField(fieldId);
  }

  addConsentField(String consentData) async {
    await demographics.setConsentField(consentData);
  }
}
