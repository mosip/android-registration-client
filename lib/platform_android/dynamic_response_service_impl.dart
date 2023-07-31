import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:registration_client/pigeon/dynamic_response_pigeon.dart';
import 'package:registration_client/platform_spi/dynamic_response_service.dart';

class DynamicResponseServiceImpl implements DynamicResponseService {
  @override
  Future<List<String?>> fetchFieldValues(
      String fieldName, String langCode) async {
    List<String?> fieldValuesList = [];
    try {
      fieldValuesList =
          await DynamicResponseApi().getFieldValues(fieldName, langCode);
    } on PlatformException {
      debugPrint('DynamicServiceResponseApi call failed!');
    } catch (e) {
      debugPrint('Field Values not fetched! ${e.toString()}');
    }

    return fieldValuesList;
  }

  @override
  Future<List<String?>> fetchLocationValues(
      String hierarchyLevelName, String langCode) async {
    List<String?> locationValuesList = [];
    try {
      locationValuesList = await DynamicResponseApi()
          .getLocationValues(hierarchyLevelName, langCode);
    } on PlatformException {
      debugPrint('DynamicServiceResponseApi call failed!');
    } catch (e) {
      debugPrint('Location Values not fetched! ${e.toString()}');
    }
    return locationValuesList;
  }

  @override
  Future<List<String?>> fetchDocumentValues(
      String categoryCode, String? applicantType, String langCode) async {
    List<String?> documentValuesList = [];
    try {
      documentValuesList = await DynamicResponseApi()
          .getDocumentValues(categoryCode, applicantType, langCode);
    } on PlatformException {
      debugPrint('DynamicServiceResponseApi call failed!');
    } catch (e) {
      debugPrint('Document Values not fetched! ${e.toString()}');
    }
    return documentValuesList;
  }
}

DynamicResponseService getDynamicResponseServiceImpl() =>
    DynamicResponseServiceImpl();
