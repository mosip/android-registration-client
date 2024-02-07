/*
 * Copyright (c) Modular Open Source Identity Platform
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *
*/

import 'package:registration_client/platform_android/process_spec_service_impl.dart';

abstract class ProcessSpecService {
  Future<String> getUISchema();
  Future<String> getStringValueGlobalParam(String key);
  Future<List<String?>> getNewProcessSpec();
  Future<List<String?>> getMandatoryLanguageCodes();
  Future<List<String?>> getOptionalLanguageCodes();
  Future<int> getMinLanguageCount();
  Future<int> getMaxLanguageCount();

  factory ProcessSpecService() => getProcessSpecServiceImpl();
}