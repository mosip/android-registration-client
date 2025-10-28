import 'package:freezed_annotation/freezed_annotation.dart';
import 'package:flutter/foundation.dart';

part 'registration.freezed.dart';

part 'registration.g.dart';

@freezed
class Registration with _$Registration {
  const factory Registration({

  required String packetId,
  String? usrId,
  String? regType,
  String? preRegId,
  String? filePath,
  String? clientStatus,
  String? serverStatus,
  int? clientStatusDtimes,
  int? serverStatusDtimes,
  String? clientStatusComment,
  String? serverStatusComment,
  String? centerId,
  String? approvedBy,
  String? approverRoleCode,
  String? fileUploadStatus,
  int? uploadCount,
  int? uploadDtimes,
  String? additionalInfo,
  String? appId,
  String? additionalInfoReqId,
  String? ackSignature,
  bool? hasBwords,
  bool? isActive,
  String? crBy,
  int? crDtime,
  String? updBy,
  int? updDtimes,
    String? id
  }) = _Registration;

  factory Registration.fromJson(Map<String, Object?> json) => _$RegistrationFromJson(json);
}
