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
  Future<String> startRegistration(List<String> langauages) async {
    String registrationStartResponse = '';
    try {
      registrationStartResponse =
          await RegistrationDataApi().startRegistration(langauages);
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
  Future<bool> evaluateMVELVisible(String fieldData, String expression) async {
    bool isMvelValid = false;
    try {
      isMvelValid =
          await RegistrationDataApi().evaluateMVELVisible(fieldData, expression);
    } on PlatformException {
      debugPrint('RegApi mvel visible call failed');
    } catch (e) {
      debugPrint('Registration not completed! ${e.toString()}');
    }
    return isMvelValid;
  }

  @override
  Future<bool> evaluateMVELRequired(String fieldData, String expression) async {
    bool isMvelValid = false;
    try {
      isMvelValid =
          await RegistrationDataApi().evaluateMVELRequired(fieldData, expression);
    } on PlatformException {
      debugPrint('RegApi mvel call failed');
    } catch (e) {
      debugPrint('Registration not completed! ${e.toString()}');
    }
    return isMvelValid;
  }

  @override
  Future<String> getPreviewTemplate(bool isPreview, Map<String, String> templateValues) async {
    String previewTemplate = '';
    try {
      previewTemplate =
          await RegistrationDataApi().getPreviewTemplate(isPreview,templateValues);
    } on PlatformException {
      debugPrint('Registration API template call failed');
    } catch (e) {
      debugPrint('Preview Template not fetched! ${e.toString()}');
    }

    return previewTemplate;
  }

  @override
  Future<RegistrationSubmitResponse> submitRegistrationDto(String makerName) async {
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
}

RegistrationService getRegistrationServiceImpl() => RegistrationServiceImpl();
