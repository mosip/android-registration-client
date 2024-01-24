/*
 * Copyright (c) Modular Open Source Identity Platform
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *
*/

import '../platform_android/packet_service_impl.dart';

abstract class PacketService {
  Future<void> packetSync(String packetId);
  Future<void> packetUpload(String packetId);
  Future<List<String?>> getAllRegistrationPacket();

  factory PacketService() => getPacketServiceImpl();
}
