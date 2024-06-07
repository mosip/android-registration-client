import 'package:pigeon/pigeon.dart';

class PacketAuth {
  final String response;
  final String? errorCode;

  PacketAuth({
    required this.response,
    this.errorCode,
  });
}

@HostApi()
abstract class PacketAuthApi {
  @async
  PacketAuth authenticate(String username, String password);

  @async
  void syncPacket(String packetId);

  @async
  void uploadPacket(String packetId);

  @async
  void syncPacketAll(List<String> packetIds);

  @async
  void uploadPacketAll(List<String> packetIds);

  @async
  List<String> getAllRegistrationPacket();

  @async
  void updatePacketStatus(String packetId, String? serverStatus, String clientStatus);
}
