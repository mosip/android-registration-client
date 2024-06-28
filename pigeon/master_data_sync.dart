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
  Sync getPolicyKeySync(bool isManualSync);
  @async
  Sync getGlobalParamsSync(bool isManualSync);
  @async
  Sync getUserDetailsSync(bool isManualSync);
  @async
  Sync getIDSchemaSync(bool isManualSync);
  @async
  Sync getMasterDataSync(bool isManualSync);
  @async
  Sync getCaCertsSync(bool isManualSync);
  @async
  String batchJob();
  @async
  Sync getKernelCertsSync(bool isManualSync);
  @async
  bool getSyncAndUploadInProgressStatus();
}
