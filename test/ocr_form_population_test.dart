import 'package:flutter_test/flutter_test.dart';
import 'package:registration_client/core/bridge/ocr_api.g.dart';
import 'package:registration_client/model/field.dart';
import 'package:registration_client/provider/global_provider.dart';
import 'package:registration_client/provider/ocr_scan_provider.dart';
import 'package:registration_client/provider/registration_task_provider.dart';
import 'package:mockito/mockito.dart';

class MockGlobalProvider extends Mock implements GlobalProvider {
  final Map<String, dynamic> _fieldInputValue = {};
  final List<String> _chosenLang = ['eng', 'ara'];
  String ageGroup = '';

  @override
  Map<String, dynamic> get fieldInputValue => _fieldInputValue;

  @override
  List<String> get chosenLang => _chosenLang;

  @override
  String langToCode(String lang) => lang;

  @override
  void setInputMapValue(String key, dynamic value, Map<String, dynamic> map) {
    map[key] = value;
  }

  @override
  void setLanguageSpecificValue(String key, dynamic value, String langCode, Map<String, dynamic> map) {
    map['${key}_$langCode'] = value;
    map[key] = value;
  }
}

class MockRegistrationTaskProvider extends Mock implements RegistrationTaskProvider {
  final Map<String, String> addedDemographicFields = {};
  final Map<String, String> simpleTypeFields = {};
  final Map<String, String> selectedCodes = {};
  final List<Map<String, String>> setDateCalls = [];

  @override
  void addDemographicField(String fieldId, String value) {
    addedDemographicFields[fieldId] = value;
  }

  @override
  void addSimpleTypeDemographicField(String fieldId, String value, String langCode) {
    simpleTypeFields['${fieldId}_$langCode'] = value;
    simpleTypeFields[fieldId] = value;
  }

