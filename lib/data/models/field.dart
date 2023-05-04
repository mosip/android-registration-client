/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
*/

import 'package:freezed_annotation/freezed_annotation.dart';
import 'package:flutter/foundation.dart';
import 'package:registration_client/data/models/label.dart';
import 'package:registration_client/data/models/validator.dart';

part 'field.freezed.dart';

part 'field.g.dart';

@freezed
class Field with _$Field {
  const factory Field({
    String? id,
    bool? inputRequired,
    bool? required,
    String? fieldCategory,
    String? type,
    int? minimum,
    int? maximum,
    String? description,
    Label? label,
    String? controlType,
    List<Validator?>? validators,
    List<RequiredOn?>? requiredOn,
  }) = _Field;

  factory Field.fromJson(Map<String, Object?> json) => _$FieldFromJson(json);
}

@freezed
class RequiredOn with _$RequiredOn {
  const factory RequiredOn({
    String? engine,
    String? expr,
  }) = _RequiredOn;

  factory RequiredOn.fromJson(Map<String, Object?> json) =>
      _$RequiredOnFromJson(json);
}
