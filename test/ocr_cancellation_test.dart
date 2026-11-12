import 'package:flutter_test/flutter_test.dart';
import 'package:registration_client/core/bridge/ocr_api.g.dart';
import 'package:registration_client/provider/ocr_scan_provider.dart';

void main() {
  TestWidgetsFlutterBinding.ensureInitialized();

  group('OCR Scan Cancellation & Late Callback Invalidation', () {
    test('late onOcrSuccess callback in cancelled/idle state is dropped', () {
      final provider = OcrScanProvider();
      expect(provider.state, OcrScanState.idle);

      // Simulate a late native success callback arriving after cancellation
      provider.onOcrSuccess(OcrResultMessage(
        documentType: 'Aadhaar',
        confidence: 0.95,
        data: {'name': 'Test Resident', 'dob': '1990/01/01'},
      ));

      // Provider must remain in idle state and not store extracted fields
      expect(provider.state, OcrScanState.idle);
      expect(provider.extractedData.isEmpty, isTrue);
      expect(provider.documentType, isNull);
    });

    test('late onOcrError callback in cancelled/idle state is dropped', () {
      final provider = OcrScanProvider();
      expect(provider.state, OcrScanState.idle);

      // Simulate a late native error callback arriving after cancellation
      provider.onOcrError(OcrErrorMessage(
        errorCode: 'TIMEOUT',
        message: 'Timeout occurred after cancel',
        isRetryable: true,
      ));

      // Provider must remain in idle state and not transition to error
      expect(provider.state, OcrScanState.idle);
      expect(provider.errorMessage, isNull);
      expect(provider.errorCode, isNull);
    });
  });
}
