/*
 * Copyright (c) Modular Open Source Identity Platform
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *
*/

import 'dart:developer';

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

import 'package:registration_client/pigeon/packet_auth_pigeon.dart';

import 'package:registration_client/platform_spi/packet_service.dart';

class PacketServiceImpl implements PacketService {

  @override
  Future<List<String?>> getAllRegistrationPacket() async {
    List<String?> packetStatus = [];

    try {
      packetStatus = await PacketAuthApi().getAllRegistrationPacket();
    } on PlatformException {
      debugPrint('PacketAuthenticationApi call failed!');
    } catch (e) {
      debugPrint(e.toString());
    }
    return packetStatus;
  }

  @override
  Future<void> updatePacketStatus(String packetId, String? serverStatus, String clientStatus) async {
    try {
      await PacketAuthApi().updatePacketStatus(packetId, serverStatus, clientStatus);
      log("Sucess Status updated");
    } on PlatformException {
      debugPrint('PacketAuthenticationApi call failed!');
    } catch (e) {
      debugPrint(e.toString());
    }
  }

  @override
  Future<void> packetSyncAll(List<String> packetIds) async {
    try {
      await PacketAuthApi().syncPacketAll(packetIds);
      log("Sucess Packets Sync");
    } on PlatformException {
      debugPrint('Sync call failed!');
    } catch (e) {
      debugPrint(e.toString());
    }
  }

  @override
  Future<void> packetUploadAll(List<String> packetIds) async {
    try {
      await PacketAuthApi().uploadPacketAll(packetIds);
      log("Sucess Packets Upload");
    } on PlatformException {
      debugPrint('Sync call failed!');
    } catch (e) {
      debugPrint(e.toString());
    }
  }

}

PacketService getPacketServiceImpl() => PacketServiceImpl();
