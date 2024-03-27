import 'package:pigeon/pigeon.dart';

class Sync{
  final String? syncType;
  final int? syncProgress;
  final String? errorCode;
  Sync({this.syncType, this.syncProgress, this.errorCode});
}

class SyncTime{
  final String? syncTime;
  SyncTime({ this.syncTime});
}

@HostApi()
abstract class SyncApi{
  @async
  SyncTime getLastSyncTime();
  @async
  Sync getPolicyKeySync();
  @async
  Sync getGlobalParamsSync();
  @async
  Sync getUserDetailsSync();
  @async
  Sync getIDSchemaSync();
  @async
  Sync getMasterDataSync();
  @async
  Sync getCaCertsSync();
  @async
  Sync getKernelCertsSync();
}
