import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:registration_client/pigeon/dash_board_pigeon.dart';
import 'package:registration_client/platform_spi/dash_board.dart';

class DashBoardImpl implements DashBoard {
  @override
  Future<List<DashBoardData?>> getDashBoardDetails() async {
    List<DashBoardData?> dashBoardData = [];
    try{
      dashBoardData = await DashBoardApi().getDashBoardDetails();
    } on PlatformException {
      debugPrint('DashBoardApi call failed');
    } catch (e) {
      debugPrint('get dashboard data failed ${e.toString()}');
    }
    return dashBoardData;
  }

  @override
  Future<List<String?>> getPacketUploadedDetails() async {
    List<String?> packetUploadedData = [];
    try{
      packetUploadedData = await DashBoardApi().getPacketUploadedDetails();
    } on PlatformException {
      debugPrint('DashBoardApi call failed');
    } catch (e) {
      debugPrint('get packet uploaded data failed ${e.toString()}');
    }
    return packetUploadedData;
  }

  @override
  Future<List<String?>> getPacketUploadedPendingDetails() async {
    List<String?> packetUploadedPendingData = [];
    try{
      packetUploadedPendingData = await DashBoardApi().getPacketUploadedPendingDetails();
    } on PlatformException {
      debugPrint('DashBoardApi call failed');
    } catch (e) {
      debugPrint('get packet uploaded pending data failed ${e.toString()}');
    }
    return packetUploadedPendingData;
  }
}