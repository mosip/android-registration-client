import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:registration_client/ui/profile/logout_alert.dart';
import 'package:registration_client/utils/app_config.dart';
import 'package:provider/provider.dart';
import 'package:registration_client/provider/connectivity_provider.dart';
import 'package:flutter_gen/gen_l10n/app_localizations.dart';
import 'package:registration_client/provider/global_provider.dart';

import '../../platform_spi/sync_response_service.dart';
import '../../provider/auth_provider.dart';
import '../../provider/sync_provider.dart';

class ProfilePage extends StatefulWidget {
  const ProfilePage({super.key});

  @override
  State<ProfilePage> createState() => _ProfilePageState();
}

class _ProfilePageState extends State<ProfilePage> {
  late AppLocalizations appLocalizations = AppLocalizations.of(context)!;
  late SyncProvider syncProvider;
  late SyncResponseService syncResponseService = SyncResponseService();

  @override
  void initState() {
    syncProvider = Provider.of<SyncProvider>(context, listen: false);
    super.initState();
  }

  void _showInSnackBar(String value) {
    ScaffoldMessenger.of(context).showSnackBar(
      SnackBar(
        content: Text(value),
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    bool isLandscape =
        MediaQuery.of(context).orientation == Orientation.landscape;
    AuthProvider authProvider =
        Provider.of<AuthProvider>(context, listen: false);
    return Card(
      elevation: 0,
      margin: const EdgeInsets.all(16),
      color: appWhite,
      surfaceTintColor: Colors.transparent,
      child: ListView(
        shrinkWrap: true,
        primary: false,
        children: [
          Padding(
            padding: const EdgeInsets.only(left: 16, right: 16, top: 16),
            child: Row(
              children: [
                ClipRRect(
                  borderRadius: BorderRadius.circular(100),
                  child: Image.asset(
                    "assets/images/user_profile@2x.png",
                    height: 50.h,
                    width: 50.h,
                  ),
                ),
                SizedBox(
                  width: (isLandscape) ? 7.w : 1.75.w,
                ),
                Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Text(
                      authProvider.username,
                      style: Theme.of(context).textTheme.titleMedium?.copyWith(
                          color: Colors.black,
                          fontSize: 16,
                          fontWeight: semiBold),
                    ),
                    SizedBox(
                      width: (isLandscape) ? 7.85.w : 1.96.w,
                    ),
                    Text(
                      authProvider.isOfficer
                          ? appLocalizations.officer
                          : authProvider.isOperator
                              ? appLocalizations.operator
                              : appLocalizations.supervisor,
                      style: Theme.of(context).textTheme.titleMedium?.copyWith(
                          color: const Color(0xFF6F6F6F),
                          fontSize: 16,
                          fontWeight: semiBold),
                    ),
                  ],
                ),
              ],
            ),
          ),
          const Divider(color: appBlueShade, height: 4),
          const SizedBox(height: 15),
          const Divider(color: Color(0xFFF5F8FF), height: 4),
          const SizedBox(height: 15),
          Padding(
            padding: const EdgeInsets.only(left: 16, right: 16),
            child: Row(
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: [
                Row(
                  children: [
                    Icon(
                      Icons.desktop_mac_outlined,
                      color: solidPrimary,
                      size: 16,
                    ),
                    const SizedBox(width: 10),
                    Text(
                      context.watch<GlobalProvider>().machineName.toUpperCase(),
                      style: Theme.of(context).textTheme.titleSmall?.copyWith(
                          color: const Color(0xff333333), fontWeight: semiBold),
                    ),
                  ],
                ),
                Row(
                  children: [
                    Icon(
                      Icons.circle,
                      color: context.watch<ConnectivityProvider>().isConnected
                          ? const Color(0xff1A9B42)
                          : Colors.grey,
                      size: 12,
                    ),
                    const SizedBox(width: 10),
                    Text(
                      context.watch<ConnectivityProvider>().isConnected
                          ? appLocalizations.online
                          : appLocalizations.offline,
                      style: Theme.of(context).textTheme.titleSmall?.copyWith(
                          color: const Color(0xff333333), fontWeight: semiBold),
                    ),
                  ],
                ),
              ],
            ),
          ),
          const SizedBox(height: 15),
          const Divider(color: Color(0xFFF5F8FF), height: 4),
          const SizedBox(height: 15),
          Padding(
            padding: const EdgeInsets.only(left: 16, right: 16),
            child: Row(
              children: [
                Icon(
                  Icons.location_pin,
                  color: solidPrimary,
                  size: 16,
                ),
                SizedBox(
                  width: (isLandscape) ? 7.85.w : 1.96.w,
                ),
                Text(
                  context.watch<GlobalProvider>().centerName,
                  style: Theme.of(context).textTheme.titleSmall?.copyWith(
                      color: const Color(0xff333333), fontWeight: semiBold),
                ),
              ],
            ),
          ),
          const SizedBox(height: 15),
          const Divider(color: Color(0xFFF5F8FF), height: 4),
          const SizedBox(height: 15),
          GestureDetector(
            onTap: () async {
              bool periodicSyncAndUploadStatus =
                  await syncResponseService.getSyncAndUploadInProgressStatus();
              if (syncProvider.isSyncInProgress ||
                  syncProvider.isSyncAndUploadInProgress ||
                  periodicSyncAndUploadStatus) {
                showDialog(
                  context: context,
                  builder: (BuildContext context) => const LogoutAlert(),
                );
              } else {
                String result = await authProvider.logoutUser();
                if (result.contains("Logout Success")) {
                  _showInSnackBar(appLocalizations.logout_success);
                  Navigator.pushNamedAndRemoveUntil(
                      context, '/login-page', (route) => false);
                } else {
                  _showInSnackBar(appLocalizations.logout_failure);
                  Navigator.of(context).pop();
                }
              }
            },
            child: Text(
              appLocalizations.logout,
              textAlign: TextAlign.center,
              style: Theme.of(context).textTheme.titleSmall?.copyWith(
                  color: const Color(0xFFC70000), fontWeight: semiBold),
            ),
          ),
          const SizedBox(height: 15),
        ],
      ),
    );
  }
}
