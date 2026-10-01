import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutter_gen/gen_l10n/app_localizations.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:provider/provider.dart';
import 'package:registration_client/pigeon/master_data_sync_pigeon.dart';
import 'package:registration_client/provider/approve_packets_provider.dart';
import 'package:registration_client/ui/approve_packet/widget/reject_dialogbox.dart';

const _reasonChannel =
    'dev.flutter.pigeon.registration_client.SyncApi.getReasonList';

// What the native side returned before the fix: every full sync re-inserted the reasons.
const _duplicatedReasons = [
  'Invalid Data',
  'Poor Photo',
  'Invalid Data',
  'Poor Photo',
];

Widget _rejectDialog(ApprovePacketsProvider provider) {
  return ChangeNotifierProvider<ApprovePacketsProvider>.value(
    value: provider,
    child: MaterialApp(
      localizationsDelegates: AppLocalizations.localizationsDelegates,
      supportedLocales: AppLocalizations.supportedLocales,
      home: Builder(builder: (context) => dialogBox(() {}, context)),
    ),
  );
}

Future<void> _selectReason(WidgetTester tester, String reason) async {
  await tester.tap(find.byType(DropdownButton<String>));
  await tester.pumpAndSettle();
  await tester.tap(find.text(reason).last);
  await tester.pumpAndSettle();
}

void main() {
  TestWidgetsFlutterBinding.ensureInitialized();

  setUp(() {
    TestDefaultBinaryMessengerBinding.instance.defaultBinaryMessenger
        .setMockMessageHandler(_reasonChannel, (ByteData? message) async {
      return SyncApi.codec.encodeMessage(<Object?>[_duplicatedReasons]);
    });
  });

  tearDown(() {
    TestDefaultBinaryMessengerBinding.instance.defaultBinaryMessenger
        .setMockMessageHandler(_reasonChannel, null);
  });

  testWidgets('#1123 repro: duplicate reasons trip the DropdownButton assertion',
      (tester) async {
    // Reason list as it reached the dialog before the fix.
    final provider = ApprovePacketsProvider()..reasonList = _duplicatedReasons;

    await tester.pumpWidget(_rejectDialog(provider));
    await _selectReason(tester, 'Invalid Data');

    final error = tester.takeException();
    expect(error, isA<AssertionError>());
    expect(error.toString(), contains('DropdownButton'));
  });

  testWidgets('#1123 fix: reasons loaded through the provider can be selected',
      (tester) async {
    final provider = ApprovePacketsProvider();
    provider.getAllReasonList('eng');
    await tester.pumpAndSettle();

    await tester.pumpWidget(_rejectDialog(provider));
    await _selectReason(tester, 'Invalid Data');

    expect(tester.takeException(), isNull);
    expect(provider.selectedReason, 'Invalid Data');
    expect(find.text('Invalid Data'), findsOneWidget);
  });
}
