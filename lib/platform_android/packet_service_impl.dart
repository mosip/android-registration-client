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
}

PacketService getPacketServiceImpl() => PacketServiceImpl();
