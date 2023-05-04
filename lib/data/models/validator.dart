/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
*/

import 'package:freezed_annotation/freezed_annotation.dart';

part 'validator.freezed.dart';

part 'validator.g.dart';

@freezed
class Validator with _$Validator {
  const factory Validator({
    String? type,
    String? validator,
    List<dynamic>? arguments,
    String? langCode,
    String? errorCode,
  }) = _Validator;
  factory Validator.fromJson(Map<String, Object?> json) =>
      _$ValidatorFromJson(json);
}
