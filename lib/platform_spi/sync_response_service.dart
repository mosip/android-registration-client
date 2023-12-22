import 'package:registration_client/pigeon/master_data_sync_pigeon.dart';

abstract class SyncResponseService {
  Future<SyncTime> getLastSyncTime();
  Future<Sync> getPolicyKeySync();
  Future<Sync> getGlobalParamsSync();
  Future<Sync> getUserDetailsSync();
  Future<Sync> getIDSchemaSync();
  Future<Sync> getMasterDataSync();
  Future<Sync> getCaCertsSync();
}