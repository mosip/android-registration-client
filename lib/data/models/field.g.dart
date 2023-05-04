// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'field.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

_$_Field _$$_FieldFromJson(Map<String, dynamic> json) => _$_Field(
      id: json['id'] as String?,
      inputRequired: json['inputRequired'] as bool?,
      required: json['required'] as bool?,
      fieldCategory: json['fieldCategory'] as String?,
      type: json['type'] as String?,
      minimum: json['minimum'] as int?,
      maximum: json['maximum'] as int?,
      description: json['description'] as String?,
      label: json['label'] == null
          ? null
          : Label.fromJson(json['label'] as Map<String, dynamic>),
      controlType: json['controlType'] as String?,
      validators: (json['validators'] as List<dynamic>?)
          ?.map((e) =>
              e == null ? null : Validator.fromJson(e as Map<String, dynamic>))
          .toList(),
      requiredOn: (json['requiredOn'] as List<dynamic>?)
          ?.map((e) =>
              e == null ? null : RequiredOn.fromJson(e as Map<String, dynamic>))
          .toList(),
    );

Map<String, dynamic> _$$_FieldToJson(_$_Field instance) => <String, dynamic>{
      'id': instance.id,
      'inputRequired': instance.inputRequired,
      'required': instance.required,
      'fieldCategory': instance.fieldCategory,
      'type': instance.type,
      'minimum': instance.minimum,
      'maximum': instance.maximum,
      'description': instance.description,
      'label': instance.label,
      'controlType': instance.controlType,
      'validators': instance.validators,
      'requiredOn': instance.requiredOn,
    };

_$_RequiredOn _$$_RequiredOnFromJson(Map<String, dynamic> json) =>
    _$_RequiredOn(
      engine: json['engine'] as String?,
      expr: json['expr'] as String?,
    );

Map<String, dynamic> _$$_RequiredOnToJson(_$_RequiredOn instance) =>
    <String, dynamic>{
      'engine': instance.engine,
      'expr': instance.expr,
    };
