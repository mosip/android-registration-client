/*
 * Copyright (c) Modular Open Source Identity Platform
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *
*/

import '../platform_android/packet_service_impl.dart';

abstract class PacketService {
  Future<void> packetSyncAll(List<String> packetIds);

  Future<void> packetUploadAll(List<String> packetIds);

  Future<List<String?>> getAllRegistrationPacket();

  Future<List<String?>> getAllCreatedRegistrationPacket();

  Future<void> updatePacketStatus(
      String packetId, String? serverStatus, String clientStatus);

  Future<void> supervisorReview(
      String packetId, String supervisorStatus, String supervisorComment);

  factory PacketService() => getPacketServiceImpl();
}
