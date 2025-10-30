/*
 * Copyright (c) Modular Open Source Identity Platform
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *
*/

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:registration_client/pigeon/registration_data_pigeon.dart';
import 'package:registration_client/platform_spi/registration_service.dart';

class RegistrationServiceImpl implements RegistrationService {
  @override
  Future<String> startRegistration(
      List<String> langauages, String flowType, String process) async {
    String registrationStartResponse = '';
    try {
      registrationStartResponse = await RegistrationDataApi()
          .startRegistration(langauages, flowType, process);
    } on PlatformException {
      registrationStartResponse = "Something went wrong!";
      debugPrint('RegApi call failed');
    } catch (e) {
      registrationStartResponse = "Something went wrong!";
      debugPrint('Registration not completed! ${e.toString()}');
    }

    return registrationStartResponse;
  }

  @override
  Future<bool> evaluateMVELVisible(String fieldData) async {
    bool isMvelValid = false;
    try {
      isMvelValid = await RegistrationDataApi().evaluateMVELVisible(fieldData);
    } on PlatformException {
      debugPrint('RegApi mvel visible call failed');
    } catch (e) {
      debugPrint('Registration not completed! ${e.toString()}');
    }
    return isMvelValid;
  }

  @override
  Future<bool> evaluateMVELRequired(String fieldData) async {
    bool isMvelValid = false;
    try {
      isMvelValid = await RegistrationDataApi().evaluateMVELRequired(fieldData);
    } on PlatformException {
      debugPrint('RegApi mvel call failed');
    } catch (e) {
      debugPrint('Registration not completed! ${e.toString()}');
    }
    return isMvelValid;
  }

  @override
  Future<String> getPreviewTemplate(
      bool isPreview, Map<String, String> templateValues) async {
    String previewTemplate = '';
    try {
      previewTemplate = await RegistrationDataApi()
          .getPreviewTemplate(isPreview, templateValues);
    } on PlatformException {
      debugPrint('Registration API template call failed');
    } catch (e) {
      debugPrint('Preview Template not fetched! ${e.toString()}');
    }

    return previewTemplate;
  }

  @override
  Future<RegistrationSubmitResponse> submitRegistrationDto(
      String makerName) async {
    late RegistrationSubmitResponse registrationSubmitResponse;
    try {
      registrationSubmitResponse =
          await RegistrationDataApi().submitRegistrationDto(makerName);
    } on PlatformException {
      debugPrint('Registration API submit registration call failed');
    } catch (e) {
      debugPrint('RegistrationDto not submitted: ${e.toString()}');
    }
    return registrationSubmitResponse;
  }

  @override
  Future<void> setApplicationId(String applicationId) async {
    try {
      await RegistrationDataApi().setApplicationId(applicationId);
    } on PlatformException {
      debugPrint('RegistrationDataApi call failed');
    } catch (e) {
      debugPrint('Application ID not added ${e.toString()}');
    }
  }

  @override
  Future<void> setAdditionalReqId(String additionalReqId) async {
    try {
      await RegistrationDataApi().setAdditionalReqId(additionalReqId);
    } on PlatformException {
      debugPrint('RegistrationDataApi call failed');
    } catch (e) {
      debugPrint('Additional info req ID not added ${e.toString()}');
    }
  }

  @override
  Future<void> setMachineLocation(double latitude, double longitude) async {
    try {
      await RegistrationDataApi().setMachineLocation(latitude, longitude);
    } on PlatformException {
      debugPrint('RegistrationDataApi call failed');
    } catch (e) {
      debugPrint('location not added ${e.toString()}');
    }
  }
}

RegistrationService getRegistrationServiceImpl() => RegistrationServiceImpl();
