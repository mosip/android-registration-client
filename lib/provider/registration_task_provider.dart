/*
 * Copyright (c) Modular Open Source Identity Platform
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *
*/

import 'dart:typed_data';

import 'package:flutter/material.dart';
import 'package:registration_client/pigeon/dash_board_pigeon.dart';
import 'package:registration_client/pigeon/dynamic_response_pigeon.dart';
import 'package:registration_client/pigeon/registration_data_pigeon.dart';
import 'package:registration_client/platform_spi/dash_board.dart';
import 'package:registration_client/platform_spi/demographic_service.dart';
import 'package:registration_client/platform_spi/document.dart';
import 'package:registration_client/platform_spi/document_category_service.dart';
import 'package:registration_client/platform_spi/dynamic_response_service.dart';
import 'package:registration_client/platform_spi/process_spec_service.dart';
import 'package:registration_client/platform_spi/registration_service.dart';

class RegistrationTaskProvider with ChangeNotifier {
  final RegistrationService registrationService = RegistrationService();
  final ProcessSpecService processSpecService = ProcessSpecService();
  final DemographicService demographics = DemographicService();
  final Document document = Document();
  final DashBoard dashBoard = DashBoard();
  DynamicResponseService dynamicResponseService = DynamicResponseService();
  final DocumentCategory documentCategory = DocumentCategory();
  List<Object?> _listOfProcesses = List.empty(growable: true);
  String _stringValueGlobalParam = "";
  String _uiSchema = "";
  String _registrationStartError = '';
  bool _isRegistrationSaved = false;

  String _previewTemplate = "";
  String _acknowledgementTemplate = "";

  List<Object?> get listOfProcesses => _listOfProcesses;
  String get stringValueGlobalParam => _stringValueGlobalParam;
  String get uiSchema => _uiSchema;
  String get previewTemplate => _previewTemplate;
  String get acknowledgementTemplate => _acknowledgementTemplate;
  String get registrationStartError => _registrationStartError;
  bool get isRegistrationSaved => _isRegistrationSaved;

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
    String result = await processSpecService.getStringValueGlobalParam(key);
    if (result == "REG_GLOBAL_PARAM_ERROR") {
      _stringValueGlobalParam = "";
    } else {
      _stringValueGlobalParam = result;
    }
    notifyListeners();
  }

  getUISchema() async {
    String result = await processSpecService.getUISchema();
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
    List<String?> result = await processSpecService.getNewProcessSpec();
    if (result.isEmpty) {
      _listOfProcesses = [];
    } else {
      _listOfProcesses = result;
    }
    notifyListeners();
  }

  startRegistration(List<String> languages) async {
    _registrationStartError = await registrationService.startRegistration(languages);
    notifyListeners();
  }

  evaluateMVELVisible(String fieldData, String expression) async {
    return await registrationService.evaluateMVELVisible(fieldData, expression);
  }

  evaluateMVELRequired(String fieldData, String expression) async {
    return await registrationService.evaluateMVELRequired(fieldData, expression);
  }

  setPreviewTemplate(String value) {
    _previewTemplate = "";
    notifyListeners();
  }

  getPreviewTemplate(bool isPreview, Map<String, String> templateValues) async {
    _previewTemplate = await registrationService.getPreviewTemplate(isPreview, templateValues);
    notifyListeners();
  }

  setAcknowledgementTemplate(String value) {
    _acknowledgementTemplate = "";
    notifyListeners();
  }

  getAcknowledgementTemplate(bool isAcknowledgement, Map<String, String> templateValues) async {
    _acknowledgementTemplate = await registrationService.getPreviewTemplate(isAcknowledgement, templateValues);
    notifyListeners();
  }

  setIsRegistrationSaved(bool value) {
    _isRegistrationSaved = value;
    notifyListeners();
  }

  submitRegistrationDto(String makerName) async {
    RegistrationSubmitResponse registrationSubmitResponse =
        await registrationService.submitRegistrationDto(makerName);
    return registrationSubmitResponse;
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

  Future<List<DynamicFieldData?>> getFieldValues(
      String fieldName, String langCode) async {
    return await dynamicResponseService.fetchFieldValues(fieldName, langCode);
  }

  Future<List<GenericData?>> getLocationValues(
      String fieldName, String langCode) async {
    return await dynamicResponseService.fetchLocationValues(
        fieldName, langCode);
  }

  Future<List<String?>> getDocumentValues(
      String fieldName, String langCode, String? applicantType) async {
    return await dynamicResponseService.fetchDocumentValues(
        fieldName, applicantType, langCode);
  }

  Future<List<GenericData?>> getLocationValuesBasedOnParent(
      String? parentCode, String fieldName, String langCode) async {
    return await dynamicResponseService.fetchLocationValuesBasedOnParent(
        parentCode, fieldName, langCode);
  }

  addDocument(
      String fieldId, String docType, String reference, Uint8List bytes) async {
    await document.addDocument(fieldId, docType, reference, bytes);
  }

  getScannedDocument(String fieldId) async {
    return document.getScannedPages(fieldId);
  }

  removeDocument(String fieldId, int pageIndex) async {
    await document.removeDocument(fieldId, pageIndex);
  }

  Future<List<String?>> getDocumentType(String categoryCode,String langCode) async {
    return await documentCategory.getDocumentCategories(categoryCode,langCode);
  }

  removeDocumentField(String fieldId) async {
    await document.removeDocumentField(fieldId);
  }

  Future<int> getPacketUploadedDetails() async {
    return await dashBoard.getPacketUploadedDetails();
  }

  Future<int> getPacketUploadedPendingDetails() async {
    return await dashBoard.getPacketUploadedPendingDetails();
  }

  Future<int> getCreatedPacketDetails() async {
    return await dashBoard.getCreatedPacketDetails();
  }

  Future<int> getSyncedPacketDetails() async {
    return await dashBoard.getSyncedPacketDetails();
  }

  Future<List<DashBoardData?>> getDashBoardDetails() async {
    return await dashBoard.getDashBoardDetails();
  }
}
