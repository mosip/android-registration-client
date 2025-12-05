import 'dart:convert';
import 'package:flutter/material.dart';
import 'package:intl/intl.dart';
import 'package:flutter_gen/gen_l10n/app_localizations.dart';
import 'package:provider/provider.dart';
import 'package:registration_client/platform_spi/sync_response_service.dart';
import 'package:registration_client/utils/sync_job_def.dart';

import '../../../provider/sync_provider.dart';

// Dart equivalent of the Java PACKET_JOBS constant
const List<String> PACKET_JOBS = ['RPS_J00006', 'RSJ_J00014', 'PUJ_J00017'];

class ScheduledJobsSettings extends StatelessWidget {
  const ScheduledJobsSettings({
    super.key,
    required this.jobJsonList,
    this.onRefreshJob,
  });

  final List<String?> jobJsonList;
  final void Function(String jobId)? onRefreshJob;

  @override
  Widget build(BuildContext context) {
    final jobs = jobJsonList
        .whereType<String>()
        .map((e) => _ScheduledJob.fromJson(json.decode(e) as Map<String, dynamic>))
        .toList();

    final bottomInset = MediaQuery.of(context).padding.bottom;
    final bottomSpacer = bottomInset + kBottomNavigationBarHeight + 230;
    final mediaSize = MediaQuery.of(context).size;
    final bool isTablet = mediaSize.shortestSide <= 450;
    final int crossAxisCount = isTablet ? 1 : 2;
    final double childAspectRatio = MediaQuery.of(context).orientation == Orientation.landscape ? 5 : 3;
    return SafeArea(
      top: false,
      bottom: true,
      child: CustomScrollView(
        slivers: [
          SliverPadding(
            padding: const EdgeInsets.all(12.0),
            sliver: SliverToBoxAdapter(
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Text(
                    AppLocalizations.of(context)!.scheduled_job_settings,
                    style: const TextStyle(fontSize: 20, fontWeight: FontWeight.w600),
                  ),
                  const SizedBox(height: 12),
                ],
              ),
            ),
          ),
          SliverPadding(
            padding: const EdgeInsets.symmetric(horizontal: 12.0),
              sliver: SliverGrid(
              gridDelegate: SliverGridDelegateWithFixedCrossAxisCount(
                crossAxisCount: crossAxisCount,
                  mainAxisSpacing: 8,
                crossAxisSpacing: 12,
                  childAspectRatio: childAspectRatio,
              ),
              delegate: SliverChildBuilderDelegate(
                (context, index) {
                  final job = jobs[index];
                  return _JobCard(job: job, onRefresh: onRefreshJob);
                },
                childCount: jobs.length,
              ),
            ),
          ),
          SliverToBoxAdapter(child: SizedBox(height: bottomSpacer)),
        ],
      ),
    );
  }
}

class _JobCard extends StatefulWidget {
  const _JobCard({required this.job, this.onRefresh});
  final _ScheduledJob job;
  final void Function(String jobId)? onRefresh;

  @override
  State<_JobCard> createState() => _JobCardState();
}

class _JobCardState extends State<_JobCard> {
  String? _lastSync;
  String? _nextSync;
  late SyncProvider syncProvider;


  @override
  void initState() {
    super.initState();
    syncProvider = Provider.of<SyncProvider>(context, listen: false);
    _loadLastSyncTime(); // Fetch last sync when widget loads
    _loadNextSyncTime();
  }

  Future<void> _loadLastSyncTime() async {
    if (widget.job.id != null && widget.job.id!.isNotEmpty) {
      final value = await syncProvider.getLastSyncTimeByJobId(widget.job.id!);
      setState(() => _lastSync = value ?? '-');
      if (widget.job.apiName == "masterSyncJob" && _lastSync == "NA") {
        _lastSync = formatDate(syncProvider.lastSuccessfulSyncTime);
        setState(() {});
      }
    } else {
      setState(() => _lastSync = '-');
    }
  }

  String formatDate(String dateString) {
    // Parse the input UTC date string
    DateTime dateTime = DateTime.parse(dateString).toLocal(); // Convert to local time

    // Format the date
    String formattedDate = DateFormat("yyyy-MMM-dd HH:mm:ss").format(dateTime);

    return formattedDate;
  }

  Future<void> _loadNextSyncTime() async {
    if (widget.job.id != null && widget.job.id!.isNotEmpty) {
      final value = await syncProvider.getNextSyncTimeByJobId(widget.job.id!);
      setState(() => _nextSync = value ?? '-');
    } else {
      setState(() => _nextSync = '-');
    }
  }

