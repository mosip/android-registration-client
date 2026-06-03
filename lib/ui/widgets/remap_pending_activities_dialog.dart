/*
 * Copyright (c) Modular Open Source Identity Platform
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *
*/

import 'package:flutter/material.dart';
import 'package:flutter_gen/gen_l10n/app_localizations.dart';
import 'package:registration_client/ui/approve_packet/approve_packet_ui.dart';
import 'package:registration_client/ui/export_packet/export_packet_ui.dart';
import 'package:registration_client/utils/app_config.dart';

class RemapPendingActivitiesDialog extends StatelessWidget {
  final int pendingUploadCount;
  final int pendingApprovalCount;

  const RemapPendingActivitiesDialog({
    super.key,
    required this.pendingUploadCount,
    required this.pendingApprovalCount,
  });

  @override
  Widget build(BuildContext context) {
    final l10n = AppLocalizations.of(context)!;
    final double screenWidth = MediaQuery.of(context).size.width;
    final double dialogWidth = (screenWidth * 0.88).clamp(0.0, 480.0);
    final double hInset = (screenWidth - dialogWidth) / 2;

    return Dialog(
      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
      insetPadding: EdgeInsets.symmetric(horizontal: hInset, vertical: 24),
      child: SingleChildScrollView(
        child: Padding(
          padding: const EdgeInsets.fromLTRB(25, 40, 25, 40),
          child: Column(
            mainAxisSize: MainAxisSize.min,
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              // Header row
              Row(
                crossAxisAlignment: CrossAxisAlignment.start,
                mainAxisAlignment: MainAxisAlignment.center,
                children: [
                  Container(
                    width: 44,
                    height: 44,
                    decoration: const BoxDecoration(
                      color: Color(0xFFFFF3E0),
                      shape: BoxShape.circle,
                    ),
                    child: const Icon(
                      Icons.warning_amber_rounded,
                      color: Color(0xFFFF8F00),
                      size: 24,
                    ),
                  ),
                  const SizedBox(width: 10),
                  Expanded(
                    child: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        Padding(
                          padding: const EdgeInsets.only(bottom: 5),
                          child: Text(
                            l10n.complete_pending_activities,
                            style: const TextStyle(
                              fontSize: 15,
                              fontWeight: FontWeight.bold,
                              color: Color(0xFF333333),
                            ),
                          ),
                        ),
                        Text(
                          l10n.pending_activities_before_remap,
                          style: const TextStyle(
                            fontSize: 13,
                            color: Color(0xFF6F6E6E),
                          ),
                        ),
                      ],
                    ),
                  ),
                ],
              ),
              const SizedBox(height: 15),
              const Divider(height: 1, thickness: 1, color: Color(0xFFE0E0E0)),
              const SizedBox(height: 15),
              if (pendingApprovalCount > 0) ...[
                _PendingCard(
                  icon: Icons.assignment_outlined,
                  title: l10n.pending_packets_awaiting_approval,
                  subtitle: l10n.packets_require_supervisor_approval(
                      pendingApprovalCount.toString()),
                  onTap: () {
                    Navigator.of(context).pop();
                    Navigator.push(
                      context,
                      MaterialPageRoute(
                          builder: (_) => const ApprovePacketsPage()),
                    );
                  },
                ),
                const SizedBox(height: 15),
              ],
              if (pendingUploadCount > 0) ...[
                _PendingCard(
                  icon: Icons.sync,
                  title: l10n.pending_packets_awaiting_upload,
                  subtitle: l10n.packets_queued_locally(
                      pendingUploadCount.toString()),
                  onTap: () {
                    Navigator.of(context).pop();
                    Navigator.push(
                      context,
                      MaterialPageRoute(
                          builder: (_) => const ExportPacketsPage()),
                    );
                  },
                ),
                const SizedBox(height: 15),
              ],
              // Info box
              Container(
                padding: const EdgeInsets.symmetric(
                    horizontal: 12, vertical: 12),
                decoration: BoxDecoration(
                  color: const Color(0xFFEEF2FF),
                  borderRadius: BorderRadius.circular(8),
                ),
                child: Row(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    const Icon(Icons.info_outline,
                        color: Color(0xFF214FBF), size: 18),
                    const SizedBox(width: 8),
                    Expanded(
                      child: Text(
                        l10n.sync_button_auto_enabled,
                        style: const TextStyle(
                          fontSize: 13,
                          color: Color(0xFF214FBF),
                        ),
                      ),
                    ),
                  ],
                ),
              ),
              const SizedBox(height: 25),
              SizedBox(
                width: double.infinity,
                child: ElevatedButton(
                  onPressed: () => Navigator.of(context).pop(),
                  style: ElevatedButton.styleFrom(
                    backgroundColor: appSolidPrimary,
                    padding: const EdgeInsets.symmetric(vertical: 16),
                    shape: RoundedRectangleBorder(
                      borderRadius: BorderRadius.circular(8),
                    ),
                  ),
                  child: Text(
                    l10n.okay_got_it,
                    style: const TextStyle(
                      fontSize: 14,
                      fontWeight: FontWeight.bold,
                      color: Colors.white,
                      letterSpacing: 0.5,
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

class _PendingCard extends StatelessWidget {
  final IconData icon;
  final String title;
  final String subtitle;
  final VoidCallback onTap;

  const _PendingCard({
    required this.icon,
    required this.title,
    required this.subtitle,
    required this.onTap,
  });

  @override
  Widget build(BuildContext context) {
    return InkWell(
      onTap: onTap,
      borderRadius: BorderRadius.circular(8),
      child: Container(
        padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 14),
        decoration: BoxDecoration(
          color: const Color(0xFFFFF8E1),
          borderRadius: BorderRadius.circular(8),
          border: Border.all(color: const Color(0xFFFFCC80), width: 1),
        ),
        child: Row(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Stack(
              clipBehavior: Clip.none,
              children: [
                Container(
                  width: 40,
                  height: 40,
                  decoration: const BoxDecoration(
                    color: Color(0xFFFF8F00),
                    shape: BoxShape.circle,
                  ),
                  child: Icon(icon, color: Colors.white, size: 20),
                ),
                Positioned(
                  top: -2,
                  right: -2,
                  child: Container(
                    width: 10,
                    height: 10,
                    decoration: const BoxDecoration(
                      color: Color(0xFFFF8F00),
                      shape: BoxShape.circle,
                      border: Border.fromBorderSide(
                        BorderSide(color: Colors.white, width: 1.5),
                      ),
                    ),
                  ),
                ),
              ],
            ),
            const SizedBox(width: 12),
            Expanded(
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Text(
                    title,
                    style: const TextStyle(
                      fontSize: 14,
                      fontWeight: FontWeight.bold,
                      color: Color(0xFF333333),
                    ),
                  ),
                  const SizedBox(height: 6),
                  Text(
                    subtitle,
                    style: const TextStyle(
                      fontSize: 13,
                      color: Color(0xFF6F6E6E),
                    ),
                  ),
                ],
              ),
            ),
            const SizedBox(width: 8),
            const Padding(
              padding: EdgeInsets.only(top: 2),
              child: Icon(
                Icons.chevron_right,
                color: Color(0xFF9E9E9E),
                size: 20,
              ),
            ),
          ],
        ),
      ),
    );
  }
}