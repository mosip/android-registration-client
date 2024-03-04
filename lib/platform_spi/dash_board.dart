import 'package:registration_client/pigeon/dash_board_pigeon.dart';
import 'package:registration_client/platform_android/dash_board_impl.dart';

abstract class DashBoard {
  Future<List<DashBoardData?>> getDashBoardDetails();

  Future<List<String?>> getPacketUploadedDetails();

  Future<List<String?>> getPacketUploadedPendingDetails();

  factory DashBoard() => DashBoardImpl();
}