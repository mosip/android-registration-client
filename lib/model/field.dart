import 'package:freezed_annotation/freezed_annotation.dart';
import 'package:flutter/foundation.dart';
import 'package:registration_client/model/validator.dart';

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
    Map<String,String>? label,
    String? controlType,
    List<String?>? bioAttributes,
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
