import 'package:flutter/material.dart';
import 'dart:convert';
import 'package:registration_client/platform_spi/process_spec_service.dart';
import 'package:registration_client/model/settings.dart';

class SettingsScreen extends StatefulWidget {
  const SettingsScreen({
    super.key,
    required this.getSettingsUI,
  });
  final void Function(BuildContext, Settings) getSettingsUI;

  static const List<Map<String, dynamic>> uiSpec = [
    {
      "description": {
        "ara": "إعدادات الوظائف المجدولة",
        "fra": "Paramètres des travaux planifiés",
        "eng": "Scheduled Jobs Settings"
      },
      "label": {
        "ara": "إعدادات الوظائف المجدولة",
        "fra": "Paramètres des travaux planifiés",
        "eng": "Scheduled Jobs Settings"
      },
      "name": "scheduledjobs",
      "order": "1",
    },
    {
      "description": {
        "ara": "إعدادات التكوين العامة",
        "fra": "Paramètres de configuration globale",
        "eng": "Global Config Settings"
      },
      "label": {
        "ara": "إعدادات التكوين العامة",
        "fra": "Paramètres de configuration globale",
        "eng": "Global Config Settings"
      },
      "name": "globalconfigs",
      "order": "2",
    },
    {
      "description": {
        "ara": "إعدادات الجهاز",
        "fra": "Réglages de l'appareil",
        "eng": "Device Settings"
      },
      "label": {
        "ara": "إعدادات الجهاز",
        "fra": "Réglages de l'appareil",
        "eng": "Device Settings"
      },
      "name": "devices",
      "order": "3",
    },
  ];

  @override
  State<SettingsScreen> createState() => _SettingsScreenState();
}

class _SettingsScreenState extends State<SettingsScreen> {
  List<Map<String, dynamic>> uiSpec = [];
  List<Map<String, dynamic>> clonedUiSpec = [];

  @override
  void initState() {
    super.initState();
    _loadUiSpec();
  }

  Future<void> _loadUiSpec() async {
    final service = ProcessSpecService();
    final specList = await service.getSettingSpec();
    uiSpec = specList
        .whereType<String>()
        .map((e) => json.decode(e) as Map<String, dynamic>)
        .toList();
    clonedUiSpec = List<Map<String, dynamic>>.from(
      uiSpec.map((tab) => Map<String, dynamic>.from(tab)),
    );
    setState(() {});
  }

  @override
  Widget build(BuildContext context) {
    if (clonedUiSpec.isEmpty) {
      return const Center(child: CircularProgressIndicator());
    }

    return DefaultTabController(
      length: clonedUiSpec.length,
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          // Header and Tabs section (with blue background)
          Container(
            width: MediaQuery.of(context).size.width,
            color: const Color(0xFF0D47A1), // Blue header and tab background
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                const Padding(
                  padding: EdgeInsets.symmetric(horizontal: 16.0, vertical: 20.0),
                  child: Text(
                    'Settings',
                    style: TextStyle(
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
                        color: Color(0xFFE5EBFA), // Top border with given color
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
                    indicatorWeight: 2.0, // You can adjust this thickness
                    indicatorSize: TabBarIndicatorSize.label, // Match underline to text width
                    tabs: [
                      for (final tab in clonedUiSpec)
                        Tab(text: tab['label']['eng']),
                    ],
                  ),
                ),
              ],
            ),
          ),

          // TabBarView section - Handle data and show content
          Expanded(
            child: TabBarView(
              children: [
                for (final tab in clonedUiSpec)
                  _buildTabContent(context, tab),
              ],
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildTabContent(BuildContext context, Map<String, dynamic> tab) {
    // Convert tab data to Settings object
    Settings settings = Settings.fromJson(tab);
    
    // Call the data handler callback
    //widget.getSettingsUI(context, settings);

    // Return the actual UI content for this tab
    return Padding(
      padding: const EdgeInsets.all(16.0),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Text(
            settings.label?['eng'] ?? settings.name ?? 'Settings',
            style: const TextStyle(
              fontSize: 20,
              fontWeight: FontWeight.bold
            ),
          ),
          const SizedBox(height: 8),
          if (settings.description != null)
            Text(
              settings.description!['eng'] ?? '',
              style: const TextStyle(fontSize: 16),
            ),
          const SizedBox(height: 16),
          // You can add more UI content here based on the settings
          _buildSettingsContent(context, settings),
        ],
      ),
    );
  }

  Widget _buildSettingsContent(BuildContext context, Settings settings) {
    // Build UI based on settings type
    switch (settings.name) {
      case 'scheduledjobs':
        return _buildScheduledJobsUI(context, settings);
      case 'globalconfigs':
        return _buildGlobalConfigUI(context, settings);
      case 'devices':
        return _buildDeviceSettingsUI(context, settings);
      default:
        return _buildDefaultSettingsUI(context, settings);
    }
  }

  Widget _buildScheduledJobsUI(BuildContext context, Settings settings) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Text('Scheduled Jobs Configuration', style: TextStyle(fontSize: 18, fontWeight: FontWeight.w600)),
        SizedBox(height: 12),
        SwitchListTile(
          title: Text('Enable Auto Sync'),
          value: true,
          onChanged: (value) {
            // Handle setting change
          },
        ),
        SwitchListTile(
          title: Text('Enable Background Jobs'),
          value: false,
          onChanged: (value) {
            // Handle setting change
          },
        ),
      ],
    );
  }

  Widget _buildGlobalConfigUI(BuildContext context, Settings settings) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Text('Global Configuration', style: TextStyle(fontSize: 18, fontWeight: FontWeight.w600)),
        SizedBox(height: 12),
        TextFormField(
          decoration: InputDecoration(labelText: 'Server URL', border: OutlineInputBorder()),
          initialValue: 'https://example.com',
          onChanged: (value) {
            // Handle setting change
          },
        ),
        SizedBox(height: 12),
        TextFormField(
          decoration: InputDecoration(labelText: 'API Key', border: OutlineInputBorder()),
          obscureText: true,
          onChanged: (value) {
            // Handle setting change
          },
        ),
      ],
    );
  }

  Widget _buildDeviceSettingsUI(BuildContext context, Settings settings) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Text('Device Configuration', style: TextStyle(fontSize: 18, fontWeight: FontWeight.w600)),
        SizedBox(height: 12),
        SwitchListTile(
          title: Text('Enable Biometric Authentication'),
          value: true,
          onChanged: (value) {
            // Handle setting change
          },
        ),
        SwitchListTile(
          title: Text('Enable Location Services'),
          value: false,
          onChanged: (value) {
            // Handle setting change
          },
        ),
        SwitchListTile(
          title: Text('Enable Camera Access'),
          value: true,
          onChanged: (value) {
            // Handle setting change
          },
        ),
      ],
    );
  }

  Widget _buildDefaultSettingsUI(BuildContext context, Settings settings) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Text('General Settings', style: TextStyle(fontSize: 18, fontWeight: FontWeight.w600)),
        SizedBox(height: 12),
        SwitchListTile(
          title: Text('Enable Notifications'),
          value: true,
          onChanged: (value) {
            // Handle setting change
          },
        ),
        SwitchListTile(
          title: Text('Auto Save'),
          value: false,
          onChanged: (value) {
            // Handle setting change
          },
        ),
      ],
    );
  }
}