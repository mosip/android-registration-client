import 'package:flutter_test/flutter_test.dart';
import 'package:registration_client/core/bridge/ocr_api.g.dart';
import 'package:registration_client/model/field.dart';
import 'package:registration_client/provider/global_provider.dart';
import 'package:registration_client/provider/ocr_scan_provider.dart';
import 'package:registration_client/provider/registration_task_provider.dart';
import 'package:mockito/mockito.dart';

class MockGlobalProvider extends Mock implements GlobalProvider {
  final Map<String, dynamic> _fieldInputValue = {};

  @override
  Map<String, dynamic> get fieldInputValue => _fieldInputValue;

  @override
  List<String> get chosenLang => ['eng'];

  @override
  String langToCode(String lang) => 'eng';

  @override
  void setInputMapValue(String key, dynamic value, Map<String, dynamic> map) {
    map[key] = value;
  }

  @override
  void setLanguageSpecificValue(String key, dynamic value, String langCode, Map<String, dynamic> map) {
    map[key] = value;
  }
}

class MockRegistrationTaskProvider extends Mock implements RegistrationTaskProvider {
  final Map<String, String> addedDemographicFields = {};
  final Map<String, String> simpleTypeDemographicFields = {};
  final List<Map<String, String>> setDateCalls = [];

  @override
  void addDemographicField(String fieldId, String value) {
    addedDemographicFields[fieldId] = value;
  }

  @override
  void addSimpleTypeDemographicField(String fieldId, String value, String langCode) {
    simpleTypeDemographicFields[fieldId] = value;
  }

  @override
  dynamic setDateField(String fieldId, String subType, String day, String month, String year) {
    setDateCalls.add({
      'fieldId': fieldId,
      'subType': subType,
      'day': day,
      'month': month,
      'year': year,
    });
    return Future.value();
  }
}

