/*
 * Copyright (c) Modular Open Source Identity Platform
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *
*/

import 'package:registration_client/pigeon/registration_data_pigeon.dart';
import 'package:registration_client/platform_android/registration_service_impl.dart';

abstract class RegistrationService {
  Future<String> startRegistration(List<String> languages);
  Future<bool> evaluateMVELVisible(String fieldData, String expression);
  Future<bool> evaluateMVELRequired(String fieldData, String expression);
  Future<String> getPreviewTemplate(bool isPreview);
  Future<RegistrationSubmitResponse> submitRegistrationDto(String makerName);

  factory RegistrationService() => getRegistrationServiceImpl();
}
