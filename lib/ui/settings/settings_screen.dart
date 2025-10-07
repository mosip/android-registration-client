import 'dart:convert';

import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:registration_client/model/settings.dart';
import 'package:registration_client/provider/auth_provider.dart';
import 'package:registration_client/provider/global_provider.dart';
import 'package:registration_client/utils/app_config.dart';
import 'package:flutter_gen/gen_l10n/app_localizations.dart';

import 'widgets/global_config_settings_tab.dart';

class SettingsScreen extends StatefulWidget {
  const SettingsScreen({
    super.key,
    required this.getSettingsUI,
  });

  final Future<List<String?>> Function(BuildContext) getSettingsUI;

  @override
  State<SettingsScreen> createState() => _SettingsScreenState();
}

class _SettingsScreenState extends State<SettingsScreen> {
  List<Settings> settingUiSpec = [];
  List<Settings> settingUiByRole  = [];
  bool isLoadingUiSpec = true;
  late AuthProvider authProvider;

  @override
  void initState() {
    super.initState();
    authProvider = Provider.of<AuthProvider>(context, listen: false);
    _loadUiSpec();
  }

  Future<void> _loadUiSpec() async {
    final specList = await widget.getSettingsUI(context);
    settingUiSpec = specList
        .whereType<String>()
        .map((e) => Settings.fromJson(json.decode(e) as Map<String, dynamic>))
        .toList();

    // Get current user roles
    final userId = authProvider.userId;
    final List<String?> userRoles =  await authProvider.getUserRole(userId);

    // Filter tabs accessible to the user based on roles
    settingUiByRole = settingUiSpec.where((settings) {
      final access = settings.accessControl ?? [];
      return access.any((role) => userRoles.contains(role));
    }).toList();

    setState(() => isLoadingUiSpec = false);
  }

  @override
  Widget build(BuildContext context) {
    if (isLoadingUiSpec) {
      return const Center(child: CircularProgressIndicator());
    }


    if (settingUiByRole.isEmpty) {
      return const Center(
        child: Text(
          "You don't have access to this page.",
          style: TextStyle(fontSize: 18, color: Colors.black),
        ),
      );
    }

    return DefaultTabController(
      length: settingUiByRole.length,
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Container(
            width: MediaQuery.of(context).size.width,
            color: solidPrimary,
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                 Padding(
                  padding: const EdgeInsets.symmetric(horizontal: 16.0, vertical: 20.0),
                  child: Text(
                    AppLocalizations.of(context)!.settings,
                    style: const TextStyle(
                      fontSize: 22,
                      color: Colors.white,
                      fontWeight: FontWeight.w500,
                    ),
                  ),
                ),
                Container(
                  padding: const EdgeInsets.only(top: 8.0, bottom: 3.0),
                  decoration: const BoxDecoration(
                    border: Border(
                      top: BorderSide(
                        color: Color(0xFFE5EBFA),
                        width: .3,
                      ),
                      bottom: BorderSide(
                        color: Colors.blueAccent,
                        width: 1,
                      ),
                    ),
                  ),
                  child: TabBar(
                    labelColor: Colors.white,
                    unselectedLabelColor: Colors.white70,
                    indicatorColor: Colors.white,
                    indicatorWeight: 2.0,
                    indicatorSize: TabBarIndicatorSize.label,
                    tabs: [
                      for (final settings in settingUiByRole)
                        Tab(
                          text:
                          settings.label?[context.read<GlobalProvider>().selectedLanguage] ??
                              settings.label?['eng'] ??
                              (settings.label?.values.first ?? 'Unknown'),
                        ),
                    ],
                  ),
                ),
              ],
            ),
          ),
          SizedBox(
            height: MediaQuery.of(context).size.height/1.4,
            child: TabBarView(
              children: [
                for (final settings in settingUiByRole) _buildTabContent(settings),
              ],
            ),
          ),
        ],
      ),
    );
  }

  String _getControllerName(Settings settings) {
    if (settings.fxml != null && settings.fxml!.isNotEmpty) {
      return settings.fxml!.replaceAll('.fxml', 'Controller');
    } else {
      return '${settings.name}Controller';
    }
  }

  Widget _buildTabContent(Settings settings) {
    final selectedLang = context.read<GlobalProvider>().selectedLanguage;

    final controllerName = _getControllerName(settings);

    switch (controllerName) {
      case 'ScheduledJobsController':
        return Center(child: Text("${settings.name}"));
      case 'GlobalConfigSettingsController':
        return const GlobalConfigSettingsTab();
      case 'DeviceSettingsController':
        return Center(child: Text("${settings.name}"));
      default:
        return _buildDescriptionOnlyTab(settings, selectedLang);
    }
  }

  Widget _buildDescriptionOnlyTab(Settings settings, String selectedLang) {
    return Center(
      child: Text(
        settings.description?[selectedLang] ??
            settings.description?['eng'] ??
            (settings.description?.values.first ?? 'No description available'),
        style: const TextStyle(color: Colors.black),
      ),
    );
  }
}
