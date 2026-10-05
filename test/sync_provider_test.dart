import 'package:flutter_test/flutter_test.dart';
import 'package:registration_client/provider/sync_provider.dart';
import 'package:shared_preferences/shared_preferences.dart';

void main() {
  TestWidgetsFlutterBinding.ensureInitialized();

  const manualTime = '2026-09-30T05:00:00.000Z';
  const laterNativeTime = '2026-09-30T06:30:00.000Z';
  const earlierNativeTime = '2026-09-30T04:00:00.000Z';

  Future<SyncProvider> providerWithManualTime(String? manual) async {
    SharedPreferences.setMockInitialValues(
        manual == null ? {} : {'last_synchronise_data_timestamp': manual});
    final provider = SyncProvider();
    await provider.loadLastSyncTimes();
    return provider;
  }

  test('auto sync after a manual sync updates the tile (#1122)', () async {
    final provider = await providerWithManualTime(manualTime);
    provider.setLastSuccessfulSyncTime(laterNativeTime);

    expect(provider.lastMasterDataSyncTime, laterNativeTime);
  });

  test('keeps the manual time when it is the newer one', () async {
    final provider = await providerWithManualTime(manualTime);
    provider.setLastSuccessfulSyncTime(earlierNativeTime);

    expect(provider.lastMasterDataSyncTime, manualTime);
  });

  test('falls back to the native time when there was no manual sync', () async {
    final provider = await providerWithManualTime(null);
    provider.setLastSuccessfulSyncTime(laterNativeTime);

    expect(provider.lastMasterDataSyncTime, laterNativeTime);
  });

  test('returns empty after a remap reset instead of the raw null marker', () async {
    final provider = await providerWithManualTime(null);
    provider.setLastSuccessfulSyncTime('LastSyncTimeIsNull');

    expect(provider.lastMasterDataSyncTime, isEmpty);
  });

  test('keeps the manual time while the native time is the null marker', () async {
    final provider = await providerWithManualTime(manualTime);
    provider.setLastSuccessfulSyncTime('LastSyncTimeIsNull');

    expect(provider.lastMasterDataSyncTime, manualTime);
  });
}