void main() {
  TestWidgetsFlutterBinding.ensureInitialized();

  group('OCR Phone Normalization & Validation', () {
    late MockGlobalProvider globalProvider;
    late MockRegistrationTaskProvider regTaskProvider;

    setUp(() {
      globalProvider = MockGlobalProvider();
      regTaskProvider = MockRegistrationTaskProvider();
    });

    OcrScanProvider createActiveProviderWithData(Map<String, String> data) {
      final provider = OcrScanProvider();
      provider.onQualityUpdate(ImageQualityMessage(guidanceMessage: 'Reading document...'));
      provider.onOcrSuccess(OcrResultMessage(
        documentType: 'ID',
        confidence: 0.95,
        data: data,
      ));
      return provider;
    }

    test('phone with hyphens and international prefix is normalized', () {
      final provider = createActiveProviderWithData({'phone': '+91-9876543210'});
      final screenFields = [
        Field(id: 'phone', controlType: 'textbox', subType: 'phone', type: 'simpleType'),
      ];

      final applied = provider.applyExtractedFields(
        screenFields: screenFields,
        globalProvider: globalProvider,
        regTaskProvider: regTaskProvider,
      );

      expect(applied, 1);
      expect(globalProvider.fieldInputValue['phone'], '+919876543210');
    });

    test('already normalized phone number with + is idempotent and does not produce ++', () {
      final provider = createActiveProviderWithData({'phone': '+919876543210'});
      final screenFields = [
        Field(id: 'phone', controlType: 'textbox', subType: 'phone', type: 'simpleType'),
      ];

      final applied = provider.applyExtractedFields(
        screenFields: screenFields,
        globalProvider: globalProvider,
        regTaskProvider: regTaskProvider,
      );

      expect(applied, 1);
      expect(globalProvider.fieldInputValue['phone'], '+919876543210');
      expect(globalProvider.fieldInputValue['phone'].toString().startsWith('++'), isFalse);
    });

    test('phone number with corrupted duplicate ++ is normalized to single +', () {
      final provider = createActiveProviderWithData({'phone': '++919876543210'});
      final screenFields = [
        Field(id: 'phone', controlType: 'textbox', subType: 'phone', type: 'simpleType'),
      ];

      final applied = provider.applyExtractedFields(
        screenFields: screenFields,
        globalProvider: globalProvider,
        regTaskProvider: regTaskProvider,
      );

      expect(applied, 1);
      expect(globalProvider.fieldInputValue['phone'], '+919876543210');
    });

    test('local phone number without + preserves digits without adding +', () {
      final provider = createActiveProviderWithData({'phone': '0712345678'});
      final screenFields = [
        Field(id: 'phone', controlType: 'textbox', subType: 'phone', type: 'simpleType'),
      ];

      final applied = provider.applyExtractedFields(
        screenFields: screenFields,
        globalProvider: globalProvider,
        regTaskProvider: regTaskProvider,
      );

      expect(applied, 1);
      expect(globalProvider.fieldInputValue['phone'], '0712345678');
    });

    test('phone number with brackets and spaces is cleanly normalized', () {
      final provider = createActiveProviderWithData({'phone': '+1 (555) 123-4567'});
      final screenFields = [
        Field(id: 'phone', controlType: 'textbox', subType: 'phone', type: 'simpleType'),
      ];

      final applied = provider.applyExtractedFields(
        screenFields: screenFields,
        globalProvider: globalProvider,
        regTaskProvider: regTaskProvider,
      );

      expect(applied, 1);
      expect(globalProvider.fieldInputValue['phone'], '+15551234567');
    });

    test('phone number with colon noise is cleanly normalized', () {
      final provider = createActiveProviderWithData({'phone': ': +91 9876543210'});
      final screenFields = [
        Field(id: 'phone', controlType: 'textbox', subType: 'phone', type: 'simpleType'),
      ];

      final applied = provider.applyExtractedFields(
        screenFields: screenFields,
        globalProvider: globalProvider,
        regTaskProvider: regTaskProvider,
      );

      expect(applied, 1);
      expect(globalProvider.fieldInputValue['phone'], '+919876543210');
    });

    test('too short phone number (< 7 digits) is rejected', () {
      final provider = createActiveProviderWithData({'phone': '12345'});
      final screenFields = [
        Field(id: 'phone', controlType: 'textbox', subType: 'phone', type: 'simpleType'),
      ];

      final applied = provider.applyExtractedFields(
        screenFields: screenFields,
        globalProvider: globalProvider,
        regTaskProvider: regTaskProvider,
      );

      expect(applied, 0);
      expect(globalProvider.fieldInputValue.containsKey('phone'), isFalse);
    });

    test('too long phone number (> 15 digits) is rejected', () {
      final provider = createActiveProviderWithData({'phone': '1234567890123456'});
      final screenFields = [
        Field(id: 'phone', controlType: 'textbox', subType: 'phone', type: 'simpleType'),
      ];

      final applied = provider.applyExtractedFields(
        screenFields: screenFields,
        globalProvider: globalProvider,
        regTaskProvider: regTaskProvider,
      );

      expect(applied, 0);
      expect(globalProvider.fieldInputValue.containsKey('phone'), isFalse);
    });

    test('non-numeric phone noise is rejected', () {
      final provider = createActiveProviderWithData({'phone': '+++'});
      final screenFields = [
        Field(id: 'phone', controlType: 'textbox', subType: 'phone', type: 'simpleType'),
      ];

      final applied = provider.applyExtractedFields(
        screenFields: screenFields,
        globalProvider: globalProvider,
        regTaskProvider: regTaskProvider,
      );

      expect(applied, 0);
      expect(globalProvider.fieldInputValue.containsKey('phone'), isFalse);
    });
  });

  group('OCR Date Strict Calendar & Two-Digit Year Rejection', () {
    late MockGlobalProvider globalProvider;
    late MockRegistrationTaskProvider regTaskProvider;

    setUp(() {
      globalProvider = MockGlobalProvider();
      regTaskProvider = MockRegistrationTaskProvider();
    });

    OcrScanProvider createActiveProviderWithData(Map<String, String> data) {
      final provider = OcrScanProvider();
      provider.onQualityUpdate(ImageQualityMessage(guidanceMessage: 'Reading document...'));
      provider.onOcrSuccess(OcrResultMessage(
        documentType: 'ID',
        confidence: 0.95,
        data: data,
      ));
      return provider;
    }

    test('ambiguous two-digit year (12/05/23) is rejected', () {
      final provider = createActiveProviderWithData({'dateOfBirth': '12/05/23'});
      final screenFields = [
        Field(id: 'dateOfBirth', controlType: 'ageDate', subType: 'dateOfBirth', type: 'string'),
      ];

      final applied = provider.applyExtractedFields(
        screenFields: screenFields,
        globalProvider: globalProvider,
        regTaskProvider: regTaskProvider,
      );

      expect(applied, 0);
      expect(regTaskProvider.setDateCalls, isEmpty);
    });

    test('ambiguous two-digit year (12/05/85) is rejected', () {
      final provider = createActiveProviderWithData({'dateOfBirth': '12/05/85'});
      final screenFields = [
        Field(id: 'dateOfBirth', controlType: 'ageDate', subType: 'dateOfBirth', type: 'string'),
      ];

      final applied = provider.applyExtractedFields(
        screenFields: screenFields,
        globalProvider: globalProvider,
        regTaskProvider: regTaskProvider,
      );

      expect(applied, 0);
      expect(regTaskProvider.setDateCalls, isEmpty);
    });

    test('impossible calendar date (31/02/2023) is rejected', () {
      final provider = createActiveProviderWithData({'dateOfBirth': '31/02/2023'});
      final screenFields = [
        Field(id: 'dateOfBirth', controlType: 'ageDate', subType: 'dateOfBirth', type: 'string'),
      ];

      final applied = provider.applyExtractedFields(
        screenFields: screenFields,
        globalProvider: globalProvider,
        regTaskProvider: regTaskProvider,
      );

      expect(applied, 0);
      expect(regTaskProvider.setDateCalls, isEmpty);
    });

    test('non-leap day (29/02/2023) is rejected', () {
      final provider = createActiveProviderWithData({'dateOfBirth': '29/02/2023'});
      final screenFields = [
        Field(id: 'dateOfBirth', controlType: 'ageDate', subType: 'dateOfBirth', type: 'string'),
      ];

      final applied = provider.applyExtractedFields(
        screenFields: screenFields,
        globalProvider: globalProvider,
        regTaskProvider: regTaskProvider,
      );

      expect(applied, 0);
      expect(regTaskProvider.setDateCalls, isEmpty);
    });

    test('valid leap day (29/02/2024) is accepted', () {
      final provider = createActiveProviderWithData({'dateOfBirth': '2024/02/29'});
      final screenFields = [
        Field(id: 'dateOfBirth', controlType: 'ageDate', subType: 'dateOfBirth', type: 'string'),
      ];

      final applied = provider.applyExtractedFields(
        screenFields: screenFields,
        globalProvider: globalProvider,
        regTaskProvider: regTaskProvider,
      );

      expect(applied, 1);
      expect(regTaskProvider.setDateCalls.length, 1);
      expect(regTaskProvider.setDateCalls.first['year'], '2024');
      expect(regTaskProvider.setDateCalls.first['month'], '02');
      expect(regTaskProvider.setDateCalls.first['day'], '29');
    });

    test('future date (15/10/2035) is rejected', () {
      final provider = createActiveProviderWithData({'dateOfBirth': '2035/10/15'});
      final screenFields = [
        Field(id: 'dateOfBirth', controlType: 'ageDate', subType: 'dateOfBirth', type: 'string'),
      ];

      final applied = provider.applyExtractedFields(
        screenFields: screenFields,
        globalProvider: globalProvider,
        regTaskProvider: regTaskProvider,
      );

      expect(applied, 0);
      expect(regTaskProvider.setDateCalls, isEmpty);
    });
  });
}
