// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target, unnecessary_question_mark

part of 'field.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

T _$identity<T>(T value) => value;

final _privateConstructorUsedError = UnsupportedError(
    'It seems like you constructed your class using `MyClass._()`. This constructor is only meant to be used by freezed and you are not supposed to need it nor use it.\nPlease check the documentation here for more information: https://github.com/rrousselGit/freezed#custom-getters-and-methods');

Field _$FieldFromJson(Map<String, dynamic> json) {
  return _Field.fromJson(json);
}

/// @nodoc
mixin _$Field {
  String? get id => throw _privateConstructorUsedError;
  bool? get inputRequired => throw _privateConstructorUsedError;
  bool? get required => throw _privateConstructorUsedError;
  String? get fieldCategory => throw _privateConstructorUsedError;
  String? get type => throw _privateConstructorUsedError;
  int? get minimum => throw _privateConstructorUsedError;
  int? get maximum => throw _privateConstructorUsedError;
  String? get description => throw _privateConstructorUsedError;
  Label? get label => throw _privateConstructorUsedError;
  String? get controlType => throw _privateConstructorUsedError;
  List<Validator?>? get validators => throw _privateConstructorUsedError;
  List<RequiredOn?>? get requiredOn => throw _privateConstructorUsedError;

  Map<String, dynamic> toJson() => throw _privateConstructorUsedError;
  @JsonKey(ignore: true)
  $FieldCopyWith<Field> get copyWith => throw _privateConstructorUsedError;
}

/// @nodoc
abstract class $FieldCopyWith<$Res> {
  factory $FieldCopyWith(Field value, $Res Function(Field) then) =
      _$FieldCopyWithImpl<$Res, Field>;
  @useResult
  $Res call(
      {String? id,
      bool? inputRequired,
      bool? required,
      String? fieldCategory,
      String? type,
      int? minimum,
      int? maximum,
      String? description,
      Label? label,
      String? controlType,
      List<Validator?>? validators,
      List<RequiredOn?>? requiredOn});

  $LabelCopyWith<$Res>? get label;
}

/// @nodoc
class _$FieldCopyWithImpl<$Res, $Val extends Field>
    implements $FieldCopyWith<$Res> {
  _$FieldCopyWithImpl(this._value, this._then);

  // ignore: unused_field
  final $Val _value;
  // ignore: unused_field
  final $Res Function($Val) _then;

  @pragma('vm:prefer-inline')
  @override
  $Res call({
    Object? id = freezed,
    Object? inputRequired = freezed,
    Object? required = freezed,
    Object? fieldCategory = freezed,
    Object? type = freezed,
    Object? minimum = freezed,
    Object? maximum = freezed,
    Object? description = freezed,
    Object? label = freezed,
    Object? controlType = freezed,
    Object? validators = freezed,
    Object? requiredOn = freezed,
  }) {
    return _then(_value.copyWith(
      id: freezed == id
          ? _value.id
          : id // ignore: cast_nullable_to_non_nullable
              as String?,
      inputRequired: freezed == inputRequired
          ? _value.inputRequired
          : inputRequired // ignore: cast_nullable_to_non_nullable
              as bool?,
      required: freezed == required
          ? _value.required
          : required // ignore: cast_nullable_to_non_nullable
              as bool?,
      fieldCategory: freezed == fieldCategory
          ? _value.fieldCategory
          : fieldCategory // ignore: cast_nullable_to_non_nullable
              as String?,
      type: freezed == type
          ? _value.type
          : type // ignore: cast_nullable_to_non_nullable
              as String?,
      minimum: freezed == minimum
          ? _value.minimum
          : minimum // ignore: cast_nullable_to_non_nullable
              as int?,
      maximum: freezed == maximum
          ? _value.maximum
          : maximum // ignore: cast_nullable_to_non_nullable
              as int?,
      description: freezed == description
          ? _value.description
          : description // ignore: cast_nullable_to_non_nullable
              as String?,
      label: freezed == label
          ? _value.label
          : label // ignore: cast_nullable_to_non_nullable
              as Label?,
      controlType: freezed == controlType
          ? _value.controlType
          : controlType // ignore: cast_nullable_to_non_nullable
              as String?,
      validators: freezed == validators
          ? _value.validators
          : validators // ignore: cast_nullable_to_non_nullable
              as List<Validator?>?,
      requiredOn: freezed == requiredOn
          ? _value.requiredOn
          : requiredOn // ignore: cast_nullable_to_non_nullable
              as List<RequiredOn?>?,
    ) as $Val);
  }

  @override
  @pragma('vm:prefer-inline')
  $LabelCopyWith<$Res>? get label {
    if (_value.label == null) {
      return null;
    }

    return $LabelCopyWith<$Res>(_value.label!, (value) {
      return _then(_value.copyWith(label: value) as $Val);
    });
  }
}

