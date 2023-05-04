/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
*/

import 'package:freezed_annotation/freezed_annotation.dart';
import 'package:flutter/foundation.dart';
import 'package:registration_client/data/models/label.dart';
import 'package:registration_client/data/models/screen.dart';

part 'process.freezed.dart';

part 'process.g.dart';

@freezed
class Process with _$Process {
  const factory Process({
    String? id,
    bool? isActive,
    int? order,
    String? flow,
    List<Screen?>? screens,
    Label? label,
    Label? caption,
    String? icon,
    String? autoSelectedGroups,
  }) = _Process;

  factory Process.fromJson(Map<String, Object?> json) =>
      _$ProcessFromJson(json);
}
