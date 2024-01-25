/*
 * Copyright (c) Modular Open Source Identity Platform
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *
*/

import 'dart:typed_data';

import 'package:registration_client/platform_android/biometrics_service_impl.dart';

abstract class BiometricsService {
  
  Future<String> invokeDiscoverSbi(String fieldId, String modality);

  Future<List<String?>> getBestBiometrics(String fieldId, String modality);

  Future<List<String?>> getBiometrics(String fieldId, String modality, int attempt);

  Future<List<Uint8List?>> extractImageValues(String fieldId, String modality);

  Future<List<Uint8List?>> extractImageValuesByAttempt(
      String fieldId, String modality, int attempt);

  Future<int> incrementBioAttempt(String fieldId, String modality);

  Future<int> getBioAttempt(String fieldId, String modality);

  Future<String> addBioException(String fieldId, String modality, String attribute);

  Future<String> removeBioException(String fieldId, String modality, String attribute);

  Future<String> getMapValue(String key);

  Future<String> getAgeGroup();

  Future<bool> conditionalBioAttributeValidation(String fieldId, String expression);

  factory BiometricsService() => getBiometricsServiceImpl();
}
