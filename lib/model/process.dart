import 'package:freezed_annotation/freezed_annotation.dart';
import 'package:flutter/foundation.dart';
import 'package:registration_client/model/screen.dart';

part 'process.freezed.dart';

part 'process.g.dart';

@freezed
class Process with _$Process {
  const factory Process({
    String? id,
    bool? isActive,
    int? order,
    String? flow,
    List<Screen?>? screens,
    Map<String,String>? label,
    Map<String,String>? caption,
    String? icon,
    String? autoSelectedGroups,
  }) = _Process;

  factory Process.fromJson(Map<String, Object?> json) =>
      _$ProcessFromJson(json);
}
