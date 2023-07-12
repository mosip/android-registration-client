import 'package:freezed_annotation/freezed_annotation.dart';

part 'biometrics_dto.freezed.dart';

part 'biometrics_dto.g.dart';

@freezed
class BiometricsDto with _$BiometricsDto {
  const factory BiometricsDto({
    String? modality,
    String? bioSubType,
    String? bioValue,
    String? specVersion,
    bool? isException,
    String? decodedBioResponse,
    String? signature,
    bool? isForceCaptured,
    int? numOfRetries,
    double? sdkScore,
    double? qualityScore,
  }) = _BiometricsDto;

  factory BiometricsDto.fromJson(Map<String, Object?> json) =>
      _$BiometricsDtoFromJson(json);
}
