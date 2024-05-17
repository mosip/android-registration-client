/*
 * Copyright (c) Modular Open Source Identity Platform
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *
*/

import 'package:registration_client/pigeon/dynamic_response_pigeon.dart';
import 'package:registration_client/platform_android/dynamic_response_service_impl.dart';

abstract class DynamicResponseService {
  Future<List<DynamicFieldData?>> fetchFieldValues(String fieldName, String langCode, List<String> languages);

  Future<List<GenericData?>> fetchLocationValues(
      String hierarchyLevelName, String langCode);

  Future<List<String?>> fetchDocumentValues(
      String categoryCode, String? applicantType, String langCode);

  Future<List<GenericData?>> fetchLocationValuesBasedOnParent(
      String? parentCode, String hierarchyLevelName, String langCode, List<String> languages);

  Future<List<LanguageData?>> fetchAllLanguages();

  Future<Map<String?, String?>> fetchLocationHierarchyMap();

  factory DynamicResponseService() => getDynamicResponseServiceImpl();
}
