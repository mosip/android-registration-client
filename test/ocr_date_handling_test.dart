import 'package:flutter_test/flutter_test.dart';
import 'package:intl/intl.dart';
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
}

class MockRegistrationTaskProvider extends Mock implements RegistrationTaskProvider {
  final Map<String, String> addedDemographicFields = {};
  final List<Map<String, String>> setDateCalls = [];

  @override
  void addDemographicField(String fieldId, String value) {
    addedDemographicFields[fieldId] = value;
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

  group('OCR Date Validation & Population', () {
    late MockGlobalProvider globalProvider;
    late MockRegistrationTaskProvider regTaskProvider;

    setUp(() {
      globalProvider = MockGlobalProvider();
      regTaskProvider = MockRegistrationTaskProvider();
    });

    test('valid past date is routed through setDateField, not addDemographicField', () {
      final provider = OcrScanProvider();
      // Set extracted data via simulate or internal helper
      provider.onQualityUpdate(ImageQualityMessage(guidanceMessage: 'Reading document...'));
      provider.onOcrSuccess(OcrResultMessage(
        documentType: 'Aadhaar',
        confidence: 0.95,
        data: {'dateOfBirth': '1995/05/20'},
      ));

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
      expect(regTaskProvider.setDateCalls.first['fieldId'], 'dateOfBirth');
      expect(regTaskProvider.setDateCalls.first['day'], '20');
      expect(regTaskProvider.setDateCalls.first['month'], '05');
      expect(regTaskProvider.setDateCalls.first['year'], '1995');
      // Must NOT be stored via addDemographicField
      expect(regTaskProvider.addedDemographicFields.containsKey('dateOfBirth'), isFalse);
      expect(globalProvider.fieldInputValue['dateOfBirth'], '1995/05/20');
    });

    test('valid leap day (2024/02/29) is accepted', () {
      final provider = OcrScanProvider();
      provider.onQualityUpdate(ImageQualityMessage(guidanceMessage: 'Reading document...'));
      provider.onOcrSuccess(OcrResultMessage(
        documentType: 'Aadhaar',
        confidence: 0.95,
        data: {'dateOfBirth': '2024/02/29'},
      ));

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
      expect(regTaskProvider.setDateCalls.first['day'], '29');
      expect(regTaskProvider.setDateCalls.first['month'], '02');
      expect(regTaskProvider.setDateCalls.first['year'], '2024');
    });

    test('invalid leap day in non-leap year (2023/02/29) is rejected', () {
      final provider = OcrScanProvider();
      provider.onQualityUpdate(ImageQualityMessage(guidanceMessage: 'Reading document...'));
      provider.onOcrSuccess(OcrResultMessage(
        documentType: 'Aadhaar',
        confidence: 0.95,
        data: {'dateOfBirth': '2023/02/29'},
      ));

      final screenFields = [
        Field(id: 'dateOfBirth', controlType: 'ageDate', subType: 'dateOfBirth', type: 'string'),
      ];

      final applied = provider.applyExtractedFields(
        screenFields: screenFields,
        globalProvider: globalProvider,
        regTaskProvider: regTaskProvider,
      );

      // Must be rejected
      expect(applied, 0);
      expect(regTaskProvider.setDateCalls.isEmpty, isTrue);
      expect(globalProvider.fieldInputValue.containsKey('dateOfBirth'), isFalse);
    });

    test('impossible calendar date (2023/04/31) is rejected', () {
      final provider = OcrScanProvider();
      provider.onQualityUpdate(ImageQualityMessage(guidanceMessage: 'Reading document...'));
      provider.onOcrSuccess(OcrResultMessage(
        documentType: 'Aadhaar',
        confidence: 0.95,
        data: {'dateOfBirth': '2023/04/31'},
      ));

      final screenFields = [
        Field(id: 'dateOfBirth', controlType: 'ageDate', subType: 'dateOfBirth', type: 'string'),
      ];

      final applied = provider.applyExtractedFields(
        screenFields: screenFields,
        globalProvider: globalProvider,
        regTaskProvider: regTaskProvider,
      );

      expect(applied, 0);
      expect(regTaskProvider.setDateCalls.isEmpty, isTrue);
    });

    test('future date of birth (2035/10/15) is rejected', () {
      final provider = OcrScanProvider();
      provider.onQualityUpdate(ImageQualityMessage(guidanceMessage: 'Reading document...'));
      provider.onOcrSuccess(OcrResultMessage(
        documentType: 'Aadhaar',
        confidence: 0.95,
        data: {'dateOfBirth': '2035/10/15'},
      ));

      final screenFields = [
        Field(id: 'dateOfBirth', controlType: 'ageDate', subType: 'dateOfBirth', type: 'string'),
      ];

      final applied = provider.applyExtractedFields(
        screenFields: screenFields,
        globalProvider: globalProvider,
        regTaskProvider: regTaskProvider,
      );

      expect(applied, 0);
      expect(regTaskProvider.setDateCalls.isEmpty, isTrue);
      expect(globalProvider.fieldInputValue.containsKey('dateOfBirth'), isFalse);
    });

    test('calculateYearDifference correctly computes positive age without abs()', () {
      int calculateYearDifference(DateTime date1, DateTime date2) {
        int yearDifference = date2.year - date1.year;
        if (date1.month > date2.month ||
            (date1.month == date2.month && date1.day > date2.day)) {
          yearDifference--;
        }
        return yearDifference;
      }

      final pastDate = DateTime(2000, 1, 1);
      final refDate = DateTime(2026, 1, 1);
      final age = calculateYearDifference(pastDate, refDate);
      expect(age, 26);

      // Future date results in negative, ensuring no positive age is computed without abs()
      final futureDate = DateTime(2030, 1, 1);
      final futureDiff = calculateYearDifference(futureDate, refDate);
      expect(futureDiff, lessThan(0));
    });
  });
}
