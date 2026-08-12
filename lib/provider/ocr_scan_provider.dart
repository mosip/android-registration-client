/*
 * Copyright (c) Modular Open Source Identity Platform
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

import 'dart:developer';

import 'package:flutter/material.dart';
import 'package:registration_client/core/bridge/ocr_api.g.dart';
import 'package:registration_client/pigeon/global_config_settings_pigeon.dart';

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

  double? _confidence;
  double? get confidence => _confidence;

  // ---------------------------------------------------------------------------
  // Error data
  // ---------------------------------------------------------------------------
  String? _errorMessage;
  String? get errorMessage => _errorMessage;

  String? _errorCode;
  String? get errorCode => _errorCode;

  bool _isRetryable = false;
  bool get isRetryable => _isRetryable;

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
    _inputSource = OcrInputSource.camera;
    notifyListeners();
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

    if (!_isQualityAcceptable) {
      _qualityRetryCount++;
      if (_qualityRetryCount >= _maxQualityRetries) {
        _showForceCapture = true;
      }
    } else {
      // Quality is good — native will auto-capture after hold timer
      _state = OcrScanState.processing;
      _guidanceMessage = 'Hold steady...';
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

    log('OcrScanProvider: OCR success — ${_extractedData.length} fields extracted '
        'from $_documentType (confidence: ${_confidence?.toStringAsFixed(1)}%)');
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
  // Cleanup
  // ---------------------------------------------------------------------------
  @override
  void dispose() {
    OcrFlutterApi.setup(null);
    super.dispose();
  }
}