  @override
  void addSelectedCode(String fieldId, String code) {
    selectedCodes[fieldId] = code;
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

  group('OCR Form Population & Validation Pipeline', () {
    late MockGlobalProvider globalProvider;
    late MockRegistrationTaskProvider regTaskProvider;
    late OcrScanProvider provider;

    setUp(() {
      globalProvider = MockGlobalProvider();
      regTaskProvider = MockRegistrationTaskProvider();
      provider = OcrScanProvider();
    });

    OcrScanProvider createActiveProvider(Map<String, String> data, {String docType = 'National ID'}) {
      final p = OcrScanProvider();
      p.onQualityUpdate(ImageQualityMessage(
        guidanceMessage: 'Reading document...',
        isAcceptable: true,
      ));
      p.onOcrSuccess(OcrResultMessage(
        documentType: docType,
        confidence: 0.98,
        data: data,
      ));
      return p;
    }

    test('populates multi-language simpleType fields across all chosen languages', () {
      final p = createActiveProvider({
        'fullName': 'John Doe',
        'addressLine1': '123 Main Street',
      });

      final screenFields = [
        Field(id: 'fullName', controlType: 'textbox', subType: 'name', type: 'simpleType'),
        Field(id: 'addressLine1', controlType: 'textbox', subType: 'address', type: 'simpleType'),
      ];

      final applied = p.applyExtractedFields(
        screenFields: screenFields,
        globalProvider: globalProvider,
        regTaskProvider: regTaskProvider,
      );

      expect(applied, 2);
      expect(globalProvider.fieldInputValue['fullName'], 'John Doe');
      expect(globalProvider.fieldInputValue['fullName_eng'], 'John Doe');
      expect(globalProvider.fieldInputValue['fullName_ara'], 'John Doe');
      expect(regTaskProvider.simpleTypeFields['fullName_eng'], 'John Doe');
      expect(regTaskProvider.simpleTypeFields['fullName_ara'], 'John Doe');
      expect(globalProvider.fieldInputValue['addressLine1'], '123 Main Street');
    });

    test('resolves female gender variations to Female with FEM code', () {
      final p = createActiveProvider({'gender': 'FEMALE'});
      final screenFields = [
        Field(id: 'gender', controlType: 'dropdown', subType: 'gender', type: 'simpleType'),
      ];

      final applied = p.applyExtractedFields(
        screenFields: screenFields,
        globalProvider: globalProvider,
        regTaskProvider: regTaskProvider,
      );

      expect(applied, 1);
      expect(regTaskProvider.selectedCodes['gender'], 'FEM');
      expect(globalProvider.fieldInputValue['gender'], 'Female');
    });

    test('resolves male gender variations to Male with MLE code', () {
      final p = createActiveProvider({'gender': 'M'});
      final screenFields = [
        Field(id: 'gender', controlType: 'dropdown', subType: 'gender', type: 'simpleType'),
      ];

      final applied = p.applyExtractedFields(
        screenFields: screenFields,
        globalProvider: globalProvider,
        regTaskProvider: regTaskProvider,
      );

      expect(applied, 1);
      expect(regTaskProvider.selectedCodes['gender'], 'MLE');
      expect(globalProvider.fieldInputValue['gender'], 'Male');
    });

    test('resolves other gender variations to Other with OTH code', () {
      final p = createActiveProvider({'gender': 'TRANSGENDER'});
      final screenFields = [
        Field(id: 'gender', controlType: 'dropdown', subType: 'gender', type: 'simpleType'),
      ];

      final applied = p.applyExtractedFields(
        screenFields: screenFields,
        globalProvider: globalProvider,
        regTaskProvider: regTaskProvider,
      );

      expect(applied, 1);
      expect(regTaskProvider.selectedCodes['gender'], 'OTH');
      expect(globalProvider.fieldInputValue['gender'], 'Other');
    });

    test('resolves OCR keys with custom mapping from FieldSpec.ocrKey', () {
      final p = createActiveProvider({
        'national_id_name': 'Jane Smith',
        'dob_extracted': '1992/08/14',
      });

      final screenFields = [
        Field(id: 'fullName', ocrKey: 'national_id_name', controlType: 'textbox', subType: 'name', type: 'simpleType'),
        Field(id: 'dateOfBirth', ocrKey: 'dob_extracted', controlType: 'ageDate', subType: 'dateOfBirth', type: 'string'),
      ];

      final applied = p.applyExtractedFields(
        screenFields: screenFields,
        globalProvider: globalProvider,
        regTaskProvider: regTaskProvider,
      );

      expect(applied, 2);
      expect(globalProvider.fieldInputValue['fullName'], 'Jane Smith');
      expect(regTaskProvider.setDateCalls.length, 1);
      expect(regTaskProvider.setDateCalls.first['fieldId'], 'dateOfBirth');
      expect(regTaskProvider.setDateCalls.first['year'], '1992');
      expect(regTaskProvider.setDateCalls.first['month'], '08');
      expect(regTaskProvider.setDateCalls.first['day'], '14');
    });

    test('skips empty and whitespace-only extracted values', () {
      final p = createActiveProvider({
        'fullName': '   ',
        'city': '',
        'phone': '   ',
      });

      final screenFields = [
        Field(id: 'fullName', controlType: 'textbox', subType: 'name', type: 'simpleType'),
        Field(id: 'city', controlType: 'textbox', subType: 'city', type: 'simpleType'),
        Field(id: 'phone', controlType: 'textbox', subType: 'phone', type: 'simpleType'),
      ];

      final applied = p.applyExtractedFields(
        screenFields: screenFields,
        globalProvider: globalProvider,
        regTaskProvider: regTaskProvider,
      );

      expect(applied, 0);
      expect(globalProvider.fieldInputValue, isEmpty);
    });

    test('partially invalid payload applies valid fields and rejects invalid ones', () {
      final p = createActiveProvider({
        'fullName': 'Alice Wonderland',
        'dateOfBirth': '31/02/2023', // Invalid calendar date
        'phone': '+91 9876543210',   // Valid phone
      });

      final screenFields = [
        Field(id: 'fullName', controlType: 'textbox', subType: 'name', type: 'simpleType'),
        Field(id: 'dateOfBirth', controlType: 'ageDate', subType: 'dateOfBirth', type: 'string'),
        Field(id: 'phone', controlType: 'textbox', subType: 'phone', type: 'simpleType'),
      ];

      final applied = p.applyExtractedFields(
        screenFields: screenFields,
        globalProvider: globalProvider,
        regTaskProvider: regTaskProvider,
      );

      expect(applied, 2); // fullName and phone applied; invalid date skipped
      expect(globalProvider.fieldInputValue['fullName'], 'Alice Wonderland');
      expect(globalProvider.fieldInputValue['phone'], '+919876543210');
      expect(regTaskProvider.setDateCalls, isEmpty);
    });

    test('quality update transitions to processing state only for active reading phrases', () {
      final p = OcrScanProvider();
      expect(p.state, OcrScanState.idle);

      p.onQualityUpdate(ImageQualityMessage(guidanceMessage: 'Hold steady'));
      expect(p.state, OcrScanState.idle);
      expect(p.guidanceMessage, 'Hold steady');

      p.onQualityUpdate(ImageQualityMessage(guidanceMessage: 'Reading document...'));
      expect(p.state, OcrScanState.processing);

      p.onOcrError(OcrErrorMessage(
        errorCode: 'TIMEOUT',
        message: 'OCR recognition timed out',
      ));
      expect(p.state, OcrScanState.error);
      expect(p.errorMessage, 'OCR recognition timed out');

      p.reset();
      expect(p.state, OcrScanState.idle);
      expect(p.extractedData, isEmpty);
      expect(p.documentType, isNull);
    });
  });
}
