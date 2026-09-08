/*
 * Copyright (c) Modular Open Source Identity Platform
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

import 'dart:async';

import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:provider/provider.dart';
import 'package:registration_client/model/field.dart';
import 'package:registration_client/model/ocr_error_messages.dart';
import 'package:registration_client/provider/global_provider.dart';
import 'package:registration_client/provider/ocr_scan_provider.dart';
import 'package:registration_client/provider/registration_task_provider.dart';
import 'package:registration_client/utils/app_config.dart';

/// Full-screen OCR document scanning page with live camera preview,
/// real-time quality guidance, and auto-fill support.
class OcrScanPage extends StatefulWidget {
  /// The form fields from the current screen — used to determine each
  /// extracted field's [type] (simpleType vs string) for correct storage.
  final List<Field?>? screenFields;

  const OcrScanPage({super.key, this.screenFields});

  @override
  State<OcrScanPage> createState() => _OcrScanPageState();
}

class _OcrScanPageState extends State<OcrScanPage>
    with SingleTickerProviderStateMixin {
  bool _hasPopped = false;
  bool _hasAuditedError = false;
  bool _wasQualityGood = false;
  bool _isAlignmentPhase = true;
  Timer? _alignmentTimer;

  late AnimationController _countdownController;
  late Animation<double> _countdownAnimation;

  @override
  void initState() {
    super.initState();

    _countdownController = AnimationController(
      vsync: this,
      duration: const Duration(milliseconds: 1800),
    );
    _countdownAnimation = CurvedAnimation(
      parent: _countdownController,
      curve: Curves.linear,
    );

    // Start the scan
    WidgetsBinding.instance.addPostFrameCallback((_) {
      context.read<OcrScanProvider>().startScan();
      _restartAlignmentPhase();
    });
  }

  @override
  void dispose() {
    _alignmentTimer?.cancel();
    _countdownController.dispose();
    // Ensure camera session is cancelled on page disposal
    context.read<OcrScanProvider>().cancelScan();
    super.dispose();
  }

  void _restartAlignmentPhase() {
    _alignmentTimer?.cancel();
    setState(() {
      _isAlignmentPhase = true;
      _wasQualityGood = false;
    });
    _countdownController.reset();
    _alignmentTimer = Timer(const Duration(seconds: 5), () {
      if (mounted) {
        setState(() => _isAlignmentPhase = false);
      }
    });
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
      final docType = ocrProvider.documentType ?? 'document';
      final confidencePct = ocrProvider.confidence;
      final confidenceText = confidencePct != null
          ? ' (${confidencePct.toStringAsFixed(1)}% confidence)'
          : '';
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(
          content: Row(
            children: [
              const Icon(Icons.check_circle_outline, color: Colors.white, size: 20),
              const SizedBox(width: 10),
              Expanded(
                child: Text(
                  '$appliedCount field${appliedCount > 1 ? 's' : ''} auto-filled from $docType$confidenceText',
                  style: const TextStyle(
                    fontWeight: FontWeight.w500,
                    fontSize: 14,
                  ),
                ),
              ),
            ],
          ),
          backgroundColor: dashBoardPacketUploadColor,
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
            try {
              context.read<GlobalProvider>().getAudit(
                    "REG-EVT-117",
                    "REG-MOD-103",
                    [
                      provider.documentType ?? 'unknown',
                      '${provider.extractedData.length}',
                      provider.confidence?.toStringAsFixed(1) ?? 'N/A',
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

        // Audit on error
        if (provider.state == OcrScanState.error && !_hasAuditedError) {
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
          backgroundColor: Colors.black,
          body: Stack(
            fit: StackFit.expand,
            children: [
              // Layer 1: Camera preview
              _buildCameraPreview(provider),

              // Layer 2: Professional Document Framing Reticle
              if (provider.state == OcrScanState.scanning)
                Positioned.fill(
                  child: CustomPaint(
                    painter: _DocumentFramePainter(
                      isGoodQuality: provider.isQualityAcceptable,
                    ),
                  ),
                ),

              // Layer 3: Processing overlay
              if (provider.state == OcrScanState.processing)
                _buildProcessingOverlay(),

              // Layer 4: Error overlay
              if (provider.state == OcrScanState.error)
                _buildErrorOverlay(provider),

              // Layer 5: Top Navigation Bar
              _buildTopBar(provider),

              // Layer 6: Bottom guidance & round shutter controls
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
        strokeWidth: 2.5,
      ),
    );
  }

  // ---------------------------------------------------------------------------
  // Processing Overlay — Modern Frosted Dark Card
  // ---------------------------------------------------------------------------
  Widget _buildProcessingOverlay() {
    return Container(
      color: Colors.black.withOpacity(0.75),
      child: Center(
        child: Container(
          margin: EdgeInsets.symmetric(horizontal: 36.w),
          padding: EdgeInsets.symmetric(horizontal: 28.w, vertical: 32.h),
          decoration: BoxDecoration(
            color: const Color(0xFF1E293B),
            borderRadius: BorderRadius.circular(20.r),
            boxShadow: [
              BoxShadow(
                color: Colors.black.withOpacity(0.4),
                blurRadius: 24,
                offset: const Offset(0, 8),
              ),
            ],
          ),
          child: Column(
            mainAxisSize: MainAxisSize.min,
            children: [
              SizedBox(
                width: 52.w,
                height: 52.w,
                child: const CircularProgressIndicator(
                  color: Color(0xFF38BDF8),
                  strokeWidth: 3.5,
                ),
              ),
              SizedBox(height: 24.h),
              Text(
                'Reading Document...',
                style: TextStyle(
                  color: Colors.white,
                  fontSize: 17.sp,
                  fontWeight: FontWeight.w600,
                  letterSpacing: 0.2,
                ),
              ),
              SizedBox(height: 8.h),
              Text(
                'Extracting demographic details',
                textAlign: TextAlign.center,
                style: TextStyle(
                  color: Colors.white.withOpacity(0.65),
                  fontSize: 13.sp,
                  height: 1.3,
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }

  // ---------------------------------------------------------------------------
  // Error Overlay — Professional Card
  // ---------------------------------------------------------------------------
  Widget _buildErrorOverlay(OcrScanProvider provider) {
    return Container(
      color: Colors.black.withOpacity(0.85),
      child: Center(
        child: Container(
          margin: EdgeInsets.symmetric(horizontal: 28.w),
          padding: EdgeInsets.all(24.w),
          decoration: BoxDecoration(
            color: pureWhite,
            borderRadius: BorderRadius.circular(20.r),
            boxShadow: [
              BoxShadow(
                color: Colors.black.withOpacity(0.3),
                blurRadius: 24,
                offset: const Offset(0, 8),
              ),
            ],
          ),
          child: Column(
            mainAxisSize: MainAxisSize.min,
            children: [
              Container(
                width: 56.w,
                height: 56.w,
                decoration: const BoxDecoration(
                  color: Color(0xFFFFF0F0),
                  shape: BoxShape.circle,
                ),
                child: const Icon(
                  Icons.error_outline_rounded,
                  color: Color(0xFFBE1B1B),
                  size: 32,
                ),
              ),
              SizedBox(height: 18.h),
              Text(
                'Scan Unsuccessful',
                style: TextStyle(
                  color: appBlackShade1,
                  fontSize: 18.sp,
                  fontWeight: FontWeight.w700,
                ),
              ),
              SizedBox(height: 8.h),
              Text(
                ocrErrorMessage(provider.errorCode, provider.errorMessage),
                textAlign: TextAlign.center,
                style: TextStyle(
                  color: appBlackShade2,
                  fontSize: 14.sp,
                  height: 1.4,
                ),
              ),
              SizedBox(height: 24.h),
              if (provider.isRetryable) ...[
                SizedBox(
                  width: double.infinity,
                  height: 48.h,
                  child: ElevatedButton.icon(
                    onPressed: () {
                      provider.startScan();
                      _restartAlignmentPhase();
                    },
                    icon: const Icon(Icons.refresh_rounded, size: 20),
                    style: ElevatedButton.styleFrom(
                      backgroundColor: solidPrimary,
                      foregroundColor: Colors.white,
                      shape: RoundedRectangleBorder(
                        borderRadius: BorderRadius.circular(10.r),
                      ),
                      elevation: 0,
                    ),
                    label: Text(
                      'Try Again',
                      style: TextStyle(
                        fontSize: 15.sp,
                        fontWeight: FontWeight.w600,
                      ),
                    ),
                  ),
                ),
                SizedBox(height: 10.h),
              ],
              if (provider.isPermanentlyDenied) ...[
                SizedBox(
                  width: double.infinity,
                  height: 48.h,
                  child: ElevatedButton.icon(
                    onPressed: () => provider.openSettings(),
                    icon: const Icon(Icons.settings, size: 18),
                    style: ElevatedButton.styleFrom(
                      backgroundColor: const Color(0xFFFF9800),
                      foregroundColor: Colors.white,
                      shape: RoundedRectangleBorder(
                        borderRadius: BorderRadius.circular(10.r),
                      ),
                      elevation: 0,
                    ),
                    label: Text(
                      'Open Settings',
                      style: TextStyle(
                        fontSize: 15.sp,
                        fontWeight: FontWeight.w600,
                      ),
                    ),
                  ),
                ),
                SizedBox(height: 10.h),
              ],
              SizedBox(
                width: double.infinity,
                height: 48.h,
                child: OutlinedButton(
                  onPressed: () {
                    provider.reset();
                    Navigator.of(context).pop();
                  },
                  style: OutlinedButton.styleFrom(
                    foregroundColor: solidPrimary,
                    side: BorderSide(color: solidPrimary, width: 1.5),
                    shape: RoundedRectangleBorder(
                      borderRadius: BorderRadius.circular(10.r),
                    ),
                  ),
                  child: Text(
                    'Enter Manually',
                    style: TextStyle(
                      fontSize: 15.sp,
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
  // Top Bar — Floating Glass Header
  // ---------------------------------------------------------------------------
  Widget _buildTopBar(OcrScanProvider provider) {
    return Positioned(
      top: 0,
      left: 0,
      right: 0,
      child: Container(
        decoration: BoxDecoration(
          gradient: LinearGradient(
            begin: Alignment.topCenter,
            end: Alignment.bottomCenter,
            colors: [
              Colors.black.withOpacity(0.7),
              Colors.transparent,
            ],
          ),
        ),
        child: SafeArea(
          bottom: false,
          child: Padding(
            padding: EdgeInsets.symmetric(horizontal: 16.w, vertical: 12.h),
            child: Row(
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: [
                // Close button
                Material(
                  color: Colors.black.withOpacity(0.35),
                  shape: const CircleBorder(),
                  child: InkWell(
                    customBorder: const CircleBorder(),
                    onTap: () {
                      provider.cancelScan();
                      Navigator.of(context).pop();
                    },
                    child: Padding(
                      padding: EdgeInsets.all(10.w),
                      child: const Icon(
                        Icons.close_rounded,
                        color: Colors.white,
                        size: 22,
                      ),
                    ),
                  ),
                ),

                // Title Pill
                Container(
                  padding: EdgeInsets.symmetric(horizontal: 16.w, vertical: 8.h),
                  decoration: BoxDecoration(
                    color: Colors.black.withOpacity(0.4),
                    borderRadius: BorderRadius.circular(24.r),
                    border: Border.all(
                      color: Colors.white.withOpacity(0.15),
                      width: 1,
                    ),
                  ),
                  child: Row(
                    mainAxisSize: MainAxisSize.min,
                    children: [
                      Icon(
                        Icons.document_scanner_rounded,
                        color: Colors.white,
                        size: 17.sp,
                      ),
                      SizedBox(width: 8.w),
                      Text(
                        'Scan Document',
                        style: TextStyle(
                          color: Colors.white,
                          fontSize: 14.sp,
                          fontWeight: FontWeight.w600,
                          letterSpacing: 0.2,
                        ),
                      ),
                    ],
                  ),
                ),

                // Symmetrical placeholder for balance
                SizedBox(width: 42.w),
              ],
            ),
          ),
        ),
      ),
    );
  }

  // ---------------------------------------------------------------------------
  // Bottom Controls — Floating White Guidance Box & Round Shutter Button
  // ---------------------------------------------------------------------------
  Widget _buildBottomControls(OcrScanProvider provider) {
    // Manage countdown animation based on quality state (skip during alignment phase)
    final isGood = _isAlignmentPhase ? false : provider.isQualityAcceptable;
    if (isGood && !_wasQualityGood) {
      // Quality just became good — start countdown
      _countdownController.forward(from: 0);
    } else if (!isGood && _wasQualityGood) {
      // Quality just became bad — reset countdown
      _countdownController.reset();
    }
    _wasQualityGood = isGood;

    return Positioned(
      bottom: 0,
      left: 0,
      right: 0,
      child: Container(
        decoration: BoxDecoration(
          gradient: LinearGradient(
            begin: Alignment.bottomCenter,
            end: Alignment.topCenter,
            colors: [
              Colors.black.withOpacity(0.85),
              Colors.black.withOpacity(0.4),
              Colors.transparent,
            ],
            stops: const [0.0, 0.65, 1.0],
          ),
        ),
        child: SafeArea(
          top: false,
          child: Padding(
            padding: EdgeInsets.fromLTRB(20.w, 12.h, 20.w, 20.h),
            child: Column(
              mainAxisSize: MainAxisSize.min,
              children: [
                // Pop-up camera guidance in white bg rounded corner box with good font
                SizedBox(
                  height: 56.h,
                  child: AnimatedSwitcher(
                    duration: const Duration(milliseconds: 250),
                    switchInCurve: Curves.easeOutCubic,
                    switchOutCurve: Curves.easeInCubic,
                    child: _buildGuidanceCard(provider),
                  ),
                ),
                SizedBox(height: 20.h),

                // Round Shutter Button with countdown ring
                _buildShutterButton(provider),
                SizedBox(height: 8.h),
                Text(
                  _isAlignmentPhase
                      ? 'Get ready — aligning...'
                      : (isGood ? 'Hold steady — capturing...' : 'Auto-capture active \u2022 Tap to snap'),
                  style: TextStyle(
                    color: Colors.white.withOpacity(0.55),
                    fontSize: 12.sp,
                    fontWeight: FontWeight.w400,
                    letterSpacing: 0.2,
                  ),
                ),
              ],
            ),
          ),
        ),
      ),
    );
  }

  /// White rounded-corner guidance card directly above the shutter button
  Widget _buildGuidanceCard(OcrScanProvider provider) {
    final isGood = _isAlignmentPhase ? false : provider.isQualityAcceptable;
    final message = _isAlignmentPhase
        ? 'Align document within the frame'
        : (provider.guidanceMessage.isEmpty
            ? 'Align document within the frame'
            : provider.guidanceMessage);

    final Color badgeBg = isGood
        ? const Color(0xFFE8F5E9)
        : const Color(0xFFFFF8E1);
    final Color badgeIconColor = isGood
        ? const Color(0xFF2E7D32)
        : const Color(0xFFF57F17);
    final IconData icon = isGood
        ? Icons.check_circle_rounded
        : (message.toLowerCase().contains('dark')
            ? Icons.wb_incandescent_outlined
            : message.toLowerCase().contains('bright')
                ? Icons.brightness_high_rounded
                : Icons.center_focus_strong_rounded);

    return Container(
      key: ValueKey('${provider.guidanceMessage}_$isGood$_isAlignmentPhase'),
      padding: EdgeInsets.symmetric(horizontal: 18.w, vertical: 12.h),
      decoration: BoxDecoration(
        color: Colors.white,
        borderRadius: BorderRadius.circular(16.r),
        boxShadow: [
          BoxShadow(
            color: Colors.black.withOpacity(0.22),
            blurRadius: 18,
            offset: const Offset(0, 4),
          ),
        ],
      ),
      child: Row(
        mainAxisSize: MainAxisSize.min,
        children: [
          Container(
            width: 32.w,
            height: 32.w,
            decoration: BoxDecoration(
              color: badgeBg,
              shape: BoxShape.circle,
            ),
            child: Icon(
              icon,
              color: badgeIconColor,
              size: 18.sp,
            ),
          ),
          SizedBox(width: 12.w),
          Flexible(
            child: Text(
              isGood && message == 'Hold steady'
                  ? 'Hold steady — capturing...'
                  : message,
              style: TextStyle(
                color: const Color(0xFF1E293B),
                fontSize: 14.sp,
                fontWeight: FontWeight.w600,
                letterSpacing: 0.1,
              ),
            ),
          ),
        ],
      ),
    );
  }

  /// Large circular shutter button with countdown progress ring
  Widget _buildShutterButton(OcrScanProvider provider) {
    final isGood = provider.isQualityAcceptable;
    final Color ringColor = isGood ? const Color(0xFF22C55E) : Colors.white;

    return GestureDetector(
      onTap: () => provider.forceCapture(),
      child: SizedBox(
        width: 84.w,
        height: 84.w,
        child: Stack(
          alignment: Alignment.center,
          children: [
            // Countdown progress ring (visible only when quality is good)
            if (isGood)
              AnimatedBuilder(
                animation: _countdownAnimation,
                builder: (context, child) {
                  return CustomPaint(
                    size: Size(84.w, 84.w),
                    painter: _CountdownRingPainter(
                      progress: _countdownAnimation.value,
                      color: ringColor,
                      strokeWidth: 4.w,
                    ),
                  );
                },
              ),
            // Static border ring
            Container(
              width: 76.w,
              height: 76.w,
              decoration: BoxDecoration(
                shape: BoxShape.circle,
                border: Border.all(
                  color: ringColor,
                  width: 4.w,
                ),
                boxShadow: [
                  BoxShadow(
                    color: ringColor.withOpacity(0.35),
                    blurRadius: 16,
                    spreadRadius: 2,
                  ),
                ],
              ),
            ),
            // Inner white circle with camera icon
            Container(
              width: 60.w,
              height: 60.w,
              decoration: const BoxDecoration(
                color: Colors.white,
                shape: BoxShape.circle,
              ),
              child: Center(
                child: Icon(
                  Icons.camera_alt_rounded,
                  color: solidPrimary,
                  size: 28.sp,
                ),
              ),
            ),
          ],
        ),
      ),
    );
  }
}

/// Custom painter that draws a circular countdown progress ring.
class _CountdownRingPainter extends CustomPainter {
  final double progress;
  final Color color;
  final double strokeWidth;

  _CountdownRingPainter({
    required this.progress,
    required this.color,
    required this.strokeWidth,
  });

  @override
  void paint(Canvas canvas, Size size) {
    final center = Offset(size.width / 2, size.height / 2);
    final radius = (size.width - strokeWidth) / 2;

    // Background track (subtle)
    final trackPaint = Paint()
      ..color = color.withOpacity(0.15)
      ..style = PaintingStyle.stroke
      ..strokeWidth = strokeWidth
      ..strokeCap = StrokeCap.round;
    canvas.drawCircle(center, radius, trackPaint);

    // Progress arc
    final progressPaint = Paint()
      ..color = color
      ..style = PaintingStyle.stroke
      ..strokeWidth = strokeWidth
      ..strokeCap = StrokeCap.round;

    final sweepAngle = 2 * 3.14159265 * progress;
    final startAngle = -3.14159265 / 2; // Start from top
    canvas.drawArc(
      Rect.fromCircle(center: center, radius: radius),
      startAngle,
      sweepAngle,
      false,
      progressPaint,
    );
  }

  @override
  bool shouldRepaint(covariant _CountdownRingPainter oldDelegate) {
    return oldDelegate.progress != progress ||
        oldDelegate.color != color ||
        oldDelegate.strokeWidth != strokeWidth;
  }
}

/// Custom painter that draws a professional document reticle with corner brackets
class _DocumentFramePainter extends CustomPainter {
  final bool isGoodQuality;

  _DocumentFramePainter({required this.isGoodQuality});

  @override
  void paint(Canvas canvas, Size size) {
    // Document aspect ratio
    final double cardWidth = size.width * 0.86;
    final double cardHeight = cardWidth * 1.38;

    final double left = (size.width - cardWidth) / 2;
    final double top = (size.height - cardHeight) / 2 - 35;
    final Rect rect = Rect.fromLTWH(left, top, cardWidth, cardHeight);
    final RRect rrect = RRect.fromRectAndRadius(rect, const Radius.circular(16));

    // Semi-transparent scrim outside the cutout
    final Path backgroundPath = Path()..addRect(Rect.fromLTWH(0, 0, size.width, size.height));
    final Path cutoutPath = Path()..addRRect(rrect);
    final Path scrimPath = Path.combine(PathOperation.difference, backgroundPath, cutoutPath);

    final Paint scrimPaint = Paint()..color = Colors.black.withOpacity(0.38);
    canvas.drawPath(scrimPath, scrimPaint);

    // Subtle border
    final Paint borderPaint = Paint()
      ..color = isGoodQuality
          ? const Color(0xFF22C55E).withOpacity(0.9)
          : Colors.white.withOpacity(0.35)
      ..style = PaintingStyle.stroke
      ..strokeWidth = 1.5;
    canvas.drawRRect(rrect, borderPaint);

    // Corner brackets
    final Paint cornerPaint = Paint()
      ..color = isGoodQuality
          ? const Color(0xFF22C55E)
          : Colors.white
      ..style = PaintingStyle.stroke
      ..strokeWidth = 4.0
      ..strokeCap = StrokeCap.round;

    const double cornerLength = 30.0;
    const double cornerRadius = 16.0;

    // Top-Left
    final Path tlPath = Path()
      ..moveTo(left, top + cornerLength)
      ..lineTo(left, top + cornerRadius)
      ..arcToPoint(Offset(left + cornerRadius, top), radius: const Radius.circular(cornerRadius))
      ..lineTo(left + cornerLength, top);
    canvas.drawPath(tlPath, cornerPaint);

    // Top-Right
    final Path trPath = Path()
      ..moveTo(left + cardWidth - cornerLength, top)
      ..lineTo(left + cardWidth - cornerRadius, top)
      ..arcToPoint(Offset(left + cardWidth, top + cornerRadius), radius: const Radius.circular(cornerRadius))
      ..lineTo(left + cardWidth, top + cornerLength);
    canvas.drawPath(trPath, cornerPaint);

    // Bottom-Left
    final Path blPath = Path()
      ..moveTo(left, top + cardHeight - cornerLength)
      ..lineTo(left, top + cardHeight - cornerRadius)
      ..arcToPoint(Offset(left + cornerRadius, top + cardHeight), radius: const Radius.circular(cornerRadius))
      ..lineTo(left + cornerLength, top + cardHeight);
    canvas.drawPath(blPath, cornerPaint);

    // Bottom-Right
    final Path brPath = Path()
      ..moveTo(left + cardWidth - cornerLength, top + cardHeight)
      ..lineTo(left + cardWidth - cornerRadius, top + cardHeight)
      ..arcToPoint(Offset(left + cardWidth, top + cardHeight - cornerRadius), radius: const Radius.circular(cornerRadius))
      ..lineTo(left + cardWidth, top + cardHeight - cornerLength);
    canvas.drawPath(brPath, cornerPaint);
  }

  @override
  bool shouldRepaint(covariant _DocumentFramePainter oldDelegate) {
    return oldDelegate.isGoodQuality != isGoodQuality;
  }
}