/// @nodoc
abstract class _$$_FieldCopyWith<$Res> implements $FieldCopyWith<$Res> {
  factory _$$_FieldCopyWith(_$_Field value, $Res Function(_$_Field) then) =
      __$$_FieldCopyWithImpl<$Res>;
  @override
  @useResult
  $Res call(
      {String? id,
      bool? inputRequired,
      bool? required,
      String? fieldCategory,
      String? type,
      int? minimum,
      int? maximum,
      String? description,
      Label? label,
      String? controlType,
      List<Validator?>? validators,
      List<RequiredOn?>? requiredOn});

  @override
  $LabelCopyWith<$Res>? get label;
}

/// @nodoc
class __$$_FieldCopyWithImpl<$Res> extends _$FieldCopyWithImpl<$Res, _$_Field>
    implements _$$_FieldCopyWith<$Res> {
  __$$_FieldCopyWithImpl(_$_Field _value, $Res Function(_$_Field) _then)
      : super(_value, _then);

  @pragma('vm:prefer-inline')
  @override
  $Res call({
    Object? id = freezed,
    Object? inputRequired = freezed,
    Object? required = freezed,
    Object? fieldCategory = freezed,
    Object? type = freezed,
    Object? minimum = freezed,
    Object? maximum = freezed,
    Object? description = freezed,
    Object? label = freezed,
    Object? controlType = freezed,
    Object? validators = freezed,
    Object? requiredOn = freezed,
  }) {
    return _then(_$_Field(
      id: freezed == id
          ? _value.id
          : id // ignore: cast_nullable_to_non_nullable
              as String?,
      inputRequired: freezed == inputRequired
          ? _value.inputRequired
          : inputRequired // ignore: cast_nullable_to_non_nullable
              as bool?,
      required: freezed == required
          ? _value.required
          : required // ignore: cast_nullable_to_non_nullable
              as bool?,
      fieldCategory: freezed == fieldCategory
          ? _value.fieldCategory
          : fieldCategory // ignore: cast_nullable_to_non_nullable
              as String?,
      type: freezed == type
          ? _value.type
          : type // ignore: cast_nullable_to_non_nullable
              as String?,
      minimum: freezed == minimum
          ? _value.minimum
          : minimum // ignore: cast_nullable_to_non_nullable
              as int?,
      maximum: freezed == maximum
          ? _value.maximum
          : maximum // ignore: cast_nullable_to_non_nullable
              as int?,
      description: freezed == description
          ? _value.description
          : description // ignore: cast_nullable_to_non_nullable
              as String?,
      label: freezed == label
          ? _value.label
          : label // ignore: cast_nullable_to_non_nullable
              as Label?,
      controlType: freezed == controlType
          ? _value.controlType
          : controlType // ignore: cast_nullable_to_non_nullable
              as String?,
      validators: freezed == validators
          ? _value._validators
          : validators // ignore: cast_nullable_to_non_nullable
              as List<Validator?>?,
      requiredOn: freezed == requiredOn
          ? _value._requiredOn
          : requiredOn // ignore: cast_nullable_to_non_nullable
              as List<RequiredOn?>?,
    ));
  }
}

/// @nodoc
@JsonSerializable()
class _$_Field with DiagnosticableTreeMixin implements _Field {
  const _$_Field(
      {this.id,
      this.inputRequired,
      this.required,
      this.fieldCategory,
      this.type,
      this.minimum,
      this.maximum,
      this.description,
      this.label,
      this.controlType,
      final List<Validator?>? validators,
      final List<RequiredOn?>? requiredOn})
      : _validators = validators,
        _requiredOn = requiredOn;

  factory _$_Field.fromJson(Map<String, dynamic> json) =>
      _$$_FieldFromJson(json);

  @override
  final String? id;
  @override
  final bool? inputRequired;
  @override
  final bool? required;
  @override
  final String? fieldCategory;
  @override
  final String? type;
  @override
  final int? minimum;
  @override
  final int? maximum;
  @override
  final String? description;
  @override
  final Label? label;
  @override
  final String? controlType;
  final List<Validator?>? _validators;
  @override
  List<Validator?>? get validators {
    final value = _validators;
    if (value == null) return null;
    if (_validators is EqualUnmodifiableListView) return _validators;
    // ignore: implicit_dynamic_type
    return EqualUnmodifiableListView(value);
  }

  final List<RequiredOn?>? _requiredOn;
  @override
  List<RequiredOn?>? get requiredOn {
    final value = _requiredOn;
    if (value == null) return null;
    if (_requiredOn is EqualUnmodifiableListView) return _requiredOn;
    // ignore: implicit_dynamic_type
    return EqualUnmodifiableListView(value);
  }

