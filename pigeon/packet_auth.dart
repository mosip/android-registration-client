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
  PacketAuth authenticate(String username, String password, bool isConnected);

  @async
  void syncPacket(String packetId);

  @async
  void uploadPacket(String packetId);

  @async
  List<String> getAllRegistrationPacket();
}
