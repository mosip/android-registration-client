import 'dart:convert';
import 'package:flutter/material.dart';
import 'package:registration_client/platform_spi/sync_response_service.dart';

// Dart equivalent of the Java PACKET_JOBS constant
const List<String> PACKET_JOBS = ['RPS_J00006', 'RSJ_J00014', 'PUJ_J00017'];

class ScheduledJobsSettings extends StatelessWidget {
  const ScheduledJobsSettings({super.key, required this.jobJsonList, this.onRefreshJob});

  final List<String?> jobJsonList;
  final void Function(String jobId)? onRefreshJob;

  @override
  Widget build(BuildContext context) {
    // Log all items in jobJsonList
    print('Logging all items in jobJsonList:');
    for (final item in jobJsonList) {
      print(item);
    }

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

class _JobCard extends StatelessWidget {
  const _JobCard({required this.job, this.onRefresh});

  final _ScheduledJob job;
  final void Function(String jobId)? onRefresh;

  @override
  Widget build(BuildContext context) {
    return Container(
      decoration: BoxDecoration(
        color: Colors.white,
        borderRadius: BorderRadius.circular(8),
        border: Border.all(color: const Color(0xFFE5EBFA), width: 0.8),
        boxShadow: const [
          BoxShadow(color: Color(0x11000000), blurRadius: 4, offset: Offset(0, 2)),
        ],
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
                  Text(
                    job.name ?? job.apiName ?? 'Unknown Job',
                    style: const TextStyle(fontSize: 16, fontWeight: FontWeight.w600),
                  ),
                  const SizedBox(height: 8),
                  _kv('Next Run', job.nextRun ?? '-'),
                  _kv('Last Run', job.lastRun ?? '-'),
                  const SizedBox(height: 6),
                  _kv('Cron Expression', job.syncFreq ?? '-'),
                ],
              ),
            ),
            if (!PACKET_JOBS.contains(job.id))
              SizedBox(
                width: 40,
                child: OutlinedButton(
                  onPressed: () => _triggerJobSync(job.apiName),
                  style: OutlinedButton.styleFrom(
                      padding: EdgeInsets.zero,
                      minimumSize: const Size(40, 40),
                      side: const BorderSide(color: Color(0xFF2A4EA7))),
                  child: const Icon(Icons.sync, size: 20, color: Color(0xFF2A4EA7)),
                ),
              ),
          ],
        ),
      ),
    );
  }

  Future<void> _triggerJobSync(String? apiName) async {
    if (apiName == null || apiName.isEmpty) {
      debugPrint('No apiName provided for sync');
      return;
    }
    print('Triggering sync for apiName: $apiName');
    final service = SyncResponseService();
    try {
      switch (apiName) {
        case 'getGlobalParamsSync':
          await service.getGlobalParamsSync(true);
          break;
        case 'masterSyncJob':
          await service.getMasterDataSync(true);
          break;
        case 'getUserDetailsSync':
          await service.getUserDetailsSync(true);
          break;
        case 'getIDSchemaSync':
          await service.getIDSchemaSync(true);
          break;
        case 'getPolicyKeySync':
          await service.getPolicyKeySync(true);
          break;
        case 'getCaCertsSync':
          await service.getCaCertsSync(true);
          break;
        case 'getKernelCertsSync':
          await service.getKernelCertsSync(true);
          break;
        case 'batchJob':
          await service.batchJob();
          break;
        case 'preRegistrationDataSyncJob':
          await service.getPreRegIds();
          break;
        default:
          debugPrint('No handler for sync job: $apiName');
      }
    } catch (e) {
      debugPrint('Failed to trigger sync for $apiName: $e');
    }
  }

  Widget _kv(String k, String v) {
    return Row(
      children: [
        Text(k, style: const TextStyle(fontSize: 12, color: Colors.black54)),
        const SizedBox(width: 8),
        Flexible(child: Text(v, style: const TextStyle(fontSize: 12, color: Colors.black87))),
      ],
    );
  }
}

class _ScheduledJob {
  _ScheduledJob({this.id, this.name, this.apiName, this.syncFreq, this.nextRun, this.lastRun});

  final String? id;
  final String? name;
  final String? apiName;
  final String? syncFreq; // cron expression from syncFreq
  final String? nextRun; // not available yet; placeholder
  final String? lastRun; // not available yet; placeholder

  factory _ScheduledJob.fromJson(Map<String, dynamic> json) {
    return _ScheduledJob(
      id: json['id'] as String?,
      name: json['name'] as String?,
      apiName: json['api_name'] as String? ?? json['apiName'] as String?,
      syncFreq: json['sync_freq'] as String? ?? json['syncFreq'] as String?,
      nextRun: null,
      lastRun: null,
    );
  }
}
