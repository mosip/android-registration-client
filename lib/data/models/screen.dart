/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
*/

import 'package:freezed_annotation/freezed_annotation.dart';
import 'package:flutter/foundation.dart';
import 'package:registration_client/data/models/field.dart';
import 'package:registration_client/data/models/label.dart';

part 'screen.freezed.dart';

part 'screen.g.dart';

@freezed
class Screen with _$Screen {
  const factory Screen({
    String? name,
    bool? active,
    List<Field?>? fields,
    Label? label,
    int? order,
    Label? caption,
    String? layoutTemplate,
    bool? preRegFetchRequired,
    bool? additionalInfoRequestIdRequired,
  }) = _Screen;

  factory Screen.fromJson(Map<String, Object?> json) => _$ScreenFromJson(json);
}
