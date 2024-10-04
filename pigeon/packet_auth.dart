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
  void syncPacketAll(List<String> packetIds);

  @async
  void uploadPacketAll(List<String> packetIds);

  @async
  List<String> getAllRegistrationPacket();

  @async
  List<String> getAllCreatedRegistrationPacket();

  @async
  void updatePacketStatus(
      String packetId, String? serverStatus, String clientStatus);

  @async
  void supervisorReview(
      String packetId, String supervisorStatus, String supervisorComment);
}
