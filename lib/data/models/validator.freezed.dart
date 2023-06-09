// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target, unnecessary_question_mark

part of 'validator.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

T _$identity<T>(T value) => value;

final _privateConstructorUsedError = UnsupportedError(
    'It seems like you constructed your class using `MyClass._()`. This constructor is only meant to be used by freezed and you are not supposed to need it nor use it.\nPlease check the documentation here for more information: https://github.com/rrousselGit/freezed#custom-getters-and-methods');

Validator _$ValidatorFromJson(Map<String, dynamic> json) {
  return _Validator.fromJson(json);
}

/// @nodoc
mixin _$Validator {
  String? get type => throw _privateConstructorUsedError;
  String? get validator => throw _privateConstructorUsedError;
  List<dynamic>? get arguments => throw _privateConstructorUsedError;
  String? get langCode => throw _privateConstructorUsedError;
  String? get errorCode => throw _privateConstructorUsedError;

  Map<String, dynamic> toJson() => throw _privateConstructorUsedError;
  @JsonKey(ignore: true)
  $ValidatorCopyWith<Validator> get copyWith =>
      throw _privateConstructorUsedError;
}

/// @nodoc
abstract class $ValidatorCopyWith<$Res> {
  factory $ValidatorCopyWith(Validator value, $Res Function(Validator) then) =
      _$ValidatorCopyWithImpl<$Res, Validator>;
  @useResult
  $Res call(
      {String? type,
      String? validator,
      List<dynamic>? arguments,
      String? langCode,
      String? errorCode});
}

/// @nodoc
class _$ValidatorCopyWithImpl<$Res, $Val extends Validator>
    implements $ValidatorCopyWith<$Res> {
  _$ValidatorCopyWithImpl(this._value, this._then);

  // ignore: unused_field
  final $Val _value;
  // ignore: unused_field
  final $Res Function($Val) _then;

  @pragma('vm:prefer-inline')
  @override
  $Res call({
    Object? type = freezed,
    Object? validator = freezed,
    Object? arguments = freezed,
    Object? langCode = freezed,
    Object? errorCode = freezed,
  }) {
    return _then(_value.copyWith(
      type: freezed == type
          ? _value.type
          : type // ignore: cast_nullable_to_non_nullable
              as String?,
      validator: freezed == validator
          ? _value.validator
          : validator // ignore: cast_nullable_to_non_nullable
              as String?,
      arguments: freezed == arguments
          ? _value.arguments
          : arguments // ignore: cast_nullable_to_non_nullable
              as List<dynamic>?,
      langCode: freezed == langCode
          ? _value.langCode
          : langCode // ignore: cast_nullable_to_non_nullable
              as String?,
      errorCode: freezed == errorCode
          ? _value.errorCode
          : errorCode // ignore: cast_nullable_to_non_nullable
              as String?,
    ) as $Val);
  }
}

/// @nodoc
abstract class _$$_ValidatorCopyWith<$Res> implements $ValidatorCopyWith<$Res> {
  factory _$$_ValidatorCopyWith(
          _$_Validator value, $Res Function(_$_Validator) then) =
      __$$_ValidatorCopyWithImpl<$Res>;
  @override
  @useResult
  $Res call(
      {String? type,
      String? validator,
      List<dynamic>? arguments,
      String? langCode,
      String? errorCode});
}

/// @nodoc
class __$$_ValidatorCopyWithImpl<$Res>
    extends _$ValidatorCopyWithImpl<$Res, _$_Validator>
    implements _$$_ValidatorCopyWith<$Res> {
  __$$_ValidatorCopyWithImpl(
      _$_Validator _value, $Res Function(_$_Validator) _then)
      : super(_value, _then);

  @pragma('vm:prefer-inline')
  @override
  $Res call({
    Object? type = freezed,
    Object? validator = freezed,
    Object? arguments = freezed,
    Object? langCode = freezed,
    Object? errorCode = freezed,
  }) {
    return _then(_$_Validator(
      type: freezed == type
          ? _value.type
          : type // ignore: cast_nullable_to_non_nullable
              as String?,
      validator: freezed == validator
          ? _value.validator
          : validator // ignore: cast_nullable_to_non_nullable
              as String?,
      arguments: freezed == arguments
          ? _value._arguments
          : arguments // ignore: cast_nullable_to_non_nullable
              as List<dynamic>?,
      langCode: freezed == langCode
          ? _value.langCode
          : langCode // ignore: cast_nullable_to_non_nullable
              as String?,
      errorCode: freezed == errorCode
          ? _value.errorCode
          : errorCode // ignore: cast_nullable_to_non_nullable
              as String?,
    ));
  }
}

/// @nodoc
@JsonSerializable()
class _$_Validator implements _Validator {
  const _$_Validator(
      {this.type,
      this.validator,
      final List<dynamic>? arguments,
      this.langCode,
      this.errorCode})
      : _arguments = arguments;

  factory _$_Validator.fromJson(Map<String, dynamic> json) =>
      _$$_ValidatorFromJson(json);

  @override
  final String? type;
  @override
  final String? validator;
  final List<dynamic>? _arguments;
  @override
  List<dynamic>? get arguments {
    final value = _arguments;
    if (value == null) return null;
    if (_arguments is EqualUnmodifiableListView) return _arguments;
    // ignore: implicit_dynamic_type
    return EqualUnmodifiableListView(value);
  }

  @override
  final String? langCode;
  @override
  final String? errorCode;

  @override
  String toString() {
    return 'Validator(type: $type, validator: $validator, arguments: $arguments, langCode: $langCode, errorCode: $errorCode)';
  }

  @override
  bool operator ==(dynamic other) {
    return identical(this, other) ||
        (other.runtimeType == runtimeType &&
            other is _$_Validator &&
            (identical(other.type, type) || other.type == type) &&
            (identical(other.validator, validator) ||
                other.validator == validator) &&
            const DeepCollectionEquality()
                .equals(other._arguments, _arguments) &&
            (identical(other.langCode, langCode) ||
                other.langCode == langCode) &&
            (identical(other.errorCode, errorCode) ||
                other.errorCode == errorCode));
  }

  @JsonKey(ignore: true)
  @override
  int get hashCode => Object.hash(runtimeType, type, validator,
      const DeepCollectionEquality().hash(_arguments), langCode, errorCode);

  @JsonKey(ignore: true)
  @override
  @pragma('vm:prefer-inline')
  _$$_ValidatorCopyWith<_$_Validator> get copyWith =>
      __$$_ValidatorCopyWithImpl<_$_Validator>(this, _$identity);

  @override
  Map<String, dynamic> toJson() {
    return _$$_ValidatorToJson(
      this,
    );
  }
}

abstract class _Validator implements Validator {
  const factory _Validator(
      {final String? type,
      final String? validator,
      final List<dynamic>? arguments,
      final String? langCode,
      final String? errorCode}) = _$_Validator;

  factory _Validator.fromJson(Map<String, dynamic> json) =
      _$_Validator.fromJson;

  @override
  String? get type;
  @override
  String? get validator;
  @override
  List<dynamic>? get arguments;
  @override
  String? get langCode;
  @override
  String? get errorCode;
  @override
  @JsonKey(ignore: true)
  _$$_ValidatorCopyWith<_$_Validator> get copyWith =>
      throw _privateConstructorUsedError;
}
