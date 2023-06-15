import 'package:freezed_annotation/freezed_annotation.dart';

part 'validator.freezed.dart';

part 'validator.g.dart';

@freezed
class Validator with _$Validator {
  const factory Validator({
    String? type,
    String? validator,
    List<dynamic>? arguments,
    String? langCode,
    String? errorCode,
  }) = _Validator;
  factory Validator.fromJson(Map<String, Object?> json) =>
      _$ValidatorFromJson(json);
}
