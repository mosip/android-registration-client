/*
 * Copyright (c) Modular Open Source Identity Platform
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

import 'dart:io';

import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:image_picker/image_picker.dart';
import 'package:provider/provider.dart';
import 'package:registration_client/provider/global_provider.dart';
import 'package:registration_client/provider/ocr_scan_provider.dart';
import 'package:registration_client/provider/registration_task_provider.dart';
import 'package:registration_client/utils/app_config.dart';

/// Full-screen OCR document upload page.
///
/// Flow:
///   1. File picker opens immediately on page mount.
///   2. User selects an image → full-screen preview is shown.
///   3. User taps "Process Document" → [OcrScanProvider.uploadDocument] is called.
///   4. Native decodes the image and feeds it into the shared OCR pipeline.
///   5. On success: fields are auto-filled and the page closes.
///   6. On error: an actionable error card is shown with retry / manual options.
class OcrUploadPage extends StatefulWidget {
  const OcrUploadPage({super.key});

  @override
  State<OcrUploadPage> createState() => _OcrUploadPageState();
}

class _OcrUploadPageState extends State<OcrUploadPage>
    with SingleTickerProviderStateMixin {
  // ---------------------------------------------------------------------------
  // State
  // ---------------------------------------------------------------------------
  File? _selectedFile;
  String? _selectedFilePath; // absolute path or content URI
  String _fileName = '';
  String _fileSize = '';

  bool _pickerActive = false;
  bool _hasPopped = false;

  // Shimmer / fade animation for the preview reveal
  late AnimationController _fadeController;
  late Animation<double> _fadeAnimation;

  // ---------------------------------------------------------------------------
  // Init / Dispose
  // ---------------------------------------------------------------------------
  @override
  void initState() {
    super.initState();

    _fadeController = AnimationController(
      vsync: this,
      duration: const Duration(milliseconds: 500),
    );
    _fadeAnimation = CurvedAnimation(
      parent: _fadeController,
      curve: Curves.easeOut,
    );

    // Open the file picker automatically on first build
    WidgetsBinding.instance.addPostFrameCallback((_) {
      _openPicker();
    });
  }

  @override
  void dispose() {
    _fadeController.dispose();
    super.dispose();
  }

  // ---------------------------------------------------------------------------
  // File Picker
  // ---------------------------------------------------------------------------
  Future<void> _openPicker() async {
    if (_pickerActive) return;
    setState(() => _pickerActive = true);

    try {
      final ImagePicker picker = ImagePicker();
      final XFile? picked = await picker.pickImage(
        source: ImageSource.gallery,
        imageQuality: 95, // slight compression for very large files
      );

      if (!mounted) return;

      if (picked == null) {
        // User dismissed the picker — stay on page with "choose file" button
        setState(() => _pickerActive = false);
        return;
      }

      final File file = File(picked.path);
      final int bytes = await file.length();

      setState(() {
        _selectedFile = file;
        _selectedFilePath = picked.path;
        _fileName = picked.name;
        _fileSize = _formatBytes(bytes);
        _pickerActive = false;
      });

      _fadeController.forward(from: 0);
    } catch (e) {
      if (!mounted) return;
      setState(() => _pickerActive = false);
      _showPickerError('Could not open the file picker: $e');
    }
  }

  void _showPickerError(String message) {
    ScaffoldMessenger.of(context).showSnackBar(
      SnackBar(
        content: Text(message),
        backgroundColor: const Color(0xFFBE1B1B),
        behavior: SnackBarBehavior.floating,
        shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(10)),
      ),
    );
  }

  String _formatBytes(int bytes) {
    if (bytes < 1024) return '$bytes B';
    if (bytes < 1024 * 1024) return '${(bytes / 1024).toStringAsFixed(1)} KB';
    return '${(bytes / (1024 * 1024)).toStringAsFixed(1)} MB';
  }

  // ---------------------------------------------------------------------------
  // OCR Processing
  // ---------------------------------------------------------------------------
  Future<void> _processDocument() async {
    if (_selectedFilePath == null) return;
    final provider = context.read<OcrScanProvider>();
    await provider.uploadDocument(_selectedFilePath!);
    // Results arrive via OcrFlutterApi callbacks → state rebuild → _buildBody
  }

  /// Apply OCR-extracted fields into the registration form and pop.
  void _applyExtractedFields() {
    final ocrProvider = context.read<OcrScanProvider>();
    final globalProvider = context.read<GlobalProvider>();
    final regTaskProvider = context.read<RegistrationTaskProvider>();

    final data = ocrProvider.extractedData;
    int count = 0;

    for (final entry in data.entries) {
      globalProvider.setInputMapValue(
        entry.key,
        entry.value,
        globalProvider.fieldInputValue,
      );
      regTaskProvider.addDemographicField(entry.key, entry.value);
      count++;
    }

    if (!mounted) return;

    if (count > 0) {
      final docType = ocrProvider.documentType ?? 'document';
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(
          content: Row(
            children: [
              const Icon(Icons.check_circle_outline,
                  color: Colors.white, size: 20),
              const SizedBox(width: 10),
              Expanded(
                child: Text(
                  '$count field${count > 1 ? 's' : ''} auto-filled from $docType',
                  style: const TextStyle(
                      fontWeight: FontWeight.w500, fontSize: 14),
                ),
              ),
            ],
          ),
          backgroundColor: const Color(0xFF1A9B42),
          behavior: SnackBarBehavior.floating,
          shape:
              RoundedRectangleBorder(borderRadius: BorderRadius.circular(10)),
          margin:
              EdgeInsets.symmetric(horizontal: 20.w, vertical: 16.h),
          duration: const Duration(seconds: 3),
        ),
      );
    }

    ocrProvider.reset();
    Navigator.of(context).pop();
  }

  // ---------------------------------------------------------------------------
  // Build
  // ---------------------------------------------------------------------------
  @override
  Widget build(BuildContext context) {
    return Consumer<OcrScanProvider>(
      builder: (context, provider, _) {
        // Auto-apply on success
        if (provider.state == OcrScanState.success && !_hasPopped) {
          _hasPopped = true;
          WidgetsBinding.instance
              .addPostFrameCallback((_) => _applyExtractedFields());
        }

        return Scaffold(
          backgroundColor: const Color(0xFF0F1117),
          body: Stack(
            fit: StackFit.expand,
            children: [
              _buildBody(provider),
              _buildTopBar(provider),
            ],
          ),
        );
      },
    );
  }

  // ---------------------------------------------------------------------------
  // Body — switches between picker prompt / preview / processing / error
  // ---------------------------------------------------------------------------
  Widget _buildBody(OcrScanProvider provider) {
    // Processing overlay
    if (provider.state == OcrScanState.processing &&
        provider.inputSource == OcrInputSource.upload) {
      return _buildProcessingView(provider);
    }

    // Error overlay
    if (provider.state == OcrScanState.error &&
        provider.inputSource == OcrInputSource.upload) {
      return _buildErrorView(provider);
    }

    // Preview (file selected)
    if (_selectedFile != null) {
      return _buildPreviewView();
    }

    // Picker prompt (no file selected yet, or picker active)
    return _buildPickerPromptView();
  }

  // ---------------------------------------------------------------------------
  // Picker Prompt (initial state / picker dismissed)
  // ---------------------------------------------------------------------------
  Widget _buildPickerPromptView() {
    return Center(
      child: Padding(
        padding: EdgeInsets.symmetric(horizontal: 40.w),
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            // Icon container
            Container(
              width: 96,
              height: 96,
              decoration: BoxDecoration(
                color: solidPrimary.withOpacity(0.12),
                borderRadius: BorderRadius.circular(24),
                border: Border.all(
                  color: solidPrimary.withOpacity(0.25),
                  width: 1.5,
                ),
              ),
              child: Icon(
                Icons.upload_file_outlined,
                color: solidPrimary,
                size: 48,
              ),
            ),
            SizedBox(height: 28.h),
            Text(
              'Select a Document',
              style: TextStyle(
                color: Colors.white,
                fontSize: 22,
                fontWeight: FontWeight.w700,
                letterSpacing: -0.3,
              ),
            ),
            SizedBox(height: 10.h),
            Text(
              'Choose a photo of your ID card, passport, or other official document from your gallery.',
              textAlign: TextAlign.center,
              style: TextStyle(
                color: Colors.white.withOpacity(0.55),
                fontSize: 14,
                height: 1.55,
              ),
            ),
            SizedBox(height: 36.h),
            SizedBox(
              width: double.infinity,
              height: 52,
              child: ElevatedButton.icon(
                onPressed: _pickerActive ? null : _openPicker,
                icon: _pickerActive
                    ? SizedBox(
                        width: 18,
                        height: 18,
                        child: CircularProgressIndicator(
                          strokeWidth: 2,
                          color: Colors.white.withOpacity(0.6),
                        ),
                      )
                    : const Icon(Icons.photo_library_outlined, size: 22),
                label: Text(
                  _pickerActive ? 'Opening gallery...' : 'Choose from Gallery',
                  style: const TextStyle(
                    fontSize: 16,
                    fontWeight: FontWeight.w600,
                    letterSpacing: 0.2,
                  ),
                ),
                style: ElevatedButton.styleFrom(
                  backgroundColor: solidPrimary,
                  foregroundColor: Colors.white,
                  disabledBackgroundColor:
                      solidPrimary.withOpacity(0.5),
                  shape: RoundedRectangleBorder(
                      borderRadius: BorderRadius.circular(12)),
                  elevation: 0,
                ),
              ),
            ),
            SizedBox(height: 14.h),
            Text(
              'Supported: JPG · PNG · WebP · BMP',
              style: TextStyle(
                color: Colors.white.withOpacity(0.3),
                fontSize: 12,
                letterSpacing: 0.4,
              ),
            ),
          ],
        ),
      ),
    );
  }

  // ---------------------------------------------------------------------------
  // Preview + Confirm
  // ---------------------------------------------------------------------------
  Widget _buildPreviewView() {
    return Column(
      children: [
        // Image fills the top portion
        Expanded(
          child: FadeTransition(
            opacity: _fadeAnimation,
            child: Container(
              margin: EdgeInsets.only(
                top: MediaQuery.of(context).padding.top + 64.h,
                bottom: 0,
              ),
              child: InteractiveViewer(
                minScale: 1.0,
                maxScale: 4.0,
                child: Image.file(
                  _selectedFile!,
                  fit: BoxFit.contain,
                  errorBuilder: (_, __, ___) => Center(
                    child: Column(
                      mainAxisAlignment: MainAxisAlignment.center,
                      children: [
                        Icon(Icons.broken_image_outlined,
                            color: Colors.white.withOpacity(0.4),
                            size: 56),
                        SizedBox(height: 12.h),
                        Text('Could not preview this file',
                            style: TextStyle(
                                color: Colors.white.withOpacity(0.4))),
                      ],
                    ),
                  ),
                ),
              ),
            ),
          ),
        ),

        // Bottom action bar
        _buildPreviewActionBar(),
      ],
    );
  }

  Widget _buildPreviewActionBar() {
    return Container(
      padding: EdgeInsets.fromLTRB(20.w, 20.h, 20.w,
          20.h + MediaQuery.of(context).padding.bottom),
      decoration: BoxDecoration(
        color: const Color(0xFF161A24),
        border: Border(
          top: BorderSide(
            color: Colors.white.withOpacity(0.08),
            width: 1,
          ),
        ),
      ),
      child: Column(
        mainAxisSize: MainAxisSize.min,
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          // File info row
          Row(
            children: [
              Container(
                width: 40,
                height: 40,
                decoration: BoxDecoration(
                  color: solidPrimary.withOpacity(0.15),
                  borderRadius: BorderRadius.circular(8),
                ),
                child: Icon(Icons.image_outlined, color: solidPrimary, size: 22),
              ),
              SizedBox(width: 12.w),
              Expanded(
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Text(
                      _fileName,
                      maxLines: 1,
                      overflow: TextOverflow.ellipsis,
                      style: const TextStyle(
                        color: Colors.white,
                        fontSize: 14,
                        fontWeight: FontWeight.w600,
                      ),
                    ),
                    Text(
                      _fileSize,
                      style: TextStyle(
                        color: Colors.white.withOpacity(0.45),
                        fontSize: 12,
                      ),
                    ),
                  ],
                ),
              ),
              // Change file button
              TextButton.icon(
                onPressed: _openPicker,
                icon: const Icon(Icons.swap_horiz_rounded, size: 18),
                label: const Text('Change',
                    style: TextStyle(fontWeight: FontWeight.w500)),
                style: TextButton.styleFrom(
                  foregroundColor: solidPrimary,
                  padding:
                      EdgeInsets.symmetric(horizontal: 12.w, vertical: 8.h),
                ),
              ),
            ],
          ),
          SizedBox(height: 16.h),
          // Process button
          SizedBox(
            width: double.infinity,
            height: 52,
            child: ElevatedButton.icon(
              onPressed: _processDocument,
              icon: const Icon(Icons.document_scanner_outlined, size: 22),
              label: const Text(
                'Process Document',
                style: TextStyle(
                  fontSize: 16,
                  fontWeight: FontWeight.w600,
                  letterSpacing: 0.2,
                ),
              ),
              style: ElevatedButton.styleFrom(
                backgroundColor: solidPrimary,
                foregroundColor: Colors.white,
                shape: RoundedRectangleBorder(
                    borderRadius: BorderRadius.circular(12)),
                elevation: 0,
              ),
            ),
          ),
          SizedBox(height: 10.h),
          Center(
            child: Text(
              'Pinch to zoom the preview before processing',
              style: TextStyle(
                color: Colors.white.withOpacity(0.3),
                fontSize: 12,
              ),
            ),
          ),
        ],
      ),
    );
  }

  // ---------------------------------------------------------------------------
  // Processing Overlay
  // ---------------------------------------------------------------------------
  Widget _buildProcessingView(OcrScanProvider provider) {
    return Stack(
      fit: StackFit.expand,
      children: [
        // Dimmed image background
        if (_selectedFile != null)
          ColorFiltered(
            colorFilter: ColorFilter.mode(
              Colors.black.withOpacity(0.7),
              BlendMode.darken,
            ),
            child: Image.file(_selectedFile!, fit: BoxFit.cover),
          ),

        // Processing card
        Center(
          child: Container(
            margin: EdgeInsets.symmetric(horizontal: 40.w),
            padding: EdgeInsets.symmetric(horizontal: 32.w, vertical: 36.h),
            decoration: BoxDecoration(
              color: const Color(0xFF161A24).withOpacity(0.95),
              borderRadius: BorderRadius.circular(20),
              border: Border.all(
                color: solidPrimary.withOpacity(0.2),
                width: 1,
              ),
              boxShadow: [
                BoxShadow(
                  color: solidPrimary.withOpacity(0.15),
                  blurRadius: 32,
                  spreadRadius: 2,
                ),
              ],
            ),
            child: Column(
              mainAxisSize: MainAxisSize.min,
              children: [
                // Animated progress ring
                SizedBox(
                  width: 64,
                  height: 64,
                  child: Stack(
                    alignment: Alignment.center,
                    children: [
                      SizedBox(
                        width: 64,
                        height: 64,
                        child: CircularProgressIndicator(
                          strokeWidth: 3.5,
                          color: solidPrimary,
                          backgroundColor:
                              solidPrimary.withOpacity(0.15),
                        ),
                      ),
                      Icon(
                        Icons.document_scanner_outlined,
                        color: solidPrimary,
                        size: 28,
                      ),
                    ],
                  ),
                ),
                SizedBox(height: 24.h),
                Text(
                  'Analysing Document',
                  style: TextStyle(
                    color: Colors.white,
                    fontSize: 18,
                    fontWeight: FontWeight.w700,
                    letterSpacing: -0.2,
                  ),
                ),
                SizedBox(height: 8.h),
                Text(
                  'Extracting fields with OCR engine...',
                  style: TextStyle(
                    color: Colors.white.withOpacity(0.5),
                    fontSize: 13,
                  ),
                ),
                SizedBox(height: 24.h),
                // Pulsing step indicators
                _buildProcessingSteps(),
              ],
            ),
          ),
        ),
      ],
    );
  }

  Widget _buildProcessingSteps() {
    final steps = [
      'Decoding image',
      'Detecting document',
      'Extracting fields',
    ];
    return Column(
      children: steps.asMap().entries.map((e) {
        return Padding(
          padding: EdgeInsets.only(bottom: 8.h),
          child: Row(
            mainAxisSize: MainAxisSize.min,
            children: [
              SizedBox(
                width: 16,
                height: 16,
                child: CircularProgressIndicator(
                  strokeWidth: 2,
                  color: solidPrimary.withOpacity(0.6),
                ),
              ),
              SizedBox(width: 10.w),
              Text(
                e.value,
                style: TextStyle(
                  color: Colors.white.withOpacity(0.55),
                  fontSize: 13,
                ),
              ),
            ],
          ),
        );
      }).toList(),
    );
  }

  // ---------------------------------------------------------------------------
  // Error View
  // ---------------------------------------------------------------------------
  Widget _buildErrorView(OcrScanProvider provider) {
    return Container(
      color: const Color(0xFF0F1117),
      child: Center(
        child: Container(
          margin: EdgeInsets.symmetric(horizontal: 28.w),
          padding: EdgeInsets.all(28.w),
          decoration: BoxDecoration(
            color: pureWhite,
            borderRadius: BorderRadius.circular(20),
            boxShadow: [
              BoxShadow(
                color: Colors.black.withOpacity(0.25),
                blurRadius: 24,
                offset: const Offset(0, 8),
              ),
            ],
          ),
          child: Column(
            mainAxisSize: MainAxisSize.min,
            children: [
              // Error icon
              Container(
                width: 60,
                height: 60,
                decoration: BoxDecoration(
                  color: const Color(0xFFFFF0F0),
                  borderRadius: BorderRadius.circular(30),
                ),
                child: const Icon(Icons.error_outline_rounded,
                    color: Color(0xFFBE1B1B), size: 34),
              ),
              SizedBox(height: 20.h),
              Text(
                'Processing Failed',
                style: TextStyle(
                  color: appBlackShade1,
                  fontSize: 18,
                  fontWeight: FontWeight.w700,
                ),
              ),
              SizedBox(height: 8.h),
              Text(
                provider.errorMessage ?? 'An unknown error occurred.',
                textAlign: TextAlign.center,
                style: TextStyle(
                  color: appBlackShade2,
                  fontSize: 14,
                  height: 1.5,
                ),
              ),
              if (provider.errorCode != null) ...[
                SizedBox(height: 6.h),
                Container(
                  padding: EdgeInsets.symmetric(
                      horizontal: 10.w, vertical: 4.h),
                  decoration: BoxDecoration(
                    color: const Color(0xFFF5F5F5),
                    borderRadius: BorderRadius.circular(6),
                  ),
                  child: Text(
                    provider.errorCode!,
                    style: TextStyle(
                      color: appBlackShade3,
                      fontSize: 11,
                      fontFamily: 'monospace',
                      letterSpacing: 0.5,
                    ),
                  ),
                ),
              ],
              SizedBox(height: 28.h),

              // Try with different image
              if (provider.isRetryable) ...[
                SizedBox(
                  width: double.infinity,
                  height: 48,
                  child: ElevatedButton.icon(
                    onPressed: _processDocument,
                    icon: const Icon(Icons.refresh_rounded, size: 20),
                    label: const Text('Try Again',
                        style: TextStyle(
                            fontSize: 15, fontWeight: FontWeight.w600)),
                    style: ElevatedButton.styleFrom(
                      backgroundColor: solidPrimary,
                      foregroundColor: Colors.white,
                      shape: RoundedRectangleBorder(
                          borderRadius: BorderRadius.circular(10)),
                      elevation: 0,
                    ),
                  ),
                ),
                SizedBox(height: 10.h),
                SizedBox(
                  width: double.infinity,
                  height: 48,
                  child: OutlinedButton.icon(
                    onPressed: _openPicker,
                    icon: const Icon(Icons.photo_library_outlined, size: 20),
                    label: const Text('Choose Different Image',
                        style: TextStyle(
                            fontSize: 15, fontWeight: FontWeight.w600)),
                    style: OutlinedButton.styleFrom(
                      foregroundColor: solidPrimary,
                      side: BorderSide(color: solidPrimary, width: 1.5),
                      shape: RoundedRectangleBorder(
                          borderRadius: BorderRadius.circular(10)),
                    ),
                  ),
                ),
                SizedBox(height: 10.h),
              ],

              SizedBox(
                width: double.infinity,
                height: 48,
                child: OutlinedButton(
                  onPressed: () {
                    provider.reset();
                    Navigator.of(context).pop();
                  },
                  style: OutlinedButton.styleFrom(
                    foregroundColor: appBlackShade2,
                    side: BorderSide(
                        color: appBlackShade3.withOpacity(0.4),
                        width: 1.5),
                    shape: RoundedRectangleBorder(
                        borderRadius: BorderRadius.circular(10)),
                  ),
                  child: const Text('Enter Manually',
                      style: TextStyle(
                          fontSize: 15, fontWeight: FontWeight.w600)),
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }

  // ---------------------------------------------------------------------------
  // Top Bar
  // ---------------------------------------------------------------------------
  Widget _buildTopBar(OcrScanProvider provider) {
    final isProcessing = provider.state == OcrScanState.processing &&
        provider.inputSource == OcrInputSource.upload;
    final isError = provider.state == OcrScanState.error &&
        provider.inputSource == OcrInputSource.upload;

    return Positioned(
      top: 0,
      left: 0,
      right: 0,
      child: SafeArea(
        child: Padding(
          padding: EdgeInsets.symmetric(horizontal: 8.w, vertical: 8.h),
          child: Row(
            children: [
              // Close / back button
              Material(
                color: Colors.black.withOpacity(0.35),
                borderRadius: BorderRadius.circular(24),
                child: InkWell(
                  onTap: () {
                    if (isProcessing) return; // block back during processing
                    provider.reset();
                    Navigator.of(context).pop();
                  },
                  borderRadius: BorderRadius.circular(24),
                  child: Padding(
                    padding: const EdgeInsets.all(10),
                    child: Icon(
                      isProcessing ? Icons.hourglass_top_rounded : Icons.close_rounded,
                      color: Colors.white.withOpacity(isProcessing ? 0.4 : 1.0),
                      size: 24,
                    ),
                  ),
                ),
              ),
              const Spacer(),
              // Title chip
              Container(
                padding: EdgeInsets.symmetric(horizontal: 16.w, vertical: 8.h),
                decoration: BoxDecoration(
                  color: Colors.black.withOpacity(0.35),
                  borderRadius: BorderRadius.circular(20),
                ),
                child: Row(
                  mainAxisSize: MainAxisSize.min,
                  children: [
                    Icon(
                      isProcessing
                          ? Icons.hourglass_top_rounded
                          : isError
                              ? Icons.warning_amber_rounded
                              : Icons.upload_file_outlined,
                      color: isError
                          ? const Color(0xFFFFB020)
                          : Colors.white,
                      size: 17,
                    ),
                    const SizedBox(width: 6),
                    Text(
                      isProcessing
                          ? 'Processing...'
                          : isError
                              ? 'Error'
                              : 'Upload Document',
                      style: const TextStyle(
                        color: Colors.white,
                        fontSize: 14,
                        fontWeight: FontWeight.w500,
                      ),
                    ),
                  ],
                ),
              ),
              const Spacer(),
              // Spacer to balance left button
              const SizedBox(width: 44),
            ],
          ),
        ),
      ),
    );
  }
}

