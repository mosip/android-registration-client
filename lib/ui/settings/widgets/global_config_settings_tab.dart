import 'package:flutter/material.dart';
import 'package:registration_client/utils/app_config.dart';
import '../../../pigeon/common_details_pigeon.dart';
import '../../../pigeon/global_config_settings_pigeon.dart';
import 'package:flutter_gen/gen_l10n/app_localizations.dart';
import 'package:provider/provider.dart';
import '../../../provider/global_provider.dart';
import 'package:restart_app/restart_app.dart';

class GlobalConfigSettingsTab extends StatefulWidget {
  const GlobalConfigSettingsTab({Key? key}) : super(key: key);

  @override
  State<GlobalConfigSettingsTab> createState() =>
      _GlobalConfigSettingsTabState();
}

class _GlobalConfigSettingsTabState extends State<GlobalConfigSettingsTab> {
  Map<String, Object>? serverValues;
  Map<String, String> localValues = {};
  Map<String, String> localConfigurations = {};
  List<String> permittedConfigurations = [];
  final Map<String, TextEditingController> _controllers = {};
  bool isLoading = true;
  String? errorMessage;

  @override
  void initState() {
    super.initState();
    _loadInitialData();
  }

  // Load initial data from the server and local storage
  Future<void> _loadInitialData() async {
    setState(() {
      isLoading = true;
      errorMessage = null;
    });
    try {
      // Load registration params, local configurations, and permitted configurations in parallel
      serverValues = (await GlobalConfigSettingsApi().getRegistrationParams())
          .cast<String, Object>();
      localConfigurations =
          (await GlobalConfigSettingsApi().getLocalConfigurations())
              .cast<String, String>();
      permittedConfigurations =
          (await GlobalConfigSettingsApi().getPermittedConfigurationNames())
              .cast<String>();

      for (var key in serverValues!.keys) {
        _controllers[key]?.dispose();
        _controllers[key] = TextEditingController(
          text: _getLocalValue(key) == '-' ? '' : _getLocalValue(key),
        );
      }

      setState(() {
        isLoading = false;
      });
    } catch (e) {
      setState(() {
        errorMessage = e.toString();
        isLoading = false;
      });
    }
  }

  @override
  void dispose() {
    for (var controller in _controllers.values) {
      controller.dispose();
    }
    super.dispose();
  }

  // Update _updateLocalValue to also update the controller's text if needed:
  void _updateLocalValue(String key, String value) {
    setState(() {
      if (value.isEmpty) {
        localValues.remove(key);
      } else {
        localValues[key] = value;
      }
    });
  }

  bool _isConfigurationPermitted(String configName) {
    return permittedConfigurations.contains(configName);
  }

  String _getLocalValue(String key) {
    // First check if user has modified it in this session
    if (localValues.containsKey(key)) {
      return localValues[key]!;
    }
    // Then check if there's a saved local configuration
    if (localConfigurations.containsKey(key)) {
      return localConfigurations[key]!;
    }
    // Return empty if no local value
    return '';
  }

  bool _hasChanges() {
    if (localValues.isEmpty) {
      return false;
    }

    // Check if any local value is different from server value
    for (String key in localValues.keys) {
      String serverValue = serverValues?[key]?.toString() ?? '-';
      String localValue = localValues[key]!;

      // 1. Local value is not empty and different from server value, OR
      // 2. Local value is empty but there was a previous local configuration
      if (localValue.isNotEmpty && localValue != serverValue) {
        return true;
      }
      if (localValue.isEmpty && localConfigurations.containsKey(key)) {
        return true;
      }
    }
    return false;
  }

