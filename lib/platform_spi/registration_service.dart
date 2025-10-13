/*
 * Copyright (c) Modular Open Source Identity Platform
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *
*/

import 'package:registration_client/pigeon/registration_data_pigeon.dart';
import 'package:registration_client/platform_android/registration_service_impl.dart';

abstract class RegistrationService {
  Future<String> startRegistration(
      List<String> languages, String flopwType, String process);
  Future<bool> evaluateMVELVisible(String fieldData);
  Future<bool> evaluateMVELRequired(String fieldData);
  Future<String> getPreviewTemplate(
      bool isPreview, Map<String, String> templateValues);
  Future<RegistrationSubmitResponse> submitRegistrationDto(String makerName);
  Future<void> setApplicationId(String applicationId);
  Future<void> setAdditionalReqId(String additionalReqId);

  factory RegistrationService() => getRegistrationServiceImpl();
}
