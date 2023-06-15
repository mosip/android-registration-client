// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'process.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

_$_Process _$$_ProcessFromJson(Map<String, dynamic> json) => _$_Process(
      id: json['id'] as String?,
      isActive: json['isActive'] as bool?,
      order: json['order'] as int?,
      flow: json['flow'] as String?,
      screens: (json['screens'] as List<dynamic>?)
          ?.map((e) =>
              e == null ? null : Screen.fromJson(e as Map<String, dynamic>))
          .toList(),
      label: (json['label'] as Map<String, dynamic>?)?.map(
        (k, e) => MapEntry(k, e as String),
      ),
      caption: (json['caption'] as Map<String, dynamic>?)?.map(
        (k, e) => MapEntry(k, e as String),
      ),
      icon: json['icon'] as String?,
      autoSelectedGroups: json['autoSelectedGroups'] as String?,
    );

Map<String, dynamic> _$$_ProcessToJson(_$_Process instance) =>
    <String, dynamic>{
      'id': instance.id,
      'isActive': instance.isActive,
      'order': instance.order,
      'flow': instance.flow,
      'screens': instance.screens,
      'label': instance.label,
      'caption': instance.caption,
      'icon': instance.icon,
      'autoSelectedGroups': instance.autoSelectedGroups,
    };
