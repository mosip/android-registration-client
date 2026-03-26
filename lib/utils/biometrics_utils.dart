/*
 * Copyright (c) Modular Open Source Identity Platform
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *
 */

import 'package:registration_client/model/biometrics_dto.dart';

/// Extension on [List<BiometricsDto>] for shared quality/SDK score helpers.
extension BiometricsDtoListExtension on List<BiometricsDto> {
  /// Average of [BiometricsDto.qualityScore] across the list. Returns 0.0 if empty or all null.
  double avgScore() {
    if (isEmpty) return 0.0;
    double avg = 0;
    int count = 0;
    for (var dto in this) {
      if (dto.qualityScore != null) {
        avg += dto.qualityScore!;
        count++;
      }
    }
    return count > 0 ? avg / count : 0.0;
  }

  /// Average of [BiometricsDto.sdkScore] for entries with sdkScore != null and > 0. Returns 0.0 if none.
  double avgSDKScore() {
    double avg = 0;
    int count = 0;
    for (var dto in this) {
      if (dto.sdkScore != null && dto.sdkScore! > 0) {
        avg += dto.sdkScore!;
        count++;
      }
    }
    return count > 0 ? avg / count : 0.0;
  }
}
