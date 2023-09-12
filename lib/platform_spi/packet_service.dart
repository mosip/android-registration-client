import '../platform_android/packet_service_impl.dart';

abstract class PacketService {
  Future<void> packetSync(String packetId);
  Future<void> packetUpload(String packetId);
  Future<List<String?>> getAllRegistrationPacket();

  factory PacketService() => getPacketServiceImpl();
}
