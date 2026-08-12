/*
 * Copyright (c) Modular Open Source Identity Platform
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:provider/provider.dart';
import 'package:registration_client/provider/global_provider.dart';
import 'package:registration_client/provider/ocr_scan_provider.dart';
import 'package:registration_client/provider/registration_task_provider.dart';
import 'package:registration_client/utils/app_config.dart';

/// Full-screen OCR document scanning page with live camera preview,
/// real-time quality guidance, and auto-fill support.
class OcrScanPage extends StatefulWidget {
  const OcrScanPage({super.key});

  @override
  State<OcrScanPage> createState() => _OcrScanPageState();
}

class _OcrScanPageState extends State<OcrScanPage>
    with SingleTickerProviderStateMixin {
  late AnimationController _pulseController;
  late Animation<double> _pulseAnimation;
  bool _hasPopped = false;

  @override
  void initState() {
    super.initState();

    // Pulsing border animation
    _pulseController = AnimationController(
      vsync: this,
      duration: const Duration(milliseconds: 1500),
    )..repeat(reverse: true);

    _pulseAnimation = Tween<double>(begin: 0.4, end: 1.0).animate(
      CurvedAnimation(parent: _pulseController, curve: Curves.easeInOut),
    );

    // Start the scan
    WidgetsBinding.instance.addPostFrameCallback((_) {
      context.read<OcrScanProvider>().startScan();
    });
  }

  @override
  void dispose() {
    _pulseController.dispose();
    super.dispose();
  }

  /// Apply OCR-extracted fields into the registration form.
  void _applyExtractedFields() {
    final ocrProvider = context.read<OcrScanProvider>();
    final globalProvider = context.read<GlobalProvider>();
    final regTaskProvider = context.read<RegistrationTaskProvider>();

    final data = ocrProvider.extractedData;
    int appliedCount = 0;

    for (final entry in data.entries) {
      final fieldId = entry.key;
      final value = entry.value;

      // Store in GlobalProvider's fieldInputValue for UI display
      globalProvider.setInputMapValue(
        fieldId,
        value,
        globalProvider.fieldInputValue,
      );

      // Persist in registration task via Pigeon to native
      regTaskProvider.addDemographicField(fieldId, value);
      appliedCount++;
    }

    if (appliedCount > 0) {
      final docType = ocrProvider.documentType ?? 'document';
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(
          content: Row(
            children: [
              const Icon(Icons.check_circle_outline, color: Colors.white, size: 20),
              const SizedBox(width: 10),
              Expanded(
                child: Text(
                  '$appliedCount field${appliedCount > 1 ? 's' : ''} auto-filled from $docType',
                  style: const TextStyle(
                    fontWeight: FontWeight.w500,
                    fontSize: 14,
                  ),
                ),
              ),
            ],
          ),
          backgroundColor: const Color(0xFF1A9B42),
          behavior: SnackBarBehavior.floating,
          shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(10)),
          margin: EdgeInsets.symmetric(horizontal: 20.w, vertical: 16.h),
          duration: const Duration(seconds: 3),
        ),
      );
    }
  }

  @override
  Widget build(BuildContext context) {
    return Consumer<OcrScanProvider>(
      builder: (context, provider, _) {
        // Auto-pop on success
        if (provider.state == OcrScanState.success && !_hasPopped) {
          _hasPopped = true;
          WidgetsBinding.instance.addPostFrameCallback((_) {
            _applyExtractedFields();
            provider.reset();
            Navigator.of(context).pop();
          });
        }

        return Scaffold(
          backgroundColor: Colors.black,
          body: Stack(
            fit: StackFit.expand,
            children: [
              // Layer 1: Camera preview or black background
              _buildCameraPreview(provider),

              // Layer 2: Document guide overlay
              if (provider.state == OcrScanState.scanning ||
                  provider.state == OcrScanState.processing)
                _buildDocumentOverlay(provider),

              // Layer 3: Processing overlay
              if (provider.state == OcrScanState.processing)
                _buildProcessingOverlay(),

              // Layer 4: Error overlay
              if (provider.state == OcrScanState.error)
                _buildErrorOverlay(provider),

              // Layer 5: Top bar (always visible)
              _buildTopBar(provider),

              // Layer 6: Bottom guidance & controls
              if (provider.state == OcrScanState.scanning)
                _buildBottomControls(provider),
            ],
          ),
        );
      },
    );
  }

  // ---------------------------------------------------------------------------
  // Camera Preview
  // ---------------------------------------------------------------------------
  Widget _buildCameraPreview(OcrScanProvider provider) {
    if (provider.textureId != null) {
      return Texture(textureId: provider.textureId!);
    }
    return const Center(
      child: CircularProgressIndicator(
        color: Colors.white,
        strokeWidth: 2,
      ),
    );
  }

  Widget _buildDocumentOverlay(OcrScanProvider provider) {
    return AnimatedBuilder(
      animation: _pulseAnimation,
      builder: (context, child) {
        return CustomPaint(
          painter: _DocumentFramePainter(
            frameColor: provider.isQualityAcceptable
                ? const Color(0xFF1A9B42)
                : solidPrimary,
            overlayOpacity: provider.state == OcrScanState.processing
                ? 0.75
                : 0.55,
            pulseValue: _pulseAnimation.value,
          ),
          size: Size.infinite,
        );
      },
    );
  }

  // ---------------------------------------------------------------------------
  // Processing Overlay
  // ---------------------------------------------------------------------------
  Widget _buildProcessingOverlay() {
    return Center(
      child: Container(
        padding: EdgeInsets.symmetric(horizontal: 32.w, vertical: 24.h),
        decoration: BoxDecoration(
          color: Colors.black.withOpacity(0.7),
          borderRadius: BorderRadius.circular(16),
        ),
        child: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            SizedBox(
              width: 48,
              height: 48,
              child: CircularProgressIndicator(
                color: solidPrimary,
                strokeWidth: 3,
              ),
            ),
            SizedBox(height: 20.h),
            const Text(
              'Reading document...',
              style: TextStyle(
                color: Colors.white,
                fontSize: 16,
                fontWeight: FontWeight.w500,
                letterSpacing: 0.3,
              ),
            ),
            SizedBox(height: 8.h),
            Text(
              'Please hold still',
              style: TextStyle(
                color: Colors.white.withOpacity(0.6),
                fontSize: 13,
              ),
            ),
          ],
        ),
      ),
    );
  }

  // ---------------------------------------------------------------------------
  // Error Overlay
  // ---------------------------------------------------------------------------
  Widget _buildErrorOverlay(OcrScanProvider provider) {
    return Container(
      color: Colors.black.withOpacity(0.85),
      child: Center(
        child: Container(
          margin: EdgeInsets.symmetric(horizontal: 32.w),
          padding: EdgeInsets.all(28.w),
          decoration: BoxDecoration(
            color: pureWhite,
            borderRadius: BorderRadius.circular(16),
            boxShadow: [
              BoxShadow(
                color: Colors.black.withOpacity(0.3),
                blurRadius: 20,
                offset: const Offset(0, 8),
              ),
            ],
          ),
          child: Column(
            mainAxisSize: MainAxisSize.min,
            children: [
              Container(
                width: 56,
                height: 56,
                decoration: BoxDecoration(
                  color: const Color(0xFFFFF0F0),
                  borderRadius: BorderRadius.circular(28),
                ),
                child: const Icon(
                  Icons.error_outline_rounded,
                  color: Color(0xFFBE1B1B),
                  size: 32,
                ),
              ),
              SizedBox(height: 20.h),
              Text(
                'Scan Failed',
                style: TextStyle(
                  color: appBlackShade1,
                  fontSize: 18,
                  fontWeight: FontWeight.w600,
                ),
              ),
              SizedBox(height: 8.h),
              Text(
                provider.errorMessage ?? 'An unknown error occurred.',
                textAlign: TextAlign.center,
                style: TextStyle(
                  color: appBlackShade2,
                  fontSize: 14,
                  height: 1.4,
                ),
              ),
              if (provider.errorCode != null) ...[
                SizedBox(height: 6.h),
                Text(
                  'Error: ${provider.errorCode}',
                  style: TextStyle(
                    color: appBlackShade3,
                    fontSize: 11,
                    fontFamily: 'monospace',
                  ),
                ),
              ],
              SizedBox(height: 28.h),
              if (provider.isRetryable)
                SizedBox(
                  width: double.infinity,
                  height: 48,
                  child: ElevatedButton(
                    onPressed: () => provider.startScan(),
                    style: ElevatedButton.styleFrom(
                      backgroundColor: solidPrimary,
                      foregroundColor: Colors.white,
                      shape: RoundedRectangleBorder(
                        borderRadius: BorderRadius.circular(8),
                      ),
                      elevation: 0,
                    ),
                    child: const Text(
                      'Try Again',
                      style: TextStyle(
                        fontSize: 15,
                        fontWeight: FontWeight.w600,
                      ),
                    ),
                  ),
                ),
              if (provider.isRetryable) SizedBox(height: 12.h),
              SizedBox(
                width: double.infinity,
                height: 48,
                child: OutlinedButton(
                  onPressed: () {
                    provider.reset();
                    Navigator.of(context).pop();
                  },
                  style: OutlinedButton.styleFrom(
                    foregroundColor: solidPrimary,
                    side: BorderSide(color: solidPrimary, width: 1.5),
                    shape: RoundedRectangleBorder(
                      borderRadius: BorderRadius.circular(8),
                    ),
                  ),
                  child: const Text(
                    'Enter Manually',
                    style: TextStyle(
                      fontSize: 15,
                      fontWeight: FontWeight.w600,
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

  // ---------------------------------------------------------------------------
  // Top Bar
  // ---------------------------------------------------------------------------
  Widget _buildTopBar(OcrScanProvider provider) {
    return Positioned(
      top: 0,
      left: 0,
      right: 0,
      child: SafeArea(
        child: Padding(
          padding: EdgeInsets.symmetric(horizontal: 8.w, vertical: 8.h),
          child: Row(
            children: [
              // Close button
              Material(
                color: Colors.black.withOpacity(0.4),
                borderRadius: BorderRadius.circular(24),
                child: InkWell(
                  onTap: () {
                    provider.cancelScan();
                    Navigator.of(context).pop();
                  },
                  borderRadius: BorderRadius.circular(24),
                  child: const Padding(
                    padding: EdgeInsets.all(10),
                    child: Icon(
                      Icons.close_rounded,
                      color: Colors.white,
                      size: 24,
                    ),
                  ),
                ),
              ),
              const Spacer(),
              // Title chip
              Container(
                padding:
                    EdgeInsets.symmetric(horizontal: 16.w, vertical: 8.h),
                decoration: BoxDecoration(
                  color: Colors.black.withOpacity(0.4),
                  borderRadius: BorderRadius.circular(20),
                ),
                child: const Row(
                  mainAxisSize: MainAxisSize.min,
                  children: [
                    Icon(
                      Icons.document_scanner_outlined,
                      color: Colors.white,
                      size: 18,
                    ),
                    SizedBox(width: 6),
                    Text(
                      'Scan Document',
                      style: TextStyle(
                        color: Colors.white,
                        fontSize: 14,
                        fontWeight: FontWeight.w500,
                      ),
                    ),
                  ],
                ),
              ),
              const Spacer(),
              // Spacer to balance the close button
              const SizedBox(width: 44),
            ],
          ),
        ),
      ),
    );
  }

  // ---------------------------------------------------------------------------
  // Bottom Controls (quality guidance + force capture)
  // ---------------------------------------------------------------------------
  Widget _buildBottomControls(OcrScanProvider provider) {
    return Positioned(
      bottom: 0,
      left: 0,
      right: 0,
      child: SafeArea(
        child: Padding(
          padding: EdgeInsets.symmetric(horizontal: 24.w, vertical: 20.h),
          child: Column(
            mainAxisSize: MainAxisSize.min,
            children: [
              // Force capture button
              if (provider.showForceCapture) ...[
                SizedBox(
                  width: double.infinity,
                  height: 48,
                  child: OutlinedButton.icon(
                    onPressed: () => provider.forceCapture(),
                    icon: const Icon(Icons.camera_alt_outlined, size: 20),
                    label: const Text(
                      'Capture Anyway',
                      style: TextStyle(
                        fontSize: 15,
                        fontWeight: FontWeight.w600,
                      ),
                    ),
                    style: OutlinedButton.styleFrom(
                      foregroundColor: Colors.white,
                      side: const BorderSide(color: Colors.white, width: 1.5),
                      shape: RoundedRectangleBorder(
                        borderRadius: BorderRadius.circular(12),
                      ),
                      backgroundColor: Colors.white.withOpacity(0.15),
                    ),
                  ),
                ),
                SizedBox(height: 16.h),
              ],

              // Quality guidance chip
              AnimatedSwitcher(
                duration: const Duration(milliseconds: 300),
                switchInCurve: Curves.easeOut,
                switchOutCurve: Curves.easeIn,
                child: _buildGuidanceChip(provider),
              ),
            ],
          ),
        ),
      ),
    );
  }

  Widget _buildGuidanceChip(OcrScanProvider provider) {
    final isGood = provider.isQualityAcceptable;
    final message = provider.guidanceMessage.isEmpty
        ? 'Position document within the frame'
        : provider.guidanceMessage;

    return Container(
      key: ValueKey(message),
      width: double.infinity,
      padding: EdgeInsets.symmetric(horizontal: 20.w, vertical: 14.h),
      decoration: BoxDecoration(
        color: isGood
            ? const Color(0xFF1A9B42).withOpacity(0.9)
            : Colors.black.withOpacity(0.7),
        borderRadius: BorderRadius.circular(12),
        border: Border.all(
          color: isGood
              ? const Color(0xFF1A9B42)
              : Colors.white.withOpacity(0.2),
          width: 1,
        ),
      ),
      child: Row(
        children: [
          Icon(
            isGood ? Icons.check_circle_rounded : Icons.info_outline_rounded,
            color: isGood ? Colors.white : const Color(0xFFFEC401),
            size: 22,
          ),
          SizedBox(width: 12.w),
          Expanded(
            child: Text(
              message,
              style: const TextStyle(
                color: Colors.white,
                fontSize: 14,
                fontWeight: FontWeight.w500,
                height: 1.3,
              ),
            ),
          ),
        ],
      ),
    );
  }
}

// =============================================================================
// Custom Painter — Document Frame with Cutout
// =============================================================================

class _DocumentFramePainter extends CustomPainter {
  final Color frameColor;
  final double overlayOpacity;
  final double pulseValue;

  _DocumentFramePainter({
    required this.frameColor,
    required this.overlayOpacity,
    required this.pulseValue,
  });

  @override
  void paint(Canvas canvas, Size size) {
    // Document frame dimensions (aspect ratio ~1.6:1 for ID cards)
    final frameWidth = size.width * 0.85;
    final frameHeight = frameWidth / 1.586; // standard ID card ratio
    final frameLeft = (size.width - frameWidth) / 2;
    final frameTop = (size.height - frameHeight) / 2 - 30;
    final frameRect = RRect.fromRectAndRadius(
      Rect.fromLTWH(frameLeft, frameTop, frameWidth, frameHeight),
      const Radius.circular(16),
    );

    // Dark overlay with cutout
    final overlayPaint = Paint()
      ..color = Colors.black.withOpacity(overlayOpacity);
    final overlayPath = Path()
      ..addRect(Rect.fromLTWH(0, 0, size.width, size.height))
      ..addRRect(frameRect)
      ..fillType = PathFillType.evenOdd;
    canvas.drawPath(overlayPath, overlayPaint);

    // Animated frame border
    final borderPaint = Paint()
      ..color = frameColor.withOpacity(pulseValue)
      ..style = PaintingStyle.stroke
      ..strokeWidth = 2.5;
    canvas.drawRRect(frameRect, borderPaint);

    // Corner accents (thick L-shaped corners)
    final cornerPaint = Paint()
      ..color = frameColor
      ..style = PaintingStyle.stroke
      ..strokeWidth = 4
      ..strokeCap = StrokeCap.round;

    const cornerLen = 28.0;
    final rect = frameRect.outerRect;

    // Top-left
    canvas.drawLine(
      Offset(rect.left + 16, rect.top),
      Offset(rect.left + 16 + cornerLen, rect.top),
      cornerPaint,
    );
    canvas.drawLine(
      Offset(rect.left, rect.top + 16),
      Offset(rect.left, rect.top + 16 + cornerLen),
      cornerPaint,
    );

    // Top-right
    canvas.drawLine(
      Offset(rect.right - 16, rect.top),
      Offset(rect.right - 16 - cornerLen, rect.top),
      cornerPaint,
    );
    canvas.drawLine(
      Offset(rect.right, rect.top + 16),
      Offset(rect.right, rect.top + 16 + cornerLen),
      cornerPaint,
    );

    // Bottom-left
    canvas.drawLine(
      Offset(rect.left + 16, rect.bottom),
      Offset(rect.left + 16 + cornerLen, rect.bottom),
      cornerPaint,
    );
    canvas.drawLine(
      Offset(rect.left, rect.bottom - 16),
      Offset(rect.left, rect.bottom - 16 - cornerLen),
      cornerPaint,
    );

    // Bottom-right
    canvas.drawLine(
      Offset(rect.right - 16, rect.bottom),
      Offset(rect.right - 16 - cornerLen, rect.bottom),
      cornerPaint,
    );
    canvas.drawLine(
      Offset(rect.right, rect.bottom - 16),
      Offset(rect.right, rect.bottom - 16 - cornerLen),
      cornerPaint,
    );
  }

  @override
  bool shouldRepaint(covariant _DocumentFramePainter oldDelegate) {
    return oldDelegate.pulseValue != pulseValue ||
        oldDelegate.frameColor != frameColor ||
        oldDelegate.overlayOpacity != overlayOpacity;
  }
}

