import 'package:freezed_annotation/freezed_annotation.dart';
import 'package:flutter/foundation.dart';
import 'package:registration_client/model/field.dart';

part 'screen.freezed.dart';

part 'screen.g.dart';

@freezed
class Screen with _$Screen {
  const factory Screen({
    String? name,
    bool? active,
    List<Field?>? fields,
    Map<String,String>? label,
    int? order,
    Map<String,String>? caption,
    String? layoutTemplate,
    bool? preRegFetchRequired,
    bool? additionalInfoRequestIdRequired,
  }) = _Screen;

  factory Screen.fromJson(Map<String, Object?> json) => _$ScreenFromJson(json);
}
