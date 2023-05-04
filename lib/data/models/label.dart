/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
*/

import 'package:freezed_annotation/freezed_annotation.dart';
import 'package:flutter/foundation.dart';

part 'label.freezed.dart';

part 'label.g.dart';

@freezed
class Label with _$Label {
  const factory Label({
    String? ara,
    String? tam,
    String? fra,
    String? kan,
    String? hin,
    String? eng,
  }) = _Label;

  factory Label.fromJson(Map<String, Object?> json) => _$LabelFromJson(json);
}