  @override
  String toString({DiagnosticLevel minLevel = DiagnosticLevel.info}) {
    return 'Field(id: $id, inputRequired: $inputRequired, required: $required, fieldCategory: $fieldCategory, type: $type, minimum: $minimum, maximum: $maximum, description: $description, label: $label, controlType: $controlType, validators: $validators, requiredOn: $requiredOn)';
  }

  @override
  void debugFillProperties(DiagnosticPropertiesBuilder properties) {
    super.debugFillProperties(properties);
    properties
      ..add(DiagnosticsProperty('type', 'Field'))
      ..add(DiagnosticsProperty('id', id))
      ..add(DiagnosticsProperty('inputRequired', inputRequired))
      ..add(DiagnosticsProperty('required', required))
      ..add(DiagnosticsProperty('fieldCategory', fieldCategory))
      ..add(DiagnosticsProperty('type', type))
      ..add(DiagnosticsProperty('minimum', minimum))
      ..add(DiagnosticsProperty('maximum', maximum))
      ..add(DiagnosticsProperty('description', description))
      ..add(DiagnosticsProperty('label', label))
      ..add(DiagnosticsProperty('controlType', controlType))
      ..add(DiagnosticsProperty('validators', validators))
      ..add(DiagnosticsProperty('requiredOn', requiredOn));
  }

  @override
  bool operator ==(dynamic other) {
    return identical(this, other) ||
        (other.runtimeType == runtimeType &&
            other is _$_Field &&
            (identical(other.id, id) || other.id == id) &&
            (identical(other.inputRequired, inputRequired) ||
                other.inputRequired == inputRequired) &&
            (identical(other.required, required) ||
                other.required == required) &&
            (identical(other.fieldCategory, fieldCategory) ||
                other.fieldCategory == fieldCategory) &&
            (identical(other.type, type) || other.type == type) &&
            (identical(other.minimum, minimum) || other.minimum == minimum) &&
            (identical(other.maximum, maximum) || other.maximum == maximum) &&
            (identical(other.description, description) ||
                other.description == description) &&
            (identical(other.label, label) || other.label == label) &&
            (identical(other.controlType, controlType) ||
                other.controlType == controlType) &&
            const DeepCollectionEquality()
                .equals(other._validators, _validators) &&
            const DeepCollectionEquality()
                .equals(other._requiredOn, _requiredOn));
  }

  @JsonKey(ignore: true)
  @override
  int get hashCode => Object.hash(
      runtimeType,
      id,
      inputRequired,
      required,
      fieldCategory,
      type,
      minimum,
      maximum,
      description,
      label,
      controlType,
      const DeepCollectionEquality().hash(_validators),
      const DeepCollectionEquality().hash(_requiredOn));

  @JsonKey(ignore: true)
  @override
  @pragma('vm:prefer-inline')
  _$$_FieldCopyWith<_$_Field> get copyWith =>
      __$$_FieldCopyWithImpl<_$_Field>(this, _$identity);

  @override
  Map<String, dynamic> toJson() {
    return _$$_FieldToJson(
      this,
    );
  }
}

abstract class _Field implements Field {
  const factory _Field(
      {final String? id,
      final bool? inputRequired,
      final bool? required,
      final String? fieldCategory,
      final String? type,
      final int? minimum,
      final int? maximum,
      final String? description,
      final Label? label,
      final String? controlType,
      final List<Validator?>? validators,
      final List<RequiredOn?>? requiredOn}) = _$_Field;

  factory _Field.fromJson(Map<String, dynamic> json) = _$_Field.fromJson;

  @override
  String? get id;
  @override
  bool? get inputRequired;
  @override
  bool? get required;
  @override
  String? get fieldCategory;
  @override
  String? get type;
  @override
  int? get minimum;
  @override
  int? get maximum;
  @override
  String? get description;
  @override
  Label? get label;
  @override
  String? get controlType;
  @override
  List<Validator?>? get validators;
  @override
  List<RequiredOn?>? get requiredOn;
  @override
  @JsonKey(ignore: true)
  _$$_FieldCopyWith<_$_Field> get copyWith =>
      throw _privateConstructorUsedError;
}

RequiredOn _$RequiredOnFromJson(Map<String, dynamic> json) {
  return _RequiredOn.fromJson(json);
}

/// @nodoc
mixin _$RequiredOn {
  String? get engine => throw _privateConstructorUsedError;
  String? get expr => throw _privateConstructorUsedError;

  Map<String, dynamic> toJson() => throw _privateConstructorUsedError;
  @JsonKey(ignore: true)
  $RequiredOnCopyWith<RequiredOn> get copyWith =>
      throw _privateConstructorUsedError;
}

