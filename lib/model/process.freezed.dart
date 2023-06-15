// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target, unnecessary_question_mark

part of 'process.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

T _$identity<T>(T value) => value;

final _privateConstructorUsedError = UnsupportedError(
    'It seems like you constructed your class using `MyClass._()`. This constructor is only meant to be used by freezed and you are not supposed to need it nor use it.\nPlease check the documentation here for more information: https://github.com/rrousselGit/freezed#custom-getters-and-methods');

Process _$ProcessFromJson(Map<String, dynamic> json) {
  return _Process.fromJson(json);
}

/// @nodoc
mixin _$Process {
  String? get id => throw _privateConstructorUsedError;
  bool? get isActive => throw _privateConstructorUsedError;
  int? get order => throw _privateConstructorUsedError;
  String? get flow => throw _privateConstructorUsedError;
  List<Screen?>? get screens => throw _privateConstructorUsedError;
  Map<String, String>? get label => throw _privateConstructorUsedError;
  Map<String, String>? get caption => throw _privateConstructorUsedError;
  String? get icon => throw _privateConstructorUsedError;
  String? get autoSelectedGroups => throw _privateConstructorUsedError;

  Map<String, dynamic> toJson() => throw _privateConstructorUsedError;
  @JsonKey(ignore: true)
  $ProcessCopyWith<Process> get copyWith => throw _privateConstructorUsedError;
}

/// @nodoc
abstract class $ProcessCopyWith<$Res> {
  factory $ProcessCopyWith(Process value, $Res Function(Process) then) =
      _$ProcessCopyWithImpl<$Res, Process>;
  @useResult
  $Res call(
      {String? id,
      bool? isActive,
      int? order,
      String? flow,
      List<Screen?>? screens,
      Map<String, String>? label,
      Map<String, String>? caption,
      String? icon,
      String? autoSelectedGroups});
}

/// @nodoc
class _$ProcessCopyWithImpl<$Res, $Val extends Process>
    implements $ProcessCopyWith<$Res> {
  _$ProcessCopyWithImpl(this._value, this._then);

  // ignore: unused_field
  final $Val _value;
  // ignore: unused_field
  final $Res Function($Val) _then;

  @pragma('vm:prefer-inline')
  @override
  $Res call({
    Object? id = freezed,
    Object? isActive = freezed,
    Object? order = freezed,
    Object? flow = freezed,
    Object? screens = freezed,
    Object? label = freezed,
    Object? caption = freezed,
    Object? icon = freezed,
    Object? autoSelectedGroups = freezed,
  }) {
    return _then(_value.copyWith(
      id: freezed == id
          ? _value.id
          : id // ignore: cast_nullable_to_non_nullable
              as String?,
      isActive: freezed == isActive
          ? _value.isActive
          : isActive // ignore: cast_nullable_to_non_nullable
              as bool?,
      order: freezed == order
          ? _value.order
          : order // ignore: cast_nullable_to_non_nullable
              as int?,
      flow: freezed == flow
          ? _value.flow
          : flow // ignore: cast_nullable_to_non_nullable
              as String?,
      screens: freezed == screens
          ? _value.screens
          : screens // ignore: cast_nullable_to_non_nullable
              as List<Screen?>?,
      label: freezed == label
          ? _value.label
          : label // ignore: cast_nullable_to_non_nullable
              as Map<String, String>?,
      caption: freezed == caption
          ? _value.caption
          : caption // ignore: cast_nullable_to_non_nullable
              as Map<String, String>?,
      icon: freezed == icon
          ? _value.icon
          : icon // ignore: cast_nullable_to_non_nullable
              as String?,
      autoSelectedGroups: freezed == autoSelectedGroups
          ? _value.autoSelectedGroups
          : autoSelectedGroups // ignore: cast_nullable_to_non_nullable
              as String?,
    ) as $Val);
  }
}

/// @nodoc
abstract class _$$_ProcessCopyWith<$Res> implements $ProcessCopyWith<$Res> {
  factory _$$_ProcessCopyWith(
          _$_Process value, $Res Function(_$_Process) then) =
      __$$_ProcessCopyWithImpl<$Res>;
  @override
  @useResult
  $Res call(
      {String? id,
      bool? isActive,
      int? order,
      String? flow,
      List<Screen?>? screens,
      Map<String, String>? label,
      Map<String, String>? caption,
      String? icon,
      String? autoSelectedGroups});
}

/// @nodoc
class __$$_ProcessCopyWithImpl<$Res>
    extends _$ProcessCopyWithImpl<$Res, _$_Process>
    implements _$$_ProcessCopyWith<$Res> {
  __$$_ProcessCopyWithImpl(_$_Process _value, $Res Function(_$_Process) _then)
      : super(_value, _then);

  @pragma('vm:prefer-inline')
  @override
  $Res call({
    Object? id = freezed,
    Object? isActive = freezed,
    Object? order = freezed,
    Object? flow = freezed,
    Object? screens = freezed,
    Object? label = freezed,
    Object? caption = freezed,
    Object? icon = freezed,
    Object? autoSelectedGroups = freezed,
  }) {
    return _then(_$_Process(
      id: freezed == id
          ? _value.id
          : id // ignore: cast_nullable_to_non_nullable
              as String?,
      isActive: freezed == isActive
          ? _value.isActive
          : isActive // ignore: cast_nullable_to_non_nullable
              as bool?,
      order: freezed == order
          ? _value.order
          : order // ignore: cast_nullable_to_non_nullable
              as int?,
      flow: freezed == flow
          ? _value.flow
          : flow // ignore: cast_nullable_to_non_nullable
              as String?,
      screens: freezed == screens
          ? _value._screens
          : screens // ignore: cast_nullable_to_non_nullable
              as List<Screen?>?,
      label: freezed == label
          ? _value._label
          : label // ignore: cast_nullable_to_non_nullable
              as Map<String, String>?,
      caption: freezed == caption
          ? _value._caption
          : caption // ignore: cast_nullable_to_non_nullable
              as Map<String, String>?,
      icon: freezed == icon
          ? _value.icon
          : icon // ignore: cast_nullable_to_non_nullable
              as String?,
      autoSelectedGroups: freezed == autoSelectedGroups
          ? _value.autoSelectedGroups
          : autoSelectedGroups // ignore: cast_nullable_to_non_nullable
              as String?,
    ));
  }
}