  void _onSaveChanges() {
    if (!_hasChanges()) {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text('No changes to save')),
      );
      return;
    }

    showDialog(
      context: context,
      builder: (context) => AlertDialog(
        title: const Text('Submit Changes'),
        content: SizedBox(
          width: 250,
          height: 20,
          child: Center(
            child: Text('${localValues.length} configuration will be updated.'),
          ),
        ),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(context),
            child: Text(AppLocalizations.of(context)!.cancel),
          ),
          ElevatedButton(
            onPressed: () async {
              Navigator.pop(context);
              await _saveChanges();
            },
            child: Text(AppLocalizations.of(context)!.confirm),
          ),
        ],
      ),
    );
  }

  Future<void> _saveChanges() async {
    try {
      showDialog(
        context: context,
        barrierDismissible: false,
        builder: (context) => const Center(
          child: Column(
            mainAxisSize: MainAxisSize.min,
            children: [
              CircularProgressIndicator(),
              SizedBox(height: 16),
              Text('Saving configuration changes...'),
            ],
          ),
        ),
      );

      // Save configuration changes
      await GlobalConfigSettingsApi().modifyConfigurations(localValues);

      // Update local configurations with the saved values
      setState(() {
        localConfigurations.addAll(localValues);
        localValues.clear();
      });

      // Hide loading indicator
      if (mounted) {
        Navigator.of(context).pop();
      }

      // Show success message
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(
          content: Text('Configuration saved successfully. Restarting app...'),
          duration: Duration(seconds: 2),
        ),
      );

      // Wait a moment for the user to see the message, then restart the app
      await Future.delayed(const Duration(seconds: 2));

      // Restart the app to apply configuration changes
      Restart.restartApp();
    } catch (e) {
      if (mounted) {
        Navigator.of(context).pop();
      }
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text('Error saving changes: $e')),
      );
    }
  }

  List<GlobalConfigItem> _getConfigurations() {
    if (serverValues == null) return [];

    List<GlobalConfigItem> globalConfigItems = [];

    for (String key in serverValues!.keys) {
      String serverValue = serverValues![key]?.toString() ?? '-';
      String localValue = _getLocalValue(key);
      bool isEditable = _isConfigurationPermitted(key);
      bool isModified =
          localValues.containsKey(key) && localValues[key] != serverValue;

      GlobalConfigItem item = GlobalConfigItem(
        key: key,
        serverValue: serverValue,
        localValue: localValue,
        editable: isEditable,
        isModified: isModified,
      );
      globalConfigItems.add(item);
    }

    return globalConfigItems;
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Card(
        margin: const EdgeInsets.all(5),
        elevation: 2,
        shape: RoundedRectangleBorder(
          borderRadius: BorderRadius.circular(4),
        ),
        child: Column(
          children: [
            Container(
              padding: const EdgeInsets.all(16),
              decoration: BoxDecoration(
                borderRadius: BorderRadius.circular(4),
                color: Colors.blue[50],
              ),
              child: Column(
                children: [
                  Row(
                    children: [
                      Expanded(
                        flex: 2,
                        child: Text(
                          AppLocalizations.of(context)!.key,
                          style: const TextStyle(
                            fontSize: 14,
                            fontWeight: FontWeight.bold,
                          ),
                        ),
                      ),
                      Expanded(
                        flex: 1,
                        child: Text(
                          AppLocalizations.of(context)!.server_value,
                          style: const TextStyle(
                            fontSize: 14,
                            fontWeight: FontWeight.bold,
                          ),
                        ),
                      ),
                      Expanded(
                        flex: 1,
                        child: Text(
                          AppLocalizations.of(context)!.local_value,
                          style: const TextStyle(
                            fontSize: 14,
                            fontWeight: FontWeight.bold,
                          ),
                        ),
                      ),
                    ],
                  ),
                ],
              ),
            ),
            Expanded(
              child: _buildContent(),
            ),
            Container(
              padding: const EdgeInsets.all(10),
              alignment: Alignment.centerRight, // Align content to the end
              child: ElevatedButton(
                onPressed: () {
                  _onSaveChanges();
                },
                style: ElevatedButton.styleFrom(
                  backgroundColor: solidPrimary,
                  foregroundColor: Colors.white,
                  padding:
                      const EdgeInsets.symmetric(vertical: 16, horizontal: 60),
                  elevation: 4,
                ),
                child: Text(AppLocalizations.of(context)!.submit),
              ),
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildContent() {
    if (isLoading) {
      return const Center(child: CircularProgressIndicator());
    }
    if (errorMessage != null) {
      return Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Icon(Icons.error, size: 64, color: Colors.red[300]),
            const SizedBox(height: 16),
            Text(
              'Error: $errorMessage',
              style: TextStyle(color: Colors.red[700]),
              textAlign: TextAlign.center,
            ),
            const SizedBox(height: 16),
            ElevatedButton(
              onPressed: _loadInitialData,
              child: Text(AppLocalizations.of(context)!.retry),
            ),
          ],
        ),
      );
    }
    if (serverValues == null || serverValues!.isEmpty) {
      return Center(
          child: Text(
              AppLocalizations.of(context)!.no_configuration_parameters_found));
    }

    final configs = _getConfigurations();
    if (configs.isEmpty) {
      return Center(
        child: Text(AppLocalizations.of(context)!.no_configurations_found),
      );
    }

    return SizedBox(
      width: double.infinity,
      child: ListView.separated(
        padding: const EdgeInsets.only(top: 10, bottom: 15),
        itemCount: configs.length,
        separatorBuilder: (_, __) =>
            Divider(height: 1, color: Colors.grey[300]),
        itemBuilder: (context, index) {
          final config = configs[index];
          return Padding(
            padding: const EdgeInsets.symmetric(horizontal: 20, vertical: 15),
            child: Row(
              mainAxisAlignment: MainAxisAlignment.spaceAround,
              children: [
                Expanded(
                  flex: 2,
                  child: Text(
                    config.key,
                    style: TextStyle(
                      fontSize: 12,
                      fontWeight: config.isModified
                          ? FontWeight.bold
                          : FontWeight.normal,
                      color: config.isModified ? Colors.blue : Colors.black,
                    ),
                  ),
                ),
                const SizedBox(width: 10),
                Expanded(
                  flex: 1,
                  child: Text(
                    config.serverValue,
                    style: const TextStyle(
                      fontSize: 12,
                    ),
                  ),
                ),
                Expanded(
                  flex: 1,
                  child: _buildEditableCell(config),
                ),
              ],
            ),
          );
        },
      ),
    );
  }

  // Builds either an editable TextField or read-only text based on config permissions
  Widget _buildEditableCell(GlobalConfigItem config) {
    if (config.editable) {
      return _buildEditableTextField(config);
    } else {
      return _buildReadOnlyText(config);
    }
  }

  // Builds an editable TextField for permitted configurations
  Widget _buildEditableTextField(GlobalConfigItem config) {
    final controller = _controllers[config.key]!;
    return TextField(
      controller: controller,
      onChanged: (newValue) => _updateLocalValue(config.key, newValue),
      style: TextStyle(
        color: config.isModified ? Colors.blue : Colors.black,
        fontSize: 12,
        fontWeight: config.isModified ? FontWeight.bold : FontWeight.normal,
      ),
      decoration: InputDecoration(
        border: OutlineInputBorder(
          borderRadius: BorderRadius.circular(4),
          borderSide: BorderSide(
            color: config.isModified ? Colors.blue : Colors.grey,
          ),
        ),
        contentPadding: const EdgeInsets.symmetric(
          horizontal: 8,
          vertical: 4,
        ),
        isDense: true,
        hintText: config.serverValue,
      ),
    );
  }

  // Builds read-only styled text for non-permitted configurations
  Widget _buildReadOnlyText(GlobalConfigItem config) {
    return Container(
      padding: const EdgeInsets.symmetric(
        horizontal: 8,
        vertical: 8,
      ),
      decoration: BoxDecoration(
        border: Border.all(color: Colors.grey[300]!),
        borderRadius: BorderRadius.circular(4),
        color: Colors.grey[100],
      ),
      child: Text(
        config.localValue.isEmpty ? '-' : config.localValue,
        style: TextStyle(
          color: config.isModified ? Colors.blue : Colors.grey[600],
          fontSize: 12,
          fontWeight: config.isModified ? FontWeight.bold : FontWeight.normal,
        ),
      ),
    );
  }
}

class GlobalConfigItem {
  final String key;
  final String serverValue;
  final String localValue;
  final bool editable;
  final bool isModified;

  GlobalConfigItem({
    required this.key,
    required this.serverValue,
    required this.localValue,
    required this.editable,
    required this.isModified,
  });
}
