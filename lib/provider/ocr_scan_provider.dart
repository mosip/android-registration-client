/*
 * Copyright (c) Modular Open Source Identity Platform
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

import 'dart:developer';

import 'package:flutter/material.dart';
import 'package:intl/intl.dart';
import 'package:permission_handler/permission_handler.dart';
import 'package:registration_client/core/bridge/ocr_api.g.dart';
import 'package:registration_client/model/field.dart';
import 'package:registration_client/pigeon/global_config_settings_pigeon.dart';
import 'package:registration_client/pigeon/transliteration_pigeon.dart';
import 'package:registration_client/platform_android/transliteration_service_impl.dart';
import 'package:registration_client/provider/global_provider.dart';
import 'package:registration_client/provider/registration_task_provider.dart';

/// The possible states of the OCR scan lifecycle.
enum OcrScanState {
  /// No scan active — button is ready.
  idle,

  /// Camera live, quality updates streaming from native.
  scanning,

  /// Image captured, OCR engine is processing.
  processing,

  /// Extraction complete, fields are ready to apply.
  success,

  /// Something failed (may or may not be retryable).
  error,
}

/// How the current (or most recent) OCR session was triggered.
enum OcrInputSource {
  /// Triggered via the live camera scan flow.
  camera,

  /// Triggered by uploading an image file from storage.
  upload,
}

/// Provider that manages the entire OCR scan lifecycle.
///
/// Implements [OcrFlutterApi] to receive callbacks from the native side
/// (quality updates, success, error) and wraps [OcrHostApi] to send
/// commands (start, cancel, force capture).
class OcrScanProvider extends ChangeNotifier implements OcrFlutterApi {
  // ---------------------------------------------------------------------------
  // Pigeon bridges
  // ---------------------------------------------------------------------------
  final OcrHostApi _hostApi = OcrHostApi();

  // ---------------------------------------------------------------------------
  // OCR enabled flag
  // ---------------------------------------------------------------------------
  bool _isOcrEnabled = false;
  bool get isOcrEnabled => _isOcrEnabled;

  bool _isOcrEnabledLoaded = false;
  bool get isOcrEnabledLoaded => _isOcrEnabledLoaded;

  // ---------------------------------------------------------------------------
  // Scan state
  // ---------------------------------------------------------------------------
  OcrScanState _state = OcrScanState.idle;
  OcrScanState get state => _state;

  int? _textureId;
  int? get textureId => _textureId;

  OcrInputSource _inputSource = OcrInputSource.camera;
  OcrInputSource get inputSource => _inputSource;

  // ---------------------------------------------------------------------------
  // Quality guidance
  // ---------------------------------------------------------------------------
  String _guidanceMessage = '';
  String get guidanceMessage => _guidanceMessage;

  bool _isQualityAcceptable = false;
  bool get isQualityAcceptable => _isQualityAcceptable;

  bool _showForceCapture = false;
  bool get showForceCapture => _showForceCapture;

  int _qualityRetryCount = 0;
  static const int _maxQualityRetries = 8;

  // ---------------------------------------------------------------------------
  // Result data
  // ---------------------------------------------------------------------------
  Map<String, String> _extractedData = {};
  Map<String, String> get extractedData => _extractedData;

  String? _documentType;
  String? get documentType => _documentType;

  String get formattedDocumentType {
    if (_documentType == null || _documentType!.isEmpty || _documentType == 'UNKNOWN') {
      return 'document';
    }
    switch (_documentType!.toLowerCase()) {
      case 'aadhar_front':
        return 'Aadhaar (Front)';
      case 'aadhar_back':
        return 'Aadhaar (Back)';
      case 'driving_license_front':
        return 'Driving License (Front)';
      case 'driving_license_back':
        return 'Driving License (Back)';
      case 'pan_card_front':
        return 'PAN Card';
      case 'passport':
        return 'Passport';
      case 'voter_id':
        return 'Voter ID';
      default:
        return _documentType!.replaceAll('_', ' ');
    }
  }

  double? _confidence;
  double? get confidence => _confidence;

  double? get confidencePercentage {
    if (_confidence == null) return null;
    return _confidence! <= 1.0 ? _confidence! * 100 : _confidence!;
  }

  // ---------------------------------------------------------------------------
  // Error data
  // ---------------------------------------------------------------------------
  String? _errorMessage;
  String? get errorMessage => _errorMessage;

  String? _errorCode;
  String? get errorCode => _errorCode;

  bool _isRetryable = false;
  bool get isRetryable => _isRetryable;

  bool _isPermanentlyDenied = false;
  bool get isPermanentlyDenied => _isPermanentlyDenied;

  // ---------------------------------------------------------------------------
  // Constructor & setup
  // ---------------------------------------------------------------------------
  OcrScanProvider() {
    OcrFlutterApi.setup(this);
    _fetchOcrEnabled();
  }

  Future<void> _fetchOcrEnabled() async {
    try {
      final params =
          await GlobalConfigSettingsApi().getRegistrationParams();
      final value = params['mosip.registration.ocr.enabled'];
      _isOcrEnabled = value?.toString().toLowerCase() == 'true';
    } catch (e) {
      log('OcrScanProvider: Failed to fetch ocr.enabled: $e');
      _isOcrEnabled = false;
    }
    _isOcrEnabledLoaded = true;
    notifyListeners();
  }

  // ---------------------------------------------------------------------------
  // Commands (Flutter → Native)
  // ---------------------------------------------------------------------------

  /// Starts a document scan. Opens the camera on the native side and returns
  /// a texture ID that Flutter can use to display a live preview.
  Future<void> startScan() async {
    // Reset state
    _state = OcrScanState.scanning;
    _guidanceMessage = '';
    _isQualityAcceptable = false;
    _showForceCapture = false;
    _qualityRetryCount = 0;
    _extractedData = {};
    _documentType = null;
    _confidence = null;
    _errorMessage = null;
    _errorCode = null;
    _isRetryable = false;
    _inputSource = OcrInputSource.camera;
    notifyListeners();

    // Check and request camera permission before accessing hardware
    var status = await Permission.camera.status;
    if (status.isPermanentlyDenied) {
      _state = OcrScanState.error;
      _errorMessage =
          'Camera permission is permanently denied. Please enable it in app Settings.';
      _errorCode = 'CAMERA_PERMISSION_DENIED';
      _isRetryable = false;
      _isPermanentlyDenied = true;
      notifyListeners();
      return;
    }
    if (!status.isGranted) {
      status = await Permission.camera.request();
    }
    if (!status.isGranted) {
      _state = OcrScanState.error;
      _isPermanentlyDenied = status.isPermanentlyDenied;
      _errorMessage = status.isPermanentlyDenied
          ? 'Camera permission is permanently denied. Please enable it in app Settings.'
          : 'Camera permission is required to scan documents. Please grant the permission when prompted.';
      _errorCode = 'CAMERA_PERMISSION_DENIED';
      _isRetryable = !status.isPermanentlyDenied;
      notifyListeners();
      return;
    }

    try {
      final id = await _hostApi.startDocumentScan();
      _textureId = id;
      notifyListeners();
    } catch (e) {
      log('OcrScanProvider: startDocumentScan failed: $e');
      _state = OcrScanState.error;
      _errorMessage = 'Failed to start camera: $e';
      _errorCode = 'CAMERA_START_FAILED';
      _isRetryable = true;
      notifyListeners();
    }
  }

  /// Cancels the active scan session.
  Future<void> cancelScan() async {
    try {
      await _hostApi.cancelScan();
    } catch (e) {
      log('OcrScanProvider: cancelScan failed: $e');
    }
    _state = OcrScanState.idle;
    _textureId = null;
    notifyListeners();
  }

  /// Forces a capture regardless of quality.
  Future<void> forceCapture() async {
    _state = OcrScanState.processing;
    _guidanceMessage = 'Processing document...';
    notifyListeners();

    try {
      await _hostApi.forceCapture();
    } catch (e) {
      log('OcrScanProvider: forceCapture failed: $e');
      _state = OcrScanState.error;
      _errorMessage = 'Force capture failed: $e';
      _errorCode = 'FORCE_CAPTURE_FAILED';
      _isRetryable = true;
      notifyListeners();
    }
  }

  void reset() {
    _state = OcrScanState.idle;
    _textureId = null;
    _guidanceMessage = '';
    _isQualityAcceptable = false;
    _showForceCapture = false;
    _qualityRetryCount = 0;
    _extractedData = {};
    _documentType = null;
    _confidence = null;
    _errorMessage = null;
    _errorCode = null;
    _isRetryable = false;
    _isPermanentlyDenied = false;
    _inputSource = OcrInputSource.camera;
    notifyListeners();
  }

  /// Opens the app settings so the user can manually enable camera permission.
  Future<void> openSettings() async {
    await openAppSettings();
  }

  Future<void> openAppSettingsMenu() async {
    await openAppSettings();
  }

  Future<void> uploadDocument(String filePath) async {
    _state = OcrScanState.processing;
    _inputSource = OcrInputSource.upload;
    _guidanceMessage = 'Analysing document...';
    _extractedData = {};
    _documentType = null;
    _confidence = null;
    _errorMessage = null;
    _errorCode = null;
    _isRetryable = false;
    notifyListeners();

    try {
      await _hostApi.processImageFile(filePath);
      // Actual result arrives asynchronously via onOcrSuccess / onOcrError
    } catch (e) {
      log('OcrScanProvider: processImageFile Pigeon call failed: $e');
      _state = OcrScanState.error;
      _errorMessage = 'Failed to send image to OCR engine: $e';
      _errorCode = 'UPLOAD_PROCESS_FAILED';
      _isRetryable = true;
      notifyListeners();
    }
  }

  // ---------------------------------------------------------------------------
  // Callbacks (Native → Flutter) — OcrFlutterApi implementation
  // ---------------------------------------------------------------------------

  @override
  void onQualityUpdate(ImageQualityMessage quality) {
    _guidanceMessage = quality.guidanceMessage ?? '';
    _isQualityAcceptable = quality.isAcceptable ?? false;

    // Transition to processing state only when actual document reading / extraction has begun
    if (_guidanceMessage == 'Reading document...' ||
        _guidanceMessage == 'Processing document...' ||
        _guidanceMessage == 'Analysing document...') {
      _state = OcrScanState.processing;
    } else {
      if (_state == OcrScanState.processing) {
        // If quality updates continue during camera scan, ensure we stay in scanning state
        _state = OcrScanState.scanning;
      }
      if (!_isQualityAcceptable) {
        _qualityRetryCount++;
        if (_qualityRetryCount >= _maxQualityRetries) {
          _showForceCapture = true;
        }
      }
    }

    if (_guidanceMessage.contains('retry limit reached') ||
        _guidanceMessage.contains('capture anyway')) {
      _showForceCapture = true;
    }

    notifyListeners();
  }

  @override
  void onOcrSuccess(OcrResultMessage result) {
    _state = OcrScanState.success;
    _documentType = result.documentType;
    _confidence = result.confidence;

    // Convert nullable map to clean Map<String, String>
    _extractedData = {};
    if (result.data != null) {
      result.data!.forEach((key, value) {
        if (key != null && value != null && value.isNotEmpty) {
          _extractedData[key] = value;
        }
      });
    }

    final confText = confidencePercentage != null
        ? '${confidencePercentage!.toStringAsFixed(1)}%'
        : 'N/A';
    log('OcrScanProvider: OCR success — ${_extractedData.length} fields extracted '
        'from $formattedDocumentType (confidence: $confText)');
    notifyListeners();
  }

  @override
  void onOcrError(OcrErrorMessage error) {
    _state = OcrScanState.error;
    _errorMessage = error.message ?? 'An unknown error occurred.';
    _errorCode = error.errorCode;
    _isRetryable = error.isRetryable ?? false;
    log('OcrScanProvider: OCR error — $_errorCode: $_errorMessage');
    notifyListeners();
  }

  // ---------------------------------------------------------------------------
  // Shared field application logic
  // ---------------------------------------------------------------------------

  /// Looks up a form [Field] by its id from the screen fields.
  Field? _findFieldSpec(String fieldId, List<Field?>? screenFields) {
    if (screenFields == null) return null;
    for (final f in screenFields) {
      if (f != null &&
          (f.id == fieldId ||
              f.id?.toLowerCase() == fieldId.toLowerCase() ||
              f.ocrKey == fieldId ||
              f.ocrKey?.toLowerCase() == fieldId.toLowerCase())) {
        return f;
      }
    }
    return null;
  }

  /// Applies OCR-extracted fields into the registration form.
  ///
  /// Returns the number of fields successfully applied.
  int applyExtractedFields({
    required List<Field?>? screenFields,
    required GlobalProvider globalProvider,
    required RegistrationTaskProvider regTaskProvider,
  }) {
    try {
      final data = extractedData;
      if (data.isEmpty) return 0;

      int appliedCount = 0;
      debugPrint('OCR_KEY_VALUE: ${data.entries.map((e) => '${e.key}=${e.value}').join(', ')}');

      // Build reverse map: ocrKey -> fieldId
      final Map<String, String> ocrKeyToFieldId = {};
      if (screenFields != null) {
        for (final f in screenFields) {
          if (f != null && f.id != null) {
            ocrKeyToFieldId[f.id!] = f.id!;
            ocrKeyToFieldId[f.id!.toLowerCase()] = f.id!;
            if (f.ocrKey != null && f.ocrKey!.isNotEmpty) {
              ocrKeyToFieldId[f.ocrKey!] = f.id!;
              ocrKeyToFieldId[f.ocrKey!.toLowerCase()] = f.id!;
            }
          }
        }
      }

      final Map<String, String> transliterationLangMapper = {
        "eng": "Latin",
        "fra": "fr",
        "ara": "Arabic",
        "hin": "Devanagari",
        "kan": "Kannada",
        "tam": "Tamil",
      };

      final String primaryLang = globalProvider.chosenLang.isNotEmpty
          ? globalProvider.chosenLang[0]
          : 'eng';

      for (final entry in data.entries) {
        try {
          final ocrKey = entry.key;
          String value = entry.value.trim();
          if (value.isEmpty) continue;

          // Resolve OCR key to form field ID
          final fieldId = ocrKeyToFieldId[ocrKey] ??
              ocrKeyToFieldId[ocrKey.toLowerCase()] ??
              ocrKey;

          final fieldSpec = _findFieldSpec(fieldId, screenFields);
          final bool isSimpleType = fieldSpec?.type == 'simpleType';

          // Format date if this is an ageDate/date field
          if (fieldSpec?.controlType == 'ageDate' ||
              fieldSpec?.controlType == 'date' ||
              fieldId.toLowerCase().contains('dob') ||
              fieldId.toLowerCase().contains('dateofbirth')) {
            try {
              final cleanVal = value.replaceAll('-', '/').replaceAll('.', '/');
              final parts = cleanVal.split('/');
              if (parts.length == 3) {
                final p0 = int.tryParse(parts[0]);
                final p1 = int.tryParse(parts[1]);
                final p2 = int.tryParse(parts[2]);
                if (p0 != null && p1 != null && p2 != null) {
                  int y, m, d;
                  if (parts[0].length == 4) {
                    y = p0;
                    m = p1;
                    d = p2;
                  } else {
                    d = p0;
                    m = p1;
                    y = p2;
                  }
                  if (m >= 1 && m <= 12 && d >= 1 && d <= 31 && y > 1900) {
                    final targetFormat = (fieldSpec?.format == null ||
                            fieldSpec!.format!.toLowerCase() == "none")
                        ? "yyyy/MM/dd"
                        : fieldSpec.format!;
                    final dt = DateTime(y, m, d);
                    value = DateFormat(targetFormat).format(dt);
                  }
                }
              }
            } catch (_) {}
          }

          // Handle gender field resolution
          if (fieldSpec?.subType == 'gender' || fieldId.toLowerCase() == 'gender') {
            try {
              final upperVal = value.toUpperCase().trim();
              if (upperVal == 'F' ||
                  upperVal == 'FEM' ||
                  upperVal == 'FEMALE' ||
                  upperVal.contains('FEMALE') ||
                  upperVal.startsWith('FEM') ||
                  upperVal.contains('WOMAN')) {
                value = 'Female';
                regTaskProvider.addSelectedCode(fieldId, 'FEM');
              } else if (upperVal == 'M' ||
                  upperVal == 'MLE' ||
                  upperVal == 'MALE' ||
                  (upperVal.contains('MALE') && !upperVal.contains('FEMALE')) ||
                  upperVal.startsWith('MAL') ||
                  upperVal.contains('MAN')) {
                value = 'Male';
                regTaskProvider.addSelectedCode(fieldId, 'MLE');
              } else if (upperVal == 'O' ||
                  upperVal == 'OTH' ||
                  upperVal == 'OTHER' ||
                  upperVal.contains('OTHER') ||
                  upperVal.contains('TRANS')) {
                value = 'Other';
                regTaskProvider.addSelectedCode(fieldId, 'OTH');
              }
            } catch (e) {
              debugPrint('Gender resolution error: $e');
            }
          }

          if (isSimpleType) {
            // Immediately populate all chosen languages so widgets display instantly
            for (final target in globalProvider.chosenLang) {
              final targetCode = globalProvider.langToCode(target);
              globalProvider.setLanguageSpecificValue(
                fieldId,
                value,
                targetCode,
                globalProvider.fieldInputValue,
              );
              regTaskProvider.addSimpleTypeDemographicField(
                fieldId,
                value,
                targetCode,
              );

              // Background transliteration for non-primary languages
              if (targetCode != primaryLang) {
                TransliterationServiceImpl().transliterate(
                  TransliterationOptions(
                    input: value,
                    sourceLanguage: "Any",
                    targetLanguage: transliterationLangMapper[targetCode] ?? targetCode,
                  ),
                ).then((result) {
                  if (result.isNotEmpty && result != value) {
                    globalProvider.setLanguageSpecificValue(
                      fieldId,
                      result,
                      targetCode,
                      globalProvider.fieldInputValue,
                    );
                    regTaskProvider.addSimpleTypeDemographicField(
                      fieldId,
                      result,
                      targetCode,
                    );
                  }
                }).catchError((_) {});
              }
            }
          } else {
            // Plain string or scalar type: store in map and Demographic service
            globalProvider.setInputMapValue(
              fieldId,
              value,
              globalProvider.fieldInputValue,
            );
            regTaskProvider.addDemographicField(fieldId, value);
          }

          appliedCount++;
        } catch (fieldError) {
          debugPrint('Error applying field ${entry.key}: $fieldError');
        }
      }

      if (appliedCount > 0) {
        try {
          globalProvider.getAudit(
                "REG-EVT-118",
                "REG-MOD-103",
                [
                  documentType ?? 'unknown',
                  '$appliedCount',
                ],
              );
        } catch (_) {}
      }

      return appliedCount;
    } catch (e) {
      debugPrint('General error in applyExtractedFields: $e');
      return 0;
    }
  }

  // ---------------------------------------------------------------------------
  // Cleanup
  // ---------------------------------------------------------------------------
  @override
  void dispose() {
    OcrFlutterApi.setup(null);
    super.dispose();
  }
}
