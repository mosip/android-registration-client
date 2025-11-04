import 'dart:convert';
import 'package:flutter/material.dart';
import 'package:registration_client/pigeon/biometrics_pigeon.dart';
import '../../../model/settings.dart';
import '../../../platform_spi/biometrics_service.dart';
import '../../../utils/app_config.dart';
import 'package:flutter_gen/gen_l10n/app_localizations.dart';

class DeviceSettingsTab extends StatefulWidget {
  DeviceSettingsTab(
      {super.key, required this.settings, required this.selectedLan});
  Settings settings;
  String selectedLan;

  @override
  _DeviceSettingsTabState createState() => _DeviceSettingsTabState();
}

class _DeviceSettingsTabState extends State<DeviceSettingsTab> {
  late Future<List<DeviceInfo>> _devicesFuture;

  @override
  void initState() {
    super.initState();
    _devicesFuture = fetchDeviceDetails();
  }

  Future<List<DeviceInfo>> fetchDeviceDetails() async {
    List<DeviceInfo> deviceDetails = [];
    try {
      await Future.delayed(const Duration(seconds: 1));
      List<String> modality = ["Face", "Iris", "Thumbs"];

      for (var modalityType in modality) {
        List<DeviceInfo?> data = await BiometricsService().getListOfDevices(modalityType);
        deviceDetails.addAll(data.whereType<DeviceInfo>());
      }
      return deviceDetails;
    } catch (e) {
      debugPrint('Error fetching device details: $e');
      return [];
    }
  }

  @override
  Widget build(BuildContext context) {
    return FutureBuilder<List<DeviceInfo>>(
      future: _devicesFuture,
      builder: (context, snapshot) {
        if (snapshot.connectionState == ConnectionState.waiting) {
          return const Center(child: CircularProgressIndicator());
        }

        if (snapshot.hasError) {
          return const Center(child: Text("Error loading device details"));
        }

        final devices = snapshot.data ?? [];
        if (devices.isEmpty) {
          return const Center(child: Text("No devices found"));
        }

        return Padding(
          padding: const EdgeInsets.all(16.0),
          child: Column(
            children: [
              Row(
                mainAxisAlignment: MainAxisAlignment.spaceBetween,
                children: [
                  Text(
                    AppLocalizations.of(context)!.device_settings,
                    style: const TextStyle(
                      fontSize: 20,
                      fontWeight: FontWeight.bold,
                    ),
                  ),
                  ElevatedButton(
                    style: ElevatedButton.styleFrom(
                      backgroundColor: solidPrimary,
                      padding: const EdgeInsets.symmetric(
                          vertical: 12, horizontal: 20),
                    ),
                    onPressed: () {
                      setState(() {});
                      _devicesFuture = fetchDeviceDetails();
                    },
                    child: Text(AppLocalizations.of(context)!.scan_now),
                  ),
                ],
              ),
              const SizedBox(height: 16),
              Expanded(
                child: GridView.builder(
                  itemCount: devices.length,
                  gridDelegate: const SliverGridDelegateWithFixedCrossAxisCount(
                    crossAxisCount: 2,
                    crossAxisSpacing: 16,
                    mainAxisSpacing: 16,
                    childAspectRatio: 3.5,
                  ),
                  itemBuilder: (context, index) {
                    final device = devices[index];
                    return Container(
                      padding: const EdgeInsets.all(8), // even less padding
                      decoration: BoxDecoration(
                        border: Border.all(color: Colors.grey.shade300),
                        borderRadius: BorderRadius.circular(8),
                      ),
                      child: Row(
                        crossAxisAlignment: CrossAxisAlignment.center,
                        mainAxisAlignment: MainAxisAlignment.start,
                        mainAxisSize: MainAxisSize.min,
                        children: [
                          Icon(Icons.scanner, size: 25, color: solidPrimary),
                          const SizedBox(width: 10),
                          Flexible(
                            child: Column(
                              crossAxisAlignment: CrossAxisAlignment.start,
                              mainAxisSize: MainAxisSize.min,
                              children: [
                                Text("ID: ${device.deviceId ?? ''}",
                                    style: const TextStyle(fontSize: 12)),
                                Text(
                                    "Name: ${device.deviceName?? ''}",
                                    style: const TextStyle(fontSize: 12)),
                                Text(
                                    "Status: ${device.connectionStatus ?? ''}",
                                    style: const TextStyle(fontSize: 12)),
                              ],
                            ),
                          ),
                        ],
                      ),
                    );
                  },
                ),
              ),
            ],
          ),
        );
      },
    );
  }
}
