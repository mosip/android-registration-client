/*
 * Copyright (c) Modular Open Source Identity Platform
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *
*/

import 'package:registration_client/platform_android/demographic_service_impl.dart';

abstract class DemographicService {
  Future<void> addDemographicField(String fieldId, String value);
  Future<String> getDemographicField(String fieldId);

  Future<void> addSimpleTypeDemographicField(
      String fieldId, String value, String language);

  Future<String> getSimpleTypeDemographicField(String fieldId, String language);

  Future<void> setDateField(
      String fieldId, String subType, String day, String month, String year);

  Future<void> removeDemographicField(String fieldId);

  Future<void> setConsentField(String consentData);

  Future<void> addUpdatableFields(List<String> fieldIds);

  Future<void> addUpdatableFieldGroup(String fieldGroup);

  Future<void> removeUpdatableFields(List<String> fieldIds);

  Future<void> removeUpdatableFieldGroup(String fieldGroup);

  factory DemographicService() => getDemographicServiceImpl();
}