/// @nodoc
@JsonSerializable()
class _$_Process with DiagnosticableTreeMixin implements _Process {
  const _$_Process(
      {this.id,
      this.isActive,
      this.order,
      this.flow,
      final List<Screen?>? screens,
      final Map<String, String>? label,
      final Map<String, String>? caption,
      this.icon,
      this.autoSelectedGroups})
      : _screens = screens,
        _label = label,
        _caption = caption;

  factory _$_Process.fromJson(Map<String, dynamic> json) =>
      _$$_ProcessFromJson(json);

  @override
  final String? id;
  @override
  final bool? isActive;
  @override
  final int? order;
  @override
  final String? flow;
  final List<Screen?>? _screens;
  @override
  List<Screen?>? get screens {
    final value = _screens;
    if (value == null) return null;
    if (_screens is EqualUnmodifiableListView) return _screens;
    // ignore: implicit_dynamic_type
    return EqualUnmodifiableListView(value);
  }

  final Map<String, String>? _label;
  @override
  Map<String, String>? get label {
    final value = _label;
    if (value == null) return null;
    if (_label is EqualUnmodifiableMapView) return _label;
    // ignore: implicit_dynamic_type
    return EqualUnmodifiableMapView(value);
  }

  final Map<String, String>? _caption;
  @override
  Map<String, String>? get caption {
    final value = _caption;
    if (value == null) return null;
    if (_caption is EqualUnmodifiableMapView) return _caption;
    // ignore: implicit_dynamic_type
    return EqualUnmodifiableMapView(value);
  }

  @override
  final String? icon;
  @override
  final String? autoSelectedGroups;

  @override
  String toString({DiagnosticLevel minLevel = DiagnosticLevel.info}) {
    return 'Process(id: $id, isActive: $isActive, order: $order, flow: $flow, screens: $screens, label: $label, caption: $caption, icon: $icon, autoSelectedGroups: $autoSelectedGroups)';
  }

  @override
  void debugFillProperties(DiagnosticPropertiesBuilder properties) {
    super.debugFillProperties(properties);
    properties
      ..add(DiagnosticsProperty('type', 'Process'))
      ..add(DiagnosticsProperty('id', id))
      ..add(DiagnosticsProperty('isActive', isActive))
      ..add(DiagnosticsProperty('order', order))
      ..add(DiagnosticsProperty('flow', flow))
      ..add(DiagnosticsProperty('screens', screens))
      ..add(DiagnosticsProperty('label', label))
      ..add(DiagnosticsProperty('caption', caption))
      ..add(DiagnosticsProperty('icon', icon))
      ..add(DiagnosticsProperty('autoSelectedGroups', autoSelectedGroups));
  }

  @override
  bool operator ==(dynamic other) {
    return identical(this, other) ||
        (other.runtimeType == runtimeType &&
            other is _$_Process &&
            (identical(other.id, id) || other.id == id) &&
            (identical(other.isActive, isActive) ||
                other.isActive == isActive) &&
            (identical(other.order, order) || other.order == order) &&
            (identical(other.flow, flow) || other.flow == flow) &&
            const DeepCollectionEquality().equals(other._screens, _screens) &&
            const DeepCollectionEquality().equals(other._label, _label) &&
            const DeepCollectionEquality().equals(other._caption, _caption) &&
            (identical(other.icon, icon) || other.icon == icon) &&
            (identical(other.autoSelectedGroups, autoSelectedGroups) ||
                other.autoSelectedGroups == autoSelectedGroups));
  }

  @JsonKey(ignore: true)
  @override
  int get hashCode => Object.hash(
      runtimeType,
      id,
      isActive,
      order,
      flow,
      const DeepCollectionEquality().hash(_screens),
      const DeepCollectionEquality().hash(_label),
      const DeepCollectionEquality().hash(_caption),
      icon,
      autoSelectedGroups);

  @JsonKey(ignore: true)
  @override
  @pragma('vm:prefer-inline')
  _$$_ProcessCopyWith<_$_Process> get copyWith =>
      __$$_ProcessCopyWithImpl<_$_Process>(this, _$identity);

  @override
  Map<String, dynamic> toJson() {
    return _$$_ProcessToJson(
      this,
    );
  }
}

abstract class _Process implements Process {
  const factory _Process(
      {final String? id,
      final bool? isActive,
      final int? order,
      final String? flow,
      final List<Screen?>? screens,
      final Map<String, String>? label,
      final Map<String, String>? caption,
      final String? icon,
      final String? autoSelectedGroups}) = _$_Process;

  factory _Process.fromJson(Map<String, dynamic> json) = _$_Process.fromJson;

  @override
  String? get id;
  @override
  bool? get isActive;
  @override
  int? get order;
  @override
  String? get flow;
  @override
  List<Screen?>? get screens;
  @override
  Map<String, String>? get label;
  @override
  Map<String, String>? get caption;
  @override
  String? get icon;
  @override
  String? get autoSelectedGroups;
  @override
  @JsonKey(ignore: true)
  _$$_ProcessCopyWith<_$_Process> get copyWith =>
      throw _privateConstructorUsedError;
}
