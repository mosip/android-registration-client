import 'package:pigeon/pigeon.dart';

class DashBoardData {
  final String userId;
  final String userName;
  final bool userStatus;
  final bool userIsOnboarded;

  DashBoardData({
    required this.userId,
    required this.userName,
    required this.userStatus,
    required this.userIsOnboarded,
  });
}

class UpdatedTimeData {
  final String? updatedTime;
  UpdatedTimeData({
    this.updatedTime,
  });
}

@HostApi()
abstract class DashBoardApi {
  @async
  List<DashBoardData> getDashBoardDetails();
  @async
  int getPacketUploadedDetails();
  @async
  int getPacketUploadedPendingDetails();
  @async
  int getCreatedPacketDetails();
  @async
  int getSyncedPacketDetails();
  @async
  UpdatedTimeData getUpdatedTime();
}