import 'package:pigeon/pigeon.dart';

@HostApi()
abstract class BiometricsApi {
  @async
  List<Uint8List> getCaptureImages(String modality);
}