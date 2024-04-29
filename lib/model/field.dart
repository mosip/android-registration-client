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
    bool? transliterate,
    String? fieldCategory,
    bool? exceptionPhotoRequired,
    String? subType,
    String? type,
    int? minimum,
    int? maximum,
    String? description,
    String? contactType,
    String? group,
    String? changeAction,
    String? templateName,
    String? locationHierarchy,
    Map<String, String>? label,
    Map<String, String>? groupLabel,
    String? controlType,
    String? fieldType,
    String? format,
    Map<String, String>? visible,
    List<ConditionalBioAttributes?>? conditionalBioAttributes,
    String? alignmentGroup,
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

@freezed
class ConditionalBioAttributes with _$ConditionalBioAttributes {
  const factory ConditionalBioAttributes({
    String? ageGroup,
    List<String?>? bioAttributes,
    String? process,
    String? validationExpr,
  }) = _ConditionalBioAttributes;

  factory ConditionalBioAttributes.fromJson(Map<String, Object?> json) =>
      _$ConditionalBioAttributesFromJson(json);
}
