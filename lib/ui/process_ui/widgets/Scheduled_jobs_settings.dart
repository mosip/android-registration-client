import 'dart:convert';
import 'package:flutter/material.dart';
import 'package:registration_client/platform_spi/sync_response_service.dart';
import 'package:registration_client/pigeon/master_data_sync_pigeon.dart';
import 'package:registration_client/utils/sync_job_def.dart';

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

    return Padding(
      padding: const EdgeInsets.all(12.0),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          const Text(
            'Scheduled Job Settings',
            style: TextStyle(fontSize: 20, fontWeight: FontWeight.w600),
          ),
          const SizedBox(height: 12),
          LayoutBuilder(
            builder: (context, constraints) {
              int crossAxisCount = 1;
              if (constraints.maxWidth >= 1200) {
                crossAxisCount = 3;
              } else if (constraints.maxWidth >= 700) {
                crossAxisCount = 2;
              }
              return GridView.builder(
                shrinkWrap: true,
                physics: const NeverScrollableScrollPhysics(),
                gridDelegate: SliverGridDelegateWithFixedCrossAxisCount(
                  crossAxisCount: crossAxisCount,
                  mainAxisSpacing: 12,
                  crossAxisSpacing: 12,
                  childAspectRatio: 3.5,
                ),
                itemCount: jobs.length,
                itemBuilder: (context, index) {
                  final job = jobs[index];
                  return _JobCard(job: job, onRefresh: onRefreshJob);
                },
              );
            },
          ),
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


  @override
  void initState() {
    super.initState();
    _loadLastSyncTime(); // Fetch last sync when widget loads
    _loadNextSyncTime();
  }

  Future<void> _loadLastSyncTime() async {
    final service = SyncResponseService();
    if (widget.job.id != null && widget.job.id!.isNotEmpty) {
      final value = await service.getLastSyncTimeByJobId(widget.job.id!);
      setState(() => _lastSync = value ?? '-');
    } else {
      setState(() => _lastSync = '-');
    }
  }

  Future<void> _loadNextSyncTime() async {
    final service = SyncResponseService();
    if (widget.job.id != null && widget.job.id!.isNotEmpty) {
      final value = await service.getNextSyncTimeByJobId(widget.job.id!);
      setState(() => _nextSync = value ?? '-');
    } else {
      setState(() => _nextSync = '-');
    }
  }

  Future<void> _triggerJobSync(BuildContext context, String? apiName, String? jobId) async {
    print("APIName>>>> $apiName");
    if (apiName == null || apiName.isEmpty) return;

    print('Triggering sync for: ${widget.job.id} / $apiName');
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
          await service.deletePreRegRecords();
          break;
        default:
          debugPrint('No handler for sync job: $apiName');
          return;
      }

      // Refresh last and next sync time after successful sync
      await _loadLastSyncTime();
      await _loadNextSyncTime();

      // ScaffoldMessenger.of(context).showSnackBar(
      //   SnackBar(
      //     content: Text('Sync completed for ${widget.job.name ?? apiName}'),
      //     behavior: SnackBarBehavior.floating,
      //     duration: const Duration(seconds: 3),
      //   ),
      // );
    } catch (e) {
      debugPrint('Sync failed for ${widget.job.id}: $e');
      // ScaffoldMessenger.of(context).showSnackBar(
      //   SnackBar(
      //     content: Text('Failed to sync ${widget.job.name ?? apiName}'),
      //     behavior: SnackBarBehavior.floating,
      //     duration: const Duration(seconds: 3),
      //   ),
      // );
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
        padding: const EdgeInsets.all(12.0),
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