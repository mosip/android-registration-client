## Scheduled Jobs

This document describes the Scheduled Jobs feature: data model, UI, APIs, and how runtime updates (next/last run) are handled.

### Overview

The Scheduled Jobs screen lists all active background jobs and allows operators to:
- Review next scheduled run and last successful sync time per job
- View each job's cron expression
- Trigger an on-demand sync for eligible jobs

The screen is available under the Settings → Scheduled Job Settings tab.

### Data Flow

1. `SettingsScreen` loads jobs once at init via `SyncResponseService.getActiveSyncJobs()`.
   - See `lib/ui/settings/settings_screen.dart` → `_loadActiveJobs()`
   - Response shape is `List<String?>` where each string is a JSON object for a job definition.
2. `ScheduledJobsSettings` receives that list as `jobJsonList` and maps each JSON string to `_ScheduledJob` using `SyncJobDef.fromJson`.
3. Each job card lazily queries `SyncProvider` for:
   - `getLastSyncTimeByJobId(jobId)`
   - `getNextSyncTimeByJobId(jobId)`
   Returned values are formatted for display.

### Core Types

- `SyncJobDef` (see `registration_client/utils/sync_job_def.dart`):
  - `id`: job id (String?)
  - `name`: display name
  - `apiName`: server function key used for ad‑hoc triggering
  - `syncFreq`: cron expression (String?)

- `_ScheduledJob` (UI adapter in `scheduled_jobs_settings.dart`): wraps `SyncJobDef` for UI needs.

### UI Composition

File: `lib/ui/settings/widgets/scheduled_jobs_settings.dart`

- Root uses `SafeArea + CustomScrollView` and splits content into slivers:
  - Header (`SliverToBoxAdapter`) with title
  - Grid (`SliverGrid`) of job cards
  - Bottom spacer (`SliverToBoxAdapter`) so the last row clears the bottom navigation

- Responsiveness (grid columns):
  - Determined using `MediaQuery.of(context).size.shortestSide`
  - Current rule can be adjusted; code selects 1 or 2 columns based on a breakpoint

- Job Card contents:
  - Title: `job.name` (fallback to `apiName`)
  - Key/Value rows: `Next Run`, `Last Sync`, `Cron Expression`
  - Optional sync button (Outlined) to trigger on-demand run

### On‑Demand Sync

Handler: `_triggerJobSync(BuildContext, String? apiName, String? jobId)`

Routing (switch on `apiName`) via `SyncResponseService`:
- `masterSyncJob` → `getMasterDataSync()`
- `keyPolicySyncJob` → `getPolicyKeySync()`
- `preRegistrationDataSyncJob` → `getPreRegIds()`
- `userDetailServiceJob` → `getUserDetailsSync()`
- `syncCertificateJob` → `getCaCertsSync()`
- `publicKeySyncJob` → `getKernelCertsSync()`
- `deleteAuditLogsJob` → `deleteAuditLogs()`
- `synchConfigDataJob` → `getGlobalParamsSync()`
- `preRegistrationPacketDeletionJob` → `deletePreRegRecords()`

Post‑trigger, the card refreshes both `Last Sync` and `Next Run` by calling the provider again.

### Eligibility Rules

- Constant `PACKET_JOBS` defines job ids for which the manual sync button is hidden.

### Formatting & Internationalization

- Dates are converted to local time and formatted with `intl` using pattern `yyyy-MMM-dd HH:mm:ss`.
- Labels are retrieved from `AppLocalizations`.


### Error Handling

- Sync trigger exceptions are caught and printed via `debugPrint`.
- If a job lacks an id or apiName, the button is disabled and values show as `-`.

### Testing Checklist

1. Jobs list renders with correct count and names.
2. For each card, `Next Run`, `Last Sync`, and `Cron Expression` display as expected.
3. Manual trigger (for eligible jobs) updates `Last Sync` and `Next Run` afterward.
4. Hidden button for packet jobs (ids in `PACKET_JOBS`).
5. Scrolling reaches the final row; last card is fully visible above bottom nav.
6. Responsiveness: verify columns per device class (phone/tablet) and orientation.

### Extensibility

- Paging/Infinite Scroll: Replace `SliverGrid` childCount with a paged data source and attach a scroll listener that loads more jobs on demand.
- Filtering/Searching: Introduce a search field in the header to filter `_jobs` by name or id.
- Bulk Actions: Add multi‑select and batch trigger where allowed by policy.

## Sequence Diagram
The following sequence diagram illustrates the registration process, including the use of the Scheduled jobs Feature:

![scheduledJobs.png](../scheduledJobs.png)
