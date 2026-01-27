import 'dart:convert';
import 'package:flutter/material.dart';
import 'package:intl/intl.dart';
import 'package:flutter_gen/gen_l10n/app_localizations.dart';
import 'package:provider/provider.dart';
import 'package:registration_client/platform_spi/sync_response_service.dart';
import 'package:registration_client/utils/sync_job_def.dart';
import 'package:restart_app/restart_app.dart';

import '../../../provider/sync_provider.dart';

// Dart equivalent of the Java PACKET_JOBS constant
const List<String> PACKET_JOBS = ['RPS_J00006', 'RSJ_J00014', 'PUJ_J00017'];

class ScheduledJobsSettings extends StatefulWidget {
  const ScheduledJobsSettings({
    super.key,
    required this.jobJsonList,
    this.onRefreshJob,
  });

  final List<String?> jobJsonList;
  final void Function(String jobId)? onRefreshJob;

  @override
  State<ScheduledJobsSettings> createState() => _ScheduledJobsSettingsState();
}

class _ScheduledJobsSettingsState extends State<ScheduledJobsSettings> {
  List<String?> _permittedJobs = [];
  bool _isLoadingPermittedJobs = true;
  SyncProvider? _syncProvider;

  @override
  void initState() {
    super.initState();
    WidgetsBinding.instance.addPostFrameCallback((_) {
      _syncProvider = context.read<SyncProvider>();
      _syncProvider?.startJobPolling();
    });
    _loadPermittedJobs();
  }

  Future<void> _loadPermittedJobs() async {
    try {
      final service = SyncResponseService();
      final permittedJobs = await service.getPermittedJobs();
      if (!mounted) return;
      setState(() {
        _permittedJobs = permittedJobs;
        _isLoadingPermittedJobs = false;
      });
    } catch (e) {
      debugPrint('Failed to load permitted jobs: $e');
      setState(() {
        _isLoadingPermittedJobs = false;
      });
    }
  }

  @override
  void dispose() {
    _syncProvider?.stopJobPolling();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    final jobs = widget.jobJsonList
        .whereType<String>()
        .map((e) => _ScheduledJob.fromJson(json.decode(e) as Map<String, dynamic>))
        .toList();

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
                crossAxisCount: MediaQuery.of(context).size.shortestSide <= 450 ? 1 : 2,
                  mainAxisSpacing: 8,
                crossAxisSpacing: 12,
                  childAspectRatio: MediaQuery.of(context).orientation == Orientation.landscape ? 5 : 3,
              ),
              delegate: SliverChildBuilderDelegate(
                (context, index) {
                  final job = jobs[index];
                  return _JobCard(
                    job: job,
                    onRefresh: widget.onRefreshJob,
                    isPermitted: _permittedJobs.contains(job.id),
                  );
                },
                childCount: jobs.length,
              ),
            ),
          ),
          SliverToBoxAdapter(child: SizedBox(height: MediaQuery.of(context).padding.bottom + kBottomNavigationBarHeight + 230)),
        ],
      ),
    );
  }
}

class _JobCard extends StatefulWidget {
  const _JobCard({required this.job, this.onRefresh, required this.isPermitted});
  final _ScheduledJob job;
  final void Function(String jobId)? onRefresh;
  final bool isPermitted;

  @override
  State<_JobCard> createState() => _JobCardState();
}

class _JobCardState extends State<_JobCard> {
  String? _lastSync;
  String? _nextSync;
  late SyncProvider syncProvider;
  final TextEditingController _cronController = TextEditingController();
  final SyncResponseService _syncResponseService = SyncResponseService();
  String? _cronError;
  bool _isSaving = false;


  @override
  void initState() {
    super.initState();
    syncProvider = Provider.of<SyncProvider>(context, listen: false);
    _loadCronExpression(); // Load custom cron expression or default
  }

  Future<void> _loadCronExpression() async {
    try {
      if (widget.job.id != null && widget.job.id!.isNotEmpty) {
        // Check for custom cron expression
        final customCron = await _syncResponseService.getValue(widget.job.id!);
        if (customCron != null && customCron.trim().isNotEmpty) {
          _cronController.text = customCron; // Use saved custom cron expression
        } else {
          _cronController.text = widget.job.syncFreq ?? ''; // Use default from DB
        }
      } else {
        _cronController.text = widget.job.syncFreq ?? '';
      }
    } catch (e) {
      debugPrint('Failed to load cron expression: $e');
      // Fallback to default cron expression from job definition
      _cronController.text = widget.job.syncFreq ?? '';
    }
  }

  @override
  void dispose() {
    _cronController.dispose();
    super.dispose();
  }

  String formatDate(String dateString) {
    try {
      // Parse the input UTC date string
      DateTime dateTime = DateTime.parse(dateString).toLocal(); // Convert to local time

      // Format the date
      String formattedDate = DateFormat("yyyy-MMM-dd HH:mm:ss").format(dateTime);

      return formattedDate;
    } catch(e) {
      return dateString;
    }
  }