  Future<void> _triggerJobSync(BuildContext context, String? apiName, String? jobId) async {
    if (apiName == null || apiName.isEmpty) return;
    final service = SyncResponseService();

    try {
      switch (apiName) {
        case 'masterSyncJob':
          await service.getMasterDataSync(true, jobId ?? '');
          break;
        case 'keyPolicySyncJob':
          await service.getPolicyKeySync(true, jobId ?? '');
          break;
        case 'preRegistrationDataSyncJob':
          await service.getPreRegIds(jobId ?? '');
          break;
        case 'userDetailServiceJob':
          await service.getUserDetailsSync(true, jobId ?? '');
          break;
        case 'syncCertificateJob':
          await service.getCaCertsSync(true, jobId ?? '');
          break;
        case 'publicKeySyncJob':
          await service.getKernelCertsSync(true, jobId ?? '');
          break;
        case 'deleteAuditLogsJob':
          await service.deleteAuditLogs(jobId ?? '');
          break;
        case 'synchConfigDataJob':
          await service.getGlobalParamsSync(true, jobId ?? '');
          break;
        case 'preRegistrationPacketDeletionJob':
          await service.deletePreRegRecords(jobId ?? '');
          break;
        case 'registrationDeletionJob':
          await service.deleteRegistrationPackets(jobId ?? '');
          break;
        case 'packetSyncStatusJob':
          await service.syncPacketStatus(jobId ?? '');
          break;
        default:
          debugPrint('No handler for sync job: $apiName');
          return;
      }

      // Refresh last and next sync time after successful sync
      await _loadLastSyncTime();
      await _loadNextSyncTime();

    } catch (e) {
      debugPrint('Sync failed for ${widget.job.id}: $e');
    }
  }


  @override
  Widget build(BuildContext context) {
    final job = widget.job;

    return Container(
      decoration: BoxDecoration(
        color: Colors.white,
        borderRadius: BorderRadius.circular(8),
        border: Border.all(color: const Color(0xFFE5EBFA), width: 0.8),
        boxShadow: const [BoxShadow(color: Color(0x11000000), blurRadius: 4, offset: Offset(0, 2))],
      ),
      child: Padding(
        padding: const EdgeInsets.all(10.0),
        child: Row(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Expanded(
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Text(job.name ?? job.apiName ?? 'Unknown Job',
                      style: const TextStyle(fontSize: 16, fontWeight: FontWeight.w600)),
                  const SizedBox(height: 8),
                  _kv('Next Run', _nextSync ?? '-'),
                  _kv('Last Sync', _lastSync ?? '-'),
                  const SizedBox(height: 6),
                  _kv('Cron Expression', job.syncFreq ?? '-'),
                ],
              ),
            ),
            if (!PACKET_JOBS.contains(job.id))
              SizedBox(
                width: 40,
                child: OutlinedButton(
                  onPressed: () => _triggerJobSync(context, job.apiName, job.id),
                  style: OutlinedButton.styleFrom(
                    padding: EdgeInsets.zero,
                    minimumSize: const Size(40, 40),
                    side: const BorderSide(color: Color(0xFF2A4EA7)),
                  ),
                  child: const Icon(Icons.sync, size: 20, color: Color(0xFF2A4EA7)),
                ),
              ),
          ],
        ),
      ),
    );
  }

  Widget _kv(String k, String v) => Row(
    children: [
      Text(k, style: const TextStyle(fontSize: 12, color: Colors.black54)),
      const SizedBox(width: 8),
      Flexible(
          child: Text(v, style: const TextStyle(fontSize: 12, color: Colors.black87))),
    ],
  );
}

class _ScheduledJob {
  _ScheduledJob({required this.syncJobDef, this.nextRun, this.lastRun});

  final SyncJobDef syncJobDef;
  final String? nextRun;
  final String? lastRun;

  // Convenience getters to maintain compatibility
  String? get id => syncJobDef.id;
  String? get name => syncJobDef.name;
  String? get apiName => syncJobDef.apiName;
  String? get syncFreq => syncJobDef.syncFreq;

  factory _ScheduledJob.fromJson(Map<String, dynamic> json) => _ScheduledJob(
    syncJobDef: SyncJobDef.fromJson(json),
    nextRun: null,
    lastRun: null,
  );
}