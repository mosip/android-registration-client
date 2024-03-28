/*
 * Copyright (c) Modular Open Source Identity Platform
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *
*/

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:registration_client/pigeon/demographics_data_pigeon.dart';
import 'package:registration_client/platform_spi/demographic_service.dart';

class DemographicServiceImpl implements DemographicService {
  @override
  Future<void> addDemographicField(String fieldId, String value) async {
    try {
      await DemographicsApi().addDemographicField(fieldId, value);
    } on PlatformException {
      debugPrint('DemographicsApi call failed');
    } catch (e) {
      debugPrint('Field not added ${e.toString()}');
    }
  }

  @override
  Future<void> addSimpleTypeDemographicField(
      String fieldId, String value, String language) async {
    try {
      await DemographicsApi()
          .addSimpleTypeDemographicField(fieldId, value, language);
    } on PlatformException {
      debugPrint('DemographicsApi call failed');
    } catch (e) {
      debugPrint('SimpleTypeField not added ${e.toString()}');
    }
  }

  @override
  Future<void> removeDemographicField(String fieldId) async {
    try {
      await DemographicsApi().removeDemographicField(fieldId);
    } on PlatformException {
      debugPrint('DemographicsApi call failed');
    } catch (e) {
      debugPrint('Remove field failed ${e.toString()}');
    }
  }

  @override
  Future<void> setDateField(String fieldId, String subType, String day,
      String month, String year) async {
    try {
      await DemographicsApi().setDateField(fieldId, subType, day, month, year);
    } on PlatformException {
      debugPrint('DemographicsApi call failed');
    } catch (e) {
      debugPrint('Date Field not added ${e.toString()}');
    }
  }

  @override
  Future<void> setConsentField(String consentData) async {
    try {
      await DemographicsApi().setConsentField(consentData);
    } on PlatformException {
      debugPrint('DemographicsApi call failed');
    } catch (e) {
      debugPrint('Date Field not added ${e.toString()}');
    }
  }

  @override
  Future<String> getDemographicField(String fieldId) async {
    String fieldValue = "";
    try {
      fieldValue = await DemographicsApi().getDemographicField(fieldId);
    } on PlatformException {
      debugPrint('DemographicsApi call failed');
    } catch (e) {
      debugPrint('Field not added ${e.toString()}');
    }
    return fieldValue;
  }

  @override
  Future<String> getSimpleTypeDemographicField(
      String fieldId, String language) async {
    String fieldValue = "";
    try {
      fieldValue = await DemographicsApi()
          .getSimpleTypeDemographicField(fieldId, language);
    } on PlatformException {
      debugPrint('DemographicsApi call failed');
    } catch (e) {
      debugPrint('Field not added ${e.toString()}');
    }
    return fieldValue;
  }
}

DemographicService getDemographicServiceImpl() => DemographicServiceImpl();
