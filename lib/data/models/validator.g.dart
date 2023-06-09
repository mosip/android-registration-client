// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'validator.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

_$_Validator _$$_ValidatorFromJson(Map<String, dynamic> json) => _$_Validator(
      type: json['type'] as String?,
      validator: json['validator'] as String?,
      arguments: json['arguments'] as List<dynamic>?,
      langCode: json['langCode'] as String?,
      errorCode: json['errorCode'] as String?,
    );

Map<String, dynamic> _$$_ValidatorToJson(_$_Validator instance) =>
    <String, dynamic>{
      'type': instance.type,
      'validator': instance.validator,
      'arguments': instance.arguments,
      'langCode': instance.langCode,
      'errorCode': instance.errorCode,
    };
