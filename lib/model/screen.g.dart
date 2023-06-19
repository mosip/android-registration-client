// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'screen.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

_$_Screen _$$_ScreenFromJson(Map<String, dynamic> json) => _$_Screen(
      name: json['name'] as String?,
      active: json['active'] as bool?,
      fields: (json['fields'] as List<dynamic>?)
          ?.map((e) =>
              e == null ? null : Field.fromJson(e as Map<String, dynamic>))
          .toList(),
      label: (json['label'] as Map<String, dynamic>?)?.map(
        (k, e) => MapEntry(k, e as String),
      ),
      order: json['order'] as int?,
      caption: (json['caption'] as Map<String, dynamic>?)?.map(
        (k, e) => MapEntry(k, e as String),
      ),
      layoutTemplate: json['layoutTemplate'] as String?,
      preRegFetchRequired: json['preRegFetchRequired'] as bool?,
      additionalInfoRequestIdRequired:
          json['additionalInfoRequestIdRequired'] as bool?,
    );

Map<String, dynamic> _$$_ScreenToJson(_$_Screen instance) => <String, dynamic>{
      'name': instance.name,
      'active': instance.active,
      'fields': instance.fields,
      'label': instance.label,
      'order': instance.order,
      'caption': instance.caption,
      'layoutTemplate': instance.layoutTemplate,
      'preRegFetchRequired': instance.preRegFetchRequired,
      'additionalInfoRequestIdRequired':
          instance.additionalInfoRequestIdRequired,
    };