  Future<void> _modifyCronExpression() async {
    if (_isSaving) return;
    
    setState(() {
      _isSaving = true;
    });
    
    final cronExpression = _cronController.text.trim();
    
    if (cronExpression.isEmpty) {
      setState(() {
        _cronError = 'Cron expression cannot be empty';
        _isSaving = false;
      });
      return;
    }

    // Validate cron expression
    final isValid = await _syncResponseService.isValidCronExpression(cronExpression);
    if (!isValid) {
      setState(() {
        _cronError = 'Invalid cron expression';
        _isSaving = false;
      });
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(content: Text('Invalid cron expression')),
        );
      }
      return;
    }

    setState(() {
      _cronError = null;
    });

    // Validate job ID before proceeding
    final jobId = widget.job.id;
    if (jobId == null || jobId.isEmpty) {
      setState(() {
        _cronError = 'Job ID is required';
        _isSaving = false;
      });
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(content: Text('Cannot save cron expression: Job ID is missing')),
        );
      }
      return;
    }

    // Save cron expression
    try {
      final success = await _syncResponseService.modifyJobCronExpression(
        jobId,
        cronExpression,
      );
      
      if (success && mounted) {
        // Clear error
        setState(() {
          _cronError = null;
        });
        
        // Show success message
        ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(
            content: Text('Cron expression saved successfully. Restarting app...'),
            duration: Duration(seconds: 2),
          ),
        );
        
        // Wait a moment for the user to see the message, then restart the app
        await Future.delayed(const Duration(seconds: 2));
        
        // Restart the app to apply cron expression changes
        if (mounted) {
          Restart.restartApp();
        }
        // Note: No need to reset _isSaving here since app is restarting
      } else if (mounted) {
        setState(() {
          _isSaving = false;
        });
        ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(content: Text('Failed to save cron expression')),
        );
      }
    } catch (e) {
      debugPrint('Error modifying cron expression: $e');
      if (mounted) {
        setState(() {
          _isSaving = false;
        });
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text('Error: $e')),
        );
      }
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
       if (!mounted) return;
       await context.read<SyncProvider>().refreshJobStatuses();

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
        padding: const EdgeInsets.all(6.0),
        child: Row(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Expanded(
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                mainAxisAlignment: MainAxisAlignment.center,
                children: [
                  Text(job.name ?? job.apiName ?? 'Unknown Job',
                      style: const TextStyle(fontSize: 14, fontWeight: FontWeight.w600)),
                  Consumer<SyncProvider>(
                    builder: (context, provider, child) {
                      final status = provider.jobStatuses[job.id];
                      String lastSync = status?.lastSyncTime ?? '-';
                      if (widget.job.apiName == "masterSyncJob" && lastSync == "NA") {
                         lastSync = formatDate(provider.lastSuccessfulSyncTime); 
                      }
                      
                      return Column(
                         crossAxisAlignment: CrossAxisAlignment.start,
                         children: [
                           _kv('Next Run', status?.nextSyncTime ?? '-'),
                           _kv('Last Sync', lastSync),
                         ],
                      );
                    },
                  ),
                  if (widget.isPermitted)
                    Row(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        Expanded(
                          child: Column(
                            crossAxisAlignment: CrossAxisAlignment.start,
                            mainAxisSize: MainAxisSize.min,
                            children: [
                              SizedBox(
                                height: 32,
                                child: TextField(
                                  controller: _cronController,
                                  decoration: InputDecoration(
                                    hintText: 'Cron Expression',
                                    errorText: null,
                                    errorBorder: _cronError != null 
                                        ? const OutlineInputBorder(
                                            borderSide: BorderSide(color: Colors.red, width: 1))
                                        : null,
                                    border: const OutlineInputBorder(),
                                    isDense: true,
                                    contentPadding: const EdgeInsets.symmetric(horizontal: 8, vertical: 8),
                                  ),
                                  style: const TextStyle(fontSize: 11),
                                ),
                              ),
                              if (_cronError != null)
                                Padding(
                                  padding: const EdgeInsets.only(top: 1.0, left: 4.0),
                                  child: Text(
                                    _cronError!,
                                    style: const TextStyle(fontSize: 9, color: Colors.red, height: 1),
                                  ),
                                ),
                            ],
                          ),
                        ),
                        const SizedBox(width: 6),
                        SizedBox(
                          height: 32,
                          width: 65,
                          child: ElevatedButton(
                            onPressed: _isSaving ? null : _modifyCronExpression,
                            style: ElevatedButton.styleFrom(
                              padding: EdgeInsets.zero,
                            ),
                            child: _isSaving
                                ? const SizedBox(
                                    width: 12,
                                    height: 12,
                                    child: CircularProgressIndicator(
                                      strokeWidth: 2,
                                      valueColor: AlwaysStoppedAnimation<Color>(Colors.white),
                                    ),
                                  )
                                : const Text('Submit', style: TextStyle(fontSize: 11)),
                          ),
                        ),
                      ],
                    )
                  else
                    _kv('Cron Expression', widget.job.syncFreq ?? '-'),
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

  Widget _kv(String k, String v) => Padding(
    padding: const EdgeInsets.only(top: 0.5),
    child: Row(
      children: [
        Text(k, style: const TextStyle(fontSize: 11, color: Colors.black54)),
        const SizedBox(width: 6),
        Flexible(
            child: Text(v, style: const TextStyle(fontSize: 11, color: Colors.black87))),
      ],
    ),
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