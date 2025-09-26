import 'package:freezed_annotation/freezed_annotation.dart';
import 'package:flutter/foundation.dart';

part 'settings.freezed.dart';
part 'settings.g.dart';

@freezed
class Settings with _$Settings {
  const factory Settings({
    Map<String, String>? description,
    String? fxml,
    String? icon,
    Map<String, String>? label,
    String? name,
    String? order,
    @JsonKey(name: 'access-control') List<String>? accessControl,
    @JsonKey(name: 'shortcut-icon') String? shortcutIcon,
  }) = _Settings;

  factory Settings.fromJson(Map<String, Object?> json) =>
      _$SettingsFromJson(json);
}
