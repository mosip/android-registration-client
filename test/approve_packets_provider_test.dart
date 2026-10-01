import 'package:flutter/services.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:registration_client/pigeon/master_data_sync_pigeon.dart';
import 'package:registration_client/provider/approve_packets_provider.dart';

const _reasonChannel =
    'dev.flutter.pigeon.registration_client.SyncApi.getReasonList';

void main() {
  TestWidgetsFlutterBinding.ensureInitialized();

  void mockReasons(List<String> reasons) {
    TestDefaultBinaryMessengerBinding.instance.defaultBinaryMessenger
        .setMockMessageHandler(_reasonChannel, (ByteData? message) async {
      return SyncApi.codec.encodeMessage(<Object?>[reasons]);
    });
  }

  tearDown(() {
    TestDefaultBinaryMessengerBinding.instance.defaultBinaryMessenger
        .setMockMessageHandler(_reasonChannel, null);
  });

  test('getAllReasonList removes duplicate reasons, keeping order', () async {
    mockReasons(['Invalid Data', 'Poor Photo', 'Invalid Data', 'Poor Photo']);
    final provider = ApprovePacketsProvider();

    provider.getAllReasonList('eng');
    await pumpEventQueue();

    expect(provider.reasonList, ['Invalid Data', 'Poor Photo']);
  });

  test('getAllReasonList keeps a selected reason that is still listed', () async {
    mockReasons(['Invalid Data', 'Poor Photo']);
    final provider = ApprovePacketsProvider()..selectedReason = 'Poor Photo';

    provider.getAllReasonList('eng');
    await pumpEventQueue();

    expect(provider.selectedReason, 'Poor Photo');
  });

  test('getAllReasonList clears a selected reason that is no longer listed', () async {
    mockReasons(['Données invalides']);
    final provider = ApprovePacketsProvider()..selectedReason = 'Invalid Data';

    provider.getAllReasonList('fra');
    await pumpEventQueue();

    expect(provider.selectedReason, isNull);
  });
}