/// @nodoc
abstract class $RequiredOnCopyWith<$Res> {
  factory $RequiredOnCopyWith(
          RequiredOn value, $Res Function(RequiredOn) then) =
      _$RequiredOnCopyWithImpl<$Res, RequiredOn>;
  @useResult
  $Res call({String? engine, String? expr});
}

/// @nodoc
class _$RequiredOnCopyWithImpl<$Res, $Val extends RequiredOn>
    implements $RequiredOnCopyWith<$Res> {
  _$RequiredOnCopyWithImpl(this._value, this._then);

  // ignore: unused_field
  final $Val _value;
  // ignore: unused_field
  final $Res Function($Val) _then;

  @pragma('vm:prefer-inline')
  @override
  $Res call({
    Object? engine = freezed,
    Object? expr = freezed,
  }) {
    return _then(_value.copyWith(
      engine: freezed == engine
          ? _value.engine
          : engine // ignore: cast_nullable_to_non_nullable
              as String?,
      expr: freezed == expr
          ? _value.expr
          : expr // ignore: cast_nullable_to_non_nullable
              as String?,
    ) as $Val);
  }
}

/// @nodoc
abstract class _$$_RequiredOnCopyWith<$Res>
    implements $RequiredOnCopyWith<$Res> {
  factory _$$_RequiredOnCopyWith(
          _$_RequiredOn value, $Res Function(_$_RequiredOn) then) =
      __$$_RequiredOnCopyWithImpl<$Res>;
  @override
  @useResult
  $Res call({String? engine, String? expr});
}

/// @nodoc
class __$$_RequiredOnCopyWithImpl<$Res>
    extends _$RequiredOnCopyWithImpl<$Res, _$_RequiredOn>
    implements _$$_RequiredOnCopyWith<$Res> {
  __$$_RequiredOnCopyWithImpl(
      _$_RequiredOn _value, $Res Function(_$_RequiredOn) _then)
      : super(_value, _then);

  @pragma('vm:prefer-inline')
  @override
  $Res call({
    Object? engine = freezed,
    Object? expr = freezed,
  }) {
    return _then(_$_RequiredOn(
      engine: freezed == engine
          ? _value.engine
          : engine // ignore: cast_nullable_to_non_nullable
              as String?,
      expr: freezed == expr
          ? _value.expr
          : expr // ignore: cast_nullable_to_non_nullable
              as String?,
    ));
  }
}

/// @nodoc
@JsonSerializable()
class _$_RequiredOn with DiagnosticableTreeMixin implements _RequiredOn {
  const _$_RequiredOn({this.engine, this.expr});

  factory _$_RequiredOn.fromJson(Map<String, dynamic> json) =>
      _$$_RequiredOnFromJson(json);

  @override
  final String? engine;
  @override
  final String? expr;

  @override
  String toString({DiagnosticLevel minLevel = DiagnosticLevel.info}) {
    return 'RequiredOn(engine: $engine, expr: $expr)';
  }

  @override
  void debugFillProperties(DiagnosticPropertiesBuilder properties) {
    super.debugFillProperties(properties);
    properties
      ..add(DiagnosticsProperty('type', 'RequiredOn'))
      ..add(DiagnosticsProperty('engine', engine))
      ..add(DiagnosticsProperty('expr', expr));
  }

  @override
  bool operator ==(dynamic other) {
    return identical(this, other) ||
        (other.runtimeType == runtimeType &&
            other is _$_RequiredOn &&
            (identical(other.engine, engine) || other.engine == engine) &&
            (identical(other.expr, expr) || other.expr == expr));
  }

  @JsonKey(ignore: true)
  @override
  int get hashCode => Object.hash(runtimeType, engine, expr);

  @JsonKey(ignore: true)
  @override
  @pragma('vm:prefer-inline')
  _$$_RequiredOnCopyWith<_$_RequiredOn> get copyWith =>
      __$$_RequiredOnCopyWithImpl<_$_RequiredOn>(this, _$identity);

  @override
  Map<String, dynamic> toJson() {
    return _$$_RequiredOnToJson(
      this,
    );
  }
}

abstract class _RequiredOn implements RequiredOn {
  const factory _RequiredOn({final String? engine, final String? expr}) =
      _$_RequiredOn;

  factory _RequiredOn.fromJson(Map<String, dynamic> json) =
      _$_RequiredOn.fromJson;

  @override
  String? get engine;
  @override
  String? get expr;
  @override
  @JsonKey(ignore: true)
  _$$_RequiredOnCopyWith<_$_RequiredOn> get copyWith =>
      throw _privateConstructorUsedError;
}
