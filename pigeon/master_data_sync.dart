import 'package:pigeon/pigeon.dart';

class Sync {
  final String? syncType;
  final int? syncProgress;
  final String? errorCode;

  Sync({this.syncType, this.syncProgress, this.errorCode});
}

class SyncTime {
  final String? syncTime;

  SyncTime({this.syncTime});
}

@HostApi()
abstract class SyncApi {
  @async
  SyncTime getLastSyncTime();

  @async
  Sync getPolicyKeySync(bool isManualSync, String jobId);

  @async
  Sync getGlobalParamsSync(bool isManualSync, String jobId);

  @async
  Sync getUserDetailsSync(bool isManualSync, String jobId);

  @async
  Sync getIDSchemaSync(bool isManualSync);

  @async
  Sync getMasterDataSync(bool isManualSync, String jobId);

  @async
  Sync getCaCertsSync(bool isManualSync, String jobId);

  @async
  String batchJob();

  @async
  List<String> getReasonList(String langCode);

  @async
  String getPreRegIds(String jobId);
  @async
  Sync getKernelCertsSync(bool isManualSync, String jobId);
  @async
  bool getSyncAndUploadInProgressStatus();
  @async
  bool deleteAuditLogsNative(String jobId);
  @async
  bool deletePreRegRecords();
  @async
  String getLastSyncTimeByJobId(String jobId);
  @async
  String getNextSyncTimeByJobId(String jobId);
}
