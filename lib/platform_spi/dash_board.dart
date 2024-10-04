import 'package:registration_client/pigeon/dash_board_pigeon.dart';
import 'package:registration_client/platform_android/dash_board_impl.dart';

abstract class DashBoard {
  Future<List<DashBoardData?>> getDashBoardDetails();

  Future<int> getPacketUploadedDetails();

  Future<int> getPacketUploadedPendingDetails();

  Future<int> getCreatedPacketDetails();

  Future<int> getSyncedPacketDetails();

  Future<UpdatedTimeData> getUpdatedTime();

  factory DashBoard() => DashBoardImpl();
}