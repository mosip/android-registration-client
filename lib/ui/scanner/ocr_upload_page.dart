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
import 'package:registration_client/model/field.dart';
import 'package:registration_client/model/ocr_error_messages.dart';
import 'package:registration_client/provider/global_provider.dart';
import 'package:registration_client/provider/ocr_scan_provider.dart';
import 'package:registration_client/provider/registration_task_provider.dart';
import 'package:registration_client/utils/app_config.dart';


class OcrUploadPage extends StatefulWidget {
  final List<Field?>? screenFields;

  const OcrUploadPage({super.key, this.screenFields});

  @override
  State<OcrUploadPage> createState() => _OcrUploadPageState();
}

class _OcrUploadPageState extends State<OcrUploadPage>
    with SingleTickerProviderStateMixin {
  // ---------------------------------------------------------------------------
  // State
  // ---------------------------------------------------------------------------
  File? _selectedFile;
  String? _selectedFilePath;
  String _fileName = '';
  String _fileSize = '';
  bool _hasAuditedError = false;

  bool _pickerActive = false;
  bool _hasPopped = false;

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
      duration: const Duration(milliseconds: 400),
    );
    _fadeAnimation = CurvedAnimation(
      parent: _fadeController,
      curve: Curves.easeOutCubic,
    );

    WidgetsBinding.instance.addPostFrameCallback((_) {
      context.read<GlobalProvider>().getAudit("REG-EVT-116", "REG-MOD-103");
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
        imageQuality: 95,
      );

      if (!mounted) return;

      if (picked == null) {
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
        content: Row(
          children: [
            const Icon(Icons.error_outline, color: Colors.white, size: 18),
            SizedBox(width: 8.w),
            Expanded(
              child: Text(
                message,
                style: TextStyle(
                  fontWeight: FontWeight.w500,
                  fontSize: 13.sp,
                ),
              ),
            ),
          ],
        ),
        backgroundColor: appRed,
        behavior: SnackBarBehavior.floating,
        shape: RoundedRectangleBorder(
          borderRadius: BorderRadius.circular(10.r),
        ),
        margin: EdgeInsets.symmetric(horizontal: 20.w, vertical: 16.h),
        duration: const Duration(seconds: 3),
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
  }

  /// Apply OCR-extracted fields into the registration form via the shared provider method.
  void _applyExtractedFields() {
    final ocrProvider = context.read<OcrScanProvider>();
    final globalProvider = context.read<GlobalProvider>();
    final regTaskProvider = context.read<RegistrationTaskProvider>();

    final appliedCount = ocrProvider.applyExtractedFields(
      screenFields: widget.screenFields,
      globalProvider: globalProvider,
      regTaskProvider: regTaskProvider,
    );

    if (appliedCount > 0 && mounted) {
      final docType = ocrProvider.formattedDocumentType;
      final confidencePct = ocrProvider.confidencePercentage;
      final confidenceText = confidencePct != null
          ? ' (${confidencePct.toStringAsFixed(1)}% confidence)'
          : '';
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(
          content: Row(
            children: [
              const Icon(Icons.check_circle_outline,
                  color: Colors.white, size: 18),
              SizedBox(width: 8.w),
              Expanded(
                child: Text(
                  '$appliedCount field${appliedCount > 1 ? 's' : ''} auto-filled from $docType$confidenceText',
                  style: TextStyle(
                    fontWeight: FontWeight.w500,
                    fontSize: 13.sp,
                  ),
                ),
              ),
            ],
          ),
          backgroundColor: dashBoardPacketUploadColor,
          behavior: SnackBarBehavior.floating,
          shape:
              RoundedRectangleBorder(borderRadius: BorderRadius.circular(10.r)),
          margin:
              EdgeInsets.symmetric(horizontal: 20.w, vertical: 16.h),
          duration: const Duration(seconds: 3),
        ),
      );
    }
  }

  // ---------------------------------------------------------------------------
  // Build
  // ---------------------------------------------------------------------------
  @override
  Widget build(BuildContext context) {
    return Consumer<OcrScanProvider>(
      builder: (context, provider, _) {
        if (provider.state == OcrScanState.success && !_hasPopped) {
          _hasPopped = true;
          WidgetsBinding.instance.addPostFrameCallback((_) {
            try {
              context.read<GlobalProvider>().getAudit(
                    "REG-EVT-117",
                    "REG-MOD-103",
                    [
                      provider.documentType ?? 'unknown',
                      '${provider.extractedData.length}',
                      provider.confidencePercentage?.toStringAsFixed(1) ?? 'N/A',
                    ],
                  );
            } catch (_) {}

            try {
              _applyExtractedFields();
            } catch (_) {}

            provider.reset();

            if (mounted) {
              Navigator.of(context).pop();
            }
          });
        }

        if (provider.state == OcrScanState.error &&
            provider.inputSource == OcrInputSource.upload &&
            !_hasAuditedError) {
          _hasAuditedError = true;
          WidgetsBinding.instance.addPostFrameCallback((_) {
            context.read<GlobalProvider>().getAudit(
                  "REG-EVT-050",
                  "REG-MOD-103",
                  [
                    provider.errorCode ?? 'UNKNOWN',
                    provider.errorMessage ?? 'No message',
                  ],
                );
          });
        }

        return Scaffold(
          backgroundColor: backgroundColor,
          body: Column(
            children: [
              _buildTopBar(provider),
              Expanded(child: _buildBody(provider)),
            ],
          ),
        );
      },
    );
  }

  // ---------------------------------------------------------------------------
  // Body
  // ---------------------------------------------------------------------------
  Widget _buildBody(OcrScanProvider provider) {
    if (provider.state == OcrScanState.processing &&
        provider.inputSource == OcrInputSource.upload) {
      return _buildProcessingView();
    }

    if (provider.state == OcrScanState.error &&
        provider.inputSource == OcrInputSource.upload) {
      return _buildErrorView(provider);
    }

    if (_selectedFile != null) {
      return _buildPreviewView();
    }

    return _buildPickerPromptView();
  }

  // ---------------------------------------------------------------------------
  // Top Bar
  // ---------------------------------------------------------------------------
  Widget _buildTopBar(OcrScanProvider provider) {
    final isProcessing = provider.state == OcrScanState.processing &&
        provider.inputSource == OcrInputSource.upload;

    return Container(
      color: solidPrimary,
      child: SafeArea(
        bottom: false,
        child: Container(
          height: 56.h,
          padding: EdgeInsets.symmetric(horizontal: 12.w),
          child: Row(
            children: [
              Material(
                color: Colors.transparent,
                borderRadius: BorderRadius.circular(8.r),
                child: InkWell(
                  onTap: () {
                    if (isProcessing) return;
                    provider.reset();
                    Navigator.of(context).pop();
                  },
                  borderRadius: BorderRadius.circular(8.r),
                  child: Padding(
                    padding: EdgeInsets.all(8.w),
                    child: Icon(
                      Icons.close_rounded,
                      color: Colors.white.withOpacity(isProcessing ? 0.4 : 0.9),
                      size: 22.sp,
                    ),
                  ),
                ),
              ),
              Expanded(
                child: Center(
                  child: Text(
                    'Upload Document',
                    style: TextStyle(
                      color: Colors.white,
                      fontSize: 16.sp,
                      fontWeight: FontWeight.w500,
                      letterSpacing: 0.3,
                    ),
                  ),
                ),
              ),
              SizedBox(width: 38.w),
            ],
          ),
        ),
      ),
    );
  }

  // ---------------------------------------------------------------------------
  // Picker Prompt
  // ---------------------------------------------------------------------------
  Widget _buildPickerPromptView() {
    return Center(
      child: Padding(
        padding: EdgeInsets.symmetric(horizontal: 32.w),
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Container(
              width: 72.w,
              height: 72.w,
              decoration: BoxDecoration(
                color: iconContainerColor,
                borderRadius: BorderRadius.circular(16.r),
              ),
              child: Icon(
                Icons.cloud_upload_outlined,
                color: solidPrimary,
                size: 34.sp,
              ),
            ),
            SizedBox(height: 24.h),
            Text(
              'Upload a Document',
              style: TextStyle(
                color: appBlackShade1,
                fontSize: 18.sp,
                fontWeight: FontWeight.w500,
              ),
            ),
            SizedBox(height: 8.h),
            Text(
              'Select a photo from your gallery to\nauto-fill form fields.',
              textAlign: TextAlign.center,
              style: TextStyle(
                color: appBlackShade2,
                fontSize: 13.sp,
                height: 1.5,
              ),
            ),
            SizedBox(height: 28.h),
            SizedBox(
              width: double.infinity,
              height: 48.h,
              child: ElevatedButton.icon(
                onPressed: _pickerActive ? null : _openPicker,
                icon: _pickerActive
                    ? SizedBox(
                        width: 18.w,
                        height: 18.w,
                        child: CircularProgressIndicator(
                          strokeWidth: 2,
                          color: Colors.white.withOpacity(0.6),
                        ),
                      )
                    : Icon(Icons.photo_library_outlined, size: 18.sp),
                label: Text(
                  _pickerActive ? 'Opening gallery...' : 'Choose from Gallery',
                  style: TextStyle(
                    fontSize: 14.sp,
                    fontWeight: FontWeight.bold,
                  ),
                ),
                style: ElevatedButton.styleFrom(
                  backgroundColor: solidPrimary,
                  foregroundColor: Colors.white,
                  disabledBackgroundColor: solidPrimary.withOpacity(0.4),
                  shape: RoundedRectangleBorder(
                    borderRadius: BorderRadius.circular(8.r),
                  ),
                  elevation: 0,
                ),
              ),
            ),
            SizedBox(height: 16.h),
            Row(
              mainAxisAlignment: MainAxisAlignment.center,
              children: ['JPG', 'PNG', 'WebP', 'BMP'].map((fmt) {
                return Container(
                  margin: EdgeInsets.symmetric(horizontal: 4.w),
                  padding: EdgeInsets.symmetric(horizontal: 10.w, vertical: 4.h),
                  decoration: BoxDecoration(
                    color: iconContainerColor,
                    borderRadius: BorderRadius.circular(4.r),
                  ),
                  child: Text(
                    fmt,
                    style: TextStyle(
                      color: appBlackShade2,
                      fontSize: 11.sp,
                      fontWeight: FontWeight.w500,
                    ),
                  ),
                );
              }).toList(),
            ),
          ],
        ),
      ),
    );
  }

  // ---------------------------------------------------------------------------
  // Preview
  // ---------------------------------------------------------------------------
  Widget _buildPreviewView() {
    return Column(
      children: [
        Expanded(
          child: FadeTransition(
            opacity: _fadeAnimation,
            child: Padding(
              padding: EdgeInsets.fromLTRB(16.w, 12.h, 16.w, 0),
              child: Card(
                elevation: 3,
                color: pureWhite,
                shape: RoundedRectangleBorder(
                  borderRadius: BorderRadius.circular(8.r),
                ),
                clipBehavior: Clip.antiAlias,
                child: InteractiveViewer(
                  minScale: 1.0,
                  maxScale: 4.0,
                  child: Image.file(
                    _selectedFile!,
                    fit: BoxFit.contain,
                    width: double.infinity,
                    errorBuilder: (_, __, ___) => Center(
                      child: Column(
                        mainAxisAlignment: MainAxisAlignment.center,
                        children: [
                          Icon(Icons.broken_image_outlined,
                              color: appBlackShade3, size: 40.sp),
                          SizedBox(height: 8.h),
                          Text(
                            'Could not preview this file',
                            style: TextStyle(
                              color: appBlackShade3,
                              fontSize: 13.sp,
                            ),
                          ),
                        ],
                      ),
                    ),
                  ),
                ),
              ),
            ),
          ),
        ),
        _buildPreviewActionBar(),
      ],
    );
  }

  Widget _buildPreviewActionBar() {
    return Container(
      padding: EdgeInsets.fromLTRB(
        16.w,
        12.h,
        16.w,
        16.h + MediaQuery.of(context).padding.bottom,
      ),
      decoration: BoxDecoration(
        color: pureWhite,
        border: Border(
          top: BorderSide(color: greyBorderShade, width: 1),
        ),
      ),
      child: Column(
        mainAxisSize: MainAxisSize.min,
        children: [
          Row(
            children: [
              Container(
                width: 40.w,
                height: 40.w,
                decoration: BoxDecoration(
                  color: iconContainerColor,
                  borderRadius: BorderRadius.circular(8.r),
                ),
                child: Icon(Icons.description_outlined,
                    color: solidPrimary, size: 20.sp),
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
                      style: TextStyle(
                        color: appBlackShade1,
                        fontSize: 13.sp,
                        fontWeight: FontWeight.w500,
                      ),
                    ),
                    SizedBox(height: 2.h),
                    Text(
                      _fileSize,
                      style: TextStyle(
                        color: appBlackShade3,
                        fontSize: 11.sp,
                      ),
                    ),
                  ],
                ),
              ),
              TextButton(
                onPressed: _openPicker,
                style: TextButton.styleFrom(
                  foregroundColor: solidPrimary,
                  padding: EdgeInsets.symmetric(horizontal: 10.w, vertical: 6.h),
                  minimumSize: Size.zero,
                  tapTargetSize: MaterialTapTargetSize.shrinkWrap,
                ),
                child: Text(
                  'Change',
                  style: TextStyle(
                    fontSize: 12.sp,
                    fontWeight: FontWeight.w500,
                  ),
                ),
              ),
            ],
          ),
          SizedBox(height: 12.h),
          SizedBox(
            width: double.infinity,
            height: 48.h,
            child: ElevatedButton.icon(
              onPressed: _processDocument,
              icon: Icon(Icons.document_scanner_outlined, size: 18.sp),
              label: Text(
                'Scan Document',
                style: TextStyle(
                  fontSize: 14.sp,
                  fontWeight: FontWeight.bold,
                ),
              ),
              style: ElevatedButton.styleFrom(
                backgroundColor: solidPrimary,
                foregroundColor: Colors.white,
                shape: RoundedRectangleBorder(
                  borderRadius: BorderRadius.circular(8.r),
                ),
                elevation: 0,
              ),
            ),
          ),
        ],
      ),
    );
  }

  // ---------------------------------------------------------------------------
  // Processing
  // ---------------------------------------------------------------------------
  Widget _buildProcessingView() {
    return Stack(
      fit: StackFit.expand,
      children: [
        if (_selectedFile != null)
          Opacity(
            opacity: 0.3,
            child: Image.file(_selectedFile!, fit: BoxFit.cover),
          ),
        Container(color: backgroundColor.withOpacity(0.85)),
        Center(
          child: Container(
            margin: EdgeInsets.symmetric(horizontal: 40.w),
            padding: EdgeInsets.symmetric(horizontal: 32.w, vertical: 36.h),
            decoration: BoxDecoration(
              color: pureWhite,
              borderRadius: BorderRadius.circular(12.r),
              boxShadow: [
                BoxShadow(
                  color: Colors.black.withOpacity(0.08),
                  blurRadius: 16,
                  offset: const Offset(0, 4),
                ),
              ],
            ),
            child: Column(
              mainAxisSize: MainAxisSize.min,
              children: [
                SizedBox(
                  width: 52.w,
                  height: 52.w,
                  child: Stack(
                    alignment: Alignment.center,
                    children: [
                      SizedBox(
                        width: 52.w,
                        height: 52.w,
                        child: CircularProgressIndicator(
                          strokeWidth: 3,
                          color: solidPrimary,
                          backgroundColor: solidPrimary.withOpacity(0.1),
                        ),
                      ),
                      Icon(
                        Icons.document_scanner_outlined,
                        color: solidPrimary,
                        size: 22.sp,
                      ),
                    ],
                  ),
                ),
                SizedBox(height: 24.h),
                Text(
                  'Processing Document',
                  style: TextStyle(
                    color: appBlackShade1,
                    fontSize: 16.sp,
                    fontWeight: FontWeight.w500,
                  ),
                ),
                SizedBox(height: 6.h),
                Text(
                  'This may take a few seconds',
                  style: TextStyle(
                    color: appBlackShade2,
                    fontSize: 12.sp,
                  ),
                ),
                SizedBox(height: 24.h),
                SizedBox(
                  width: 160.w,
                  child: ClipRRect(
                    borderRadius: BorderRadius.circular(2.r),
                    child: LinearProgressIndicator(
                      backgroundColor: solidPrimary.withOpacity(0.1),
                      color: solidPrimary,
                      minHeight: 3.h,
                    ),
                  ),
                ),
              ],
            ),
          ),
        ),
      ],
    );
  }

  // ---------------------------------------------------------------------------
  // Error
  // ---------------------------------------------------------------------------
  Widget _buildErrorView(OcrScanProvider provider) {
    return Center(
      child: Padding(
        padding: EdgeInsets.symmetric(horizontal: 28.w),
        child: Container(
          padding: EdgeInsets.all(24.w),
          decoration: BoxDecoration(
            color: pureWhite,
            borderRadius: BorderRadius.circular(12.r),
            boxShadow: [
              BoxShadow(
                color: Colors.black.withOpacity(0.06),
                blurRadius: 12,
                offset: const Offset(0, 2),
              ),
            ],
          ),
          child: Column(
            mainAxisSize: MainAxisSize.min,
            children: [
              Container(
                width: 52.w,
                height: 52.w,
                decoration: BoxDecoration(
                  color: appRed.withOpacity(0.08),
                  shape: BoxShape.circle,
                ),
                child: Icon(Icons.error_outline_rounded,
                    color: appRed, size: 26.sp),
              ),
              SizedBox(height: 16.h),
              Text(
                'Something went wrong',
                style: TextStyle(
                  color: appBlackShade1,
                  fontSize: 16.sp,
                  fontWeight: FontWeight.w500,
                ),
              ),
              SizedBox(height: 6.h),
              Text(
                ocrErrorMessage(provider.errorCode, provider.errorMessage),
                textAlign: TextAlign.center,
                style: TextStyle(
                  color: appBlackShade2,
                  fontSize: 13.sp,
                  height: 1.5,
                ),
              ),
              SizedBox(height: 24.h),

              if (provider.isRetryable) ...[
                SizedBox(
                  width: double.infinity,
                  height: 44.h,
                  child: ElevatedButton.icon(
                    onPressed: _processDocument,
                    icon: Icon(Icons.refresh_rounded, size: 18.sp),
                    label: Text(
                      'Try Again',
                      style: TextStyle(
                        fontSize: 13.sp,
                        fontWeight: FontWeight.bold,
                      ),
                    ),
                    style: ElevatedButton.styleFrom(
                      backgroundColor: solidPrimary,
                      foregroundColor: Colors.white,
                      shape: RoundedRectangleBorder(
                        borderRadius: BorderRadius.circular(8.r),
                      ),
                      elevation: 0,
                    ),
                  ),
                ),
                SizedBox(height: 8.h),
                SizedBox(
                  width: double.infinity,
                  height: 44.h,
                  child: OutlinedButton.icon(
                    onPressed: _openPicker,
                    icon: Icon(Icons.photo_library_outlined, size: 18.sp),
                    label: Text(
                      'Choose Different Image',
                      style: TextStyle(
                        fontSize: 13.sp,
                        fontWeight: FontWeight.bold,
                      ),
                    ),
                    style: OutlinedButton.styleFrom(
                      foregroundColor: solidPrimary,
                      side: BorderSide(
                          color: solidPrimary.withOpacity(0.5), width: 1.5),
                      shape: RoundedRectangleBorder(
                        borderRadius: BorderRadius.circular(8.r),
                      ),
                    ),
                  ),
                ),
                SizedBox(height: 8.h),
              ],

              SizedBox(
                width: double.infinity,
                height: 44.h,
                child: TextButton(
                  onPressed: () {
                    provider.reset();
                    Navigator.of(context).pop();
                  },
                  style: TextButton.styleFrom(
                    foregroundColor: appBlackShade2,
                    shape: RoundedRectangleBorder(
                      borderRadius: BorderRadius.circular(8.r),
                    ),
                  ),
                  child: Text(
                    'Enter Manually',
                    style: TextStyle(
                      fontSize: 13.sp,
                      fontWeight: FontWeight.w500,
                    ),
                  ),
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }
}
