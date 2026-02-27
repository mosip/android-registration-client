import 'package:pigeon/pigeon.dart';

@HostApi()
abstract class SecureScreenApi {
  @async
  bool addFlagSecure();

  @async
  bool clearFlagSecure();
}