import 'package:registration_client/pigeon/sync_pigeon.dart';

abstract class SyncResponse{
  Future<SyncTime> getLastSyncTime();
  Future<Sync> getCertificateSync();
  Future<Sync> getGlobalParamsSync();
  Future<Sync> getUserDetailsSync();
  Future<Sync> getIDSchemaSync();
  Future<Sync> getMasterDataSync();
}