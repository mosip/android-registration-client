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
  Future<void> packetSync(String packetId) async {
    try {
      await PacketAuthApi().syncPacket(packetId);
      log("Sucess Sync packet");
    } on PlatformException {
      debugPrint('PacketAuthenticationApi call failed!');
    } catch (e) {
      debugPrint(e.toString());
    }
  }

  @override
  Future<void> packetUpload(String packetId) async {
    try {
      await PacketAuthApi().uploadPacket(packetId);
      log("Sucess Upload packet");
    } on PlatformException {
      debugPrint('PacketAuthenticationApi call failed!');
    } catch (e) {
      debugPrint(e.toString());
    }
  }

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
}

PacketService getPacketServiceImpl() => PacketServiceImpl();
