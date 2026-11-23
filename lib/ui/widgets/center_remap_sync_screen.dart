/*
 * Copyright (c) Modular Open Source Identity Platform
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

import 'dart:math' as math;

import 'package:flutter/material.dart';
import 'package:flutter_gen/gen_l10n/app_localizations.dart';
import 'package:intl/intl.dart';
import 'package:provider/provider.dart';
import 'package:registration_client/provider/sync_provider.dart';
import 'package:registration_client/utils/app_config.dart';
import 'package:restart_app/restart_app.dart';

class CenterRemapSyncScreen extends StatefulWidget {
  const CenterRemapSyncScreen({super.key});

  @override
  State<CenterRemapSyncScreen> createState() => _CenterRemapSyncScreenState();
}

class _CenterRemapSyncScreenState extends State<CenterRemapSyncScreen>
    with SingleTickerProviderStateMixin {
  late AnimationController _rotationController;

  @override
  void initState() {
    super.initState();
    _rotationController = AnimationController(
      vsync: this,
      duration: const Duration(seconds: 2),
    )..repeat();

    WidgetsBinding.instance.addPostFrameCallback((_) {
      context.read<SyncProvider>().performCenterRemapSync();
    });
  }

  @override
  void dispose() {
    _rotationController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    final l10n = AppLocalizations.of(context)!;
    final syncProvider = context.watch<SyncProvider>();

    Widget body;
    switch (syncProvider.remapSyncStatus) {
      case RemapSyncStatus.success:
        body = _buildSuccessBody(l10n, syncProvider);
        break;
      case RemapSyncStatus.failed:
        body = _buildFailureBody(l10n);
        break;
      default:
        body = _buildProgressBody(l10n, syncProvider);
    }

    return Scaffold(
      backgroundColor: Colors.white,
      appBar: AppBar(
        backgroundColor: appSolidPrimary,
        title: Text(
          l10n.remap_sync_screen_title,
          style: const TextStyle(color: Colors.white),
        ),
        automaticallyImplyLeading: false,
      ),
      body: body,
    );
  }

  Widget _buildProgressBody(AppLocalizations l10n, SyncProvider syncProvider) {
    final progress = syncProvider.remapSyncProgress / 100.0;
    return Padding(
      padding: const EdgeInsets.symmetric(horizontal: 32),
      child: Column(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          AnimatedBuilder(
            animation: _rotationController,
            builder: (_, child) => Transform.rotate(
              angle: _rotationController.value * 2 * math.pi,
              child: child,
            ),
            child: Container(
              width: 80,
              height: 80,
              decoration: BoxDecoration(
                shape: BoxShape.circle,
                border: Border.all(color: const Color(0xFFBBCCEB), width: 3),
              ),
              child: const Icon(Icons.sync, color: appSolidPrimary, size: 36),
            ),
          ),
          const SizedBox(height: 32),
          Text(
            l10n.remap_syncing_message,
            style: const TextStyle(
              fontSize: 18,
              fontWeight: FontWeight.bold,
              color: Color(0xFF333333),
            ),
          ),
          const SizedBox(height: 24),
          Align(
            alignment: Alignment.centerLeft,
            child: Text(
              '${syncProvider.remapSyncProgress}%',
              style: const TextStyle(
                fontSize: 14,
                fontWeight: FontWeight.w600,
                color: appSolidPrimary,
              ),
            ),
          ),
          const SizedBox(height: 8),
          ClipRRect(
            borderRadius: BorderRadius.circular(5),
            child: LinearProgressIndicator(
              value: progress,
              backgroundColor: const Color(0xFFE0E0E0),
              color: appSolidPrimary,
              minHeight: 10,
            ),
          ),
          const SizedBox(height: 24),
          _buildWarningBanner(l10n.remap_sync_in_progress_warning),
        ],
      ),
    );
  }

  Widget _buildSuccessBody(AppLocalizations l10n, SyncProvider syncProvider) {
    final completedAt = syncProvider.remapSyncCompletedAt;
    final formattedTime = completedAt != null
        ? DateFormat('d MMM yyyy, hh:mm a').format(completedAt)
        : '';

    return Column(
      children: [
        Expanded(
          child: Padding(
            padding: const EdgeInsets.symmetric(horizontal: 32),
            child: Column(
              mainAxisAlignment: MainAxisAlignment.center,
              children: [
                Container(
                  width: 80,
                  height: 80,
                  decoration: const BoxDecoration(
                    color: Color(0xFFE8F5E9),
                    shape: BoxShape.circle,
                  ),
                  child: const Icon(Icons.check_circle_outline,
                      color: Color(0xFF2E7D32), size: 44),
                ),
                const SizedBox(height: 24),
                Text(
                  l10n.remap_sync_completed_title,
                  textAlign: TextAlign.center,
                  style: const TextStyle(
                    fontSize: 18,
                    fontWeight: FontWeight.bold,
                    color: Color(0xFF333333),
                  ),
                ),
                const SizedBox(height: 12),
                Text(
                  l10n.remap_sync_completed_subtitle,
                  textAlign: TextAlign.center,
                  style: const TextStyle(fontSize: 13, color: Color(0xFF6F6E6E)),
                ),
                if (formattedTime.isNotEmpty) ...[
                  const SizedBox(height: 24),
                  Container(
                    padding: const EdgeInsets.symmetric(
                        horizontal: 16, vertical: 12),
                    decoration: BoxDecoration(
                      border: Border.all(color: const Color(0xFFE0E0E0)),
                      borderRadius: BorderRadius.circular(8),
                    ),
                    child: Row(
                      mainAxisAlignment: MainAxisAlignment.spaceBetween,
                      children: [
                        Text(l10n.remap_synced_at,
                            style: const TextStyle(
                                fontSize: 13, color: Color(0xFF6F6E6E))),
                        Text(formattedTime,
                            style: const TextStyle(
                              fontSize: 13,
                              fontWeight: FontWeight.bold,
                              color: Color(0xFF333333),
                            )),
                      ],
                    ),
                  ),
                ],
              ],
            ),
          ),
        ),
        Padding(
          padding: const EdgeInsets.all(24),
          child: SizedBox(
            width: double.infinity,
            child: ElevatedButton(
              onPressed: () => Restart.restartApp(),
              style: ElevatedButton.styleFrom(
                backgroundColor: appSolidPrimary,
                padding: const EdgeInsets.symmetric(vertical: 16),
                shape: RoundedRectangleBorder(
                    borderRadius: BorderRadius.circular(8)),
              ),
              child: Text(
                l10n.restart,
                style: const TextStyle(
                  fontSize: 14,
                  fontWeight: FontWeight.bold,
                  color: Colors.white,
                  letterSpacing: 0.5,
                ),
              ),
            ),
          ),
        ),
      ],
    );
  }

  Widget _buildFailureBody(AppLocalizations l10n) {
    return Column(
      children: [
        Expanded(
          child: Padding(
            padding: const EdgeInsets.symmetric(horizontal: 32),
            child: Column(
              mainAxisAlignment: MainAxisAlignment.center,
              children: [
                Container(
                  width: 80,
                  height: 80,
                  decoration: const BoxDecoration(
                    color: Color(0xFFFFEBEE),
                    shape: BoxShape.circle,
                  ),
                  child: const Icon(Icons.cancel_outlined,
                      color: Color(0xFFC62828), size: 44),
                ),
                const SizedBox(height: 24),
                Text(
                  l10n.remap_sync_failed_title,
                  style: const TextStyle(
                    fontSize: 18,
                    fontWeight: FontWeight.bold,
                    color: Color(0xFF333333),
                  ),
                ),
                const SizedBox(height: 12),
                Text(
                  l10n.remap_sync_failed_subtitle,
                  textAlign: TextAlign.center,
                  style: const TextStyle(fontSize: 13, color: Color(0xFF6F6E6E)),
                ),
              ],
            ),
          ),
        ),
        Padding(
          padding: const EdgeInsets.all(24),
          child: SizedBox(
            width: double.infinity,
            child: ElevatedButton(
              onPressed: () {
                final provider = context.read<SyncProvider>();
                provider.resetRemapSyncState();
                provider.performCenterRemapSync();
              },
              style: ElevatedButton.styleFrom(
                backgroundColor: appSolidPrimary,
                padding: const EdgeInsets.symmetric(vertical: 16),
                shape: RoundedRectangleBorder(
                    borderRadius: BorderRadius.circular(8)),
              ),
              child: Text(
                l10n.retry,
                style: const TextStyle(
                  fontSize: 14,
                  fontWeight: FontWeight.bold,
                  color: Colors.white,
                  letterSpacing: 0.5,
                ),
              ),
            ),
          ),
        ),
      ],
    );
  }

  Widget _buildWarningBanner(String message) {
    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 12),
      decoration: BoxDecoration(
        color: const Color(0xFFFFF8E1),
        borderRadius: BorderRadius.circular(8),
        border: Border.all(color: const Color(0xFFFFCC80)),
      ),
      child: Row(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          const Icon(Icons.warning_amber_rounded,
              color: Color(0xFFFF8F00), size: 20),
          const SizedBox(width: 8),
          Expanded(
            child: Text(message,
                style: const TextStyle(fontSize: 13, color: Color(0xFF6F6E6E))),
          ),
        ],
      ),
    );
  }
}