import 'package:pigeon/pigeon.dart';

class User {
  final String userId;
  final String? name;
  final String? email;
  final bool? isActive;
  final bool? isLocked;
  final bool isOnboarded;
  final String? centerName;
  final String? centerId;
  final String? machineName;
  final String? machineId;
  final String? failedAttempts;
  final String? errorCode;
  final bool? machineStatus;
  final bool? centerStatus;
  final String? machineCenterId;

  User({
    required this.userId,
    this.name,
    this.email,
    this.isActive,
    this.isLocked,
    required this.isOnboarded,
    this.centerName,
    this.centerId,
    this.machineName,
    this.machineId,
    this.failedAttempts,
    this.errorCode,
    this.machineStatus,
    this.centerStatus,
    this.machineCenterId,
  });
}

@HostApi()
abstract class UserApi {
  @async
  User validateUser(String username, String langCode);
}
